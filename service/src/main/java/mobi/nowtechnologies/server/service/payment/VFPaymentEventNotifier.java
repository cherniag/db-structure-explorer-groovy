package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
public class VFPaymentEventNotifier implements PaymentEventNotifier {
    @Override
    public void onError(PaymentDetails paymentDetails) {
        //will be sent by separate job - need to be migrated on Task model
    }

    @Override
    public void onUnsubscribe(User user) {
        //no sms required
    }
}
