package fit.nlu.dapm.dto.energy;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DailyEnergyPointResponse {
    private LocalDate date;
    private Double kwh;
    private Double estimatedCost;
}
