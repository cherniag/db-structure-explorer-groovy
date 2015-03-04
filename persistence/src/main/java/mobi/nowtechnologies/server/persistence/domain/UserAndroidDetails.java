package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "tb_userAndroidDetails")
@NamedQueries(
    {@NamedQuery(name = UserAndroidDetails.NQ_GET_USER_ANDROID_DETAILS_BY_USER_ID, query = "SELECT userAndroidDetails FROM UserAndroidDetails userAndroidDetails WHERE userAndroidDetails.userId=?")})
public class UserAndroidDetails extends UserDeviceDetails {

    public static final String NQ_GET_USER_ANDROID_DETAILS_BY_USER_ID = "NQ_GET_USER_ANDROID_DETAILS_BY_USER_ID";

    @Override
    public String toString() {
        return "UserAndroidDetails [" + super.toString() + "]";
    }
}
