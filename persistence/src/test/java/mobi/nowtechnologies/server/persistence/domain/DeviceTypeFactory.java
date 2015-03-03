package mobi.nowtechnologies.server.persistence.domain;


public class DeviceTypeFactory {

    private DeviceTypeFactory() {
    }


    public static DeviceType createDeviceType() {
        final DeviceType deviceType = new DeviceType();
        return deviceType;
    }

    public static DeviceType createDeviceType(String name) {
        final DeviceType deviceType = new DeviceType();
        deviceType.setName(name);
        return deviceType;
    }
}