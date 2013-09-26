package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceSet;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import java.util.Map;

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

	public static Map<String, Object> setDevice(int userId, String deviceType, String deviceUID) {
		if (null == deviceType)
			throw new ServiceException("The parameter deviceType is null");
		if (null == deviceUID)
			throw new ServiceException("The parameter deviceUID is null");
		return DeviceTypeDao.setDevice(userId, deviceType, deviceUID);
	}

}
