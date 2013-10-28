package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.*;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.o2.O2Service;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserService.class, UserStatusDao.class, Utils.class, DeviceTypeDao.class, UserGroupDao.class,
		OperatorDao.class, AccountLog.class, EmailValidator.class })
public class UserServiceActivationTest {

	public static final long EIGHT_WEEKS_MILLIS = 8 * 7 * 24 * 60 * 60 * 1000L;

	public static final int YEAR_SECONDS = 365 * 24 * 60 * 60;
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
	private O2ProviderService o2ClientServiceMock;
	private O2Service o2ServiceMock;
	private DeviceService deviceServiceMock;
	private FacebookService facebookServiceMock;
	private ITunesService iTunesServiceMock;
	private UserBannedRepository userBannedRepositoryMock;
	private RefundService refundServiceMock;

	private PromotionService promotionServiceMock;
    private O2UserDetailsUpdater o2UserDetailsUpdaterMock;

    @Before
	public void setUp() throws Exception {
		userServiceSpy = Mockito.spy(new UserService());

		@SuppressWarnings("deprecation")
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
		o2ClientServiceMock = PowerMockito.mock(O2ProviderService.class);
		o2ServiceMock = PowerMockito.mock(O2Service.class);
		MailService mailServiceMock = PowerMockito.mock(MailService.class);
		iTunesServiceMock = PowerMockito.mock(ITunesService.class);
		userBannedRepositoryMock = PowerMockito.mock(UserBannedRepository.class);
		refundServiceMock = PowerMockito.mock(RefundService.class);
        o2UserDetailsUpdaterMock = PowerMockito.mock(O2UserDetailsUpdater.class);

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
        userServiceSpy.setMobileProviderService(o2ClientServiceMock);
        userServiceSpy.setUserDetailsUpdater(o2UserDetailsUpdaterMock);

		PowerMockito.mockStatic(UserStatusDao.class);
	}

	@Test
	public void testActivatePhoneNumber_NullPhone_Success_Populate4G() throws Exception {
		final User user = UserFactory.createUser();
		final String phoneNumber = "+447870111111";
		final String pin = "111111";
		user.setMobile(phoneNumber);
		user.setUserName(phoneNumber);

		Mockito.when(o2ClientServiceMock.validatePhoneNumber(anyString())).thenAnswer(new Answer<PhoneNumberValidationData>() {
			@Override
			public PhoneNumberValidationData answer(InvocationOnMock invocation) throws Throwable {
				String phone = (String) invocation.getArguments()[0];
				assertEquals(user.getMobile(), phone);
				return new PhoneNumberValidationData().withPhoneNumber(phoneNumber).withPin(pin);
			}
		});
		User userResult = userServiceSpy.activatePhoneNumber(user, phoneNumber);

		assertNotNull(user);
		assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
		assertEquals("+447870111111", userResult.getMobile());
		assertEquals("111111", userResult.getPin());

		verify(userRepositoryMock, times(1)).save(any(User.class));
		verify(o2ClientServiceMock, times(1)).validatePhoneNumber(anyString());
	}

}
