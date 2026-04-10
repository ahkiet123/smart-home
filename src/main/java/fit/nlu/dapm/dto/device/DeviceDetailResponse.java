package fit.nlu.dapm.dto.device;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DeviceDetailResponse {
    private Long id;
    private String deviceName;
    private String roomName;
    private Double powerRating;
    private Double hours;
    private Boolean isOn;
    private Double kwhToday;
    private Double costToday;
}
