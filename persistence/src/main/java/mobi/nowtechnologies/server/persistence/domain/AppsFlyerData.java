package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Author: Gennadii Cherniaiev
 * Date: 11/10/2014
 */
@Entity
@Table(name = "apps_flyer_data",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"})
)
public class AppsFlyerData {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "apps_flyer_uid", nullable = false)
    private String appsFlyerUid;

    protected AppsFlyerData() {
    }

    public AppsFlyerData(int userId, String appsFlyerUid) {
        Preconditions.checkArgument(userId > 0);
        this.userId = userId;
        this.appsFlyerUid = Preconditions.checkNotNull(appsFlyerUid);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAppsFlyerUid() {
        return appsFlyerUid;
    }

    public void setAppsFlyerUid(String appsFlyerUid) {
        this.appsFlyerUid = appsFlyerUid;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("userId", userId)
                .append("appsFlyerUid", appsFlyerUid)
                .toString();
    }
}
