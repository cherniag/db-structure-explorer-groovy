package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.SetPassword;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * The class <code>UserServiceTest</code> contains tests for the class <code>{@link UserService}</code>.
 *
 * @generatedBy CodePro at 29.06.11 13:05
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserServiceTestIT {
	
	private static final int HOUR_SECONDS = 60 * 60;
	private static final int DAY_SECONDS = 24 * HOUR_SECONDS;
	private static final int TWO_DAY_SECONDS = 2 * DAY_SECONDS;
	
	@Resource(name="service.UserService")
	private UserService userService;
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

	/**
	 * Run the UserService() constructor test.
	 *
	 * @generatedBy CodePro at 29.06.11 13:05
	 */
	@Test
	public void testUserService()
		throws Exception {
		assertNotNull(userService);
		// add additional test code here
	}

	/**
	 * Run the int checkCredentialsAndStatus(String,String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 29.06.11 13:05
	 */
	@Test
	public void testCheckCredentialsAndStatus_Success()
		throws Exception {
		
		String userName = "test@test.com";
		String password="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(Calendar.getInstance().getTime());
		//String storredToken = userService.getStoredToken(userName, password);
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";

		@SuppressWarnings("deprecation")
		User result = userService.checkCredentials(
				userName, Utils.createTimestampToken(storedToken, timestamp), 
				timestamp, "CN Commercial Beta");

		assertEquals(1, result.getId());
	}

	/**
	 * Run the int checkCredentialsAndStatus(String,String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 29.06.11 13:05
	 */
	@Test(expected=UserCredentialsException.class)
	public void testCheckCredentialsAndStatus_Wrong()
		throws Exception {
		
		Calendar currentDateCalendar=Calendar.getInstance();
		System.out.println(currentDateCalendar.toString());
		String userName = "66";
		String userToken = "1";
		String timestamp = "1";

		userService.checkCredentials(
				userName, userToken, timestamp, "CN Commercial Beta");
	}

	/**
	 * Run the Object[] setPassword(int,String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 29.06.11 13:05
	 */
	@Ignore
	@Test
	public void testSetPassword_Success()
		throws Exception {
		int aUserId = 1;
		String token = "";
		String aCommunityName="";

		Object[] result = userService.processSetPasswordCommand(aUserId, token,aCommunityName);

		assertNotNull(result);
		assertTrue(2==result.length);
		Class firstElementClass=result[0].getClass();
		assertTrue (firstElementClass.equals(AccountCheckDTO.class)||firstElementClass.equals(SetPassword.class));
		Class secondElementClass=result[1].getClass();
		assertTrue (secondElementClass.equals(AccountCheckDTO.class)||secondElementClass.equals(SetPassword.class));
		
		assertFalse(firstElementClass.equals(secondElementClass));
		
	}

	/**
	 * Run the Object[] setPassword(int,String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 29.06.11 13:05
	 */
	@Ignore
	@Test(expected = java.lang.NullPointerException.class)
	public void testSetPassword_2()
		throws Exception {
		int aUserId = 1;
		String token = null;
		String aCommunityName="";

		Object[] result = userService.processSetPasswordCommand(aUserId, token,aCommunityName);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the Object[] setPassword(int,String,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 29.06.11 13:05
	 */
	@Ignore
	@Test(expected = java.lang.NullPointerException.class)
	public void testSetPassword_3()
		throws Exception {
		int aUserId = 1;
		String token = "";
		String aCommunityName=null;

		Object[] result = userService.processSetPasswordCommand(aUserId, token,aCommunityName);

		// add additional test code here
		assertNotNull(result);
	}
	
	@Test
	public void testFindByIsrc_Success(){
		User user=userService.findByName("test@test.com");
		assertNotNull(user);
	}
	
	@Test(expected=ServiceException.class)
	public void testFindByIsrc_mediaIsrcIsNull(){
		userService.findByName(null);
	}
	
	@Test
	public void testProceessAccountCheckCommand() {
		int userId=1;
		AccountCheckDTO accountCheckDTO = userService.proceessAccountCheckCommandForAuthorizedUser(userId, null, null, null);
		assertNotNull(accountCheckDTO);
	}
	
	@Test
	@Rollback
	public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithActiveMigPaymentDetailsAndNotZeroBalanceAndNextSubPaymentInThePastAndLastSubscribedPaymentSystemIsNull_Success() {		
		User testUser = UserFactory.createUser();
		testUser.setSubBalance(1);
		testUser.setDeactivatedO2PSMSGraceCreditMillis(Long.MAX_VALUE);
		testUser.setNextSubPayment(Utils.getEpochSeconds()-100);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setLastSubscribedPaymentSystem(null);
		
		entityDao.saveEntity(testUser);
		
		PaymentDetails currentMigPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		
		currentMigPaymentDetails.setActivated(false);
		currentMigPaymentDetails.setOwner(testUser);
		
		entityDao.saveEntity(currentMigPaymentDetails);
		testUser.setCurrentPaymentDetails(currentMigPaymentDetails);
		entityDao.updateEntity(testUser);
		
		List<User> users = userService.getListOfUsersForWeeklyUpdate();
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(testUser.getId(), users.get(0).getId());
	}
	
	@Test
	@Rollback
	public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithInactiveMigPaymentDetailsAndZeroBalanceAndNextSubPaymentInThePastAndLastSubscribedPaymentSystemIsPSMS_Success() {
		final int gracePeriodSeconds = TWO_DAY_SECONDS;
		
		User testUser = UserFactory.createUser();
		testUser.setSubBalance(0);
		testUser.setDeactivatedO2PSMSGraceCreditMillis(gracePeriodSeconds*1000L);
		testUser.setNextSubPayment(Utils.getEpochSeconds()-gracePeriodSeconds - 10);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		
		entityDao.saveEntity(testUser);
		
		PaymentDetails currentMigPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		
		currentMigPaymentDetails.setActivated(false);
		currentMigPaymentDetails.setOwner(testUser);
		
		entityDao.saveEntity(currentMigPaymentDetails);
		testUser.setCurrentPaymentDetails(currentMigPaymentDetails);
		entityDao.updateEntity(testUser);
		
		List<User> users = userService.getListOfUsersForWeeklyUpdate();
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(testUser.getId(), users.get(0).getId());
	}
	
	@Test
	@Rollback
	public void testGetListOfUsersForWeeklyUpdate_FreeTrial_Success() {
		User testUser = UserFactory.createUser();
		testUser.setSubBalance(0);
		testUser.setDeactivatedO2PSMSGraceCreditMillis(0L);
		testUser.setNextSubPayment(Utils.getEpochSeconds()- 10);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setCurrentPaymentDetails(null);
		
		entityDao.saveEntity(testUser);
		
		List<User> users = userService.getListOfUsersForWeeklyUpdate();
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(testUser.getId(), users.get(0).getId());
	}
	
	@Test
	@Rollback
	public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithActiveO2PaymentDetailsAndZeroBalanceAndNextSubPaymentInThePast_Success() {
		
		User testUser = UserFactory.createUser();
		testUser.setSubBalance(0);
		testUser.setDeactivatedO2PSMSGraceCreditMillis(0L);
		testUser.setNextSubPayment(Utils.getEpochSeconds() - TWO_DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		
		entityDao.saveEntity(testUser);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		
		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		
		entityDao.saveEntity(currentO2PaymentDetails);
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);
		entityDao.updateEntity(testUser);
		
		List<User> users = userService.getListOfUsersForWeeklyUpdate();
		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	@Rollback
	public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithInActiveO2PaymentDetailsAndZeroBalanceAndNextSubPaymentInThePast_Success() {
		
		User testUser = UserFactory.createUser();
		testUser.setSubBalance(0);
		testUser.setDeactivatedO2PSMSGraceCreditMillis(0L);
		testUser.setNextSubPayment(Utils.getEpochSeconds() - TWO_DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setLastSubscribedPaymentSystem(PaymentDetails.O2_PSMS_TYPE);
		
		entityDao.saveEntity(testUser);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		
		currentO2PaymentDetails.setActivated(false);
		currentO2PaymentDetails.setOwner(testUser);
		
		entityDao.saveEntity(currentO2PaymentDetails);
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);
		entityDao.updateEntity(testUser);
		
		List<User> users = userService.getListOfUsersForWeeklyUpdate();
		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(testUser.getId(), users.get(0).getId());
	}
	
	@Test
	@Rollback
	public void testGetListOfUsersForWeeklyUpdate_SubscribedUserWithActiveO2PaymentDetailsAndZeroBalanceAndNextSubPaymentInThePastAndLastSubscribedPaymentSystemIsMIG_Success() {
		
		User testUser = UserFactory.createUser();
		testUser.setSubBalance(0);
		testUser.setDeactivatedO2PSMSGraceCreditMillis(0L);
		testUser.setNextSubPayment(Utils.getEpochSeconds() - TWO_DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setLastSubscribedPaymentSystem(PaymentDetails.MIG_SMS_TYPE);
		
		entityDao.saveEntity(testUser);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
		
		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		
		entityDao.saveEntity(currentO2PaymentDetails);
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);
		entityDao.updateEntity(testUser);
		
		List<User> users = userService.getListOfUsersForWeeklyUpdate();
		assertNotNull(users);
		assertEquals(0, users.size());
	}

}