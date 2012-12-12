package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.UserAndroidDetails;
import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class UserAndroidDetailsDao  extends JpaDaoSupport {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAndroidDetailsDao.class);

	@SuppressWarnings("unchecked")
	public UserDeviceDetails getUserDeviceDetails(int userId) {
		LOGGER.debug("input parameters userId: [{}]", userId);
		
		UserDeviceDetails userDeviceDetails = null;
		List<UserDeviceDetails> userDeviceDetailsList = getJpaTemplate().findByNamedQuery(UserAndroidDetails.NQ_GET_USER_ANDROID_DETAILS_BY_USER_ID, userId);
		if (!userDeviceDetailsList.isEmpty()) userDeviceDetails = userDeviceDetailsList.get(0);
		
		LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
		return userDeviceDetails;
	}
}
