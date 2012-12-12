package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserAndroidDetails;
import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserDeviceDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserDeviceDetailsService.class);

	protected EntityService entityService;

	private UserIPhoneDetailsService userIPhoneDetailsService;
	private UserAndroidDetailsService userAndroidDetailsService;

	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	public void setUserIPhoneDetailsService(UserIPhoneDetailsService userIPhoneDetailsService) {
		this.userIPhoneDetailsService = userIPhoneDetailsService;
	}

	public void setUserAndroidDetailsService(UserAndroidDetailsService userAndroidDetailsService) {
		this.userAndroidDetailsService = userAndroidDetailsService;
	}

	private UserDeviceDetails getUserDeviceDetailsNewInstance(String deviceType) {
		if (deviceType == null)
			throw new ServiceException("The parameter deviceType is null");
		LOGGER.debug("input parameters deviceType: [{}]", deviceType);

		UserDeviceDetails userDeviceDetails;
		if (deviceType.equals(UserRegInfo.DeviceType.IOS)) {
			userDeviceDetails = new UserIPhoneDetails();
		} else if (deviceType.equals(UserRegInfo.DeviceType.ANDROID)) {
			userDeviceDetails = new UserAndroidDetails();
		} else {
			throw new ServiceException("Unknown deviceType=[" + deviceType + "]");
		}

		LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}

	public UserDeviceDetails getUserDeviceDetails(String deviceType, int userId) {
		if (deviceType == null)
			throw new ServiceException("The parameter deviceType is null");
		LOGGER.debug("input parameters deviceType, userId: [{}], [{}]", deviceType, userId);

		final UserDeviceDetails userDeviceDetails;
		if (deviceType.equals(UserRegInfo.DeviceType.IOS)) {
			userDeviceDetails = userIPhoneDetailsService.getUserDeviceDetails(userId);
		} else if (deviceType.equals(UserRegInfo.DeviceType.ANDROID)) {
			userDeviceDetails = userAndroidDetailsService.getUserDeviceDetails(userId);
		} else {
			userDeviceDetails = null;
		}

		LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}

	public List<UserDeviceDetails> getAllUserDeviceDetails(int userId) {
		final List<UserDeviceDetails> userDeviceDetails = new LinkedList<UserDeviceDetails>();
		for (String deviceType : UserRegInfo.DeviceType.getValues())
		{
			UserDeviceDetails udd = getUserDeviceDetails(deviceType, userId);
			if (udd != null)
				userDeviceDetails.add(udd);
		}

		return userDeviceDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public UserDeviceDetails mergeUserDeviceDetails(User user, String pushNotificationToken, String deviceType) {
		if (user == null)
			throw new ServiceException("The parameter user is null");
		if (pushNotificationToken == null)
			throw new ServiceException("The parameter pushNotificationToken is null");
		if (deviceType == null)
			throw new ServiceException("The parameter deviceType is null");

		LOGGER.debug("input parameters user, pushNotificationToken, deviceType: [{}], [{}], [{}]", new Object[] { user, pushNotificationToken, deviceType });
		int userId = user.getId();

		UserDeviceDetails userDeviceDetails = getUserDeviceDetails(deviceType, userId);
		if (userDeviceDetails == null) {
			userDeviceDetails = getUserDeviceDetailsNewInstance(deviceType);
			userDeviceDetails.setToken(pushNotificationToken);
			userDeviceDetails.setUser(user);
			userDeviceDetails.setUserGroup(user.getUserGroup());
			entityService.saveEntity(userDeviceDetails);
		} else if (!pushNotificationToken.equals(userDeviceDetails.getToken())) {
			userDeviceDetails.setToken(pushNotificationToken);
			entityService.updateEntity(userDeviceDetails);
		}

		LOGGER.debug("Output parameter userIPhoneDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void removeUserDeviceDetails(User user) {
		if (user == null)
			throw new ServiceException("The parameter user is null");

		int userId = user.getId();

		for (UserDeviceDetails userDeviceDetails : getAllUserDeviceDetails(userId))
			entityService.removeEntity(userDeviceDetails.getClass(), userDeviceDetails.getId());
	}
}
