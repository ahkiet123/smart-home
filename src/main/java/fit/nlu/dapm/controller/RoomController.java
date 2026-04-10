package fit.nlu.dapm.controller;

import fit.nlu.dapm.dto.room.RoomDropdownResponse;
import fit.nlu.dapm.repository.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private  RoomRepository roomRepository;

    @GetMapping
    public List<RoomDropdownResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(r -> new RoomDropdownResponse(r.getId(), r.getRoomName()))
                .toList();
    }
}
