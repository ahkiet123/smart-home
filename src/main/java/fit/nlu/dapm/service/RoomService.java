package fit.nlu.dapm.service;

import fit.nlu.dapm.dto.room.RoomRequest;
import fit.nlu.dapm.dto.room.RoomResponse;
import fit.nlu.dapm.entity.Home;
import fit.nlu.dapm.entity.Room;
import fit.nlu.dapm.exception.ResourceNotFoundException;
import fit.nlu.dapm.mapper.GenericMapper;
import fit.nlu.dapm.repository.HomeRepository;
import fit.nlu.dapm.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private GenericMapper mapper;

    public RoomResponse createRoom(RoomRequest request) {
        Home home = homeRepository.findById(request.getHomeId())
                .orElseThrow(() -> new ResourceNotFoundException("Home not found"));

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

    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        mapper.mapTo(request, room);
        room = roomRepository.save(room);
        return mapper.map(room, RoomResponse.class);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        roomRepository.deleteById(roomId);
    }
}
