package com.smartgridready.intermediary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartgridready.driver.apachehttp.ApacheHttpClientFactory;
import com.smartgridready.driver.api.http.GenHttpClientFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;
import com.smartgridready.driver.hivemq.HiveMqtt5MessagingClientFactory;


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
}
