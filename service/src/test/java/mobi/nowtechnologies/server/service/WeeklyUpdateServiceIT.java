package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.enums.UserType;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * @author Anton Rogachevskiy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class WeeklyUpdateServiceIT {

//    @Resource(name = "service.WeeklyUpdateService")
//    private WeeklyUpdateService weeklyUpdateService;

    @Resource
    UserStatusRepository userStatusRepository;

    @Resource
    UserRepository userRepository;

    @Test
    public final void testSaveWeeklyPayment() throws Exception {
        // weeklyUpdateService.setMigService(service);
        //weeklyUpdateService.saveWeeklyPayment(testUser);

    }

    @Test(expected = Exception.class)
    public final void testSaveWeeklyPaymentBalanceException() throws Exception {
        User user = new User();
        user.setSubBalance((byte) -1);
        //weeklyUpdateService.saveWeeklyPayment(userWithCommunity);
    }

    @Test(expected = NullPointerException.class)
    public final void testSaveWeeklyPaymentWithUserIsNull() throws Exception {
        //weeklyUpdateService.saveWeeklyPayment(null);
    }

    @Before
    public void setUp() throws Exception {

		/* User */
        User testUser = new User();
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
        testUser.setStatus(userStatusRepository.findByName(UserStatus.EULA.name()));
        testUser.setSubBalance((byte) 5);
        testUser.setTempToken("NONE");
        testUser.setTitle("Mr");
        testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
        // testUser.setUserGroup();
        testUser.setUserName("test_transaction3@test.com");
        testUser.setUserType(UserType.NORMAL);

        userRepository.save(testUser);
    }

}
