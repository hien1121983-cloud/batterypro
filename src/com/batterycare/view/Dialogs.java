package com.batterycare.view;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

public class Dialogs {
    public static void showExplanationDialog(Context context, String title, String message) {
        if (context == null) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
}
