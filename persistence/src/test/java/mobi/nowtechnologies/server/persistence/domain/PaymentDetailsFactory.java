package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

/**
 * @author Titov Mykhaylo (titov)
 *         08.03.14 8:57
 */
public class PaymentDetailsFactory {

    public static PaymentDetails paymentDetailsWithActivatedTrueAndLastPaymentStatusNone(){
        return new PaymentDetails().withActivated(true).withLastPaymentStatus(NONE);
    }

    public static PaymentDetails paymentDetailsWithActivatedTrueAndLastPaymentStatusError(){
        return new PaymentDetails().withActivated(true).withLastPaymentStatus(ERROR);
    }
}
