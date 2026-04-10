package fit.nlu.dapm.dto.device;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRequest {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Device type ID is required")
    private Long deviceTypeId;

    @NotBlank(message = "Device name is required")
    private String deviceName;

    private String brand;
    private String modelNumber;

    @Min(value = 1, message = "Power rating must be greater than 0")
    private Double powerRating;
}
