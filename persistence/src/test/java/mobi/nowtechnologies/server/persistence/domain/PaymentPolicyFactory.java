package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import java.math.BigDecimal;



/**
 * The class <code>PaymentPolicyFactory</code> implements static methods that return instances of the class <code>{@link mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy}</code>.
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
	 * Create an instance of the class <code>{@link mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 11:09
	 */
	public static PaymentPolicy createPaymentPolicy() {
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setSubcost(BigDecimal.ZERO);
		paymentPolicy.setSubweeks((byte)5);
		paymentPolicy.setCurrencyISO("GBP");
		paymentPolicy.setShortCode("shortCode");
        paymentPolicy.setAvailableInStore(false);
        paymentPolicy.setPaymentType(PaymentDetails.O2_PSMS_TYPE);
		return paymentPolicy;
	}

     public static PaymentPolicy createPaymentPolicy(Tariff tariff) {
         PaymentPolicy paymentPolicy = createPaymentPolicy();
         paymentPolicy.setTariff(tariff);
         return paymentPolicy;
     }
}