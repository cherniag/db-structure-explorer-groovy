package mobi.nowtechnologies.server.persistence.domain.filter;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractFilter;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@DiscriminatorValue("PromotionUserStateFilter")
public class PUserStateFilter extends AbstractFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PUserStateFilter.class);
	
	private static final int ONE_DAY_SECONDS=24*60*60;
	
	@ElementCollection(targetClass=UserState.class)
	@Enumerated(EnumType.STRING)
	@Column(name="userStates", nullable=true)
	@CollectionTable(name="tb_filter_params")
	private List<UserState> userStates;
	
	@Override
	public boolean doFilter(User user, Object param) {
		final UserStatus userStatus = user.getStatus();
		final PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		
		List<UserState> userStates = new LinkedList<NewsDetailDto.UserState>();
		
		if (currentPaymentDetails == null || !currentPaymentDetails.isActivated()) {
			userStates.add(UserState.NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS);
		}
		if (userStatus.equals(UserStatusDao.getLimitedUserStatus())) {
			userStates.add(UserState.LIMITED);
		}
		if (userStatus.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails == null) {
			int nextSubPaymentSeconds = user.getNextSubPayment();
			int currentTimeSeconds= Utils.getEpochSeconds();
			if (nextSubPaymentSeconds-currentTimeSeconds<ONE_DAY_SECONDS) {
				userStates.add(UserState.LAST_TRIAL_DAY);
			}
		}
		if (userStatus.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails == null) {
			userStates.add(UserState.FREE_TRIAL);
		}
		if (currentPaymentDetails != null && currentPaymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.ERROR)) {
			userStates.add(UserState.PAYMENT_ERROR);
		}
		if (userStatus.equals(UserStatusDao.getLimitedUserStatus()) && currentPaymentDetails == null) {
			userStates.add(UserState.LIMITED_AFTER_TRIAL);
		}
		for (UserState state : userStates) {
			if (this.userStates.contains(state))
				return true;
		}
		return false;
	}

	public List<UserState> getUserStates() {
		return userStates;
	}

	public void setUserStates(List<UserState> userStates) {
		this.userStates = userStates;
	}
	
}