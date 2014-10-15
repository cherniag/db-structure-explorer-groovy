package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.AccountLog;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.UserType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Maksym Chernolevskyi (maksym)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class EntityDaoIT {
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

	@Test
	//@TODO Complete test writing
	public void testUpdateEntity_1()
		throws Exception {
		
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
	public void testFind() {
		User user = entityDao.findById(User.class, 1);
		assertEquals("", user.getFirstName());
	}
	
	@Test
	public void testSaveAccountLog() {
		entityDao.saveEntity(new AccountLog(98708, null, (byte) 8, null));
	}
	
	@Test
	public void testFindListByProperty_Success() {
		Class<Genre> entityClass = Genre.class;
		String fieldName = Genre.Fields.name.toString();
		String fieldValue = "Default";
		List<Genre> genres = entityDao.findListByProperty(entityClass,
				fieldName, fieldValue);
		assertNotNull(genres);
	}

	@Test
	public void testFindByProperty_SuccessOrConditions() {
		Class<Media> entityClass = Media.class;
		String fieldName = Media.Fields.isrc.toString();
		String fieldValue = "US-UM7-11-00061";
		Media media = entityDao.findByProperty(entityClass, fieldName,
				fieldValue);
		assertNotNull(media);
	}

	// OR condtions
	@Test
	public void testFindListByProperty() {
		Class<Media> entityClass = Media.class;
		String fieldName = Media.Fields.isrc.toString();
		String[] fieldValue = new String[] { "USJAY1100032", "USAT21001886" };
		List<Media> medias = entityDao.findListByProperty(entityClass,
				fieldName, fieldValue);
		assertNotNull(medias);
	}

	/**
	 * and conditions
	 */
	@Test
	public void testFindListByProperties() {
		Class<Media> entityClass = Media.class;
		Map<String, Object> fieldNameValueMap = new HashMap<String, Object>();
		fieldNameValueMap.put(Media.Fields.i.toString(), 48);
		fieldNameValueMap.put(Media.Fields.isrc.toString(), "USAT21001886");
		
		List<Media> medias=entityDao.findListByProperties(entityClass, fieldNameValueMap);
		assertNotNull(medias);
	}

	/**
	 * and conditions
	 */
	@Test
	public void testFindByProperties() {
		Class<Media> entityClass = Media.class;
		Map<String, Object> fieldNameValueMap = new HashMap<String, Object>();
		fieldNameValueMap.put(Media.Fields.i.toString(), 50);
		fieldNameValueMap.put(Media.Fields.isrc.toString(), "US-UM7-11-00061");
		
		Media media=entityDao.findByProperties(entityClass, fieldNameValueMap);
		assertNotNull(media);
	}
}
