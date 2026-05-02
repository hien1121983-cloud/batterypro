package com.batterycare.utils;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import com.batterycare.R;
import java.util.ArrayList;
import java.util.List;

public class BatteryUtil {
    public static int getBatteryLevel(Intent intent) {
        return intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
    }

    public static int getBatteryVoltage(Intent intent) {
        return intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
    }

    public static int getBatteryTemperature(Intent intent) {
        return intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
    }

    public static int getBatteryStatus(Intent intent) {
        return intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
    }

    public static int getBatteryHealth(Intent intent) {
        return intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN);
    }

    public static String getBatteryTechnology(Intent intent) {
        String value = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        return value == null || value.isEmpty() ? "Không xác định" : value;
    }

    public static int getBatteryCurrent(Context context) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager == null) {
            return 0;
        }
        int current = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        return current == Integer.MIN_VALUE ? 0 : Math.abs(current);
    }

    public static List<Float> buildChargeCurve(int level) {
        List<Float> points = new ArrayList<>();
        int total = Math.max(1, level);
        for (int i = 0; i <= 100; i += 5) {
            float value = i <= level ? i : level + (i - level) * 0.3f;
            points.add(value);
            if (points.size() >= 20) {
                break;
            }
        }
        if (points.isEmpty()) {
            points.add(0f);
        }
        return points;
    }

    public static String statusToString(int status, Context context) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Đang sạc";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Đang xả";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Đã đầy";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Không sạc";
            default:
                return context.getString(R.string.battery_status_unknown);
        }
    }
}
