package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.shared.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * The class <code>ChartRepositoryTest</code> contains tests for the class <code>{@link ChartRepository}</code>.
 * 
 * @generatedBy CodePro at 28.05.12 17:33
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public class UserRepositoryIT {

	@Resource(name = "userRepository")
	private UserRepository userRepository;

	@Test
	public void testFindBefore48hExpireUsers() throws Exception {

		User testUser = UserFactory.createUser();
		testUser.setProvider("o2");
		testUser.setContract("payg");
		testUser.setSegment("consumer");
		
		userRepository.save(testUser);
		
		testUser = UserFactory.createUser();
		testUser.setProvider("o2");
		testUser.setContract("payg");
		testUser.setSegment("consumer");
		
		userRepository.save(testUser);
		
		testUser = UserFactory.createUser();
		testUser.setProvider("o2");
		testUser.setContract("paym");
		testUser.setSegment("consumer");
		
		userRepository.save(testUser);
		
		testUser = UserFactory.createUser();
		testUser.setProvider("o2");
		testUser.setContract("paym");
		testUser.setSegment("business");
		
		Pageable page = new PageRequest(0, 1);
		
		List<User> user = userRepository.findBefore48hExpireUsers(Utils.getEpochSeconds(), Collections.singletonList("o2"), Collections.singletonList("consumer"), Collections.singletonList("payg"), page);

		assertNotNull(user);
		assertEquals(1, user.size());
	}
}