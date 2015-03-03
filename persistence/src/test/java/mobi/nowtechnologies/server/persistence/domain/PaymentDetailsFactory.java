package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.SUCCESSFUL;

/**
 * @author Titov Mykhaylo (titov) 08.03.14 8:57
 */
public class PaymentDetailsFactory {

    public static PaymentDetails paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful() {
        return new PaymentDetails().withActivated(true).withLastPaymentStatus(SUCCESSFUL);
    }

    public static PaymentDetails paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3() {
        return new PaymentDetails().withActivated(true).withLastPaymentStatus(ERROR).withRetriesOnError(3);
    }
}
