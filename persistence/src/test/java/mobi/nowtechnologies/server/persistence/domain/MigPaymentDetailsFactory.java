package mobi.nowtechnologies.server.persistence.domain;


import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;

/**
 * The class <code>MigPaymentDetailsFactory</code> implements static methods that return instances of the class <code>{@link mobi.nowtechnologies.server.persistence.domain.payment
 * .MigPaymentDetails}</code>.
 *
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class MigPaymentDetailsFactory {

    public static MigPaymentDetails createMigPaymentDetails() {
        final MigPaymentDetails migPaymentDetails = new MigPaymentDetails();
        migPaymentDetails.setMigPhoneNumber("migPhoneNumber");
        return migPaymentDetails;
    }


    public static MigPaymentDetails createMigPaymentDetails2() {
        return new MigPaymentDetails();
    }
}