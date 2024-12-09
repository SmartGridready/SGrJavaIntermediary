/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Entity for device.
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Device
{
    @Id
    @GeneratedValue
    private long id;

    @Column(length = 256, nullable = false, unique = true)
    private String name;

    @ManyToOne(optional = false)
    private ExternalInterfaceXml eiXml;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "device",
            orphanRemoval = true)
    private List<ConfigurationValue> configurationValues = new ArrayList<>();

    @Override
    public String toString()
    {
        return "id='" + id + "', name='" + name + "', eiXml.name='" + eiXml.getName() 
                + "', configurationValues=" + configurationValues;
    }
}
