package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "J2ME")
public class J2MEFilter extends AbstractFilterWithCtiteria {

    @Override
    public boolean doFilter(User user) {
        return DeviceTypeCache.getJ2meDeviceType().equals(user.getDeviceType());
    }
}
