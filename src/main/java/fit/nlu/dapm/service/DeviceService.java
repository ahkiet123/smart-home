package fit.nlu.dapm.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fit.nlu.dapm.dto.device.CreateDeviceRequest;
import fit.nlu.dapm.dto.device.DeviceDetailResponse;
import fit.nlu.dapm.dto.device.DeviceResponse;
import fit.nlu.dapm.dto.device.DeviceUsageRequest;
import fit.nlu.dapm.dto.device.DeviceUsageResponse;
import fit.nlu.dapm.dto.device.UpdateDeviceRequest;
import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.DeviceUsageLog;
import fit.nlu.dapm.exception.BadRequestException;
import fit.nlu.dapm.repository.DeviceRepository;
import fit.nlu.dapm.repository.DeviceTypeRepository;
import fit.nlu.dapm.repository.DeviceUsageLogRepository;
import fit.nlu.dapm.repository.RoomRepository;

@Service
public class DeviceService {

    private static final double ELECTRIC_RATE = 2500;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private DeviceUsageLogRepository logRepository;

    // Endpoint cũ để tương thích với các màn hình đang gọi trực tiếp /energy/logs.
    public Map<String, Object> saveUsageLog(Map<String, Object> payload) {
        double power = Double.parseDouble(payload.getOrDefault("power", 0).toString());
        double hours = Double.parseDouble(payload.getOrDefault("hours", 0).toString());
        double cost = round4((power * hours / 1000) * ELECTRIC_RATE);

        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("message", "Đã ghi nhận nhật ký sử dụng");
        res.put("estimatedCost", cost);
        return res;
    }

    // Tạo bản ghi sử dụng cho 1 thiết bị bằng số giờ sử dụng.
    public DeviceUsageResponse createUsageLog(Long deviceId, DeviceUsageRequest req) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (req == null) {
            throw new BadRequestException("Payload không hợp lệ");
        }

        if (req.getHoursUsed() == null) {
            throw new BadRequestException("Vui lòng nhập số giờ sử dụng");
        }

        LocalDate usageDate = LocalDate.now();
        Double hoursUsed = req.getHoursUsed();

        if (hoursUsed == null || hoursUsed <= 0) {
            throw new BadRequestException("Số giờ sử dụng phải lớn hơn 0");
        }

        if (hoursUsed > 24) {
            throw new BadRequestException("Số giờ sử dụng không được vượt quá 24 giờ");
        }

        DeviceUsageLog log = new DeviceUsageLog();
        log.setDevice(device);
        log.setUsageDate(usageDate);
        log.setHoursUsed(hoursUsed);

        return toUsageResponse(logRepository.save(log));
    }

    // Lấy lịch sử sử dụng theo thiết bị, sắp xếp mới nhất trước.
    public List<DeviceUsageResponse> getUsageLogs(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        return logRepository.findByDeviceOrderByUsageDateDescIdDesc(device)
                .stream()
                .map(this::toUsageResponse)
                .toList();
    }

    public Map<String, Object> getTodaySummary(LocalDate date) {
        List<DeviceUsageLog> logs = logRepository.findByUsageDate(date);

        double totalKwh = logs.stream()
                .mapToDouble(log -> estimateKwh(log.getDevice(), log.getHoursUsed()))
                .sum();
        double totalCost = totalKwh * ELECTRIC_RATE;
        long deviceCount = logs.stream()
                .map(DeviceUsageLog::getDevice)
                .filter(Objects::nonNull)
                .map(Device::getId)
                .distinct()
                .count();

        Map<String, Object> res = new HashMap<>();
        res.put("kwhToday", round4(totalKwh));
        res.put("costToday", Math.round(totalCost));
        res.put("deviceCount", deviceCount);
        return res;
    }

    public List<Map<String, Object>> getHourlyLogs(LocalDate date) {
        List<DeviceUsageLog> logs = logRepository.findByUsageDate(date);
        double[] hourlyCost = new double[24];

        for (DeviceUsageLog log : logs) {
            int slot = 0;
            slot = Math.min(Math.max(slot, 0), 23);
            double kwh = estimateKwh(log.getDevice(), log.getHoursUsed());
            hourlyCost[slot] += kwh * ELECTRIC_RATE;
        }

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("label", i + "h-" + (i + 1) + "h");
            point.put("value", Math.round(hourlyCost[i]));
            chartData.add(point);
        }
        return chartData;
    }

    public List<Map<String, Object>> getMonthlyLogs(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<DeviceUsageLog> logs = logRepository.findByUsageDateBetween(startDate, endDate);
        Map<Integer, Double> costByDay = new HashMap<>();

        for (DeviceUsageLog log : logs) {
            int day = log.getUsageDate().getDayOfMonth();
            double kwh = estimateKwh(log.getDevice(), log.getHoursUsed());
            costByDay.merge(day, kwh * ELECTRIC_RATE, Double::sum);
        }

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (int i = 1; i <= startDate.lengthOfMonth(); i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("label", "Ngày " + i);
            point.put("value", Math.round(costByDay.getOrDefault(i, 0.0)));
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

    // Trả về chi tiết thiết bị kèm tổng giờ dùng hôm nay, kWh/chi phí và lịch sử sử dụng.
    public DeviceDetailResponse getDeviceDetail(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Double devicePower = device.getPowerRating();
        double power = devicePower != null ? devicePower : 0.0;

        List<DeviceUsageLog> todayLogs = logRepository
                .findByDeviceAndUsageDate(device, LocalDate.now());

        double totalHours = todayLogs.stream()
            .mapToDouble(log -> {
                Double used = log.getHoursUsed();
                    return used == null ? 0.0 : used;
            })
                .sum();

        double kwhToday = round4((power * totalHours) / 1000);
        double costToday = Math.round(kwhToday * ELECTRIC_RATE);

        List<DeviceUsageResponse> usageHistory = logRepository
            .findByDeviceOrderByUsageDateDescIdDesc(device)
                .stream()
                .sorted(Comparator.comparing(DeviceUsageLog::getUsageDate).reversed())
                .map(this::toUsageResponse)
                .toList();

        return new DeviceDetailResponse(
                device.getId(),
                device.getDeviceName(),
                device.getRoom().getRoomName(),
                power,
                round4(totalHours),
                false,
                kwhToday,
                costToday,
                usageHistory
        );
    }

    // Map entity log sang DTO để trả dữ liệu ổn định cho frontend.
    private DeviceUsageResponse toUsageResponse(DeviceUsageLog log) {
        DeviceUsageResponse response = new DeviceUsageResponse();
        response.setId(log.getId());
        response.setDeviceId(log.getDevice() != null ? log.getDevice().getId() : null);
        response.setDeviceName(log.getDevice() != null ? log.getDevice().getDeviceName() : null);
        response.setUsageDate(log.getUsageDate());
        response.setHoursUsed(log.getHoursUsed());
        return response;
    }

    // Tính điện năng tiêu thụ dựa trên công suất thiết bị và số giờ dùng.
    private double estimateKwh(Device device, Double hoursUsed) {
        if (device == null || device.getPowerRating() == null || hoursUsed == null) {
            return 0;
        }
        return (device.getPowerRating() * hoursUsed) / 1000;
    }

    // Làm tròn 4 chữ số thập phân để đồng nhất dữ liệu hiển thị.
    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }

}