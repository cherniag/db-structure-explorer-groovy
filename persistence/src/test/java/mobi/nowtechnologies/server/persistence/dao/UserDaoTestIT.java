package mobi.nowtechnologies.server.persistence.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.User;

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


	@Test
	public void testGetCommunityNameByUserGroup(){
		System.out.println(userDao.getCommunityNameByUserGroup((byte) 4));
	}
	
	@Test
	public void testFindUserGroupByCommunity() {
		System.out.println(userDao.getUserGroupByCommunity("Metal Hammer"));
	}
	
	@Test
	public void testFindUserTree() {
		int userId = 1;
		User user = userDao.findUserTree(userId);
		assertNotNull(user);
	}
}
