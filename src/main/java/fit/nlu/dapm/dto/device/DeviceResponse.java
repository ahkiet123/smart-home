package fit.nlu.dapm.dto.device;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceResponse {
    private Long id;
    private Long roomId;
    private Long deviceTypeId;
    private String typeName;
    private String deviceName;
    private String brand;
    private String modelNumber;
    private Double powerRating;
}
