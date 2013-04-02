package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserRepositoryIT {
	
	private static final int HOUR_SECONDS = 60 * 60;
	private static final int DAY_SECONDS = 24 * HOUR_SECONDS;
	private static final int TWO_DAY_SECONDS = 2 * DAY_SECONDS;
	private static final byte o2CommunityId = 7;

	@Resource(name = "userRepository")
	private UserRepository userRepository;
	
	@Resource(name = "paymentDetailsRepository")
	private PaymentDetailsRepository paymentDetailsRepository;

	@Test
	@Rollback
	public void testFindBefore48hExpireUsers() throws Exception {
		final int epochSeconds = Utils.getEpochSeconds();

		User testUser = UserFactory.createUser();
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + TWO_DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(users.get(0).getId(), testUser.getId());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_InActivePaymentDetails() throws Exception {
		final int epochSeconds = Utils.getEpochSeconds();

		User testUser = UserFactory.createUser();
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + TWO_DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(false);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_LastBefore48SmsMillisAfter48() throws Exception {
		final int epochSeconds = Utils.getEpochSeconds();
		final int nextSubPaymentSeconds = epochSeconds + DAY_SECONDS;

		User testUser = UserFactory.createUser();
		testUser.setLastBefore48SmsMillis((nextSubPaymentSeconds-10)*1000L);
		testUser.setNextSubPayment(nextSubPaymentSeconds);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_NextSubPaymentAtThreeDays() throws Exception {
		final int epochSeconds = Utils.getEpochSeconds();

		User testUser = UserFactory.createUser();
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + 3*DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_NextSubPaymentAtDay() throws Exception {
		final int epochSeconds = Utils.getEpochSeconds();

		User testUser = UserFactory.createUser();
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds + DAY_SECONDS);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(1, users.size());
		assertEquals(users.get(0).getId(), testUser.getId());
	}
	
	@Test
	@Rollback
	public void testFindBefore48hExpireUsers_NextSubPaymentNow() throws Exception {
		final int epochSeconds = Utils.getEpochSeconds();

		User testUser = UserFactory.createUser();
		testUser.setLastBefore48SmsMillis(0);
		testUser.setNextSubPayment(epochSeconds);
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());

		testUser = userRepository.save(testUser);

		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);

		Pageable pageable = new PageRequest(0, 1);
		List<User> users = userRepository.findBefore48hExpireUsers(epochSeconds, pageable);

		assertNotNull(users);
		assertEquals(0, users.size());
	}
	
	@Test
	public void testUpdateLastBefore48SmsMillis_Success() throws Exception {
		long newLastBefore48SmsMillis = 10L;

		User testUser = UserFactory.createUser();
		testUser.setLastBefore48SmsMillis(Long.MIN_VALUE);
		
		testUser = userRepository.save(testUser);
		
		int updatedCount = userRepository.updateLastBefore48SmsMillis(newLastBefore48SmsMillis , testUser.getId());
		assertEquals(1, updatedCount);
		
//		User user = userRepository.findOne(testUser.getId());
//		
//		assertNotNull(user);
//		assertEquals(testUser.getId(), user.getId());
//		assertEquals(newLastBefore48SmsMillis, user.getLastBefore48SmsMillis());
		
	}
	
	@Test
	public void testgetUsersForRetryPayment_MadeRetriesNotEqRetriesOnError_Success() throws Exception {
		
		int epochSeconds = Utils.getEpochSeconds();
		
		User testUser = UserFactory.createUser();
		testUser.setNextSubPayment(epochSeconds + DAY_SECONDS);
		testUser.setLastDeviceLogin(epochSeconds);

		testUser = userRepository.save(testUser);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		currentO2PaymentDetails.setMadeRetries(0);
		currentO2PaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		currentO2PaymentDetails.setRetriesOnError(3);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);
		
		List<User> actualUsers = userRepository.getUsersForRetryPayment(epochSeconds);
		
		assertNotNull(actualUsers);
		assertEquals(1, actualUsers.size());
		assertEquals(testUser.getId(), actualUsers.get(0).getId());

	}
	
	@Test
	public void testGetUsersForRetryPayment_O2CommunityUserWithActivatePaymentDetailsAndNextSubPaymentInTheFutureAndMadeRetriesEqRetriesOnError_Success() throws Exception {
		
		int epochSeconds = Utils.getEpochSeconds();
		
		UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		
		User testUser = UserFactory.createUser();
		testUser.setNextSubPayment(epochSeconds + DAY_SECONDS);
		testUser.setLastDeviceLogin(epochSeconds);
		testUser.setUserGroup(o2UserGroup);

		testUser = userRepository.save(testUser);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		currentO2PaymentDetails.setMadeRetries(3);
		currentO2PaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		currentO2PaymentDetails.setRetriesOnError(3);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);
		
		List<User> actualUsers = userRepository.getUsersForRetryPayment(epochSeconds);
		
		assertNotNull(actualUsers);
		assertEquals(0, actualUsers.size());
	}
	
	@Test
	public void testGetUsersForRetryPayment_O2CommunityUserWithActivatePaymentDetailsAndActivatePaymentDetailsAndNextSubPaymentInThePastAndMadeRetriesEqRetriesOnError_Success() throws Exception {
		
		int epochSeconds = Utils.getEpochSeconds();
		
		UserGroup o2UserGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(o2CommunityId);
		
		User testUser = UserFactory.createUser();
		testUser.setNextSubPayment(epochSeconds);
		testUser.setLastDeviceLogin(epochSeconds);
		testUser.setUserGroup(o2UserGroup);

		testUser = userRepository.save(testUser);
		
		PaymentDetails currentO2PaymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();

		currentO2PaymentDetails.setActivated(true);
		currentO2PaymentDetails.setOwner(testUser);
		currentO2PaymentDetails.setMadeRetries(3);
		currentO2PaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.ERROR);
		currentO2PaymentDetails.setRetriesOnError(3);

		currentO2PaymentDetails = paymentDetailsRepository.save(currentO2PaymentDetails);
		
		testUser.setCurrentPaymentDetails(currentO2PaymentDetails);

		testUser = userRepository.save(testUser);
		
		List<User> actualUsers = userRepository.getUsersForRetryPayment(epochSeconds);
		
		assertNotNull(actualUsers);
		assertEquals(1, actualUsers.size());
		assertEquals(testUser.getId(), actualUsers.get(0).getId());

	}
}