package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.device.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "BLACKBERRY")
public class BlackBerryFilter extends AbstractFilterWithCtiteria {
    @Override
    public boolean doFilter(User user) {
        return DeviceTypeDao.getBlackberryDeviceType().equals(user.getDeviceType());
    }
}
