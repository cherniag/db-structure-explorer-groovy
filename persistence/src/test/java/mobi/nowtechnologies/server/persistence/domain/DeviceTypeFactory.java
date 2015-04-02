package mobi.nowtechnologies.server.persistence.domain;

public class DeviceTypeFactory {
	
	public static DeviceType createDeviceType(String name) {
		final DeviceType deviceType = new DeviceType();
		deviceType.setName(name);
		return deviceType;
	}
}