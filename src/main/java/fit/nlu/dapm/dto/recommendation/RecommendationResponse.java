package fit.nlu.dapm.dto.recommendation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RecommendationResponse {
    private Long id;
    private Long homeId;
    private Long tipId;
    private String tipTitle;
    private String tipDescription;
    private String status;
    private LocalDate recommendationDate;
    private Double estimatedSaving;
}
