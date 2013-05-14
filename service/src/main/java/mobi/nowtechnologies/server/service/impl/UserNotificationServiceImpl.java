package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.aop.SMSNotification;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserNotificationServiceImpl implements UserNotificationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserNotificationServiceImpl.class);

	private UserService userService;

	// TODO remove it after refactoring
	private SMSNotification smsNotification;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setSmsNotification(SMSNotification smsNotification) {
		this.smsNotification = smsNotification;
	}

	@Async
	@Override
	public Future<Boolean> notifyUserAboutSuccesfullPayment(User user) {
		try {
			LOGGER.debug("input parameters user: [{}]", user);
			if (user == null)
				throw new NullPointerException("The parameter user is null");

			LogUtils.putPaymentMDC(String.valueOf(user.getId()), String.valueOf(user.getUserName()), String.valueOf(user.getUserGroup().getCommunity()
					.getName()), UserNotificationService.class);

			Future<Boolean> result;
			try {

				result = userService.makeSuccesfullPaymentFreeSMSRequest(user);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				result = new AsyncResult<Boolean>(Boolean.FALSE);
			}
			LOGGER.info("Output parameter result=[{}]", result);
			return result;
		} finally {
			LogUtils.removePaymentMDC();
		}
	}

	@Async
	@Override
	public Future<Boolean> sendUnsubscribeAfterSMS(User user) throws UnsupportedEncodingException {
		try {
			if (user == null)
				throw new NullPointerException("The parameter user is null");
			if (user.getCurrentPaymentDetails() == null)
				throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");
			
			LogUtils.putGlobalMDC(user.getUserName(), user.getUserGroup().getCommunity().getName(), "", this.getClass(), "");

			Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

			LOGGER.info("Attempt to send unsubscription confirmation sms async in memory");

			Integer days = Days.daysBetween(new DateTime(Utils.getEpochMillis()).toDateMidnight(), new DateTime(user.getNextSubPayment() * 1000L).toDateMidnight()).getDays();
			if (!smsNotification.rejectDevice(user, "sms.notification.unsubscribed.not.for.device.type")) {

				DeviceType deviceType = user.getDeviceType();
				String deviceTypeName = deviceType.getName();

				smsNotification.sendSMSWithUrl(user, "sms.unsubscribe.after.text." + deviceTypeName, new String[] { smsNotification.getPaymentsUrl(), days.toString() });

				LOGGER.info("The unsubscribe unsubscription sms was sent successfully");
				result = new AsyncResult<Boolean>(Boolean.TRUE);
			} else {
				LOGGER.info("The unsubscription confirmation sms wasn't send cause rejecting by device type");
			}
			return result;
		} finally {
			LogUtils.removeGlobalMDC();
		}
	}

	@Async
	@Override
	public Future<Boolean> sendUnsubscribePotentialSMS(User user) throws UnsupportedEncodingException {
		try {
			if (user == null)
				throw new NullPointerException("The parameter user is null");
			if (user.getCurrentPaymentDetails() == null)
				throw new NullPointerException("The parameter user.getCurrentPaymentDetails() is null");

			LogUtils.putGlobalMDC(user.getUserName(), user.getUserGroup().getCommunity().getName(), "", this.getClass(), "");

			Future<Boolean> result = new AsyncResult<Boolean>(Boolean.FALSE);

			LOGGER.info("Attempt to send subscription confirmation sms async in memory");

			if (!smsNotification.rejectDevice(user, "sms.notification.subscribed.not.for.device.type")) {

				DeviceType deviceType = user.getDeviceType();
				String deviceTypeName = deviceType.getName();

				smsNotification.sendSMSWithUrl(user, "sms.unsubscribe.potential.text" + deviceTypeName, new String[] { smsNotification.getUnsubscribeUrl() });

				LOGGER.info("The subscription confirmation sms was sent successfully");
				result = new AsyncResult<Boolean>(Boolean.TRUE);
			} else {
				LOGGER.info("The subscription confirmation sms wasn't send cause rejecting by device type");
			}
			return result;
		} finally {
			LogUtils.removeGlobalMDC();
		}
	}

}
