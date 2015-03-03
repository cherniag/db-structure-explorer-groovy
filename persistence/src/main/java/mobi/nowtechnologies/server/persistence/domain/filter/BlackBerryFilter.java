package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @generated
 */
@javax.persistence.Entity
@javax.persistence.DiscriminatorValue(value = "BLACKBERRY")
public class BlackBerryFilter extends AbstractFilterWithCtiteria implements java.io.Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlackBerryFilter.class);
    /**
     * @generated
     */
    private static final long serialVersionUID = 1919163639L;

    @Override
    public boolean doFilter(User user) {
        LOGGER.debug("input parameters user: [{}]", user);

        final DeviceType deviceType = user.getDeviceType();

        boolean filtrate = false;

        if (deviceType.equals(DeviceTypeDao.getBlackberryDeviceType())) {
            filtrate = true;
        }

        LOGGER.debug("Output parameter [{}]", filtrate);
        return filtrate;
    }
}
