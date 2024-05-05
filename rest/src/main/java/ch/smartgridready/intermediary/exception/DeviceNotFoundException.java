package ch.smartgridready.intermediary;

public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String deviceName)  {
        super("Could not find device '" + deviceName + "'");
    }

}
