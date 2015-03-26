package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import java.util.Map;

/**
 * @author Titov Mykhaylo (titov)
 */
public class DeviceTypeService {

    public static Map<String, Object> setDevice(int userId, String deviceType, String deviceUID) {
        if (null == deviceType) {
            throw new ServiceException("The parameter deviceType is null");
        }
        if (null == deviceUID) {
            throw new ServiceException("The parameter deviceUID is null");
        }
        return DeviceTypeDao.setDevice(userId, deviceType, deviceUID);
    }

}
