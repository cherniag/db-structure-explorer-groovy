package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.device.domain.DeviceTypeFactory;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.Country;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.OperatorRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.service.data.PhoneNumberValidationData;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.o2.impl.O2UserDetailsUpdater;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.MigResponseFactory;
import mobi.nowtechnologies.server.shared.Processor;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDtoFactory;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService;
import static mobi.nowtechnologies.common.util.DateTimeUtils.newDate;
import static mobi.nowtechnologies.server.persistence.domain.Community.VF_NZ_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.SubmittedPaymentFactory.createSubmittedPayment;
import static mobi.nowtechnologies.server.persistence.domain.UserStatusFactory.createUserStatus;
import static mobi.nowtechnologies.server.shared.Utils.WEEK_SECONDS;
import static mobi.nowtechnologies.server.shared.Utils.createStoredToken;
import static mobi.nowtechnologies.server.shared.Utils.getEpochMillis;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.Utils.getMonthlyNextSubPayment;
import static mobi.nowtechnologies.server.shared.Utils.getNewNextSubPayment;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.USER_DOWNGRADED_TARIFF;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ENTERED_NUMBER;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.REGISTERED;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.DIRECT;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.INDIRECT;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.BOUGHT_PERIOD_SKIPPING;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.CARD_TOP_UP;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.SUBSCRIPTION_CHARGE;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.SUPPORT_TOPUP;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.TRIAL_SKIPPING;
import static mobi.nowtechnologies.server.shared.enums.TransactionType.TRIAL_TOPUP;
import static mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED;
import static mobi.nowtechnologies.server.user.autooptin.AutoOptInRuleService.AutoOptInTriggerType.ALL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import org.joda.time.DateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({UserService.class, Utils.class, DeviceTypeCache.class, AccountLog.class, EmailValidator.class})
public class UserServiceTest {

    public static final int YEAR_SECONDS = 365 * 24 * 60 * 60;
    private static final String SMS_SUCCESFULL_PAYMENT_TEXT = "SMS_SUCCESFULL_PAYMENT_TEXT";
    private static final String SMS_SUCCESFULL_PAYMENT_TEXT_MESSAGE_CODE = "sms.succesfullPayment.text";
    private static final String UNSUBSCRIBED_BY_ADMIN = "Unsubscribed by admin";
    @Mock
    AutoOptInRuleService autoOptInRuleServiceMock;
    Answer userWithPromoAnswer;
    Answer userWithoutPromoAnswer;
    DeviceUserDataService deviceUserDataService;
    @Mock
    AppsFlyerDataService appsFlyerDataService;
    @Mock
    UserActivationStatusService userActivationStatusService;
    @Mock
    OperatorRepository operatorRepository;
    @Mock
    PromotionRepository promotionRepository;
    @Mock
    UserGroupRepository userGroupRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserStatusRepository userStatusRepository;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    TimeService timeServiceMock;

    private UserService userServiceSpy;
    private AccountLogService accountLogServiceMock;
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSourceMock;
    private MigHttpService migHttpServiceMock;
    private PaymentDetailsService paymentDetailsServiceMock;
    private CommunityService communityServiceMock;
    private CountryService countryServiceMock;
    private O2ProviderService o2ClientServiceMock;
    private DevicePromotionsService deviceServiceMock;
    private RefundService refundServiceMock;
    private User actualUser;
    private User user;
    private Tariff paymentPolicyTariff;
    private Tariff newUserTariff;
    private MediaType mediaType;
    private Long currentTimeMillis;
    private int nextSubPayment;
    private Long freeTrialExpiredMillis;
    private long freeTrialStartedTimestampMillis;
    private O2PSMSPaymentDetails lastSuccessfulPaymentDetails;
    private Tariff lastSuccessfulPaymentPolicyTariff;
    private MediaType lastSuccessfulPaymentPolicyMediaType;
    private Tariff currentUserTariff;
    private int currentTimeSeconds;
    private PromotionService promotionServiceMock;
    private UserServiceNotification userServiceNotification;
    private O2UserDetailsUpdater o2UserDetailsUpdaterMock;
    private OtacValidationService otacValidationServiceMock;
    private UrbanAirshipTokenService urbanAirshipTokenServiceMock;
    private TaskService taskService;

    @Before
    public void setUp() throws Exception {
        userServiceSpy = Mockito.spy(new UserService());

        countryServiceMock = PowerMockito.mock(CountryService.class);
        communityResourceBundleMessageSourceMock = PowerMockito.mock(CommunityResourceBundleMessageSource.class);
        CountryByIpService countryByIpServiceMock = PowerMockito.mock(CountryByIpService.class);
        paymentDetailsServiceMock = PowerMockito.mock(PaymentDetailsService.class);
        paymentDetailsServiceMock.paymentDetailsRepository = paymentDetailsRepository;
        promotionServiceMock = PowerMockito.mock(PromotionService.class);
        CountryAppVersionService countryAppVersionServiceMock = PowerMockito.mock(CountryAppVersionService.class);
        communityServiceMock = PowerMockito.mock(CommunityService.class);
        deviceServiceMock = PowerMockito.mock(DevicePromotionsService.class);
        migHttpServiceMock = PowerMockito.mock(MigHttpService.class);
        accountLogServiceMock = PowerMockito.mock(AccountLogService.class);
        o2ClientServiceMock = PowerMockito.mock(O2ProviderService.class);
        MailService mailServiceMock = PowerMockito.mock(MailService.class);
        refundServiceMock = PowerMockito.mock(RefundService.class);
        userServiceNotification = PowerMockito.mock(UserServiceNotification.class);
        otacValidationServiceMock = PowerMockito.mock(OtacValidationService.class);

        o2UserDetailsUpdaterMock = PowerMockito.mock(O2UserDetailsUpdater.class);
        urbanAirshipTokenServiceMock = PowerMockito.mock(UrbanAirshipTokenService.class);
        deviceUserDataService = PowerMockito.mock(DeviceUserDataService.class);
        taskService = PowerMockito.mock(TaskService.class);

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

        userServiceSpy.setRefundService(refundServiceMock);
        userServiceSpy.setUserServiceNotification(userServiceNotification);
        userServiceSpy.setO2UserDetailsUpdater(o2UserDetailsUpdaterMock);
        userServiceSpy.setOtacValidationService(otacValidationServiceMock);


        userServiceSpy.setUserDetailsUpdater(o2UserDetailsUpdaterMock);
        userServiceSpy.setMobileProviderService(o2ClientServiceMock);
        userServiceSpy.setTaskService(taskService);
        userServiceSpy.setAutoOptInRuleService(autoOptInRuleServiceMock);
        userServiceSpy.setDeviceUserDataService(deviceUserDataService);
        userServiceSpy.setAppsFlyerDataService(appsFlyerDataService);
        userServiceSpy.setUserActivationStatusService(userActivationStatusService);

        userServiceSpy.userGroupRepository = userGroupRepository;
        userServiceSpy.promotionRepository = promotionRepository;
        userServiceSpy.operatorRepository = operatorRepository;
        userServiceSpy.userRepository = userRepository;
        userServiceSpy.userStatusRepository = userStatusRepository;
        userServiceSpy.setTimeService(timeServiceMock);

        userWithPromoAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return ((User) invocation.getArguments()[0]).withIsPromotionApplied(true);
            }
        };

        userWithoutPromoAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return ((User) invocation.getArguments()[0]).withIsPromotionApplied(false);
            }
        };
    }

    @Test
    public void testChangePassword_Success() throws Exception {
        String password = "newPa$$1";

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        String storedToken = createStoredToken(user.getUserName(), password);

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        PowerMockito.when(userRepository.updateFields(Mockito.eq(storedToken), Mockito.eq(user.getId()))).thenReturn(1);

        User result = userServiceSpy.changePassword(user.getId(), password);

        assertNotNull(result);
        assertEquals(result, user);

        verify(userRepository, times(1));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = Exception.class)
    public void testChangePassword_Error() throws Exception {
        String password = "newPa$$1";

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        String storedToken = createStoredToken(user.getUserName(), password);

        Mockito.when(userRepository.findOne(user.getId())).thenReturn(user);
        PowerMockito.when(userRepository.updateFields(Mockito.eq(storedToken), Mockito.eq(user.getId()))).thenThrow(new Exception());

        userServiceSpy.changePassword(user.getId(), password);
    }

    @Test
    public void testFindUsers_Success() throws Exception {
        String searchWords = "Led Zeppelin";
        String communityURL = "nowtop40";

        List<User> mockedUserCollection = UserFactory.getUserUnmodifableList();

        PowerMockito.when(userRepository.findUser(Mockito.eq(communityURL), Mockito.eq("%" + searchWords + "%"))).thenReturn(mockedUserCollection);

        Collection<User> result = userServiceSpy.findUsers(searchWords, communityURL);

        assertNotNull(result);
        assertEquals(mockedUserCollection, result);
    }

    @Test(expected = NullPointerException.class)
    public void testFindUsers_searchWordsIsNull_Failure() throws Exception {
        String searchWords = null;
        String communityURL = "nowtop40";

        userServiceSpy.findUsers(searchWords, communityURL);
    }

    @Test(expected = NullPointerException.class)
    public void testFindUsers_communityURLIsNull_Failure() throws Exception {
        String searchWords = "Led Zeppelin";
        String communityURL = null;

        userServiceSpy.findUsers(searchWords, communityURL);
    }

    @Test(expected = RuntimeException.class)
    public void testFindUsers_UserRepository_findUser_RuntimeException_Failure() throws Exception {
        String searchWords = "Led Zeppelin";
        String communityURL = "nowtop40";

        List<User> mockedUserCollection = UserFactory.getUserUnmodifableList();

        PowerMockito.when(userRepository.findUser(Mockito.eq(communityURL), Mockito.eq("%" + searchWords + "%"))).thenThrow(new RuntimeException());

        Collection<User> result = userServiceSpy.findUsers(searchWords, communityURL);

        assertNotNull(result);
        assertEquals(mockedUserCollection, result);
    }

    @Test
    public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentInTheFutureAndSubBalanceIsChangedAndIsFreeTrialIsTrue_Success() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;
        int nextSubPayment = Utils.getEpochSeconds() + 24 * 60 * 60;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(3);
        userDto.setNextSubPayment(new Date(nextSubPayment * 1000L + 200000L));
        userDto.setPaymentEnabled(false);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(nextSubPayment);
        mockedUser.setFreeTrialExpiredMillis(new Long(nextSubPayment * 1000L));
        mockedUser.setLastSuccessfulPaymentTimeMillis(0L);

        PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        mockedUser.setCurrentPaymentDetails(paymentDetails);

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TRIAL_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        User actualUser = userServiceSpy.updateUser(userDto);

        assertNotNull(actualUser);
        assertEquals(mockedUser, actualUser);

        assertEquals(mockedUserStatus, actualUser.getStatus());
        assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
        assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
        assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

        verify(accountLogServiceMock).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TRIAL_TOPUP);
        verify(accountLogServiceMock).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test
    public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentInTheFutureAndSubBalanceIsChangedAndIsFreeTrialIsFalse_Success() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(3);
        userDto.setNextSubPayment(new Date());
        userDto.setPaymentEnabled(false);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(0);

        PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        mockedUser.setCurrentPaymentDetails(paymentDetails);

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        User actualUser = userServiceSpy.updateUser(userDto);

        assertNotNull(actualUser);
        assertEquals(mockedUser, actualUser);

        assertEquals(mockedUserStatus, actualUser.getStatus());
        assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
        assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
        assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

        verify(accountLogServiceMock).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test
    public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentIsTheSameAndSubBalanceIsChangedAndIsFreeTrialIsFalse_Success() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;
        final int nextSubPayment = 5;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(3);
        userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
        userDto.setPaymentEnabled(false);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(nextSubPayment);

        PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        mockedUser.setCurrentPaymentDetails(paymentDetails);

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        User actualUser = userServiceSpy.updateUser(userDto);

        assertNotNull(actualUser);
        assertEquals(mockedUser, actualUser);

        assertEquals(mockedUserStatus, actualUser.getStatus());
        assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
        assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
        assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test
    public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentIsTheSameAndSubBalanceIsTheSameAndIsFreeTrialIsTrue_Success() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;
        final int nextSubPayment = 5;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(originalSubBalance);
        userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
        userDto.setPaymentEnabled(false);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(nextSubPayment);
        mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());

        PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        mockedUser.setCurrentPaymentDetails(paymentDetails);

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        User actualUser = userServiceSpy.updateUser(userDto);

        assertNotNull(actualUser);
        assertEquals(mockedUser, actualUser);

        assertEquals(mockedUserStatus, actualUser.getStatus());
        assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
        assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
        assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test
    public void testUpdateUser_PaymentEnabledIsTrueAndNextSubPaymentIsTheSameAndSubBalanceIsTheSameAndIsFreeTrialIsFalse_Success() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;
        final int nextSubPayment = 5;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(originalSubBalance);
        userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
        userDto.setPaymentEnabled(true);

        PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        paymentDetails.setActivated(true);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(nextSubPayment);
        mockedUser.setCurrentPaymentDetails(paymentDetails);

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        User actualUser = userServiceSpy.updateUser(userDto);

        assertNotNull(actualUser);
        assertEquals(mockedUser, actualUser);

        assertEquals(mockedUserStatus, actualUser.getStatus());
        assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
        assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
        assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test
    public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentIsTheSameAndSubBalanceIsTheSameAndIsFreeTrialIsTrueAndCurrentPaymentDetailsIsNull_Success() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;
        final int nextSubPayment = 5;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(originalSubBalance);
        userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
        userDto.setPaymentEnabled(false);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(nextSubPayment);

        mockedUser.setCurrentPaymentDetails(null);

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        User actualUser = userServiceSpy.updateUser(userDto);

        assertNotNull(actualUser);
        assertEquals(mockedUser, actualUser);

        assertEquals(mockedUserStatus, actualUser.getStatus());
        assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
        assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
        assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test(expected = ServiceException.class)
    public void testUpdateUser_NextSubPaymentIsMoreThanOriginal_Failure() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(3);
        userDto.setNextSubPayment(new Date(2L));
        userDto.setPaymentEnabled(false);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(30000000);
        mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        userServiceSpy.updateUser(userDto);

        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock, times(1)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test(expected = ServiceException.class)
    public void testUpdateUser_UserIsNull_Failure() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(3);
        userDto.setNextSubPayment(new Date());
        userDto.setPaymentEnabled(false);

        User mockedUser = null;

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        userServiceSpy.updateUser(userDto);

        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test(expected = ServiceException.class)
    public void testUpdateUser_OriginalPaymentEnabledIsFalse_Failure() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        final int originalSubBalance = 2;
        final int nextSubPayment = 5;

        userDto.setId(5);
        userDto.setUserStatus(LIMITED);
        userDto.setDisplayName("displayName");
        userDto.setSubBalance(originalSubBalance);
        userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
        userDto.setPaymentEnabled(true);

        PaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        migPaymentDetails.setActivated(false);

        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);

        mockedUser.setId(5);
        mockedUser.setStatus(null);
        mockedUser.setDisplayName("");
        mockedUser.setSubBalance(originalSubBalance);
        mockedUser.setNextSubPayment(nextSubPayment);
        mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());
        mockedUser.setCurrentPaymentDetails(migPaymentDetails);

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenReturn(mockedUser);
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP)).thenReturn(mock(AccountLog.class));
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        final UserStatus mockedUserStatus = new UserStatus();
        Mockito.when(userStatusRepository.findByName(LIMITED.name())).thenReturn(mockedUserStatus);

        userServiceSpy.updateUser(userDto);

        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, SUBSCRIPTION_CHARGE);
        verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, SUPPORT_TOPUP);
        verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
    }

    @Test(expected = RuntimeException.class)
    public void testUpdateUser_UserRepository_findOne_RuntimeException_Failure() throws Exception {
        UserDto userDto = UserDtoFactory.createUserDto();

        PowerMockito.when(userRepository.findOne(userDto.getId())).thenThrow(new RuntimeException());

        userServiceSpy.updateUser(userDto);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateUser_userDtoIsNull_Failure() throws Exception {
        UserDto userDto = null;

        userServiceSpy.updateUser(userDto);
    }

    @Test
    public void testFindActivePsmsUsers_Success() {
        String communityURL = "";
        BigDecimal amountOfMoneyToUserNotification = BigDecimal.TEN;
        long deltaSuccessfulPaymentSumsSendingTimestampMillis = 256L;
        long epochMillis = 64564L;

        List<User> users = UserFactory.getUserUnmodifableList();

        PowerMockito.mockStatic(Utils.class);

        Mockito.when(getEpochMillis()).thenReturn(epochMillis);

        Mockito.when(userRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis, deltaSuccessfulPaymentSumsSendingTimestampMillis)).thenReturn(users);

        List<User> actualUsers = userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccessfulPaymentSumsSendingTimestampMillis);

        assertEquals(users, actualUsers);
    }

    @Test(expected = NullPointerException.class)
    public void testFindActivePsmsUsers_communityURLisNull_Failure() {
        String communityURL = null;
        BigDecimal amountOfMoneyToUserNotification = BigDecimal.TEN;
        long deltaSuccessfulPaymentSumsSendingTimestampMillis = 256L;
        long epochMillis = 64564L;

        List<User> users = UserFactory.getUserUnmodifableList();

        PowerMockito.mockStatic(Utils.class);

        Mockito.when(getEpochMillis()).thenReturn(epochMillis);

        Mockito.when(userRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis, deltaSuccessfulPaymentSumsSendingTimestampMillis)).thenReturn(users);

        userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccessfulPaymentSumsSendingTimestampMillis);

        verify(userRepository, times(0)).findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis, deltaSuccessfulPaymentSumsSendingTimestampMillis);
        PowerMockito.verifyStatic(times(0));
        getEpochMillis();
    }

    @Test(expected = NullPointerException.class)
    public void testFindActivePsmsUsers_amountOfMoneyToUserNotificationisNull_Failure() {
        String communityURL = "";
        BigDecimal amountOfMoneyToUserNotification = null;
        long deltaSuccessfulPaymentSumsSendingTimestampMillis = 256L;
        long epochMillis = 64564L;

        List<User> users = UserFactory.getUserUnmodifableList();

        PowerMockito.mockStatic(Utils.class);

        Mockito.when(getEpochMillis()).thenReturn(epochMillis);

        Mockito.when(userRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis, deltaSuccessfulPaymentSumsSendingTimestampMillis)).thenReturn(users);

        userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccessfulPaymentSumsSendingTimestampMillis);

        verify(userRepository, times(0)).findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis, deltaSuccessfulPaymentSumsSendingTimestampMillis);
        PowerMockito.verifyStatic(times(0));
        getEpochMillis();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testResetSmsAccordingToLawAttributes_Success() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        long epochMillis = 25L;

        PowerMockito.mockStatic(Utils.class);
        Mockito.when(getEpochMillis()).thenReturn(epochMillis);

        Mockito.when(userRepository.updateFields(BigDecimal.ZERO, epochMillis, user.getId())).thenReturn(1);

        User actualUser = userServiceSpy.resetSmsAccordingToLawAttributes(user);

        assertEquals(user, actualUser);
        assertEquals(BigDecimal.ZERO, actualUser.getAmountOfMoneyToUserNotification());
        assertEquals(epochMillis, actualUser.getLastSuccesfullPaymentSmsSendingTimestampMillis());

        verify(userRepository).updateFields(BigDecimal.ZERO, epochMillis, user.getId());
    }

    @SuppressWarnings("deprecation")
    @Test(expected = ServiceException.class)
    public void testResetSmsAccordingToLawAttributes_Failure() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        long epochMillis = 25L;

        PowerMockito.mockStatic(Utils.class);
        Mockito.when(getEpochMillis()).thenReturn(epochMillis);

        Mockito.when(userRepository.updateFields(BigDecimal.ZERO, epochMillis, user.getId())).thenReturn(0);

        User actualUser = userServiceSpy.resetSmsAccordingToLawAttributes(user);

        assertEquals(user, actualUser);
        assertEquals(BigDecimal.ZERO, actualUser.getAmountOfMoneyToUserNotification());
        assertEquals(epochMillis, actualUser.getLastSuccesfullPaymentSmsSendingTimestampMillis());

        verify(userRepository).updateFields(BigDecimal.ZERO, epochMillis, user.getId());
    }

    @SuppressWarnings("deprecation")
    @Test(expected = NullPointerException.class)
    public void testResetSmsAccordingToLawAttributes_UserIsNull_Failure() {
        User user = null;

        Mockito.when(userRepository.save(user)).thenReturn(user);

        userServiceSpy.resetSmsAccordingToLawAttributes(user);

        verify(userRepository, times(0)).save(user);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testPopulateAmountOfMoneyToUserNotification_Success() {
        final BigDecimal userAmountOfMoneyToUserNotification = BigDecimal.ONE;

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setAmountOfMoneyToUserNotification(userAmountOfMoneyToUserNotification);

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setAmount(BigDecimal.TEN);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        User actualUser = userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

        assertEquals(user, actualUser);
        BigDecimal expectedAmountOfMoneyToUserNotification = userAmountOfMoneyToUserNotification.add(submittedPayment.getAmount());
        assertEquals(expectedAmountOfMoneyToUserNotification, actualUser.getAmountOfMoneyToUserNotification());
    }

    @SuppressWarnings("deprecation")
    @Test(expected = NullPointerException.class)
    public void testPopulateAmountOfMoneyToUserNotification_UserIsNull_Failure() {
        User user = null;

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setAmount(BigDecimal.TEN);

        Mockito.when(userRepository.save(user)).thenReturn(user);

        userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

        verify(userRepository, times(0)).save(user);

    }

    @SuppressWarnings("deprecation")
    @Test(expected = NullPointerException.class)
    public void testPopulateAmountOfMoneyToUserNotification_SubmittedPaymentIsNull_Failure() {
        final BigDecimal userAmountOfMoneyToUserNotification = BigDecimal.ONE;

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setAmountOfMoneyToUserNotification(userAmountOfMoneyToUserNotification);

        SubmittedPayment submittedPayment = null;

        Mockito.when(userRepository.save(user)).thenReturn(user);

        userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

        verify(userRepository, times(0)).save(user);

    }

    @Test()
    public void unsubscribeUser_Success() {
        long epochMillis = 12354L;
        User mockedUser = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final String reason = null;

        PaymentDetails mockedCurrentPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();

        mockedUser.setCurrentPaymentDetails(mockedCurrentPaymentDetails);

        PowerMockito.mockStatic(Utils.class);

        Mockito.when(getEpochMillis()).thenReturn(epochMillis);
        PowerMockito.when(userRepository.save(mockedUser)).thenReturn(mockedUser);
//        PowerMockito.when(paymentDetailsRepository.save(mockedCurrentPaymentDetails)).thenReturn(mockedCurrentPaymentDetails);
        PowerMockito.when(paymentDetailsServiceMock.deactivateCurrentPaymentDetailsIfOneExist(mockedUser, reason)).thenReturn(mockedUser);

        User actualUser = userServiceSpy.unsubscribeUser(mockedUser, reason);

        assertNotNull(actualUser);

        PaymentDetails actualCurrentPaymentDetails = actualUser.getCurrentPaymentDetails();

        assertFalse(actualCurrentPaymentDetails.isActivated());

        verify(userRepository).save(mockedUser);
        verify(paymentDetailsServiceMock).deactivateCurrentPaymentDetailsIfOneExist(mockedUser, reason);

    }

    @Test()
    public void testMakeSuccessfulPaymentFreeSMSRequest_successfulMigResponse_Success() throws Exception {

        final long epochMillis = 123L;
        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        migPaymentDetails.setPaymentPolicy(paymentPolicy);

        Community community = CommunityFactory.createCommunity();
        UserGroup userGroup = UserGroupFactory.createUserGroup(community);
        final BigDecimal amountOfMoneyToUserNotification = BigDecimal.ONE;
        User user = UserFactory.createUser(migPaymentDetails, amountOfMoneyToUserNotification, userGroup);

        Period period = paymentPolicy.getPeriod();
        final Object[] successfulPaymentMessageArgs =
            new Object[] {community.getDisplayName(), paymentPolicy.getSubcost(), period.getDuration(), period.getDurationUnit(), paymentPolicy.getShortCode()};

        MigResponse successfulMigResponse = MigResponseFactory.createSuccessfulMigResponse();

        final MigPaymentDetails currentMigPaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();
        mockMakeFreeSMSRequest(currentMigPaymentDetails, SMS_SUCCESFULL_PAYMENT_TEXT, successfulMigResponse);
        mockMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter().toUpperCase(), SMS_SUCCESFULL_PAYMENT_TEXT_MESSAGE_CODE, successfulPaymentMessageArgs, SMS_SUCCESFULL_PAYMENT_TEXT);
        PowerMockito.mockStatic(Utils.class);

        Mockito.when(getEpochMillis()).thenReturn(epochMillis);
        Mockito.when(userRepository.updateFields(epochMillis, user.getId())).thenReturn(1);

        Future<Boolean> futureMigResponse = userServiceSpy.makeSuccessfulPaymentFreeSMSRequest(user);

        assertNotNull(futureMigResponse);
        assertTrue(futureMigResponse.get());

        verify(migHttpServiceMock).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), SMS_SUCCESFULL_PAYMENT_TEXT);
    }

    @Test(expected = ServiceCheckedException.class)
    public void testMakeSuccessfulPaymentFreeSMSRequest_failureMigResponse_Failure() throws Exception {

        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

        MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        migPaymentDetails.setPaymentPolicy(paymentPolicy);

        Community community = CommunityFactory.createCommunity();
        UserGroup userGroup = UserGroupFactory.createUserGroup(community);
        final BigDecimal amountOfMoneyToUserNotification = BigDecimal.ONE;
        User user = UserFactory.createUser(migPaymentDetails, amountOfMoneyToUserNotification, userGroup);

        Period period = paymentPolicy.getPeriod();
        final Object[] successfulPaymentMessageArgs =
            new Object[] {community.getDisplayName(), paymentPolicy.getSubcost(), period.getDuration(), period.getDurationUnit(), paymentPolicy.getShortCode()};

        MigResponse failureMigResponse = MigResponseFactory.createFailMigResponse();

        final MigPaymentDetails currentMigPaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();
        mockMakeFreeSMSRequest(currentMigPaymentDetails, SMS_SUCCESFULL_PAYMENT_TEXT, failureMigResponse);
        mockMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter().toUpperCase(), SMS_SUCCESFULL_PAYMENT_TEXT_MESSAGE_CODE, successfulPaymentMessageArgs, SMS_SUCCESFULL_PAYMENT_TEXT);

        userServiceSpy.makeSuccessfulPaymentFreeSMSRequest(user);

        verify(migHttpServiceMock).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), SMS_SUCCESFULL_PAYMENT_TEXT);
    }

    private Object[] testRegisterUser(final String storedToken,
                                      String communityName,
                                      final String deviceUID,
                                      final String deviceTypeName,
                                      final String ipAddress,
                                      final boolean notExistUser,
                                      boolean notDeviceType) throws Exception {
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        final Country country = mock(Country.class);
        final Integer operatorId = 1;
        final DeviceType deviceType = new DeviceType();
        deviceType.setName(deviceTypeName);
        user.setDeviceType(deviceType);
        final DeviceType noneDeviceType = new DeviceType();
        noneDeviceType.setName(DeviceType.NONE);
        final UserStatus userStatus = new UserStatus();
        userStatus.setName(UserStatusType.LIMITED.name());
        user.setStatus(userStatus);
        final Community community = CommunityFactory.createCommunity();
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Map<String, DeviceType> deviceTypeMap = Collections.singletonMap(deviceTypeName, notDeviceType ? null : deviceType);
        final UserDeviceRegDetailsDto userDeviceRegDetailsDto = new UserDeviceRegDetailsDto();
        userDeviceRegDetailsDto.setDEVICE_TYPE(deviceTypeName);
        userDeviceRegDetailsDto.setCommunityUri(communityName);
        userDeviceRegDetailsDto.setDEVICE_UID(deviceUID);
        userDeviceRegDetailsDto.setIpAddress(ipAddress);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(DeviceTypeCache.class);

        Mockito.doReturn(user).when(userRepository).save(any(User.class));
        Mockito.when(createStoredToken(anyString(), anyString())).thenReturn(storedToken);
        Mockito.when(DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue()).thenReturn(deviceTypeMap);
        Mockito.when(DeviceTypeCache.getNoneDeviceType()).thenReturn(noneDeviceType);
        Mockito.when(userGroupRepository.findByCommunity(community)).thenReturn(userGroup);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(userStatus);
        Mockito.when(communityServiceMock.getCommunityByUrl(anyString())).thenReturn(community);
        Mockito.when(countryServiceMock.findIdByName(anyString())).thenReturn(country);
        PowerMockito.doReturn(notExistUser ? null : user).when(userRepository).findUserWithUserNameAsPassedDeviceUID(anyString(), any(Community.class));
        whenNew(User.class).withNoArguments().thenReturn(user);
        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        }).when(userRepository).saveAndFlush(any(User.class));

        return new Object[] {userDeviceRegDetailsDto, user};
    }

    @Test
    public void shouldDetectUserAccountWithSameDeviceAndDisableIt() throws Exception {
        //given
        final String deviceUID = "imei_357841034540704";
        final UserDeviceRegDetailsDto userDeviceRegDetailsDto = new UserDeviceRegDetailsDto().withDeviceUID(deviceUID).withCommunityUri("chartsnow").withDeviceModel("");
        User userAccountWithSameDevice = new User().withDeviceUID(deviceUID);

        final Country country = mock(Country.class);
        Mockito.when(countryServiceMock.findIdByName(anyString())).thenReturn(country);

        Community community = new Community();
        User expectedUser = new User();
        doReturn(community).when(communityServiceMock).getCommunityByUrl(userDeviceRegDetailsDto.getCommunityUri());
        doReturn(null).when(userRepository).findUserWithUserNameAsPassedDeviceUID(userDeviceRegDetailsDto.getDeviceUID(), community);
        doReturn(userAccountWithSameDevice).when(userRepository).findByDeviceUIDAndCommunity(userDeviceRegDetailsDto.getDeviceUID(), community);
        doReturn(userAccountWithSameDevice).when(userRepository).save(userAccountWithSameDevice);
        PowerMockito.whenNew(User.class).withNoArguments().thenReturn(expectedUser);
        PowerMockito.mockStatic(DeviceTypeCache.class);
        PowerMockito.when(DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue()).thenReturn(new HashMap<String, DeviceType>());
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(new UserStatus());
        Answer returnFirsParamAnswer = new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];

                assertThat(user.getDeviceUID(), is(deviceUID));
                assertThat(user.getUserName(), is(deviceUID));

                return user;
            }
        };
        Mockito.when(operatorRepository.findFirst()).thenReturn(new Operator());
        Mockito.doAnswer(returnFirsParamAnswer).when(userRepository).save(any(User.class));
        Mockito.doAnswer(returnFirsParamAnswer).when(userRepository).saveAndFlush(any(User.class));
        doReturn(expectedUser).when(userServiceSpy).processAccountCheckCommandForAuthorizedUser(any(int.class));
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getEpochMillis()).thenReturn(Long.MAX_VALUE);
        UserGroup userGroup = new UserGroup();
        PowerMockito.doReturn(userGroup).when(userGroupRepository).findByCommunity(community);
        PowerMockito.doReturn(1).when(userRepository).updateUserAccountWithSameDeviceAndDisableIt(deviceUID, userGroup);

        //when
        User actualUser = userServiceSpy.registerUser(userDeviceRegDetailsDto, false, false);

        //then
        assertNotNull(actualUser);
        assertThat(actualUser, is(expectedUser));

        verify(userRepository, times(1)).saveAndFlush(any(User.class));
        verify(userRepository, times(1)).updateUserAccountWithSameDeviceAndDisableIt(deviceUID, userGroup);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterUser_WOPotentialPromo_Success() throws Exception {
        final String storedToken = "50c86945713ac8c870eafbc19980706b";
        final String communityName = "chartsnow";
        final String deviceUID = "imei_357841034540704";
        final String deviceTypeName = "android";
        final String ipAddress = "10.10.0.2";

        Object[] testData = testRegisterUser(storedToken, communityName, deviceUID, deviceTypeName, ipAddress, true, false);
        final UserDeviceRegDetailsDto userDeviceRegDetailsDto = (UserDeviceRegDetailsDto) testData[0];
        Operator operator = new Operator();
        operator.setId(1);
        Mockito.when(operatorRepository.findFirst()).thenReturn(operator);

        User user = userServiceSpy.registerUser(userDeviceRegDetailsDto, false, false);

        assertNotNull(user);
        assertEquals(user.getToken(), storedToken);
        assertEquals(user.getUserName(), deviceUID);
        assertEquals(user.getDeviceType().getName().toLowerCase(), deviceTypeName);
        assertEquals(user.getOperator(), operator.getId());
        assertEquals(user.getDeviceUID(), deviceUID);
        assertEquals(user.getStatus().getName(), UserStatusType.LIMITED.name());
        assertEquals(user.getActivationStatus(), ActivationStatus.REGISTERED);

        verify(communityServiceMock, times(1)).getCommunityByUrl(anyString());
        verify(countryServiceMock, times(1)).findIdByName(anyString());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
        verify(userServiceSpy, times(0)).processAccountCheckCommandForAuthorizedUser(anyInt());
        verifyStatic(times(1));
        createStoredToken(anyString(), anyString());
        verifyStatic(times(1));
        DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue();
        verifyStatic(times(1));
    }

    @Test()
    public void testRegisterUser_WOPotentialPromo_NoneDeviceType_Success() throws Exception {
        String storedToken = "50c86945713ac8c870eafbc19980706b";
        final String communityName = "chartsnow";
        final String deviceUID = "imei_357841034540704";
        final String deviceTypeName = "";
        final String ipAddress = "10.10.0.2";

        Object[] testData = testRegisterUser(storedToken, communityName, deviceUID, deviceTypeName, ipAddress, true, true);
        final UserDeviceRegDetailsDto userDeviceRegDetailsDto = (UserDeviceRegDetailsDto) testData[0];
        Mockito.when(operatorRepository.findFirst()).thenReturn(new Operator());

        User result = userServiceSpy.registerUser(userDeviceRegDetailsDto, false, false);

        assertNotNull(result);
        assertEquals(result.getDeviceType().getName(), DeviceType.NONE);

        verifyStatic(times(1));
        DeviceTypeCache.getNoneDeviceType();
    }

    @Test
    public void testRegisterUser_WOPotentialPromo_ExistUser_Success() throws Exception {
        final String storedToken = "50c86945713ac8c870eafbc19980706b";
        final String communityName = "chartsnow";
        final String deviceUID = "imei_357841034540704";
        final String deviceTypeName = "android";
        final String ipAddress = "10.10.0.2";

        Object[] testData = testRegisterUser(storedToken, communityName, deviceUID, deviceTypeName, ipAddress, false, false);
        final User user = (User) testData[1];
        final UserDeviceRegDetailsDto userDeviceRegDetailsDto = (UserDeviceRegDetailsDto) testData[0];

        User result = userServiceSpy.registerUser(userDeviceRegDetailsDto, false, false);

        assertNotNull(result);
        assertEquals(result.getToken(), user.getToken());
        assertEquals(result.getUserName(), user.getUserName());

        verify(communityServiceMock, times(1)).getCommunityByUrl(anyString());
        verify(countryServiceMock, times(0)).findIdByName(anyString());
        verify(userRepository, times(0)).save(any(User.class));
        verify(userServiceSpy, times(0)).processAccountCheckCommandForAuthorizedUser(anyInt());
        verifyStatic(times(0));
        createStoredToken(anyString(), anyString());
        verifyStatic(times(0));
        DeviceTypeCache.getDeviceTypeMapNameAsKeyAndDeviceTypeValue();
        verifyStatic(times(0));
    }

    @Test()
    public void testActivatePhoneNumber_Success() throws Exception {
        final String phone = "07870111111";
        final String pin = "1111";
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setPin("1234");

        Mockito.when(o2ClientServiceMock.validatePhoneNumber(anyString())).thenReturn(new PhoneNumberValidationData().withPhoneNumber("+447870111111").withPin(pin));

        boolean populateO2SubscriberData = false;
        User userResult = userServiceSpy.activatePhoneNumber(user, phone);

        assertNotNull(user);
        assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
        assertEquals("+447870111111", userResult.getMobile());
        assertEquals(pin, userResult.getPin());

        verify(userRepository, times(1)).save(any(User.class));
        verify(o2ClientServiceMock, times(1)).validatePhoneNumber(anyString());
    }

    @Test()
    public void testActivatePhoneNumber_NullPhone_Success() throws Exception {
        final String phone = null;
        final String pin = null;
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setPin("1234");

        Mockito.when(o2ClientServiceMock.validatePhoneNumber(anyString())).thenAnswer(new Answer<PhoneNumberValidationData>() {
            @Override
            public PhoneNumberValidationData answer(InvocationOnMock invocation) throws Throwable {
                String phone = (String) invocation.getArguments()[0];
                assertEquals(user.getMobile(), phone);

                return new PhoneNumberValidationData().withPhoneNumber("+447870111111").withPin(pin);
            }
        });
        boolean populateO2SubscriberData = false;
        User userResult = userServiceSpy.activatePhoneNumber(user, phone);

        assertNotNull(user);
        assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
        assertEquals("+447870111111", userResult.getMobile());
        assertEquals("1234", userResult.getPin());

        verify(userRepository, times(1)).save(any(User.class));
        verify(o2ClientServiceMock, times(1)).validatePhoneNumber(anyString());
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_nonVFUser_Success() throws Exception {
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setLastSubscribedPaymentSystem(migSmsType);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        community.setRewriteUrlParameter("vf_nz");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(NON_VF);
        user.setSubBalance(0);
        user.setStatus(limitedUserStatus);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setSubBalance(0);

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setNextSubPayment(Integer.MIN_VALUE);
        submittedPayment.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        submittedPayment.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        submittedPayment.setPaymentSystem(iTunesSubscriptionType);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(0, passedUser.getSubBalance());
                assertEquals(Integer.MIN_VALUE, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(Long.MAX_VALUE, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(iTunesSubscriptionType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochMillis()).thenReturn(Long.MAX_VALUE);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_VFLimitedUser_Success() throws Exception {
        final int oldNextSubPayment = 0;
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        community.setRewriteUrlParameter(Community.VF_NZ_COMMUNITY_REWRITE_URL);
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(VF);
        user.setSubBalance(2);
        user.setStatus(limitedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setNextSubPayment(oldNextSubPayment);

        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(migSmsType);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(oldNextSubPayment + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(Long.MAX_VALUE, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(migSmsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochMillis()).thenReturn(Long.MAX_VALUE);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_nonO2User_Success() throws Exception {
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setLastSubscribedPaymentSystem(migSmsType);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(NON_O2);
        user.setSubBalance(0);
        user.setStatus(limitedUserStatus);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setSubBalance(0);

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setNextSubPayment(Integer.MIN_VALUE);
        submittedPayment.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        submittedPayment.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        submittedPayment.setPaymentSystem(iTunesSubscriptionType);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(0, passedUser.getSubBalance());
                assertEquals(Integer.MIN_VALUE, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(Long.MAX_VALUE, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(iTunesSubscriptionType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochMillis()).thenReturn(Long.MAX_VALUE);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2LimitedUser_Success() throws Exception {
        final int oldNextSubPayment = 0;
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(2);
        user.setStatus(limitedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setNextSubPayment(oldNextSubPayment);

        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(migSmsType);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(oldNextSubPayment + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(Long.MAX_VALUE, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(migSmsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochMillis()).thenReturn(Long.MAX_VALUE);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2BusinessLimitedUser_Success() throws Exception {
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        final int oldNextSubPayment = 2;

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(2);
        user.setStatus(limitedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setNextSubPayment(oldNextSubPayment);
        user.setSegment(SegmentType.BUSINESS);

        final SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(migSmsType);
        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        final int currentTimeSeconds = oldNextSubPayment + 25;
        final long currentTimeMillis = currentTimeSeconds * 1000L;

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(currentTimeSeconds + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(currentTimeMillis, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(migSmsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
        Mockito.when(getEpochMillis()).thenReturn(currentTimeMillis);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2BussinesSubscribedUser_Success() throws Exception {
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        final int oldNextSubPayment = 2;

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(2);
        user.setStatus(subscribedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setNextSubPayment(oldNextSubPayment);
        user.setSegment(SegmentType.BUSINESS);

        final SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(migSmsType);
        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        final int currentTimeSeconds = oldNextSubPayment + 25;
        final long currentTimeMillis = currentTimeSeconds * 1000L;

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(currentTimeSeconds + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(currentTimeMillis, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(migSmsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
        Mockito.when(getEpochMillis()).thenReturn(currentTimeMillis);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2BusinessSubscribedUserAndCurrentTimeLessThanNextSubPayment_Success() throws Exception {
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        final int oldNextSubPayment = 2;

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(2);
        user.setStatus(subscribedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setNextSubPayment(oldNextSubPayment);
        user.setSegment(SegmentType.BUSINESS);

        final SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(migSmsType);
        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        final int currentTimeSeconds = oldNextSubPayment - 5;
        final long currentTimeMillis = currentTimeSeconds * 1000L;

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(oldNextSubPayment + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(currentTimeMillis, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(migSmsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
        Mockito.when(getEpochMillis()).thenReturn(currentTimeMillis);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2EulaUser_Success() throws Exception {
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        final int oldSubBalance = 2;
        final int oldNextSubPayment = 0;

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(oldSubBalance);
        user.setStatus(eulaUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setNextSubPayment(oldNextSubPayment);

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(migSmsType);
        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(oldSubBalance, passedUser.getSubBalance());
                assertEquals(oldNextSubPayment + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(Long.MAX_VALUE, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(migSmsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(null, passedUser.getFreeTrialExpiredMillis());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochMillis()).thenReturn(Long.MAX_VALUE);


        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), oldSubBalance, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2SubscribedUser_Success() throws Exception {
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        final int oldSubBalance = 2;
        final int nextSubPayment = 1;

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(oldSubBalance);
        user.setStatus(subscribedUserStatus);
        user.setLastSubscribedPaymentSystem(migSmsType);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setNextSubPayment(nextSubPayment);

        SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(migSmsType);
        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(oldSubBalance, passedUser.getSubBalance());
                assertEquals(nextSubPayment + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(Long.MAX_VALUE, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(migSmsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);

        Mockito.when(getEpochMillis()).thenReturn(Long.MAX_VALUE);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), oldSubBalance, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2ConsumerSubscribedUserPayedByO2Psms_Success() throws Exception {
        final int currentTimeSeconds = 0;
        final long currentTimeMillis = currentTimeSeconds * 1000L;
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String paymentDetailsType = PaymentDetails.O2_PSMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(2);
        user.setStatus(subscribedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setSegment(CONSUMER);
        user.setContract(PAYG);
        final int oldNextSubPayment = currentTimeSeconds - WEEK_SECONDS;
        user.setNextSubPayment(oldNextSubPayment);

        final SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(paymentDetailsType);
        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(currentTimeSeconds + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(currentTimeSeconds, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(paymentDetailsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(0);

        Mockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
        Mockito.when(getEpochMillis()).thenReturn(currentTimeMillis);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testProcessPaymentSubBalanceCommand_O2PAYMConsumerSubscribedUserPayedByO2Psms_Success() throws Exception {
        final int currentTimeSeconds = 0;
        final long currentTimeMillis = currentTimeSeconds * 1000L;
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String paymentDetailsType = PaymentDetails.O2_PSMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setSubBalance(2);
        user.setStatus(subscribedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setSegment(CONSUMER);
        user.setContract(PAYM);
        final int oldNextSubPayment = currentTimeSeconds - WEEK_SECONDS;
        user.setNextSubPayment(oldNextSubPayment);

        final SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(paymentDetailsType);
        final Period period = new Period().withDuration(5).withDurationUnit(WEEKS);
        submittedPayment.setPeriod(period);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(currentTimeSeconds + period.getDuration() * WEEK_SECONDS, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(currentTimeSeconds, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(paymentDetailsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(0);

        Mockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
        Mockito.when(getEpochMillis()).thenReturn(currentTimeMillis);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test(expected = NullPointerException.class)
    public void testProcessPaymentSubBalanceCommand_UserIsNull_Failure() throws Exception {
        userServiceSpy.processPaymentSubBalanceCommand(null, createSubmittedPayment());
    }

    @Test
    public void testIsIOsnonO2ItunesSubscribedUser_LIMITED_Success() throws Exception {
        DeviceType iosDeviceType = DeviceTypeFactory.createDeviceType("IOs");
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        user.setLastSubscribedPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        user.setStatus(limitedUserStatus);
        user.setDeviceType(iosDeviceType);

        PowerMockito.mockStatic(DeviceTypeCache.class);
        PowerMockito.when(DeviceTypeCache.getIOSDeviceType()).thenReturn(iosDeviceType);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);

        boolean isIOsnonO2ItunesSubscribedUser = user.isIOsNonO2ITunesSubscribedUser();

        assertFalse(isIOsnonO2ItunesSubscribedUser);
    }

    @Test
    public void test_isNonO2UserSubscribeByO2_PSMS_Success() throws Exception {
        final int monthlyNextSubPayment = 31 * 24 * 60 * 60;
        final int currentTimeSeconds = 0;
        final long currentTimeMillis = currentTimeSeconds * 1000L;
        final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
        final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
        final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
        final String paymentDetailsType = PaymentDetails.O2_PSMS_TYPE;

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        final Community community = CommunityFactory.createCommunity();

        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus eulaUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);

        community.setRewriteUrlParameter("o2");
        userGroup.setCommunity(community);
        user.setUserGroup(userGroup);
        user.setProvider(NON_O2);
        user.setSubBalance(2);
        user.setStatus(subscribedUserStatus);
        user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
        user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
        user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
        user.setSegment(CONSUMER);
        user.setContract(PAYM);
        final int oldNextSubPayment = currentTimeSeconds - 5 * WEEK_SECONDS;
        user.setNextSubPayment(oldNextSubPayment);

        final SubmittedPayment submittedPayment = createSubmittedPayment();
        submittedPayment.setPaymentSystem(paymentDetailsType);
        final Period periodMock = PowerMockito.mock(Period.class);
        submittedPayment.setPeriod(periodMock);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);
        Mockito.when(userStatusRepository.findByName(UserStatusType.LIMITED.name())).thenReturn(limitedUserStatus);
        Mockito.when(userStatusRepository.findByName(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA.name())).thenReturn(eulaUserStatus);

        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {

            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User passedUser = (User) invocation.getArguments()[0];

                assertEquals(2, passedUser.getSubBalance());
                assertEquals(0, passedUser.getNextSubPayment());
                assertEquals(subscribedUserStatus, passedUser.getStatus());
                assertEquals(currentTimeSeconds, passedUser.getLastSuccessfulPaymentTimeMillis());

                assertEquals(base64EncodedAppStoreReceipt, passedUser.getBase64EncodedAppStoreReceipt());
                assertEquals(appStoreOriginalTransactionId, passedUser.getAppStoreOriginalTransactionId());
                assertEquals(paymentDetailsType, passedUser.getLastSubscribedPaymentSystem());
                assertEquals(Long.MAX_VALUE, passedUser.getFreeTrialExpiredMillis().longValue());

                return passedUser;
            }
        });

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
        PowerMockito.when(periodMock.toNextSubPaymentSeconds(oldNextSubPayment)).thenReturn(monthlyNextSubPayment);

        Mockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
        Mockito.when(getEpochMillis()).thenReturn(currentTimeMillis);

        userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment);

        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), 2, null, submittedPayment, CARD_TOP_UP);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testIsIOsnonO2ItunesSubscribedUser_SUBSCRIBED_Success() throws Exception {
        DeviceType iosDeviceType = DeviceTypeFactory.createDeviceType("IOs");
        final UserStatus limitedUserStatus = createUserStatus(LIMITED);
        final UserStatus subscribedUserStatus = createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        user.setLastSubscribedPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        user.setStatus(subscribedUserStatus);
        user.setDeviceType(iosDeviceType);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
        user.setProvider(NON_O2);

        PowerMockito.mockStatic(DeviceTypeCache.class);
        PowerMockito.when(DeviceTypeCache.getIOSDeviceType()).thenReturn(iosDeviceType);

        Mockito.when(userStatusRepository.findByName(UserStatusType.SUBSCRIBED.name())).thenReturn(subscribedUserStatus);

        boolean isIOsnonO2ItunesSubscribedUser = user.isIOsNonO2ITunesSubscribedUser();

        assertTrue(isIOsnonO2ItunesSubscribedUser);
    }

    @Test
    public void testFindUsersForItunesInAppSubscription_Success() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        User user2 = UserFactory.createUser(ActivationStatus.ACTIVATED);

        int nextSubPayment = 1;
        String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";

        List<User> users = new ArrayList<User>();
        users.add(user2);


        Mockito.when(userRepository.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId)).thenReturn(users);

        List<User> actualUsers = userServiceSpy.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);

        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertTrue(users.contains(user));
        assertTrue(users.contains(user2));

        verify(userRepository, times(1)).findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
    }

    @Test(expected = NullPointerException.class)
    public void testFindUsersForItunesInAppSubscription_appStoreOriginalTransactionIdIsNull_Failure() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);

        String appStoreOriginalTransactionId = null;
        int nextSubPayment = 1;

        userServiceSpy.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
    }

    @Test(expected = NullPointerException.class)
    public void testFindUsersForItunesInAppSubscription_userIsNull_Failure() {
        User user = null;

        int nextSubPayment = 1;
        String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";

        userServiceSpy.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
    }

    @Test
    public void testGetUsersForPendingPaymentMoreThanMaxCount() throws Exception {
        //given
        int maxCount = 4;
        Page usersPageMock = Mockito.mock(Page.class);
        final int currentTimeSeconds = Integer.MAX_VALUE;
        mockStatic(Utils.class);
        PowerMockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);

        when(userRepository.findUsersForPendingPayment(eq(currentTimeSeconds), eq(new PageRequest(0, maxCount, Sort.Direction.ASC, "nextSubPayment")))).thenReturn(usersPageMock);

        //when
        Page<User> usersPage = userServiceSpy.getUsersForPendingPayment(maxCount);

        //then
        assertEquals(usersPageMock, usersPage);

        verify(userRepository).findUsersForPendingPayment(eq(currentTimeSeconds), eq(new PageRequest(0, maxCount, Sort.Direction.ASC, "nextSubPayment")));
    }

    @Test
    public void shouldApplyInitPromoAndAccCheckWithUpdateContractAndProvider() {
        //given
        Community community = new Community().withRewriteUrl(VF_NZ_COMMUNITY_REWRITE_URL).withName(VF_NZ_COMMUNITY_REWRITE_URL);
        User user = new User().withActivationStatus(ENTERED_NUMBER).withDeviceType(new DeviceType()).withUserName("g@g.gg").withUserGroup(new UserGroup().withCommunity(community));

        User mobileUser = null;
        String otac = "otac";

        ProviderUserDetails providerUserDetails = new ProviderUserDetails().withOperator(VF.getKey()).withContract(PAYG.name());

        doReturn(user).when(userServiceSpy).mergeUser(mobileUser, user);
        Mockito.when(otacValidationServiceMock.validate(otac, user.getMobile(), community)).thenReturn(providerUserDetails);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        doReturn(null).when(userServiceSpy).processAccountCheckCommandForAuthorizedUser(user.getId());

        //when
        MergeResult resultOfOperation = userServiceSpy.applyInitPromo(user, otac, false, false, false);
        User result = resultOfOperation.getResultOfOperation();

        //then
        assertNotNull(result);
        assertEquals(user, result);

        assertNull(user.getContract());
        assertThat(user.getProvider(), is(VF));
        assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        assertEquals(user.getMobile(), user.getUserName());

        verify(userServiceSpy, times(0)).mergeUser(mobileUser, user);
        verify(otacValidationServiceMock, times(1)).validate(otac, user.getMobile(), community);
        verify(userRepository, times(1)).save(user);
        verify(userServiceSpy, times(0)).processAccountCheckCommandForAuthorizedUser(user.getId());
    }

    @Test
    public void shouldApplyInitPromoAndAccCheck() {
        //given
        Community community = new Community().withRewriteUrl(VF_NZ_COMMUNITY_REWRITE_URL).withName(VF_NZ_COMMUNITY_REWRITE_URL);
        User user = new User().withActivationStatus(ENTERED_NUMBER).withDeviceType(new DeviceType()).withUserName("+380913158096").withUserGroup(new UserGroup().withCommunity(community));

        User mobileUser = null;
        String otac = "otac";

        ProviderUserDetails providerUserDetails = new ProviderUserDetails().withOperator(VF.getKey()).withContract(PAYG.name());

        doReturn(user).when(userServiceSpy).mergeUser(mobileUser, user);
        Mockito.when(otacValidationServiceMock.validate(otac, user.getMobile(), community)).thenReturn(providerUserDetails);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(promotionServiceMock.applyPotentialPromo(user)).thenAnswer(userWithPromoAnswer);

        doReturn(null).when(userServiceSpy).processAccountCheckCommandForAuthorizedUser(user.getId());

        //when
        MergeResult opResult = userServiceSpy.applyInitPromo(user, otac, false, false, false);
        User result = opResult.getResultOfOperation();

        //then
        assertNotNull(result);
        assertEquals(user, result);

        assertNull(user.getContract());
        assertThat(user.getProvider(), is(ProviderType.valueOfKey(providerUserDetails.operator)));
        assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        assertEquals(user.getMobile(), user.getUserName());

        verify(userServiceSpy, times(0)).mergeUser(mobileUser, user);
        verify(otacValidationServiceMock, times(1)).validate(otac, user.getMobile(), community);
        verify(userRepository, times(1)).save(user);
        verify(userServiceSpy, times(0)).processAccountCheckCommandForAuthorizedUser(user.getId());
    }

    @Test
    public void applyInitPromoO2_EmailAsUserName_Success() {
        Community community = new Community().withRewriteUrl("o2").withName("o2");
        User user = new User().withActivationStatus(ENTERED_NUMBER).withDeviceType(new DeviceType()).withUserName("g@g.gg").withUserGroup(new UserGroup().withCommunity(community));

        User mobileUser = null;
        String otac = "otac";

        ProviderUserDetails o2UserDetails = new ProviderUserDetails().withContract(PAYG.name());

        doReturn(user).when(userServiceSpy).mergeUser(mobileUser, user);
        Mockito.when(otacValidationServiceMock.validate(otac, user.getMobile(), community)).thenReturn(o2UserDetails);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(communityServiceMock.getCommunityByName(community.getName())).thenReturn(community);

        doReturn(null).when(userServiceSpy).processAccountCheckCommandForAuthorizedUser(user.getId());

        MergeResult opResult = userServiceSpy.applyInitPromo(user, otac, true, false, false);
        User result = opResult.getResultOfOperation();

        assertNotNull(result);
        assertEquals(user, result);

        assertEquals(Contract.valueOf(o2UserDetails.contract), user.getContract());
        assertEquals(ProviderType.valueOfKey(o2UserDetails.operator), user.getProvider());
        assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        assertEquals(user.getMobile(), user.getUserName());

        verify(userServiceSpy, times(0)).mergeUser(mobileUser, user);
        verify(otacValidationServiceMock, times(1)).validate(otac, user.getMobile(), community);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void applyInitPromoO2_NotEmailAsUserName_Success() {
        Community community = new Community().withRewriteUrl("o2").withName("o2");
        User user = new User().withActivationStatus(ENTERED_NUMBER).withDeviceType(new DeviceType()).withUserName("+380913008066").withUserGroup(new UserGroup().withCommunity(community));

        User mobileUser = null;
        String otac = "otac";

        ProviderUserDetails o2UserDetails = new ProviderUserDetails().withOperator(VF.getKey()).withContract(PAYG.name());

        doReturn(user).when(userServiceSpy).mergeUser(mobileUser, user);
        Mockito.when(otacValidationServiceMock.validate(otac, user.getMobile(), community)).thenReturn(o2UserDetails);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(communityServiceMock.getCommunityByName(community.getName())).thenReturn(community);

        boolean hasPromo = false;
        Mockito.when(promotionServiceMock.applyPotentialPromo(user)).thenAnswer(userWithoutPromoAnswer);

        doReturn(null).when(userServiceSpy).processAccountCheckCommandForAuthorizedUser(user.getId());

        MergeResult opResult = userServiceSpy.applyInitPromo(user, otac, true, false, false);

        User result = opResult.getResultOfOperation();

        assertNotNull(result);
        assertEquals(user, result);

        assertEquals(Contract.valueOf(o2UserDetails.contract), user.getContract());
        assertEquals(ProviderType.valueOfKey(o2UserDetails.operator), user.getProvider());
        assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        assertEquals(user.getMobile(), user.getUserName());

        verify(userServiceSpy, times(0)).mergeUser(mobileUser, user);
        verify(otacValidationServiceMock, times(1)).validate(otac, user.getMobile(), community);
        verify(userRepository, times(1)).save(user);
        verify(promotionServiceMock, times(1)).applyPotentialPromo(user);
        verify(userServiceSpy, times(0)).processAccountCheckCommandForAuthorizedUser(user.getId());
    }

    private void mockMessage(final String upperCaseCommunityURL, String messageCode, final Object[] expectedMessageArgs, String message) {
        final ArgumentMatcher<Object[]> matcher = new ArgumentMatcher<Object[]>() {
            @Override
            public boolean matches(Object argument) {
                Object[] messageArgs = (Object[]) argument;

                Assert.assertEquals(expectedMessageArgs.length, messageArgs.length);
                for (int i = 0; i < expectedMessageArgs.length; i++) {
                    Assert.assertEquals(expectedMessageArgs[i], messageArgs[i]);
                }

                return true;
            }
        };

        Mockito.when(communityResourceBundleMessageSourceMock.getMessage(Mockito.eq(upperCaseCommunityURL), Mockito.eq(messageCode), Mockito.argThat(matcher), Mockito.any(Locale.class)))
               .thenReturn(message);

    }

    private void mockMakeFreeSMSRequest(final MigPaymentDetails currentMigPaymentDetails, String message, MigResponse migResponse) {
        Mockito.when(migHttpServiceMock.makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), message)).thenReturn(migResponse);
    }

    @Test
    public void testSkipBoughtPeriodAndUnsubscribe_4GVideoAudioFreeTrialTo3G_Success() throws Exception {

        currentTimeMillis = 0L;
        currentUserTariff = _4G;
        newUserTariff = _3G;

        create4GVideoAudioSubscribedUserOnVideoAudioFreeTrial();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.skipBoughtPeriodAndUnsubscribe(user, USER_DOWNGRADED_TARIFF);

        assertNotNull(actualUser);
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment() * 1000L));

        verify(refundServiceMock, times(1)).logSkippedBoughtPeriod(user, USER_DOWNGRADED_TARIFF);
        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
    }

    @Test
    public void testDowngradeUserTariff_4GVideoAudioFreeTrialTo3G_Success() throws Exception {

        currentTimeMillis = 0L;
        currentUserTariff = _4G;
        newUserTariff = _3G;

        create4GVideoAudioSubscribedUserOnVideoAudioFreeTrial();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.downgradeUserTariff(user, newUserTariff);

        assertNotNull(actualUser);
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment() * 1000L));
        assertEquals(currentTimeMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(promotionServiceMock, times(1)).applyPotentialPromo(user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }

    @Test
    public void testDowngradeUserTariff_4GVideoAudioSubscriptionBoughtTo3G_Success() throws Exception {

        currentTimeMillis = 10000L;
        currentUserTariff = _4G;
        newUserTariff = _3G;

        create4GOnBoughtVideoAudioSubscriptionUser();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.downgradeUserTariff(user, newUserTariff);

        assertNotNull(actualUser);
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment() * 1000L));
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(promotionServiceMock, times(0)).applyPotentialPromo(user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }

    @Test
    public void testDowngradeUserTariff_4GVideoAudioSubscriptionNotBoughtYetFreeTrialIsOverTo3G_Success() throws Exception {

        currentTimeMillis = 10000L;
        currentUserTariff = _4G;
        newUserTariff = _3G;

        create4GOnBoughtVideoAudioSubscriptionUser();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.downgradeUserTariff(user, newUserTariff);

        assertNotNull(actualUser);
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment() * 1000L));
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(promotionServiceMock, times(0)).applyPotentialPromo(user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }

    @Test
    public void testDowngradeUserTariff_4GVideoAudioFreeTrialTo4GMusic_Success() throws Exception {
        currentTimeMillis = 0L;
        currentUserTariff = _4G;
        newUserTariff = _4G;

        create4GVideoAudioSubscribedUserOnVideoAudioFreeTrial();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.downgradeUserTariff(user, newUserTariff);

        assertNotNull(actualUser);
        assertEquals(nextSubPayment, actualUser.getNextSubPayment());
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(0)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(promotionServiceMock, times(0)).applyPotentialPromo(user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }

    @Test
    public void testDowngradeUserTariff_4GVideoMusicTo4GMusicAndThenTo3G_Success() throws Exception {
        currentTimeMillis = 10000L;
        currentUserTariff = _4G;
        newUserTariff = _3G;

        create4GMusicSubscribedOnBoughtVideoAudioSubscriptionYetUser();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.downgradeUserTariff(user, newUserTariff);

        assertNotNull(actualUser);
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment() * 1000L));
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(promotionServiceMock, times(0)).applyPotentialPromo(user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }

    @Test
    public void testDowngradeUserTariff_4GOnBoughtAudioPeriodTo3G_Success() throws Exception {
        currentTimeMillis = 10000L;
        currentUserTariff = _4G;
        newUserTariff = _3G;

        create4GOnBoughtAudioSubscriptionUser();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.downgradeUserTariff(user, newUserTariff);

        assertNotNull(actualUser);
        assertEquals(nextSubPayment, actualUser.getNextSubPayment());
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(0)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(promotionServiceMock, times(0)).applyPotentialPromo(user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }


    @Test
    public void shouldMightActivateVideoTrialForO2Payg4GConsumerWithVideoAudioFreeTrialHasNotBeenActivated() {
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false)
                         .withContract(PAYG)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Paym4GIndirectConsumerWithVideoAudioFreeTrialHasNotBeenActivated() {
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false)
                         .withContractChanel(INDIRECT)
                         .withContract(PAYM)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForO2Paym4GIndirectConsumerWithVideoAudioFreeTrialHasBeenActivated() {
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(true)
                         .withContractChanel(INDIRECT)
                         .withContract(PAYM)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Paym4GConsumerWithNullContractChannelNotOnVideoAudioFreeTrialAndNotOnVideoAudioSubscriptionBeforeMultipleFreeTrialsStopDate() {
        //given
        user = new User().withContractChanel(null)
                         .withContract(PAYM)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")))
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Paym4GDirectConsumerNotOnVideoAudioFreeTrialAndNotOnVideoAudioSubscriptionBeforeMultipleFreeTrialsStopDate() {
        //given
        user = new User().withContractChanel(DIRECT)
                         .withContract(PAYM)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")))
                         .withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldNotdMightActivateVideoTrialForO2Paym4GDirectConsumerOnVideoAudioFreeTrialBeforeMultipleFreeTrialsStopDate() {
        //given
        user = new User().withContractChanel(DIRECT)
                         .withContract(PAYM)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")))
                         .withFreeTrialExpiredMillis(Long.MAX_VALUE)
                         .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForO2Paym4GDirectConsumerOnVideoAudioSubscriptionBeforeMultipleFreeTrialsStopDate() {
        //given
        user = new User().withContractChanel(DIRECT)
                         .withContract(PAYM)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")))
                         .withCurrentPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForO2Payg4GConsumerVideoAudioFreeTrialHasNotBeenActivated() {
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false)
                         .withContract(PAYG)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Payg4GConsumerVideoAudioFreeTrialHasNotBeenActivated() {
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false)
                         .withContract(PAYG)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        Date multipleFreeTrialsStopDate = new DateTime().minus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForO2Payg4GConsumerVideoAudioFreeTrialHasBeenActivated() {
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(true)
                         .withContract(PAYG)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        Date multipleFreeTrialsStopDate = new DateTime().minus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForUserOnWhiteListedVideoAudioFreeTrial() {
        //given
        user = new User().withContractChanel(DIRECT)
                         .withContract(PAYM)
                         .withSegment(CONSUMER)
                         .withProvider(O2)
                         .withTariff(_4G)
                         .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")))
                         .withFreeTrialExpiredMillis(Long.MAX_VALUE)
                         .withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }


    @Test
    public void testUnsubscribeAndSkipFreeTrial_4GVideoAudioFreeTrialTo3G_Success() throws Exception {

        currentTimeMillis = 0L;
        currentUserTariff = _4G;
        newUserTariff = _3G;

        create4GVideoAudioSubscribedUserOnVideoAudioFreeTrial();

        mockDowngradeUserTariffMethodsCalls();

        actualUser = userServiceSpy.unsubscribeAndSkipFreeTrial(user, USER_DOWNGRADED_TARIFF);

        assertNotNull(actualUser);
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment() * 1000L));
        assertEquals(currentTimeMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }

    @Test
    public void shouldNotDowngradeUserOnWhiteListedVideoAudioFreeTrial() {
        //given
        User user =
            new User().withTariff(_4G).withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));
        O2SubscriberData o2SubscriberData = new O2SubscriberData().withTariff4G(false);

        PowerMockito.doReturn(null).when(userServiceSpy).downgradeUserTariff(any(User.class), any(Tariff.class));
        PowerMockito.doReturn(user).when(userRepository).save(user);
        PowerMockito.doReturn(user).when(o2UserDetailsUpdaterMock).setUserFieldsFromSubscriberData(user, o2SubscriberData);

        //when
        User actualUser = userServiceSpy.o2SubscriberDataChanged(user, o2SubscriberData);

        //then
        assertNotNull(actualUser);
        assertEquals(user, actualUser);

        verify(userServiceSpy, times(0)).downgradeUserTariff(any(User.class), any(Tariff.class));
        verify(o2UserDetailsUpdaterMock, times(1)).setUserFieldsFromSubscriberData(user, o2SubscriberData);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldDowngradeUser() {
        //given
        User user =
            new User().withTariff(_4G).withFreeTrialExpiredMillis(Long.MIN_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));
        O2SubscriberData o2SubscriberData = new O2SubscriberData().withTariff4G(false);

        PowerMockito.doReturn(user).when(userServiceSpy).downgradeUserTariff(any(User.class), any(Tariff.class));
        PowerMockito.doReturn(user).when(userRepository).save(user);
        PowerMockito.doReturn(user).when(o2UserDetailsUpdaterMock).setUserFieldsFromSubscriberData(user, o2SubscriberData);

        //when
        User actualUser = userServiceSpy.o2SubscriberDataChanged(user, o2SubscriberData);

        //then
        assertNotNull(actualUser);
        assertEquals(user, actualUser);

        verify(userServiceSpy, times(1)).downgradeUserTariff(any(User.class), any(Tariff.class));
        verify(o2UserDetailsUpdaterMock, times(1)).setUserFieldsFromSubscriberData(user, o2SubscriberData);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testPopulateSubscriberData_IsPromotedNumber_Success() throws Exception {
        //given
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        String phoneNumber = "+6421111111";
        user.setMobile(phoneNumber);
        Community community = user.getUserGroup().getCommunity();
        final O2SubscriberData subscriberData = new O2SubscriberData();

        doReturn(user).when(userRepository).save(eq(user));
        doReturn(true).when(deviceServiceMock).isPromotedDevicePhone(eq(community), eq(phoneNumber), anyString());
        doReturn(user).when(o2UserDetailsUpdaterMock).setUserFieldsFromSubscriberData(eq(user), any(O2SubscriberData.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                userServiceSpy.populateSubscriberData(user, (O2SubscriberData) invocationOnMock.getArguments()[0]);
                return user;
            }
        }).when(o2UserDetailsUpdaterMock).process(any(O2SubscriberData.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Processor<O2SubscriberData> processor = (Processor<O2SubscriberData>) invocationOnMock.getArguments()[1];
                processor.process(subscriberData);
                return null;
            }
        }).when(o2ClientServiceMock).getSubscriberData(eq(phoneNumber), any(Processor.class));

        //when
        userServiceSpy.populateSubscriberData(user);

        //then
        verify(userRepository, times(1)).save(eq(user));
        verify(deviceServiceMock, times(1)).isPromotedDevicePhone(eq(community), eq(phoneNumber), anyString());
        verify(o2UserDetailsUpdaterMock, times(0)).setUserFieldsFromSubscriberData(eq(user), eq(subscriberData));
        verify(o2UserDetailsUpdaterMock, times(1)).setUserFieldsFromSubscriberData(eq(user), eq((O2SubscriberData) null));
        verify(o2ClientServiceMock, times(0)).getSubscriberData(eq(phoneNumber), eq(o2UserDetailsUpdaterMock));
    }

    @Test
    public void testPopulateSubscriberData_IsNotPromotedNumber_Success() throws Exception {
        //given
        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        String phoneNumber = "+6421111111";
        user.setMobile(phoneNumber);
        Community community = user.getUserGroup().getCommunity();
        final O2SubscriberData subscriberData = new O2SubscriberData();

        doReturn(user).when(userRepository).save(eq(user));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                userServiceSpy.populateSubscriberData(user, (O2SubscriberData) invocationOnMock.getArguments()[0]);
                return user;
            }
        }).when(o2UserDetailsUpdaterMock).process(any(O2SubscriberData.class));
        doReturn(user).when(o2UserDetailsUpdaterMock).setUserFieldsFromSubscriberData(eq(user), any(O2SubscriberData.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Processor<O2SubscriberData> processor = (Processor<O2SubscriberData>) invocationOnMock.getArguments()[1];
                processor.process(subscriberData);
                return null;
            }
        }).when(o2ClientServiceMock).getSubscriberData(eq(phoneNumber), eq(o2UserDetailsUpdaterMock));

        //when
        userServiceSpy.populateSubscriberData(user);

        //then
        verify(userRepository, times(1)).save(eq(user));
        verify(deviceServiceMock, times(1)).isPromotedDevicePhone(eq(community), eq(phoneNumber), anyString());
        verify(o2UserDetailsUpdaterMock, times(1)).setUserFieldsFromSubscriberData(eq(user), eq(subscriberData));
        verify(o2UserDetailsUpdaterMock, times(0)).setUserFieldsFromSubscriberData(eq(user), eq((O2SubscriberData) null));
        verify(o2ClientServiceMock, times(1)).getSubscriberData(eq(phoneNumber), eq(o2UserDetailsUpdaterMock));
    }

    @Test
    public void shouldMergeUsers() {
        //given
        User oldUser = new User().withDeviceUID("d1").withDeviceModel("dm1").withDeviceType(new DeviceType()).withIpAddress("ip1");
        User currentUser = new User().withDeviceUID("d2").withDeviceModel("dm2").withDeviceType(new DeviceType()).withIpAddress("ip2");

        Mockito.doNothing().when(urbanAirshipTokenServiceMock).mergeToken(currentUser, oldUser);
        Mockito.doReturn(1).when(userRepository).deleteUser(currentUser.getId());
        Mockito.doReturn(oldUser).when(userRepository).save(oldUser);
        Mockito.doReturn(mock(AccountLog.class)).when(accountLogServiceMock).logAccountMergeEvent(oldUser, currentUser);

        //when
        User actualUser = userServiceSpy.mergeUser(oldUser, currentUser);

        //then
        assertThat(actualUser, is(oldUser));
        assertThat(actualUser.getDeviceUID(), is(currentUser.getDeviceUID()));
        assertThat(actualUser.getDeviceType(), is(currentUser.getDeviceType()));
        assertThat(actualUser.getDeviceModel(), is(currentUser.getDeviceModel()));
        assertThat(actualUser.getIpAddress(), is(currentUser.getIpAddress()));

        verify(urbanAirshipTokenServiceMock, times(1)).mergeToken(currentUser, oldUser);
        verify(deviceUserDataService, times(1)).removeDeviceUserData(currentUser);
        verify(deviceUserDataService, times(1)).removeDeviceUserData(oldUser);
        verify(userRepository, times(1)).deleteUser(currentUser.getId());
        verify(userRepository, times(1)).save(oldUser);
        verify(accountLogServiceMock, times(1)).logAccountMergeEvent(oldUser, currentUser);
        verify(appsFlyerDataService, times(1)).mergeAppsFlyerData(currentUser, oldUser);
    }

    @Test(expected = NullPointerException.class)
    public void shouldDoNotMergeUsersWhenOldUserIsNull() {
        //given
        User oldUser = null;
        User currentUser = new User().withDeviceUID("b");

        Mockito.doNothing().when(urbanAirshipTokenServiceMock).mergeToken(currentUser, oldUser);
        Mockito.doReturn(1).when(userRepository).deleteUser(currentUser.getId());
        Mockito.doReturn(oldUser).when(userRepository).save(oldUser);
        Mockito.doReturn(mock(AccountLog.class)).when(accountLogServiceMock).logAccountMergeEvent(oldUser, currentUser);

        //when
        userServiceSpy.mergeUser(oldUser, currentUser);
    }

    @Test(expected = NullPointerException.class)
    public void shouldDoNotMergeUsersWhenCurrentUserIsNull() {
        //given
        User oldUser = new User().withDeviceUID("a");
        User currentUser = null;

        Mockito.doNothing().when(urbanAirshipTokenServiceMock).mergeToken(currentUser, oldUser);
        Mockito.doNothing().when(userRepository).delete(currentUser);
        Mockito.doReturn(oldUser).when(userRepository).save(oldUser);
        Mockito.doReturn(mock(AccountLog.class)).when(accountLogServiceMock).logAccountMergeEvent(oldUser, currentUser);

        //when
        userServiceSpy.mergeUser(oldUser, currentUser);
    }

    @Test
    public void shouldAutoOptIn() {
        //given
        String userToken = "";
        String timestamp = "";
        String otac = "g";
        String userName = "";

        User expectedUser = new User().withUserName(userName)
                                      .withMobile("+380913008199")
                                      .withDeviceUID("")
                                      .withUserStatus(createUserStatus(LIMITED))
                                      .withActivationStatus(ENTERED_NUMBER)
                                      .withTariff(_3G)
                                      .withSegment(CONSUMER)
                                      .withProvider(O2)
                                      .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        PaymentDetails expectedPaymentDetails = new O2PSMSPaymentDetails().withOwner(expectedUser);

        doReturn(true).when(autoOptInRuleServiceMock).isSubjectToAutoOptIn(ALL, expectedUser);
        doReturn(expectedUser).when(userServiceSpy).checkCredentials(expectedUser.getUserName(), userToken, timestamp, expectedUser.getCommunityRewriteUrl());
        doAnswer(userWithPromoAnswer).when(promotionServiceMock).applyPotentialPromo(expectedUser);
        doReturn(expectedPaymentDetails).when(paymentDetailsServiceMock).createDefaultO2PsmsPaymentDetails(expectedUser);
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        doReturn(providerUserDetails).when(otacValidationServiceMock).validate(otac, expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity());
        doReturn(expectedUser).when(userRepository).findOne(expectedUser.getId());
        doReturn(expectedUser).when(userRepository).save(expectedUser);

        //when
        MergeResult opResult = userServiceSpy.autoOptIn(expectedUser.getCommunityRewriteUrl(), expectedUser.getUserName(), timestamp, userToken, expectedUser.getDeviceUID(), otac, false);
        User actualUser = opResult.getResultOfOperation();

        //then
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);

        verify(autoOptInRuleServiceMock, times(1)).isSubjectToAutoOptIn(ALL, expectedUser);
        verify(userServiceSpy, times(1)).checkCredentials(userName, userToken, timestamp, expectedUser.getCommunityRewriteUrl());
        verify(promotionServiceMock, times(1)).applyPotentialPromo(expectedUser);
        verify(paymentDetailsServiceMock, times(1)).createDefaultO2PsmsPaymentDetails(expectedUser);
        verify(otacValidationServiceMock, times(1)).validate(otac, expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity());
        verify(userRepository, times(1)).save(expectedUser);
    }

    @Test
    public void shouldAutoOptInWhenOtacIsNull() {
        //given
        String userName = "";
        String userToken = "";
        String timestamp = "";
        String otac = null;

        User expectedUser = new User().withUserName(userName)
                                      .withMobile("+380913008199")
                                      .withDeviceUID("")
                                      .withUserStatus(createUserStatus(LIMITED))
                                      .withActivationStatus(ENTERED_NUMBER)
                                      .withTariff(_3G)
                                      .withSegment(CONSUMER)
                                      .withProvider(O2)
                                      .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        PaymentDetails expectedPaymentDetails = new O2PSMSPaymentDetails().withOwner(expectedUser);

        doReturn(true).when(autoOptInRuleServiceMock).isSubjectToAutoOptIn(ALL, expectedUser);
        doReturn(expectedUser).when(userServiceSpy).checkCredentials(expectedUser.getUserName(), userToken, timestamp, expectedUser.getCommunityRewriteUrl());
        doAnswer(userWithPromoAnswer).when(promotionServiceMock).applyPotentialPromo(expectedUser);
        doReturn(expectedPaymentDetails).when(paymentDetailsServiceMock).createDefaultO2PsmsPaymentDetails(expectedUser);
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        doReturn(providerUserDetails).when(o2ClientServiceMock).getUserDetails(otac, expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity());
        doReturn(expectedUser).when(userRepository).findOne(expectedUser.getId());

        //when
        MergeResult opResult = userServiceSpy.autoOptIn(expectedUser.getCommunityRewriteUrl(), expectedUser.getUserName(), timestamp, userToken, expectedUser.getDeviceUID(), otac, false);
        User actualUser = opResult.getResultOfOperation();

        //then
        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);

        verify(userServiceSpy, times(1)).checkCredentials(userName, userToken, timestamp, expectedUser.getCommunityRewriteUrl());
        verify(autoOptInRuleServiceMock, times(1)).isSubjectToAutoOptIn(ALL, expectedUser);
        verify(userServiceSpy, times(0)).checkCredentials(userName, userToken, timestamp, expectedUser.getCommunityRewriteUrl(), expectedUser.getDeviceUID());
        verify(promotionServiceMock, times(1)).applyPotentialPromo(expectedUser);
        verify(paymentDetailsServiceMock, times(1)).createDefaultO2PsmsPaymentDetails(expectedUser);
        verify(o2ClientServiceMock, times(0)).getUserDetails(otac, expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity());
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotAutoOptInBecauseOfNoPromotion() {
        //given
        String userName = "";
        String userToken = "";
        String timestamp = "";
        String otac = "";

        User expectedUser = new User().withUserName(userName)
                                      .withMobile("+380913008199")
                                      .withDeviceUID("")
                                      .withUserStatus(createUserStatus(LIMITED))
                                      .withActivationStatus(ENTERED_NUMBER)
                                      .withTariff(_3G)
                                      .withSegment(CONSUMER)
                                      .withProvider(O2)
                                      .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        PaymentDetails expectedPaymentDetails = new O2PSMSPaymentDetails().withOwner(expectedUser);

        doReturn(expectedUser).when(userServiceSpy).checkCredentials(expectedUser.getUserName(), userToken, timestamp, expectedUser.getCommunityRewriteUrl());
        doAnswer(userWithoutPromoAnswer).when(promotionServiceMock).applyPotentialPromo(expectedUser);
        doReturn(expectedPaymentDetails).when(paymentDetailsServiceMock).createDefaultO2PsmsPaymentDetails(expectedUser);
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        doReturn(providerUserDetails).when(o2ClientServiceMock).getUserDetails(otac, expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity());

        //when
        userServiceSpy.autoOptIn(expectedUser.getCommunityRewriteUrl(), expectedUser.getUserName(), timestamp, userToken, expectedUser.getDeviceUID(), otac, false);
    }

    @Test(expected = RuntimeException.class)
    public void shouldDoNotAutoOptInBecauseOfException() {
        //given
        String userName = "";
        String userToken = "";
        String timestamp = "";
        String communityUri = "";
        String deviceUID = "";
        String otac = "";

        User expectedUser = new User().withProvider(O2).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        doReturn(expectedUser).when(userServiceSpy).checkCredentials(userName, userToken, timestamp, communityUri, deviceUID);
        doReturn(true).when(promotionServiceMock).applyPotentialPromo(expectedUser);
        Mockito.doThrow(new RuntimeException()).when(paymentDetailsServiceMock).createDefaultO2PsmsPaymentDetails(expectedUser);
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        doReturn(providerUserDetails).when(o2ClientServiceMock).getUserDetails(otac, expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity());

        //when
        userServiceSpy.autoOptIn(expectedUser.getCommunityRewriteUrl(), expectedUser.getUserName(), timestamp, userToken, expectedUser.getDeviceUID(), otac, false);
    }

    @Test(expected = ServiceException.class)
    public void shouldDoNotAutoOptInBecauseOfUserIsNotSubjectToAutoOptIn() {
        //given
        String userName = "";
        String userToken = "";
        String timestamp = "";
        String communityUri = "";
        String deviceUID = "";
        String otac = "";

        User expectedUser = new User().withProvider(O2).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        PaymentDetails expectedPaymentDetails = new O2PSMSPaymentDetails().withOwner(expectedUser);

        doReturn(expectedUser).when(userServiceSpy).checkCredentials(expectedUser.getUserName(), userToken, timestamp, expectedUser.getCommunityRewriteUrl());
        doAnswer(userWithPromoAnswer).when(promotionServiceMock).applyPotentialPromo(expectedUser);
        doReturn(expectedPaymentDetails).when(paymentDetailsServiceMock).createDefaultO2PsmsPaymentDetails(expectedUser);
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        doReturn(providerUserDetails).when(o2ClientServiceMock).getUserDetails(otac, expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity());

        //when
        userServiceSpy.autoOptIn(expectedUser.getCommunityRewriteUrl(), expectedUser.getUserName(), timestamp, userToken, expectedUser.getDeviceUID(), otac, false);
    }

    @Test
    public void shouldAutoOptInPromoCampaignUser() {
        //given
        String userToken = "";
        String timestamp = "";
        String otac = "g";
        String userName = "";

        final User deviceUIdUser = new User().withId(1)
                                             .withUserName(userName)
                                             .withMobile("+380913008199")
                                             .withDeviceUID("")
                                             .withUserStatus(createUserStatus(LIMITED))
                                             .withActivationStatus(ENTERED_NUMBER)
                                             .withTariff(_3G)
                                             .withSegment(CONSUMER)
                                             .withProvider(O2)
                                             .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        doReturn(true).when(autoOptInRuleServiceMock).isSubjectToAutoOptIn(ALL, deviceUIdUser);
        doReturn(deviceUIdUser).when(userServiceSpy).checkCredentials(deviceUIdUser.getUserName(), userToken, timestamp, deviceUIdUser.getCommunityRewriteUrl());
        doAnswer(userWithPromoAnswer).when(promotionServiceMock).applyPotentialPromo(deviceUIdUser);
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        doReturn(providerUserDetails).when(otacValidationServiceMock).validate(otac, deviceUIdUser.getMobile(), deviceUIdUser.getUserGroup().getCommunity());
        doReturn(deviceUIdUser).when(userRepository).findOne(deviceUIdUser.getId());
        doReturn(deviceUIdUser).when(userRepository).save(deviceUIdUser);
        User mobileUser = new User().withId(2);
        doReturn(mobileUser).when(userRepository).findByUserNameAndCommunityAndOtherThanPassedId(deviceUIdUser.getMobile(), deviceUIdUser.getUserGroup().getCommunity(), deviceUIdUser.getId());

        doAnswer(new Answer() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                doReturn(null).when(userRepository).findOne(deviceUIdUser.getId());
                return 1;
            }
        }).when(userRepository).deleteUser(deviceUIdUser.getId());
        doReturn(mobileUser).when(userRepository).findOne(mobileUser.getId());

        PaymentDetails expectedPaymentDetails = new O2PSMSPaymentDetails().withOwner(mobileUser);
        doReturn(expectedPaymentDetails).when(paymentDetailsServiceMock).createDefaultO2PsmsPaymentDetails(mobileUser);
        doReturn(mobileUser).when(userRepository).save(mobileUser);

        Promotion promotion = new Promotion();
        doReturn(promotion).when(promotionServiceMock).getPromotionFromRuleForAutoOptIn(mobileUser);
        doAnswer(userWithPromoAnswer).when(promotionServiceMock).applyPromotionByPromoCode(mobileUser, promotion);

        //when
        MergeResult opResult = userServiceSpy.autoOptIn(deviceUIdUser.getCommunityRewriteUrl(), deviceUIdUser.getUserName(), timestamp, userToken, deviceUIdUser.getDeviceUID(), otac, false);
        User actualUser = opResult.getResultOfOperation();

        //then
        assertNotNull(actualUser);
        assertEquals(mobileUser, actualUser);

        verify(autoOptInRuleServiceMock, times(1)).isSubjectToAutoOptIn(ALL, deviceUIdUser);
        verify(userServiceSpy, times(1)).checkCredentials(userName, userToken, timestamp, deviceUIdUser.getCommunityRewriteUrl());
        verify(otacValidationServiceMock, times(1)).validate(otac, deviceUIdUser.getMobile(), deviceUIdUser.getUserGroup().getCommunity());
        verify(promotionServiceMock, times(1)).applyPromotionByPromoCode(mobileUser, promotion);
        verify(paymentDetailsServiceMock, times(1)).createDefaultO2PsmsPaymentDetails(mobileUser);
        verify(userRepository, times(2)).save(mobileUser);
        verify(appsFlyerDataService, times(1)).mergeAppsFlyerData(deviceUIdUser, mobileUser);
    }

    @Test
    public void shouldFindUserTree() {
        //given
        User expectedUser = new User().withMobile("mobile").withDeviceUID("deviceUID").withOldUser(new User()).withUserGroup(new UserGroup().withCommunity(new Community()));
        doReturn(expectedUser).when(userRepository).findUserTree(expectedUser.getId());
        doReturn(expectedUser.getOldUser()).when(userRepository)
                                           .findByUserNameAndCommunityAndOtherThanPassedId(expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity(), expectedUser.getId());

        //when
        User actualUser = userServiceSpy.findUserTree(expectedUser.getId());

        //then
        assertThat(actualUser, is(expectedUser));
        assertThat(actualUser.getOldUser(), is(expectedUser.getOldUser()));

        verify(userRepository, times(1)).findUserTree(expectedUser.getId());
        verify(userRepository, times(1)).findByUserNameAndCommunityAndOtherThanPassedId(expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity(), expectedUser.getId());
    }

    @Test
    public void shouldFindUserTreeWithOutOldUser() {
        //given
        User expectedUser = new User().withMobile("mobile").withDeviceUID("deviceUID").withOldUser(new User()).withUserGroup(new UserGroup().withCommunity(new Community()));
        doReturn(expectedUser).when(userRepository).findUserTree(expectedUser.getId());
        doReturn(null).when(userRepository).findByUserNameAndCommunityAndOtherThanPassedId(expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity(), expectedUser.getId());

        //when
        User actualUser = userServiceSpy.findUserTree(expectedUser.getId());

        //then
        assertThat(actualUser, is(expectedUser));
        assertThat(actualUser.getOldUser(), is(nullValue()));

        verify(userRepository, times(1)).findUserTree(expectedUser.getId());
        verify(userRepository, times(1)).findByUserNameAndCommunityAndOtherThanPassedId(expectedUser.getMobile(), expectedUser.getUserGroup().getCommunity(), expectedUser.getId());
    }

    @Test
    public void shouldNotFindUserTree() {
        //given
        int userId = Integer.MAX_VALUE;
        doReturn(null).when(userRepository).findUserTree(userId);

        //when
        User actualUser = userServiceSpy.findUserTree(userId);

        //then
        assertThat(actualUser, is((User) null));

        verify(userRepository, times(1)).findUserTree(userId);
    }


    @Test
    public void shouldReturnTrue_OnIsTempUserName_WhenEqualUsernameAndDeviceUID() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserName(user.getDeviceUID());

        boolean result = user.isTempUserName();

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrue_OnIsActivatedUserName_WhenEqualUsernameAndMobile() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setUserName(user.getMobile());

        boolean result = user.isActivatedUserName();

        assertTrue(result);
    }

    @Test
    public void shouldReturnTrue_OnHasPhoneNumber_WhenMobileNotNUll() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setMobile("+4440000000001");

        boolean result = user.hasPhoneNumber();

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalse_OnHasPhoneNumber_WhenMobileNUll() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setMobile(null);

        boolean result = user.hasPhoneNumber();

        assertFalse(result);
    }

    @Test
    public void shouldReturnFalse_OnHasPhoneNumber_WhenMobileEmpty() {
        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setMobile(null);

        boolean result = user.hasPhoneNumber();

        assertFalse(result);
    }

    @Test(expected = UserCredentialsException.class)
    public void shouldThrow_OnCheckCredentials_BecauseUserInvalid() {
        String userName = "";
        String token = "";
        String timestamp = "";
        String communityName = "";

        User user = Mockito.mock(User.class);
        PowerMockito.when(user.getActivationStatus()).thenReturn(REGISTERED);
        PowerMockito.when(user.hasAllDetails()).thenReturn(false);

        PowerMockito.doReturn(user).when(userRepository).findByUserNameAndCommunityUrl(anyString(), anyString());
        PowerMockito.doNothing().when(userActivationStatusService).checkActivationStatus(eq(user));

        userServiceSpy.checkCredentials(userName, token, timestamp, communityName);

        verify(userRepository, times(1)).findByUserNameAndCommunityUrl(anyString(), anyString());
        verify(userActivationStatusService, times(1)).checkActivationStatus(eq(user));
    }


    @Test
    public void shouldNotUpdateProvider() {

        //given
        User user = new User().withUserName("userName")
                              .withProvider(VF)
                              .withActivationStatus(ENTERED_NUMBER)
                              .withMobile("mobile")
                              .withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl(VF_NZ_COMMUNITY_REWRITE_URL)));
        User mobileUser = null;
        String otac = "otac";
        boolean isMajorApiVersionNumberLessThan4 = false;
        boolean isApplyingWithoutEnterPhone = false;

        Mockito.when(otacValidationServiceMock.validate(otac, user.getMobile(), user.getUserGroup().getCommunity())).thenReturn(new ProviderUserDetails());
        Mockito.when(promotionServiceMock.applyPotentialPromo(user)).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                return user.withIsPromotionApplied(true);
            }
        });
        Mockito.when(userRepository.save(user)).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = (User) invocation.getArguments()[0];
                return user.withActivationStatus(ACTIVATED).withUserName(user.getMobile());
            }
        });

        //when
        MergeResult opResult = userServiceSpy.applyInitPromo(user, mobileUser, otac, isMajorApiVersionNumberLessThan4, isApplyingWithoutEnterPhone, false);
        User actualUser = opResult.getResultOfOperation();

        //then
        assertThat(actualUser, is(user));
        assertThat(actualUser.getActivationStatus(), is(ACTIVATED));
        assertThat(actualUser.getProvider(), is(VF));
        assertThat(actualUser.getUserName(), is(user.getMobile()));
        assertThat(actualUser.isHasPromo(), is(true));
    }

    @Test
    public void shouldUpdateLastWebLogin() {
        //given
        int userId = Integer.MAX_VALUE;

        User user = new User();
        when(userRepository.findOne(userId)).thenReturn(user);
        final int nowSeconds = Integer.MAX_VALUE;
        when(timeServiceMock.nowSeconds()).thenReturn(nowSeconds);

        //when
        final User actualUser = userServiceSpy.updateLastWebLogin(userId);

        //then
        assertThat(actualUser, is(user));
        assertThat(actualUser.getLastWebLogin(), is(nowSeconds));

        verify(userRepository).findOne(userId);
        verify(timeServiceMock).nowSeconds();
    }


    private void create4GVideoAudioSubscribedUserOnVideoAudioFreeTrial() {
        paymentPolicyTariff = _4G;
        mediaType = VIDEO_AND_AUDIO;

        freeTrialStartedTimestampMillis = currentTimeMillis;
        freeTrialExpiredMillis = freeTrialStartedTimestampMillis + YEAR_SECONDS * 1000L;
        nextSubPayment = (int) (freeTrialExpiredMillis / 1000);

        createUserWithO2PaymentDetails();
    }

    private void create4GOnBoughtAudioSubscriptionUser() {
        paymentPolicyTariff = _4G;
        mediaType = AUDIO;

        setFreeTrialInThePastNextSubPaymentInTheFuture();

        createUserWithO2PaymentDetails();
    }

    private void create4GOnBoughtVideoAudioSubscriptionUser() {
        paymentPolicyTariff = _4G;
        mediaType = VIDEO_AND_AUDIO;

        create4GVideoAudioLastSuccessfulPaymentDetails();
    }

    private void create4GVideoAudioLastSuccessfulPaymentDetails() {
        lastSuccessfulPaymentPolicyTariff = _4G;
        lastSuccessfulPaymentPolicyMediaType = VIDEO_AND_AUDIO;

        setFreeTrialInThePastNextSubPaymentInTheFuture();

        createLastSuccessfulPaymentDetailsWithPaymentPolicy();
        createUserWithO2PaymentDetails();
    }

    private void create4GMusicSubscribedOnBoughtVideoAudioSubscriptionYetUser() {
        paymentPolicyTariff = _4G;
        mediaType = AUDIO;

        create4GVideoAudioLastSuccessfulPaymentDetails();
    }

    private void setFreeTrialInThePastNextSubPaymentInTheFuture() {
        freeTrialStartedTimestampMillis = currentTimeMillis - 1000L;
        freeTrialExpiredMillis = currentTimeMillis - 1L;
        nextSubPayment = (int) (freeTrialExpiredMillis / 1000) + 10;
    }

    private void setFreeTrialAndNextSubPaymentInThePast() {
        freeTrialStartedTimestampMillis = currentTimeMillis - 1000L;
        freeTrialExpiredMillis = currentTimeMillis - 1L;
        nextSubPayment = (int) (freeTrialExpiredMillis / 1000);
    }

    private void createUserWithO2PaymentDetails() {

        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentPolicy.setTariff(paymentPolicyTariff);
        paymentPolicy.setMediaType(mediaType);

        O2PSMSPaymentDetails o2PSMSPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        o2PSMSPaymentDetails.setPaymentPolicy(paymentPolicy);
        o2PSMSPaymentDetails.setActivated(true);

        Community community = new Community();
        community.setRewriteUrlParameter("o2");

        UserGroup userGroup = new UserGroup();
        userGroup.setCommunity(community);

        user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setTariff(currentUserTariff);
        user.setLastPromo(new PromoCode().withCode("testCode").withMediaType(VIDEO_AND_AUDIO));

        user.setFreeTrialStartedTimestampMillis(freeTrialStartedTimestampMillis);
        user.setFreeTrialExpiredMillis(freeTrialExpiredMillis);
        user.setNextSubPayment(nextSubPayment);

        user.setCurrentPaymentDetails(o2PSMSPaymentDetails);
        user.setUserGroup(userGroup);
        user.setProvider(O2);
        user.setLastSuccessfulPaymentDetails(lastSuccessfulPaymentDetails);
    }

    private void createLastSuccessfulPaymentDetailsWithPaymentPolicy() {
        PaymentPolicy lastSuccessfulPaymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        lastSuccessfulPaymentPolicy.setTariff(lastSuccessfulPaymentPolicyTariff);
        lastSuccessfulPaymentPolicy.setMediaType(lastSuccessfulPaymentPolicyMediaType);

        lastSuccessfulPaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        lastSuccessfulPaymentDetails.setPaymentPolicy(lastSuccessfulPaymentPolicy);
        lastSuccessfulPaymentDetails.setActivated(false);
    }

    private void mockDowngradeUserTariffMethodsCalls() throws Exception {
        mockStatic(Utils.class);
        PowerMockito.when(getEpochMillis()).thenReturn(currentTimeMillis);
        currentTimeSeconds = (int) (currentTimeMillis / 1000);
        PowerMockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);

        Mockito.doReturn(user.getLastPromo().getCode()).when(promotionServiceMock).getVideoCodeForO24GConsumer(user);
        Mockito.doReturn(user).when(userServiceSpy).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        Mockito.doAnswer(userWithPromoAnswer).when(promotionServiceMock).applyPotentialPromo(user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        Mockito.doReturn(null).when(accountLogServiceMock).logAccountEvent(user.getId(), user.getSubBalance(), null, null, BOUGHT_PERIOD_SKIPPING);
        Mockito.doReturn(null).when(accountLogServiceMock).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TRIAL_SKIPPING);
    }
}
