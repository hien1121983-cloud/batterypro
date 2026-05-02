package com.batterycare.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ChargingSessionDao {
    @Insert
    long insertSession(ChargingSession session);

    @Query("SELECT * FROM charging_sessions ORDER BY startTime DESC")
    List<ChargingSession> getAllSessions();
}
