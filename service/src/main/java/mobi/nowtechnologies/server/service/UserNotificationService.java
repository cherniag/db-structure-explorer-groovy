package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;

/**
 * @author Titov Mykhaylo (titov)
 */
public interface UserNotificationService {

    String DOWNGRADE_FROM_4G_SUBSCRIBED = "DOWNGRADE_FROM_4G_SUBSCRIBED";
    String DOWNGRADE_FROM_4G_FREETRIAL = "DOWNGRADE_FROM_4G_FREETRIAL";

    Future<Boolean> notifyUserAboutSuccessfulPayment(User user);

    Future<Boolean> sendUnsubscribeAfterSMS(User user) throws UnsupportedEncodingException;

    Future<Boolean> sendSubscriptionChangedSMS(User user) throws UnsupportedEncodingException;

    Future<Boolean> sendSmsOnFreeTrialExpired(User user) throws UnsupportedEncodingException;

    Future<Boolean> sendChargeNotificationReminder(User user) throws UnsupportedEncodingException;

    Future<Boolean> sendLowBalanceWarning(User user) throws UnsupportedEncodingException;

    Future<Boolean> send4GDowngradeSMS(User user, String smsType) throws UnsupportedEncodingException;

    Future<Boolean> sendActivationPinSMS(User user) throws UnsupportedEncodingException;

    Future<Boolean> sendPaymentFailSMS(PaymentDetails paymentDetails);

    boolean sendSMSByKey(User user, String phoneNumber, String messageKey) throws UnsupportedEncodingException;
}
