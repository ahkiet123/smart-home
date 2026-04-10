package fit.nlu.dapm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "device_types")
@Getter
@Setter
public class DeviceType extends BaseEntity {

    @Column(name = "type_name", unique = true, nullable = false)
    private String typeName;

    private String description;
}
