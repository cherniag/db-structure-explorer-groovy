package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.UserType;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * @author Maksym Chernolevskyi (maksym)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class EntityDaoIT {

    @Resource(name = "persistence.EntityDao")
    private EntityDao entityDao;

    @Test
    //@TODO Complete test writing
    public void testUpdateEntity_1() throws Exception {

        User testUser = new User();
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
        testUser.setId(1);
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
        testUser.setSubBalance(5);
        testUser.setTempToken("NONE");
        testUser.setTitle("Mr");
        testUser.setToken("26b34b31237dfffb4caeb9518ad1ce02");
        //testUser.setUserGroup((byte) 1);
        testUser.setUserName("nr@rbt.com");
        testUser.setUserType(UserType.NORMAL);

        //entityDao.updateEntity(testUser);
    }

    @Test
    public void testSaveAccountLog() {
        entityDao.saveEntity(new AccountLog(98708, null, (byte) 8, null));
    }


}
