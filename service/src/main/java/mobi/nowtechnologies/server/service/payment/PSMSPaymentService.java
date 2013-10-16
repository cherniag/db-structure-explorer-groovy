package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.exception.ServiceException;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/15/13
 * Time: 2:54 PM
 */
public interface PSMSPaymentService<T extends PSMSPaymentDetails> extends PaymentSystemService {
    T commitPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException;

    T createPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException;
}
