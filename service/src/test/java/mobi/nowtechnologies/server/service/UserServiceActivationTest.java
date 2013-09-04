package mobi.nowtechnologies.server.service;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.*;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.*;
import static mobi.nowtechnologies.server.shared.enums.Tariff.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserService.class, UserStatusDao.class, Utils.class, DeviceTypeDao.class, UserGroupDao.class,
		OperatorDao.class, AccountLog.class, EmailValidator.class })
public class UserServiceActivationTest {

	private UserService userServiceSpy;
	private UserRepository userRepositoryMock;
	private UserDao userDaoMock;
	private EntityService entityServiceMock;
	private AccountLogService accountLogServiceMock;
	private CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;
	private MigHttpService migHttpServiceMock;
	private PaymentDetailsService paymentDetailsServiceMock;
	private CommunityService communityServiceMock;
	private CountryService countryServiceMock;
	private O2ClientService o2ClientServiceMock;
	private O2Service o2ServiceMock;
	private DeviceService deviceServiceMock;
	private FacebookService facebookServiceMock;
	private ITunesService iTunesServiceMock;
	private UserBannedRepository userBannedRepositoryMock;
	private RefundService refundServiceMock;

	private PromotionService promotionServiceMock;

	@Before
	public void setUp() throws Exception {
		userServiceSpy = Mockito.spy(new UserService());

		@SuppressWarnings("deprecation")
		SagePayService sagePayServiceMock = PowerMockito.mock(SagePayService.class);
		PaymentPolicyService paymentPolicyServiceMock = PowerMockito.mock(PaymentPolicyService.class);
		countryServiceMock = PowerMockito.mock(CountryService.class);
		communityResourceBundleMessageSourceMock = PowerMockito.mock(CommunityResourceBundleMessageSource.class);
		DeviceTypeService deviceTypeServiceMock = PowerMockito.mock(DeviceTypeService.class);
		userRepositoryMock = PowerMockito.mock(UserRepository.class);
		CountryByIpService countryByIpServiceMock = PowerMockito.mock(CountryByIpService.class);
		OfferService offerServiceMock = PowerMockito.mock(OfferService.class);
		paymentDetailsServiceMock = PowerMockito.mock(PaymentDetailsService.class);
		UserDeviceDetailsService userDeviceDetailsServiceMock = PowerMockito.mock(UserDeviceDetailsService.class);
		promotionServiceMock = PowerMockito.mock(PromotionService.class);
		userDaoMock = PowerMockito.mock(UserDao.class);
		CountryAppVersionService countryAppVersionServiceMock = PowerMockito.mock(CountryAppVersionService.class);
		entityServiceMock = PowerMockito.mock(EntityService.class);
		MigPaymentService migPaymentServiceMock = PowerMockito.mock(MigPaymentService.class);
		DrmService drmServiceMock = PowerMockito.mock(DrmService.class);
		facebookServiceMock = PowerMockito.mock(FacebookService.class);
		communityServiceMock = PowerMockito.mock(CommunityService.class);
		deviceServiceMock = PowerMockito.mock(DeviceService.class);
		migHttpServiceMock = PowerMockito.mock(MigHttpService.class);
		PaymentService paymentServiceMock = PowerMockito.mock(PaymentService.class);
		accountLogServiceMock = PowerMockito.mock(AccountLogService.class);
		o2ClientServiceMock = PowerMockito.mock(O2ClientService.class);
		o2ServiceMock = PowerMockito.mock(O2Service.class);
		MailService mailServiceMock = PowerMockito.mock(MailService.class);
		iTunesServiceMock = PowerMockito.mock(ITunesService.class);
		userBannedRepositoryMock = PowerMockito.mock(UserBannedRepository.class);
		refundServiceMock = PowerMockito.mock(RefundService.class);

		userServiceSpy.setSagePayService(sagePayServiceMock);
		userServiceSpy.setPaymentPolicyService(paymentPolicyServiceMock);
		userServiceSpy.setCountryService(countryServiceMock);
		userServiceSpy.setMessageSource(communityResourceBundleMessageSourceMock);
		userServiceSpy.setDeviceTypeService(deviceTypeServiceMock);
		userServiceSpy.setUserRepository(userRepositoryMock);
		userServiceSpy.setCountryByIpService(countryByIpServiceMock);
		userServiceSpy.setOfferService(offerServiceMock);
		userServiceSpy.setPaymentDetailsService(paymentDetailsServiceMock);
		userServiceSpy.setUserDeviceDetailsService(userDeviceDetailsServiceMock);
		userServiceSpy.setPromotionService(promotionServiceMock);
		userServiceSpy.setUserDao(userDaoMock);
		userServiceSpy.setCountryAppVersionService(countryAppVersionServiceMock);
		userServiceSpy.setEntityService(entityServiceMock);
		userServiceSpy.setMigPaymentService(migPaymentServiceMock);
		userServiceSpy.setDrmService(drmServiceMock);
		userServiceSpy.setFacebookService(facebookServiceMock);
		userServiceSpy.setCommunityService(communityServiceMock);
		userServiceSpy.setDeviceService(deviceServiceMock);
		userServiceSpy.setMigHttpService(migHttpServiceMock);
		userServiceSpy.setPaymentService(paymentServiceMock);
		userServiceSpy.setAccountLogService(accountLogServiceMock);
		userServiceSpy.setMailService(mailServiceMock);
		userServiceSpy.setO2ClientService(o2ClientServiceMock);
		userServiceSpy.setO2Service(o2ServiceMock);
		userServiceSpy.setUserRepository(userRepositoryMock);
		userServiceSpy.setiTunesService(iTunesServiceMock);
		userServiceSpy.setUserBannedRepository(userBannedRepositoryMock);
		userServiceSpy.setRefundService(refundServiceMock);

		PowerMockito.mockStatic(UserStatusDao.class);
	}

	@Test
	public void testActivatePhoneNumber_NullPhone_Success_Populate4G() throws Exception {
		final User user = UserFactory.createUser();
		final String phoneNumber = "+447870111111";
		user.setMobile(phoneNumber);
		user.setUserName(phoneNumber);

		Mockito.when(o2ClientServiceMock.validatePhoneNumber(anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				String phone = (String) invocation.getArguments()[0];
				assertEquals(user.getMobile(), phone);
				return phoneNumber;
			}
		});

		final O2SubscriberData o2SubscriberData = new O2SubscriberData();
		o2SubscriberData.setProviderO2(true);
		o2SubscriberData.setContractPostPayOrPrePay(true);
		o2SubscriberData.setTariff4G(false);

		Mockito.when(o2ServiceMock.getSubscriberData(anyString())).thenAnswer(new Answer<O2SubscriberData>() {
			@Override
			public O2SubscriberData answer(InvocationOnMock invocation) throws Throwable {

				String phone = (String) invocation.getArguments()[0];
				assertEquals(user.getMobile(), phone);

				return o2SubscriberData;
			}
		});

		boolean populateO2SubscriberData = true;
		User userResult = userServiceSpy.activatePhoneNumber(user, phoneNumber, populateO2SubscriberData);

		assertNotNull(user);
		assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
		assertEquals("+447870111111", userResult.getMobile());

		verify(userRepositoryMock, times(1)).save(any(User.class));
		verify(o2ClientServiceMock, times(1)).validatePhoneNumber(anyString());
		verify(o2ServiceMock, times(1)).getSubscriberData(anyString());
		assertEquals(user.getSegment(), CONSUMER);
		assertEquals(user.getProvider(), O2);
		assertEquals(user.getTariff(), _3G);

	}

}
