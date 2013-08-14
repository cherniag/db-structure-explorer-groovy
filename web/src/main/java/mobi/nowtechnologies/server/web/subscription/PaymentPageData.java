package mobi.nowtechnologies.server.web.subscription;

/** data displayed in payments page */
public class PaymentPageData implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private SubscriptionState subscriptionState = new SubscriptionState();
	private SubscriptionTexts subscriptionTexts = new SubscriptionTexts();
	private boolean appleIOSNonO2Business = false;
	
	public PaymentPageData(SubscriptionState subscriptionState, SubscriptionTexts subscriptionTexts) {
		super();
		this.subscriptionState = subscriptionState;
		this.subscriptionTexts = subscriptionTexts;
	}

	public SubscriptionState getSubscriptionState() {
		return subscriptionState;
	}

	public void setSubscriptionState(SubscriptionState subscriptionState) {
		this.subscriptionState = subscriptionState;
	}

	public SubscriptionTexts getSubscriptionTexts() {
		return subscriptionTexts;
	}

	public void setSubscriptionTexts(SubscriptionTexts subscriptionTexts) {
		this.subscriptionTexts = subscriptionTexts;
	}

	public boolean isAppleIOSNonO2Business() {
		return appleIOSNonO2Business;
	}

	public void setAppleIOSNonO2Business(boolean appleIOSNonO2Business) {
		this.appleIOSNonO2Business = appleIOSNonO2Business;
	}



}
