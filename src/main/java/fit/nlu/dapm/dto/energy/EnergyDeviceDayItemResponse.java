package fit.nlu.dapm.dto.energy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergyDeviceDayItemResponse {
    private Long deviceId;
    private String deviceName;
    private Double powerRating;
    private Double kwh;
    private Double estimatedCost;
}
