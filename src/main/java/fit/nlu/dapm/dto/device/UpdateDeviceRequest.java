package fit.nlu.dapm.dto.device;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDeviceRequest {
    private String deviceName;
    private Double powerRating;
    private Long roomId;
    private Long deviceTypeId;
    private String brand;
    private String modelNumber;
}
