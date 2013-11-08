package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.dto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.web.subscription.PaymentPageData;

import java.util.List;

/**
 * Bean exposed on the payments page
 * 
 * @author Adrian Zavelcuta
 * @date 10 Oct 2013
 *
 */
public class PaymentsPage {

	private String mobilePhoneNumber;
	private List<PaymentPolicyDto> paymentPolicies;
	private boolean consumerUser;
	private PaymentDetails paymentDetails;
	private PaymentPolicy activePaymentPolicy;
	private String paymentPoliciesNote;
	private boolean userCanGetVideo;
	private boolean userIsOptedInToVideo;
	private boolean appleIOSAndNotBusiness;
	private PaymentPageData paymentPageData;
	private boolean disablePageIfUserHasPendingPayment;

	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	public List<PaymentPolicyDto> getPaymentPolicies() {
		return paymentPolicies;
	}

	public void setPaymentPolicies(List<PaymentPolicyDto> paymentPolicies) {
		this.paymentPolicies = paymentPolicies;
	}

    public boolean isAwaitingPaymentStatus() {
        return paymentDetails != null && paymentDetails.getLastPaymentStatus() == PaymentDetailsStatus.AWAITING;
    }

	public boolean isConsumerUser() {
		return consumerUser;
	}

	public void setConsumerUser(boolean consumerUser) {
		this.consumerUser = consumerUser;
	}

	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
		
		if ( this.paymentDetails != null ) {
			this.activePaymentPolicy = this.paymentDetails.getPaymentPolicy();
		}
	}

	public PaymentPolicy getActivePaymentPolicy() {
		return activePaymentPolicy;
	}

	public void setActivePaymentPolicy(PaymentPolicy activePaymentPolicy) {
		this.activePaymentPolicy = activePaymentPolicy;
	}

	public String getPaymentPoliciesNote() {
		return paymentPoliciesNote;
	}

	public void setPaymentPoliciesNote(String paymentPoliciesNote) {
		this.paymentPoliciesNote = paymentPoliciesNote;
	}

	public boolean isUserCanGetVideo() {
		return userCanGetVideo;
	}

	public void setUserCanGetVideo(boolean userCanGetVideo) {
		this.userCanGetVideo = userCanGetVideo;
	}

	public boolean isUserIsOptedInToVideo() {
		return userIsOptedInToVideo;
	}

	public void setUserIsOptedInToVideo(boolean userIsOptedInToVideo) {
		this.userIsOptedInToVideo = userIsOptedInToVideo;
	}

	public boolean isAppleIOSAndNotBusiness() {
		return appleIOSAndNotBusiness;
	}

	public void setAppleIOSAndNotBusiness(boolean appleIOSAndNotBusiness) {
		this.appleIOSAndNotBusiness = appleIOSAndNotBusiness;
	}
	
	public PaymentPageData getPaymentPageData() {
		return paymentPageData;
	}

	public void setPaymentPageData(PaymentPageData paymentPageData) {
		this.paymentPageData = paymentPageData;
	}

	public boolean isDisablePageIfUserHasPendingPayment() {
		return disablePageIfUserHasPendingPayment;
	}

	public void setDisablePageIfUserHasPendingPayment(
			boolean disablePageIfUserHasPendingPayment) {
		this.disablePageIfUserHasPendingPayment = disablePageIfUserHasPendingPayment;
	}

	public String getPaymentDetailsType() {
		String paymentType = null;
        if ( paymentDetails != null ) {
        	if ( PaymentDetails.PAYPAL_TYPE.equalsIgnoreCase(paymentDetails.getPaymentType()) ) {
        		paymentType = "paypal";
        	} else if ( PaymentDetails.SAGEPAY_CREDITCARD_TYPE.equalsIgnoreCase( paymentDetails.getPaymentType()) ) {
        		paymentType = "creditcard";
        	}
        }
        
        return paymentType;
	}
	
	public boolean isPaymentDetailsActivated() {
		return paymentDetails!=null && paymentDetails.isActivated();
	}
}
