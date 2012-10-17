package mobi.nowtechnologies.server.service.listener;

import java.math.BigDecimal;
import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.MigResponseFactory;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.annotation.AsyncResult;

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
		// Mockito.doNothing().when(mockPromotionService).applyPromotion(submittedPayment.getUser());

		Future<Boolean> futureResponse = new AsyncResult<Boolean>(Boolean.TRUE);

		Mockito.when(mockUserNotificationService.notifyUserAboutSuccesfullPayment(user)).thenReturn(futureResponse);
		Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

		fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

		Mockito.verify(mockUserNotificationService).notifyUserAboutSuccesfullPayment(submittedPayment.getUser());
		Mockito.verify(mockUserService).populateAmountOfMoneyToUserNotification(user, submittedPayment);
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
		// Mockito.doNothing().when(mockPromotionService).applyPromotion(submittedPayment.getUser());

		Future<Boolean> futurResponse = new AsyncResult<Boolean>(Boolean.FALSE);

		Mockito.when(mockUserNotificationService.notifyUserAboutSuccesfullPayment(user)).thenReturn(futurResponse);
		Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

		fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

		Mockito.verify(mockUserNotificationService).notifyUserAboutSuccesfullPayment(submittedPayment.getUser());
		Mockito.verify(mockUserService).populateAmountOfMoneyToUserNotification(user, submittedPayment);
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