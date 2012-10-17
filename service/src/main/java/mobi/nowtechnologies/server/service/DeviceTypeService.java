package mobi.nowtechnologies.server.service;

import java.util.Map;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceSet;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class DeviceTypeService {
	public byte findIdByName(String name) {
		if (name == null)
			throw new ServiceException("The parammeter name is null");
		return DeviceTypeDao.findIdByName(name);
	}

	public Map<Byte, String> getDeviceTypeMapWhitIdAsKey() {
		return DeviceTypeDao.getDeviceTypeMapWhitIdAsKey();
	}

	public Map<String, String> getDeviceTypeMapWhitNameAsKey() {
		return DeviceTypeDao.getDeviceTypeMapWhitNameAsKey();
	}

	public static DeviceSet setDevice(int userId, String deviceType, String deviceUID) {
		if (null == deviceType)
			throw new ServiceException("The parameter deviceType is null");
		if (null == deviceUID)
			throw new ServiceException("The parameter deviceUID is null");
		return DeviceTypeDao.setDevice(userId, deviceType, deviceUID);
	}
	
	public static DeviceType getIOSDeviceType() {
		return DeviceTypeDao.getIOSDeviceType();
	}
	
	public static DeviceType getNoneDeviceType() {
		return DeviceTypeDao.getNoneDeviceType();

	}

}
