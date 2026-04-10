package fit.nlu.dapm.dto.room;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequest {

    @NotNull(message = "Home ID is required")
    private Long homeId;

    @NotBlank(message = "Room name is required")
    private String roomName;

    private String roomType;

    private Double area;

    public Long getHomeId() { return homeId; }
}
