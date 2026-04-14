package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.room.RoomDropdownResponse;
import fit.nlu.dapm.dto.room.RoomRequest;
import fit.nlu.dapm.dto.room.RoomResponse;
import fit.nlu.dapm.dto.room.RoomStatsResponse;
import fit.nlu.dapm.security.SecurityUtil;
import fit.nlu.dapm.service.RoomService;
import fit.nlu.dapm.repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<RoomDropdownResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(r -> new RoomDropdownResponse(r.getId(), r.getRoomName()))
                .toList();
    }

    @GetMapping("/by-home")
    public List<RoomResponse> getRoomsByHome(@RequestParam Long homeId) {
        return roomService.getRoomsByHome(homeId);
    }

    @GetMapping("/stats")
    public List<RoomStatsResponse> getRoomStats(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "kwh") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        Long userId = securityUtil.getCurrentUserId();
        return roomService.getRoomStatsForCurrentUser(userId, search, sortBy, order);
    }

    @GetMapping("/{id}/stats")
    public RoomStatsResponse getRoomStatsById(@PathVariable Long id) {
        Long userId = securityUtil.getCurrentUserId();
        return roomService.getRoomStatsById(id, userId);
    }

    @PostMapping
    public RoomResponse createRoom(@Valid @RequestBody RoomRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        return roomService.createRoom(userId, request);
    }
}
