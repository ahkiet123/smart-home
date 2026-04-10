package fit.nlu.dapm.dto.energy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergyBreakdownItemResponse {
    private Long id;
    private String name;
    private Double kwh;
    private Double estimatedCost;
}
