package mobi.nowtechnologies.server.web.subscription;

import java.util.Date;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.SubscriptionDirection;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriptionStateFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionStateFactory.class);

	public SubscriptionState getInstance(User user) {
		SubscriptionState state = new SubscriptionState();

		state.setEligibleForVideo(user.getTariff() == Tariff._4G);
		state.setPendingPayment(user.hasPendingPayment());

		boolean userInLimitedStatus = !user.isSubscribedStatus();
		if ((!user.isOnFreeTrial()) && (userInLimitedStatus || !user.isNextSubPaymentInTheFuture())) {
			//preview mode
			return state;
		}

		boolean paySubscription = (user.isSubscribedStatus() && user.isNextSubPaymentInTheFuture() 
				&& (user.getCurrentPaymentDetails() != null));

		if (paySubscription  && !(user.isOnFreeTrial()) ) {
			state.setPaySubscription(true);
		} else {
			state.setFreeTrial(true);

			if (!user.isOnFreeTrial()) {
				LOGGER.warn("Expecting user to be on free trial! {} {} {} {} ", user.getUserName(), user.getStatus(),
						user.getNextSubPayment(), user.getFreeTrialExpiredMillis());
			}
		}
		
		if (state.isFreeTrial()) {
			if (state.isEligibleForVideo()) {
				state.setUnlimitedFreeTrialFor4G(true);
			}
			state.setFreeTrialAudioOnly(!user.isOnVideoAudioFreeTrial());
			
			state.setFreeTrialOptedIn(paySubscription && !user.isExpiring());
		}

		if (state.isPaySubscription()) {

			state.setExpiringSubscription(user.isExpiring());

			if (SubscriptionDirection.DOWNGRADE == user.getSubscriptionDirection()) {
				state.setDowngradingToAudioOnly(true);
			}

			if (SubscriptionDirection.UPGRADE == user.getSubscriptionDirection()) {
				state.setUpgradingToVideo(true);
			}

			state.setSubscribedToVideo(user.isOn4GVideoAudioBoughtPeriod());
		}

		if (state.isFreeTrial()) {
			state.setNextBillingDate(new Date(user.getFreeTrialExpiredMillis()));
		} else {
			state.setNextBillingDate(Utils.getDateFromInt(user.getNextSubPayment()));
		}
		state.setDaysToNextBillingDate(calculateDaysTillNextBilling(state.getNextBillingDate()));
		
		return state;
	}

	protected Integer calculateDaysTillNextBilling(Date date) {
		if (date == null) {
			return null;
		}

		return Days.daysBetween(new DateTime(getCurrentDate()), new DateTime(date)).getDays();
	}

	protected Date getCurrentDate() {
		return new Date();
	}

}