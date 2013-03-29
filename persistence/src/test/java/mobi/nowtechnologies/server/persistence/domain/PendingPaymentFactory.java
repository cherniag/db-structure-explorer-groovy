package mobi.nowtechnologies.server.persistence.domain;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PendingPaymentFactory
 {
	private PendingPaymentFactory() {
	}


	public static PendingPayment createPendingPayment() {
		return new PendingPayment();
	}
}