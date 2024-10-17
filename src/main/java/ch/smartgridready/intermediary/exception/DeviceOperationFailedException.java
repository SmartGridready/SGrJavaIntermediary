package ch.smartgridready.intermediary.exception;

public class DeviceOperationFailedException extends RuntimeException {

    public DeviceOperationFailedException(String deviceName) {
        super("Read/write failed for device '" + deviceName + "'.");
    }

    public DeviceOperationFailedException(String deviceName, Throwable cause) {
        super("Read/write failed for device '" + deviceName + "'. Error: " + cause.getMessage());
    }

}
