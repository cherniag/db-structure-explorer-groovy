package mobi.nowtechnologies.server.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;

import java.util.concurrent.Future;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.ServiceCheckedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * The class <code>UserNotificationImplTest</code> contains tests for the class <code>{@link UserNotificationServiceImpl}</code>.
 *
 * @generatedBy CodePro at 04.09.12 13:21
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class UserNotificationImplTest {
	private UserService mockUserService;
	private UserNotificationServiceImpl fixtureUserNotificationImpl;

	/**
	 * Run the UserNotificationImpl() constructor test.
	 *
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testUserNotificationImpl_1()
		throws Exception {
		UserNotificationServiceImpl result = new UserNotificationServiceImpl();
		assertNotNull(result);
	}

	/**
	 * Run the Future<Boolean> notifyUserAboutSuccesfullPayment(User) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testNotifyUserAboutSuccesfullPayment_Success()
		throws Exception {
		
		User user = UserFactory.createUser();
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		user.setUserGroup(userGroup);

		Future<Boolean> futureResult = new AsyncResult<Boolean>(Boolean.TRUE);
		
		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenReturn(futureResult);
		
		Future<Boolean> result = fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.TRUE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());
		
		Mockito.verify(mockUserService).makeSuccesfullPaymentFreeSMSRequest(user);
	}

	/**
	 * Run the Future<Boolean> notifyUserAboutSuccesfullPayment(User) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testNotifyUserAboutSuccesfullPayment_UserIsNull_Failure()
		throws Exception {
		User user = null;

		Future<Boolean> futureResult = new AsyncResult<Boolean>(Boolean.TRUE);
		
		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenReturn(futureResult);
		
		fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);
	
		
		Mockito.verify(mockUserService, times(0)).makeSuccesfullPaymentFreeSMSRequest(user);
	}

	/**
	 * Run the void setUserService(UserService) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testSetUserService_UserNotificationThrowsRuntimeException_Success()
		throws Exception {
		User user = UserFactory.createUser();
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		user.setUserGroup(userGroup);
		
		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenThrow(new RuntimeException());
		
		Future<Boolean> result = fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.FALSE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());
		
		Mockito.verify(mockUserService).makeSuccesfullPaymentFreeSMSRequest(user);
	}
	
	/**
	 * Run the void setUserService(UserService) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Test
	public void testSetUserService_UserNotificationThrowsServiceCheckedException_Success()
		throws Exception {
		User user = UserFactory.createUser();
		Community community = CommunityFactory.createCommunity();
		UserGroup userGroup = UserGroupFactory.createUserGroup(community);
		user.setUserGroup(userGroup);
		
		Mockito.when(mockUserService.makeSuccesfullPaymentFreeSMSRequest(user)).thenThrow(new ServiceCheckedException(null, null, null));
		
		Future<Boolean> result = fixtureUserNotificationImpl.notifyUserAboutSuccesfullPayment(user);

		assertNotNull(result);
		assertEquals(Boolean.FALSE, result.get());
		assertEquals(false, result.isCancelled());
		assertEquals(true, result.isDone());
		
		Mockito.verify(mockUserService).makeSuccesfullPaymentFreeSMSRequest(user);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@Before
	public void setUp()
		throws Exception {
		fixtureUserNotificationImpl = new UserNotificationServiceImpl();
		
		mockUserService = Mockito.mock(UserService.class);
		fixtureUserNotificationImpl.setUserService(mockUserService);
		
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 04.09.12 13:21
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}
}