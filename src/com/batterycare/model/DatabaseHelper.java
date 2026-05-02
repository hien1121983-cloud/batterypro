package com.batterycare.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "batterycare.db";
    private static final int DB_VERSION = 1;
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE charging_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "start_time INTEGER," +
                "end_time INTEGER," +
                "max_temp INTEGER," +
                "avg_current INTEGER," +
                "final_health TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS charging_history");
        onCreate(db);
    }

    public long insertSession(long startTime, long endTime, int maxTemp, int avgCurrent, String health) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("max_temp", maxTemp);
        values.put("avg_current", avgCurrent);
        values.put("final_health", health);
        return db.insert("charging_history", null, values);
    }

    public List<String> getAllSessions() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM charging_history ORDER BY start_time DESC", null);
        if (c.moveToFirst()) {
            do {
                list.add("ID:" + c.getLong(0) + " Start:" + c.getLong(1) + " End:" + c.getLong(2) +
                        " MaxTemp:" + c.getInt(3) + " AvgCur:" + c.getInt(4) + " Health:" + c.getString(5));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
}
