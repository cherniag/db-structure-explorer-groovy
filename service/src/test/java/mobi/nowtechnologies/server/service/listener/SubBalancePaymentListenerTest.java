package mobi.nowtechnologies.server.service.listener;

import static org.mockito.Mockito.times;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.annotation.AsyncResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * The class <code>SubBalancePaymentListenerTest</code> contains tests for the
 * class <code>{@link SubBalancePaymentListener}</code>.
 * 
 * @generatedBy CodePro at 29.08.12 17:11
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(PowerMockRunner.class)
public class SubBalancePaymentListenerTest {
	private SubBalancePaymentListener fixtureSubBalancePaymentListener;
	private PromotionService mockPromotionService;
	private CommunityResourceBundleMessageSource mockCommunityResourceBundleMessageSource;
	private UserService mockUserService;
	private MigHttpService mockMigHttpService;
	private UserNotificationService mockUserNotificationService;

	/**
	 * Run the void setUserService(UserService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 17:11
	 */
	@Test
	public void testOnApplicationEvent_Success() throws Exception {
		PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

		MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		migPaymentDetails.setPaymentPolicy(paymentPolicy);

		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		final BigDecimal amountOfMoneyToUserNotification = BigDecimal.ONE;
		User user = UserFactory.createUser(migPaymentDetails, amountOfMoneyToUserNotification, userGroup);

		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment(1L, user, PaymentDetailsType.FIRST);
		submittedPayment.setAmount(BigDecimal.TEN);

		PaymentEvent mockPaymentEvent = Mockito.mock(PaymentEvent.class);
		
		Mockito.when(mockPaymentEvent.getPayment()).thenReturn(submittedPayment);

		Mockito.doNothing().when(mockUserService).processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);

		Mockito.when(mockUserService.applyInitialPromotion(submittedPayment.getUser())).thenReturn(new AccountCheckDTO());
		Mockito.when(mockUserService.findUsersForItunesInAppSubscription(Mockito.any(User.class), Mockito.anyInt(), Mockito.anyString())).thenReturn(Collections.<User>emptyList());
		// Mockito.doNothing().when(mockPromotionService).applyPromotion(submittedPayment.getUser());

		Future<Boolean> futureResponse = new AsyncResult<Boolean>(Boolean.TRUE);

		Mockito.when(mockUserNotificationService.notifyUserAboutSuccesfullPayment(user)).thenReturn(futureResponse);
		Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

		fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

		Mockito.verify(mockUserNotificationService).notifyUserAboutSuccesfullPayment(submittedPayment.getUser());
		Mockito.verify(mockUserService).populateAmountOfMoneyToUserNotification(user, submittedPayment);
		Mockito.verify(mockUserService, times(0)).findUsersForItunesInAppSubscription(Mockito.any(User.class), Mockito.anyInt(), Mockito.anyString());
	}
	
	/**
	 * Run the void setUserService(UserService) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 29.08.12 17:11
	 */
	@Test
	public void testOnApplicationEvent_failureMigResponse_Success() throws Exception {
		PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

		MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		migPaymentDetails.setPaymentPolicy(paymentPolicy);

		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		final BigDecimal amountOfMoneyToUserNotification = BigDecimal.ONE;
		User user = UserFactory.createUser(migPaymentDetails, amountOfMoneyToUserNotification, userGroup);

		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment(1L, user, PaymentDetailsType.FIRST);
		submittedPayment.setAmount(BigDecimal.TEN);

		PaymentEvent mockPaymentEvent = Mockito.mock(PaymentEvent.class);
		
		Mockito.when(mockPaymentEvent.getPayment()).thenReturn(submittedPayment);

		Mockito.doNothing().when(mockUserService).processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);

		Mockito.when(mockUserService.applyInitialPromotion(submittedPayment.getUser())).thenReturn(new AccountCheckDTO());
		Mockito.when(mockUserService.findUsersForItunesInAppSubscription(Mockito.any(User.class), Mockito.anyInt(), Mockito.anyString())).thenReturn(Collections.<User>emptyList());
		// Mockito.doNothing().when(mockPromotionService).applyPromotion(submittedPayment.getUser());

		Future<Boolean> futurResponse = new AsyncResult<Boolean>(Boolean.FALSE);

		Mockito.when(mockUserNotificationService.notifyUserAboutSuccesfullPayment(user)).thenReturn(futurResponse);
		Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

		fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

		Mockito.verify(mockUserNotificationService).notifyUserAboutSuccesfullPayment(submittedPayment.getUser());
		Mockito.verify(mockUserService).populateAmountOfMoneyToUserNotification(user, submittedPayment);
		Mockito.verify(mockUserService, times(0)).findUsersForItunesInAppSubscription(Mockito.any(User.class), Mockito.anyInt(), Mockito.anyString());
	}
	
	@Test
	public void testOnApplicationEvent_ITunes_Success() throws Exception {
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final int nextSubPayment = 666;
		
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		final BigDecimal amountOfMoneyToUserNotification = BigDecimal.ONE;
		User user = UserFactory.createUser(null, amountOfMoneyToUserNotification, userGroup);
		User user2 = UserFactory.createUser(null, amountOfMoneyToUserNotification, userGroup);
		
		List<User> users = new ArrayList<User>();
		users.add(user);
		users.add(user2);

		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment(1L, user, PaymentDetailsType.FIRST);
		submittedPayment.setAmount(BigDecimal.TEN);
		submittedPayment.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		submittedPayment.setNextSubPayment(nextSubPayment);

		PaymentEvent mockPaymentEvent = Mockito.mock(PaymentEvent.class);
		
		Mockito.when(mockPaymentEvent.getPayment()).thenReturn(submittedPayment);

		Mockito.doNothing().when(mockUserService).processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);

		Mockito.when(mockUserService.applyInitialPromotion(submittedPayment.getUser())).thenReturn(new AccountCheckDTO());
		Mockito.when(mockUserService.findUsersForItunesInAppSubscription(Mockito.eq(user), Mockito.eq(nextSubPayment), Mockito.eq(appStoreOriginalTransactionId))).thenReturn(users);
		// Mockito.doNothing().when(mockPromotionService).applyPromotion(submittedPayment.getUser());

		Future<Boolean> futureResponse = new AsyncResult<Boolean>(Boolean.TRUE);

		Mockito.when(mockUserNotificationService.notifyUserAboutSuccesfullPayment(user)).thenReturn(futureResponse);
		Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

		fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

		Mockito.verify(mockUserNotificationService, times(0)).notifyUserAboutSuccesfullPayment(user);
		Mockito.verify(mockUserService, times(1)).populateAmountOfMoneyToUserNotification(user, submittedPayment);
		Mockito.verify(mockUserService, times(1)).populateAmountOfMoneyToUserNotification(user2, submittedPayment);
		Mockito.verify(mockUserService, times(1)).findUsersForItunesInAppSubscription(Mockito.eq(user), Mockito.eq(nextSubPayment), Mockito.eq(appStoreOriginalTransactionId));
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 29.08.12 17:11
	 */
	@Before
	public void setUp() throws Exception {

		mockPromotionService = Mockito.mock(PromotionService.class);
		mockCommunityResourceBundleMessageSource = Mockito.mock(CommunityResourceBundleMessageSource.class);
		mockUserService = Mockito.mock(UserService.class);
		mockMigHttpService = Mockito.mock(MigHttpService.class);
		mockUserNotificationService = Mockito.mock(UserNotificationService.class);

		fixtureSubBalancePaymentListener = new SubBalancePaymentListener();
		fixtureSubBalancePaymentListener.setPromotionService(mockPromotionService);
		fixtureSubBalancePaymentListener.setMessageSource(mockCommunityResourceBundleMessageSource);
		fixtureSubBalancePaymentListener.setUserService(mockUserService);
		fixtureSubBalancePaymentListener.setMigHttpService(mockMigHttpService);
		fixtureSubBalancePaymentListener.setUserNotificationService(mockUserNotificationService);
	}
}