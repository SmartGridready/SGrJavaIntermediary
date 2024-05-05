package ch.smartgridready.intermediary;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
