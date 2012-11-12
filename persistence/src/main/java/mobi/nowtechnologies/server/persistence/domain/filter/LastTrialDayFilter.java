package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilterWithCtiteria;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.shared.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@javax.persistence.Entity
@javax.persistence.DiscriminatorValue(value = "LAST_TRIAL_DAY")
public class LastTrialDayFilter extends AbstractFilterWithCtiteria implements java.io.Serializable {
	
	private static final int ONE_DAY_SECONDS=24*60*60;

	private static final Logger LOGGER = LoggerFactory.getLogger(LastTrialDayFilter.class);
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -1290443204L;

	@Override
	public boolean doFilter(User user) {
		LOGGER.debug("input parameters user: [{}]", user);

		boolean filtrate = false;
		final UserStatus userStatus = user.getStatus();
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		if (userStatus.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails == null) {
			int nextSubPaymentSeconds = user.getNextSubPayment();
			int currentTimeSeconds = Utils.getEpochSeconds();
			if (nextSubPaymentSeconds - currentTimeSeconds < ONE_DAY_SECONDS)
				filtrate = true;
		}
		LOGGER.debug("Output parameter [{}]", filtrate);
		return filtrate;
	}
}
