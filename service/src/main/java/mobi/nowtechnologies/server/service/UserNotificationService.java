package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public interface UserNotificationService {

	String DOWNGRADE_FROM_4G_SUBSCRIBED = "DOWNGRADE_FROM_4G_SUBSCRIBED";
	String DOWNGRADE_FROM_4G_FREETRIAL = "DOWNGRADE_FROM_4G_FREETRIAL";
	
	Future<Boolean> notifyUserAboutSuccesfullPayment(User user);

	Future<Boolean> sendUnsubscribeAfterSMS(User user) throws UnsupportedEncodingException;

	Future<Boolean> sendUnsubscribePotentialSMS(User user) throws UnsupportedEncodingException;

	Future<Boolean> sendSmsOnFreeTrialExpired(User user) throws UnsupportedEncodingException;

	Future<Boolean> sendChargeNotificationReminder(User user) throws UnsupportedEncodingException;

	Future<Boolean> sendLowBalanceWarning(User user) throws UnsupportedEncodingException;

	Future<Boolean> sendPaymentFailSMS(PendingPayment pendingPayment) throws UnsupportedEncodingException;
	
	Future<Boolean> send4GDowngradeSMS(User user, String smsType) throws UnsupportedEncodingException;

    Future<Boolean> sendActivationPinSMS(User user) throws UnsupportedEncodingException;

    boolean sendPaymentFailSMS(PaymentDetails paymentDetails) throws UnsupportedEncodingException;
}
