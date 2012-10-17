package mobi.nowtechnologies.server.persistence.domain;

import java.math.BigDecimal;



/**
 * The class <code>PaymentPolicyFactory</code> implements static methods that return instances of the class <code>{@link PaymentPolicy}</code>.
 *
 * @generatedBy CodePro at 29.08.12 11:09
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class PaymentPolicyFactory
 {
	/**
	 * Prevent creation of instances of this class.
	 *
	 * @generatedBy CodePro at 29.08.12 11:09
	 */
	private PaymentPolicyFactory() {
	}


	/**
	 * Create an instance of the class <code>{@link PaymentPolicy}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 11:09
	 */
	public static PaymentPolicy createPaymentPolicy() {
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setSubcost(BigDecimal.ZERO);
		paymentPolicy.setShortCode("shortCode");
		return paymentPolicy;
	}
}