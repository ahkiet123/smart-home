package fit.nlu.dapm.dto.bill;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElectricityBillResponse {
    private Long id;
    private Long homeId;
    private Integer month;
    private Integer year;
    private Double totalKwh;
    private Double totalCost;
}
