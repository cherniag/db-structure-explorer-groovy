package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PendingPaymentFactory {
	public static PendingPayment createPendingPayment() {
		return new PendingPayment();
	}
}