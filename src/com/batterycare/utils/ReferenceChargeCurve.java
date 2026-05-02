package com.batterycare.utils;

import android.content.Context;
import android.content.res.AssetManager;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReferenceChargeCurve {
    public static Map<Integer, Integer> load(Context context) {
        Map<Integer, Integer> result = new HashMap<>();
        try {
            AssetManager assets = context.getAssets();
            InputStream stream = assets.open("reference_curve.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject object = new JSONObject(json);
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                int percent = Integer.parseInt(key);
                int seconds = object.getInt(key);
                result.put(percent, seconds);
            }
        } catch (Exception ignored) {
        }
        return result;
    }
}
