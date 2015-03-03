package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserIPhoneDetailsDao extends JpaDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserIPhoneDetailsDao.class);

    @SuppressWarnings("unchecked")
    public UserDeviceDetails getUserDeviceDetails(int userId) {
        LOGGER.debug("input parameters userId: [{}]", userId);

        UserDeviceDetails userDeviceDetails = null;
        List<UserDeviceDetails> userDeviceDetailsList = getJpaTemplate().findByNamedQuery(UserIPhoneDetails.NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID, userId);
        if (!userDeviceDetailsList.isEmpty()) {
            userDeviceDetails = userDeviceDetailsList.get(0);
        }

        LOGGER.debug("Output parameter userDeviceDetails=[{}]", userDeviceDetails);
        return userDeviceDetails;
    }

}
