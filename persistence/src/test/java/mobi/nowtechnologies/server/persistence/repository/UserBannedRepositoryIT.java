package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserBanned;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;


/**
 * @author Alexander Kolpakov (akolpakov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserBannedRepositoryIT {
	
	@Resource(name = "userRepository")
	private UserRepository userRepository;

	@Resource(name = "userBannedRepository")
	private UserBannedRepository userBannedRepository;

	@Test
	public void testSaveAndFindOneBannedUser() {
		Integer userId = 1;

        User user = userRepository.findOne(userId);
		UserBanned userBanned = new UserBanned(user);

		userBannedRepository.save(userBanned);

		UserBanned result = userBannedRepository.findOne(userId);
		
		assertEquals(userId.intValue(), result.getUser().getId());
	}	
}