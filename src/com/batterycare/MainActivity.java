package com.batterycare;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.batterycare.utils.NotificationHelper;
import com.batterycare.viewmodel.BatteryViewModel;
import com.batterycare.view.BatteryCurveView;
import com.batterycare.view.Dialogs;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvBatteryLevel;
    private TextView tvVoltage;
    private TextView tvTemperature;
    private TextView tvStatus;
    private TextView tvTechnology;
    private TextView tvDiagnosis;
    private BatteryCurveView batteryCurveView;
    private BatteryViewModel batteryViewModel;

    private final ActivityResultLauncher<String> requestNotificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Dialogs.showExplanationDialog(this,
                            getString(R.string.dialog_title),
                            getString(R.string.permission_rationale));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goFullScreen();
        setContentView(R.layout.activity_main);
        bindViews();
        requestPermissionsIfNeeded();
        batteryViewModel = BatteryViewModel.getInstance(getApplication());
        observeViewModel();
        startBatteryService();
        NotificationHelper.createNotificationChannel(this);
    }

    private void goFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            getWindow().getInsetsController().hide(WindowInsets.Type.statusBars());
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void bindViews() {
        tvBatteryLevel = findViewById(R.id.tvBatteryLevel);
        tvVoltage = findViewById(R.id.tvVoltage);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvStatus = findViewById(R.id.tvStatus);
        tvTechnology = findViewById(R.id.tvTechnology);
        tvDiagnosis = findViewById(R.id.tvDiagnosis);
        batteryCurveView = findViewById(R.id.batteryCurveView);
        View explanationTarget = findViewById(R.id.tvBatteryLevel);
        explanationTarget.setOnClickListener(v -> showDialog());
    }

    private void observeViewModel() {
        batteryViewModel.getLevel().observe(this, level ->
                tvBatteryLevel.setText(getString(R.string.battery_level_format, level)));

        batteryViewModel.getVoltage().observe(this, voltage ->
                tvVoltage.setText(getString(R.string.label_voltage) + ": " + voltage + " mV"));

        batteryViewModel.getTemperature().observe(this, temperature ->
                tvTemperature.setText(getString(R.string.label_temperature) + ": " + temperature / 10f + "°C"));

        batteryViewModel.getStatus().observe(this, status ->
                tvStatus.setText(getString(R.string.label_status) + ": " + BatteryUtil.statusToString(status, this)));

        batteryViewModel.getTechnology().observe(this, technology ->
                tvTechnology.setText(getString(R.string.label_technology) + ": " + technology));

        batteryViewModel.getDiagnosisText().observe(this, tvDiagnosis::setText);

        batteryViewModel.getCurveData().observe(this, batteryCurveView::setCurveData);
    }

    private void requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void startBatteryService() {
        Intent serviceIntent = new Intent(this, BatteryMonitorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void showDialog() {
        Dialogs.showExplanationDialog(this,
                getString(R.string.dialog_title),
                getString(R.string.dialog_message));
    }
}
