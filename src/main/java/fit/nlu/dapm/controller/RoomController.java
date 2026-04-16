package fit.nlu.dapm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fit.nlu.dapm.dto.room.RoomDropdownResponse;
import fit.nlu.dapm.dto.room.RoomRequest;
import fit.nlu.dapm.dto.room.RoomResponse;
import fit.nlu.dapm.dto.room.RoomStatsResponse;
import fit.nlu.dapm.repository.RoomRepository;
import fit.nlu.dapm.security.SecurityUtil;
import fit.nlu.dapm.service.RoomService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomRepository roomRepository;
    private final RoomService roomService;
    private final SecurityUtil securityUtil;

    public RoomController(RoomRepository roomRepository, RoomService roomService, SecurityUtil securityUtil) {
        this.roomRepository = roomRepository;
        this.roomService = roomService;
        this.securityUtil = securityUtil;
    }

    // Lấy danh sách tất cả các phòng dưới dạng dropdown (chỉ id và tên).
    @GetMapping
    public List<RoomDropdownResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(r -> new RoomDropdownResponse(r.getId(), r.getRoomName()))
                .toList();
    }

    //Lấy danh sách phòng theo homeId.
    @GetMapping("/by-home")
    public List<RoomResponse> getRoomsByHome(@RequestParam Long homeId) {
        return roomService.getRoomsByHome(homeId);
    }

    //Lấy thống kê tiêu thụ điện của tất cả phòng cho người dùng hiện tại.
    @GetMapping("/stats")
    public List<RoomStatsResponse> getRoomStats(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "kwh") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        Long userId = securityUtil.getCurrentUserId();
        return roomService.getRoomStatsForCurrentUser(userId, search, sortBy, order);
    }

    //Lấy thống kê chi tiết tiêu thụ điện của một phòng cụ thể.
    @GetMapping("/{id}/stats")
    public RoomStatsResponse getRoomStatsById(@PathVariable Long id) {
        Long userId = securityUtil.getCurrentUserId();
        return roomService.getRoomStatsById(id, userId);
    }

    //Tạo phòng mới cho người dùng hiện tại.
    @PostMapping
    public RoomResponse createRoom(@Valid @RequestBody RoomRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        return roomService.createRoom(userId, request);
    }

    //Cập nhật thông tin phòng (tên, loại, diện tích).
    @PutMapping("/{id}")
    public RoomResponse updateRoom(@PathVariable Long id, @Valid @RequestBody RoomRequest request) {
        return roomService.updateRoom(id, request);
    }

    //Xóa một phòng theo ID.
    @DeleteMapping("/{id}")
    public Map<String, String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return Map.of("message", "Room deleted successfully");
    }
}
