package com.smartgridready.intermediary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartgridready.intermediary.service.IntermediaryService;

import lombok.AllArgsConstructor;


/**
 * Loads data base.
 */
@AllArgsConstructor
@Configuration
class LoadDatabase
{

    private static final Logger log = LoggerFactory.getLogger( LoadDatabase.class );

    private final IntermediaryService intermediaryService;

    /**
     * Loads the devices.
     * 
     * @return {@code CommandLineRunner}
     */
    @Bean
    CommandLineRunner initDatabase()
    {
        log.info( "Going to load devices." );
        return args -> intermediaryService.loadDevices();
    }
}
