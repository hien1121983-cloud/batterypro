package com.batterycare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.batterycare.utils.BatteryUtil;
import com.batterycare.viewmodel.BatteryViewModel;
import java.util.List;

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        int level = BatteryUtil.getBatteryLevel(intent);
        int voltage = BatteryUtil.getBatteryVoltage(intent);
        int temperature = BatteryUtil.getBatteryTemperature(intent);
        int status = BatteryUtil.getBatteryStatus(intent);
        int health = BatteryUtil.getBatteryHealth(intent);
        String technology = BatteryUtil.getBatteryTechnology(intent);
        int current = BatteryUtil.getBatteryCurrent(context);
        List<Float> curve = BatteryUtil.buildChargeCurve(level);
        String diagnosis = BatteryStateAnalyzer.analyze(level, status, health, temperature);
        BatteryViewModel.getInstance((android.app.Application) context.getApplicationContext())
                .updateBattery(level, voltage, temperature, current, status, health, technology, diagnosis, curve);
    }
}
