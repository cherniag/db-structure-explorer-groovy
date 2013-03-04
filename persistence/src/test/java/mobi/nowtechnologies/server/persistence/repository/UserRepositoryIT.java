package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.enums.SegmentType;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

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
		testUser.setContract(Contract.PAYG);
		testUser.setSegment(SegmentType.CONSUMER);
		
		userRepository.save(testUser);
		
		testUser = UserFactory.createUser();
		testUser.setProvider("o2");
		testUser.setContract(Contract.PAYG);
		testUser.setSegment(SegmentType.CONSUMER);
		
		userRepository.save(testUser);
		
		testUser = UserFactory.createUser();
		testUser.setProvider("o2");
		testUser.setContract(Contract.PAYM);
		testUser.setSegment(SegmentType.CONSUMER);
		
		userRepository.save(testUser);
		
		testUser = UserFactory.createUser();
		testUser.setProvider("o2");
		testUser.setContract(Contract.PAYG);
		testUser.setSegment(SegmentType.CONSUMER);
		
		Pageable page = new PageRequest(0, 1);
		
		List<User> user = userRepository.findBefore48hExpireUsers(Utils.getEpochSeconds(), "o2", SegmentType.CONSUMER, Contract.PAYG);

		assertNotNull(user);
		assertEquals(1, user.size());
	}
}