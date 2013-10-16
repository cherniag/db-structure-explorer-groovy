package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
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
@javax.persistence.DiscriminatorValue(value = "FREE_TRIAL")
public class FreeTrialFilter extends AbstractFilterWithCtiteria implements java.io.Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(FreeTrialFilter.class);
	/**
	 * @generated
	 */
	private static final long serialVersionUID = 1375507162L;

	@Override
	public boolean doFilter(User user) {
		LOGGER.debug("input parameters user: [{}]", user);

		boolean filtrate = false;
		final UserStatus userStatus = user.getStatus();
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

		if (userStatus.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails == null)
			filtrate = true;
		LOGGER.debug("Output parameter [{}]", filtrate);
		return filtrate;
	}

}
