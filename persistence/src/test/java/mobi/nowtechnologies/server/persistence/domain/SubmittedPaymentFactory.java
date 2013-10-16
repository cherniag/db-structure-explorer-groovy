package mobi.nowtechnologies.server.persistence.domain;


import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;

/**
 * The class <code>SubmittedPaymentFactory</code> implements static methods that return instances of the class <code>{@link mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment}</code>.
 *
 * @generatedBy CodePro at 29.08.12 17:23
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class SubmittedPaymentFactory
 {
	/**
	 * Prevent creation of instances of this class.
	 *
	 * @generatedBy CodePro at 29.08.12 17:23
	 */
	private SubmittedPaymentFactory() {
	}


	/**
	 * Create an instance of the class <code>{@link mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 17:23
	 */
	public static SubmittedPayment createSubmittedPayment() {
		return SubmittedPayment.valueOf(new PendingPayment());
	}


	/**
	 * Create an instance of the class <code>{@link SubmittedPayment}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 17:23
	 */
	public static SubmittedPayment createSubmittedPayment2() {
		return new SubmittedPayment();
	}
	

	/**
	 * Create an instance of the class <code>{@link SubmittedPayment}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 17:23
	 */
	public static SubmittedPayment createSubmittedPayment3(PaymentDetailsType paymentDetailsType) {
		SubmittedPayment submittedPayment = new SubmittedPayment();
		submittedPayment.setType(paymentDetailsType);
		return submittedPayment;
	}
	
	public static SubmittedPayment createSubmittedPayment(Long id, User user,PaymentDetailsType paymentDetailsType) {
		SubmittedPayment submittedPayment = new SubmittedPayment();
		submittedPayment.setI(1L);
		submittedPayment.setUser(user);
		submittedPayment.setType(paymentDetailsType);
		return submittedPayment;
	}
	
	
}