package mobi.nowtechnologies.server.persistence.domain;


import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;

/**
 * @author Titov Mykhaylo (titov)
 */
public class O2PSMSPaymentDetailsFactory{

	public static O2PSMSPaymentDetails createO2PSMSPaymentDetails() {
		return new O2PSMSPaymentDetails();
	}
}