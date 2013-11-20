package mobi.nowtechnologies.server.service.aop;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.service.*;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
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
import mobi.nowtechnologies.server.shared.service.BasicResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/shared.xml", "/META-INF/dao-test.xml", "/META-INF/service-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class SMSNotificationIT {

	@Rule
	public PowerMockRule rule = new PowerMockRule();

	@Autowired
	private SMSNotification smsNotificationFixture;
	
	@Autowired
	@Qualifier("service.WeeklyUpdateService")
	private WeeklyUpdateService weeklyUpdateService;
	
	@Autowired
	@Qualifier("service.SpyUserService")
	private UserService userService;
	
	@Autowired
	@Qualifier("service.SpyEntityService")
	private EntityService entityService;
	
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
	
	private UserService mockUserService;
	
	private UserNotificationService userNotificationServiceMock;
	
	private CommunityResourceBundleMessageSource mockMessageSource;
	
	private BasicResponse successfulResponse = new BasicResponse() {
		@Override
		public int getStatusCode() {
			return HttpServletResponse.SC_OK;
		}
		@Override public String getMessage() {
			return "TOKEN=EC%2d5YJ748178G052312W&TIMESTAMP=2011%2d12%2d23T19%3a40%3a07Z&CORRELATIONID=80d5883fa4b48&ACK=Success&VERSION=80%2e0&BUILD=2271164";
		}
	};

	private O2ProviderService mockO2ClientService;
	
	@Test
	public void testUpdateLastBefore48SmsMillis_Success()
			throws Exception {		
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doNothing().when(userService).updateLastBefore48SmsMillis(anyLong(), anyInt());
		Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		userService.updateLastBefore48SmsMillis(System.currentTimeMillis(), user.getId());
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSendUnsubscribeAfterSMS_Success()
			throws Exception {		
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doReturn(null).when(userService).unsubscribeUser(any(User.class), anyString());
		Mockito.doReturn(user).when(entityService).findById(any(Class.class), any(Object.class));
		Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		UnsubscribeDto unsubscribeDto = new UnsubscribeDto();
		unsubscribeDto.setReason("");
		
		userService.unsubscribeUser(user.getId(), unsubscribeDto);
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendPaymentFail_PayPal_Success()
			throws Exception {		
		PayPalResponse response = new PayPalResponse(successfulResponse);
		
		User user = UserFactory.createUser(new PayPalPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		PaymentDetails paymentDetails = new PayPalPaymentDetails();
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setMadeRetries(3);
		PendingPayment pendingPayment = new PendingPayment();
		pendingPayment.setTimestamp(user.getNextSubPayment()*1000L);
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(paymentDetails);
		
		Mockito.doReturn(null).when(payPalPaymentService).commitPayment(any(PendingPayment.class), any(PayPalResponse.class));
		Mockito.doReturn(pendingPayment).when(entityService).updateEntity(any(PendingPayment.class));
		Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		Mockito.doReturn(response).when(mockPaypalHttpService).makeReferenceTransactionRequest(anyString(), anyString(), any(BigDecimal.class), anyString());
		
		payPalPaymentService.startPayment(pendingPayment);
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendPaymentFail_SagePay_Success()
			throws Exception {		
		SagePayResponse response = new SagePayResponse(successfulResponse);
		
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
		user.setSegment(null);
		user.setProvider(NON_O2);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		SagePayCreditCardPaymentDetails paymentDetails = new SagePayCreditCardPaymentDetails();
		paymentDetails.setReleased(true);
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setMadeRetries(3);
		PendingPayment pendingPayment = new PendingPayment();
		pendingPayment.setTimestamp(user.getNextSubPayment()*1000L);
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(paymentDetails);
		
		Mockito.doReturn(null).when(sagePayPaymentService).commitPayment(any(PendingPayment.class), any(PayPalResponse.class));
		Mockito.doReturn(pendingPayment).when(entityService).updateEntity(any(PendingPayment.class));
		Mockito.doNothing().when(entityService).removeEntity(any(Class.class), any(Object.class));
		Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		Mockito.doReturn(response).when(mockSagePayHttpService).makeReleaseRequest(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class));
		Mockito.doReturn(response).when(mockSagePayHttpService).makeRepeatRequest(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(BigDecimal.class));
		
		sagePayPaymentService.startPayment(pendingPayment);
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendPaymentFail_o2PSMS_Success()
			throws Exception {		
		O2Response response = O2Response.successfulO2Response();
		
		O2PSMSPaymentDetails paymentDetails = new O2PSMSPaymentDetails();
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setMadeRetries(3);
		paymentDetails.setPaymentPolicy(new PaymentPolicy());
		
		User user = UserFactory.createUser(paymentDetails, null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		PendingPayment pendingPayment = new PendingPayment();
		pendingPayment.setTimestamp(user.getNextSubPayment()*1000L);
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(paymentDetails);
		
		Mockito.doReturn(null).when(o2PaymentService).commitPayment(any(PendingPayment.class), any(PayPalResponse.class));
		Mockito.doReturn(pendingPayment).when(entityService).updateEntity(any(PendingPayment.class));
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		Mockito.doReturn("false").when(mockMessageSource).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("sms.o2_psms.send"), any(Object[].class), eq((Locale)null));
		Mockito.doReturn("false").when(mockMessageSource).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("sms.o2_psms.send"), any(Object[].class), eq((Locale)null));
		Mockito.doReturn("falsedfdsfsd").when(mockMessageSource).getMessage(eq(user.getUserGroup().getCommunity().getRewriteUrlParameter()), eq("sms.o2_psms"), any(Object[].class), eq((Locale)null));
		Mockito.doReturn(response).when(mockO2ClientService).makePremiumSMSRequest(anyInt(), anyString(), any(BigDecimal.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());
		
		o2PaymentService.startPayment(pendingPayment);
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_afterCreatedCreditCardPaymentDetails_Success()
			throws Exception {		
		CreditCardDto creditCardDto = new CreditCardDto();
		User user = UserFactory.createUser(new SagePayCreditCardPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doReturn(null).when(paymentDetailsService).createCreditCardPaymentDetails(any(CreditCardDto.class), anyString(), anyInt());
		Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		paymentDetailsService.createCreditCardPaymentDetails(creditCardDto, "O2", user.getId());
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}

	@Test
	public void testSendUnsubscribePotentialSMS_afterCreatedPayPalPaymentDetails_Success()
			throws Exception {		
		User user = UserFactory.createUser(new PayPalPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		int paymentPolicyId = 1;
		
		Mockito.doReturn(null).when(paymentDetailsService).commitPayPalPaymentDetails(anyString(), anyInt(), anyString(), anyInt());
		Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		paymentDetailsService.commitPayPalPaymentDetails("xxxxxxxxxxxxxxxxx", paymentPolicyId, "O2", user.getId());
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}
	
	@Test
	public void testSendUnsubscribePotentialSMS_afterCreatedMigPaymentDetails_Success()
			throws Exception {		
		User user = UserFactory.createUser(new MigPaymentDetails(), null);
		user.getUserGroup().getCommunity().setRewriteUrlParameter("O2");
		
		Mockito.doReturn(null).when(paymentDetailsService).commitMigPaymentDetails(anyString(), anyInt());
		Mockito.doReturn(null).when(mockMigService).makeFreeSMSRequest(anyString(), anyString(), anyString());
		Mockito.doReturn(user).when(mockUserService).findById(anyInt());
		
		paymentDetailsService.commitMigPaymentDetails("xxxxxxxxxxxxxxxxx", user.getId());
		
		verify(mockMigService, times(1)).makeFreeSMSRequest(anyString(), anyString(), anyString());
	}

	@Before
	public void setUp()
			throws Exception {
		mockMigService = mock(MigHttpService.class);
		mockUserService = mock(UserService.class);
		mockPaypalHttpService = mock(PayPalHttpService.class);
		mockSagePayHttpService = mock(SagePayHttpService.class);
		mockMessageSource = mock(CommunityResourceBundleMessageSource.class);
		mockO2ClientService = mock(O2ProviderService.class);
		
		userNotificationServiceMock = mock(UserNotificationService.class);
		
		smsNotificationFixture.setUserService(mockUserService);
		smsNotificationFixture.setUserNotificationService(userNotificationServiceMock);
		
		payPalPaymentService.setEntityService(entityService);
		payPalPaymentService.setHttpService(mockPaypalHttpService);
		
		sagePayPaymentService.setEntityService(entityService);
		sagePayPaymentService.setHttpService(mockSagePayHttpService);
		
		o2PaymentService.setEntityService(entityService);
		o2PaymentService.setMessageSource(mockMessageSource);
		o2PaymentService.setO2ClientService(mockO2ClientService);
	}
}
