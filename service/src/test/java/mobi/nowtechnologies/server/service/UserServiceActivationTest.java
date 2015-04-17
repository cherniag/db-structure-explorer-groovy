package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class, Utils.class, DeviceTypeCache.class, AccountLog.class, EmailValidator.class})
public class UserServiceActivationTest {

    private UserService userServiceSpy;
    private UserRepository userRepositoryMock;
    private AccountLogService accountLogServiceMock;
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;
    private MigHttpService migHttpServiceMock;
    private PaymentDetailsService paymentDetailsServiceMock;
    private CommunityService communityServiceMock;
    private CountryService countryServiceMock;
    private O2ProviderService o2ClientServiceMock;
    private DevicePromotionsService deviceServiceMock;
    private RefundService refundServiceMock;

    private PromotionService promotionServiceMock;
    private O2UserDetailsUpdater o2UserDetailsUpdaterMock;

    @Before
    public void setUp() throws Exception {
        userServiceSpy = Mockito.spy(new UserService());

        countryServiceMock = PowerMockito.mock(CountryService.class);
        communityResourceBundleMessageSourceMock = PowerMockito.mock(CommunityResourceBundleMessageSource.class);
        userRepositoryMock = PowerMockito.mock(UserRepository.class);
        CountryByIpService countryByIpServiceMock = PowerMockito.mock(CountryByIpService.class);
        paymentDetailsServiceMock = PowerMockito.mock(PaymentDetailsService.class);
        UrbanAirshipTokenService urbanAirshipTokenServiceMock = PowerMockito.mock(UrbanAirshipTokenService.class);
        promotionServiceMock = PowerMockito.mock(PromotionService.class);
        CountryAppVersionService countryAppVersionServiceMock = PowerMockito.mock(CountryAppVersionService.class);
        communityServiceMock = PowerMockito.mock(CommunityService.class);
        deviceServiceMock = PowerMockito.mock(DevicePromotionsService.class);
        migHttpServiceMock = PowerMockito.mock(MigHttpService.class);
        accountLogServiceMock = PowerMockito.mock(AccountLogService.class);
        o2ClientServiceMock = PowerMockito.mock(O2ProviderService.class);
        MailService mailServiceMock = PowerMockito.mock(MailService.class);
        refundServiceMock = PowerMockito.mock(RefundService.class);
        o2UserDetailsUpdaterMock = PowerMockito.mock(O2UserDetailsUpdater.class);

        userServiceSpy.setCountryService(countryServiceMock);
        userServiceSpy.setMessageSource(communityResourceBundleMessageSourceMock);
        userServiceSpy.setCountryByIpService(countryByIpServiceMock);
        userServiceSpy.setPaymentDetailsService(paymentDetailsServiceMock);
        userServiceSpy.setUrbanAirshipTokenService(urbanAirshipTokenServiceMock);
        userServiceSpy.setPromotionService(promotionServiceMock);
        userServiceSpy.setCountryAppVersionService(countryAppVersionServiceMock);
        userServiceSpy.setCommunityService(communityServiceMock);
        userServiceSpy.setDeviceService(deviceServiceMock);
        userServiceSpy.setMigHttpService(migHttpServiceMock);
        userServiceSpy.setAccountLogService(accountLogServiceMock);
        userServiceSpy.userRepository = userRepositoryMock;
        userServiceSpy.setRefundService(refundServiceMock);
        userServiceSpy.setMobileProviderService(o2ClientServiceMock);
        userServiceSpy.setUserDetailsUpdater(o2UserDetailsUpdaterMock);

    }

    @Test
    public void testActivatePhoneNumber_NullPhone_Success_Populate4G() throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
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
