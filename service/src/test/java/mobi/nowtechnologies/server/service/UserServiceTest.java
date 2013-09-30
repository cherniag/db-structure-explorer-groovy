package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.dao.*;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.repository.UserBannedRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.FacebookService.UserCredentions;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.o2.impl.O2SubscriberData;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.MigResponseFactory;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTOFactory;
import mobi.nowtechnologies.server.shared.dto.UserFacebookDetailsDto;
import mobi.nowtechnologies.server.shared.dto.UserFacebookDetailsDtoFactory;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDtoFactory;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.Utils.*;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ENTERED_NUMBER;
import static mobi.nowtechnologies.server.shared.enums.Contract.*;
import static mobi.nowtechnologies.server.shared.enums.ContractChannel.*;
import static mobi.nowtechnologies.server.shared.enums.MediaType.*;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.*;
import static mobi.nowtechnologies.server.shared.enums.Tariff.*;
import static mobi.nowtechnologies.server.shared.util.DateUtils.newDate;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * 
 * @generatedBy CodePro at 20.08.12 18:31
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 */
@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserService.class, UserStatusDao.class, Utils.class, DeviceTypeDao.class, UserGroupDao.class, OperatorDao.class, AccountLog.class, EmailValidator.class })
public class UserServiceTest {

    private static final String SMS_SUCCESFULL_PAYMENT_TEXT = "SMS_SUCCESFULL_PAYMENT_TEXT";
	private static final String SMS_SUCCESFULL_PAYMENT_TEXT_MESSAGE_CODE = "sms.succesfullPayment.text";
	private static final String UNSUBSCRIBED_BY_ADMIN = "Unsubscribed by admin";
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
	private O2ClientService o2ClientServiceMock;
	private O2Service o2ServiceMock;
	private DeviceService deviceServiceMock;
	private FacebookService facebookServiceMock;
	private ITunesService iTunesServiceMock;
    private UserBannedRepository userBannedRepositoryMock;
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

    @Test
	public void testChangePassword_Success() throws Exception {
		String password = "newPa$$1";

		User user = UserFactory.createUser();
		String storedToken = createStoredToken(user.getUserName(), password);

		Mockito.when(entityServiceMock.findById(User.class, user.getId())).thenReturn(user);
		PowerMockito.when(userRepositoryMock.updateFields(Mockito.eq(storedToken), Mockito.eq(user.getId()))).thenReturn(1);

		User result = userServiceSpy.changePassword(user.getId(), password);

		assertNotNull(result);
		assertEquals(result, user);

        verify(userRepositoryMock, times(1));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = Exception.class)
	public void testChangePassword_Error() throws Exception {
		String password = "newPa$$1";

		User user = UserFactory.createUser();
		String storedToken = createStoredToken(user.getUserName(), password);

		Mockito.when(entityServiceMock.findById(User.class, user.getId())).thenReturn(user);
		PowerMockito.when(userRepositoryMock.updateFields(Mockito.eq(storedToken), Mockito.eq(user.getId()))).thenThrow(new Exception());

		userServiceSpy.changePassword(user.getId(), password);
	}

	@Test
	public void testFindUsers_Success() throws Exception {
		String searchWords = "Led Zeppelin";
		String communityURL = "nowtop40";

		List<User> mockedUserCollection = UserFactory.getUserUnmodifableList();

		PowerMockito.when(userRepositoryMock.findUser(Mockito.eq(communityURL), Mockito.eq("%" + searchWords + "%"))).thenReturn(mockedUserCollection);

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

		PowerMockito.when(userRepositoryMock.findUser(Mockito.eq(communityURL), Mockito.eq("%" + searchWords + "%"))).thenThrow(new RuntimeException());

		Collection<User> result = userServiceSpy.findUsers(searchWords, communityURL);

		assertNotNull(result);
		assertEquals(mockedUserCollection, result);
	}

	@Test
    @Ignore
	public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentInTheFutureAndSubBalanceIsChangedAndIsFreeTrialIsTrue_Success() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;
		int nextSubPayment = Utils.getEpochSeconds() + 24 * 60 * 60;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(3);
		userDto.setNextSubPayment(new Date(nextSubPayment * 1000L + 200000L));
		userDto.setPaymentEnabled(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(nextSubPayment);
		mockedUser.setFreeTrialExpiredMillis(new Long(nextSubPayment * 1000L));
		mockedUser.setLastSuccessfulPaymentTimeMillis(0L);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.TRIAL_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(entityServiceMock.updateEntity(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

		verify(accountLogServiceMock).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.TRIAL_TOPUP, null);
		verify(accountLogServiceMock).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test
	public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentInTheFutureAndSubBalanceIsChangedAndIsFreeTrialIsFalse_Success() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(3);
		userDto.setNextSubPayment(new Date());
		userDto.setPaymentEnabled(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(0);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(userRepositoryMock.save(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

		verify(accountLogServiceMock).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test
	public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentIsTheSameAndSubBalanceIsChangedAndIsFreeTrialIsFalse_Success() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;
		final int nextSubPayment = 5;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(3);
		userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
		userDto.setPaymentEnabled(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(nextSubPayment);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(userRepositoryMock.save(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test
	public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentIsTheSameAndSubBalanceIsTheSameAndIsFreeTrialIsTrue_Success() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;
		final int nextSubPayment = 5;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(originalSubBalance);
		userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
		userDto.setPaymentEnabled(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(nextSubPayment);
		mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(userRepositoryMock.save(mockedUser)).thenReturn(mockedUser);
		
		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test
	public void testUpdateUser_PaymentEnabledIsTrueAndNextSubPaymentIsTheSameAndSubBalanceIsTheSameAndIsFreeTrialIsFalse_Success() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;
		final int nextSubPayment = 5;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(originalSubBalance);
		userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
		userDto.setPaymentEnabled(true);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		paymentDetails.setActivated(true);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(nextSubPayment);
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(userRepositoryMock.save(mockedUser)).thenReturn(mockedUser);
		
		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test
	public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentIsTheSameAndSubBalanceIsTheSameAndIsFreeTrialIsTrueAndCurrentPaymentDetailsIsNull_Success() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;
		final int nextSubPayment = 5;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(originalSubBalance);
		userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
		userDto.setPaymentEnabled(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(nextSubPayment);

		mockedUser.setCurrentPaymentDetails(null);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(userRepositoryMock.save(mockedUser)).thenReturn(mockedUser);
		
		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());

		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test(expected = ServiceException.class)
	public void testUpdateUser_NextSubPaymentIsMoreThanOriginal_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(3);
		userDto.setNextSubPayment(new Date(2L));
		userDto.setPaymentEnabled(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(30000000);
		mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(entityServiceMock.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock, times(1)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test(expected = ServiceException.class)
	public void testUpdateUser_UserIsNull_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(3);
		userDto.setNextSubPayment(new Date());
		userDto.setPaymentEnabled(false);

		User mockedUser = null;

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(entityServiceMock.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test(expected = ServiceException.class)
	public void testUpdateUser_OriginalPaymentEnabledIsFalse_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;
		final int nextSubPayment = 5;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(originalSubBalance);
		userDto.setNextSubPayment(new Date(nextSubPayment * 1000L));
		userDto.setPaymentEnabled(true);

		PaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		migPaymentDetails.setActivated(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(nextSubPayment);
		mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());
		mockedUser.setCurrentPaymentDetails(migPaymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(accountLogServiceMock.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(entityServiceMock.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		verify(accountLogServiceMock, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	@Test(expected = RuntimeException.class)
	public void testUpdateUser_UserStatusDao_getUserStatusMapIdAsKey_RuntimeException_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		User mockedUser = UserFactory.createUser();

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenThrow(new RuntimeException());

		userServiceSpy.updateUser(userDto);
	}

	@Test(expected = RuntimeException.class)
	public void testUpdateUser_UserRepository_findOne_RuntimeException_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		PowerMockito.when(userRepositoryMock.findOne(userDto.getId())).thenThrow(new RuntimeException());

		userServiceSpy.updateUser(userDto);
	}

	@Test(expected = NullPointerException.class)
	public void testUpdateUser_userDtoIsNull_Failure() throws Exception {
		UserDto userDto = null;

		userServiceSpy.updateUser(userDto);
	}

	@Before
	public void setUp() throws Exception {
		userServiceSpy = Mockito.spy(new UserService());

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
        userServiceNotification = PowerMockito.mock(UserServiceNotification.class);
        otacValidationServiceMock = PowerMockito.mock(OtacValidationService.class);

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
        userServiceSpy.setUserServiceNotification(userServiceNotification);
        userServiceSpy.setO2UserDetailsUpdater(o2UserDetailsUpdaterMock);
        userServiceSpy.setOtacValidationService(otacValidationServiceMock);

		PowerMockito.mockStatic(UserStatusDao.class);
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

		Mockito.when(
				userRepositoryMock.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
						deltaSuccessfulPaymentSumsSendingTimestampMillis)).thenReturn(users);

		List<User> actualUsers = userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification,
				deltaSuccessfulPaymentSumsSendingTimestampMillis);

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

		Mockito.when(
				userRepositoryMock.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
						deltaSuccessfulPaymentSumsSendingTimestampMillis)).thenReturn(users);

		userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccessfulPaymentSumsSendingTimestampMillis);

		verify(userRepositoryMock, times(0)).findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
				deltaSuccessfulPaymentSumsSendingTimestampMillis);
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

		Mockito.when(
				userRepositoryMock.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
						deltaSuccessfulPaymentSumsSendingTimestampMillis)).thenReturn(users);

		userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccessfulPaymentSumsSendingTimestampMillis);

		verify(userRepositoryMock, times(0)).findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
				deltaSuccessfulPaymentSumsSendingTimestampMillis);
		PowerMockito.verifyStatic(times(0));
		getEpochMillis();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testResetSmsAccordingToLawAttributes_Success() {
		User user = UserFactory.createUser();

		long epochMillis = 25L;

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(getEpochMillis()).thenReturn(epochMillis);

		Mockito.when(userRepositoryMock.updateFields(BigDecimal.ZERO, epochMillis, user.getId())).thenReturn(1);

		User actualUser = userServiceSpy.resetSmsAccordingToLawAttributes(user);

		assertEquals(user, actualUser);
		assertEquals(BigDecimal.ZERO, actualUser.getAmountOfMoneyToUserNotification());
		assertEquals(epochMillis, actualUser.getLastSuccesfullPaymentSmsSendingTimestampMillis());

		verify(userRepositoryMock).updateFields(BigDecimal.ZERO, epochMillis, user.getId());
	}

	@SuppressWarnings("deprecation")
	@Test(expected = ServiceException.class)
	public void testResetSmsAccordingToLawAttributes_Failure() {
		User user = UserFactory.createUser();

		long epochMillis = 25L;

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(getEpochMillis()).thenReturn(epochMillis);

		Mockito.when(userRepositoryMock.updateFields(BigDecimal.ZERO, epochMillis, user.getId())).thenReturn(0);

		User actualUser = userServiceSpy.resetSmsAccordingToLawAttributes(user);

		assertEquals(user, actualUser);
		assertEquals(BigDecimal.ZERO, actualUser.getAmountOfMoneyToUserNotification());
		assertEquals(epochMillis, actualUser.getLastSuccesfullPaymentSmsSendingTimestampMillis());

		verify(userRepositoryMock).updateFields(BigDecimal.ZERO, epochMillis, user.getId());
	}

	@SuppressWarnings("deprecation")
	@Test(expected = NullPointerException.class)
	public void testResetSmsAccordingToLawAttributes_UserIsNull_Failure() {
		User user = null;

		Mockito.when(entityServiceMock.updateEntity(user)).thenReturn(user);

		userServiceSpy.resetSmsAccordingToLawAttributes(user);

		verify(entityServiceMock, times(0)).updateEntity(user);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testPopulateAmountOfMoneyToUserNotification_Success() {
		final BigDecimal userAmountOfMoneyToUserNotification = BigDecimal.ONE;

		User user = UserFactory.createUser();
		user.setAmountOfMoneyToUserNotification(userAmountOfMoneyToUserNotification);

		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setAmount(BigDecimal.TEN);
		Mockito.when(userRepositoryMock.save(user)).thenReturn(user);

		User actualUser = userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

		assertEquals(user, actualUser);
		BigDecimal expectedAmountOfMoneyToUserNotification = userAmountOfMoneyToUserNotification.add(submittedPayment.getAmount());
		assertEquals(expectedAmountOfMoneyToUserNotification, actualUser.getAmountOfMoneyToUserNotification());
	}

	@SuppressWarnings("deprecation")
	@Test(expected = NullPointerException.class)
	public void testPopulateAmountOfMoneyToUserNotification_UserIsNull_Failure() {
		User user = null;

		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setAmount(BigDecimal.TEN);

		Mockito.when(entityServiceMock.updateEntity(user)).thenReturn(user);

		userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

		verify(entityServiceMock, times(0)).updateEntity(user);

	}

	@SuppressWarnings("deprecation")
	@Test(expected = NullPointerException.class)
	public void testPopulateAmountOfMoneyToUserNotification_SubmittedPaymentIsNull_Failure() {
		final BigDecimal userAmountOfMoneyToUserNotification = BigDecimal.ONE;

		User user = UserFactory.createUser();
		user.setAmountOfMoneyToUserNotification(userAmountOfMoneyToUserNotification);

		SubmittedPayment submittedPayment = null;

		Mockito.when(entityServiceMock.updateEntity(user)).thenReturn(user);

		userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

		verify(entityServiceMock, times(0)).updateEntity(user);

	}

	@Test(expected = IllegalArgumentException.class)
	public void unsubscribeUser_Failure() {
		long epochMillis = 12354L;
		User mockedUser = null;
		final String reason = null;

		PaymentDetails mockedCurrentPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();

		PowerMockito.mockStatic(Utils.class);

		Mockito.when(getEpochMillis()).thenReturn(epochMillis);
		PowerMockito.when(entityServiceMock.updateEntity(mockedUser)).thenReturn(mockedUser);
		PowerMockito.when(entityServiceMock.updateEntity(mockedCurrentPaymentDetails)).thenReturn(mockedCurrentPaymentDetails);

		userServiceSpy.unsubscribeUser(mockedUser, reason);

		verify(entityServiceMock, times(0)).updateEntity(mockedUser);
		verify(entityServiceMock, times(0)).updateEntity(mockedCurrentPaymentDetails);
	}

	@Test()
	public void unsubscribeUser_Success() {
		long epochMillis = 12354L;
		User mockedUser = UserFactory.createUser();
		final String reason = null;

		PaymentDetails mockedCurrentPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();

		mockedUser.setCurrentPaymentDetails(mockedCurrentPaymentDetails);

		PowerMockito.mockStatic(Utils.class);

		Mockito.when(getEpochMillis()).thenReturn(epochMillis);
		PowerMockito.when(entityServiceMock.updateEntity(mockedUser)).thenReturn(mockedUser);
		PowerMockito.when(entityServiceMock.updateEntity(mockedCurrentPaymentDetails)).thenReturn(mockedCurrentPaymentDetails);
		PowerMockito.when(paymentDetailsServiceMock.deactivateCurrentPaymentDetailsIfOneExist(mockedUser, reason)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.unsubscribeUser(mockedUser, reason);

		assertNotNull(actualUser);

		PaymentDetails actualCurrentPaymentDetails = actualUser.getCurrentPaymentDetails();

		assertFalse(actualCurrentPaymentDetails.isActivated());

		verify(entityServiceMock).updateEntity(mockedUser);
		verify(paymentDetailsServiceMock).deactivateCurrentPaymentDetailsIfOneExist(mockedUser, reason);

	}

	@Test()
	public void testMakeSuccesfullPaymentFreeSMSRequest_successfullMigResponse_Success() throws Exception {

		final long epochMillis = 123L;
		PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

		MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		migPaymentDetails.setPaymentPolicy(paymentPolicy);

		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		final BigDecimal amountOfMoneyToUserNotification = BigDecimal.ONE;
		User user = UserFactory.createUser(migPaymentDetails, amountOfMoneyToUserNotification, userGroup);

		final Object[] succesfullPaymentMessageArgs = new Object[] { community.getDisplayName(), paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(),
				paymentPolicy.getShortCode() };

		MigResponse succesfullMigResponse = MigResponseFactory.createSuccessfulMigResponse();

		final MigPaymentDetails currentMigPaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();
		mockMakeFreeSMSRequest(currentMigPaymentDetails, SMS_SUCCESFULL_PAYMENT_TEXT, succesfullMigResponse);
		mockMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter().toUpperCase(), SMS_SUCCESFULL_PAYMENT_TEXT_MESSAGE_CODE,
				succesfullPaymentMessageArgs, SMS_SUCCESFULL_PAYMENT_TEXT);
		PowerMockito.mockStatic(Utils.class);

		Mockito.when(getEpochMillis()).thenReturn(epochMillis);
		Mockito.when(userRepositoryMock.updateFields(epochMillis, user.getId())).thenReturn(1);

		Future<Boolean> futureMigResponse = userServiceSpy.makeSuccesfullPaymentFreeSMSRequest(user);

		assertNotNull(futureMigResponse);
		assertTrue(futureMigResponse.get());

		verify(migHttpServiceMock).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), SMS_SUCCESFULL_PAYMENT_TEXT);
	}

	@Test(expected = ServiceCheckedException.class)
	public void testMakeSuccesfullPaymentFreeSMSRequest_failureMigResponse_Failure() throws Exception {

		PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();

		MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		migPaymentDetails.setPaymentPolicy(paymentPolicy);

		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		final BigDecimal amountOfMoneyToUserNotification = BigDecimal.ONE;
		User user = UserFactory.createUser(migPaymentDetails, amountOfMoneyToUserNotification, userGroup);

		final Object[] succesfullPaymentMessageArgs = new Object[] { community.getDisplayName(), paymentPolicy.getSubcost(), paymentPolicy.getSubweeks(),
				paymentPolicy.getShortCode() };

		MigResponse failureMigResponse = MigResponseFactory.createFailMigResponse();

		final MigPaymentDetails currentMigPaymentDetails = (MigPaymentDetails) user.getCurrentPaymentDetails();
		mockMakeFreeSMSRequest(currentMigPaymentDetails, SMS_SUCCESFULL_PAYMENT_TEXT, failureMigResponse);
		mockMessage(user.getUserGroup().getCommunity().getRewriteUrlParameter().toUpperCase(), SMS_SUCCESFULL_PAYMENT_TEXT_MESSAGE_CODE,
				succesfullPaymentMessageArgs, SMS_SUCCESFULL_PAYMENT_TEXT);

		userServiceSpy.makeSuccesfullPaymentFreeSMSRequest(user);

		verify(migHttpServiceMock).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), SMS_SUCCESFULL_PAYMENT_TEXT);
	}

	private Object[] testRegisterUser(final String storedToken, String communityName, final String deviceUID
			, final String deviceTypeName, final String ipAddress
			, final boolean notExistUser, boolean notDeviceType) throws Exception {
		final User user = UserFactory.createUser();

		final Integer countryId = 1;
		final Integer operatorId = 1;
		final DeviceType deviceType = new DeviceType();
		deviceType.setName(deviceTypeName);
		user.setDeviceType(deviceType);
		final DeviceType noneDeviceType = new DeviceType();
		noneDeviceType.setName(DeviceTypeDao.NONE);
		final UserStatus userStatus = new UserStatus();
		userStatus.setName(UserStatusDao.LIMITED);
		user.setStatus(userStatus);
		final Community community = CommunityFactory.createCommunity();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Map<String, DeviceType> deviceTypeMap = Collections.singletonMap(deviceTypeName, notDeviceType ? null : deviceType);
		final Map<Byte, UserGroup> userGroupMap = Collections.singletonMap(community.getId(), userGroup);
		final Map<Integer, Operator> operatorMap = Collections.singletonMap(operatorId, new Operator());
		final UserDeviceRegDetailsDto userDeviceRegDetailsDto = new UserDeviceRegDetailsDto();
		userDeviceRegDetailsDto.setDEVICE_TYPE(deviceTypeName);
		userDeviceRegDetailsDto.setCOMMUNITY_NAME(communityName);
		userDeviceRegDetailsDto.setDEVICE_UID(deviceUID);
		userDeviceRegDetailsDto.setIpAddress(ipAddress);

		PowerMockito.mockStatic(Utils.class);
		PowerMockito.mockStatic(DeviceTypeDao.class);
		PowerMockito.mockStatic(UserStatusDao.class);
		PowerMockito.mockStatic(UserGroupDao.class);
		PowerMockito.mockStatic(OperatorDao.class);

		Mockito.doReturn(user).when(entityServiceMock).saveEntity(any(User.class));
		Mockito.when(createStoredToken(anyString(), anyString())).thenReturn(storedToken);
		Mockito.when(DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue()).thenReturn(deviceTypeMap);
		Mockito.when(DeviceTypeDao.getNoneDeviceType()).thenReturn(noneDeviceType);
		Mockito.when(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY()).thenReturn(userGroupMap);
		Mockito.when(OperatorDao.getMapAsIds()).thenReturn(operatorMap);
		Mockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(userStatus);
		Mockito.when(communityServiceMock.getCommunityByName(anyString())).thenReturn(community);
		Mockito.when(countryServiceMock.findIdByFullName(anyString())).thenReturn(countryId);
		doAnswer(new Answer<AccountCheckDTO>() {
			@Override
			public AccountCheckDTO answer(InvocationOnMock invocation) throws Throwable {
				AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
				
				if(notExistUser){
					assertNull(user.getPotentialPromoCodePromotion());
					assertNotNull(user.getUserGroup());
					assertEquals(user.getUserGroup().getName(), userGroup.getName());
					assertEquals(user.getCountry(), countryId.intValue());
					assertEquals(user.getIpAddress(), userDeviceRegDetailsDto.getIpAddress());

					long curTime = System.currentTimeMillis();
					assertEquals(user.getFirstDeviceLoginMillis() - user.getFirstDeviceLoginMillis() % 100000, curTime - curTime % 100000);
				}

				accountCheckDTO.setUserName(user.getUserName());
				accountCheckDTO.setUserToken(user.getToken());
				accountCheckDTO.setDeviceType(user.getDeviceType().getName());
				accountCheckDTO.setOperator(user.getOperator());
				accountCheckDTO.setStatus(user.getStatus().getName());
				accountCheckDTO.setDeviceUID(user.getDeviceUID());
				accountCheckDTO.setActivation(ActivationStatus.REGISTERED);

				return accountCheckDTO;
			}
		}).when(userServiceSpy).proceessAccountCheckCommandForAuthorizedUser(anyInt(), anyString(), anyString(), anyString());
		PowerMockito.doReturn(notExistUser ? null : user).when(userServiceSpy).findByDeviceUIDAndCommunityRedirectURL(anyString(), anyString());
		whenNew(User.class).withNoArguments().thenReturn(user);

		return new Object[] { operatorMap, userDeviceRegDetailsDto, user };
	}

	@SuppressWarnings("unchecked")
	@Test()
	public void testRegisterUser_WOPotentialPromo_Success() throws Exception {
		final String storedToken = "50c86945713ac8c870eafbc19980706b";
		final String communityName = "chartsnow";
		final String deviceUID = "imei_357841034540704";
		final String deviceTypeName = "android";
		final String ipAddress = "10.10.0.2";

		Object[] testData = testRegisterUser(storedToken, communityName, deviceUID, deviceTypeName, ipAddress, true, false);
		final Map<Integer, Operator> operatorMap = (Map<Integer, Operator>) testData[0];
		final UserDeviceRegDetailsDto userDeviceRegDetailsDto = (UserDeviceRegDetailsDto) testData[1];

		AccountCheckDTO accountCheckDTO = userServiceSpy.registerUser(userDeviceRegDetailsDto, false);

		assertNotNull(accountCheckDTO);
		assertEquals(accountCheckDTO.getUserToken(), storedToken);
		assertEquals(accountCheckDTO.getUserName(), deviceUID);
		assertEquals(accountCheckDTO.getDeviceType(), deviceTypeName);
		Entry<Integer, Operator> entry = operatorMap.entrySet().iterator().next();
		assertEquals(accountCheckDTO.getOperator(), entry.getKey());
		assertEquals(accountCheckDTO.getDeviceUID(), deviceUID);
		assertEquals(accountCheckDTO.getStatus(), UserStatusDao.LIMITED);
		assertEquals(accountCheckDTO.getActivation(), ActivationStatus.REGISTERED);

		verify(communityServiceMock, times(1)).getCommunityByName(anyString());
		verify(countryServiceMock, times(1)).findIdByFullName(anyString());
		verify(entityServiceMock, times(1)).saveEntity(any(User.class));
		verify(userServiceSpy, times(1)).proceessAccountCheckCommandForAuthorizedUser(anyInt(), anyString(), anyString(), anyString());
		verifyStatic(times(1));
		createStoredToken(anyString(), anyString());
		verifyStatic(times(1));
		DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue();
		verifyStatic(times(1));
		UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY();
		verifyStatic(times(1));
		OperatorDao.getMapAsIds();
		verifyStatic(times(1));
		UserStatusDao.getLimitedUserStatus();
	}

	@Test()
	public void testRegisterUser_WOPotentialPromo_NoneDeviceType_Success() throws Exception {
		String storedToken = "50c86945713ac8c870eafbc19980706b";
		final String communityName = "chartsnow";
		final String deviceUID = "imei_357841034540704";
		final String deviceTypeName = "";
		final String ipAddress = "10.10.0.2";

		Object[] testData = testRegisterUser(storedToken, communityName, deviceUID, deviceTypeName, ipAddress, true, true);
		final UserDeviceRegDetailsDto userDeviceRegDetailsDto = (UserDeviceRegDetailsDto) testData[1];

		AccountCheckDTO accountCheckDTO = userServiceSpy.registerUser(userDeviceRegDetailsDto, false);

		assertNotNull(accountCheckDTO);
		assertEquals(accountCheckDTO.getDeviceType(), DeviceTypeDao.NONE);
		
		verifyStatic(times(1));
		DeviceTypeDao.getNoneDeviceType();
	}

	@Test()
	public void testRegisterUser_WOPotentialPromo_ExistUser_Success() throws Exception {
		final String storedToken = "50c86945713ac8c870eafbc19980706b";
		final String communityName = "chartsnow";
		final String deviceUID = "imei_357841034540704";
		final String deviceTypeName = "android";
		final String ipAddress = "10.10.0.2";

		Object[] testData = testRegisterUser(storedToken, communityName, deviceUID, deviceTypeName, ipAddress, false, false);
		final User user = (User) testData[2];
		final UserDeviceRegDetailsDto userDeviceRegDetailsDto = (UserDeviceRegDetailsDto) testData[1];

		AccountCheckDTO accountCheckDTO = userServiceSpy.registerUser(userDeviceRegDetailsDto, false);

		assertNotNull(accountCheckDTO);
		assertEquals(accountCheckDTO.getUserToken(), user.getToken());
		assertEquals(accountCheckDTO.getUserName(), user.getUserName());

		verify(communityServiceMock, times(1)).getCommunityByName(anyString());
		verify(countryServiceMock, times(0)).findIdByFullName(anyString());
		verify(entityServiceMock, times(0)).saveEntity(any(User.class));
		verify(userServiceSpy, times(1)).proceessAccountCheckCommandForAuthorizedUser(anyInt(), anyString(), anyString(), anyString());
		verifyStatic(times(0));
		createStoredToken(anyString(), anyString());
		verifyStatic(times(1));
		DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue();
		verifyStatic(times(0));
		UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY();
		verifyStatic(times(0));
		OperatorDao.getMapAsIds();
		verifyStatic(times(0));
		UserStatusDao.getLimitedUserStatus();
	}
	
	@Test()
	public void testActivatePhoneNumber_Success() throws Exception {
		final String phone = "07870111111";
		final User user = UserFactory.createUser();

		Mockito.when(o2ClientServiceMock.validatePhoneNumber(anyString())).thenReturn("+447870111111");

		boolean populateO2SubscriberData = false;
		User userResult = userServiceSpy.activatePhoneNumber(user, phone, populateO2SubscriberData);

		assertNotNull(user);
		assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
		assertEquals("+447870111111", userResult.getMobile());

		verify(userRepositoryMock, times(1)).save(any(User.class));
		verify(o2ClientServiceMock, times(1)).validatePhoneNumber(anyString());
	}
	
	@Test()
	public void testActivatePhoneNumber_NullPhone_Success() throws Exception {
		final String phone = null;
		final User user = UserFactory.createUser();

		Mockito.when(o2ClientServiceMock.validatePhoneNumber(anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				String phone = (String)invocation.getArguments()[0];
				assertEquals(user.getMobile(), phone);
				
				return "+447870111111";
			}
		});
		boolean populateO2SubscriberData = false;
		User userResult = userServiceSpy.activatePhoneNumber(user, phone, populateO2SubscriberData);

		assertNotNull(user);
		assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
		assertEquals("+447870111111", userResult.getMobile());

		verify(userRepositoryMock, times(1)).save(any(User.class));
		verify(o2ClientServiceMock, times(1)).validatePhoneNumber(anyString());
	}

	@Test
	public void testIsnonO2User_nonO2User_Success() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("provider");
		
		boolean isnonO2User = userServiceSpy.isnonO2User(user);
		assertTrue(isnonO2User);
	}
	
	@Test
	public void testIsnonO2User_O2User_Success() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		
		boolean isnonO2User = userServiceSpy.isnonO2User(user);
		assertFalse(isnonO2User);
	}
	
	@Test
	public void testIsnonO2User_UserFromNotO2Community_Success() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("r");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider(null);
		
		boolean isnonO2User = userServiceSpy.isnonO2User(user);
		assertFalse(isnonO2User);
	}
	
	@Test(expected=NullPointerException.class)
	public void testIsnonO2User_UserIsNull_Failure() throws Exception{
		final User user = null;
		
		userServiceSpy.isnonO2User(user);
	}
	
	@Test(expected=NullPointerException.class)
	public void testIsnonO2User_UserGroupIsNull_Failure() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = null;
		
		user.setUserGroup(userGroup);
		
		userServiceSpy.isnonO2User(user);
	}
	
	@Test(expected=NullPointerException.class)
	public void testIsnonO2User_CommunityIsNull_Failure() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = null;
		
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		
		userServiceSpy.isnonO2User(user);
	}
	
	@Test(expected=NullPointerException.class)
    @Ignore //TODO review
	public void testIsnonO2User_RewriteUrlParameterIsO2AndProviderIsNull_Failure() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider(null);
		
		userServiceSpy.isnonO2User(user);
	}
	
	@Test(expected=NullPointerException.class)
	public void testIsnonO2User_RewriteUrlParameterIsNull_Failure() throws Exception{
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();
		
		community.setRewriteUrlParameter(null);
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider(null);
		
		userServiceSpy.isnonO2User(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_nonO2User_Success() throws Exception{
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;
		
		final User user = UserFactory.createUser();
		user.setLastSubscribedPaymentSystem(migSmsType);
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("provider");
		user.setSubBalance(0);
		user.setStatus(limitedUserStatus);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setSubBalance(0);
		
		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setNextSubPayment(Integer.MIN_VALUE);
		submittedPayment.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		submittedPayment.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		submittedPayment.setPaymentSystem(iTunesSubscriptionType);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, user.getSubBalance(), TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, user.getSubBalance(), TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, user.getSubBalance(), TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, user.getSubBalance(), TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, Integer.MAX_VALUE, submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2LimitedUser_Success() throws Exception{
		final int oldNextSubPayment = 0;
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(2);
		user.setStatus(limitedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setNextSubPayment(oldNextSubPayment);
		
		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(migSmsType);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 7, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 7, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int passedSubweeks = 5;
		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(oldNextSubPayment + passedSubweeks * WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, passedSubweeks, submittedPayment);
		
		verify(entityServiceMock, times(0)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(1)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2BussinesLimitedUser_Success() throws Exception{
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		final int oldNextSubPayment = 2;
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(2);
		user.setStatus(limitedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setNextSubPayment(oldNextSubPayment);
		user.setSegment(SegmentType.BUSINESS);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(migSmsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int currentTimeSeconds = oldNextSubPayment  + 25;
		final long currentTimeMillis = currentTimeSeconds*1000L;

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds + submittedPayment.getSubweeks() * WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2BussinesSubscribedUser_Success() throws Exception{
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		final int oldNextSubPayment = 2;
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(2);
		user.setStatus(subscribedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setNextSubPayment(oldNextSubPayment);
		user.setSegment(SegmentType.BUSINESS);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(migSmsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int currentTimeSeconds = oldNextSubPayment  + 25;
		final long currentTimeMillis = currentTimeSeconds*1000L;

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds + submittedPayment.getSubweeks() * WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2BussinesSubscribedUserAndCurrentTimeLessThanoNextSubPayment_Success() throws Exception{
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		final int oldNextSubPayment = 2;
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(2);
		user.setStatus(subscribedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setNextSubPayment(oldNextSubPayment);
		user.setSegment(SegmentType.BUSINESS);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(migSmsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int currentTimeSeconds = oldNextSubPayment-5;
		final long currentTimeMillis = currentTimeSeconds*1000L;

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(oldNextSubPayment + submittedPayment.getSubweeks() * WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2EulaUser_Success() throws Exception{
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;

		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		final int oldSubBalance = 2;
		final int oldNextSubPayment=0;

		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(oldSubBalance);
		user.setStatus(eulaUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setNextSubPayment(oldNextSubPayment);
		
		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(migSmsType);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, oldSubBalance, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, oldSubBalance, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);
		
		final int passedSubweeks = 5;

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(oldSubBalance, passedUser.getSubBalance());
				assertEquals(oldNextSubPayment + passedSubweeks * WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		
		userServiceSpy.processPaymentSubBalanceCommand(user, passedSubweeks, submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2SubscribedUser_Success() throws Exception{
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		final int oldSubBalance = 2;
		final int nextSubPayment = 1;

		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(oldSubBalance);
		user.setStatus(subscribedUserStatus);
		user.setLastSubscribedPaymentSystem(migSmsType);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setNextSubPayment(nextSubPayment);
		
		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(migSmsType);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, oldSubBalance, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, oldSubBalance, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);
		
		final int passedSubweeks = 5;

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(oldSubBalance, passedUser.getSubBalance());
				assertEquals(nextSubPayment + passedSubweeks * WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, passedSubweeks, submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_ChartsNowLimitedUserPayedByMig_Success() throws Exception{
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String migSmsType = PaymentDetails.MIG_SMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		community.setRewriteUrlParameter("chartsNow");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider(null);
		user.setSubBalance(2);
		user.setStatus(limitedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		
		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(migSmsType);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 7, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 7, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(6, passedUser.getSubBalance());
				assertEquals(Integer.MIN_VALUE, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, 5, submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(1)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2ConsumerSubscribedUserPayedByO2Psms_Success() throws Exception{
        final int currentTimeSeconds = 0;
		final long currentTimeMillis = currentTimeSeconds*1000L;
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String paymentDetailsType = PaymentDetails.O2_PSMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(2);
		user.setStatus(subscribedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setSegment(CONSUMER);
		user.setContract(PAYG);
		final int oldNextSubPayment = currentTimeSeconds- WEEK_SECONDS;
		user.setNextSubPayment(oldNextSubPayment);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(paymentDetailsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds+submittedPayment.getSubweeks()* WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2PAYMConsumerSubscribedUserPayedByO2Psms_Success() throws Exception{
        final int currentTimeSeconds = 0;
		final long currentTimeMillis = currentTimeSeconds*1000L;
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String paymentDetailsType = PaymentDetails.O2_PSMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("o2");
		user.setSubBalance(2);
		user.setStatus(subscribedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setSegment(CONSUMER);
		user.setContract(PAYM);
		final int oldNextSubPayment = currentTimeSeconds- WEEK_SECONDS;
		user.setNextSubPayment(oldNextSubPayment);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(paymentDetailsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds+submittedPayment.getSubweeks()* WEEK_SECONDS, passedUser.getNextSubPayment());
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
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(0)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test(expected=NullPointerException.class)
	public void testProcessPaymentSubBalanceCommand_UserIsNull_Failure() throws Exception{
		final User user = null;
		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		
		userServiceSpy.processPaymentSubBalanceCommand(user, 5, submittedPayment);
	}
	
	@Test
	public void testGetRedeemServerO2Url_Success() throws Exception{
		String redeemServerO2Url = "identity.o2.co.uk"; 
		final User user = UserFactory.createUser();
		
		when(o2ClientServiceMock.getRedeemServerO2Url(eq(user.getMobile()))).thenReturn(redeemServerO2Url);
		
		String result = userServiceSpy.getRedeemServerO2Url(user);
	
		assertEquals(redeemServerO2Url, result);
		
		verify(o2ClientServiceMock, times(1)).getRedeemServerO2Url(eq(user.getMobile()));
	}
	
	@Test
	public void testIsIOsnonO2ItunesSubscribedUser_LIMITED_Success() throws Exception{
		DeviceType iosDeviceType = DeviceTypeFactory.createDeviceType("IOs");
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		
		final User user = UserFactory.createUser();
		
		user.setLastSubscribedPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
		user.setStatus(limitedUserStatus);
		user.setDeviceType(iosDeviceType);
		
		PowerMockito.mockStatic(DeviceTypeDao.class);
		PowerMockito.when(DeviceTypeDao.getIOSDeviceType()).thenReturn(iosDeviceType);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		
		boolean isIOsnonO2ItunesSubscribedUser = user.isIOsnonO2ItunesSubscribedUser();
		
		assertFalse(isIOsnonO2ItunesSubscribedUser);
	}
	
	@Test
	public void test_isNonO2UserSubscribeByO2_PSMS_Success() throws Exception{
        final int monthlyNextSubPayment = 0;
		final int currentTimeSeconds = monthlyNextSubPayment;
		final long currentTimeMillis = currentTimeSeconds*1000L;
		final String base64EncodedAppStoreReceipt = "base64EncodedAppStoreReceipt";
		final String appStoreOriginalTransactionId = "appStoreOriginalTransactionId";
		final String iTunesSubscriptionType = PaymentDetails.ITUNES_SUBSCRIPTION;
		final String paymentDetailsType = PaymentDetails.O2_PSMS_TYPE;
		
		final User user = UserFactory.createUser();
		final UserGroup userGroup = UserGroupFactory.createUserGroup();
		final Community community = CommunityFactory.createCommunity();

		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus eulaUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.EULA);
		
		community.setRewriteUrlParameter("o2");
		userGroup.setCommunity(community);
		user.setUserGroup(userGroup);
		user.setProvider("non-o2");
		user.setSubBalance(2);
		user.setStatus(subscribedUserStatus);
		user.setBase64EncodedAppStoreReceipt(base64EncodedAppStoreReceipt);
		user.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
		user.setFreeTrialExpiredMillis(Long.MAX_VALUE);
		user.setSegment(CONSUMER);
		user.setContract(PAYM);
		final int oldNextSubPayment = currentTimeSeconds- WEEK_SECONDS;
		user.setNextSubPayment(oldNextSubPayment);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(paymentDetailsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(entityServiceMock.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(entityServiceMock.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(entityServiceMock.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[monthlyNextSubPayment];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(monthlyNextSubPayment, passedUser.getNextSubPayment());
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
		PowerMockito.when(getMonthlyNextSubPayment(user.getNextSubPayment())).thenReturn(monthlyNextSubPayment);
		
		Mockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
		Mockito.when(getEpochMillis()).thenReturn(currentTimeMillis);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		verify(entityServiceMock, times(1)).saveEntity(cardTopUpAccountLog);
		verify(entityServiceMock, times(monthlyNextSubPayment)).saveEntity(subscriptionChargeAccountLog);
		verify(entityServiceMock, times(1)).updateEntity(user);
	}
	
	@Test
	public void testIsIOsnonO2ItunesSubscribedUser_SUBSCRIBED_Success() throws Exception{
		DeviceType iosDeviceType = DeviceTypeFactory.createDeviceType("IOs");
		final UserStatus limitedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		final UserStatus subscribedUserStatus = UserStatusFactory.createUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED);
		
		final User user = UserFactory.createUser();
		
		user.setLastSubscribedPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
		user.setStatus(subscribedUserStatus);
		user.setDeviceType(iosDeviceType);
        user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
        user.setProvider("nonO2");
		
		PowerMockito.mockStatic(DeviceTypeDao.class);
		PowerMockito.when(DeviceTypeDao.getIOSDeviceType()).thenReturn(iosDeviceType);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		
		boolean isIOsnonO2ItunesSubscribedUser = user.isIOsnonO2ItunesSubscribedUser();
		
		assertTrue(isIOsnonO2ItunesSubscribedUser);
	}
	
	@Test
	public void testFindUsersForItunesInAppSubscription_Success(){
		User user = UserFactory.createUser();
		User user2 = UserFactory.createUser();

		int nextSubPayment = 1;
		String appStoreOriginalTransactionId="appStoreOriginalTransactionId";
		
		List<User> users = new ArrayList<User>();
		users.add(user2);


		Mockito.when(userRepositoryMock.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId)).thenReturn(users);
		
		List<User> actualUsers = userServiceSpy.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
		
		assertNotNull(actualUsers);
		assertEquals(2, actualUsers.size());
		assertTrue(users.contains(user));
		assertTrue(users.contains(user2));
		
		verify(userRepositoryMock, times(1)).findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
	}

	@Test(expected=NullPointerException.class)
	public void testFindUsersForItunesInAppSubscription_appStoreOriginalTransactionIdIsNull_Failure(){
		User user = UserFactory.createUser();

		String appStoreOriginalTransactionId=null;
		int nextSubPayment = 1;
		
		userServiceSpy.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);  
	}
	
	@Test(expected=NullPointerException.class)
	public void testFindUsersForItunesInAppSubscription_userIsNull_Failure(){
		User user = null;

		int nextSubPayment = 1;
		String appStoreOriginalTransactionId="appStoreOriginalTransactionId"; 
	
		userServiceSpy.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId); 
	}

	@Test
	public void testFetUsersForRetryPayment() {
		
		final int currentTimeSeconds = Integer.MAX_VALUE;
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
		
		List<User> expectedUsers = Collections.<User>emptyList(); 
		
		Mockito.when(userRepositoryMock.getUsersForRetryPayment(currentTimeSeconds)).thenReturn(expectedUsers);
		
		List<User> users = userServiceSpy.getUsersForRetryPayment();
		
		assertNotNull(users);
		assertEquals(expectedUsers, users);
	}
	
	@Test
	public void testUpdateUserFacebookDetails_NotO2Community_Success() {
		UserFacebookDetailsDto userFacebookDetailsDto = UserFacebookDetailsDtoFactory.createUserFacebookDetailsDto();
		
		Community community = CommunityFactory.createCommunity();
		community.setRewriteUrlParameter("not-o2");
		
		DrmType drmType = DrmTypeFactory.createDrmType();
		DrmPolicy drmPolicy = DrmPolicyFactory.createDrmPolicy();
		drmPolicy.setDrmType(drmType);
		Chart chart = ChartFactory.createChart();
		
		UserGroup userGroup = UserGroupFactory.createUserGroup();
		userGroup.setCommunity(community);
		userGroup.setChart(chart);
		userGroup.setDrmPolicy(drmPolicy);
		
		final String passedCommunityName = userFacebookDetailsDto.getCommunityName();
		final String deviceUID = userFacebookDetailsDto.getDeviceUID();
		final String storedToken = userFacebookDetailsDto.getStoredToken();
		
		when(communityServiceMock.getCommunityByName(userFacebookDetailsDto.getCommunityName())).thenReturn(community);
		
		UserCredentions userCredentions = UserCredentionsFactory.createUserCredentions();
		userCredentions.setEmail("email");
		userCredentions.setId("id");
		
		when(facebookServiceMock.getUserCredentions(passedCommunityName, userFacebookDetailsDto.getFacebookToken())).thenReturn(userCredentions);
		
		final String oldUserName = "phoneNumber";
		final String communityRedirectUrl = community.getRewriteUrlParameter();
		
		User userByDeviceUID = UserFactory.createUser();
		userByDeviceUID.setToken(storedToken);
		userByDeviceUID.setUserGroup(userGroup);
		userByDeviceUID.setUserName(oldUserName);
		userByDeviceUID.setFacebookId(null);
		userByDeviceUID.setFirstUserLoginMillis(null);
		
		doReturn(userByDeviceUID).when(userServiceSpy).findByDeviceUIDAndCommunityRedirectURL(deviceUID, communityRedirectUrl);
		
		User userByFacebookId = UserFactory.createUser();
		userByFacebookId.setId(userByDeviceUID.getId());

		doReturn(userByFacebookId).when(userServiceSpy).findByFacebookId(userCredentions.getId(), passedCommunityName);
		doReturn(userByDeviceUID).when(userServiceSpy).findByNameAndCommunity(userCredentions.getEmail(), passedCommunityName);
		doReturn(userByDeviceUID).when(userServiceSpy).mergeUser(userByDeviceUID, userByFacebookId);
		doReturn(userByDeviceUID).when(userServiceSpy).updateUser(userByDeviceUID);
		
		final Long currentTimeMillis = Long.MAX_VALUE;
		PowerMockito.mockStatic(Utils.class);
		when(getEpochMillis()).thenReturn(currentTimeMillis);
		
		when(userDaoMock.findUserById(userByDeviceUID.getId())).thenReturn(userByDeviceUID);
		
		AccountCheckDTO accountCheckDTO = AccountCheckDTOFactory.createAccountCheckDTO();
				
		
		doReturn(accountCheckDTO).when(userServiceSpy).proceessAccountCheckCommandForAuthorizedUser(userByDeviceUID.getId(), null, null, null);
		
		AccountCheckDTO actualAccountCheckDTO = userServiceSpy.updateUserFacebookDetails(userFacebookDetailsDto);
		
		assertNotNull(actualAccountCheckDTO);
		assertEquals(accountCheckDTO, actualAccountCheckDTO);
		
		assertEquals(userCredentions.getEmail(), userByDeviceUID.getUserName());
		assertEquals(userCredentions.getId(), userByDeviceUID.getFacebookId());
		assertEquals(currentTimeMillis, userByDeviceUID.getFirstUserLoginMillis());
		
		verify(communityServiceMock, times(1)).getCommunityByName(userFacebookDetailsDto.getCommunityName());
		verify(facebookServiceMock, times(1)).getUserCredentions(passedCommunityName, userFacebookDetailsDto.getFacebookToken());
		verify(userServiceSpy, times(1)).findByDeviceUIDAndCommunityRedirectURL(deviceUID, communityRedirectUrl);
		verify(userServiceSpy, times(1)).findByFacebookId(userCredentions.getId(), passedCommunityName);
		verify(userServiceSpy, times(0)).findByNameAndCommunity(userCredentions.getEmail(), passedCommunityName);
		verify(userServiceSpy, times(0)).mergeUser(userByDeviceUID, userByDeviceUID);
		verify(userServiceSpy, times(1)).updateUser(userByDeviceUID);
		verify(userServiceSpy, times(1)).proceessAccountCheckCommandForAuthorizedUser(userByDeviceUID.getId(), null, null, null);
	}
	
	@Test
	public void applyInitPromoO2_EmailAsUserName_Success() {
        Community community = new Community().withRewriteUrl("o2").withName("o2");
        User user = new User().withActivationStatus(ENTERED_NUMBER).withDeviceType(new DeviceType()).withUserName("g@g.gg").withUserGroup(new UserGroup().withCommunity(community));

        User mobileUser = null;
        String otac = "otac";

        ProviderUserDetails o2UserDetails = new ProviderUserDetails().withContract(PAYG.name());
		
		doReturn(user).when(userServiceSpy).mergeUser(mobileUser, user);
		when(otacValidationServiceMock.validate(otac, user.getMobile(), community)).thenReturn(o2UserDetails);
		when(userRepositoryMock.save(user)).thenReturn(user);
		when(communityServiceMock.getCommunityByName(community.getName())).thenReturn(community);
		
		boolean hasPromo = false;
		doReturn(hasPromo).when(userServiceSpy).applyO2PotentialPromo(o2UserDetails, user, community);
		
		AccountCheckDTO accountCheckDTO = AccountCheckDTOFactory.createAccountCheckDTO();
		doReturn(accountCheckDTO).when(userServiceSpy).proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, user.getDeviceTypeIdString(), null);
		
		AccountCheckDTO actualAccountCheckDTO = userServiceSpy.applyInitPromoAndAccCheck(user, mobileUser, otac, true);
		
		assertNotNull(actualAccountCheckDTO);
		assertEquals(accountCheckDTO, actualAccountCheckDTO);
		assertEquals(true, actualAccountCheckDTO.isFullyRegistred());
		assertEquals(hasPromo, actualAccountCheckDTO.isHasPotentialPromoCodePromotion());
		
		assertEquals(Contract.valueOf(o2UserDetails.contract), user.getContract());
		assertEquals(o2UserDetails.operator, user.getProvider());
		assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
		assertEquals(user.getMobile(), user.getUserName());
		
		verify(userServiceSpy, times(0)).mergeUser(mobileUser, user);
		verify(otacValidationServiceMock, times(1)).validate(otac, user.getMobile(), community);
		verify(userRepositoryMock, times(1)).save(user);
		verify(userServiceSpy,times(0) ).applyO2PotentialPromo(o2UserDetails, user, community);
		verify(userServiceSpy, times(1)).proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, user.getDeviceTypeIdString(), null);
	}
	
	@Test
	public void applyInitPromoO2_NotEmailAsUserName_Success() {
        Community community = new Community().withRewriteUrl("o2").withName("o2");
        User user = new User().withActivationStatus(ENTERED_NUMBER).withDeviceType(new DeviceType()).withUserName("+380913008066").withUserGroup(new UserGroup().withCommunity(community));
		
		User mobileUser = null;
		String otac = "otac";

        ProviderUserDetails o2UserDetails = new ProviderUserDetails().withOperator("").withContract(PAYG.name());
		
		doReturn(user).when(userServiceSpy).mergeUser(mobileUser, user);
		when(otacValidationServiceMock.validate(otac, user.getMobile(), community)).thenReturn(o2UserDetails);
		when(userRepositoryMock.save(user)).thenReturn(user);
		when(communityServiceMock.getCommunityByName(community.getName())).thenReturn(community);
		
		boolean hasPromo = false;
        doReturn(hasPromo).when(promotionServiceMock).applyPotentialPromo(user, false);
		
		AccountCheckDTO accountCheckDTO = AccountCheckDTOFactory.createAccountCheckDTO();
		doReturn(accountCheckDTO).when(userServiceSpy).proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, user.getDeviceTypeIdString(), null);

		AccountCheckDTO actualAccountCheckDTO = userServiceSpy.applyInitPromoAndAccCheck(user, mobileUser, otac, true);
		
		assertNotNull(actualAccountCheckDTO);
		assertEquals(accountCheckDTO, actualAccountCheckDTO);
		assertEquals(true, actualAccountCheckDTO.isFullyRegistred());
		assertEquals(hasPromo, actualAccountCheckDTO.isHasPotentialPromoCodePromotion());
		
		assertEquals(Contract.valueOf(o2UserDetails.contract), user.getContract());
		assertEquals(o2UserDetails.operator, user.getProvider());
		assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
		assertEquals(user.getMobile(), user.getUserName());
		
		verify(userServiceSpy, times(0)).mergeUser(mobileUser, user);
		verify(otacValidationServiceMock, times(1)).validate(otac, user.getMobile(), community);
		verify(userRepositoryMock, times(1)).save(user);
		verify(promotionServiceMock,times(1) ).applyPotentialPromo(user, false);
		verify(userServiceSpy, times(1)).proceessAccountCheckCommandForAuthorizedUser(user.getId(), null, user.getDeviceTypeIdString(), null);
	}
	
	@Test
	public void testApplyO2PotentialPromo_StaffPromotion_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
		o2UserDetails.operator="o2";
		o2UserDetails.contract="payg";
		
		User user = UserFactory.createUser();
		user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
		
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("staff");
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("store");
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"))).thenReturn(true);
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("store"))).thenReturn(true);
		when(o2ClientServiceMock.isO2User(any(ProviderUserDetails.class))).thenReturn(true);
		doReturn(null).when(userServiceSpy).setPotentialPromo(anyString(), any(User.class), anyString());
		doReturn(null).when(userServiceSpy).setPotentialPromo(any(Community.class), any(User.class), anyString());
		doReturn(true).when(userServiceSpy).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
		
		boolean result = userServiceSpy.applyO2PotentialPromo(o2UserDetails, user, user.getUserGroup().getCommunity());
		
		assertEquals(true, result);
		
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class));
		verify(deviceServiceMock, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"));
		verify(deviceServiceMock, times(0)).isPromotedDevicePhone(any(Community.class), anyString(), eq("store"));
		verify(o2ClientServiceMock, times(1)).isO2User(any(ProviderUserDetails.class));
		verify(userServiceSpy, times(0)).setPotentialPromo(anyString(), any(User.class), anyString());
		verify(userServiceSpy, times(1)).setPotentialPromo(any(Community.class), any(User.class), anyString());
		verify(userServiceSpy, times(1)).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
	}
	
	@Test
	public void testApplyO2PotentialPromo_StorePromotion_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
		o2UserDetails.operator="o2";
		o2UserDetails.contract="payg";
		
		User user = UserFactory.createUser();
		user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
		
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("staff");
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("store");
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"))).thenReturn(false);
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("store"))).thenReturn(true);
		when(o2ClientServiceMock.isO2User(any(ProviderUserDetails.class))).thenReturn(true);
		doReturn(null).when(userServiceSpy).setPotentialPromo(anyString(), any(User.class), anyString());
		doReturn(null).when(userServiceSpy).setPotentialPromo(any(Community.class), any(User.class), eq("staff"));
		doReturn(null).when(userServiceSpy).setPotentialPromo(any(Community.class), any(User.class), eq("store"));
		doReturn(true).when(userServiceSpy).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
		
		boolean result = userServiceSpy.applyO2PotentialPromo(o2UserDetails, user, user.getUserGroup().getCommunity());
		
		assertEquals(true, result);
		
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class));
		verify(deviceServiceMock, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"));
		verify(deviceServiceMock, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), eq("store"));
		verify(o2ClientServiceMock, times(1)).isO2User(any(ProviderUserDetails.class));
		verify(userServiceSpy, times(0)).setPotentialPromo(anyString(), any(User.class), anyString());
		verify(userServiceSpy, times(1)).setPotentialPromo(any(Community.class), any(User.class), eq("store"));
		verify(userServiceSpy, times(0)).setPotentialPromo(any(Community.class), any(User.class), eq("staff"));
		verify(userServiceSpy, times(1)).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
	}
	
	@Test
	public void testApplyO2PotentialPromo_O2UserPromotion_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
		o2UserDetails.operator="o2";
		o2UserDetails.contract="payg";
		
		User user = UserFactory.createUser();
		user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
		
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("staff");
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("store");
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"))).thenReturn(false);
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("store"))).thenReturn(false);
		when(o2ClientServiceMock.isO2User(any(ProviderUserDetails.class))).thenReturn(true);
		doReturn(null).when(userServiceSpy).setPotentialPromo(anyString(), any(User.class), anyString());
		doReturn(null).when(userServiceSpy).setPotentialPromo(any(Community.class), any(User.class), eq("staff"));
		doReturn(null).when(userServiceSpy).setPotentialPromo(any(Community.class), any(User.class), eq("store"));
		doReturn(true).when(userServiceSpy).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
		
		boolean result = userServiceSpy.applyO2PotentialPromo(o2UserDetails, user, user.getUserGroup().getCommunity());
		
		assertEquals(true, result);
		
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class));
		verify(deviceServiceMock, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"));
		verify(deviceServiceMock, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), eq("store"));
		verify(o2ClientServiceMock, times(1)).isO2User(any(ProviderUserDetails.class));
		verify(userServiceSpy, times(1)).setPotentialPromo(anyString(), any(User.class), eq("promotionCode"));
		verify(userServiceSpy, times(0)).setPotentialPromo(anyString(), any(User.class), eq("defaultPromotionCode"));
		verify(userServiceSpy, times(0)).setPotentialPromo(any(Community.class), any(User.class), eq("store"));
		verify(userServiceSpy, times(0)).setPotentialPromo(any(Community.class), any(User.class), eq("staff"));
		verify(userServiceSpy, times(1)).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
	}
	
	@Test
	public void testApplyO2PotentialPromo_DefaultPromotion_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
		o2UserDetails.operator = "o2";
		o2UserDetails.contract="payg";
		
		User user = UserFactory.createUser();
		user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
		
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("staff");
		when(communityResourceBundleMessageSourceMock.getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class))).thenReturn("store");
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"))).thenReturn(false);
		when(deviceServiceMock.isPromotedDevicePhone(any(Community.class), anyString(), eq("store"))).thenReturn(false);
		when(o2ClientServiceMock.isO2User(any(ProviderUserDetails.class))).thenReturn(false);
		doReturn(null).when(userServiceSpy).setPotentialPromo(anyString(), any(User.class), anyString());
		doReturn(null).when(userServiceSpy).setPotentialPromo(any(Community.class), any(User.class), eq("staff"));
		doReturn(null).when(userServiceSpy).setPotentialPromo(any(Community.class), any(User.class), eq("store"));
		doReturn(true).when(userServiceSpy).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
		
		boolean result = userServiceSpy.applyO2PotentialPromo(o2UserDetails, user, user.getUserGroup().getCommunity());
		
		assertEquals(true, result);
		
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.staff.promotionCode"), any(Object[].class), any(Locale.class));
		verify(communityResourceBundleMessageSourceMock, times(1)).getMessage(anyString(), eq("o2.store.promotionCode"), any(Object[].class), any(Locale.class));
		verify(deviceServiceMock, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), eq("staff"));
		verify(deviceServiceMock, times(1)).isPromotedDevicePhone(any(Community.class), anyString(), eq("store"));
		verify(o2ClientServiceMock, times(1)).isO2User(any(ProviderUserDetails.class));
		verify(userServiceSpy, times(0)).setPotentialPromo(anyString(), any(User.class), eq("promotionCode"));
		verify(userServiceSpy, times(1)).setPotentialPromo(anyString(), any(User.class), eq("defaultPromotionCode"));
		verify(userServiceSpy, times(0)).setPotentialPromo(any(Community.class), any(User.class), eq("store"));
		verify(userServiceSpy, times(0)).setPotentialPromo(any(Community.class), any(User.class), eq("staff"));
		verify(userServiceSpy, times(1)).applyPromotionByPromoCode(any(User.class), any(Promotion.class), any(int.class));
	}
	
	@Test
	public void testApplyPromotionByPromoCode_ToSomeDate_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
		o2UserDetails.operator ="o2";
		o2UserDetails.contract="payg";
		
		User user = UserFactory.createUser();
		user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 0, 1);
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("staff");
		final Promotion promotion = new Promotion();
		promotion.setPromoCode(promoCode);
		promotion.setEndDate((int)(calendar.getTimeInMillis()/1000));

        when(userBannedRepositoryMock.findOne(anyInt())).thenReturn(null);
		when(entityServiceMock.updateEntity(eq(user))).thenAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User user = (User)invocation.getArguments()[0];
				if(user != null)
					assertEquals(promotion.getEndDate(), user.getNextSubPayment());
				
				return user;
			}
		});
		when(entityServiceMock.updateEntity(eq(promotion))).thenReturn(promotion);
		when(entityServiceMock.saveEntity(any(AccountLog.class))).thenReturn(null);
		
		userServiceSpy.applyPromotionByPromoCode(user, promotion);

        verify(userBannedRepositoryMock, times(1)).findOne(anyInt());
		verify(entityServiceMock, times(1)).updateEntity(eq(promotion));
		verify(entityServiceMock, times(1)).updateEntity(eq(user));
	}
	
	@Test
	public void testApplyPromotionByPromoCode_OnSomeWeeks_Success() {
        ProviderUserDetails o2UserDetails = new ProviderUserDetails();
		o2UserDetails.operator= "o2";
		o2UserDetails.contract="payg";
		
		User user = UserFactory.createUser();
		user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");

		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 0, 1);
		PromoCode promoCode = new PromoCode();
		promoCode.setCode("store");
		final Promotion promotion = new Promotion();
		promotion.setPromoCode(promoCode);
		promotion.setFreeWeeks((byte)52);
		
		when(entityServiceMock.updateEntity(eq(user))).thenAnswer(new Answer<User>() {
			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User user = (User)invocation.getArguments()[0];
				if(user != null)
					assertEquals(getEpochSeconds() + 52 * WEEK_SECONDS, user.getNextSubPayment());
				
				return user;
			}
		});
        when(userBannedRepositoryMock.findOne(anyInt())).thenReturn(null);
		when(entityServiceMock.updateEntity(eq(promotion))).thenReturn(promotion);
		when(entityServiceMock.saveEntity(any(AccountLog.class))).thenReturn(null);
		
		userServiceSpy.applyPromotionByPromoCode(user, promotion);

        verify(userBannedRepositoryMock, times(1)).findOne(anyInt());
		verify(entityServiceMock, times(1)).updateEntity(eq(promotion));
		verify(entityServiceMock, times(1)).updateEntity(eq(user));
	}

	@Test
	public void testApplyPromotionByPromoCode_BannedUserWithNoPromotion_Success() {
		User user = UserFactory.createUser();
		user.getUserGroup().getCommunity().setRewriteUrlParameter("o2");
        UserBanned userBanned = new UserBanned(user);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, 0, 1);
        PromoCode promoCode = new PromoCode();
        promoCode.setCode("store");
        final Promotion promotion = new Promotion();
        promotion.setPromoCode(promoCode);
        promotion.setFreeWeeks((byte)52);

        when(entityServiceMock.updateEntity(eq(user))).thenAnswer(new Answer<User>() {
            @Override
            public User answer(InvocationOnMock invocation) throws Throwable {
                User user = (User)invocation.getArguments()[0];
                return user;
            }
        });
        when(userBannedRepositoryMock.findOne(anyInt())).thenReturn(userBanned);
		when(entityServiceMock.updateEntity(eq(promotion))).thenReturn(promotion);
		when(entityServiceMock.saveEntity(any(AccountLog.class))).thenReturn(null);

		userServiceSpy.applyPromotionByPromoCode(user, promotion);

        verify(userBannedRepositoryMock, times(1)).findOne(anyInt());
		verify(entityServiceMock, times(0)).updateEntity(eq(promotion));
		verify(entityServiceMock, times(1)).updateEntity(eq(user));
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

		Mockito.when(
				communityResourceBundleMessageSourceMock.getMessage(Mockito.eq(upperCaseCommunityURL), Mockito.eq(messageCode), Mockito
						.argThat(matcher), Mockito.any(Locale.class))).thenReturn(message);

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
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment()*1000L));

        verify(refundServiceMock, times(1)).logSkippedBoughtPeriod(user, USER_DOWNGRADED_TARIFF);
        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
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
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment()*1000L));
        assertEquals(currentTimeMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(userServiceSpy, times(1)).applyPotentialPromo(true, user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
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
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment()*1000L));
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(userServiceSpy, times(0)).applyPotentialPromo(true, user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
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
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment()*1000L));
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(userServiceSpy, times(0)).applyPotentialPromo(true, user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
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
        verify(userServiceSpy, times(0)).applyPotentialPromo(true, user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
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
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment()*1000L));
        assertEquals(freeTrialExpiredMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(userServiceSpy, times(0)).applyPotentialPromo(true, user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
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
        verify(userServiceSpy, times(0)).applyPotentialPromo(true, user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
        verify(accountLogServiceMock, times(0)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
    }



    @Test
    public void shouldMightActivateVideoTrialForO2Payg4GConsumerWithVideoAudioFreeTrialHasNotBeenActivated(){
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false).withContract(PAYG).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Paym4GIndirectConsumerWithVideoAudioFreeTrialHasNotBeenActivated(){
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false).withContractChanel(INDIRECT).withContract(PAYM).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForO2Paym4GIndirectConsumerWithVideoAudioFreeTrialHasBeenActivated(){
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(true).withContractChanel(INDIRECT).withContract(PAYM).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365*24*60*60*1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Paym4GConsumerWithNullContractChannelNotOnVideoAudioFreeTrialAndNotOnVideoAudioSubscriptionBeforeMultipleFreeTrialsStopDate(){
        //given
        user = new User().withContractChanel(null).withContract(PAYM).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Paym4GDirectConsumerNotOnVideoAudioFreeTrialAndNotOnVideoAudioSubscriptionBeforeMultipleFreeTrialsStopDate(){
        //given
        user = new User().withContractChanel(DIRECT).withContract(PAYM).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withLastSuccessfulPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withMediaType(AUDIO)));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldNotdMightActivateVideoTrialForO2Paym4GDirectConsumerOnVideoAudioFreeTrialBeforeMultipleFreeTrialsStopDate(){
        //given
        user = new User().withContractChanel(DIRECT).withContract(PAYM).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForO2Paym4GDirectConsumerOnVideoAudioSubscriptionBeforeMultipleFreeTrialsStopDate(){
        //given
        user = new User().withContractChanel(DIRECT).withContract(PAYM).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withCurrentPaymentDetails(new O2PSMSPaymentDetails().withPaymentPolicy(new PaymentPolicy().withTariff(_4G).withMediaType(VIDEO_AND_AUDIO)));
        Date multipleFreeTrialsStopDate = new DateTime().plus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
     public void shouldNotMightActivateVideoTrialForO2Payg4GConsumerVideoAudioFreeTrialHasNotBeenActivated(){
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false).withContract(PAYG).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldMightActivateVideoTrialForO2Payg4GConsumerVideoAudioFreeTrialHasNotBeenActivated(){
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(false).withContract(PAYG).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        Date multipleFreeTrialsStopDate = new DateTime().minus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(true, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForO2Payg4GConsumerVideoAudioFreeTrialHasBeenActivated(){
        //given
        user = new User().withVideoFreeTrialHasBeenActivated(true).withContract(PAYG).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2")));
        Date multipleFreeTrialsStopDate = new DateTime().minus(365 * 24 * 60 * 60 * 1000L).toDate();
        Mockito.when(communityResourceBundleMessageSourceMock.readDate("o2", UserService.MULTIPLE_FREE_TRIAL_STOP_DATE, newDate(1, 1, 2014))).thenReturn(multipleFreeTrialsStopDate);

        //when
        boolean canActivateVideoTrial = userServiceSpy.canActivateVideoTrial(user);

        //then
        assertEquals(false, canActivateVideoTrial);
    }

    @Test
    public void shouldNotMightActivateVideoTrialForUserOnWhiteListedVideoAudioFreeTrial(){
        //given
        user = new User().withContractChanel(DIRECT).withContract(PAYM).withSegment(CONSUMER).withProvider("o2").withTariff(_4G).withUserGroup(new UserGroup().withCommunity(new Community().withRewriteUrl("o2"))).withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));
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
        assertEquals(currentTimeMillis, new Long(actualUser.getNextSubPayment()*1000L));
        assertEquals(currentTimeMillis, actualUser.getFreeTrialExpiredMillis());

        verify(userServiceSpy, times(1)).unsubscribeUser(user, USER_DOWNGRADED_TARIFF.getDescription());
        verify(accountLogServiceMock, times(1)).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
    }

    @Test
    public void shouldNotDowngradeUserOnWhiteListedVideoAudioFreeTrial(){
       //given
        User user = new User().withTariff(_4G).withFreeTrialExpiredMillis(Long.MAX_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));
        O2SubscriberData o2SubscriberData = new O2SubscriberData().withTariff4G(false);

        PowerMockito.doReturn(null).when(userServiceSpy).downgradeUserTariff(any(User.class), any(Tariff.class));
        PowerMockito.doReturn(user).when(userRepositoryMock).save(user);
        PowerMockito.doReturn(user).when(o2UserDetailsUpdaterMock).setUserFieldsFromSubscriberData(user, o2SubscriberData);

        //when
        User actualUser = userServiceSpy.o2SubscriberDataChanged(user, o2SubscriberData);

        //then
        assertNotNull(actualUser);
        assertEquals(user, actualUser);

        verify(userServiceSpy, times(0)).downgradeUserTariff(any(User.class), any(Tariff.class));
        verify(o2UserDetailsUpdaterMock, times(1)).setUserFieldsFromSubscriberData(user, o2SubscriberData);
        verify(userRepositoryMock, times(1)).save(user);
    }

    @Test
    public void shouldDowngradeUser(){
        //given
        User user = new User().withTariff(_4G).withFreeTrialExpiredMillis(Long.MIN_VALUE).withLastPromo(new PromoCode().withMediaType(VIDEO_AND_AUDIO).withPromotion(new Promotion().withIsWhiteListed(true)));
        O2SubscriberData o2SubscriberData = new O2SubscriberData().withTariff4G(false);

        PowerMockito.doReturn(user).when(userServiceSpy).downgradeUserTariff(any(User.class), any(Tariff.class));
        PowerMockito.doReturn(user).when(userRepositoryMock).save(user);
        PowerMockito.doReturn(user).when(o2UserDetailsUpdaterMock).setUserFieldsFromSubscriberData(user, o2SubscriberData);

        //when
        User actualUser = userServiceSpy.o2SubscriberDataChanged(user, o2SubscriberData);

        //then
        assertNotNull(actualUser);
        assertEquals(user, actualUser);

        verify(userServiceSpy, times(1)).downgradeUserTariff(any(User.class), any(Tariff.class));
        verify(o2UserDetailsUpdaterMock, times(1)).setUserFieldsFromSubscriberData(user, o2SubscriberData);
        verify(userRepositoryMock, times(1)).save(user);
    }

    @Test
    public void shouldValidateAsValidOtac() throws Exception{
        //given
        String otac ="otac";
        String phoneNumber="phoneNumber";
        Community community = new Community();

        doReturn(1L).when(userRepositoryMock).findByOtacMobileAndCommunity(otac, phoneNumber, community);

        //when
        boolean isOtacValid= userServiceSpy.isVFNZOtacValid(otac, phoneNumber, community);

        //then
        assertThat(isOtacValid, is(true));

        verify(userRepositoryMock, times(1)).findByOtacMobileAndCommunity(otac, phoneNumber, community);
    }

    @Test
    public void shouldValidateAsNotValidOtac() throws Exception{
        //given
        String otac ="otac";
        String phoneNumber="phoneNumber";
        Community community = new Community();

        doReturn(0L).when(userRepositoryMock).findByOtacMobileAndCommunity(otac, phoneNumber, community);

        //when
        boolean isOtacValid= userServiceSpy.isVFNZOtacValid(otac, phoneNumber, community);

        //then
        assertThat(isOtacValid, is(false));

        verify(userRepositoryMock, times(1)).findByOtacMobileAndCommunity(otac, phoneNumber, community);
    }

    private void create4GVideoAudioSubscribedUserOnVideoAudioFreeTrial() {
        paymentPolicyTariff = _4G;
        mediaType = VIDEO_AND_AUDIO;

        freeTrialStartedTimestampMillis = currentTimeMillis;
        freeTrialExpiredMillis = freeTrialStartedTimestampMillis + YEAR_SECONDS * 1000L;
        nextSubPayment = (int)(freeTrialExpiredMillis/1000);

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

    private void create4GVideoAudioAndNoLastSuccessfulPaymentDetails() {
        lastSuccessfulPaymentPolicyTariff = _4G;
        lastSuccessfulPaymentPolicyMediaType = VIDEO_AND_AUDIO;

        setFreeTrialAndNextSubPaymentInThePast();

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
        nextSubPayment = (int)(freeTrialExpiredMillis/1000) + 10;
    }

    private void setFreeTrialAndNextSubPaymentInThePast() {
        freeTrialStartedTimestampMillis = currentTimeMillis - 1000L;
        freeTrialExpiredMillis = currentTimeMillis - 1L;
        nextSubPayment = (int)(freeTrialExpiredMillis/1000);
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

        user = UserFactory.createUser();
        user.setTariff(currentUserTariff);
        user.setLastPromo(new PromoCode().withCode("testCode").withMediaType(VIDEO_AND_AUDIO));

        user.setFreeTrialStartedTimestampMillis(freeTrialStartedTimestampMillis);
        user.setFreeTrialExpiredMillis(freeTrialExpiredMillis);
        user.setNextSubPayment(nextSubPayment);

        user.setCurrentPaymentDetails(o2PSMSPaymentDetails);
        user.setUserGroup(userGroup);
        user.setProvider("o2");
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
        Mockito.doReturn(true).when(userServiceSpy).applyPotentialPromo(true, user, user.getUserGroup().getCommunity(), currentTimeSeconds);
        Mockito.doReturn(null).when(accountLogServiceMock).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.BOUGHT_PERIOD_SKIPPING, null);
        Mockito.doReturn(null).when(accountLogServiceMock).logAccountEvent(user.getId(), user.getSubBalance(), null, null, TransactionType.TRIAL_SKIPPING, null);
    }
}
