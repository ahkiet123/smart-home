package fit.nlu.dapm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tip_categories")
@Getter
@Setter
public class TipCategory extends BaseEntity {

    @Column(name = "category_name", unique = true, nullable = false)
    private String categoryName;
}
