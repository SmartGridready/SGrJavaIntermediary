/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.smartgridready.communicator.common.api.values.Float64Value;
import com.smartgridready.communicator.modbus.api.ModbusGateway;
import com.smartgridready.communicator.modbus.api.ModbusGatewayRegistry;
import com.smartgridready.driver.api.common.GenDriverException;
import com.smartgridready.driver.api.http.GenHttpClientFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;
import com.smartgridready.driver.api.modbus.GenDriverAPI4Modbus;
import com.smartgridready.intermediary.entity.ConfigurationValue;
import com.smartgridready.intermediary.entity.Device;
import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.exception.DeviceNotFoundException;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
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
    private static final String DEVICE_NAME = "WAGOMeter";
    private static final String FUNCTIONAL_PROFILE_NAME = "VoltageAC";
    private static final String DATA_POINT_NAME = "Voltage-L1-L2-L3";
    
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private ExternalInterfaceXmlRepository eiXmlRepository;
    @Mock
    private ConfigurationValueRepository configurationValueRepository;
    @Mock
    private GenMessagingClientFactory messagingClientFactory;
    @Mock
    private GenHttpClientFactory httpRequestFactory;
    @Mock
    private ModbusGatewayRegistry modbusGatewayRegistry;
    @Mock
    private ModbusGateway modbusGateway;
    @Mock
    private GenDriverAPI4Modbus genDriverAPI4Modbus;
    @Mock 
    private GitHubLoader gitHubLoader;
    
    private boolean loadFromGitHubShouldFail;
    
    private IntermediaryService testee;

    @BeforeEach
    void createTestee() throws Exception
    {
        Mockito.lenient()
                /*
                 * The register values here are read from DATA_POINT_NAME of FUNCTIONAL_PROFILE_NAME of
                 * DEVICE_NAME in EI_XML_FILE_NAME in the resources. The return value is not checked, but
                 * must be long enough that the tests work.
                 */
                .when( genDriverAPI4Modbus.ReadHoldingRegisters( 20482, 6 ) )
                .thenReturn( new int[] { 1, 2, 3, 4, 5, 6 } );

        Mockito.lenient()
                .when( modbusGateway.getTransport() )
                .thenReturn( genDriverAPI4Modbus );
        
        Mockito.lenient()
                .when( modbusGatewayRegistry.attachGateway( any(), any(), any() ) )
                .thenReturn( modbusGateway );

        Mockito.lenient()
                .when( gitHubLoader.loadExternalInterface( EI_XML_NAME ) )
                .thenAnswer( new Answer<String>()
                {
                    public String answer( InvocationOnMock invocation )
                    {
                        /*
                         * Do not access GitHub, but return the test file in the resources.
                         */
                        if ( loadFromGitHubShouldFail )
                        {
                            throw new ExtIfXmlNotFoundException( "UNIT_TEST" );
                        }
                        
                        return readEiXml();
                    }
                } );
        
        testee = new IntermediaryService( deviceRepository,
                                          eiXmlRepository,
                                          configurationValueRepository,
                                          messagingClientFactory,
                                          httpRequestFactory,
                                          modbusGatewayRegistry,
                                          gitHubLoader );
    }
    
    /**
     * Reads EI-XML from resource.
     * 
     * @return EI-XML contents
     */
    private String readEiXml()
    {

        try
        {
            var resource = getClass().getClassLoader().getResource( EI_XML_FILE_NAME );
            var file = new File( resource.toURI() );
            return Files.readString( file.toPath() );
        }
        catch ( Exception e )
        {
            throw new ExtIfXmlNotFoundException( e );
        }

    }
    
    @Test
    void testSaveEiXmlWithNewEiXmlSuccess() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        when( eiXmlRepository.findByName( EI_XML_NAME ) )
                .thenReturn( new ArrayList<ExternalInterfaceXml>() );
        when( eiXmlRepository.save( eiXml )).thenReturn( eiXml );
        final var device = createDevice( DEVICE_NAME, eiXml );
        final var deviceList = new ArrayList<Device>();
        deviceList.add( device );
        when( deviceRepository.findByEiXmlName( EI_XML_NAME ) ).thenReturn( deviceList );
        
        // call testee
        final var newEiXml = testee.saveEiXml( EI_XML_NAME );
        
        // check
        assertEquals( eiXml, newEiXml );
        assertEquals( "OK", testee.getDeviceStatus( DEVICE_NAME ) );
    }
    
    @Test
    void testSaveEiXmlWithExistingEiXmlSuccess() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );
        when( eiXmlRepository.save( eiXml )).thenReturn( eiXml );
        final var device = createDevice( DEVICE_NAME, eiXml );
        final var deviceList = new ArrayList<Device>();
        deviceList.add( device );
        when( deviceRepository.findByEiXmlName( EI_XML_NAME ) ).thenReturn( deviceList );
        
        // call testee
        var newEiXml = testee.saveEiXml( EI_XML_NAME );
        
        // check
        assertEquals( eiXml, newEiXml);
        assertEquals( "OK", testee.getDeviceStatus( DEVICE_NAME ) );
        
        // call again to test disconnect and re-connect
        newEiXml = testee.saveEiXml( EI_XML_NAME );
        
        // check
        assertEquals( eiXml, newEiXml );
        assertEquals( "OK", testee.getDeviceStatus( DEVICE_NAME ) );
    }

    @Test
    void testSaveEiXmlWithNewEiXmlNotFound() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        Mockito.lenient()
                .when( eiXmlRepository.findByName( EI_XML_NAME ) )
                .thenReturn( new ArrayList<ExternalInterfaceXml>() );
        Mockito.lenient().when( eiXmlRepository.save( eiXml ) ).thenReturn( eiXml );
        final var device = createDevice( DEVICE_NAME, eiXml );
        final var deviceList = new ArrayList<Device>();
        deviceList.add( device );
        Mockito.lenient()
                .when( deviceRepository.findByEiXmlName( EI_XML_NAME ) )
                .thenReturn( deviceList );

        // call testee and check
        try
        {
            loadFromGitHubShouldFail = true;
            assertThrows( ExtIfXmlNotFoundException.class, () -> testee.saveEiXml( EI_XML_NAME ) );
        }
        finally
        {
            loadFromGitHubShouldFail = false;
        }

    }
    
    /**
     * Creates a device with the given device name and EI-XML name.
     * 
     * @param name
     *        device name
     * @param eiXml
     *        EI-XML name
     * @return device
     */
    private Device createDevice( String name, ExternalInterfaceXml eiXml )
    {
        final var device = new Device();
        device.setId( 1 );
        device.setName( name );
        device.setEiXml( eiXml );
        return device;
    }

    @Test
    void testGetEiXmlSuccess() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );
        
        // call testee
        var retrievedEiXml = testee.getEiXml( EI_XML_NAME );
        
        // check
        assertEquals( eiXml, retrievedEiXml );
    }

    @Test
    void testGetEiXmlNotFound()
    {
        // set up mocks
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( new ArrayList<ExternalInterfaceXml>() );

        // call and check
        assertThrows( ExtIfXmlNotFoundException.class, () -> testee.getEiXml( EI_XML_NAME ) );
    }

    @Test
    void testGetAllEiXmlSuccess() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findAll() ).thenReturn( eiXmlList );

        // call testee
        var retrievedEiXmlList = testee.getAllEiXml();
        
        // check
        assertEquals( 1, retrievedEiXmlList.size() );
        assertEquals( eiXml, retrievedEiXmlList.get( 0 ) );
    }

    @Test
    void testGetAllEiXmlNoEntries()
    {
        // set up mocks
        when( eiXmlRepository.findAll() ).thenReturn( new ArrayList<ExternalInterfaceXml>() );

        // call testee
        var retrievedEiXmlList = testee.getAllEiXml();
        
        // check
        assertEquals( 0, retrievedEiXmlList.size() );
    }

    @Test
    void testDeleteEiXmlSuccess() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );

        // call testee
        testee.deleteEiXml( EI_XML_NAME );
    }

    @Test
    void testDeleteEiXmlNotFound() throws Exception
    {
        // set up mocks
        when( eiXmlRepository.findByName( EI_XML_NAME ) )
                .thenReturn( new ArrayList<ExternalInterfaceXml>() );

        // call testee
        assertThrows( ExtIfXmlNotFoundException.class, () -> testee.deleteEiXml( EI_XML_NAME ) );
    }

    @Test
    void testLoadDevicesWithNoDeviceSuccess()
    {
        // set up mocks
        when( deviceRepository.findAll() ).thenReturn( new ArrayList<Device>() );
        
        // call testee
        testee.loadDevices();
        
        // check
        assertEquals( "UNKNOWN", testee.getDeviceStatus( DEVICE_NAME ) );
    }

    @Test
    void testLoadDevicesWithDeviceSuccess() throws Exception
    {
        // set up mocks
        final var deviceList = new ArrayList<Device>();
        deviceList.add( createDevice( DEVICE_NAME, new ExternalInterfaceXml( EI_XML_FILE_NAME, readEiXml() ) ) );
        when( deviceRepository.findAll() ).thenReturn( deviceList );
        
        // call testee
        testee.loadDevices();
        
        // check
        assertEquals( "OK", testee.getDeviceStatus( DEVICE_NAME ) );
    }
    
    @Test
    void testLoadDevicesWithDeviceFailure() throws Exception
    {
        // set up mocks
        final var deviceList = new ArrayList<Device>();
        deviceList.add( createDevice( DEVICE_NAME, new ExternalInterfaceXml( EI_XML_FILE_NAME, readEiXml() ) ) );
        when( deviceRepository.findAll() ).thenReturn( deviceList );
        final var UNIT_TEST_EXCEPTION_TEXT = "UNIT_TEST_EXCEPTION";
        doThrow( new GenDriverException( UNIT_TEST_EXCEPTION_TEXT ) ).when( modbusGateway ).connect( any() );
        
        // call testee
        testee.loadDevices();
        
        // check
        assertEquals( UNIT_TEST_EXCEPTION_TEXT, testee.getDeviceStatus( DEVICE_NAME ) );
    }
    
    @Test
    void testInsertOrUpdateDeviceInsertSuccess() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );
        // empty list => insert
        when( deviceRepository.findByName( DEVICE_NAME ) ).thenReturn( new ArrayList<Device>() );
        
        // call testee
        var addedDevice = testee.insertOrUpdateDevice( DEVICE_NAME, EI_XML_NAME, new ArrayList<ConfigurationValue>() );
        
        // check
        assertEquals( DEVICE_NAME, addedDevice.getName() );
        assertEquals( EI_XML_NAME, addedDevice.getEiXml().getName() );
        assertEquals( "OK", testee.getDeviceStatus( DEVICE_NAME ) );
    }

    @Test
    void testInsertOrUpdateDeviceUpdateSuccess() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );
        // non-empty list => update
        final var deviceList = new ArrayList<Device>();
        final var device = createDevice( DEVICE_NAME, new ExternalInterfaceXml( EI_XML_FILE_NAME, readEiXml() ) );
        deviceList.add( device );
        when( deviceRepository.findByName( DEVICE_NAME ) ).thenReturn( deviceList );
        
        // call testee
        var addedDevice = testee.insertOrUpdateDevice( DEVICE_NAME, EI_XML_NAME, new ArrayList<ConfigurationValue>() );
        
        // check
        assertEquals( device, addedDevice );
        assertEquals( "OK", testee.getDeviceStatus( DEVICE_NAME ) );
    }

    @Test
    void testInsertOrUpdateDeviceNoEiXml()
    {
        // call testee and check
        assertThrows( ExtIfXmlNotFoundException.class,
                      () -> testee.insertOrUpdateDevice( DEVICE_NAME,
                                                         EI_XML_NAME,
                                                         new ArrayList<ConfigurationValue>() ) );
    }
    
    @Test
    void testGetDeviceSuccess() throws Exception
    {
        // set up mocks
        final var deviceList = new ArrayList<Device>();
        var device = createDevice( DEVICE_NAME, new ExternalInterfaceXml( EI_XML_FILE_NAME, readEiXml() ) );
        deviceList.add( device );
        when( deviceRepository.findByName( DEVICE_NAME ) ).thenReturn( deviceList );

        // call testee
        var retrievedDevice = testee.getDevice( DEVICE_NAME );
        
        // check
        assertEquals( device, retrievedDevice );
    }

    @Test
    void testGetDeviceNotFound() throws Exception
    {
        // set up mocks
        when( deviceRepository.findByName( DEVICE_NAME ) ).thenReturn( new ArrayList<Device>() );

        // call testee and check
        assertThrows( DeviceNotFoundException.class, () -> testee.getDevice( DEVICE_NAME ) );
    }

    @Test
    void testGetAllDevicesSuccess() throws Exception
    {
        // set up mocks
        final var deviceList = new ArrayList<Device>();
        var device = createDevice( DEVICE_NAME, new ExternalInterfaceXml( EI_XML_FILE_NAME, readEiXml() ) );
        deviceList.add( device );
        when( deviceRepository.findAll() ).thenReturn( deviceList );
        
        // call testee
        var retrievedDeviceList = testee.getAllDevices();
        
        // check
        assertEquals( 1, retrievedDeviceList.size() );
        assertEquals( deviceList.get( 0 ), retrievedDeviceList.get( 0 ) );
    }

    @Test
    void testGetAllDevicesNoDeviceSuccess()
    {
        // set up mocks
        when( deviceRepository.findAll() ).thenReturn( new ArrayList<Device>() );
        
        // call testee
        var retrievedDeviceList = testee.getAllDevices();
        
        // check
        assertEquals( 0, retrievedDeviceList.size() );
    }

    @SuppressWarnings("unchecked")  // added for warning on thenReturn() with a list of lists
    @Test
    void testDeleteDeviceSuccess() throws Exception
    {
        addDeviceToRegistry();
        
        // set up mocks
        final var deviceList = new ArrayList<Device>();
        var device = createDevice( DEVICE_NAME, new ExternalInterfaceXml( EI_XML_FILE_NAME, readEiXml() ) );
        deviceList.add( device );
        when( deviceRepository.findByName( DEVICE_NAME ) ).thenReturn( deviceList, new ArrayList<Device>() );
        
        // call testee
        testee.deleteDevice( DEVICE_NAME );
        
        // check
        assertThrows( DeviceNotFoundException.class, () -> testee.getDevice( DEVICE_NAME ) );
    }

    @Test
    void testDeleteDeviceNotFound() throws Exception
    {
        // set up mocks
        when( deviceRepository.findByName( DEVICE_NAME ) ).thenReturn( new ArrayList<Device>() );

        // call testee and check
        assertThrows( DeviceNotFoundException.class, () -> testee.deleteDevice( DEVICE_NAME ) );
    }

    /*
     * Remark:
     * The tests for getVal() and setVal() aren't very exhausting, as most code is mocked.
     * But as they just call through to the device, this is probably good enough here.
     */
    
    @Test
    void testGetValSuccess() throws Exception
    {
        addDeviceToRegistry();
        
        // call testee
        final var val = testee.getVal( DEVICE_NAME, FUNCTIONAL_PROFILE_NAME, DATA_POINT_NAME, new Properties() );
        
        // check
        assertNotNull( val );
    }
    
    @Test
    void testGetValNoDevice() throws Exception
    {
        // call testee and check
        assertThrows( DeviceNotFoundException.class,
                      () -> testee.getVal( DEVICE_NAME,
                                           FUNCTIONAL_PROFILE_NAME,
                                           DATA_POINT_NAME,
                                           new Properties() ) );
    }
    
    @Test
    void testSetValSuccess() throws Exception
    {
        addDeviceToRegistry();
        
        // call testee
        testee.setVal( DEVICE_NAME, FUNCTIONAL_PROFILE_NAME, DATA_POINT_NAME, Float64Value.of( 1.1 ) );
        
        // check
        final var val = testee.getVal( DEVICE_NAME, FUNCTIONAL_PROFILE_NAME, DATA_POINT_NAME, new Properties() );
        assertNotNull( val );
    }

    @Test
    void testSetValNoDevice() throws Exception
    {
        // call testee and check
        assertThrows( DeviceNotFoundException.class,
                      () -> testee.setVal( DEVICE_NAME,
                                           FUNCTIONAL_PROFILE_NAME,
                                           DATA_POINT_NAME,
                                           Float64Value.of( 1.1 ) ) );
    }

    /**
     * Adds the device {@code DEVICE_NAME} to the internal registry.
     * 
     * @return added {@code Device}
     * @throws Exception
     *         if add fails
     */
    private Device addDeviceToRegistry() throws Exception
    {
        // set up mocks
        final var eiXml = new ExternalInterfaceXml( EI_XML_NAME, readEiXml() );
        final var eiXmlList = new ArrayList<ExternalInterfaceXml>();
        eiXmlList.add( eiXml );
        when( eiXmlRepository.findByName( EI_XML_NAME ) ).thenReturn( eiXmlList );
        when( deviceRepository.findByName( DEVICE_NAME ) ).thenReturn( new ArrayList<Device>() );
        // call testee
        return testee
                .insertOrUpdateDevice( DEVICE_NAME, EI_XML_NAME, new ArrayList<ConfigurationValue>() );
    }

    /*
     * Remark: getDeviceStatus() is tested implicitly in other tests.
     */
    
    @Test
    void testBeforeTerminationSuccess() throws Exception
    {
        addDeviceToRegistry();
        
        // call testee
        testee.beforeTermination();
        
        // no check possible
    }

}
