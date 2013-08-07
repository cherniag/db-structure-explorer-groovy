package mobi.nowtechnologies.server.web.subscription;

import java.util.Date;

public class SubscriptionState implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private boolean paySubscription;
	private boolean freeTrial;

	private boolean unlimitedFreeTrialFor4G;
	private boolean freeTrialAudioOnly;
	private boolean freeTrialOptedIn;

	private boolean eligibleForVideo;

	private boolean subscribedToVideo;
	private Date nextBillingDate;
	private Integer daysToNextBillingDate;
	private boolean upgradingToVideo;
	private boolean downgradingToAudioOnly;
	
	private boolean expiringSubscription;

	public boolean isPreviewMode() {
		return !(isFreeTrial() || isPaySubscription());
	}

	public boolean isPaySubscription() {
		return paySubscription;
	}

	public void setPaySubscription(boolean paySubscription) {
		this.paySubscription = paySubscription;
	}

	public boolean isFreeTrial() {
		return freeTrial;
	}

	public void setFreeTrial(boolean freeTrial) {
		this.freeTrial = freeTrial;
	}

	public boolean isUnlimitedFreeTrialFor4G() {
		return unlimitedFreeTrialFor4G;
	}

	public void setUnlimitedFreeTrialFor4G(boolean unlimitedFreeTrialFor4G) {
		this.unlimitedFreeTrialFor4G = unlimitedFreeTrialFor4G;
	}

	public boolean isFreeTrialAudioOnly() {
		return freeTrialAudioOnly;
	}

	public void setFreeTrialAudioOnly(boolean freeTrialAudioOnly) {
		this.freeTrialAudioOnly = freeTrialAudioOnly;
	}

	public boolean isEligibleForVideo() {
		return eligibleForVideo;
	}

	public void setEligibleForVideo(boolean eligibleForVideo) {
		this.eligibleForVideo = eligibleForVideo;
	}

	public boolean isSubscribedToVideo() {
		return subscribedToVideo;
	}

	public void setSubscribedToVideo(boolean subscribedToVideo) {
		this.subscribedToVideo = subscribedToVideo;
	}

	public Date getNextBillingDate() {
		return nextBillingDate;
	}

	public void setNextBillingDate(Date nextBillingDate) {
		this.nextBillingDate = nextBillingDate;
	}

	public boolean isUpgradingToVideo() {
		return upgradingToVideo;
	}

	public void setUpgradingToVideo(boolean upgradingToVideo) {
		this.upgradingToVideo = upgradingToVideo;
	}

	public boolean isExpiringSubscription() {
		return expiringSubscription;
	}

	public void setExpiringSubscription(boolean expiringSubscription) {
		this.expiringSubscription = expiringSubscription;
	}

	public Integer getDaysToNextBillingDate() {
		return daysToNextBillingDate;
	}

	public void setDaysToNextBillingDate(Integer daysToNextBillingDate) {
		this.daysToNextBillingDate = daysToNextBillingDate;
	}

	public boolean isFreeTrialOptedIn() {
		return freeTrialOptedIn;
	}

	public void setFreeTrialOptedIn(boolean freeTrialOptedIn) {
		this.freeTrialOptedIn = freeTrialOptedIn;
	}

	public boolean isDowngradingToAudioOnly() {
		return downgradingToAudioOnly;
	}

	public void setDowngradingToAudioOnly(boolean downgradingToAudioOnly) {
		this.downgradingToAudioOnly = downgradingToAudioOnly;
	}

}
