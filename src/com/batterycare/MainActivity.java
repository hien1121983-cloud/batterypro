package com.batterycare;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.batterycare.utils.BatteryUtil;
import com.batterycare.utils.NotificationHelper;
import com.batterycare.utils.UIHelper;
import com.batterycare.viewmodel.BatteryViewModel;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvBatteryLevel;
    private TextView tvVoltage;
    private TextView tvTemperature;
    private TextView tvCurrent;
    private TextView tvStatus;
    private TextView tvTechnology;
    private TextView tvDiagnosis;
    private BatteryCurveView batteryCurveView;
    private BatteryViewModel batteryViewModel;

    private final BroadcastReceiver batteryUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("BATTERY_UPDATE".equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temperature = intent.getIntExtra("temperature", 0);
                int current = intent.getIntExtra("current", 0);
                int status = intent.getIntExtra("status", 0);
                int health = intent.getIntExtra("health", 0);
                String technology = intent.getStringExtra("technology");
                String diagnosis = intent.getStringExtra("diagnosis");
                List<Float> curve = BatteryUtil.buildChargeCurve(level);
                batteryViewModel.updateBattery(level, voltage, temperature, current, status, health, technology, diagnosis, curve);
            }
        }
    };

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
        tvCurrent = findViewById(R.id.tvCurrent);
        tvStatus = findViewById(R.id.tvStatus);
        tvTechnology = findViewById(R.id.tvTechnology);
        tvDiagnosis = findViewById(R.id.tvDiagnosisBottom);
        batteryCurveView = findViewById(R.id.batteryCurveView);

        UIHelper.bindViews(this, tvBatteryLevel, tvVoltage, tvTemperature, tvCurrent, tvStatus, tvTechnology, tvDiagnosis, batteryCurveView);
    }

    private void observeViewModel() {
        UIHelper.observeViewModel(this, batteryViewModel, this, tvBatteryLevel, tvVoltage, tvTemperature, tvCurrent, tvStatus, tvTechnology, tvDiagnosis, batteryCurveView);
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

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(batteryUpdateReceiver, new IntentFilter("BATTERY_UPDATE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(batteryUpdateReceiver);
    }
