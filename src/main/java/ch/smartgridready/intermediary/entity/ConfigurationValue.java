package ch.smartgridready.intermediary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
public class ConfigurationValue {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    @Column(nullable = false, length = 4096)
    private String val;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    private Device device;
}
