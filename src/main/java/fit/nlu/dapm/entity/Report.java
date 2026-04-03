package fit.nlu.dapm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private Home home;

    @Column(name = "total_energy_before")
    private Double totalEnergyBefore;

    @Column(name = "total_energy_after")
    private Double totalEnergyAfter;

    @Column(name = "saving_percentage")
    private Double savingPercentage;
}
