package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.device.CreateDeviceRequest;
import fit.nlu.dapm.dto.device.DeviceDetailResponse;
import fit.nlu.dapm.dto.device.DeviceResponse;
import fit.nlu.dapm.dto.device.UpdateDeviceRequest;
import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.DeviceUsageLog;
import fit.nlu.dapm.repository.DeviceRepository;
import fit.nlu.dapm.repository.DeviceTypeRepository;
import fit.nlu.dapm.repository.DeviceUsageLogRepository;
import fit.nlu.dapm.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeviceService {

    // Giả sử đơn giá điện nhà nước bình quân
    private final double ELECTRIC_RATE = 2500;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private DeviceUsageLogRepository logRepository;

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

    // Hiển thị tất cả danh sách thiết bị
    public List<DeviceResponse> getAllDevices() {
        return deviceRepository.findAllByOrderByIdAsc().stream()
                .map(device -> new DeviceResponse(
                        device.getId(),
                        device.getDeviceName(),
                        device.getRoom().getRoomName(),
                        device.getRoom().getId(),
                        device.getDeviceType().getId(),
                        device.getPowerRating(),
                        device.getIsOn(),
                        device.getBrand(),
                        device.getModelNumber()
                ))
                .toList();
    }

    // Thêm thiết bị
    public Device createDevice(CreateDeviceRequest req) {
        Device device = new Device();

        device.setDeviceName(req.getDeviceName());
        device.setPowerRating(req.getPowerRating());
        device.setBrand(req.getBrand());
        device.setModelNumber(req.getModelNumber());
        device.setRoom(roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));
        device.setDeviceType(deviceTypeRepository.findById(req.getDeviceTypeId())
                .orElseThrow(() -> new RuntimeException("Device type not found")));
        device.setIsOn(false);

        Device savedDevice = deviceRepository.save(device);

        return savedDevice;
    }

    // Sửa thiết bị
    public Device updateDevice(Long id, UpdateDeviceRequest req) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        device.setDeviceName(req.getDeviceName());
        device.setPowerRating(req.getPowerRating());
        device.setBrand(req.getBrand());
        device.setModelNumber(req.getModelNumber());

        device.setRoom(roomRepository.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));

        device.setDeviceType(deviceTypeRepository.findById(req.getDeviceTypeId())
                .orElseThrow(() -> new RuntimeException("Device type not found")));

        return deviceRepository.save(device);
    }

    // Xóa thiết bị
    public void deleteDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        deviceRepository.delete(device);
    }

    // Lấy thông tin thiết bị dựa theo id của thiết bị
//    public DeviceDetailResponse getDeviceDetail(Long deviceId) {
//
//        Device device = deviceRepository.findById(deviceId)
//                .orElseThrow(() -> new RuntimeException("Device not found"));
//
//        double power = device.getPowerRating() != null ? device.getPowerRating() : 0;
//
//        List<DeviceUsageLog> logs = logRepository
//                .findByDeviceAndUsageDate(device, LocalDate.now());
//
//        double totalHours = logs.stream()
//                .mapToDouble(log -> log.getHoursUsed() != null ? log.getHoursUsed() : 0)
//                .sum();
//
//        if (device.getIsOn() && device.getTurnedOnAt() != null) {
//            double currentHours = Duration.between(
//                    device.getTurnedOnAt(),
//                    LocalDateTime.now()
//            ).toMinutes() / 60.0;
//
//            totalHours += currentHours;
//        }
//
//        // Tính điện
//        double kwhToday = (power * totalHours) / 1000;
//        kwhToday = Math.round(kwhToday * 10000.0) / 10000.0;
//
//        double costToday = Math.round(kwhToday * ELECTRIC_RATE);
//
//        return new DeviceDetailResponse(
//                device.getId(),
//                device.getDeviceName(),
//                device.getRoom().getRoomName(),
//                power,
//                totalHours,
//                device.getIsOn(),
//                kwhToday,
//                costToday
//        );
//    }


}