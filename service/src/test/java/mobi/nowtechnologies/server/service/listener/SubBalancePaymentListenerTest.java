package mobi.nowtechnologies.server.service.listener;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.AsyncResult;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
public class SubBalancePaymentListenerTest {

    private SubBalancePaymentListener fixtureSubBalancePaymentListener;
    private PromotionService mockPromotionService;
    private CommunityResourceBundleMessageSource mockCommunityResourceBundleMessageSource;
    private UserService mockUserService;
    private MigHttpService mockMigHttpService;
    private UserNotificationService mockUserNotificationService;

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

        Mockito.doNothing().when(mockUserService).processPaymentSubBalanceCommand(user, submittedPayment);

        Mockito.when(mockUserService.findUsersForItunesInAppSubscription(Mockito.any(User.class), Mockito.anyInt(), Mockito.anyString())).thenReturn(Collections.<User>emptyList());

        Future<Boolean> futureResponse = new AsyncResult<Boolean>(Boolean.TRUE);

        Mockito.when(mockUserNotificationService.notifyUserAboutSuccessfulPayment(user)).thenReturn(futureResponse);
        Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

        fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

        Mockito.verify(mockUserNotificationService).notifyUserAboutSuccessfulPayment(submittedPayment.getUser());
        Mockito.verify(mockUserService).populateAmountOfMoneyToUserNotification(user, submittedPayment);
        Mockito.verify(mockUserService, times(0)).findUsersForItunesInAppSubscription(Mockito.any(User.class), Mockito.anyInt(), Mockito.anyString());
    }


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

        Mockito.doNothing().when(mockUserService).processPaymentSubBalanceCommand(user, submittedPayment);

        Mockito.when(mockUserService.findUsersForItunesInAppSubscription(Mockito.any(User.class), Mockito.anyInt(), Mockito.anyString())).thenReturn(Collections.<User>emptyList());

        Future<Boolean> futurResponse = new AsyncResult<Boolean>(Boolean.FALSE);

        Mockito.when(mockUserNotificationService.notifyUserAboutSuccessfulPayment(user)).thenReturn(futurResponse);
        Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

        fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

        Mockito.verify(mockUserNotificationService).notifyUserAboutSuccessfulPayment(submittedPayment.getUser());
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

        Mockito.doNothing().when(mockUserService).processPaymentSubBalanceCommand(user, submittedPayment);

        Mockito.when(mockUserService.findUsersForItunesInAppSubscription(Mockito.eq(user), Mockito.eq(nextSubPayment), Mockito.eq(appStoreOriginalTransactionId))).thenReturn(users);

        Future<Boolean> futureResponse = new AsyncResult<Boolean>(Boolean.TRUE);

        Mockito.when(mockUserNotificationService.notifyUserAboutSuccessfulPayment(user)).thenReturn(futureResponse);
        Mockito.when(mockUserService.populateAmountOfMoneyToUserNotification(user, submittedPayment)).thenReturn(user);

        fixtureSubBalancePaymentListener.onApplicationEvent(mockPaymentEvent);

        Mockito.verify(mockUserNotificationService, times(0)).notifyUserAboutSuccessfulPayment(user);
        Mockito.verify(mockUserService, times(1)).populateAmountOfMoneyToUserNotification(user, submittedPayment);
        Mockito.verify(mockUserService, times(1)).populateAmountOfMoneyToUserNotification(user2, submittedPayment);
        Mockito.verify(mockUserService, times(1)).findUsersForItunesInAppSubscription(Mockito.eq(user), Mockito.eq(nextSubPayment), Mockito.eq(appStoreOriginalTransactionId));
    }

    @Before
    public void setUp() throws Exception {

        mockPromotionService = Mockito.mock(PromotionService.class);
        mockCommunityResourceBundleMessageSource = Mockito.mock(CommunityResourceBundleMessageSource.class);
        mockUserService = Mockito.mock(UserService.class);
        mockMigHttpService = Mockito.mock(MigHttpService.class);
        mockUserNotificationService = Mockito.mock(UserNotificationService.class);

        fixtureSubBalancePaymentListener = new SubBalancePaymentListener();
        fixtureSubBalancePaymentListener.setPromotionService(mockPromotionService);
        fixtureSubBalancePaymentListener.setUserService(mockUserService);
        fixtureSubBalancePaymentListener.setUserNotificationService(mockUserNotificationService);
    }
}