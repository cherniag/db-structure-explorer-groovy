package mobi.nowtechnologies.server.service;

import java.util.List;

import mobi.nowtechnologies.server.persistence.dao.UserIPhoneDetailsDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class UserIPhoneDetailsService extends UserDeviceDetailsService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserIPhoneDetailsService.class);

	private UserIPhoneDetailsDao userIPhoneDetailsDao;
	
	public void setUserIPhoneDetailsDao(UserIPhoneDetailsDao userIPhoneDetailsDao) {
		this.userIPhoneDetailsDao = userIPhoneDetailsDao;
	}
	
	public List<UserIPhoneDetails> getUserIPhoneDetailsListForPushNotification(Community community) {
		if (community == null)
			throw new ServiceException("The parameter community is null");
		LOGGER.debug("input parameters community: [{}]", community);
		List<UserIPhoneDetails> userIPhoneDetailsList = userIPhoneDetailsDao.getUserIPhoneDetailsListForPushNotification(community);
//		for (UserIPhoneDetails userIPhoneDetails : userIPhoneDetailsList) {
//			userIPhoneDetails.setStatus(2);
//			entityService.updateEntity(userIPhoneDetails);
//		}
		LOGGER.debug("Output parameter userIPhoneDetailsList=[{}]", userIPhoneDetailsList);
		return userIPhoneDetailsList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public UserIPhoneDetails markUserIPhoneDetailsAsProcessed(UserIPhoneDetails userIPhoneDetails) {
		if (userIPhoneDetails == null)
			throw new NullPointerException("The parameter userIPhoneDetails is null");
		LOGGER.debug("input parameters userIPhoneDetails: [{}]", new Object[] { userIPhoneDetails });
		userIPhoneDetails.setStatus(0);
		UserIPhoneDetails resultUserIPhoneDetails = entityService.updateEntity(userIPhoneDetails);
		LOGGER.debug("Output parameter resultUserIPhoneDetails=[{}]", resultUserIPhoneDetails);
		return resultUserIPhoneDetails;
	}
	
	public int updateUserIPhoneDetailsForPushNotification(Community community) {
		if (community == null)
			throw new ServiceException("The parameter community is null");
		LOGGER.debug("input parameters community: [{}]", community);
		int updatedRowsCount = userIPhoneDetailsDao.updateUserIPhoneDetailsForPushNotification(community);
		LOGGER.debug("Output parameter updatedRowsCount=[{}]", updatedRowsCount);
		return updatedRowsCount;
	}

	public UserDeviceDetails getUserDeviceDetails(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		UserDeviceDetails userDeviceDetails = userIPhoneDetailsDao.getUserDeviceDetails(userId);
		
		LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}

}
