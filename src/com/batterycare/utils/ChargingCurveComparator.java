package com.batterycare.utils;

import java.util.Map;

public class ChargingCurveComparator {
    public static int compareChargeTime(int percent, int actualSeconds, Map<Integer, Integer> referenceCurve) {
        if (referenceCurve == null || !referenceCurve.containsKey(percent)) {
            return 0;
        }
        return actualSeconds - referenceCurve.get(percent);
    }

    public static String describeComparison(int percent, int actualSeconds, Map<Integer, Integer> referenceCurve) {
        int diff = compareChargeTime(percent, actualSeconds, referenceCurve);
        if (diff == 0) {
            return "Thời gian sạc đúng mức tham chiếu";
        }
        if (diff > 0) {
            return "Sạc chậm hơn " + diff + " giây so với tham chiếu";
        }
        return "Sạc nhanh hơn " + Math.abs(diff) + " giây so với tham chiếu";
    }
}
