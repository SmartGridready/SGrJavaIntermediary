package ch.smartgridready.intermediary.exception;

public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String deviceName)  {
        super("Could not find device '" + deviceName + "'");
    }

}
