package mobi.nowtechnologies.server.web.subscription;

/** data displayed in payments page */
public class PaymentPageData implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private SubscriptionState subscriptionState = new SubscriptionState();
    private SubscriptionTexts subscriptionTexts = new SubscriptionTexts();

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

}
