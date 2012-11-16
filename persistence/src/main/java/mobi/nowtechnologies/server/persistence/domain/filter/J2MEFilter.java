package mobi.nowtechnologies.server.persistence.domain.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * @generated
 */
@javax.persistence.Entity
@javax.persistence.DiscriminatorValue(value = "J2ME")
public class J2MEFilter extends AbstractFilterWithCtiteria implements java.io.Serializable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(J2MEFilter.class);
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -288947584L;

	@Override
	public boolean doFilter(User user) {
		LOGGER.debug("input parameters user: [{}]", user);
		
		final DeviceType deviceType = user.getDeviceType();

		boolean filtrate = false;
		
		if (deviceType.equals(DeviceTypeDao.getJ2meDeviceType()))
			filtrate = true;
		
		LOGGER.debug("Output parameter [{}]", filtrate);
		return filtrate;
	}
}
