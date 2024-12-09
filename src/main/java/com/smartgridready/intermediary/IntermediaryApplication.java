/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * SpringBoot main.
 */
@SpringBootApplication
public class IntermediaryApplication
{

    public static void main( String... args )
    {
        SpringApplication.run( IntermediaryApplication.class, args );
    }
}
