package fit.nlu.dapm.dto.home;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeResponse {
    private Long id;
    private String homeName;
    private String homeType;
    private Double area;
    private Integer numberOfRooms;
    private Integer numberOfFloors;
    private Integer buildYear;
}
