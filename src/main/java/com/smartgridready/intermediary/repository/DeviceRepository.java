/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartgridready.intermediary.entity.Device;

import java.util.List;


/**
 * Repository for device.
 */
public interface DeviceRepository extends JpaRepository<Device, Long>
{
    List<Device> findByName( String name );
    List<Device> findByEiXmlName( String eiXmlName );
}
