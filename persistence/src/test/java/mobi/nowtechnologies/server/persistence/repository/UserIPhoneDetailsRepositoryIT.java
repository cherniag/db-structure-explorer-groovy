package mobi.nowtechnologies.server.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetailsFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class UserIPhoneDetailsRepositoryIT {
	
	@Resource(name="userIPhoneDetailsRepository")
	private UserIPhoneDetailsRepository userIPhoneDetailsRepository;
	
	@Resource(name="communityRepository")
	private CommunityRepository communityRepository;
	
	@Resource(name="userRepository")
	private UserRepository userRepository;;
	
	@Test
	public void testGetUserIPhoneDetailsListForPushNotification_UsersExsist_Success(){
		final long currentTimeMillis = Long.MAX_VALUE;
		User user = userRepository.findOne(1); 
		
		Pageable pageable = new PageRequest(0, 1000);
		
		Community community = communityRepository.findByRewriteUrlParameter("chartsnow");
		
		UserGroup userGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId());
		
		UserIPhoneDetails userIPhoneDetails = UserIPhoneDetailsFactory.createUserIPhoneDetails();
		userIPhoneDetails.setUserGroup(userGroup);
		userIPhoneDetails.setUser(user);
		userIPhoneDetails.setLastPushOfContentUpdateMillis(currentTimeMillis-10000L);
		
		userIPhoneDetails = userIPhoneDetailsRepository.save(userIPhoneDetails);
		
		List<UserIPhoneDetails> userIPhoneDetailsList = userIPhoneDetailsRepository.getUserIPhoneDetailsListForPushNotification(community, currentTimeMillis, pageable);
		
		assertNotNull(userIPhoneDetailsList);
		assertEquals(1, userIPhoneDetailsList.size());
		assertEquals(userIPhoneDetails.getId(), userIPhoneDetailsList.get(0).getId());
		
	}
	
	@Test
	public void testGetUserIPhoneDetailsListForPushNotification_UsersDoeNotExsist_Success(){
		final long currentTimeMillis = Long.MAX_VALUE;
		User user = userRepository.findOne(1); 
		
		Pageable pageable = new PageRequest(0, 1000);
		
		Community community = communityRepository.findByRewriteUrlParameter("chartsnow");
		
		UserGroup userGroup = UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(community.getId());
		
		UserIPhoneDetails userIPhoneDetails = UserIPhoneDetailsFactory.createUserIPhoneDetails();
		userIPhoneDetails.setUserGroup(userGroup);
		userIPhoneDetails.setUser(user);
		userIPhoneDetails.setLastPushOfContentUpdateMillis(currentTimeMillis);
		
		userIPhoneDetails = userIPhoneDetailsRepository.save(userIPhoneDetails);
		
		List<UserIPhoneDetails> userIPhoneDetailsList = userIPhoneDetailsRepository.getUserIPhoneDetailsListForPushNotification(community, currentTimeMillis, pageable);
		
		assertNotNull(userIPhoneDetailsList);
		assertEquals(0, userIPhoneDetailsList.size());
		
	}
	
}