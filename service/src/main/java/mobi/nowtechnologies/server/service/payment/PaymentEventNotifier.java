package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
public interface PaymentEventNotifier {

    void onError(PaymentDetails paymentDetails);

    void onUnsubscribe(User user);

}
