package fit.nlu.dapm.dto.room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomResponse {
    private Long id;
    private Long homeId;
    private String roomName;
    private String roomType;
    private Double area;
}
