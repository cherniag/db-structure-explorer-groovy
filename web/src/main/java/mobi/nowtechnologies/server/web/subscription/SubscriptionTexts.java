package mobi.nowtechnologies.server.web.subscription;


/** subscription texts displayed in payments page */
public class SubscriptionTexts implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private String statusText;// freeTrial/preview/subscribed
	private String futureText;// Ongoing/expiring/upgrading/downgrading
	private String nextBillingText;// Next billing cycle 12 August 2013/etc
	
	//************************************************************
	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getFutureText() {
		return futureText;
	}

	public void setFutureText(String futureText) {
		this.futureText = futureText;
	}

	public String getNextBillingText() {
		return nextBillingText;
	}

	public void setNextBillingText(String nextBillingText) {
		this.nextBillingText = nextBillingText;
	}

}
