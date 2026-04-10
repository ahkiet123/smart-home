package fit.nlu.dapm.controller;

import fit.nlu.dapm.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map; // PHẢI CÓ DÒNG NÀY

@RestController
@RequestMapping("/api/v1/energy")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 1. GHI NHẬT KÝ MỚI
     * Frontend gửi: { "deviceName": "Quạt", "power": 55, "hours": 2, "date": "2026-04-03" }
     */
    @PostMapping("/logs")
    public ResponseEntity<?> addUsageLog(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(deviceService.saveUsageLog(payload));
    }

    /**
     * 2. XEM TỔNG QUAN HÔM NAY
     * Lấy tổng tiền và kWh đã ghi nhật ký của ngày hiện tại
     */
    @GetMapping("/summary/today")
    public ResponseEntity<?> getTodaySummary() {
        // Sửa tên hàm từ getSummaryByDate thành getTodaySummary cho khớp với Service
        return ResponseEntity.ok(deviceService.getTodaySummary(LocalDate.now()));
    }

    /**
     * 3. CHI TIẾT THEO GIỜ (Click vào Dashboard xem 24h)
     */
    @GetMapping("/details/hourly")
    public ResponseEntity<?> getHourlyLogDetails(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(deviceService.getHourlyLogs(localDate));
    }

    /**
     * 4. CHI TIẾT THEO NGÀY TRONG THÁNG (Vẽ biểu đồ tháng)
     */
    @GetMapping("/details/monthly")
    public ResponseEntity<?> getMonthlyLogDetails(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(deviceService.getMonthlyLogs(month, year));
    }
}