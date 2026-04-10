package fit.nlu.dapm.dto.device;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DeviceUsageRequest {

    @NotNull(message = "Device ID is required")
    private Long deviceId;

    @NotNull(message = "Usage date is required")
    private LocalDate usageDate;

    @Min(value = 0, message = "Hours used must be at least 0")
    @Max(value = 24, message = "Hours used cannot exceed 24")
    private Double hoursUsed;
}
