package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_userAndroidDetails")
public class UserAndroidDetails extends UserDeviceDetails {
    @Override
    public String toString() {
        return "UserAndroidDetails [" + super.toString() + "]";
    }
}
