package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * The class <code>UserIPhoneDetailsDaoTest</code> contains tests for the class <code>{@link UserIPhoneDetailsDao}</code>.
 *
 * @generatedBy CodePro at 06.01.12 12:18
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserIPhoneDetailsDaoIT {
	
	@Resource(name="persistence.UserIPhoneDetailsDao")
	private UserIPhoneDetailsDao userDIPhoneDetailsDao;

	/**
	 * Run the List<UserIPhoneDetails> getUserIPhoneDetailsListForPushNotification(Community) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 06.01.12 12:18
	 */
	@Test
	public void testGetUserIPhoneDetailsListForPushNotification_1()
		throws Exception {
		
		final Community community = Mockito.mock(Community.class);
		Mockito.doReturn((byte)5).when(community).getId();

		List<UserIPhoneDetails> userIPhoneDetailsList = userDIPhoneDetailsDao.getUserIPhoneDetailsListForPushNotification(community);
		assertNotNull(userIPhoneDetailsList);
	}

	/**
	 * Run the List<UserIPhoneDetails> getUserIPhoneDetailsListForPushNotification(Community) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 06.01.12 12:18
	 */
	@Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
	public void testGetUserIPhoneDetailsListForPushNotification_3()
		throws Exception {
		UserIPhoneDetailsDao fixture = new UserIPhoneDetailsDao();
		Community community = null;

		List<UserIPhoneDetails> result = fixture.getUserIPhoneDetailsListForPushNotification(community);
	}
}