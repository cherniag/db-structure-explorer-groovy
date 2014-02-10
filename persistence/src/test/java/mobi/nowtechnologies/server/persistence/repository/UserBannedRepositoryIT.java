package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserBanned;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;


/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class UserBannedRepositoryIT  extends AbstractRepositoryIT{
	
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