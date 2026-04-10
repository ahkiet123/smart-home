package fit.nlu.dapm.dto.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {
    private Long homeId;

    @NotBlank(message = "Room name is required")
    private String roomName;

    private String roomType;

    private Double area;

    public Long getHomeId() { return homeId; }
}
