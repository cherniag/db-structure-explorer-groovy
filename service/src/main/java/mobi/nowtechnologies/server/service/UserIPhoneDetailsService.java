package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.UserIPhoneDetailsDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.persistence.repository.UserIPhoneDetailsRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class UserIPhoneDetailsService extends UserDeviceDetailsService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserIPhoneDetailsService.class);

	private UserIPhoneDetailsDao userIPhoneDetailsDao;
	private UserIPhoneDetailsRepository userIPhoneDetailsRepository;
	
	public void setUserIPhoneDetailsDao(UserIPhoneDetailsDao userIPhoneDetailsDao) {
		this.userIPhoneDetailsDao = userIPhoneDetailsDao;
	}
	
	public void setUserIPhoneDetailsRepository(UserIPhoneDetailsRepository userIPhoneDetailsRepository) {
		this.userIPhoneDetailsRepository = userIPhoneDetailsRepository;
	}

	public UserDeviceDetails getUserDeviceDetails(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		UserDeviceDetails userDeviceDetails = userIPhoneDetailsDao.getUserDeviceDetails(userId);
		
		LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}

}
