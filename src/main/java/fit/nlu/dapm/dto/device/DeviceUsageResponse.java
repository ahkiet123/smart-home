package fit.nlu.dapm.dto.device;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DeviceUsageResponse {
    private Long id;
    private Long deviceId;
    private String deviceName;
    private LocalDate usageDate;
    private Double hoursUsed;
}
