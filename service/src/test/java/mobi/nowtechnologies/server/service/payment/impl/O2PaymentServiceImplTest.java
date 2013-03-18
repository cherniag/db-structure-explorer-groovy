package mobi.nowtechnologies.server.service.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;

import java.util.Locale;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AbstractPayment;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.GracePeriod;
import mobi.nowtechnologies.server.persistence.domain.GracePeriodFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.AccountLogService;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.CountryAppVersionService;
import mobi.nowtechnologies.server.service.CountryByIpService;
import mobi.nowtechnologies.server.service.CountryService;
import mobi.nowtechnologies.server.service.DeviceService;
import mobi.nowtechnologies.server.service.DeviceTypeService;
import mobi.nowtechnologies.server.service.DrmService;
import mobi.nowtechnologies.server.service.EntityService;
import mobi.nowtechnologies.server.service.FacebookService;
import mobi.nowtechnologies.server.service.MailService;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.OfferService;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.PaymentPolicyService;
import mobi.nowtechnologies.server.service.PaymentService;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.service.SagePayService;
import mobi.nowtechnologies.server.service.UserDeviceDetailsService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.event.PaymentEvent;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.O2Response;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.service.UserServiceTest.O2_PAYG_CONSUMER_GRACE_DURATION_CODE;
import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserService.class, UserStatusDao.class, Utils.class, DeviceTypeDao.class, UserGroupDao.class, OperatorDao.class, AccountLog.class, SubmittedPayment.class })
public class O2PaymentServiceImplTest {
	
	private UserService userServiceMock;
	private UserRepository mockUserRepository;
	private UserDao mockUserDao;
	private EntityService mockEntityService;
	private AccountLogService mockAccountLogService;
	private CommunityResourceBundleMessageSource mockCommunityResourceBundleMessageSource;
	private MigHttpService mockMigHttpService;
	private PaymentDetailsService mockPaymentDetailsService;
	private CommunityService mockCommunityService;
	private CountryService mockCountryService;
	private O2ClientService mockO2ClientService;
	private DeviceService mockDeviceService;
	private O2PaymentServiceImpl o2PaymentServiceImpl;
	private ApplicationEventPublisher mockApplicationEventPublisher;
	
	@Before
	public void setUp() throws Exception {
		o2PaymentServiceImpl = new O2PaymentServiceImpl();
		
		userServiceMock = Mockito.mock(UserService.class);

		SagePayService mockSagePayService = PowerMockito.mock(SagePayService.class);
		PaymentPolicyService mockPaymentPolicyService = PowerMockito.mock(PaymentPolicyService.class);
		mockCountryService = PowerMockito.mock(CountryService.class);
		mockCommunityResourceBundleMessageSource = PowerMockito.mock(CommunityResourceBundleMessageSource.class);
		DeviceTypeService mockDeviceTypeService = PowerMockito.mock(DeviceTypeService.class);
		mockUserRepository = PowerMockito.mock(UserRepository.class);
		CountryByIpService mockCountryByIpService = PowerMockito.mock(CountryByIpService.class);
		OfferService mockOfferService = PowerMockito.mock(OfferService.class);
		mockPaymentDetailsService = PowerMockito.mock(PaymentDetailsService.class);
		UserDeviceDetailsService mockUserDeviceDetailsService = PowerMockito.mock(UserDeviceDetailsService.class);
		PromotionService mockPromotionService = PowerMockito.mock(PromotionService.class);
		mockUserDao = PowerMockito.mock(UserDao.class);
		CountryAppVersionService mocCountryAppVersionService = PowerMockito.mock(CountryAppVersionService.class);
		mockEntityService = PowerMockito.mock(EntityService.class);
		MigPaymentService mockMigPaymentService = PowerMockito.mock(MigPaymentService.class);
		DrmService mockDrmService = PowerMockito.mock(DrmService.class);
		FacebookService mockFacebookService = PowerMockito.mock(FacebookService.class);
		mockCommunityService = PowerMockito.mock(CommunityService.class);
		mockDeviceService = PowerMockito.mock(DeviceService.class);
		mockMigHttpService = PowerMockito.mock(MigHttpService.class);
		PaymentService mockPaymentService = PowerMockito.mock(PaymentService.class);
		mockAccountLogService = PowerMockito.mock(AccountLogService.class);
		mockO2ClientService = PowerMockito.mock(O2ClientService.class);
		mockUserRepository = PowerMockito.mock(UserRepository.class);
		MailService mockMailService = PowerMockito.mock(MailService.class);

		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", O2_PAYG_CONSUMER_GRACE_DURATION_CODE, null, null)).thenReturn(48*60*60+"");

		PowerMockito.mockStatic(UserStatusDao.class);
		
		mockApplicationEventPublisher = PowerMockito.mock(ApplicationEventPublisher.class);
		
		o2PaymentServiceImpl.setUserService(userServiceMock);
		o2PaymentServiceImpl.setApplicationEventPublisher(mockApplicationEventPublisher);
		o2PaymentServiceImpl.setMessageSource(mockCommunityResourceBundleMessageSource);
		o2PaymentServiceImpl.setO2ClientService(mockO2ClientService);
		o2PaymentServiceImpl.setEntityService(mockEntityService);
	}
	
	
	@Test
	public void testStartPayment_SuccessfullO2Response_Success() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		o2psmsPaymentDetails.setActivated(true);
		
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
		
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleMillis((user.getNextSubPayment()-10)*1000L);
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		
		
		PendingPayment pendingPayment = new PendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(o2psmsPaymentDetails);
		
		final Boolean smsNotify = Boolean.TRUE;
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", "sms.o2_psms.send", null, null)).thenReturn(String.valueOf(smsNotify));
		final String message = "message";
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage(Mockito.eq("o2"), Mockito.eq("sms.o2_psms"), (Object[]) Mockito.any(), (Locale)Mockito.isNull())).thenReturn(message);
		
		final int internalTxId = Integer.MAX_VALUE;

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getBigRandomInt()).thenReturn(internalTxId);
		
		final String externalTxId = String.valueOf(Integer.MIN_VALUE);
		O2Response o2Response = O2Response.successfulO2Response();
		o2Response.setExternalTxId(externalTxId);
		
		Mockito.when(
				mockO2ClientService.makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
						paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue()))
				.thenReturn(o2Response);
		
		Mockito.when(mockEntityService.updateEntity(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

			@Override
			public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
				PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];
				
				assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
				assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());
				
				return passedPendingPayment;
			}
		});
		
		Mockito.when(mockEntityService.updateEntity(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

			@Override
			public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
				O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];
				
				assertEquals(PaymentDetailsStatus.SUCCESSFUL, o2psmsPaymentDetails.getLastPaymentStatus());
				assertTrue(o2psmsPaymentDetails.isActivated());
				
				return o2psmsPaymentDetails;
			}
		});
		
		Mockito.doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());
		
		
		PowerMockito.mock(SubmittedPayment.class);
		final SubmittedPayment submittedPayment = new SubmittedPayment();
		PowerMockito.whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);
		
		Mockito.when(mockEntityService.updateEntity(Mockito.eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

			@Override
			public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
				SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];
				
				assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
				
				return submittedPayment;
			}
		});
		
		final ArgumentMatcher<PaymentEvent> matcher = new ArgumentMatcher<PaymentEvent>() {

			@Override
			public boolean matches(Object argument) {
				PaymentEvent paymentEvent = (PaymentEvent) argument;
				
				assertNotNull(paymentEvent);
				
				final AbstractPayment payment = paymentEvent.getPayment();
				assertNotNull(payment);
				assertEquals(submittedPayment, payment);
				
				return true;
			}
		};
		
		Mockito.doNothing().when(mockApplicationEventPublisher).publishEvent(Mockito.argThat(matcher ));

		o2PaymentServiceImpl.startPayment(pendingPayment);
		
		Mockito.verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage("o2", "sms.o2_psms.send", null, null);
		Mockito.verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage(Mockito.eq("o2"), Mockito.eq("sms.o2_psms"), (Object[]) Mockito.any(), (Locale)Mockito.isNull());
		Mockito.verify(
				mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
						paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue());
		Mockito.verify(mockEntityService, times(1)).updateEntity(pendingPayment);
		Mockito.verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
		Mockito.verify(mockApplicationEventPublisher, times(1)).publishEvent(Mockito.argThat(matcher));
		
	}
	
	@Test
	public void testStartPayment_FailureO2ResponseAndMedeRetriesEqRetriesOnError_Success() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
		
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		o2psmsPaymentDetails.setMadeRetries(Integer.MAX_VALUE);
		o2psmsPaymentDetails.setRetriesOnError(Integer.MAX_VALUE);
		o2psmsPaymentDetails.setActivated(true);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleMillis((user.getNextSubPayment()-10)*1000L);
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		
		
		PendingPayment pendingPayment = new PendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(o2psmsPaymentDetails);
		
		final Boolean smsNotify = Boolean.TRUE;
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", "sms.o2_psms.send", null, null)).thenReturn(String.valueOf(smsNotify));
		final String message = "message";
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage(Mockito.eq("o2"), Mockito.eq("sms.o2_psms"), (Object[]) Mockito.any(), (Locale)Mockito.isNull())).thenReturn(message);
		
		final int internalTxId = Integer.MAX_VALUE;

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getBigRandomInt()).thenReturn(internalTxId);
		
		final String externalTxId = String.valueOf(Integer.MIN_VALUE);
		final O2Response o2Response = O2Response.failO2Response("");
		o2Response.setExternalTxId(externalTxId);
		
		Mockito.when(
				mockO2ClientService.makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
						paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue()))
				.thenReturn(o2Response);
		
		Mockito.when(mockEntityService.updateEntity(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

			@Override
			public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
				PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];
				
				assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
				assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());
				
				return passedPendingPayment;
			}
		});
		
		Mockito.when(mockEntityService.updateEntity(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

			@Override
			public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
				O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];
				
				assertEquals(PaymentDetailsStatus.ERROR, o2psmsPaymentDetails.getLastPaymentStatus());
				assertFalse(o2psmsPaymentDetails.isActivated());
				
				return o2psmsPaymentDetails;
			}
		});
		
		Mockito.doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());
		
		
		PowerMockito.mock(SubmittedPayment.class);
		final SubmittedPayment submittedPayment = new SubmittedPayment();
		PowerMockito.whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);
		
		Mockito.when(mockEntityService.updateEntity(Mockito.eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

			@Override
			public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
				SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];
				
				assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
				assertEquals(o2Response.getDescriptionError(), submittedPayment.getDescriptionError());
				
				return submittedPayment;
			}
		});
		
		final ArgumentMatcher<PaymentEvent> matcher = new ArgumentMatcher<PaymentEvent>() {

			@Override
			public boolean matches(Object argument) {
				PaymentEvent paymentEvent = (PaymentEvent) argument;
				
				assertNotNull(paymentEvent);
				
				final AbstractPayment payment = paymentEvent.getPayment();
				assertNotNull(payment);
				assertEquals(submittedPayment, payment);
				
				return true;
			}
		};
		
		Mockito.doNothing().when(mockApplicationEventPublisher).publishEvent(Mockito.argThat(matcher ));

		o2PaymentServiceImpl.startPayment(pendingPayment);
		
		Mockito.verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage("o2", "sms.o2_psms.send", null, null);
		Mockito.verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage(Mockito.eq("o2"), Mockito.eq("sms.o2_psms"), (Object[]) Mockito.any(), (Locale)Mockito.isNull());
		Mockito.verify(
				mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
						paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue());
		Mockito.verify(mockEntityService, times(1)).updateEntity(pendingPayment);
		Mockito.verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
		Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(matcher));
		
	}
	
	@Test
	public void testStartPayment_FailureO2ResponseAndMedeRetriesNotEqRetriesOnError_Success() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		final O2PSMSPaymentDetails o2psmsPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		final PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
		
		o2psmsPaymentDetails.setPaymentPolicy(paymentPolicy);
		o2psmsPaymentDetails.setMadeRetries(Integer.MIN_VALUE);
		o2psmsPaymentDetails.setRetriesOnError(Integer.MAX_VALUE);
		o2psmsPaymentDetails.setActivated(true);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleMillis((user.getNextSubPayment()-10)*1000L);
		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
		
		
		PendingPayment pendingPayment = new PendingPayment();
		pendingPayment.setUser(user);
		pendingPayment.setPaymentDetails(o2psmsPaymentDetails);
		
		final Boolean smsNotify = Boolean.TRUE;
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", "sms.o2_psms.send", null, null)).thenReturn(String.valueOf(smsNotify));
		final String message = "message";
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage(Mockito.eq("o2"), Mockito.eq("sms.o2_psms"), (Object[]) Mockito.any(), (Locale)Mockito.isNull())).thenReturn(message);
		
		final int internalTxId = Integer.MAX_VALUE;

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getBigRandomInt()).thenReturn(internalTxId);
		
		final String externalTxId = String.valueOf(Integer.MIN_VALUE);
		final O2Response o2Response = O2Response.failO2Response("");
		o2Response.setExternalTxId(externalTxId);
		
		Mockito.when(
				mockO2ClientService.makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
						paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue()))
				.thenReturn(o2Response);
		
		Mockito.when(mockEntityService.updateEntity(pendingPayment)).thenAnswer(new Answer<PendingPayment>() {

			@Override
			public PendingPayment answer(InvocationOnMock invocation) throws Throwable {
				PendingPayment passedPendingPayment = (PendingPayment) invocation.getArguments()[0];
				
				assertEquals(String.valueOf(externalTxId), passedPendingPayment.getExternalTxId());
				assertEquals(String.valueOf(internalTxId), passedPendingPayment.getInternalTxId());
				
				return passedPendingPayment;
			}
		});
		
		Mockito.when(mockEntityService.updateEntity(o2psmsPaymentDetails)).thenAnswer(new Answer<O2PSMSPaymentDetails>() {

			@Override
			public O2PSMSPaymentDetails answer(InvocationOnMock invocation) throws Throwable {
				O2PSMSPaymentDetails o2psmsPaymentDetails = (O2PSMSPaymentDetails) invocation.getArguments()[0];
				
				assertEquals(PaymentDetailsStatus.ERROR, o2psmsPaymentDetails.getLastPaymentStatus());
				assertTrue(o2psmsPaymentDetails.isActivated());
				
				return o2psmsPaymentDetails;
			}
		});
		
		Mockito.doNothing().when(mockEntityService).removeEntity(PendingPayment.class, pendingPayment.getI());
		
		
		PowerMockito.mock(SubmittedPayment.class);
		final SubmittedPayment submittedPayment = new SubmittedPayment();
		PowerMockito.whenNew(SubmittedPayment.class).withNoArguments().thenReturn(submittedPayment);
		
		Mockito.when(mockEntityService.updateEntity(Mockito.eq(submittedPayment))).thenAnswer(new Answer<SubmittedPayment>() {

			@Override
			public SubmittedPayment answer(InvocationOnMock invocation) throws Throwable {
				SubmittedPayment submittedPayment = (SubmittedPayment) invocation.getArguments()[0];
				
				assertEquals(PaymentDetailsStatus.ERROR, submittedPayment.getStatus());
				assertEquals(o2Response.getDescriptionError(), submittedPayment.getDescriptionError());
				
				return submittedPayment;
			}
		});
		
		final ArgumentMatcher<PaymentEvent> matcher = new ArgumentMatcher<PaymentEvent>() {

			@Override
			public boolean matches(Object argument) {
				PaymentEvent paymentEvent = (PaymentEvent) argument;
				
				assertNotNull(paymentEvent);
				
				final AbstractPayment payment = paymentEvent.getPayment();
				assertNotNull(payment);
				assertEquals(submittedPayment, payment);
				
				return true;
			}
		};
		
		Mockito.doNothing().when(mockApplicationEventPublisher).publishEvent(Mockito.argThat(matcher ));

		o2PaymentServiceImpl.startPayment(pendingPayment);
		
		Mockito.verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage("o2", "sms.o2_psms.send", null, null);
		Mockito.verify(mockCommunityResourceBundleMessageSource, times(1)).getMessage(Mockito.eq("o2"), Mockito.eq("sms.o2_psms"), (Object[]) Mockito.any(), (Locale)Mockito.isNull());
		Mockito.verify(
				mockO2ClientService, times(1)).makePremiumSMSRequest(user.getId(), String.valueOf(internalTxId), pendingPayment.getAmount(), o2psmsPaymentDetails.getPhoneNumber(), message,
						paymentPolicy.getContentCategory(), paymentPolicy.getContentType(), paymentPolicy.getContentDescription(), paymentPolicy.getSubMerchantId(), smsNotify.booleanValue());
		Mockito.verify(mockEntityService, times(1)).updateEntity(pendingPayment);
		Mockito.verify(mockEntityService, times(1)).removeEntity(PendingPayment.class, pendingPayment.getI());
		Mockito.verify(mockApplicationEventPublisher, times(0)).publishEvent(Mockito.argThat(matcher));
		
	}

	@Test
	public void testMustTheAttemptsOfPaymentContinue_LastPaymentTryMillisBeforeNextSubPayment_Success(){		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		final GracePeriod gracePeriod = GracePeriodFactory.createGracePeriod();
		
		gracePeriod.setDurationMillis(2*Utils.WEEK_SECONDS*1000L);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleMillis((user.getNextSubPayment()-10)*1000L);
		user.setGracePeriod(gracePeriod);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertTrue(mustTheAttemptsOfPaymentContinue);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_CurrentTimeEqNextSubPayment_Success(){		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		final GracePeriod gracePeriod = GracePeriodFactory.createGracePeriod();
		
		gracePeriod.setDurationMillis(2*Utils.WEEK_SECONDS*1000L);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleMillis((user.getNextSubPayment()-10)*1000L);
		user.setGracePeriod(gracePeriod);
		
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getEpochSeconds()).thenReturn(user.getNextSubPayment());
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertTrue(mustTheAttemptsOfPaymentContinue);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_LastPaymentTryMillisEqGracePeriodEnding_Success(){		
		final int graceDurationSeconds = 2*Utils.WEEK_SECONDS;

		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		final GracePeriod gracePeriod = GracePeriodFactory.createGracePeriod();
		
		gracePeriod.setDurationMillis(graceDurationSeconds*1000L);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleSeconds(user.getNextSubPayment() + graceDurationSeconds);
		user.setGracePeriod(gracePeriod);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertFalse(mustTheAttemptsOfPaymentContinue);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_LastPaymentTryMillisAfterGracePeriodEnding_Success(){		
		final int graceDurationSeconds = 2*Utils.WEEK_SECONDS;

		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		final GracePeriod gracePeriod = GracePeriodFactory.createGracePeriod();
		
		gracePeriod.setDurationMillis(graceDurationSeconds*1000L);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleMillis((user.getNextSubPayment()+graceDurationSeconds+1)*1000L);
		user.setGracePeriod(gracePeriod);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertFalse(mustTheAttemptsOfPaymentContinue);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_graceDurationSecondsIs0_Success(){		
		final int graceDurationSeconds = 0;

		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		final GracePeriod gracePeriod = GracePeriodFactory.createGracePeriod();
		
		gracePeriod.setDurationMillis(graceDurationSeconds*1000L);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryInCycleMillis((user.getNextSubPayment()+graceDurationSeconds)*1000L);
		user.setGracePeriod(gracePeriod);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertFalse(mustTheAttemptsOfPaymentContinue);
	}
}