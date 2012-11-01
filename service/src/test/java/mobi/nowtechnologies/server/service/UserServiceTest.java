package mobi.nowtechnologies.server.service;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.dao.UserDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
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
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.MigResponseFactory;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.admin.UserDto;
import mobi.nowtechnologies.server.shared.dto.admin.UserDtoFactory;
import mobi.nowtechnologies.server.shared.enums.TransactionType;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * The class <code>UserServiceTest</code> contains tests for the class
 * <code>{@link UserService}</code>.
 * 
 * @generatedBy CodePro at 20.08.12 18:31
 * @author Alexander Kolpakov (akolpakov)
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest( { UserStatusDao.class, Utils.class })
public class UserServiceTest {

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
	@Test(expected=Exception.class)
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
	public void testUpdateUser_PaymentEnabledIsFalseAndNextSubPaymentInTheFutureAndSubBalanceIsChangedAndIsFreeTrialIsTrue_Success() throws Exception {
		UserDto userDto = UserDtoFactory.createUserDto();

		final int originalSubBalance = 2;
		int nextSubPayment = Utils.getEpochSeconds()+24*60*60;

		userDto.setId(5);
		userDto.setUserStatus(mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED);
		userDto.setDisplayName("displayName");
		userDto.setSubBalance(3);
		userDto.setNextSubPayment(new Date(nextSubPayment*1000L+200000L));
		userDto.setPaymentEnabled(false);

		User mockedUser = UserFactory.createUser();

		mockedUser.setId(5);
		mockedUser.setStatus(null);
		mockedUser.setDisplayName("");
		mockedUser.setSubBalance(originalSubBalance);
		mockedUser.setNextSubPayment(nextSubPayment);
		mockedUser.setFreeTrialExpiredMillis(new Long(nextSubPayment*1000L));
		mockedUser.setPaymentEnabled(true);
		mockedUser.setLastSuccessfulPaymentTimeMillis(0L);

		PaymentDetails paymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		mockedUser.setCurrentPaymentDetails(paymentDetails);

		Map<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus> USER_STATUS_MAP_USER_STATUS_AS_KEY = new HashMap<mobi.nowtechnologies.server.shared.enums.UserStatus, UserStatus>();
		final UserStatus mockedUserStatus = new UserStatus();
		USER_STATUS_MAP_USER_STATUS_AS_KEY.put(userDto.getUserStatus(), mockedUserStatus);

		PowerMockito.when(mockUserRepository.findOne(userDto.getId())).thenReturn(mockedUser);
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.TRIAL_TOPUP)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
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

		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.TRIAL_TOPUP);
		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
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

		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
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

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
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

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
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

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
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

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}
	
	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected=ServiceException.class)
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}
	
	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected=ServiceException.class)
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
		Mockito.verify(userServiceSpy, times(0)).unsubscribeUser(Mockito.eq(mockedUser), Mockito.eq(UNSUBSCRIBED_BY_ADMIN));
	}
	
	/**
	 * Run the User updateUser(UserDto) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 20.08.12 18:31
	 */
	@Test(expected=ServiceException.class)
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
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE)).thenReturn(
				new AccountLog());
		PowerMockito.when(mockAccountLogService.logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP)).thenReturn(
				new AccountLog());
		PowerMockito.when(UserStatusDao.getUserStatusMapUserStatusAsKey()).thenReturn(USER_STATUS_MAP_USER_STATUS_AS_KEY);
		PowerMockito.when(mockEntityService.updateEntity(mockedUser)).thenReturn(mockedUser);

		userServiceSpy.updateUser(userDto);

		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), originalSubBalance, 0, null, TransactionType.SUBSCRIPTION_CHARGE);
		Mockito.verify(mockAccountLogService, times(0)).logAccountEvent(userDto.getId(), userDto.getSubBalance(), 0, null, TransactionType.SUPPORT_TOPUP);
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
		CountryService mockCountryService = PowerMockito.mock(CountryService.class);
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
		CommunityService mockCommunityService = PowerMockito.mock(CommunityService.class);
		DeviceService mockDeviceService = PowerMockito.mock(DeviceService.class);
		mockMigHttpService = PowerMockito.mock(MigHttpService.class);
		PaymentService mockPaymentService = PowerMockito.mock(PaymentService.class);
		mockAccountLogService = PowerMockito.mock(AccountLogService.class);
		MailService mockMailService = PowerMockito.mock(MailService.class);

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
	@Test(expected=ServiceException.class)
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

		//assertEquals(epochMillis, actualCurrentPaymentDetails.getDisableTimestampMillis());
		assertFalse(actualCurrentPaymentDetails.isActivated());
		//assertEquals(reason, actualCurrentPaymentDetails.getDescriptionError());

		Mockito.verify(mockEntityService).updateEntity(mockedUser);
		//Mockito.verify(mockEntityService).updateEntity(mockedCurrentPaymentDetails);
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
	
	@Test(expected=ServiceCheckedException.class)
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