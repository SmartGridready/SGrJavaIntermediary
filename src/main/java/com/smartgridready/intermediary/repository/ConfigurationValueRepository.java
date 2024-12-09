/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartgridready.intermediary.entity.ConfigurationValue;


/**
 * Repository for configuration value. 
 */
public interface ConfigurationValueRepository extends JpaRepository<ConfigurationValue, Long>
{
}
