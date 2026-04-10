package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.room.RoomDropdownResponse;
import fit.nlu.dapm.repository.RoomRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public List<RoomDropdownResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(r -> new RoomDropdownResponse(r.getId(), r.getRoomName()))
                .toList();
    }
}
