package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_useriPhoneDetails")
public class UserIPhoneDetails extends UserDeviceDetails {
    @Override
    public String toString() {
        return "UserIPhoneDetails [" + super.toString() + "]";
    }
}
