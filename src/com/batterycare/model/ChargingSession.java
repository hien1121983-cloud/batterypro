package com.batterycare.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "charging_sessions")
public class ChargingSession {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long startTime;
    public long endTime;
    public int maxTemp;
    public int avgCurrent;
    public int finalHealth;

    public ChargingSession(long startTime, long endTime, int maxTemp, int avgCurrent, int finalHealth) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxTemp = maxTemp;
        this.avgCurrent = avgCurrent;
        this.finalHealth = finalHealth;
    }
}
