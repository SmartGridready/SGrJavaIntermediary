package com.smartgridready.intermediary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartgridready.intermediary.entity.ConfigurationValue;


/**
 * Repository for configuration value. 
 */
public interface ConfigurationValueRepository extends JpaRepository<ConfigurationValue, Long>
{
}
