package com.batterycare;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.batterycare.model.DatabaseHelper;
import com.batterycare.utils.BatteryUtil;
import com.batterycare.utils.NotificationHelper;
import com.batterycare.utils.SoundPlayer;
import com.batterycare.viewmodel.BatteryViewModel;
import java.util.List;

public class BatteryMonitorService extends Service {
    private BatteryReceiver batteryReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private DatabaseHelper databaseHelper;
    private long chargingStartTime = 0;
    private int maxTempDuringCharge = 0;
    private int totalCurrentDuringCharge = 0;
    private int currentCount = 0;
    private boolean wasCharging = false;

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationHelper.createNotificationChannel(this);
        startForeground(101, NotificationHelper.buildServiceNotification(this));
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        databaseHelper = DatabaseHelper.getInstance(this);
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

        // Logic theo dõi sạc
        boolean isCharging = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING;
        if (isCharging && !wasCharging) {
            // Bắt đầu sạc
            chargingStartTime = System.currentTimeMillis();
            maxTempDuringCharge = temperature;
            totalCurrentDuringCharge = 0;
            currentCount = 0;
        } else if (!isCharging && wasCharging) {
            // Kết thúc sạc
            if (chargingStartTime > 0) {
                long endTime = System.currentTimeMillis();
                int avgCurrent = currentCount > 0 ? totalCurrentDuringCharge / currentCount : 0;
                String healthStr = BatteryUtil.healthToString(health);
                databaseHelper.insertSession(chargingStartTime, endTime, maxTempDuringCharge, avgCurrent, healthStr);
                chargingStartTime = 0;
            }
        }
        wasCharging = isCharging;

        if (isCharging) {
            maxTempDuringCharge = Math.max(maxTempDuringCharge, temperature);
            totalCurrentDuringCharge += current;
            currentCount++;
        }

        // Thông báo khi đầy hoặc bất thường
        if (level == 100 && isCharging) {
            NotificationHelper.sendNotification(this, getString(R.string.notification_full), getString(R.string.notification_full));
            SoundPlayer.playAlertSound(this);
        }
        if (temperature >= 450 || voltage < 3000 || voltage > 4500) {
            NotificationHelper.sendNotification(this, getString(R.string.notification_abnormal), "Nhiệt độ hoặc điện áp bất thường!");
            SoundPlayer.playAlertSound(this);
        }

        // Gửi broadcast tới Activity
        Intent broadcastIntent = new Intent("BATTERY_UPDATE");
        broadcastIntent.putExtra("level", level);
        broadcastIntent.putExtra("voltage", voltage);
        broadcastIntent.putExtra("temperature", temperature);
        broadcastIntent.putExtra("current", current);
        broadcastIntent.putExtra("status", status);
        broadcastIntent.putExtra("health", health);
        broadcastIntent.putExtra("technology", technology);
        broadcastIntent.putExtra("diagnosis", diagnosis);
        localBroadcastManager.sendBroadcast(broadcastIntent);
    }
}
