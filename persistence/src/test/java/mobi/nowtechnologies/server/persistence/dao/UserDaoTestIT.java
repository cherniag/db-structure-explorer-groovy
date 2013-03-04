package mobi.nowtechnologies.server.persistence.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.UserSegment;
import mobi.nowtechnologies.server.shared.enums.UserType;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>UserDaoTest</code> contains tests for the class <code>{@link UserDao}</code>.
 *
 * @generatedBy CodePro at 24.06.11 11:19
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = false)
@Transactional
public class UserDaoTestIT {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserDaoTestIT.class.getName());
	
	@Resource(name = "persistence.UserDao")
	private UserDao userDao;
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

//	/**
//	 * Run the User findByName(String) method test.
//	 *
//	 * @throws Exception
//	 *
//	 * @generatedBy CodePro at 24.06.11 11:19
//	 */
//	@Test
//	@Ignore
//	public void testFindByName_1()
//		throws Exception {
//		
//		User testUser= new User();
//		testUser.setAddress1("678");
//		testUser.setAddress2("");
//		testUser.setCanContact(true);
//		testUser.setCity("St.Albans");
//		testUser.setCode("f72b0b018fed801932f97f3e3a26b23f");
//		testUser.setCountry(1);
//		testUser.setDevice("HTC HERO");
//		testUser.setDeviceString("iPhone");
//		testUser.setDeviceType((byte) 2);
//		testUser.setDisplayName("Nigel");
//		testUser.setFirstName("Nigel");
//		testUser.setFreeBalance((byte) 0);
//		testUser.setId(1);
//		testUser.setIpAddress("217.35.32.182");
//		testUser.setLastDeviceLogin(1306902146);
//		testUser.setLastName("Rees");
//		testUser.setLastPaymentTx(72);
//		testUser.setLastWebLogin(1306873638);
//		testUser.setMobile("+447770608575");
//		testUser.setNextSubPayment(1307219588);
//		testUser.setPostcode("412");
//		testUser.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
//		testUser.setStatus((byte) 10);
//		testUser.setSubBalance((byte) 5);
//		testUser.setTempToken("NONE");
//		testUser.setTitle("Mr");
//		testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
//		testUser.setUserGroup((byte) 1);
//		testUser.setUserName("nr@rbt.com");
//		testUser.setUserType((byte) 4);
//
//		User result = userDao.findByName(testUser.getUserName());
//
//		assertNotNull(result);
//		assertEquals(testUser.getAddress1(), result.getAddress1());
//		assertEquals(testUser.getAddress2(), result.getAddress2());
//		assertEquals(testUser.getCanContact(), result.getCanContact());
//		assertEquals(testUser.getCity(), result.getCity());
//		assertEquals(testUser.getCode(), result.getCode());
//		assertEquals(testUser.getCountry(), result.getCountry());
//		assertEquals(testUser.getDevice(), result.getDevice());
//		assertEquals(testUser.getDeviceString(), result.getDeviceString());
//		assertEquals(testUser.getDeviceType(), result.getDeviceType());
//		assertEquals(testUser.getDisplayName(), result.getDisplayName());
//		assertEquals(testUser.getFirstName(), result.getFirstName());
//		assertEquals(testUser.getFreeBalance(), result.getFreeBalance());
//		assertEquals(testUser.getId(), result.getId());
//		assertEquals(testUser.getIpAddress(), result.getIpAddress());
//		assertEquals(testUser.getLastDeviceLogin(), result.getLastDeviceLogin());
//		assertEquals(testUser.getLastName(), result.getLastName());
//		assertEquals(testUser.getLastPaymentTx(), result.getLastPaymentTx());
//		assertEquals(testUser.getLastWebLogin(), result.getLastWebLogin());
//		assertEquals(testUser.getMobile(), result.getMobile());
//		assertEquals(testUser.getNextSubPayment(), result.getNextSubPayment());
//		assertEquals(testUser.getPostcode(), result.getPostcode());
//		assertEquals(testUser.getSessionID(), result.getSessionID());
//		assertEquals(testUser.getStatus(), result.getStatus());
//		assertEquals(testUser.getSubBalance(), result.getSubBalance());
//		assertEquals(testUser.getTempToken(), result.getTempToken());
//		assertEquals(testUser.getTitle(), result.getTitle());
//		assertEquals(testUser.getToken(), result.getToken());
//		assertEquals(testUser.getUserGroup(), result.getUserGroup());
//		assertEquals(testUser.getUserName(), result.getUserName());
//		assertEquals(testUser.getUserType(), result.getUserType());
//	}
	
	@Test
	public void testGetCommunityNameByUserGroup(){
		System.out.println(userDao.getCommunityNameByUserGroup((byte) 4));
	}
	
	@Test
	@Ignore
	public void testGetLatestPaymentForUser(){
		System.out.println(userDao.getLatestPaymentForUser(98735).getTxType());
	}
	
	@Test
	public void testFindUserGroupByCommunity() {
		System.out.println(userDao.getUserGroupByCommunity("Metal Hammer"));
	}
	
	@Test
	public void testGetLatestDeferredPaymentForUser() {
		System.out.println(userDao.getLatestDeferredPaymentForUser(98730));
	}
	
	@Test
	@Ignore
	public void testGetActivePromotion() {
//		int startDate = Utils.getEpochSeconds();
//		int endDate = 300 + startDate;

		byte userGroup = (byte) 5;

		Promotion promotion = userDao.getActivePromotion(userGroup);
		assertNotNull(promotion);

		assertEquals(0, promotion.getStartDate());
		assertEquals(1655270400, promotion.getEndDate());
		assertTrue(promotion.getIsActive());
		assertEquals(userGroup, promotion.getUserGroup());
		assertTrue(promotion.getNumUsers() < promotion.getMaxUsers());
		assertEquals(2, promotion.getFreeWeeks());
		//assertEquals(18, promotion.getNumUsers());
		//assertEquals(300, promotion.getMaxUsers());
	}
	
	@Test
	@Ignore
	public void test_getListOfUsersForPsmsRetry() {
		User testUser= new User();
		testUser.setAddress1("678");
		testUser.setAddress2("");
		testUser.setCanContact(true);
		testUser.setCity("St.Albans");
		testUser.setCode("f72b0b018fed801932f97f3e3a26b23f");
		testUser.setCountry(1);
		testUser.setDevice("HTC HERO");
		testUser.setDeviceString("iPhone");
		//testUser.setDeviceType((byte) 2);
		testUser.setDisplayName("Nigel");
		testUser.setFirstName("Nigel");
		testUser.setIpAddress("217.35.32.182");
		testUser.setLastDeviceLogin(1306902146);
		testUser.setLastName("Rees");
		testUser.setLastPaymentTx(72);
		testUser.setLastWebLogin(1306873638);
		testUser.setMobile("+447770608575");
		testUser.setNextSubPayment(1307219588);
		testUser.setPostcode("412");
		testUser.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setSubBalance((byte) 5);
		testUser.setTempToken("NONE");
		testUser.setTitle("Mr");
		testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
		//testUser.setUserGroup((byte) 1);
		testUser.setUserName("test_getListOfUsersForUpdate@rbt.com");
		testUser.setUserType(UserType.NORMAL);
		testUser.setPaymentType(UserRegInfo.PaymentType.UNKNOWN);
		testUser.setPin("pin");
		testUser.setPaymentStatus(PaymentStatusDao.getAWAITING_PSMS().getId());
		testUser.setPaymentEnabled(true);
		
		entityDao.saveEntity(testUser);
		
		List<User> users=userDao.getListOfUsersForPaymentRetry();
		assertNotNull(users);
		assertTrue(users.size()<1001&&users.size()>0);
		for (User user : users) {
			assertEquals(PaymentStatusDao.getAWAITING_PSMS().getId(),user.getPaymentStatus());
		}
	}
	
	@Test
	public void test_GetListOfUsersForWeeklyUpdate() {
		
		User testUser = createUser();
		testUser.setContract(Contract.PAYG.name());
		testUser.setSegment(UserSegment.Consumer.name());
		
		entityDao.saveEntity(testUser);
		
		testUser = createUser();
		testUser.setContract(Contract.PAYM.name());
		testUser.setSegment(UserSegment.Consumer.name());
		
		entityDao.saveEntity(testUser);
		
		testUser = createUser();
		testUser.setContract(Contract.PAYM.name());
		testUser.setSegment(UserSegment.Business.name());
		
		entityDao.saveEntity(testUser);
		
		List<User> users=userDao.getListOfUsersForWeeklyUpdate();
		assertNotNull(users);
		assertTrue(users.size() == 2);
		for (User user : users) {
			assertTrue(Utils.getEpochSeconds()>user.getNextSubPayment());
			assertEquals(10,user.getUserStatusId());
		}
	}
	
	@Test

	public void test_getListOfUsersForUpdate() throws Exception {
		User testUser= new User();
		testUser.setAddress1("678");
		testUser.setAddress2("");
		testUser.setCanContact(true);
		testUser.setCity("St.Albans");
		testUser.setCode("f72b0b018fed801932f97f3e3a26b23f");
		testUser.setCountry(1);
		testUser.setDevice("HTC HERO");
		testUser.setDeviceString("iPhone");
		//testUser.setDeviceType((byte) 2);
		testUser.setDisplayName("Nigel");
		testUser.setFirstName("Nigel");
		testUser.setIpAddress("217.35.32.182");
		testUser.setLastDeviceLogin(1306902146);
		testUser.setLastName("Rees");
		testUser.setLastPaymentTx(72);
		testUser.setLastWebLogin(1306873638);
		testUser.setMobile("+447770608575");
		testUser.setNextSubPayment(1307219588);
		testUser.setPostcode("412");
		testUser.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setSubBalance((byte) 0);
		testUser.setTempToken("NONE");
		testUser.setTitle("Mr");
		testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
		//testUser.setUserGroup((byte) 1);
		testUser.setUserName("test_getListOfUsersForUpdate@rbt.com");
		testUser.setUserType(UserType.NORMAL);
		testUser.setPaymentType(UserRegInfo.PaymentType.UNKNOWN);
		testUser.setPin("pin");
        testUser.setSegment(UserSegment.Consumer.name());
		testUser.setPaymentStatus(PaymentStatusDao.getAWAITING_PAYMENT().getId());
		testUser.setPaymentEnabled(true);
		
		entityDao.saveEntity(testUser);
		
		List<User> users=userDao.getListOfUsersForUpdate();
		assertNotNull(users);
		assertTrue(users.size()<1001&&users.size()>0);
		for (User user : users) {
			assertTrue(user.getUserStatusId()==10||user.getUserStatusId()==11);
			if (user.getId()==testUser.getId())
			{
				assertEquals(PaymentStatusDao.getAWAITING_PAYMENT().getId(),user.getPaymentStatus());
			}
			assertTrue(user.isPaymentEnabled());
			assertEquals(0, user.getSubBalance());
		}
	}
	
	@Test
	public void testFindUserTree() {
		int userId = 1;
		User user = userDao.findUserTree(userId);
		assertNotNull(user);
	}
	
	private User createUser(){
		DeviceType deviceType = new DeviceType();
		deviceType.setI((byte)5);
		
		UserGroup userGroup = new UserGroup();
		UserFactory.createUser();
		userGroup.setI((byte)7);
		
		User testUser= new User();
		testUser.setAddress1("678");
		testUser.setAddress2("");
		testUser.setCanContact(true);
		testUser.setCity("St.Albans");
		testUser.setCode("f72b0b018fed801932f97f3e3a26b23f");
		testUser.setCountry(1);
		testUser.setDevice("HTC HERO");
		testUser.setDeviceString("iPhone");
		testUser.setDeviceType(deviceType);
		testUser.setDisplayName("Nigel");
		testUser.setFirstName("Nigel");
		testUser.setIpAddress("217.35.32.182");
		testUser.setLastDeviceLogin(1306902146);
		testUser.setLastName("Rees");
		testUser.setLastPaymentTx(72);
		testUser.setLastWebLogin(1306873638);
		testUser.setMobile("+447770608575");
		testUser.setNextSubPayment(1307219588);
		testUser.setPostcode("412");
		testUser.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
		testUser.setStatus(UserStatusDao.getSubscribedUserStatus());
		testUser.setSubBalance((byte) 5);
		testUser.setTempToken("NONE");
		testUser.setTitle("Mr");
		testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
		testUser.setUserGroup(userGroup);
		testUser.setUserName("test_getListOfUsersForUpdate@rbt.com");
		testUser.setUserType(UserType.NORMAL);
		testUser.setPaymentType(UserRegInfo.PaymentType.UNKNOWN);
		testUser.setPin("pin");
		testUser.setPaymentStatus(PaymentStatusDao.getAWAITING_PSMS().getId());
		testUser.setPaymentEnabled(true);
		
		return testUser;
	}
}
