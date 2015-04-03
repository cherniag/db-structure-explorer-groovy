package mobi.nowtechnologies.server.device.domain;

public class DeviceTypeFactory {

    public static DeviceType createDeviceType(String name) {
        final DeviceType deviceType = new DeviceType();
        deviceType.setName(name);
        return deviceType;
    }
}