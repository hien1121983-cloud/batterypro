package com.batterycare.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class BatteryViewModel extends AndroidViewModel {
    private static BatteryViewModel instance;
    private final MutableLiveData<Integer> level = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> voltage = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> temperature = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> current = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> status = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> health = new MutableLiveData<>(0);
    private final MutableLiveData<String> technology = new MutableLiveData<>("");
    private final MutableLiveData<String> diagnosisText = new MutableLiveData<>("");
    private final MutableLiveData<List<Float>> curveData = new MutableLiveData<>(new ArrayList<>());

    private BatteryViewModel(@NonNull Application application) {
        super(application);
    }

    public static BatteryViewModel getInstance(@NonNull Application application) {
        if (instance == null) {
            instance = new BatteryViewModel(application);
        }
        return instance;
    }

    public LiveData<Integer> getLevel() {
        return level;
    }

    public LiveData<Integer> getVoltage() {
        return voltage;
    }

    public LiveData<Integer> getTemperature() {
        return temperature;
    }

    public LiveData<Integer> getCurrent() {
        return current;
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public LiveData<Integer> getHealth() {
        return health;
    }

    public LiveData<String> getTechnology() {
        return technology;
    }

    public LiveData<String> getDiagnosisText() {
        return diagnosisText;
    }

    public LiveData<List<Float>> getCurveData() {
        return curveData;
    }

    public void updateBattery(int levelValue,
                              int voltageValue,
                              int temperatureValue,
                              int currentValue,
                              int statusValue,
                              int healthValue,
                              String technologyValue,
                              String diagnosisValue,
                              List<Float> curve) {
        level.postValue(levelValue);
        voltage.postValue(voltageValue);
        temperature.postValue(temperatureValue);
        current.postValue(currentValue);
        status.postValue(statusValue);
        health.postValue(healthValue);
        technology.postValue(technologyValue == null ? "" : technologyValue);
        diagnosisText.postValue(diagnosisValue == null ? "" : diagnosisValue);
        curveData.postValue(curve == null ? new ArrayList<>() : curve);
    }
}
