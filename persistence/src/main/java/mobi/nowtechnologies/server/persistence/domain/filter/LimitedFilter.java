package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @generated
 */
/**
 * @author Titov Mykhaylo (titov)
 *
 */
@javax.persistence.Entity
@javax.persistence.DiscriminatorValue(value = "LIMITED")
public class LimitedFilter extends AbstractFilterWithCtiteria implements
		java.io.Serializable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LimitedFilter.class);
	/**
	 * @generated
	 */
	private static final long serialVersionUID = 1399103082L;

	@Override
	public boolean doFilter(User user) {
		LOGGER.debug("input parameters user: [{}]", user);
		
		boolean filtrate = false;
		final UserStatus userStatus = user.getStatus();
		if (userStatus.equals(UserStatusDao.getLimitedUserStatus()))
			filtrate = true;
		LOGGER.debug("Output parameter [{}]", filtrate);
		return filtrate;
	}
}
