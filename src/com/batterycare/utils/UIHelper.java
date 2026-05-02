package com.batterycare.utils;

import android.content.Context;
import android.widget.TextView;
import androidx.lifecycle.LifecycleOwner;
import com.batterycare.R;
import com.batterycare.view.BatteryCurveView;
import com.batterycare.viewmodel.BatteryViewModel;

public class UIHelper {
    public static void bindViews(Context context, TextView tvBatteryLevel, TextView tvVoltage,
                                 TextView tvTemperature, TextView tvCurrent, TextView tvStatus,
                                 TextView tvTechnology, TextView tvDiagnosis, BatteryCurveView batteryCurveView) {
        tvBatteryLevel.setOnClickListener(v -> showDialog(context, R.string.dialog_level_title, R.string.dialog_level_message));
        tvVoltage.setOnClickListener(v -> showDialog(context, R.string.dialog_voltage_title, R.string.dialog_voltage_message));
        tvTemperature.setOnClickListener(v -> showDialog(context, R.string.dialog_temperature_title, R.string.dialog_temperature_message));
        tvCurrent.setOnClickListener(v -> showDialog(context, R.string.dialog_current_title, R.string.dialog_current_message));
        tvStatus.setOnClickListener(v -> showDialog(context, R.string.dialog_status_title, R.string.dialog_status_message));
        tvTechnology.setOnClickListener(v -> showDialog(context, R.string.dialog_technology_title, R.string.dialog_technology_message));
        tvDiagnosis.setOnClickListener(v -> showDialog(context, R.string.dialog_diagnosis_title, R.string.dialog_diagnosis_message));
    }

    public static void observeViewModel(Context context, BatteryViewModel batteryViewModel,
                                        LifecycleOwner lifecycleOwner, TextView tvBatteryLevel,
                                        TextView tvVoltage, TextView tvTemperature, TextView tvCurrent,
                                        TextView tvStatus, TextView tvTechnology, TextView tvDiagnosis,
                                        BatteryCurveView batteryCurveView) {
        batteryViewModel.getLevel().observe(lifecycleOwner, level ->
                tvBatteryLevel.setText(context.getString(R.string.battery_level_format, level)));

        batteryViewModel.getVoltage().observe(lifecycleOwner, voltage ->
                tvVoltage.setText(context.getString(R.string.label_voltage) + ": " + voltage + " mV"));

        batteryViewModel.getTemperature().observe(lifecycleOwner, temperature ->
                tvTemperature.setText(context.getString(R.string.label_temperature) + ": " + temperature / 10f + "°C"));

        batteryViewModel.getCurrent().observe(lifecycleOwner, current ->
                tvCurrent.setText(context.getString(R.string.label_current) + ": " + current + " mA"));

        batteryViewModel.getStatus().observe(lifecycleOwner, status ->
                tvStatus.setText(context.getString(R.string.label_status) + ": " + BatteryUtil.statusToString(status, context)));

        batteryViewModel.getTechnology().observe(lifecycleOwner, technology ->
                tvTechnology.setText(context.getString(R.string.label_technology) + ": " + technology));

        batteryViewModel.getDiagnosisText().observe(lifecycleOwner, text -> tvDiagnosis.setText(text));

        batteryViewModel.getCurveData().observe(lifecycleOwner, batteryCurveView::setCurveData);
    }

    private static void showDialog(Context context, int titleRes, int messageRes) {
        com.batterycare.view.Dialogs.showExplanationDialog(context, context.getString(titleRes), context.getString(messageRes));
    }
}