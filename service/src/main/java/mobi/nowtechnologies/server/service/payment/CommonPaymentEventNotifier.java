package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.UserNotificationService;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
public class CommonPaymentEventNotifier implements PaymentEventNotifier {

    private UserNotificationService userNotificationService;


    @Override
    public void onError(PaymentDetails paymentDetails) {
        if(paymentDetails.isCurrentAttemptFailed()){
            userNotificationService.sendPaymentFailSMS(paymentDetails);
        }
    }

    @Override
    public void onUnsubscribe(User user) {
        //no sms required in general flow
    }

    public void setUserNotificationService(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }
}
