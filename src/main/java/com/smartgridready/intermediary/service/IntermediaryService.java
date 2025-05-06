/*
 * Copyright(c) 2024 Verein SmartGridready Switzerland
 *
 * This file is licensed under the terms in LICENSE.md at the root of this project.
 */
package com.smartgridready.intermediary.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smartgridready.communicator.common.api.GenDeviceApi;
import com.smartgridready.communicator.common.api.SGrDeviceBuilder;
import com.smartgridready.communicator.common.api.values.Value;
import com.smartgridready.communicator.common.helper.DeviceDescriptionLoader;
import com.smartgridready.communicator.messaging.impl.SGrMessagingDevice;
import com.smartgridready.communicator.modbus.api.ModbusGatewayRegistry;
import com.smartgridready.communicator.rest.exception.RestApiAuthenticationException;
import com.smartgridready.communicator.rest.impl.SGrRestApiDevice;
import com.smartgridready.driver.api.common.GenDriverException;
import com.smartgridready.driver.api.http.GenHttpClientFactory;
import com.smartgridready.driver.api.messaging.GenMessagingClientFactory;
import com.smartgridready.driver.api.messaging.model.MessagingPlatformType;
import com.smartgridready.intermediary.dto.DeviceInfoDto;
import com.smartgridready.intermediary.dto.EidInfoDto;
import com.smartgridready.intermediary.entity.ConfigurationValue;
import com.smartgridready.intermediary.entity.Device;
import com.smartgridready.intermediary.entity.ExternalInterfaceXml;
import com.smartgridready.intermediary.exception.DeviceNotFoundException;
import com.smartgridready.intermediary.exception.DeviceOperationFailedException;
import com.smartgridready.intermediary.exception.ExtIfXmlNotFoundException;
import com.smartgridready.intermediary.helper.DtoConverter;
import com.smartgridready.intermediary.helper.EidHelper;
import com.smartgridready.intermediary.repository.ConfigurationValueRepository;
import com.smartgridready.intermediary.repository.DeviceRepository;
import com.smartgridready.intermediary.repository.ExternalInterfaceXmlRepository;

import io.vavr.control.Either;
import jakarta.annotation.PreDestroy;

/**
 * This class provides the services for the end points.
 */
@Service
public class IntermediaryService
{
    private static final Logger LOG = LoggerFactory.getLogger( IntermediaryService.class );

    private final DeviceRepository deviceRepository;
    private final ExternalInterfaceXmlRepository eiXmlRepository;
    private final ConfigurationValueRepository configurationValueRepository;
    private final GenMessagingClientFactory messagingClientFactory;
    private final GenHttpClientFactory httpRequestFactory;
    private final ModbusGatewayRegistry sharedModbusGatewayRegistry;
    private final UriLoader uriLoader;
    
    /**
     * device registry, key is device name
     */
    private final Map<String, GenDeviceApi> deviceRegistry = new HashMap<>();
    /**
     * device error registry, key is device name
     */
    private final Map<String, String> errorDeviceRegistry = new HashMap<>();

    /**
     * Constructor with all dependencies.
     */
    public IntermediaryService( DeviceRepository deviceRepository,
                                ExternalInterfaceXmlRepository eiXmlRepository,
                                ConfigurationValueRepository configurationValueRepository,
                                GenMessagingClientFactory messagingClientFactory,
                                GenHttpClientFactory httpRequestFactory,
                                ModbusGatewayRegistry modbusGatewayRegistry,
                                UriLoader uriLoader)
    {
        this.deviceRepository = deviceRepository;
        this.eiXmlRepository = eiXmlRepository;
        this.configurationValueRepository = configurationValueRepository;
        this.messagingClientFactory = messagingClientFactory;
        this.httpRequestFactory = httpRequestFactory;
        this.sharedModbusGatewayRegistry = modbusGatewayRegistry;
        this.uriLoader = uriLoader;
    }


    /**
     * Saves the given EI-XML by looking it up from any Web resource URI and saves it ib the local DB.
     * @param eiXmlName The name of the EI-XML (referenced by the device entities)
     * @param uri The URI of the EI-XML
     * @return {@code ExternalInterfaceXml}
     */
    public ExternalInterfaceXml loadEiXmlFromUri(String eiXmlName, String uri) {

        // load EI-XML file from GitHub
        final var eiXmlContents = uriLoader.loadExternalInterface( uri );

        // update or create EI-XML
        final var eiXmlList = eiXmlRepository.findByName( eiXmlName );
        final var eiXml = eiXmlList.stream()
                .findFirst()
                .orElseGet( () -> new ExternalInterfaceXml( eiXmlName, eiXmlContents ) );
        eiXml.setXml( eiXmlContents ); // overwrite XML
        final var newEiXml = eiXmlRepository.save( eiXml );

        // reload all devices that are based on this EI-XML file
        deviceRepository.findByEiXmlName( eiXmlName ).forEach( this::loadDevice );

        LOG.debug( "loadEiXmlFromUri() with ExternalInterfaceXml.name='{}' from uri='{}' done.", newEiXml.getName(), uri );
        return newEiXml;
    }

    /**
     * Loads the given EI-XML from a local device and saving it on the local DB.
     *
     * @param fileName
     *        The file name
     * @param fileInputStream
     *        The file as input stream
     * @return {@code ExternalInterfaceXml}
     */
    public ExternalInterfaceXml loadEiXmlFromLocalFile(String fileName, InputStream fileInputStream) {

        LOG.info( "starting loadEiXmlFromGitHub() with eiXmlFileName='{}'", fileName );

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
            final var xml = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            var eiXmlList = eiXmlRepository.findByName(fileName);
            var eiXml = eiXmlList.stream().findFirst().orElseGet(() -> new ExternalInterfaceXml(fileName, xml));
            eiXml.setXml(xml); // overwrite XML
            return eiXmlRepository.save(eiXml);
        } catch (IOException e) {
            LOG.error("Unable to read file: " + fileName, e);
            throw new ExtIfXmlNotFoundException(fileName);
        }
    }

    /**
     * Retrieves the EI-XML with the given name.
     * 
     * @param eiXmlName
     *        EI-XML name
     * @return {@code ExternalInterfaceXml}
     */
    public ExternalInterfaceXml getEiXml( String eiXmlName )
    {
        LOG.info( "starting getEiXml() with eiXmlFileName='{}'", eiXmlName );
        final var eiXml = eiXmlRepository.findByName( eiXmlName )
                .stream()
                .findFirst()
                .orElseThrow( () -> new ExtIfXmlNotFoundException( eiXmlName ) );
        LOG.debug( "finishing getEiXml() with ExternalInterfaceXml.name='{}'", eiXml.getName() );
        return eiXml;
    }

    /**
     * Retrieves all EI-XMLs.
     * 
     * @return list of EI-XML
     */
    public List<ExternalInterfaceXml> getAllEiXml()
    {
        LOG.info( "starting getAllEiXml()" );
        var eiXmls = eiXmlRepository.findAll();
        var eiXmlNames = eiXmls.stream().map(ExternalInterfaceXml::getName).toList();
        LOG.debug( "finishing getAllEiXml() with list of ExternalInterfaceXml.name {}", eiXmlNames );
        return eiXmls;
    }

    /**
     * Deletes the EI-XML with the given name.
     * 
     * @param eiXmlName
     *        EI-XML name
     */
    public void deleteEiXml( String eiXmlName )
    {
        LOG.info( "starting deleteEiXml() with eiXmlFileName='{}'", eiXmlName );
        var eiXml = eiXmlRepository.findByName( eiXmlName )
                .stream()
                .findFirst()
                .orElseThrow( () -> new ExtIfXmlNotFoundException( eiXmlName ) );
        eiXmlRepository.delete( eiXml );
        LOG.debug( "finishing deleteEiXml()" );
    }

    /**
     * Retrieves the EI-XML with the given name and its configuration information.
     * 
     * @param eiXmlName
     *        EI-XML name
     * @return {@code EidInfoDto}
     */
    public EidInfoDto getEiXmlInfo( String eiXmlName )
    {
        final var eiXml = getEiXml(eiXmlName);
        final var deviceFrame = new DeviceDescriptionLoader().load(eiXml.getXml());

        return DtoConverter.eidInfoDto(
            eiXmlName,
            EidHelper.getVersionString(deviceFrame.getDeviceInformation().getVersionNumber()),
            EidHelper.getConfigurationParameters(deviceFrame)
        );
    }

    /**
     * Loads all devices.
     */
    public void loadDevices()
    {
        LOG.info( "starting loadDevices()" );
        deviceRepository.findAll().forEach( this::loadDevice );
        LOG.debug( "finishing loadDevices()" );
    }

    private void loadDevice( Device device )
    {
        Properties properties = new Properties();
        device.getConfigurationValues()
                .forEach( configurationValue -> properties.put( configurationValue.getName(),
                                                                configurationValue.getVal() ) );

        try
        {
            disconnectDevice( device.getName(), false );

            var deviceInstance = new SGrDeviceBuilder()
                    .properties( properties )
                    .eid( device.getEiXml().getXml() )
                    .useMessagingClientFactory( messagingClientFactory, MessagingPlatformType.MQTT5 )
                    .useRestServiceClientFactory( httpRequestFactory )
                    .useSharedModbusGatewayRegistry( sharedModbusGatewayRegistry )
                    .useSharedModbusRtu( true )
                    .build();

            LOG.debug( "connecting new device named '{}'", device.getName() );
            deviceInstance.connect();

            deviceRegistry.put( device.getName(), deviceInstance );
            errorDeviceRegistry.remove( device.getName() );
            LOG.info( "Successfully loaded device named '{}' with EI-XML '{}'.",
                      device.getName(),
                      device.getEiXml().getName() );
        }
        catch ( GenDriverException | RestApiAuthenticationException e )
        {
            LOG.error( "Failed to load device named '{}' with EI-XML '{}'. Error: {}",
                       device.getName(),
                       device.getEiXml().getName(),
                       e.getMessage() );
            errorDeviceRegistry.put( device.getName(), e.getMessage() );
        }

    }

    private void disconnectDevice( String deviceName, boolean removeFromRegistry )
    {
        var device = deviceRegistry.get( deviceName );

        if ( device != null )
        {

            try
            {
                LOG.debug( "disconnecting old device named '{}'", deviceName );
                device.disconnect();
            }
            catch ( GenDriverException e )
            {
                LOG.error( "Failed to disconnect device named '{}'. Error: {}",
                           deviceName,
                           e.getMessage() );
                errorDeviceRegistry.put( deviceName, e.getMessage() );
            }

            if ( removeFromRegistry )
            {
                deviceRegistry.remove( deviceName );
                errorDeviceRegistry.remove( deviceName );
            }

        }

    }

    /**
     * Inserts or update the device with the given name.
     * 
     * @param deviceName
     *        name of the device
     * @param eiXmlName
     *        name of the associated EI-XML
     * @param configurationValues
     *        configuration values
     * @return device
     */
    public Device insertOrUpdateDevice( String deviceName,
                                        String eiXmlName,
                                        List<ConfigurationValue> configurationValues )
    {
        LOG.info( "starting insertOrUpdateDevice() with deviceName='{}', eiXmlName='{}', configurationValues={}",
                  deviceName,
                  eiXmlName,
                  configurationValues );
        var eiXml = eiXmlRepository.findByName( eiXmlName )
                .stream()
                .findFirst()
                .orElseThrow( () -> new ExtIfXmlNotFoundException( "EI-XML with name '" + eiXmlName
                                                                   + "' not found" ) );

        // look up existing device or create new one
        var device =
                deviceRepository.findByName( deviceName ).stream().findFirst().orElseGet( Device::new );
        device.setName( deviceName );
        device.setEiXml( eiXml );

        // 1st delete all old configuration values
        device.getConfigurationValues().clear();
        deviceRepository.save( device );

        // 2nd add all new configuration values
        configurationValues.forEach( configValue ->
            {
                configValue.setDevice( device );
                device.getConfigurationValues().add( configValue );
            } );

        deviceRepository.save( device );

        loadDevice( device );
        LOG.debug( "finishing insertOrUpdateDevice() with device {}", device );
        return device;
    }

    /**
     * Retrieves the device with the given name.
     * 
     * @param deviceName
     *        name of the device
     * @return device
     */
    public Device getDevice( String deviceName )
    {
        LOG.info( "starting findByName() with deviceName='{}'", deviceName );
        var device = deviceRepository.findByName( deviceName )
                .stream()
                .findFirst()
                .orElseThrow( () -> new DeviceNotFoundException( deviceName ) );
        LOG.debug( "finishing findByName() with device {}", device );
        return device;
    }

    /**
     * Retrieves all devices.
     * 
     * @return list of devices
     */
    public List<Device> getAllDevices()
    {
        LOG.info( "starting getAllDevices()" );
        var devices = deviceRepository.findAll();
        LOG.debug( "finishing getAllDevices() with devices {}", devices );
        return devices;
    }

    /**
     * Deletes the device with the given name.
     * 
     * @param deviceName
     *        name of the device
     */
    public void deleteDevice( String deviceName )
    {
        LOG.info( "starting deleteDevice() with deviceName='{}'", deviceName );
        var device = deviceRepository.findByName( deviceName )
                .stream()
                .findFirst()
                .orElseThrow( () -> new DeviceNotFoundException( deviceName ) );

        disconnectDevice( device.getName(), true );

        device.getConfigurationValues().forEach( configurationValueRepository::delete );
        deviceRepository.delete( device );

        LOG.debug( "finishing deleteDevice()" );
    }

    /**
     * Retrieves the value of the given data point of the given functional profile of the given device,
     * respecting the optional (REST API) parameters.
     * 
     * @param deviceName
     *        name of device
     * @param functionalProfileName
     *        name of functional profile
     * @param dataPointName
     *        name of the data point
     * @param parameters
     *        optional parameters, only supported by REST API devices
     * @return value
     */
    public Value getVal( String deviceName,
                         String functionalProfileName,
                         String dataPointName,
                         Properties parameters )
    {
        LOG.info( "starting getVal() with deviceName='{}', functionalProfileName='{}', dataPointName='{}', parameters={}",
                  deviceName,
                  functionalProfileName,
                  dataPointName,
                  parameters );
        final var device = findDeviceInRegistries( deviceName );

        try
        {

            if ( device instanceof SGrMessagingDevice messagingDevice )
            {
                messagingDevice
                        .subscribe( functionalProfileName, dataPointName, this::mqttCallbackFunction );
            }

            // Rest API devices support query parameters
            if ( device instanceof SGrRestApiDevice restApiDevice )
            {
                return restApiDevice.getVal( functionalProfileName, dataPointName, parameters );
            }

            var value = device.getVal( functionalProfileName, dataPointName );

            errorDeviceRegistry.remove( deviceName );
            LOG.debug( "finishing getVal() with value='{}'", value );
            return value;
        }
        catch ( Exception e )
        {
            errorDeviceRegistry.put( deviceName, e.getMessage() );
            throw new DeviceOperationFailedException( deviceName, e );
        }

    }

    private GenDeviceApi findDeviceInRegistries( String deviceName, boolean ignoreErrorState )
    {
        var deviceOpt = Optional.ofNullable( deviceRegistry.get( deviceName ) );

        if ( deviceOpt.isEmpty() )
        {
            var deviceError = Optional.ofNullable( errorDeviceRegistry.get( deviceName ) );

            if ( deviceError.isPresent() )
            {
                if ( !ignoreErrorState )
                {
                    throw new DeviceOperationFailedException( deviceError.get() );
                }
            }
            else
            {
                throw new DeviceNotFoundException( deviceName );
            }

        }

        return deviceOpt.get();
    }

    private GenDeviceApi findDeviceInRegistries( String deviceName )
    {
        return findDeviceInRegistries(deviceName, false);
    }

    private void mqttCallbackFunction( Either<Throwable, Value> message )
    {
        LOG.info( "Received value: {}", message );
    }

    /**
     * Sets the value of the given data point of the given functional profile of the given device.
     * 
     * @param deviceName
     *        name of device
     * @param functionalProfileName
     *        name of functional profile
     * @param dataPointName
     *        name of the data point
     * @param value
     *        new value
     */
    public void setVal( String deviceName,
                        String functionalProfileName,
                        String dataPointName,
                        Value value )
    {
        LOG.info( "starting setVal() with deviceName='{}', functionalProfileName='{}', dataPointName='{}', value='{}'",
                  deviceName,
                  functionalProfileName,
                  dataPointName,
                  value);
        final var device = findDeviceInRegistries( deviceName );

        try
        {
            device.setVal( functionalProfileName, dataPointName, value );
            errorDeviceRegistry.remove( deviceName );
            LOG.debug( "finishing setVal()" );
        }
        catch ( Exception e )
        {
            errorDeviceRegistry.put( deviceName, e.getMessage() );
            throw new DeviceOperationFailedException( deviceName, e );
        }

    }

    /**
     * Retrieves the device status of the given device.
     * 
     * @param deviceName
     *        name of the device
     * @return device status
     */
    public String getDeviceStatus( String deviceName )
    {
        LOG.info( "starting getDeviceStatus() with deviceName='{}'", deviceName );

        final var UNKNOWN = "UNKNOWN";
        var status = errorDeviceRegistry.entrySet()
                .stream()
                .filter( entry -> entry.getKey().equals( deviceName ) )
                .map( Map.Entry::getValue )
                .findFirst()
                .orElse( UNKNOWN );

        if ( UNKNOWN.equals( status ) )
        {
            status = deviceRegistry.entrySet()
                    .stream()
                    .filter( entry -> entry.getKey().equals( deviceName ) )
                    .map( entry -> "OK" )
                    .findFirst()
                    .orElse( UNKNOWN );
        }

        LOG.info( "finishing getDeviceStatus() with status='{}'", status );
        return status;
    }

    /**
     * Retrieves the device information of the given device.
     * 
     * @param deviceName
     *        name of the device
     * @return device information with functional profiles and data points
     */
    public DeviceInfoDto getDeviceInfo( String deviceName )
    {
        final var device = findDeviceInRegistries( deviceName, true );

        try
        {
            var info = device.getDeviceInfo();
            var functionalProfiles = device.getFunctionalProfiles();

            return DtoConverter.deviceInfoDto(info, functionalProfiles);
        }
        catch ( Exception e )
        {
            throw new DeviceOperationFailedException( deviceName, e );
        }
    }

    @PreDestroy
    void beforeTermination()
    {
        deviceRegistry.forEach( ( deviceName, deviceApi ) ->
            {

                try
                {
                    deviceApi.disconnect();
                    LOG.info( "Successfully closed device: {}", deviceName );
                }
                catch ( Exception e )
                {
                    LOG.info( "Failed to disconnect device: {}, {}", deviceName, e.getMessage() );
                }

            } );
    }
}
