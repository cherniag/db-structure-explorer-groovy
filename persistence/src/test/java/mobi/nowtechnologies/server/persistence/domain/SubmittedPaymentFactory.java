package mobi.nowtechnologies.server.persistence.domain;


import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;

/**
 * @author Titov Mykhaylo (titov)
 */
public class SubmittedPaymentFactory {
	public static SubmittedPayment createSubmittedPayment() {
		return SubmittedPayment.valueOf(new PendingPayment());
	}
	
	public static SubmittedPayment createSubmittedPayment(Long id, User user,PaymentDetailsType paymentDetailsType) {
		SubmittedPayment submittedPayment = new SubmittedPayment();
		submittedPayment.setI(1L);
		submittedPayment.setUser(user);
		submittedPayment.setType(paymentDetailsType);
		return submittedPayment;
	}
}