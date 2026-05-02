package com.batterycare;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.batterycare.utils.BatteryUtil;
import com.batterycare.utils.NotificationHelper;
import com.batterycare.viewmodel.BatteryViewModel;
import java.util.List;

public class BatteryMonitorService extends Service {
    private BatteryReceiver batteryReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(this);
        startForeground(101, NotificationHelper.buildServiceNotification(this));
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        updateFromBatteryIntent(registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateFromBatteryIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        int level = BatteryUtil.getBatteryLevel(intent);
        int voltage = BatteryUtil.getBatteryVoltage(intent);
        int temperature = BatteryUtil.getBatteryTemperature(intent);
        int status = BatteryUtil.getBatteryStatus(intent);
        int health = BatteryUtil.getBatteryHealth(intent);
        String technology = BatteryUtil.getBatteryTechnology(intent);
        int current = BatteryUtil.getBatteryCurrent(this);
        List<Float> curve = BatteryUtil.buildChargeCurve(level);
        String diagnosis = BatteryStateAnalyzer.analyze(level, status, health, temperature);
        BatteryViewModel.getInstance(getApplication()).updateBattery(
                level, voltage, temperature, current, status, health, technology, diagnosis, curve);
    }
}
