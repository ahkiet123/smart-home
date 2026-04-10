package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.ApiResponse;
import fit.nlu.dapm.dto.energy.EnergyDeviceDayItemResponse;
import fit.nlu.dapm.dto.energy.EnergyDailyAnalyticsResponse;
import fit.nlu.dapm.dto.energy.EnergyRoomDayItemResponse;
import fit.nlu.dapm.exception.BadRequestException;
import fit.nlu.dapm.security.SecurityUtil;
import fit.nlu.dapm.service.EnergyAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/energy/analytics")
public class EnergyAnalyticsController {

    @Autowired
    private EnergyAnalyticsService energyAnalyticsService;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<EnergyDailyAnalyticsResponse>> getDailyAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(defaultValue = "room") String groupBy
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BadRequestException("Unauthorized");
        }

        EnergyDailyAnalyticsResponse response = energyAnalyticsService.getDailyAnalytics(
                userId,
                fromDate,
                toDate,
                roomId,
                deviceId,
                groupBy
        );

        return ResponseEntity.ok(ApiResponse.success("Daily analytics retrieved", response));
    }

    @PostMapping("/seed-demo")
    public ResponseEntity<ApiResponse<Map<String, Object>>> seedDemoData(
            @RequestParam(defaultValue = "30") Integer days
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BadRequestException("Unauthorized");
        }

        Map<String, Object> result = energyAnalyticsService.seedDemoData(userId, days);
        return ResponseEntity.ok(ApiResponse.success("Seed demo data successful", result));
    }

    @GetMapping("/day/rooms")
    public ResponseEntity<ApiResponse<List<EnergyRoomDayItemResponse>>> getRoomsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate usageDate
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BadRequestException("Unauthorized");
        }

        List<EnergyRoomDayItemResponse> response = energyAnalyticsService.getRoomsByDate(userId, usageDate);
        return ResponseEntity.ok(ApiResponse.success("Room daily analytics retrieved", response));
    }

    @GetMapping("/day/rooms/{roomId}/devices")
    public ResponseEntity<ApiResponse<List<EnergyDeviceDayItemResponse>>> getDevicesByRoomAndDate(
            @org.springframework.web.bind.annotation.PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate usageDate
    ) {
        Long userId = securityUtil.getCurrentUserId();
        if (userId == null) {
            throw new BadRequestException("Unauthorized");
        }

        List<EnergyDeviceDayItemResponse> response = energyAnalyticsService.getDevicesByRoomAndDate(userId, roomId, usageDate);
        return ResponseEntity.ok(ApiResponse.success("Device daily analytics retrieved", response));
    }
}
