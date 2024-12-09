/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartgridready.communicator.modbus.api.ModbusGatewayRegistry;
import com.smartgridready.communicator.modbus.impl.SGrModbusGatewayRegistry;
import com.smartgridready.driver.apachehttp.ApacheHttpClientFactory;
import com.smartgridready.driver.api.http.GenHttpClientFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;
import com.smartgridready.driver.hivemq.HiveMqtt5MessagingClientFactory;
import com.smartgridready.intermediary.service.GitHubLoader;


/**
 * Configuration for factories. 
 */
@Configuration
public class IntermediaryConfiguration
{

    @Bean
    GenHttpClientFactory httpRequestFactory()
    {
        return new ApacheHttpClientFactory();
    }

    @Bean
    GenMessagingClientFactory messagingClientFactory()
    {
        return new HiveMqtt5MessagingClientFactory();
    }
    
    @Bean
    ModbusGatewayRegistry modbusGatewayRegistry()
    {
        return new SGrModbusGatewayRegistry();
    }
    
    @Bean
    GitHubLoader gitHubLoader()
    {
        return new GitHubLoader();
    }
}
