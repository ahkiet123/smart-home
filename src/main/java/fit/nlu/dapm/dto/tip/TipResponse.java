package fit.nlu.dapm.dto.tip;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipResponse {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Double estimatedSaving;
}
