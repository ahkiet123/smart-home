package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.energy.DailyEnergyPointResponse;
import fit.nlu.dapm.dto.energy.EnergyDeviceDayItemResponse;
import fit.nlu.dapm.dto.energy.EnergyBreakdownItemResponse;
import fit.nlu.dapm.dto.energy.EnergyDailyAnalyticsResponse;
import fit.nlu.dapm.dto.energy.EnergyRoomDayItemResponse;
import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.DeviceType;
import fit.nlu.dapm.entity.EnergyRecord;
import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.Room;
import fit.nlu.dapm.entity.User;
import fit.nlu.dapm.exception.BadRequestException;
import fit.nlu.dapm.repository.DeviceRepository;
import fit.nlu.dapm.repository.DeviceTypeRepository;
import fit.nlu.dapm.repository.EnergyRecordRepository;
import fit.nlu.dapm.repository.HomeRepository;
import fit.nlu.dapm.repository.RoomRepository;
import fit.nlu.dapm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EnergyAnalyticsService {

    private static final double ELECTRICITY_RATE = 2500.0;
    private static final int MAX_RANGE_DAYS = 90;
    private static final int MAX_BREAKDOWN_ITEMS = 5;
    private static final int MIN_DEMO_DEVICE_COUNT = 12;

    @Autowired
    private EnergyRecordRepository energyRecordRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Map<String, Object> seedDemoData(Long userId, Integer days) {
        int rangeDays = (days == null || days <= 0) ? 30 : days;
        if (rangeDays > MAX_RANGE_DAYS) {
            throw new BadRequestException("So ngay seed toi da la 90");
        }

        List<Device> devices = ensureDevicesForSeeding(userId);

        int insertedCount = 0;
        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(rangeDays - 1L);

        for (Device device : devices) {
            double powerRating = device.getPowerRating() == null ? 120.0 : device.getPowerRating();

            for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
                boolean existed = energyRecordRepository.findByDeviceAndUsageDate(device, date).isPresent();
                if (existed) {
                    continue;
                }

                // Generate stable-but-random usage around 1h~3.6h/day from device power.
                double hours = 0.9 + ThreadLocalRandom.current().nextDouble() * 2.7;
                double kwh = Math.max(0.12, (powerRating / 1000.0) * hours);

                EnergyRecord record = new EnergyRecord();
                record.setDevice(device);
                record.setUsageDate(date);
                record.setEnergyConsumed(Math.round(kwh * 1000.0) / 1000.0);

                energyRecordRepository.save(record);
                insertedCount++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("inserted", insertedCount);
        result.put("days", rangeDays);
        result.put("devices", devices.size());
        return result;
    }

    private List<Device> ensureDevicesForSeeding(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Nguoi dung khong ton tai"));

        Home home = homeRepository.findByUser(user)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    Home newHome = new Home();
                    newHome.setUser(user);
                    newHome.setHomeName("Nha demo");
                    newHome.setHomeType("Apartment");
                    newHome.setArea(65.0);
                    newHome.setNumberOfRooms(3);
                    newHome.setNumberOfFloors(1);
                    return homeRepository.save(newHome);
                });

        mergeDuplicateRoomsByName(home);

        List<Device> devices = deviceRepository.findAllByUserId(userId);
        if (devices.size() >= MIN_DEMO_DEVICE_COUNT) {
            return devices;
        }

        DeviceType type = deviceTypeRepository.findByTypeName("Demo Device")
                .orElseGet(() -> {
                    DeviceType newType = new DeviceType();
                    newType.setTypeName("Demo Device");
                    newType.setDescription("Loai thiet bi tao tu dong de seed du lieu");
                    return deviceTypeRepository.save(newType);
                });

        List<Room> homeRooms = roomRepository.findByHome(home);
        Map<String, Room> roomByName = new LinkedHashMap<>();
        for (Room room : homeRooms) {
            if (room.getRoomName() != null) {
                roomByName.put(normalizeKey(room.getRoomName()), room);
            }
        }

        devices = deviceRepository.findAllByUserId(userId);
        Set<String> existingDeviceKeys = new HashSet<>();
        for (Device device : devices) {
            String roomName = device.getRoom() != null ? device.getRoom().getRoomName() : "";
            String deviceName = device.getDeviceName() == null ? "" : device.getDeviceName();
            existingDeviceKeys.add(normalizeKey(roomName) + "|" + normalizeKey(deviceName));
        }

        Object[][] demoDevices = new Object[][] {
                {"Phòng khách", "Điều hòa", 1200.0},
                {"Phòng khách", "Tivi", 140.0},
                {"Phòng khách", "Đèn trần", 40.0},
                {"Phòng khách", "Quạt đứng", 70.0},
                {"Phòng ngủ", "Máy lạnh", 1000.0},
                {"Phòng ngủ", "Đèn ngủ", 15.0},
                {"Phòng ngủ", "Máy lọc không khí", 55.0},
                {"Phòng ngủ", "Sạc laptop", 65.0},
                {"Bếp", "Tủ lạnh", 180.0},
                {"Bếp", "Lò vi sóng", 1200.0},
                {"Bếp", "Nồi cơm điện", 700.0},
                {"Bếp", "Máy hút mùi", 110.0},
                {"Phòng tắm", "Bình nóng lạnh", 2500.0},
                {"Phòng tắm", "Đèn gương", 20.0},
                {"Phòng làm việc", "Máy tính để bàn", 300.0},
                {"Phòng làm việc", "Màn hình", 45.0}
        };

        int created = 0;
        for (Object[] spec : demoDevices) {
            String roomName = (String) spec[0];
            String deviceName = (String) spec[1];
            Double powerRating = (Double) spec[2];

            String key = normalizeKey(roomName) + "|" + normalizeKey(deviceName);
            if (existingDeviceKeys.contains(key)) {
                continue;
            }

            Room room = roomByName.get(normalizeKey(roomName));
            if (room == null) {
                room = new Room();
                room.setHome(home);
                room.setRoomName(roomName);
                room.setRoomType("STANDARD");
                room.setArea(12.0 + ThreadLocalRandom.current().nextDouble() * 10.0);
                room = roomRepository.save(room);
                roomByName.put(normalizeKey(roomName), room);
            }

            Device device = new Device();
            device.setRoom(room);
            device.setDeviceType(type);
            device.setDeviceName(deviceName);
            device.setBrand("DemoBrand");
            device.setModelNumber("D-" + (existingDeviceKeys.size() + 1));
            device.setPowerRating(powerRating);
            deviceRepository.save(device);

            existingDeviceKeys.add(key);
            created++;

            if (existingDeviceKeys.size() >= MIN_DEMO_DEVICE_COUNT && created >= 6) {
                // Keep demo realistic while ensuring enough device variety.
                break;
            }
        }

        return deviceRepository.findAllByUserId(userId);
    }

    private String normalizeKey(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.replace('đ', 'd');
    }

    private void mergeDuplicateRoomsByName(Home home) {
        List<Room> rooms = roomRepository.findByHome(home);
        Map<String, Room> canonical = new LinkedHashMap<>();

        for (Room room : rooms) {
            String key = normalizeKey(room.getRoomName());
            Room primary = canonical.get(key);

            if (primary == null) {
                canonical.put(key, room);
                continue;
            }

            List<Device> duplicateRoomDevices = deviceRepository.findByRoom(room);
            for (Device device : duplicateRoomDevices) {
                device.setRoom(primary);
                deviceRepository.save(device);
            }

            roomRepository.delete(room);
        }
    }

    public EnergyDailyAnalyticsResponse getDailyAnalytics(
            Long userId,
            LocalDate fromDate,
            LocalDate toDate,
            Long roomId,
            Long deviceId,
            String groupBy
    ) {
        if (fromDate == null || toDate == null) {
            throw new BadRequestException("Vui long chon tu ngay va den ngay");
        }

        if (fromDate.isAfter(toDate)) {
            throw new BadRequestException("Tu ngay phai nho hon hoac bang den ngay");
        }

        int totalDays = (int) ChronoUnit.DAYS.between(fromDate, toDate) + 1;
        if (totalDays > MAX_RANGE_DAYS) {
            throw new BadRequestException("Khoang thoi gian toi da la 90 ngay");
        }

        String breakdownType = "device".equalsIgnoreCase(groupBy) ? "device" : "room";

        List<Object[]> currentRows = energyRecordRepository.aggregateDailyEnergy(
                userId, fromDate, toDate, roomId, deviceId
        );

        Map<LocalDate, Double> currentMap = new LinkedHashMap<>();
        for (Object[] row : currentRows) {
            LocalDate usageDate = (LocalDate) row[0];
            Double totalKwh = ((Number) row[1]).doubleValue();
            currentMap.put(usageDate, totalKwh);
        }

        List<DailyEnergyPointResponse> dailySeries = new ArrayList<>();
        double totalKwh = 0.0;

        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            double kwh = currentMap.getOrDefault(date, 0.0);

            DailyEnergyPointResponse point = new DailyEnergyPointResponse();
            point.setDate(date);
            point.setKwh(kwh);
            point.setEstimatedCost(kwh * ELECTRICITY_RATE);
            dailySeries.add(point);

            totalKwh += kwh;
        }

        LocalDate previousFrom = fromDate.minusDays(totalDays);
        LocalDate previousTo = fromDate.minusDays(1);

        List<Object[]> previousRows = energyRecordRepository.aggregateDailyEnergy(
                userId, previousFrom, previousTo, roomId, deviceId
        );

        double previousPeriodTotalKwh = 0.0;
        for (Object[] row : previousRows) {
            previousPeriodTotalKwh += ((Number) row[1]).doubleValue();
        }

        double totalEstimatedCost = totalKwh * ELECTRICITY_RATE;
        double previousPeriodTotalCost = previousPeriodTotalKwh * ELECTRICITY_RATE;
        double deltaKwh = totalKwh - previousPeriodTotalKwh;
        double deltaCost = totalEstimatedCost - previousPeriodTotalCost;

        Double deltaPercent = null;
        if (previousPeriodTotalKwh > 0) {
            deltaPercent = (deltaKwh / previousPeriodTotalKwh) * 100.0;
        } else if (totalKwh > 0) {
            deltaPercent = 100.0;
        }

        double lastDayKwh = currentMap.getOrDefault(toDate, 0.0);
        Double previousDayKwhObj = energyRecordRepository.aggregateTotalEnergyByDate(
                userId, toDate.minusDays(1), roomId, deviceId
        );
        double previousDayKwh = previousDayKwhObj == null ? 0.0 : previousDayKwhObj;

        double lastDayDeltaKwh = lastDayKwh - previousDayKwh;
        Double lastDayDeltaPercent = null;
        if (previousDayKwh > 0) {
            lastDayDeltaPercent = (lastDayDeltaKwh / previousDayKwh) * 100.0;
        } else if (lastDayKwh > 0) {
            lastDayDeltaPercent = 100.0;
        }

        List<Object[]> breakdownRows = "device".equals(breakdownType)
                ? energyRecordRepository.aggregateByDevice(userId, fromDate, toDate, roomId, deviceId)
                : energyRecordRepository.aggregateByRoom(userId, fromDate, toDate, roomId, deviceId);

        List<EnergyBreakdownItemResponse> breakdown = breakdownRows.stream()
                .limit(MAX_BREAKDOWN_ITEMS)
                .map(row -> {
                    EnergyBreakdownItemResponse item = new EnergyBreakdownItemResponse();
                    item.setId(((Number) row[0]).longValue());
                    item.setName((String) row[1]);
                    double kwh = ((Number) row[2]).doubleValue();
                    item.setKwh(kwh);
                    item.setEstimatedCost(kwh * ELECTRICITY_RATE);
                    return item;
                })
                .toList();

        EnergyDailyAnalyticsResponse response = new EnergyDailyAnalyticsResponse();
        response.setFromDate(fromDate);
        response.setToDate(toDate);
        response.setTotalDays(totalDays);

        response.setTotalKwh(totalKwh);
        response.setTotalEstimatedCost(totalEstimatedCost);

        response.setPreviousPeriodTotalKwh(previousPeriodTotalKwh);
        response.setPreviousPeriodTotalCost(previousPeriodTotalCost);
        response.setDeltaKwh(deltaKwh);
        response.setDeltaCost(deltaCost);
        response.setDeltaPercent(deltaPercent);

        response.setLastDayKwh(lastDayKwh);
        response.setPreviousDayKwh(previousDayKwh);
        response.setLastDayDeltaKwh(lastDayDeltaKwh);
        response.setLastDayDeltaPercent(lastDayDeltaPercent);

        response.setBreakdownType(breakdownType);
        response.setBreakdown(breakdown);
        response.setDailySeries(dailySeries);
        response.setHasData(totalKwh > 0.0);

        return response;
    }

    public List<EnergyRoomDayItemResponse> getRoomsByDate(Long userId, LocalDate usageDate) {
        List<Object[]> rows = energyRecordRepository.aggregateRoomsByDate(userId, usageDate);
        return rows.stream().map(row -> {
            EnergyRoomDayItemResponse item = new EnergyRoomDayItemResponse();
            double kwh = ((Number) row[2]).doubleValue();

            item.setRoomId(((Number) row[0]).longValue());
            item.setRoomName((String) row[1]);
            item.setKwh(kwh);
            item.setEstimatedCost(kwh * ELECTRICITY_RATE);
            return item;
        }).toList();
    }

    public List<EnergyDeviceDayItemResponse> getDevicesByRoomAndDate(Long userId, Long roomId, LocalDate usageDate) {
        List<Object[]> rows = energyRecordRepository.aggregateDevicesByRoomAndDate(userId, roomId, usageDate);
        return rows.stream().map(row -> {
            EnergyDeviceDayItemResponse item = new EnergyDeviceDayItemResponse();
            double powerRating = row[2] == null ? 0.0 : ((Number) row[2]).doubleValue();
            double kwh = ((Number) row[3]).doubleValue();

            item.setDeviceId(((Number) row[0]).longValue());
            item.setDeviceName((String) row[1]);
            item.setPowerRating(powerRating);
            item.setKwh(kwh);
            item.setEstimatedCost(kwh * ELECTRICITY_RATE);
            return item;
        }).toList();
    }
}
