package mobi.nowtechnologies.server.persistence.domain.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;

/**
 * @generated
 */
/**
 * @author Titov Mykhaylo (titov)
 *
 */
@javax.persistence.Entity
@javax.persistence.DiscriminatorValue(value = "LIMITED_AFTER_TRIAL")
public class LimitedAfterTrialFilter extends AbstractFilterWithCtiteria implements java.io.Serializable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LimitedAfterTrialFilter.class);
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -2011329756L;

	@Override
	public boolean doFilter(User user) {
		LOGGER.debug("input parameters user: [{}]", user);

		boolean filtrate = false;
		final UserStatus userStatus = user.getStatus();
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		if (userStatus.equals(UserStatusDao.getLimitedUserStatus()) && currentPaymentDetails == null)
			filtrate = true;
		
		LOGGER.debug("Output parameter [{}]", filtrate);
		return filtrate;
	}
}
