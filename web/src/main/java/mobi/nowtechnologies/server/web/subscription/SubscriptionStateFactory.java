package mobi.nowtechnologies.server.web.subscription;

import java.util.Date;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.SubscriptionDirection;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import org.joda.time.DateTime;
import org.joda.time.Days;

public class SubscriptionStateFactory {

	public SubscriptionState getInstance(User user) {
		SubscriptionState state = new SubscriptionState();

		state.setEligibleForVideo(user.getTariff() == Tariff._4G);

		if (!user.isSubscribedStatus()) {
			//preview mode
			return state;
		}

		PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
		if ((currentPaymentDetails != null) && (currentPaymentDetails.isActivated())) {
			state.setPaySubscription(true);
		}
		if(!state.isPaySubscription()){
			state.setFreeTrial(user.isOnFreeTrial());
		}
		
		
		if (state.isFreeTrial()) {
			if (state.isEligibleForVideo()) {
				state.setUnlimitedFreeTrialFor4G(true);

			}
			state.setFreeTrialAudioOnly(!user.isOnVideoAudioFreeTrial());
			state.setFreeTrialOptedIn(false);//we are not auto-subscribing user at the moment
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

		// accountCheckDTO.setCanPlayVideo(user.canPlayVideo());
		// accountCheckDTO.setCanActivateVideoTrial(canActivateVideoTrial);
		// accountCheckDTO.setHasAllDetails(user.hasAllDetails());
		// accountCheckDTO.setShowFreeTrial(user.isShowFreeTrial());
		// accountCheckDTO.setSubscriptionChanged(user.getSubscriptionDirection());
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
