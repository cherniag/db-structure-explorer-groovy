package mobi.nowtechnologies.server.persistence.dao;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Titov Mykhaylo (titov)
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserDaoTestIT {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserDaoTestIT.class.getName());
	
	@Resource(name = "persistence.UserDao")
	private UserDao userDao;


	@Test
	public void testGetCommunityNameByUserGroup(){
		System.out.println(userDao.getCommunityNameByUserGroup((byte) 4));
	}
	
	@Test
	public void testFindUserGroupByCommunity() {
		System.out.println(userDao.getUserGroupByCommunity("Metal Hammer"));
	}
}
