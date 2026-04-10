package fit.nlu.dapm.dto.home;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeSummaryResponse {
    private Long id;
    private String homeName;
    private String homeType;
}
