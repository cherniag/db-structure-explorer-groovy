package mobi.nowtechnologies.server.service.payment.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
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
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.service.UserServiceTest.O2_PAYG_CONSUMER_GRACE_DURATION_CODE;

import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserService.class, UserStatusDao.class, Utils.class, DeviceTypeDao.class, UserGroupDao.class, OperatorDao.class, AccountLog.class })
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
		
		o2PaymentServiceImpl.setUserService(userServiceMock);
	}

	@Test
	public void testMustTheAttemptsOfPaymentContinue_LastPaymentTryMillisBeforeNextSubPayment_Success(){		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryMillis((user.getNextSubPayment()-10)*1000L);
		
		Mockito.when(userServiceMock.getGraceDurationSeconds(user)).thenReturn(2*Utils.WEEK_SECONDS);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertTrue(mustTheAttemptsOfPaymentContinue);
		
		Mockito.verify(userServiceMock, Mockito.timeout(1)).getGraceDurationSeconds(user);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_CurrentTimeEqNextSubPayment_Success(){		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryMillis((user.getNextSubPayment()-10)*1000L);
		
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.getEpochSeconds()).thenReturn(user.getNextSubPayment());
		
		Mockito.when(userServiceMock.getGraceDurationSeconds(user)).thenReturn(2*Utils.WEEK_SECONDS);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertTrue(mustTheAttemptsOfPaymentContinue);
				
		Mockito.verify(userServiceMock, Mockito.timeout(1)).getGraceDurationSeconds(user);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_LastPaymentTryMillisEqGracePeriodEnding_Success(){		
		final int graceDurationSeconds = 2*Utils.WEEK_SECONDS;

		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryMillis((user.getNextSubPayment()+graceDurationSeconds)*1000L);
		
		Mockito.when(userServiceMock.getGraceDurationSeconds(user)).thenReturn(graceDurationSeconds);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertTrue(mustTheAttemptsOfPaymentContinue);
				
		Mockito.verify(userServiceMock, Mockito.timeout(1)).getGraceDurationSeconds(user);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_LastPaymentTryMillisAfterGracePeriodEnding_Success(){		
		final int graceDurationSeconds = 2*Utils.WEEK_SECONDS;

		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryMillis((user.getNextSubPayment()+graceDurationSeconds+1)*1000L);
		
		Mockito.when(userServiceMock.getGraceDurationSeconds(user)).thenReturn(graceDurationSeconds);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertFalse(mustTheAttemptsOfPaymentContinue);
		
		Mockito.verify(userServiceMock, Mockito.timeout(1)).getGraceDurationSeconds(user);
	}
	
	@Test
	public void testMustTheAttemptsOfPaymentContinue_graceDurationSecondsIs0_Success(){		
		final int graceDurationSeconds = 0;

		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSegment(CONSUMER);
		user.setContract(Contract.PAYG);
		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		user.setLastPaymentTryMillis((user.getNextSubPayment()+graceDurationSeconds)*1000L);
		
		Mockito.when(userServiceMock.getGraceDurationSeconds(user)).thenReturn(graceDurationSeconds);
		
		boolean mustTheAttemptsOfPaymentContinue = o2PaymentServiceImpl.mustTheAttemptsOfPaymentContinue(user);
		assertFalse(mustTheAttemptsOfPaymentContinue);
		
		Mockito.verify(userServiceMock, Mockito.timeout(1)).getGraceDurationSeconds(user);
	}
}