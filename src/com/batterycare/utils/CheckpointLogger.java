package com.batterycare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CheckpointLogger {
    private static final String PREFS_NAME = "battery_checkpoint_prefs";
    private static final String KEY_LAST_CHECKPOINT = "last_checkpoint";
    private static final String KEY_LAST_SUMMARY = "last_summary";

    public static void saveCheckpoint(Context context, String checkpoint, String summary) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_LAST_CHECKPOINT, checkpoint)
                .putString(KEY_LAST_SUMMARY, summary)
                .apply();
    }

    public static String readLastCheckpoint(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LAST_CHECKPOINT, "");
    }

    public static String readLastSummary(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LAST_SUMMARY, "");
    }
}
