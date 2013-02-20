package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.SetPassword;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = false)
@Transactional
public class UserServiceTestIT {
	
	@Resource(name="service.UserService")
	private UserService userService;
	
	@Resource(name="userRepository")
	private UserRepository userRepository;

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
	public void testFindUsersForItunesInAppSubscription_Success(){
		int nextSubPayment = 1;
		String appStoreOriginalTransactionId="appStoreOriginalTransactionId";

		User user = UserFactory.createUser();
		User user2 = UserFactory.createUser();
		
		List<User> users = new ArrayList<User>();
		users.add(user);
		users.add(user2);
		
		
		for (int i = 0; i < users.size(); i++) {
			User u = users.get(i);
		
			u.setId(104+i);
			u.setUserName("userName" + i);
			u.setDeviceUID("deviceUID" + i);
			u.setNextSubPayment(0);
			u.setAppStoreOriginalTransactionId(appStoreOriginalTransactionId);
			u.setStatus(UserStatusDao.getLimitedUserStatus());
			u.setUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get((byte) 7));
			u.setProvider("provider");
			u.setCurrentPaymentDetails(null);
		}
		
		userRepository.save(users);
		
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		
		List<User> actualUsers = userService.findUsersForItunesInAppSubscription(user, nextSubPayment, appStoreOriginalTransactionId, pageable);
		
		assertNotNull(actualUsers);
		assertEquals(users.size(), actualUsers.size());
		assertTrue(users.contains(user));
		assertTrue(users.contains(user2));
	}

}