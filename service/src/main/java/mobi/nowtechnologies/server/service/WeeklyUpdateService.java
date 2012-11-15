package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * WeeklyUpdateService
 * 
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class WeeklyUpdateService implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyUpdateService.class);

	private static final int UPDATE_JOB_SLEEP_TIME_MS = 60000;

	private UserDao userDao;
	private EntityDao entityDao;
	private MigHttpService migHttpService;
	private CommunityResourceBundleMessageSource messageSource;

	private boolean stopped = false;

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public void setMigHttpService(MigHttpService migHttpService) {
		this.migHttpService = migHttpService;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setEntityDao(EntityDao entityDao) {
		this.entityDao = entityDao;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public void run() {
		MDC.put(LogUtils.LOG_CLASS, this.getClass());
		LOGGER.info("start weekly update job");
		while (!stopped) {
			LOGGER.info("Job awaked");
			try {
				updateWeekly();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				Thread.sleep(UPDATE_JOB_SLEEP_TIME_MS);
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		MDC.remove(LogUtils.LOG_CLASS);
	}

	public void updateWeekly() {
		List<User> users = userDao.getListOfUsersForWeeklyUpdate();
		LOGGER.info("weekly update job found [{}] users for update", users.size());
		for (User user : users) {
			try {
				MDC.put(LogUtils.LOG_USER_NAME, user.getUserName());
				MDC.put(LogUtils.LOG_USER_ID, user.getId());
				// if (user.getDeviceType() != DeviceType.IOS)
				saveWeeklyPayment(user);
				// else
				// LOGGER.info("user was ignored because device type is IOS");
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				MDC.remove(LogUtils.LOG_USER_NAME);
				MDC.remove(LogUtils.LOG_USER_ID);
			}

		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void saveWeeklyPayment(User user) throws Exception {
		if (user == null)
			throw new ServiceException("The parameter user is null");

		final int subBalance = user.getSubBalance();
		if (subBalance <= 0) {
			user.setStatus(UserStatusDao.getLimitedUserStatus());
			entityDao.updateEntity(user);
			LOGGER.info("Unable to make weekly update balance is " + subBalance + ", user id [" + user.getId()
					+ "]. So the user subscribtion status was changed on LIMITED");
		} else {
			final int nextSubPayment = user.getNextSubPayment();
			final byte status = user.getUserStatusId();
			final int paymentStatus = user.getPaymentStatus();

			user.setSubBalance((byte) (subBalance - 1));
			user.setNextSubPayment(Utils.getNewNextSubPayment(user.getNextSubPayment()));
			user.setStatus(UserStatusDao.getSubscribedUserStatus());
			entityDao.updateEntity(user);

			entityDao.saveEntity(new AccountLog(user.getId(), null, (byte) user.getSubBalance(), TransactionType.SUBSCRIPTION_CHARGE));

			LOGGER.info("weekly updated user id [{}], status OK, next payment [{}], subBalance [{}]",
					new Object[] { user.getId(), Utils.getDateFromInt(user.getNextSubPayment()), user.getSubBalance() });

//			PaymentDetails currentActivePaymentDetails = user.getCurrentPaymentDetails();
//
//			if (currentActivePaymentDetails!=null && PaymentDetails.MIG_SMS_TYPE.equals(currentActivePaymentDetails.getPaymentType())) {
//				String communityName = userDao.getCommunityNameByUserGroup(user.getUserGroupId());
//				PaymentPolicy paymentPolicy = userDao.getPaymentPolicyForUser(communityName, UserRegInfo.PaymentType.PREMIUM_USER, user.getOperator());
//
//				Locale locale = null;
//				Community community = CommunityDao.getMapAsNames().get(communityName);
//
//				String message = messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), AppConstants.SMS_WEEK_REMINDER_MSG, new Object[] { community.getDisplayName(),
//						paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(), paymentPolicy.getShortCode() }, locale);
//
//				migHttpService.makeFreeSMSRequest(((MigPaymentDetails) currentActivePaymentDetails).getMigPhoneNumber(), message);
//
//				LOGGER.info("The freeSms sent to user {} succesfully. The nextSubPayment, status, paymentStatus and subBalance was {}, {}, {}, {} respectively",
//						new Object[] { user, nextSubPayment, status, paymentStatus, subBalance });
//			}
		}
	}
}