package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "tb_useriPhoneDetails")
@NamedQueries(
    {@NamedQuery(name = UserIPhoneDetails.NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID, query = "SELECT userIPhoneDetails FROM UserIPhoneDetails userIPhoneDetails WHERE userIPhoneDetails.userId=?")})
public class UserIPhoneDetails extends UserDeviceDetails {

    public static final String NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID = "NQ_GET_USER_IPHONE_DETAILS_BY_USER_ID";

    @Override
    public String toString() {
        return "UserIPhoneDetails [" + super.toString() + "]";
    }

}
