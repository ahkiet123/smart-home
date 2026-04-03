package fit.nlu.dapm.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRecommendationStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
