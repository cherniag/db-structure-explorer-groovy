package mobi.nowtechnologies.server.persistence.domain;



/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class O2PSMSPaymentDetailsFactory
 {
	private O2PSMSPaymentDetailsFactory() {
	}


	public static O2PSMSPaymentDetails createO2PSMSPaymentDetails() {
		return new O2PSMSPaymentDetails();
	}
}