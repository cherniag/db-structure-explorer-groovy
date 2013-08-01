package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean.StatefulMethodInvokingJob;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class SmsAccordingToLawJob extends StatefulMethodInvokingJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsAccordingToLawJob.class);

	private UserService userService;
	private MigHttpService migHttpService;
	private CommunityResourceBundleMessageSource messageSource;
	private CommunityService communityService;

	public void setUserService(UserService userService) {
		if (userService == null)
			throw new NullPointerException("The parameter userService is null");
		this.userService = userService;
	}

	public void setMigHttpService(MigHttpService migHttpService) {
		if (migHttpService == null)
			throw new NullPointerException("The parameter migHttpService is null");
		this.migHttpService = migHttpService;
	}

	public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
		if (messageSource == null)
			throw new NullPointerException("The parameter messageSource is null");
		this.messageSource = messageSource;
	}

	public void setCommunityService(CommunityService communityService) {
		if (communityService == null)
			throw new NullPointerException("The parameter communityService is null");
		this.communityService = communityService;
	}

	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] SmsAccordingToLawJob job starting...");

			LOGGER.debug("input parameters jobExecutionContext: [{}]", jobExecutionContext);
			JobDataMap mergedJobDataMap = jobExecutionContext.getMergedJobDataMap();

			setCommunityService((CommunityService) mergedJobDataMap.get("communityService"));
			setMessageSource((CommunityResourceBundleMessageSource) mergedJobDataMap.get("messageSource"));
			setUserService((UserService) mergedJobDataMap.get("userService"));
			setMigHttpService((MigHttpService) mergedJobDataMap.get("migHttpService"));

			String communityURL = mergedJobDataMap.getString("communityURL");

			if (communityURL == null)
				throw new NullPointerException("The parameter communityURL is null");

			final String upperCaseCommunityName = communityURL.toUpperCase();

			Community community = communityService.getCommunityByUrl(upperCaseCommunityName);

			if (community == null)
				throw new NullPointerException("The parameter community is null");

			final String amountOfMoneyToUserNotificationString = messageSource
					.getMessage(upperCaseCommunityName, "amountOfMoneyToUserNotification", null, null);

			if (amountOfMoneyToUserNotificationString == null)
				throw new NullPointerException("The parameter amountOfMoneyToUserNotificationString is null");

			final String deltaSuccesfullPaymentSmsSendingTimestampMillisString = messageSource.getMessage(upperCaseCommunityName,
					"deltaSuccesfullPaymentSmsSendingTimestampMillis", null, null);
			if (deltaSuccesfullPaymentSmsSendingTimestampMillisString == null)
				throw new NullPointerException("The parameter deltaSuccesfullPaymentSmsSendingTimestampMillisString is null");

			final long deltaSuccesfullPaymentSmsSendingTimestampMillis = Long.parseLong(deltaSuccesfullPaymentSmsSendingTimestampMillisString);
			final BigDecimal amountOfMoneyToUserNotification = new BigDecimal(amountOfMoneyToUserNotificationString);

			List<User> users = userService.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis);
			
			LOGGER.info("Trying to proccess list of [{}] users for communityURL [{}]", users.size(), communityURL);
			
			final String amountOfMoneyToUserNotificationIsReachedMessageCode = "sms.amountOfMoneyToUserNotificationIsReached.text";
			final String deltaSuccesfullPaymentSmsSendingTimestampMillisIsReachedMessageCode = "sms.deltaSuccesfullPaymentSmsSendingTimestampMillis.text";

			for (User user : users) {
				final MigPaymentDetails currentActivePaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();
				final PaymentPolicy paymentPolicy = currentActivePaymentDetails.getPaymentPolicy();
				
				if (user.getAmountOfMoneyToUserNotification().compareTo(amountOfMoneyToUserNotification) >= 0) {
					proccess(upperCaseCommunityName, community, user, currentActivePaymentDetails, paymentPolicy,
							amountOfMoneyToUserNotificationIsReachedMessageCode);
				} else {
					proccess(upperCaseCommunityName, community, user, currentActivePaymentDetails, paymentPolicy,
							deltaSuccesfullPaymentSmsSendingTimestampMillisIsReachedMessageCode);
				}
			}

			LOGGER.info("[DONE] SmsAccordingToLawJob job finished");
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	// TODO Investigate why this method is not in UserNotificationService class
	private User proccess(final String upperCaseCommunityName, Community community, User user, final MigPaymentDetails currentActivePaymentDetails,
			final PaymentPolicy paymentPolicy, final String messageCode) {
		
		String messageToSearch = messageCode;
		if ( user.has4GVideoAudioSubscription() ) {
			messageToSearch = new StringBuilder().append(messageCode).append(".video").toString();
		}
		
		LOGGER
				.debug(
						"input parameters upperCaseCommunityName, community, user, currentActivePaymentDetails, paymentPolicy, messageCode: [{}], [{}], [{}], {{}}, [{}], [{}]",
						new Object[] { upperCaseCommunityName, community, user, currentActivePaymentDetails, paymentPolicy, messageToSearch });
		try {
			LogUtils.putSpecificMDC(user.getUserName(), community.getName());
			
			LOGGER.info("Processing started for user with id [{}], userName [{}], communityName [{}]", new Object[] { user.getId(), user.getUserName(), community.getName() });
			String message = messageSource.getMessage(upperCaseCommunityName, messageToSearch, new Object[] { community.getDisplayName(),
					paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(), paymentPolicy.getShortCode() }, null);

			if ( message == null || message.isEmpty() ) {
//				LOGGER.error("The message for video users is missing in services.properties!!! Key should be [{}]. User without message [{}]", messageToSearch, user.getId());
				throw new RuntimeException("No message found in services.properties file. Key:" + messageToSearch);
			}
			
			MigResponse migResponse = migHttpService.makeFreeSMSRequest(currentActivePaymentDetails.getMigPhoneNumber(), message);
			
			if (migResponse.isSuccessful()) {
				LOGGER
						.info(
								"The request for freeSms sent to MIG about user {} succesfully. The nextSubPayment, status, paymentStatus and subBalance was {}, {}, {}, {} respectively",
								new Object[] { user, user.getNextSubPayment(), user.getStatus(), user.getPaymentStatus(), user.getSubBalance() });
			} else
				throw new Exception(migResponse.getDescriptionError());

			user = userService.resetSmsAccordingToLawAttributes(user);
			LOGGER.info("Processing finished successfully for user with id [{}], userName [{}], communityName [{}]", new Object[] { user.getId(), user.getUserName(), community.getName() });
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LOGGER.error("Processing finished UNSUCCESSFULLY for user with id [{}], userName [{}], communityName [{}]", new Object[] { user.getId(), user.getUserName(), community.getName() });
		} finally {
			LogUtils.removeSpecificMDC();
		}
		LOGGER.info("Output parameter user=[{}]", user);
		return user;
	}

}
