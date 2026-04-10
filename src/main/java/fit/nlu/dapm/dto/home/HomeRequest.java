package fit.nlu.dapm.dto.home;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeRequest {

    @NotBlank(message = "Home name is required")
    private String homeName;

    private String homeType;

    @Min(value = 1, message = "Area must be greater than 0")
    private Double area;

    private Integer numberOfRooms;
    private Integer numberOfFloors;
    private Integer buildYear;
}
