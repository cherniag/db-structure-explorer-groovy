package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.UserType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * WeeklyUpdateServiceTest
 * 
 * @author Anton Rogachevskiy
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = false)
@Transactional
@Ignore
public class WeeklyUpdateServiceTest {

	@Resource(name = "service.WeeklyUpdateService")
	private WeeklyUpdateService weeklyUpdateService;
	private User testUser;

	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

	private static final mobi.nowtechnologies.server.persistence.domain.UserStatus userSubscribtionStatus = UserStatusDao.getEulaUserStatus();

	@Test
	public final void testSaveWeeklyPayment() throws Exception {
		// weeklyUpdateService.setMigService(service);
		weeklyUpdateService.saveWeeklyPayment(testUser);

	}

	@Test(expected = Exception.class)
	public final void testSaveWeeklyPaymentBalanceException() throws Exception {
		User user = new User();
		user.setSubBalance((byte) -1);
		weeklyUpdateService.saveWeeklyPayment(user);
	}

	@Test(expected = NullPointerException.class)
	public final void testSaveWeeklyPaymentWithUserIsNull() throws Exception {
		weeklyUpdateService.saveWeeklyPayment(null);
	}

	@Before
	public void setUp() throws Exception {

		/* User */
		testUser = new User();
		testUser.setAddress1("678");
		testUser.setAddress2("");
		testUser.setCanContact(true);
		testUser.setCity("St.Albans");
		testUser.setCode("f72b0b018fed801932f97f3e3a26b23f");
		testUser.setCountry(1);
		testUser.setDevice("HTC HERO");
		testUser.setDeviceString("iPhone");
		// testUser.setDeviceType((byte) 2);
		testUser.setDisplayName("Nigel");
		testUser.setFirstName("Nigel");
		testUser.setIpAddress("217.35.32.182");
		testUser.setLastDeviceLogin(1306902146);
		testUser.setLastName("Rees");
		testUser.setLastPaymentTx(72);
		testUser.setLastWebLogin(1306873638);
		testUser.setMobile("00447580381128");
		testUser.setNextSubPayment(1307219588);
		testUser.setOperator(1);
		testUser.setPaymentType(UserRegInfo.PaymentType.PREMIUM_USER);
		testUser.setPin("");
		testUser.setPostcode("412");
		testUser.setSessionID("attg0vs3e98dsddc2a4k9vdkc6");
		testUser.setStatus(userSubscribtionStatus);
		testUser.setSubBalance((byte) 5);
		testUser.setTempToken("NONE");
		testUser.setTitle("Mr");
		testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
		// testUser.setUserGroup();
		testUser.setUserName("test_transaction3@test.com");
		testUser.setUserType(UserType.NORMAL);

		entityDao.saveEntity(testUser);
	}

	@AfterTransaction
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

}
