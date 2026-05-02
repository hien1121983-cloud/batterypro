package com.batterycare.utils;

public class BatteryStateAnalyzer {
    public static String analyze(int level, int status, int health, int temperature) {
        if (temperature >= 450) {
            return "Pin đang quá nóng. Hạn chế sạc ngay lập tức.";
        }
        if (health == android.os.BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            return "Tình trạng pin quá nhiệt. Kiểm tra phần cứng.";
        }
        if (status == android.os.BatteryManager.BATTERY_STATUS_CHARGING && level >= 90) {
            return "Pin sạc gần đầy, có thể ngắt sạc để bảo vệ tuổi thọ.";
        }
        if (status == android.os.BatteryManager.BATTERY_STATUS_DISCHARGING && level <= 20) {
            return "Pin yếu, nên sạc khi có thể.";
        }
        if (health == android.os.BatteryManager.BATTERY_HEALTH_GOOD) {
            return "Pin ở trạng thái tốt. Tiếp tục theo dõi thường xuyên.";
        }
        if (health == android.os.BatteryManager.BATTERY_HEALTH_POOR || health == android.os.BatteryManager.BATTERY_HEALTH_DEAD) {
            return "Pin có dấu hiệu suy giảm. Xem xét thay pin nếu cần.";
        }
        return "Pin hoạt động ổn định. Theo dõi thêm các chỉ số dòng và nhiệt độ.";
    }
}
