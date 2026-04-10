package fit.nlu.dapm.dto.energy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergyRoomDayItemResponse {
    private Long roomId;
    private String roomName;
    private Double kwh;
    private Double estimatedCost;
}
