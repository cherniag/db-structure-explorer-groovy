package mobi.nowtechnologies.server.job;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.MigResponseFactory;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * The class <code>SmsAccordingToLawJobTest</code> contains tests for the class
 * <code>{@link SmsAccordingToLawJob}</code>.
 * 
 * @generatedBy CodePro at 29.08.12 10:04
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
public class SmsAccordingToLawJobTest {
	private static final String DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_VALUE_MESSAGE_CODE = "deltaSuccesfullPaymentSmsSendingTimestampMillis";
	private static final String AMOUNT_OF_MONEY_TO_USER_NOTIFICATION_VALUE_MESSAGE_CODE = "amountOfMoneyToUserNotification";
	private static String AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE_CODE = "sms.amountOfMoneyToUserNotificationIsReached.text";
	private static String DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_REACHED_MESSAGE_CODE = "sms.deltaSuccesfullPaymentSmsSendingTimestampMillis.text";
	private static final String AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE = "AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE";
	private static final String DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_REACHED_MESSAGE = "DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_REACHED_MESSAGE";
	private static final String COMMUNITY_URL = "communityURL";
	private static final String MIG_HTTP_SERVICE = "migHttpService";
	private static final String USER_SERVICE = "userService";
	private static final String MESSAGE_SOURCE = "messageSource";
	private static final String COMMUNITY_SERVICE = "communityService";
	private SmsAccordingToLawJob smsAccordingToLawJobFixture;
	private CommunityService mockCommunityService;
	private CommunityResourceBundleMessageSource mockCommunityResourceBundleMessageSource;
	private UserService mockUserService;
	private MigHttpService mockMigHttpService;
	private JobExecutionContext mockJobExecutionContext;
	private Community mockCommunity;
	
	
	private void mockMessage(String message, final String upperCaseCommunityURL, String messageCode) {
		PowerMockito.when(
				mockCommunityResourceBundleMessageSource.getMessage(Mockito.eq(upperCaseCommunityURL), Mockito.eq(messageCode), Mockito
						.argThat(new ArgumentMatcher<Object[]>() {
							@Override
							public boolean matches(Object argument) {
								assertNull(argument);
								return true;
							}
						}), Mockito.any(Locale.class))).thenReturn(message);
	}

	private void mockMessage(final String upperCaseCommunityURL, String messageCode, final Object[] expectedMessageArgs, String message) {
		PowerMockito.when(
				mockCommunityResourceBundleMessageSource.getMessage(Mockito.eq(upperCaseCommunityURL), Mockito.eq(messageCode), Mockito
						.argThat(new ArgumentMatcher<Object[]>() {
							@Override
							public boolean matches(Object argument) {
								Object[] messageArgs = (Object[]) argument;

								assertEquals(expectedMessageArgs.length, messageArgs.length);
								for (int i = 0; i < expectedMessageArgs.length; i++) {
									assertEquals(expectedMessageArgs[i], messageArgs[i]);
								}

								return true;
							}
						}), Mockito.any(Locale.class))).thenReturn(message);
	}

	private void mockMakeFreeSMSRequest(final MigPaymentDetails currentMigPaymentDetails, String message, MigResponse migResponse) {
		PowerMockito.when(mockMigHttpService.makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), message)).thenReturn(migResponse);
	}

	private JobDataMap getJobDataMap(String communityURL) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put(COMMUNITY_SERVICE, mockCommunityService);
		jobDataMap.put(MESSAGE_SOURCE, mockCommunityResourceBundleMessageSource);
		jobDataMap.put(USER_SERVICE, mockUserService);
		jobDataMap.put(MIG_HTTP_SERVICE, mockMigHttpService);
		jobDataMap.put(COMMUNITY_URL, communityURL);
		return jobDataMap;
	}

	private User createUser(BigDecimal amountOfMoneyToUserNotification) {
		PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

		MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		migPaymentDetails.setPaymentPolicy(paymentPolicy);

		User user = UserFactory.createUser(migPaymentDetails, amountOfMoneyToUserNotification);
		return user;
	}

	/**
	 * Run the void executeInternal(JobExecutionContext) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test
	public void testExecuteInternal_deltaSuccesfullPaymentSmsSendingTimestampMillis_Success() throws Exception {
		String communityURL = "nowtop40";
		String amountOfMoneyToUserNotificationStringConfigValue = "20";
		Long deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue = 321L;
		BigDecimal amountOfMoneyToUserNotification = BigDecimal.TEN;

		JobDataMap jobDataMap = getJobDataMap(communityURL);

		BigDecimal amountOfMoneyToUserNotificationConfigValue = new BigDecimal(amountOfMoneyToUserNotificationStringConfigValue);
		String deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValueString = deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue.toString();
		final String upperCaseCommunityURL = communityURL.toUpperCase();

		User user = createUser(amountOfMoneyToUserNotification);

		List<User> userUnmodifableList = new ArrayList<User>(1);
		userUnmodifableList.add(user);

		userUnmodifableList = Collections.unmodifiableList(userUnmodifableList);

		final MigPaymentDetails currentMigPaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();
		final PaymentPolicy paymentPolicyForCurrentMigPaymentDetails = currentMigPaymentDetails.getPaymentPolicy();

		final Object[] deltaSuccesfullPaymentSmsSendingTimestampMillisReachedMessageArgs = new Object[] { mockCommunity.getDisplayName(),
				paymentPolicyForCurrentMigPaymentDetails.getSubcost(), paymentPolicyForCurrentMigPaymentDetails.getSubweeks(),
				paymentPolicyForCurrentMigPaymentDetails.getShortCode() };
		MigResponse successfullMigResponse = MigResponseFactory.createSuccessfulMigResponse();

		PowerMockito.when(mockJobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);
		PowerMockito.when(mockCommunityService.getCommunityByUrl(Mockito.eq(upperCaseCommunityURL))).thenReturn(mockCommunity);

		mockMessage(amountOfMoneyToUserNotificationStringConfigValue, upperCaseCommunityURL, AMOUNT_OF_MONEY_TO_USER_NOTIFICATION_VALUE_MESSAGE_CODE);
		mockMessage(deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValueString, upperCaseCommunityURL,
				DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_VALUE_MESSAGE_CODE);

		PowerMockito.when(
				mockUserService.findActivePsmsUsers(Mockito.eq(communityURL), Mockito.eq(amountOfMoneyToUserNotificationConfigValue), Mockito
						.eq(deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue))).thenReturn(userUnmodifableList);

		mockMessage(upperCaseCommunityURL, DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_REACHED_MESSAGE_CODE,
				deltaSuccesfullPaymentSmsSendingTimestampMillisReachedMessageArgs, DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_REACHED_MESSAGE);

		mockMakeFreeSMSRequest(currentMigPaymentDetails, DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_REACHED_MESSAGE, successfullMigResponse);

		PowerMockito.when(mockUserService.resetSmsAccordingToLawAttributes(user)).thenReturn(user);

		smsAccordingToLawJobFixture.executeInternal(mockJobExecutionContext);

		Mockito.verify(mockMigHttpService).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(),
				DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_REACHED_MESSAGE);
		Mockito.verify(mockUserService).resetSmsAccordingToLawAttributes(user);
	}

	/**
	 * Run the void executeInternal(JobExecutionContext) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test
	public void testExecuteInternal_amountOfMoneyToUserNotification_Success() throws Exception {
		String communityURL = "nowtop40";
		String amountOfMoneyToUserNotificationStringConfigValue = "20";
		Long deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue = 321L;
		BigDecimal amountOfMoneyToUserNotification = new BigDecimal("20");

		JobDataMap jobDataMap = getJobDataMap(communityURL);

		BigDecimal amountOfMoneyToUserNotificationConfigValue = new BigDecimal(amountOfMoneyToUserNotificationStringConfigValue);
		String deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValueString = deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue.toString();
		final String upperCaseCommunityURL = communityURL.toUpperCase();

		User user = createUser(amountOfMoneyToUserNotification);

		List<User> userUnmodifableList = new ArrayList<User>(1);
		userUnmodifableList.add(user);

		userUnmodifableList = Collections.unmodifiableList(userUnmodifableList);

		final MigPaymentDetails currentMigPaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();
		final PaymentPolicy paymentPolicyForCurrentMigPaymentDetails = currentMigPaymentDetails.getPaymentPolicy();

		final Object[] amountOfMoneyToUserNotificationMessageArgs = new Object[] { mockCommunity.getDisplayName(),
				paymentPolicyForCurrentMigPaymentDetails.getSubcost(), paymentPolicyForCurrentMigPaymentDetails.getSubweeks(),
				paymentPolicyForCurrentMigPaymentDetails.getShortCode() };
		MigResponse successfullMigResponse = MigResponseFactory.createSuccessfulMigResponse();

		PowerMockito.when(mockJobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);
		PowerMockito.when(mockCommunityService.getCommunityByUrl(Mockito.eq(upperCaseCommunityURL))).thenReturn(mockCommunity);

		mockMessage(amountOfMoneyToUserNotificationStringConfigValue, upperCaseCommunityURL, AMOUNT_OF_MONEY_TO_USER_NOTIFICATION_VALUE_MESSAGE_CODE);
		mockMessage(deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValueString, upperCaseCommunityURL,
				DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_VALUE_MESSAGE_CODE);

		PowerMockito.when(
				mockUserService.findActivePsmsUsers(Mockito.eq(communityURL), Mockito.eq(amountOfMoneyToUserNotificationConfigValue), Mockito
						.eq(deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue))).thenReturn(userUnmodifableList);

		mockMessage(upperCaseCommunityURL, AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE_CODE, amountOfMoneyToUserNotificationMessageArgs,
				AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE);

		mockMakeFreeSMSRequest(currentMigPaymentDetails, AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE,  successfullMigResponse);

		PowerMockito.when(mockUserService.resetSmsAccordingToLawAttributes(user)).thenReturn(user);

		smsAccordingToLawJobFixture.executeInternal(mockJobExecutionContext);

		Mockito.verify(mockMigHttpService).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(),
				AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE);
		Mockito.verify(mockUserService).resetSmsAccordingToLawAttributes(user);
	}

	/**
	 * Run the void executeInternal(JobExecutionContext) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test
	public void testExecuteInternal_failureMigResponse_Success() throws Exception {
		String communityURL = "nowtop40";
		String amountOfMoneyToUserNotificationStringConfigValue = "20";
		Long deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue = 321L;
		BigDecimal amountOfMoneyToUserNotification = new BigDecimal("20");

		JobDataMap jobDataMap = getJobDataMap(communityURL);

		BigDecimal amountOfMoneyToUserNotificationConfigValue = new BigDecimal(amountOfMoneyToUserNotificationStringConfigValue);
		String deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValueString = deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue.toString();
		final String upperCaseCommunityURL = communityURL.toUpperCase();

		User user1 = createUser(amountOfMoneyToUserNotification);
		User user2 = createUser(amountOfMoneyToUserNotification);

		List<User> userUnmodifableList = new ArrayList<User>(1);
		userUnmodifableList.add(user1);
		userUnmodifableList.add(user2);

		userUnmodifableList = Collections.unmodifiableList(userUnmodifableList);

		final MigPaymentDetails currentMigPaymentDetails1 = (MigPaymentDetails) user1.getCurrentPaymentDetails();
		final PaymentPolicy paymentPolicyForCurrentMigPaymentDetails1 = currentMigPaymentDetails1.getPaymentPolicy();

		final MigPaymentDetails currentMigPaymentDetails2 = (MigPaymentDetails) user2.getCurrentPaymentDetails();
		final PaymentPolicy paymentPolicyForCurrentMigPaymentDetails2 = currentMigPaymentDetails2.getPaymentPolicy();

		final Object[] amountOfMoneyToUserNotificationMessageArgs1 = new Object[] { mockCommunity.getDisplayName(),
				paymentPolicyForCurrentMigPaymentDetails1.getSubcost(), paymentPolicyForCurrentMigPaymentDetails1.getSubweeks(),
				paymentPolicyForCurrentMigPaymentDetails1.getShortCode() };
		final Object[] amountOfMoneyToUserNotificationMessageArgs2 = new Object[] { mockCommunity.getDisplayName(),
				paymentPolicyForCurrentMigPaymentDetails2.getSubcost(), paymentPolicyForCurrentMigPaymentDetails2.getSubweeks(),
				paymentPolicyForCurrentMigPaymentDetails2.getShortCode() };

		MigResponse failureMigResponse = MigResponseFactory.createFailMigResponse();
		MigResponse successfullMigResponse = MigResponseFactory.createSuccessfulMigResponse();

		PowerMockito.when(mockJobExecutionContext.getMergedJobDataMap()).thenReturn(jobDataMap);
		PowerMockito.when(mockCommunityService.getCommunityByUrl(Mockito.eq(upperCaseCommunityURL))).thenReturn(mockCommunity);

		mockMessage(amountOfMoneyToUserNotificationStringConfigValue, upperCaseCommunityURL, AMOUNT_OF_MONEY_TO_USER_NOTIFICATION_VALUE_MESSAGE_CODE);
		mockMessage(deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValueString, upperCaseCommunityURL,
				DELTA_SUCCESFULL_PAYMENT_SMS_SENDING_TIMESTAMP_MILLIS_VALUE_MESSAGE_CODE);

		PowerMockito.when(
				mockUserService.findActivePsmsUsers(Mockito.eq(communityURL), Mockito.eq(amountOfMoneyToUserNotificationConfigValue), Mockito
						.eq(deltaSuccesfullPaymentSmsSendingTimestampMillisConfigValue))).thenReturn(userUnmodifableList);

		mockMessage(upperCaseCommunityURL, AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE_CODE, amountOfMoneyToUserNotificationMessageArgs1,
				AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE);
		mockMessage(upperCaseCommunityURL, AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE_CODE, amountOfMoneyToUserNotificationMessageArgs2,
				AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE);

		mockMakeFreeSMSRequest(currentMigPaymentDetails1, AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE, failureMigResponse);
		mockMakeFreeSMSRequest(currentMigPaymentDetails2, AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE, successfullMigResponse);

		PowerMockito.when(
				mockMigHttpService.makeFreeSMSRequest(currentMigPaymentDetails2.getMigPhoneNumber(), AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE))
				.thenReturn(successfullMigResponse);

		PowerMockito.when(mockUserService.resetSmsAccordingToLawAttributes(user1)).thenReturn(user1);
		PowerMockito.when(mockUserService.resetSmsAccordingToLawAttributes(user2)).thenReturn(user2);

		smsAccordingToLawJobFixture.executeInternal(mockJobExecutionContext);

		Mockito.verify(mockMigHttpService, Mockito.times(2)).makeFreeSMSRequest(currentMigPaymentDetails1.getMigPhoneNumber(),
				AMOUNT_OF_MONEY_TO_USER_NOTIFICATIONIS_REACHED_MESSAGE);

		Mockito.verify(mockUserService).resetSmsAccordingToLawAttributes(user1);

		Mockito.verify(mockUserService).resetSmsAccordingToLawAttributes(user2);
	}

	/**
	 * Run the void setCommunityService(CommunityService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test
	public void testSetCommunityService_notNull_Success() throws Exception {
		smsAccordingToLawJobFixture.setCommunityService(mockCommunityService);
	}

	/**
	 * Run the void setCommunityService(CommunityService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testSetCommunityService_null_Failure() throws Exception {
		smsAccordingToLawJobFixture.setCommunityService(null);
	}

	/**
	 * Run the void setMessageSource(CommunityResourceBundleMessageSource)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test
	public void testSetMessageSource_notNull_Success() throws Exception {
		smsAccordingToLawJobFixture.setMessageSource(mockCommunityResourceBundleMessageSource);
	}

	/**
	 * Run the void setMessageSource(CommunityResourceBundleMessageSource)
	 * method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testSetMessageSource_null_Failure() throws Exception {
		smsAccordingToLawJobFixture.setMessageSource(null);
	}

	/**
	 * Run the void setMigHttpService(MigHttpService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test
	public void testSetMigHttpService_notNull_Success() throws Exception {
		smsAccordingToLawJobFixture.setMigHttpService(mockMigHttpService);
	}

	/**
	 * Run the void setMigHttpService(MigHttpService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testSetMigHttpService_null_Failure() throws Exception {
		smsAccordingToLawJobFixture.setMigHttpService(null);
	}

	/**
	 * Run the void setUserService(UserService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test
	public void testSetUserService_notNull_Success() throws Exception {
		smsAccordingToLawJobFixture.setUserService(mockUserService);
	}

	/**
	 * Run the void setUserService(UserService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testSetUserService_null_Failure() throws Exception {
		smsAccordingToLawJobFixture.setUserService(null);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 29.08.12 10:04
	 */
	@Before
	public void setUp() throws Exception {
		smsAccordingToLawJobFixture = new SmsAccordingToLawJob();

		mockCommunityService = Mockito.mock(CommunityService.class);
		mockCommunityResourceBundleMessageSource = Mockito.mock(CommunityResourceBundleMessageSource.class);
		mockUserService = Mockito.mock(UserService.class);
		mockMigHttpService = Mockito.mock(MigHttpService.class);

		mockJobExecutionContext = Mockito.mock(JobExecutionContext.class);

		mockCommunity = CommunityFactory.createCommunity();
	}
}