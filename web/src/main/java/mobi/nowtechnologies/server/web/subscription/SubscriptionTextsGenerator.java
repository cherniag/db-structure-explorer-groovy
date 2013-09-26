package mobi.nowtechnologies.server.web.subscription;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class SubscriptionTextsGenerator {

	private MessageSource messageSource;
	private Locale locale;

	public SubscriptionTextsGenerator(MessageSource messageSource, Locale locale) {
		super();
		this.messageSource = messageSource;
		this.locale = locale;
	}

	public SubscriptionTexts generate(SubscriptionState state) {
		SubscriptionTexts data = new SubscriptionTexts();

		if (state.isPreviewMode()) {
			data.setStatusText(getMessage("subscription.text.previewMode"));
			data.setNextBillingText(getMessage("subscription.text.nextBilling_previewMode"));
			data.setFutureText(null);
			return data;
		}

		if (state.isFreeTrial()) {
			if (!state.isEligibleForVideo()) {
				if (state.isFreeTrialOptedIn()) {
					data.setStatusText(getMessage("subscription.text.subscribed"));
					data.setNextBillingText(getMessage("subscription.text.freeTrial_next_bill.youWillBeSubscribed",
							state.getDaysToNextBillingDate()));
					data.setFutureText(getMessage("subscription.text.subscription_future.subscribed"));
					return data;
				}
				data.setStatusText(getMessage("subscription.text.freeTrial"));
				data.setNextBillingText(getMessage("subscription.text.freeTrial_next_bill.leftDays",
						state.getDaysToNextBillingDate()));
				data.setFutureText(null);
			} else {

				if (state.isFreeTrialOptedIn()) {
					data.setStatusText(getMessage("subscription.text.subscribed"));
					data.setNextBillingText(getMessage("subscription.text.freeTrial_next_bill.youWillBeNotified"));
					data.setFutureText(getMessage("subscription.text.subscription_future.subscribed"));
					return data;
				}

				if (state.isFreeTrialAudioOnly()) {
					data.setStatusText(getMessage("subscription.text.freeTrial_AudioOnly"));
				} else {
					data.setStatusText(getMessage("subscription.text.freeTrial_Video"));
				}
				data.setNextBillingText(getMessage("subscription.text.freeTrial_next_bill.youWillBeNotified"));
				data.setFutureText(null);
			}
			return data;
		}

		if (!state.isPaySubscription()) {
			throw new IllegalArgumentException("Expecting pay subscription here");
		}

		if (state.isEligibleForVideo()) {
			if (state.isSubscribedToVideo()) {
				data.setStatusText(getMessage("subscription.text.subscribed_Video"));
			} else {
				data.setStatusText(getMessage("subscription.text.subscribed_AudioOnly"));
			}
		} else {
			data.setStatusText(getMessage("subscription.text.subscribed"));
		}

		if (state.isExpiringSubscription()) {
			data.setFutureText(getMessage("subscription.text.subscription_future.expiring"));
			data.setNextBillingText(getMessage("subscription.text.subscription_next_bill.expiring",
					getLongDate(state.getNextBillingDate())));
			return data;
		}

		if (state.isUpgradingToVideo()) {
			data.setNextBillingText(getMessage("subscription.text.subscription_next_bill.upgradeVideo",
					getLongDate(state.getNextBillingDate())));
			data.setFutureText(getMessage("subscription.text.subscription_future.upgrading"));
		} else if (state.isDowngradingToAudioOnly()) {
			data.setNextBillingText(getMessage("subscription.text.subscription_next_bill.downgradeVideo",
					getLongDate(state.getNextBillingDate())));
			data.setFutureText(getMessage("subscription.text.subscription_future.downgrading"));

		} else {
			data.setNextBillingText(getMessage("subscription.text.subscription_next_bill.ongoing",
					getLongDate(state.getNextBillingDate())));
			data.setFutureText(getMessage("subscription.text.subscription_future.ongoing"));
		}
		return data;
	}

	private String getLongDate(Date date) {
		DateFormat df = new SimpleDateFormat("dd MMMMM yyyy");
		return df.format(date);
	}

	private String getMessage(String code, Object... params) {
		return messageSource.getMessage(code, params, locale);
	}

}
