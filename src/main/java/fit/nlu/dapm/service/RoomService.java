package fit.nlu.dapm.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fit.nlu.dapm.dto.room.RoomRequest;
import fit.nlu.dapm.dto.room.RoomResponse;
import fit.nlu.dapm.dto.room.RoomStatsResponse;
import fit.nlu.dapm.entity.Device;
import fit.nlu.dapm.entity.DeviceUsageLog;
import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.Room;
import fit.nlu.dapm.entity.User;
import fit.nlu.dapm.exception.ResourceNotFoundException;
import fit.nlu.dapm.mapper.GenericMapper;
import fit.nlu.dapm.repository.DeviceRepository;
import fit.nlu.dapm.repository.HomeRepository;
import fit.nlu.dapm.repository.RoomRepository;
import fit.nlu.dapm.repository.UserRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private GenericMapper mapper;

    @Transactional
    public RoomResponse createRoom(Long userId, RoomRequest request) {
        Home home = resolvePrimaryHome(userId);

        if (request.getHomeId() != null && !request.getHomeId().equals(home.getId())) {
            home = homeRepository.findById(request.getHomeId())
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
        }

        Room room = mapper.map(request, Room.class);
        room.setHome(home);

        room = roomRepository.save(room);
        return mapper.map(room, RoomResponse.class);
    }

    public List<RoomResponse> getRoomsByHome(Long homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

        List<Room> rooms = roomRepository.findByHome(home);
        return mapper.mapList(rooms, RoomResponse.class);
    }

    @Transactional(readOnly = true)
    public List<RoomStatsResponse> getRoomStatsForCurrentUser(Long userId, String search, String sortBy, String order) {
        Home home = resolvePrimaryHome(userId);
        List<Room> rooms = roomRepository.findByHome(home);

        LocalDate currentStart = YearMonth.now().atDay(1);
        LocalDate currentEnd = YearMonth.now().atEndOfMonth();
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        LocalDate previousStart = previousMonth.atDay(1);
        LocalDate previousEnd = previousMonth.atEndOfMonth();

        String normalizedSearch = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);

        List<RoomStatsResponse> responses = rooms.stream()
                .map(room -> toStats(room, currentStart, currentEnd, previousStart, previousEnd))
                .filter(room -> normalizedSearch.isBlank()
                        || room.getRoomName().toLowerCase(Locale.ROOT).contains(normalizedSearch)
                        || (room.getRoomType() != null && room.getRoomType().toLowerCase(Locale.ROOT).contains(normalizedSearch)))
                .collect(Collectors.toCollection(ArrayList::new));

        java.util.Comparator<RoomStatsResponse> comparator;
        if ("cost".equalsIgnoreCase(sortBy)) {
            comparator = java.util.Comparator.comparing(RoomStatsResponse::getCurrentCost, java.util.Comparator.nullsLast(Double::compareTo));
        } else if ("trend".equalsIgnoreCase(sortBy)) {
            comparator = java.util.Comparator.comparing(RoomStatsResponse::getTrendPercent, java.util.Comparator.nullsLast(Double::compareTo));
        } else if ("name".equalsIgnoreCase(sortBy)) {
            comparator = java.util.Comparator.comparing(RoomStatsResponse::getRoomName, String.CASE_INSENSITIVE_ORDER);
        } else {
            comparator = java.util.Comparator.comparing(RoomStatsResponse::getCurrentKwh, java.util.Comparator.nullsLast(Double::compareTo));
        }

        if ("asc".equalsIgnoreCase(order)) {
            responses.sort(comparator);
        } else {
            responses.sort(comparator.reversed());
        }

        return responses;
    }

    @Transactional(readOnly = true)
    public RoomStatsResponse getRoomStatsById(Long roomId, Long userId) {
        Home home = resolvePrimaryHome(userId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getHome() == null || !room.getHome().getId().equals(home.getId())) {
            throw new ResourceNotFoundException("Room not found");
        }

        LocalDate currentStart = YearMonth.now().atDay(1);
        LocalDate currentEnd = YearMonth.now().atEndOfMonth();
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        LocalDate previousStart = previousMonth.atDay(1);
        LocalDate previousEnd = previousMonth.atEndOfMonth();

        return toStats(room, currentStart, currentEnd, previousStart, previousEnd);
    }

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // Update explicitly to avoid ModelMapper trying to bind homeId -> home entity.
        room.setRoomName(request.getRoomName());
        room.setRoomType(request.getRoomType());
        room.setArea(request.getArea());

        if (request.getHomeId() != null
            && (room.getHome() == null || !request.getHomeId().equals(room.getHome().getId()))) {
            Home newHome = homeRepository.findById(request.getHomeId())
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));
            room.setHome(newHome);
        }

        room = roomRepository.save(room);
        return mapper.map(room, RoomResponse.class);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        roomRepository.delete(room);
    }

    private Home resolvePrimaryHome(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Home> homes = homeRepository.findByUser(user);
        if (homes.isEmpty()) {
            throw new ResourceNotFoundException("Home not found");
        }

        return homes.get(0);
    }

    private RoomStatsResponse toStats(Room room,
                                      LocalDate currentStart,
                                      LocalDate currentEnd,
                                      LocalDate previousStart,
                                      LocalDate previousEnd) {
        List<Device> devices = deviceRepository.findByRoom(room);

        double currentHours = 0;
        double previousHours = 0;

        for (Device device : devices) {
            for (DeviceUsageLog log : device.getUsageLogs()) {
                LocalDate usageDate = log.getUsageDate();
                Double hoursUsed = log.getHoursUsed();
                if (usageDate == null || hoursUsed == null) {
                    continue;
                }

                double kwh = estimateKwh(device, hoursUsed);
                if (!usageDate.isBefore(currentStart) && !usageDate.isAfter(currentEnd)) {
                    currentHours += kwh;
                } else if (!usageDate.isBefore(previousStart) && !usageDate.isAfter(previousEnd)) {
                    previousHours += kwh;
                }
            }
        }

        RoomStatsResponse response = new RoomStatsResponse();
        response.setId(room.getId());
        response.setHomeId(room.getHome() != null ? room.getHome().getId() : null);
        response.setRoomName(room.getRoomName());
        response.setRoomType(room.getRoomType());
        response.setArea(room.getArea());
        response.setDeviceCount(devices.size());
        response.setCurrentKwh(round4(currentHours));
        response.setCurrentCost((double) Math.round(currentHours * 2500));
        response.setPreviousKwh(round4(previousHours));
        response.setPreviousCost((double) Math.round(previousHours * 2500));
        response.setTrendPercent(calculateTrendPercent(currentHours, previousHours));
        return response;
    }

    private double estimateKwh(Device device, Double hoursUsed) {
        if (device == null || device.getPowerRating() == null || hoursUsed == null) {
            return 0;
        }
        return (device.getPowerRating() * hoursUsed) / 1000;
    }

    private double calculateTrendPercent(double currentKwh, double previousKwh) {
        if (previousKwh <= 0) {
            return currentKwh > 0 ? 100 : 0;
        }
        return round4(((currentKwh - previousKwh) / previousKwh) * 100);
    }

    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
