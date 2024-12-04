package com.smartgridready.intermediary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartgridready.communicator.modbus.api.ModbusGateway;
import com.smartgridready.communicator.modbus.api.ModbusGatewayRegistry;
import com.smartgridready.driver.api.common.GenDriverException;
import com.smartgridready.driver.api.http.GenHttpClientFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;
import com.smartgridready.intermediary.entity.Device;
import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.exception.DeviceOperationFailedException;
import com.smartgridready.intermediary.repository.ConfigurationValueRepository;
import com.smartgridready.intermediary.repository.DeviceRepository;
import com.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;

/**
 * Test for IntermediaryService.
 */
@ExtendWith(value = MockitoExtension.class)
class IntermediaryServiceTest
{
    private static final String EI_XML_NAME = "WAGOMeterV0.2.1";
    private static final String EI_XML_FILE_NAME = "SGr_04_0014_0000_WAGO_SmartMeterV0.2.1-Arrays.xml";
    
    @Mock
    DeviceRepository deviceRepository;
    @Mock
    ExternalInterfaceXmlRepository eiXmlRepository;
    @Mock
    ConfigurationValueRepository configurationValueRepository;
    @Mock
    GenMessagingClientFactory messagingClientFactory;
    @Mock
    GenHttpClientFactory httpRequestFactory;

    @Mock
    ModbusGatewayRegistry modbusGatewayRegistry;
    @Mock
    ModbusGateway modbusGateway;
    
    IntermediaryService testee;

    @BeforeEach
    void createTestee()
    {
        testee = new IntermediaryServiceMock( deviceRepository,
                                              eiXmlRepository,
                                              configurationValueRepository,
                                              messagingClientFactory,
                                              httpRequestFactory );
    }
    
    private class IntermediaryServiceMock extends IntermediaryService
    {
        public IntermediaryServiceMock( DeviceRepository deviceRepository,
                                        ExternalInterfaceXmlRepository eiXmlRepository,
                                        ConfigurationValueRepository configurationValueRepository,
                                        GenMessagingClientFactory messagingClientFactory,
                                        GenHttpClientFactory httpRequestFactory )
        {
            super( deviceRepository,
                   eiXmlRepository,
                   configurationValueRepository,
                   messagingClientFactory,
                   httpRequestFactory );
        }

        @Override
        protected ModbusGatewayRegistry createModbusGatewayRegistry()
        {
            try
            {
                when( modbusGatewayRegistry.attachGateway( any(), any(), any() )).thenReturn( modbusGateway );
            }
            catch ( GenDriverException e )
            {
                throw new DeviceOperationFailedException( "UNIT_TEST", e );
            }
            
            return modbusGatewayRegistry;
        }
        
        @Override
        protected String loadExternalInterfaceFromGitHub( String fileName ) throws IOException,
                                                                            InterruptedException
        {
            return readEiXml();
        }
        
    }
    
    /**
     * Reads EI-XML from resource.
     * 
     * @return EI-XML contents
     * @throws IOException
     *         when read fails
     */
    private String readEiXml() throws IOException
    {

        try
        {
            var resource = getClass().getClassLoader().getResource( EI_XML_FILE_NAME );
            var file = new File( resource.toURI() );
            return Files.readString( file.toPath() );
        }
        catch ( URISyntaxException e )
        {
            throw new IOException( e );
        }

    }
    
    @Test
    void testSaveEiXmlWithNewEiXml() throws Exception
    {
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        // test new EI-XML
//        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );
        when( eiXmlRepository.save( eiXml )).thenReturn( eiXml );
        
        final var device = createDevice( EI_XML_NAME, eiXml );
        final var deviceList = new ArrayList<Device>();
        deviceList.add( device );
        when( deviceRepository.findByEiXmlName( EI_XML_NAME ) ).thenReturn( deviceList );
        
        // call testee
        final var newEiXml = testee.saveEiXml( EI_XML_NAME );
        
        // check
        assertEquals( eiXml, newEiXml);
        assertEquals( "OK", testee.getDeviceStatus( EI_XML_NAME ) );
    }
    
    @Test
    void testSaveEiXmlWithExistingEiXml() throws Exception
    {
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        // test existing EI-XML
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );
        when( eiXmlRepository.save( eiXml )).thenReturn( eiXml );
        
        final var device = createDevice( EI_XML_NAME, eiXml );
        final var deviceList = new ArrayList<Device>();
        deviceList.add( device );
        when( deviceRepository.findByEiXmlName( EI_XML_NAME ) ).thenReturn( deviceList );
        
        // call testee
        var newEiXml = testee.saveEiXml( EI_XML_NAME );
        
        // check
        assertEquals( eiXml, newEiXml);
        assertEquals( "OK", testee.getDeviceStatus( EI_XML_NAME ) );
        
        // call again to test disconnect and re-connect
        newEiXml = testee.saveEiXml( EI_XML_NAME );
        
        // check
        assertEquals( eiXml, newEiXml);
        assertEquals( "OK", testee.getDeviceStatus( EI_XML_NAME ) );
        
    }
    
    private Device createDevice( String name, ExternalInterfaceXml eiXml )
    {
        final var device = new Device();
        device.setId( 1 );
        device.setName( name );
        device.setEiXml( eiXml );
        return device;
    }

    @Test
    void testGetEiXml()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testGetAllEiXml()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testDeleteEiXml()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testLoadDevices()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testInsertOrUpdateDevice()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testGetDevice()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testGetAllDevices()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testDeleteDevice()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testGetVal()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testSetVal()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testGetDeviceStatus()
    {
        fail( "Not yet implemented" );
    }

    @Test
    void testBeforeTermination()
    {
        fail( "Not yet implemented" );
    }

}
