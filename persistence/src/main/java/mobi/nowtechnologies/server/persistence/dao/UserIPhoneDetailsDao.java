package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;

import java.util.List;

import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserIPhoneDetailsDao extends JpaDaoSupport {
    @SuppressWarnings("unchecked")
    public UserDeviceDetails getUserDeviceDetails(int userId) {

        UserDeviceDetails userDeviceDetails = null;
        List<UserDeviceDetails> userDeviceDetailsList = getJpaTemplate().findByNamedQuery(UserIPhoneDetails.NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID, userId);
        if (!userDeviceDetailsList.isEmpty()) {
            userDeviceDetails = userDeviceDetailsList.get(0);
        }

        return userDeviceDetails;
    }

}
