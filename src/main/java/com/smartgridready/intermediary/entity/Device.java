package com.smartgridready.intermediary.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Device {

    @Id
    @GeneratedValue
    private long id;

    @Column(length = 256, nullable = false, unique = true)
    private String name;

    @ManyToOne(optional = false)
    private ExternalInterfaceXml eiXml;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "device", orphanRemoval = true)
    private List<ConfigurationValue> configurationValues = new ArrayList<>();
}
