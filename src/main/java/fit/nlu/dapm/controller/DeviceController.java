package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.device.CreateDeviceRequest;
import fit.nlu.dapm.dto.device.DeviceUsageRequest;
import fit.nlu.dapm.dto.device.UpdateDeviceRequest;
import fit.nlu.dapm.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/energy")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/logs")
    public ResponseEntity<?> addUsageLog(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(deviceService.saveUsageLog(payload));
    }

    @PostMapping("/{id}/usage-logs")
    // Ghi nhận thời gian sử dụng cho một thiết bị cụ thể.
    public ResponseEntity<?> addUsageLogByDevice(
            @PathVariable Long id,
            @Valid @RequestBody DeviceUsageRequest request) {
        return ResponseEntity.ok(deviceService.createUsageLog(id, request));
    }

    @GetMapping("/{id}/usage-logs")
    // Lấy lịch sử ghi nhận sử dụng của thiết bị.
    public ResponseEntity<?> getUsageLogsByDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getUsageLogs(id));
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

    @GetMapping
    public ResponseEntity<?> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDeviceRequest req) {
        return ResponseEntity.ok(deviceService.createDevice(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody UpdateDeviceRequest req) {
        return ResponseEntity.ok(deviceService.updateDevice(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @GetMapping("/{id}")
    // Lấy chi tiết thiết bị và dữ liệu tiêu thụ đã tổng hợp.
    public ResponseEntity<?> getDeviceDetail(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceDetail(id));
    }

}