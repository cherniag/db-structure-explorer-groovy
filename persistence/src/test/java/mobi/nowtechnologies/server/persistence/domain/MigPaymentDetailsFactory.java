package mobi.nowtechnologies.server.persistence.domain;



/**
 * The class <code>MigPaymentDetailsFactory</code> implements static methods that return instances of the class <code>{@link MigPaymentDetails}</code>.
 *
 * @generatedBy CodePro at 29.08.12 11:04
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class MigPaymentDetailsFactory
 {
	/**
	 * Prevent creation of instances of this class.
	 *
	 * @generatedBy CodePro at 29.08.12 11:04
	 */
	private MigPaymentDetailsFactory() {
	}


	/**
	 * Create an instance of the class <code>{@link MigPaymentDetails}</code>.
	 *
	 * @generatedBy CodePro at 29.08.12 11:04
	 */
	public static MigPaymentDetails createMigPaymentDetails() {
		final MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
		migPaymentDetails.setMigPhoneNumber("migPhoneNumber");
		return migPaymentDetails;
	}
}