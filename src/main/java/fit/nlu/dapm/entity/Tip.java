package fit.nlu.dapm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tips")
@Getter
@Setter
public class Tip extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private TipCategory category;

    @Column(name = "estimated_saving")
    private Double estimatedSaving;
}
