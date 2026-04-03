package fit.nlu.dapm.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceService {

    // Giả sử đơn giá điện nhà nước bình quân
    private final double ELECTRIC_RATE = 2500;

    /**
     * 1. LƯU NHẬT KÝ SỬ DỤNG (Usage Log)
     * Thay vì tính toán suông, ta lưu bản ghi này vào DB
     */
    public Map<String, Object> saveUsageLog(Map<String, Object> payload) {
        // Logic: Lấy power, hours từ payload -> Tính cost -> Lưu vào Table usage_logs
        double power = Double.parseDouble(payload.get("power").toString());
        double hours = Double.parseDouble(payload.get("hours").toString());
        double cost = (power * hours / 1000) * ELECTRIC_RATE;

        // Ở đây Toàn gọi Repository để save() vào Postgres nhé
        // usageLogRepository.save(new UsageLog(...));

        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("message", "Đã ghi nhận nhật ký sử dụng!");
        return res;
    }

    /**
     * 2. TỔNG HỢP HÔM NAY (Today Summary)
     * Cộng dồn tất cả nhật ký của ngày hôm nay
     */
    public Map<String, Object> getTodaySummary(LocalDate date) {
        // SQL: SELECT SUM(kwh), SUM(cost), COUNT(id) FROM usage_logs WHERE used_date = :date
        // Demo: Giả lập kết quả từ DB đổ ra
        double totalKwh = 10.7;
        double totalCost = totalKwh * ELECTRIC_RATE;

        Map<String, Object> res = new HashMap<>();
        res.put("kwhToday", totalKwh);
        res.put("costToday", totalCost);
        res.put("deviceCount", 5); // Số thiết bị đã ghi nhật ký hôm nay
        return res;
    }

    /**
     * 3. CHI TIẾT THEO GIỜ (Hourly Logs) - Click vào "Hôm nay"
     * Trả về mảng 24 giờ để vẽ biểu đồ
     */
    public List<Map<String, Object>> getHourlyLogs(LocalDate date) {
        List<Map<String, Object>> chartData = new ArrayList<>();
        // Query DB để lấy tổng tiền theo từng khung giờ người dùng đã khai báo
        for (int i = 0; i < 24; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("label", i + "h-" + (i + 1) + "h");
            // Ví dụ: Nếu trong DB có bản ghi lúc 10h thì lấy ra, không thì để 0
            point.put("value", (i == 10 || i == 20) ? 5000 : 0);
            chartData.add(point);
        }
        return chartData;
    }

    /**
     * 4. CHI TIẾT THEO NGÀY (Monthly Logs) - Click vào "Tháng này"
     * Cộng dồn nhật ký theo từng ngày trong tháng
     */
    public List<Map<String, Object>> getMonthlyLogs(int month, int year) {
        List<Map<String, Object>> chartData = new ArrayList<>();
        // SQL: SELECT day, SUM(cost) FROM usage_logs WHERE month = :month GROUP BY day
        for (int i = 1; i <= 30; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("label", "Ngày " + i);
            // Giả lập dữ liệu: Những ngày đã ghi nhật ký sẽ có cột tiền
            point.put("value", (i % 5 == 0) ? 25000 : 0);
            chartData.add(point);
        }
        return chartData;
    }
}