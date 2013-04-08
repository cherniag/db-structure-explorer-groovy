package mobi.nowtechnologies.server.service;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.OperatorDao;
import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.DeviceTypeFactory;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPaymentFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.UserStatusFactory;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.MigResponseFactory;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDtoFactory;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * The class <code>UserServiceTest</code> contains tests for the class <code>{@link UserService}</code>.
 * 
 * @generatedBy CodePro at 20.08.12 18:31
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserService.class, UserStatusDao.class, Utils.class, DeviceTypeDao.class, UserGroupDao.class, OperatorDao.class, AccountLog.class })
public class UserServiceTest {
	
	public static final String O2_PAYG_CONSUMER_GRACE_DURATION_CODE = ("o2.provider."+SegmentType.CONSUMER+".segment."+Contract.PAYG+".contract."+PaymentDetails.O2_PSMS_TYPE+".payment.grace.duration.seconds").toLowerCase();

	private static final String SMS_SUCCESFULL_PAYMENT_TEXT = "SMS_SUCCESFULL_PAYMENT_TEXT";
	private static final String SMS_SUCCESFULL_PAYMENT_TEXT_MESSAGE_CODE = "sms.succesfullPayment.text";
	private static final String UNSUBSCRIBED_BY_ADMIN = "Unsubscribed by admin";
	private UserService userServiceSpy;
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

	/**
	 * Run the User changePassword(userId, password) method test with success result.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */

	@Test
	public void testChangePassword_Success() throws Exception {
		String password = "newPa$$1";

		User user = UserFactory.createUser();
		String storedToken = Utils.createStoredToken(user.getUserName(), password);

		Mockito.when(mockEntityService.findById(User.class, user.getId())).thenReturn(user);
		PowerMockito.when(mockUserRepository.updateFields(Mockito.eq(storedToken), Mockito.eq(user.getId()))).thenReturn(1);

		User result = userServiceSpy.changePassword(user.getId(), password);

		assertNotNull(result);
		assertEquals(result, user);

		verify(mockUserRepository, times(1)).updateFields(Mockito.eq(storedToken), Mockito.eq(user.getId()));
	}

	/**
	 * Run the User changePassword(userId, password) method test with success result.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = Exception.class)
	public void testChangePassword_Error() throws Exception {
		String password = "newPa$$1";

		User user = UserFactory.createUser();
		String storedToken = Utils.createStoredToken(user.getUserName(), password);

		Mockito.when(mockEntityService.findById(User.class, user.getId())).thenReturn(user);
		PowerMockito.when(mockUserRepository.updateFields(Mockito.eq(storedToken), Mockito.eq(user.getId()))).thenThrow(new Exception());

		userServiceSpy.changePassword(user.getId(), password);
	}

	/**
	 * Run the Collection<User> findUsers(String,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test
	public void testFindUsers_Success() throws Exception {
		String searchWords = "Led Zeppeling";
		String communityURL = "nowtop40";

		List<User> mockedUserCollection = UserFactory.getUserUnmodifableList();

		PowerMockito.when(mockUserRepository.findUser(Mockito.eq(communityURL), Mockito.eq("%" + searchWords + "%"))).thenReturn(mockedUserCollection);

		Collection<User> result = userServiceSpy.findUsers(searchWords, communityURL);

		assertNotNull(result);
		assertEquals(mockedUserCollection, result);
	}

	/**
	 * Run the Collection<User> findUsers(String,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected = NullPointerException.class)
	public void testFindUsers_searchWordsIsNull_Failure() throws Exception {
		String searchWords = null;
		String communityURL = "nowtop40";

		userServiceSpy.findUsers(searchWords, communityURL);
	}

	/**
	 * Run the Collection<User> findUsers(String,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected = NullPointerException.class)
	public void testFindUsers_communityURLIsNull_Failure() throws Exception {
		String searchWords = "Led Zeppeling";
		String communityURL = null;

		userServiceSpy.findUsers(searchWords, communityURL);
	}

	/**
	 * Run the Collection<User> findUsers(String,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected = RuntimeException.class)
	public void testFindUsers_UserRepository_findUser_RuntimeException_Failure() throws Exception {
		String searchWords = "Led Zeppeling";
		String communityURL = "nowtop40";

		List<User> mockedUserCollection = UserFactory.getUserUnmodifableList();

		PowerMockito.when(mockUserRepository.findUser(Mockito.eq(communityURL), Mockito.eq("%" + searchWords + "%"))).thenThrow(new RuntimeException());

		Collection<User> result = userServiceSpy.findUsers(searchWords, communityURL);

		assertNotNull(result);
		assertEquals(mockedUserCollection, result);
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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
		mockedUser.setPaymentEnabled(true);
		mockedUser.setLastSuccessfulPaymentTimeMillis(0L);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.TRIAL_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());
		assertEquals(userDto.getPaymentEnabled(), actualUser.isPaymentEnabled());

		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.TRIAL_TOPUP, null);
		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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
		mockedUser.setPaymentEnabled(true);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());
		assertEquals(userDto.getPaymentEnabled(), actualUser.isPaymentEnabled());

		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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
		mockedUser.setPaymentEnabled(true);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());
		assertEquals(userDto.getPaymentEnabled(), actualUser.isPaymentEnabled());

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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
		mockedUser.setPaymentEnabled(true);
		mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());
		assertEquals(userDto.getPaymentEnabled(), actualUser.isPaymentEnabled());

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());
		assertEquals(userDto.getPaymentEnabled(), actualUser.isPaymentEnabled());

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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
		mockedUser.setPaymentEnabled(false);

		mockedUser.setCurrentPaymentDetails(null);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.updateUser(userDto);

		assertNotNull(actualUser);
		assertEquals(mockedUser, actualUser);

		assertEquals(mockedUserStatus, actualUser.getStatus());
		assertEquals(userDto.getDisplayName(), actualUser.getDisplayName());
		assertEquals(userDto.getSubBalance(), actualUser.getSubBalance());
		assertEquals(userDto.getNextSubPayment().getTime() / 1000, actualUser.getNextSubPayment());
		assertEquals(userDto.getPaymentEnabled(), actualUser.isPaymentEnabled());

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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
		mockedUser.setPaymentEnabled(true);
		mockedUser.setLastSuccessfulPaymentTimeMillis(System.currentTimeMillis());

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService, times(1)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
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

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, null, null, TransactionType.SUBSCRIPTION_CHARGE, null);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), null, null, TransactionType.SUPPORT_TOPUP, null);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected = RuntimeException.class)
	public void testUpdateUser_EntityService_updateEntity_RuntimeException_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		User mockedUser = UserFactory.createUser();

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenThrow(new RuntimeException());

		userServiceSpy.updateUser(userDto);
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected = RuntimeException.class)
	public void testUpdateUser_UserStatusDao_getUserStatusMapIdAsKey_RuntimeException_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		User mockedUser = UserFactory.createUser();

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenThrow(new RuntimeException());

		userServiceSpy.updateUser(userDto);
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected = RuntimeException.class)
	public void testUpdateUser_UserRepository_findOne_RuntimeException_Failure() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenThrow(new RuntimeException());

		userServiceSpy.updateUser(userDto);
	}

	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected = NullPointerException.class)
	public void testUpdateUser_userDtoIsNull_Failure() throws Exception {
		UserDto userDto = null;

		userServiceSpy.updateUser(userDto);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Before
	public void setUp() throws Exception {
		userServiceSpy = Mockito.spy(new UserService());

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
		
		userServiceSpy.setSagePayService(mockSagePayService);
		userServiceSpy.setPaymentPolicyService(mockPaymentPolicyService);
		userServiceSpy.setCountryService(mockCountryService);
		userServiceSpy.setMessageSource(mockCommunityResourceBundleMessageSource);
		userServiceSpy.setDeviceTypeService(mockDeviceTypeService);
		userServiceSpy.setUserRepository(mockUserRepository);
		userServiceSpy.setCountryByIpService(mockCountryByIpService);
		userServiceSpy.setOfferService(mockOfferService);
		userServiceSpy.setPaymentDetailsService(mockPaymentDetailsService);
		userServiceSpy.setUserDeviceDetailsService(mockUserDeviceDetailsService);
		userServiceSpy.setPromotionService(mockPromotionService);
		userServiceSpy.setUserDao(mockUserDao);
		userServiceSpy.setCountryAppVersionService(mocCountryAppVersionService);
		userServiceSpy.setEntityService(mockEntityService);
		userServiceSpy.setMigPaymentService(mockMigPaymentService);
		userServiceSpy.setDrmService(mockDrmService);
		userServiceSpy.setFacebookService(mockFacebookService);
		userServiceSpy.setCommunityService(mockCommunityService);
		userServiceSpy.setDeviceService(mockDeviceService);
		userServiceSpy.setMigHttpService(mockMigHttpService);
		userServiceSpy.setPaymentService(mockPaymentService);
		userServiceSpy.setAccountLogService(mockAccountLogService);
		userServiceSpy.setMailService(mockMailService);
		userServiceSpy.setO2ClientService(mockO2ClientService);
		userServiceSpy.setUserRepository(mockUserRepository);

		PowerMockito.mockStatic(UserStatusDao.class);
	}

	@Test
	public void testFindActivePsmsUsers_Success() {
		String communityURL = "";
		BigDecimal amountOfMoneyToUserNotification = BigDecimal.TEN;
		long deltaSuccesfullPaymentSmsSendingTimestampMillis = 256L;
		long epochMillis = 64564L;

		List<User> users = UserFactory.getUserUnmodifableList();

		PowerMockito.mockStatic(Utils.class);

		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Mockito.when(
				mockUserRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
						deltaSuccesfullPaymentSmsSendingTimestampMillis)).thenReturn(users);

		List<User> actualUsers = userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification,
				deltaSuccesfullPaymentSmsSendingTimestampMillis);

		assertEquals(users, actualUsers);
	}

	@Test(expected = NullPointerException.class)
	public void testFindActivePsmsUsers_communityURLisNull_Failure() {
		String communityURL = null;
		BigDecimal amountOfMoneyToUserNotification = BigDecimal.TEN;
		long deltaSuccesfullPaymentSmsSendingTimestampMillis = 256L;
		long epochMillis = 64564L;

		List<User> users = UserFactory.getUserUnmodifableList();

		PowerMockito.mockStatic(Utils.class);

		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Mockito.when(
				mockUserRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
						deltaSuccesfullPaymentSmsSendingTimestampMillis)).thenReturn(users);

		userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis);

		Mockito.verify(mockUserRepository, times(0)).findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
				deltaSuccesfullPaymentSmsSendingTimestampMillis);
		PowerMockito.verifyStatic(times(0));
		Utils.getEpochMillis();
	}

	@Test(expected = NullPointerException.class)
	public void testFindActivePsmsUsers_amountOfMoneyToUserNotificationisNull_Failure() {
		String communityURL = "";
		BigDecimal amountOfMoneyToUserNotification = null;
		long deltaSuccesfullPaymentSmsSendingTimestampMillis = 256L;
		long epochMillis = 64564L;

		List<User> users = UserFactory.getUserUnmodifableList();

		PowerMockito.mockStatic(Utils.class);

		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Mockito.when(
				mockUserRepository.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
						deltaSuccesfullPaymentSmsSendingTimestampMillis)).thenReturn(users);

		userServiceSpy.findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, deltaSuccesfullPaymentSmsSendingTimestampMillis);

		Mockito.verify(mockUserRepository, times(0)).findActivePsmsUsers(communityURL, amountOfMoneyToUserNotification, epochMillis,
				deltaSuccesfullPaymentSmsSendingTimestampMillis);
		PowerMockito.verifyStatic(times(0));
		Utils.getEpochMillis();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testResetSmsAccordingToLawAttributes_Success() {
		User user = UserFactory.createUser();

		long epochMillis = 25L;

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Mockito.when(mockUserRepository.updateFields(BigDecimal.ZERO, epochMillis, user.getId())).thenReturn(1);

		User actualUser = userServiceSpy.resetSmsAccordingToLawAttributes(user);

		assertEquals(user, actualUser);
		assertEquals(BigDecimal.ZERO, actualUser.getAmountOfMoneyToUserNotification());
		assertEquals(epochMillis, actualUser.getLastSuccesfullPaymentSmsSendingTimestampMillis());

		Mockito.verify(mockUserRepository).updateFields(BigDecimal.ZERO, epochMillis, user.getId());
	}

	@SuppressWarnings("deprecation")
	@Test(expected = ServiceException.class)
	public void testResetSmsAccordingToLawAttributes_Failure() {
		User user = UserFactory.createUser();

		long epochMillis = 25L;

		PowerMockito.mockStatic(Utils.class);
		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Mockito.when(mockUserRepository.updateFields(BigDecimal.ZERO, epochMillis, user.getId())).thenReturn(0);

		User actualUser = userServiceSpy.resetSmsAccordingToLawAttributes(user);

		assertEquals(user, actualUser);
		assertEquals(BigDecimal.ZERO, actualUser.getAmountOfMoneyToUserNotification());
		assertEquals(epochMillis, actualUser.getLastSuccesfullPaymentSmsSendingTimestampMillis());

		Mockito.verify(mockUserRepository).updateFields(BigDecimal.ZERO, epochMillis, user.getId());
	}

	@SuppressWarnings("deprecation")
	@Test(expected = NullPointerException.class)
	public void testResetSmsAccordingToLawAttributes_UserIsNull_Failure() {
		User user = null;

		Mockito.when(mockEntityService.updateEntity(user)).thenReturn(user);

		userServiceSpy.resetSmsAccordingToLawAttributes(user);

		Mockito.verify(mockEntityService, times(0)).updateEntity(user);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testPopulateAmountOfMoneyToUserNotification_Success() {
		final BigDecimal userAmountOfMoneyToUserNotification = BigDecimal.ONE;

		User user = UserFactory.createUser();
		user.setAmountOfMoneyToUserNotification(userAmountOfMoneyToUserNotification);

		SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setAmount(BigDecimal.TEN);

		Mockito.when(mockEntityService.updateEntity(user)).thenReturn(user);

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

		Mockito.when(mockEntityService.updateEntity(user)).thenReturn(user);

		userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

		Mockito.verify(mockEntityService, times(0)).updateEntity(user);

	}

	@SuppressWarnings("deprecation")
	@Test(expected = NullPointerException.class)
	public void testPopulateAmountOfMoneyToUserNotification_SubmittedPaymentIsNull_Failure() {
		final BigDecimal userAmountOfMoneyToUserNotification = BigDecimal.ONE;

		User user = UserFactory.createUser();
		user.setAmountOfMoneyToUserNotification(userAmountOfMoneyToUserNotification);

		SubmittedPayment submittedPayment = null;

		Mockito.when(mockEntityService.updateEntity(user)).thenReturn(user);

		userServiceSpy.populateAmountOfMoneyToUserNotification(user, submittedPayment);

		Mockito.verify(mockEntityService, times(0)).updateEntity(user);

	}

	@Test(expected = NullPointerException.class)
	public void unsubscribeUser_Failure() {
		long epochMillis = 12354L;
		User mockedUser = null;
		final String reason = null;

		PaymentDetails mockedCurrentPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();

		PowerMockito.mockStatic(Utils.class);

		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);
		PowerMockito.when(mockEntityService.updateEntity(mockedCurrentPaymentDetails)).thenReturn(mockedCurrentPaymentDetails);

		userServiceSpy.unsubscribeUser(mockedUser, reason);

		Mockito.verify(mockEntityService, times(0)).updateEntity(mockedUser);
		Mockito.verify(mockEntityService, times(0)).updateEntity(mockedCurrentPaymentDetails);
	}

	@Test()
	public void unsubscribeUser_Success() {
		long epochMillis = 12354L;
		User mockedUser = UserFactory.createUser();
		final String reason = null;

		PaymentDetails mockedCurrentPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();

		mockedUser.setCurrentPaymentDetails(mockedCurrentPaymentDetails);

		PowerMockito.mockStatic(Utils.class);

		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);
		PowerMockito.when(mockEntityService.updateEntity(mockedCurrentPaymentDetails)).thenReturn(mockedCurrentPaymentDetails);
		PowerMockito.when(mockPaymentDetailsService.deactivateCurrentPaymentDetailsIfOneExist(mockedUser, reason)).thenReturn(mockedUser);

		User actualUser = userServiceSpy.unsubscribeUser(mockedUser, reason);

		assertNotNull(actualUser);
		assertFalse(actualUser.isPaymentEnabled());

		PaymentDetails actualCurrentPaymentDetails = actualUser.getCurrentPaymentDetails();

		// assertEquals(epochMillis, actualCurrentPaymentDetails.getDisableTimestampMillis());
		assertFalse(actualCurrentPaymentDetails.isActivated());
		// assertEquals(reason, actualCurrentPaymentDetails.getDescriptionError());

		Mockito.verify(mockEntityService).updateEntity(mockedUser);
		// Mockito.verify(mockEntityService).updateEntity(mockedCurrentPaymentDetails);
		Mockito.verify(mockPaymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(mockedUser, reason);

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

		Mockito.when(Utils.getEpochMillis()).thenReturn(epochMillis);
		Mockito.when(mockUserRepository.updateFields(epochMillis, user.getId())).thenReturn(1);

		Future<Boolean> futureMigResponse = userServiceSpy.makeSuccesfullPaymentFreeSMSRequest(user);

		assertNotNull(futureMigResponse);
		assertTrue(futureMigResponse.get());

		Mockito.verify(mockMigHttpService).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), SMS_SUCCESFULL_PAYMENT_TEXT);
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

		Mockito.verify(mockMigHttpService).makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), SMS_SUCCESFULL_PAYMENT_TEXT);
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

		Mockito.doReturn(user).when(mockEntityService).saveEntity(any(User.class));
		Mockito.when(Utils.createStoredToken(anyString(), anyString())).thenReturn(storedToken);
		Mockito.when(DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue()).thenReturn(deviceTypeMap);
		Mockito.when(DeviceTypeDao.getNoneDeviceType()).thenReturn(noneDeviceType);
		Mockito.when(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY()).thenReturn(userGroupMap);
		Mockito.when(OperatorDao.getMapAsIds()).thenReturn(operatorMap);
		Mockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(userStatus);
		Mockito.when(mockCommunityService.getCommunityByName(anyString())).thenReturn(community);
		Mockito.when(mockCountryService.findIdByFullName(anyString())).thenReturn(countryId);
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
	
//	@Test
//	public void testgGetO2PSMSGraceCreditSeconds_NotO2PSMSLastPayment_Success() throws Exception {
//		final User user = UserFactory.createUser();
//		user.setLastSubscribedPaymentSystem(PaymentDetails.PAYPAL_TYPE);
//
//		int graceCredit = userServiceSpy.getGraceCreditSeconds(user);
//		
//		assertEquals(0, graceCredit);
//	}
//	
//	@Test
//	public void testGetO2PSMSGraceCreditSeconds_NotExpiredPayment_Success() throws Exception {
//		final User user = UserFactory.createUser();
//		user.setNextSubPayment(Utils.getEpochSeconds()+10000000);
//		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
//
//		int graceCredit = userServiceSpy.getGraceCreditSeconds(user);
//		
//		assertEquals(0, graceCredit);
//	}
//	
//	@Test(expected=NullPointerException.class)
//	public void testGetO2PSMSGraceCreditSeconds_NullUser_Success() throws Exception {
//		final User user = null;
//
//		userServiceSpy.getGraceCreditSeconds(user);
//	}
//	
//	@Test
//	public void testGetO2PSMSGraceCreditSeconds_GraceEnded_Success() throws Exception {
//		final int graceDurationSeconds = 48*60*60;
//		final int fullGraceCreditSeconds = graceDurationSeconds/2;
//		
//		final Community community = new Community();
//		community.setRewriteUrlParameter("o2");
//		
//		O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
//		o2psmsPaymentDetails.setActivated(false);
//		
//		final UserGroup userGroup = new UserGroup();
//		userGroup.setCommunity(community);
//
//		final User user = UserFactory.createUser();
//		user.setUserGroup(userGroup);
//		user.setNextSubPayment(Utils.getEpochSeconds() - 50*60*60);
//		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
//		user.setDeactivatedGraceCreditMillis(fullGraceCreditSeconds*1000L);
//		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
//		
//		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", O2_PAYG_CONSUMER_GRACE_DURATION_CODE, null, null)).thenReturn(graceDurationSeconds+"");
//		
//		int o2PSMSGraceCreditSeconds = userServiceSpy.getGraceCreditSeconds(user);
//		
//		assertEquals(fullGraceCreditSeconds, o2PSMSGraceCreditSeconds);
//
//		Mockito.verify(mockCommunityResourceBundleMessageSource, times(0)).getMessage("o2", O2_PAYG_CONSUMER_GRACE_DURATION_CODE, null, null);
//	}
//	
//	@Test
//	public void testGetO2PSMSGraceCreditSeconds_InGraceNow_Success() throws Exception {
//		final int currentTimeSeconds = Integer.MAX_VALUE/2;
//		final int graceDurationSeconds = 2*24*60*60;
//		final Community community = new Community();
//		community.setRewriteUrlParameter("o2");
//		
//		O2PSMSPaymentDetails o2psmsPaymentDetails = new O2PSMSPaymentDetails();
//		o2psmsPaymentDetails.setActivated(true);
//		
//		final UserGroup userGroup = new UserGroup();
//		userGroup.setCommunity(community);
//		
//		final User user = UserFactory.createUser();
//		user.setUserGroup(userGroup);
//		final int halfOfGraceDurationSeconds = graceDurationSeconds/2;
//		user.setNextSubPayment(currentTimeSeconds - halfOfGraceDurationSeconds);
//		user.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
//		user.setCurrentPaymentDetails(o2psmsPaymentDetails);
//		
//		PowerMockito.mockStatic(Utils.class);
//		PowerMockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
//		
//		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", O2_PAYG_CONSUMER_GRACE_DURATION_CODE, null, null)).thenReturn(graceDurationSeconds+"");
//	
//		int graceCredit = userServiceSpy.getGraceCreditSeconds(user);
//		
//		assertEquals(graceDurationSeconds-halfOfGraceDurationSeconds, graceCredit);
//
//		Mockito.verify(mockCommunityResourceBundleMessageSource).getMessage("o2", O2_PAYG_CONSUMER_GRACE_DURATION_CODE, null, null);
//		PowerMockito.verifyStatic(Mockito.times(1));
//		Utils.getEpochSeconds();
//	}

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

		verify(mockCommunityService, times(1)).getCommunityByName(anyString());
		verify(mockCountryService, times(1)).findIdByFullName(anyString());
		verify(mockEntityService, times(1)).saveEntity(any(User.class));
		verify(userServiceSpy, times(1)).proceessAccountCheckCommandForAuthorizedUser(anyInt(), anyString(), anyString(), anyString());
		verifyStatic(times(1));
		Utils.createStoredToken(anyString(), anyString());
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

		verify(mockCommunityService, times(1)).getCommunityByName(anyString());
		verify(mockCountryService, times(0)).findIdByFullName(anyString());
		verify(mockEntityService, times(0)).saveEntity(any(User.class));
		verify(userServiceSpy, times(1)).proceessAccountCheckCommandForAuthorizedUser(anyInt(), anyString(), anyString(), anyString());
		verifyStatic(times(0));
		Utils.createStoredToken(anyString(), anyString());
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

		Mockito.when(mockO2ClientService.validatePhoneNumber(anyString())).thenReturn("+447870111111");

		User userResult = userServiceSpy.activatePhoneNumber(user, phone);

		assertNotNull(user);
		assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
		assertEquals("+447870111111", userResult.getMobile());

		verify(mockUserRepository, times(1)).save(any(User.class));
		verify(mockO2ClientService, times(1)).validatePhoneNumber(anyString());
	}
	
	@Test()
	public void testActivatePhoneNumber_NullPhone_Success() throws Exception {
		final String phone = null;
		final User user = UserFactory.createUser();

		Mockito.when(mockO2ClientService.validatePhoneNumber(anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				String phone = (String)invocation.getArguments()[0];
				assertEquals(user.getMobile(), phone);
				
				return "+447870111111";
			}
		});

		User userResult = userServiceSpy.activatePhoneNumber(user, phone);

		assertNotNull(user);
		assertEquals(ActivationStatus.ENTERED_NUMBER, userResult.getActivationStatus());
		assertEquals("+447870111111", userResult.getMobile());

		verify(mockUserRepository, times(1)).save(any(User.class));
		verify(mockO2ClientService, times(1)).validatePhoneNumber(anyString());
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, user.getSubBalance(), TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, user.getSubBalance(), TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochMillis()).thenReturn(Long.MAX_VALUE);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, Integer.MAX_VALUE, submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int passedSubweeks = 5;
		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(oldNextSubPayment + passedSubweeks * Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochMillis()).thenReturn(Long.MAX_VALUE);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, passedSubweeks, submittedPayment);
		
		Mockito.verify(mockEntityService, times(0)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(1)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int currentTimeSeconds = oldNextSubPayment  + 25;
		final long currentTimeMillis = currentTimeSeconds*1000L;

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds + submittedPayment.getSubweeks() * Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
		Mockito.when(Utils.getEpochMillis()).thenReturn(currentTimeMillis);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int currentTimeSeconds = oldNextSubPayment  + 25;
		final long currentTimeMillis = currentTimeSeconds*1000L;

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds + submittedPayment.getSubweeks() * Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
		Mockito.when(Utils.getEpochMillis()).thenReturn(currentTimeMillis);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		final int currentTimeSeconds = oldNextSubPayment-5;
		final long currentTimeMillis = currentTimeSeconds*1000L;

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(oldNextSubPayment + submittedPayment.getSubweeks() * Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
		Mockito.when(Utils.getEpochMillis()).thenReturn(currentTimeMillis);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);
		
		final int passedSubweeks = 5;

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(oldSubBalance, passedUser.getSubBalance());
				assertEquals(oldNextSubPayment + passedSubweeks * Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochMillis()).thenReturn(Long.MAX_VALUE);
		
		
		userServiceSpy.processPaymentSubBalanceCommand(user, passedSubweeks, submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);
		
		final int passedSubweeks = 5;

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(oldSubBalance, passedUser.getSubBalance());
				assertEquals(nextSubPayment + passedSubweeks * Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochMillis()).thenReturn(Long.MAX_VALUE);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, passedSubweeks, submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 6, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MAX_VALUE);
		
		Mockito.when(Utils.getEpochMillis()).thenReturn(Long.MAX_VALUE);
		
		Mockito.when(mockUserRepository.payOffDebt(Integer.MIN_VALUE, 0, user.getId())).thenReturn(0);
		
		userServiceSpy.processPaymentSubBalanceCommand(user, 5, submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(1)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
		Mockito.verify(mockUserRepository, times(0)).payOffDebt(Integer.MIN_VALUE, 0, user.getId());
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2ConsumerSubscribedUserPayedByO2Psms_Success() throws Exception{
		final Integer graceDurationSeconds = 2*Utils.WEEK_SECONDS;
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
		user.setContract(Contract.PAYG);
		final int oldNextSubPayment = currentTimeSeconds-graceDurationSeconds/2;
		user.setNextSubPayment(oldNextSubPayment);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(paymentDetailsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds+submittedPayment.getSubweeks()*Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(0);
		
		Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
		Mockito.when(Utils.getEpochMillis()).thenReturn(currentTimeMillis);
		
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", "o2.payment.psms.grace.duration.seconds", null, null)).thenReturn(graceDurationSeconds+"");
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
	}
	
	@Test
	public void testProcessPaymentSubBalanceCommand_O2PAYMConsumerSubscribedUserPayedByO2Psms_Success() throws Exception{
		final Integer graceDurationSeconds = 2*Utils.WEEK_SECONDS;
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
		user.setContract(Contract.PAYM);
		final int oldNextSubPayment = currentTimeSeconds-graceDurationSeconds/2;
		user.setNextSubPayment(oldNextSubPayment);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(paymentDetailsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User passedUser = (User)invocation.getArguments()[0];
				
				assertEquals(2, passedUser.getSubBalance());
				assertEquals(currentTimeSeconds+submittedPayment.getSubweeks()*Utils.WEEK_SECONDS, passedUser.getNextSubPayment());
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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(0);
		
		Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
		Mockito.when(Utils.getEpochMillis()).thenReturn(currentTimeMillis);
		
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", "o2.payment.psms.grace.duration.seconds", null, null)).thenReturn(graceDurationSeconds+"");
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(0)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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
		
		when(mockO2ClientService.getRedeemServerO2Url(eq(user.getMobile()))).thenReturn(redeemServerO2Url);
		
		String result = userServiceSpy.getRedeemServerO2Url(user);
	
		assertEquals(redeemServerO2Url, result);
		
		Mockito.verify(mockO2ClientService, times(1)).getRedeemServerO2Url(eq(user.getMobile()));
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
		final Integer graceDurationSeconds = 2*Utils.WEEK_SECONDS;
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
		user.setContract(Contract.PAYM);
		final int oldNextSubPayment = currentTimeSeconds-graceDurationSeconds/2;
		user.setNextSubPayment(oldNextSubPayment);
		
		final SubmittedPayment submittedPayment = SubmittedPaymentFactory.createSubmittedPayment();
		submittedPayment.setPaymentSystem(paymentDetailsType);
		submittedPayment.setSubweeks(5);
		
		AccountLog cardTopUpAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.CARD_TOP_UP).thenReturn(cardTopUpAccountLog);
		Mockito.when(mockEntityService.saveEntity(cardTopUpAccountLog)).thenReturn(cardTopUpAccountLog);
		
		AccountLog subscriptionChargeAccountLog = new AccountLog(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE); 
		PowerMockito.whenNew(AccountLog.class).withArguments(user.getId(), submittedPayment, 2, TransactionType.SUBSCRIPTION_CHARGE).thenReturn(subscriptionChargeAccountLog);
		Mockito.when(mockEntityService.saveEntity(subscriptionChargeAccountLog)).thenReturn(subscriptionChargeAccountLog);
		
		PowerMockito.mockStatic(UserStatusDao.class);
		
		PowerMockito.when(UserStatusDao.getSubscribedUserStatus()).thenReturn(subscribedUserStatus);
		PowerMockito.when(UserStatusDao.getLimitedUserStatus()).thenReturn(limitedUserStatus);
		PowerMockito.when(UserStatusDao.getEulaUserStatus()).thenReturn(eulaUserStatus);

		Mockito.when(mockEntityService.updateEntity(user)).thenAnswer(new Answer<User>() {

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
		PowerMockito.when(Utils.getNewNextSubPayment(user.getNextSubPayment())).thenReturn(Integer.MIN_VALUE);
		PowerMockito.when(Utils.getMontlyNextSubPayment(user.getNextSubPayment())).thenReturn(monthlyNextSubPayment);
		
		Mockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
		Mockito.when(Utils.getEpochMillis()).thenReturn(currentTimeMillis);
		
		Mockito.when(mockCommunityResourceBundleMessageSource.getMessage("o2", "o2.payment.psms.grace.duration.seconds", null, null)).thenReturn(graceDurationSeconds+"");
		
		userServiceSpy.processPaymentSubBalanceCommand(user, submittedPayment.getSubweeks(), submittedPayment);
		
		Mockito.verify(mockEntityService, times(1)).saveEntity(cardTopUpAccountLog);
		Mockito.verify(mockEntityService, times(monthlyNextSubPayment)).saveEntity(subscriptionChargeAccountLog);
		Mockito.verify(mockEntityService, times(1)).updateEntity(user);
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


		Mockito.when(mockUserRepository.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId)).thenReturn(users);
		
		List<User> actualUsers = userServiceSpy.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
		
		assertNotNull(actualUsers);
		assertEquals(2, actualUsers.size());
		assertTrue(users.contains(user));
		assertTrue(users.contains(user2));
		
		Mockito.verify(mockUserRepository, times(1)).findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId);
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
		PowerMockito.when(Utils.getEpochSeconds()).thenReturn(currentTimeSeconds);
		
		List<User> expectedUsers = Collections.<User>emptyList(); 
		
		Mockito.when(mockUserRepository.getUsersForRetryPayment(currentTimeSeconds)).thenReturn(expectedUsers);
		
		List<User> users = userServiceSpy.getUsersForRetryPayment();
		
		assertNotNull(users);
		assertEquals(expectedUsers, users);
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
				mockCommunityResourceBundleMessageSource.getMessage(Mockito.eq(upperCaseCommunityURL), Mockito.eq(messageCode), Mockito
						.argThat(matcher), Mockito.any(Locale.class))).thenReturn(message);

	}

	private void mockMakeFreeSMSRequest(final MigPaymentDetails currentMigPaymentDetails, String message, MigResponse migResponse) {
		Mockito.when(mockMigHttpService.makeFreeSMSRequest(currentMigPaymentDetails.getMigPhoneNumber(), message)).thenReturn(migResponse);
	}
}
