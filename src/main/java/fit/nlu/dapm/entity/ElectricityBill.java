package fit.nlu.dapm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "electricity_bills")
@Getter
@Setter
public class ElectricityBill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id")
    private Home home;

    @Column(name = "\"month\"")
    private Integer month;

    @Column(name = "\"year\"")
    private Integer year;

    @Column(name = "total_kwh")
    private Double totalKwh;

    @Column(name = "total_cost")
    private Double totalCost;
}
