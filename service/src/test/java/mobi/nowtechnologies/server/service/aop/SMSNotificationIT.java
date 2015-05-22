package mobi.nowtechnologies.server.service.aop;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.http.PayPalHttpService;
import mobi.nowtechnologies.server.service.payment.http.SagePayHttpService;
import mobi.nowtechnologies.server.service.payment.impl.O2PaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.impl.PayPalPaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.impl.SagePayPaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.service.payment.response.PayPalResponse;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.shared.dto.web.payment.CreditCardDto;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.support.http.BasicResponse;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_O2;

import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class SMSNotificationIT {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Autowired
    private SMSNotification smsNotificationFixture;

    @Autowired
    @Qualifier("service.SpyUserService")
    private UserService userService;

    @Autowired
    @Qualifier("service.SpyPaymentDetailsService")
    private PaymentDetailsService paymentDetailsService;

    @Autowired
    @Qualifier("service.SpySagePayPaymentService")
    private SagePayPaymentServiceImpl sagePayPaymentService;

    @Autowired
    @Qualifier("service.SpyPayPalPaymentService")
    private PayPalPaymentServiceImpl payPalPaymentService;

    @Autowired
    @Qualifier("service.SpyO2PaymentService")
    private O2PaymentServiceImpl o2PaymentService;

    private PayPalHttpService mockPaypalHttpService;

    private SagePayHttpService mockSagePayHttpService;

    private MigHttpService mockMigService;

    private UserRepository userRepository;

    private UserNotificationService userNotificationServiceMock;

    private CommunityResourceBundleMessageSource mockMessageSource;

    private BasicResponse successfulResponse = PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK,
                                                                                    "TOKEN=EC%2d5YJ748178G052312W&TIMESTAMP=2011%2d12%2d23T19%3a40%3a07Z&CORRELATIONID=80d5883fa4b48&ACK=Success&VERSION=80%2e0&BUILD=2271164");

    private O2ProviderService mockO2ClientService;

    @Mock
    private PendingPaymentRepository pendingPaymentRepository;

    @Before
    public void setUp() throws Exception {
        mockMigService = mock(MigHttpService.class);
        userRepository = mock(UserRepository.class);
        mockPaypalHttpService = mock(PayPalHttpService.class);
        mockSagePayHttpService = mock(SagePayHttpService.class);
        mockMessageSource = mock(CommunityResourceBundleMessageSource.class);
        mockO2ClientService = mock(O2ProviderService.class);

        userNotificationServiceMock = mock(UserNotificationService.class);

        smsNotificationFixture.setUserRepository(userRepository);
        smsNotificationFixture.setUserNotificationService(userNotificationServiceMock);

        payPalPaymentService.setPendingPaymentRepository(pendingPaymentRepository);
        payPalPaymentService.setHttpService(mockPaypalHttpService);

        sagePayPaymentService.setPendingPaymentRepository(pendingPaymentRepository);
        sagePayPaymentService.setHttpService(mockSagePayHttpService);

        o2PaymentService.setPendingPaymentRepository(pendingPaymentRepository);
        o2PaymentService.setMessageSource(mockMessageSource);
        o2PaymentService.setO2ClientService(mockO2ClientService);
    }

    @Test
    public void testUpdateLastBefore48SmsMillis_Success() throws Exception {
        User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");

        Mockito.doNothing().when(userService).updateLastBefore48SmsMillis(anyLong(), anyInt());
        Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());

        userService.updateLastBefore48SmsMillis(System.currentTimeMillis(), user.getId());

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendUnsubscribeAfterSMS_Success() throws Exception {
        User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");

        Mockito.doReturn(null).when(userService).unsubscribeUser(any(User.class), anyString());
        Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());

        UnsubscribeDto unsubscribeDto = new UnsubscribeDto();
        unsubscribeDto.setReason("");

        userService.unsubscribeUser(user.getId(), unsubscribeDto);

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendPaymentFail_PayPal_Success() throws Exception {
        PayPalResponse response = new PayPalResponse(successfulResponse);

        User user = UserFactory.createUser(new PayPalPaymentDetails(), null);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
        PaymentDetails paymentDetails = new PayPalPaymentDetails();
        paymentDetails.setRetriesOnError(3);
        paymentDetails.withMadeRetries(3);
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setTimestamp(user.getNextSubPayment() * 1000L);
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);

        Mockito.doReturn(null).when(payPalPaymentService).commitPayment(any(PendingPayment.class), any(PayPalResponse.class));
        Mockito.doReturn(pendingPayment).when(pendingPaymentRepository).save(any(PendingPayment.class));
        Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());
        Mockito.doReturn(response).when(mockPaypalHttpService).makePaymentForRecurrentType(anyString(), anyString(), any(BigDecimal.class), anyString());

        payPalPaymentService.startPayment(pendingPayment);

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendPaymentFail_SagePay_Success() throws Exception {
        SagePayResponse response = new SagePayResponse(successfulResponse);

        User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
        user.setSegment(null);
        user.setProvider(NON_O2);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
        SagePayCreditCardPaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails();
        paymentDetails.setReleased(true);
        paymentDetails.setRetriesOnError(3);
        paymentDetails.withMadeRetries(3);
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setTimestamp(user.getNextSubPayment() * 1000L);
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);

        Mockito.doReturn(null).when(sagePayPaymentService).commitPayment(any(PendingPayment.class), any(PayPalResponse.class));
        Mockito.doReturn(pendingPayment).when(pendingPaymentRepository).save(any(PendingPayment.class));
        Mockito.doNothing().when(pendingPaymentRepository).delete(any(Long.class));
        Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());
        Mockito.doReturn(response).when(mockSagePayHttpService).makeReleaseRequest(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class));
        Mockito.doReturn(response).when(mockSagePayHttpService).makeRepeatRequest(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class));

        sagePayPaymentService.startPayment(pendingPayment);

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendPaymentFail_o2PSMS_Success() throws Exception {
        O2Response response = O2Response.successfulO2Response();

        O2PSMSPaymentDetails paymentDetails = new O2PSMSPaymentDetails();
        paymentDetails.setRetriesOnError(3);
        paymentDetails.withMadeRetries(3);
        paymentDetails.setPaymentPolicy(new PaymentPolicy());

        User user = UserFactory.createUser(paymentDetails, null);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");

        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setTimestamp(user.getNextSubPayment() * 1000L);
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);

        Mockito.doReturn(null).when(o2PaymentService).commitPayment(any(PendingPayment.class), any(PayPalResponse.class));
        Mockito.doReturn(pendingPayment).when(pendingPaymentRepository).save(any(PendingPayment.class));
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());
        Mockito.doReturn("false").when(mockMessageSource).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("sms.o2_psms.send"), any(Object[].class), eq((Locale) null));
        Mockito.doReturn("false").when(mockMessageSource).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("sms.o2_psms.send"), any(Object[].class), eq((Locale) null));
        Mockito.doReturn("falsedfdsfsd").when(mockMessageSource).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("sms.o2_psms"), any(Object[].class), eq((Locale) null));
        Mockito.doReturn(response)
               .when(mockO2ClientService)
               .makePremiumSMSRequest(anyInt(), anyString(), any(BigDecimal.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        o2PaymentService.startPayment(pendingPayment);

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendUnsubscribePotentialSMS_afterCreatedCreditCardPaymentDetails_Success() throws Exception {
        CreditCardDto creditCardDto = new CreditCardDto();
        User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");

        Mockito.doReturn(null).when(paymentDetailsService).createCreditCardPaymentDetails(any(CreditCardDto.class), anyString(), anyInt());
        Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());

        paymentDetailsService.createCreditCardPaymentDetails(creditCardDto, "O2", user.getId());

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendUnsubscribePotentialSMS_afterCreatedPayPalPaymentDetails_Success() throws Exception {
        User user = UserFactory.createUser(new PayPalPaymentDetails(), null);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
        int paymentPolicyId = 1;

        Mockito.doReturn(null).when(paymentDetailsService).commitPayPalPaymentDetails(anyString(), anyInt(), anyInt());
        Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());

        paymentDetailsService.commitPayPalPaymentDetails("xxxxxxxxxxxxxxxxx", paymentPolicyId, user.getId());

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

    @Test
    public void testSendUnsubscribePotentialSMS_afterCreatedMigPaymentDetails_Success() throws Exception {
        User user = UserFactory.createUser(new MigPaymentDetails(), null);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");

        Mockito.doReturn(null).when(paymentDetailsService).commitMigPaymentDetails(anyString(), anyInt());
        Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
        Mockito.doReturn(user).when(userRepository).findOne(anyInt());

        paymentDetailsService.commitMigPaymentDetails("xxxxxxxxxxxxxxxxx", user.getId());

        verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
    }

}
