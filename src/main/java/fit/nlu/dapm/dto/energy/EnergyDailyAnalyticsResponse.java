package fit.nlu.dapm.dto.energy;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EnergyDailyAnalyticsResponse {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer totalDays;

    private Double totalKwh;
    private Double totalEstimatedCost;

    private Double previousPeriodTotalKwh;
    private Double previousPeriodTotalCost;
    private Double deltaKwh;
    private Double deltaCost;
    private Double deltaPercent;

    private Double lastDayKwh;
    private Double previousDayKwh;
    private Double lastDayDeltaKwh;
    private Double lastDayDeltaPercent;

    private String breakdownType;
    private List<EnergyBreakdownItemResponse> breakdown;
    private List<DailyEnergyPointResponse> dailySeries;

    private Boolean hasData;
}
