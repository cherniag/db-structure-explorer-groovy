package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.UserAndroidDetailsDao;
import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class UserAndroidDetailsService extends UserDeviceDetailsService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAndroidDetailsService.class);
	
	private UserAndroidDetailsDao userAndroidDetailsDao;
	
	public void setUserAndroidDetailsDao(UserAndroidDetailsDao userAndroidDetailsDao) {
		this.userAndroidDetailsDao = userAndroidDetailsDao;
	}
	
	public UserDeviceDetails getUserDeviceDetails(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		UserDeviceDetails userDeviceDetails = userAndroidDetailsDao.getUserDeviceDetails(userId);
		
		LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}

}
