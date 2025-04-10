/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Entity for configuration value.
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class ConfigurationValue
{
    @JsonIgnore
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

    @Override
    public String toString()
    {
        return "id='" + id + "', name='" + name + "', val='" + val + "'";
    }
}
