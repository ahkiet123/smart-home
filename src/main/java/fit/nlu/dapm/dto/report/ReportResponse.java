package fit.nlu.dapm.dto.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponse {
    private Long id;
    private Long homeId;
    private String homeName;
    private Double totalEnergyBefore;
    private Double totalEnergyAfter;
    private Double savingPercentage;
}
