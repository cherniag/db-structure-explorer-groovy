package mobi.nowtechnologies.server.persistence.domain.filter;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("UserStateFilter")
public class UserStateFilter extends AbstractFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserStateFilter.class);
	private static final int ONE_DAY_SECONDS=24*60*60;

	@Override
	public boolean doFilter(User user, Object param) {
		LOGGER.debug("input parameters user, param: [{}], [{}]", user, param);
		NewsDetail newsDetail = (NewsDetail) param;

		UserState userState = newsDetail.getUserState();

		final UserStatus userStatus = user.getStatus();
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

		boolean filtrate = false;

		switch (userState) {
		case NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS:
			if (currentPaymentDetails == null || !currentPaymentDetails.isActivated())
				filtrate = true;
			break;
		case LIMITED:
			if (userStatus.equals(UserStatusDao.getLimitedUserStatus()))
				filtrate = true;
			break;
		case FREE_TRIAL:
			if (userStatus.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails == null)
				filtrate = true;
			break;
		case LAST_TRIAL_DAY:
			if (userStatus.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails == null) {
				int nextSubPaymentSeconds = user.getNextSubPayment();
				int currentTimeSeconds= Utils.getEpochSeconds();
				if (nextSubPaymentSeconds-currentTimeSeconds<ONE_DAY_SECONDS) filtrate = true;
			}
			break;
		case PAYMENT_ERROR:
			if (currentPaymentDetails != null && currentPaymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.ERROR))
				filtrate = true;
			break;
		case LIMITED_AFTER_TRIAL:
			if (userStatus.equals(UserStatusDao.getLimitedUserStatus()) && currentPaymentDetails == null)
				filtrate = true;
			break;
		case ONE_MONTH_PROMO:
			throw new PersistenceException("Not implemented");
		default:
			throw new PersistenceException("Unknown user state ["+userState+"]");
		}	
		
		LOGGER.debug("Output parameter filtrate=[{}]", filtrate);
		return filtrate;
	}
}
