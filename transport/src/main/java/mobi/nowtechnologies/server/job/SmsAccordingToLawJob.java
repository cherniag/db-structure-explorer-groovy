package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
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

    private final String amountOfMoneyToUserNotificationIsReachedMessageCode = "sms.amountOfMoneyToUserNotificationIsReached.text";
    private  final String deltaSuccesfullPaymentSmsSendingTimestampMillisIsReachedMessageCode = "sms.deltaSuccesfullPaymentSmsSendingTimestampMillis.text";

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

			Community community = communityService.getCommunityByUrl(communityURL);

			if (community == null)
				throw new NullPointerException("The parameter community is null");

			final String amountOfMoneyToUserNotificationString = messageSource
					.getMessage(communityURL, "amountOfMoneyToUserNotification", null, null);

			if (amountOfMoneyToUserNotificationString == null)
				throw new NullPointerException("The parameter amountOfMoneyToUserNotificationString is null");

			final String deltaSuccesfullPaymentSmsSendingTimestampMillisString = messageSource.getMessage(communityURL,
					"deltaSuccesfullPaymentSmsSendingTimestampMillis", null, null);
			if (deltaSuccesfullPaymentSmsSendingTimestampMillisString == null)
				throw new NullPointerException("The parameter deltaSuccesfullPaymentSmsSendingTimestampMillisString is null");

			final long deltaSuccesfullPaymentSmsSendingTimestampMillis = Long.parseLong(deltaSuccesfullPaymentSmsSendingTimestampMillisString);
			final BigDecimal amountOfMoneyToUserNotification = new BigDecimal(amountOfMoneyToUserNotificationString);

			List<User> users = userService.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis);
			
			LOGGER.info("Trying to process list of [{}] users for communityURL [{}]", users.size(), communityURL);
			
			for (User user : users) {
				final MigPaymentDetails currentActivePaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();

                String code = getSmsMessageCode(user, amountOfMoneyToUserNotification);

                proccess(community, user, currentActivePaymentDetails, code);
			}

			LOGGER.info("[DONE] SmsAccordingToLawJob job finished");
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

    // TODO Investigate why this method is not in UserNotificationService class
	private void proccess(Community community, User user, final MigPaymentDetails currentActivePaymentDetails, final String messageToSearch) {
        final PaymentPolicy paymentPolicy = currentActivePaymentDetails.getPaymentPolicy();

		LOGGER
				.debug(
						"input parameters upperCaseCommunityName, community, user, currentActivePaymentDetails, paymentPolicy, messageCode: [{}], [{}], {{}}, [{}], [{}]",
						community, user, currentActivePaymentDetails, paymentPolicy, messageToSearch);
		try {
			LogUtils.putSpecificMDC(user.getUserName(), community.getName());
			
			LOGGER.info("Processing started for user with id [{}], userName [{}], communityName [{}]", user.getId(), user.getUserName(), community.getName());

            String message = getSmsText(community, paymentPolicy, messageToSearch);

			if ( message == null || message.isEmpty() ) {
				LOGGER.error("The message for video users is missing in services.properties!!! Key should be [{}]. The sms message was not sent for user [{}]", messageToSearch, user.getId());
				return;
			}
			
			MigResponse migResponse = migHttpService.makeFreeSMSRequest(currentActivePaymentDetails.getMigPhoneNumber(), message);
			
			if (migResponse.isSuccessful()) {
				LOGGER
						.info(
								"The request for freeSms sent to MIG about user {} successfully. The nextSubPayment, status, paymentStatus and subBalance was {}, {}, {}, {} respectively",
								user, user.getNextSubPayment(), user.getStatus(), user.getPaymentStatus(), user.getSubBalance());
			} else
				throw new Exception(migResponse.getDescriptionError());

			user = userService.resetSmsAccordingToLawAttributes(user);
			LOGGER.info("Processing finished successfully for user with id [{}], userName [{}], communityName [{}]", user.getId(), user.getUserName(), community.getName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LOGGER.error("Processing finished UNSUCCESSFULLY for user with id [{}], userName [{}], communityName [{}]", user.getId(), user.getUserName(), community.getName());
		} finally {
			LogUtils.removeSpecificMDC();
		}
		LOGGER.info("Output parameter user=[{}]", user);
	}

    private String getSmsText(Community community, PaymentPolicy paymentPolicy, String messageToSearch) {
        Period period = paymentPolicy.getPeriod();
        Object[] args = {community.getDisplayName(), paymentPolicy.getSubcost(), period.getDuration(), period.getDurationUnit(), paymentPolicy.getShortCode()};
        final String communityUrl = community.getRewriteUrlParameter().toLowerCase();
        return messageSource.getMessage(communityUrl, messageToSearch, args, null);
    }

    private String getSmsMessageCode(User user, BigDecimal amountOfMoneyToUserNotification) {
        if (user.getAmountOfMoneyToUserNotification().compareTo(amountOfMoneyToUserNotification) >= 0) {
            return amountOfMoneyToUserNotificationIsReachedMessageCode + getSmsMessageKeyVideoPart(user);
        } else {
            return deltaSuccesfullPaymentSmsSendingTimestampMillisIsReachedMessageCode + getSmsMessageKeyVideoPart(user);
        }
    }

    private String getSmsMessageKeyVideoPart(User user) {
        if ( user.has4GVideoAudioSubscription() ) {
            return ".video";
        }
        return "";
    }

}
