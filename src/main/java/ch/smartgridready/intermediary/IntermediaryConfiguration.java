package ch.smartgridready.intermediary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smartgridready.communicator.messaging.client.HiveMqtt5MessagingClientFactory;
import com.smartgridready.communicator.modbus.api.ModbusGatewayFactory;
import com.smartgridready.communicator.modbus.impl.EasyModbusGatewayFactory;
import com.smartgridready.communicator.rest.http.client.ApacheHttpRequestFactory;
import com.smartgridready.driver.api.http.GenHttpRequestFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;

@SuppressWarnings("unused")
@Configuration
public class IntermediaryConfiguration {
    
    @Bean
    ModbusGatewayFactory modbusGatewayFactory() {
        return new EasyModbusGatewayFactory();
    }

    @Bean
    GenHttpRequestFactory httpRequestFactory() {
        return new ApacheHttpRequestFactory();
    }

    @Bean
    GenMessagingClientFactory messagingClientFactory() {
        return new HiveMqtt5MessagingClientFactory();
    }
}
