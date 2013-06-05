package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import mobi.nowtechnologies.server.persistence.dao.UserIPhoneDetailsDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetailsFactory;
import mobi.nowtechnologies.server.persistence.repository.UserIPhoneDetailsRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Pageable;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UserIPhoneDetailsServiceTest {
	
	private UserIPhoneDetailsService userIPhoneDetailsServiceFixture;
	private UserIPhoneDetailsRepository userIPhoneDetailsRepositoryMock;
	private EntityService entityServiceMock;
	private UserIPhoneDetailsDao userIPhoneDetailsDaoMock;

	@Before
	public void setUp()
			throws Exception {
		
		userIPhoneDetailsServiceFixture = new UserIPhoneDetailsService();
		
		entityServiceMock = mock(EntityService.class);
		userIPhoneDetailsRepositoryMock = mock(UserIPhoneDetailsRepository.class);
		userIPhoneDetailsDaoMock = mock(UserIPhoneDetailsDao.class);

		userIPhoneDetailsServiceFixture.setUserIPhoneDetailsDao(userIPhoneDetailsDaoMock);
		userIPhoneDetailsServiceFixture.setEntityService(entityServiceMock);
		userIPhoneDetailsServiceFixture.setUserIPhoneDetailsRepository(userIPhoneDetailsRepositoryMock);
	}

	@Test
	public void testUserIPhoneDetailsService_Constructor_Success()
			throws Exception {

		UserIPhoneDetailsService result = new UserIPhoneDetailsService();
		assertNotNull(result);
	}

	@Test
	public void testGetUserIPhoneDetailsListForPushNotification_Success()
			throws Exception {
		
		Community community = new Community();
		long nearestLatestPublishTimeMillis = 1L;
		
		List<UserIPhoneDetails> userIPhoneDetails = UserIPhoneDetailsFactory.createUserIPhoneDetailsSingletonList();
		
		Pageable pageable = mock(Pageable.class);
		
		when(userIPhoneDetailsRepositoryMock.getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable)).thenReturn(userIPhoneDetails);

		List<UserIPhoneDetails> result = userIPhoneDetailsServiceFixture.getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable);

		assertNotNull(result);
		assertEquals(userIPhoneDetails, result);
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetUserIPhoneDetailsListForPushNotification_userIPhoneDetailsRepositoryThrowException_Fail()
			throws Exception {
		
		Community community = new Community();
		long nearestLatestPublishTimeMillis = 1L;
		
		Pageable pageable = mock(Pageable.class);
		
		when(userIPhoneDetailsRepositoryMock.getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable)).thenThrow(new NullPointerException());
		
		userIPhoneDetailsServiceFixture.getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable);
	}

	@Test(expected = mobi.nowtechnologies.server.service.exception.ServiceException.class)
	public void testGetUserIPhoneDetailsListForPushNotification_CommunityIsNull_Fail()
			throws Exception {
		Community community = null;
		long nearestLatestPublishTimeMillis = 1L;
		
		Pageable pageable = mock(Pageable.class);

		userIPhoneDetailsServiceFixture.getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable);
	}

	@Test()
	public void testMarkUserIPhoneDetailsAsProcessed_Success()
			throws Exception {
		
		UserIPhoneDetails userIPhoneDetails = UserIPhoneDetailsFactory.createUserIPhoneDetails();
		long nearestLatestPublishTimeMillis = 1L;

		when(entityServiceMock.updateEntity(userIPhoneDetails)).thenReturn(userIPhoneDetails);
		
		UserIPhoneDetails result = userIPhoneDetailsServiceFixture.markUserIPhoneDetailsAsProcessed(userIPhoneDetails, nearestLatestPublishTimeMillis);

		assertNotNull(result);
		assertEquals(0, result.getStatus());
		assertEquals(nearestLatestPublishTimeMillis, result.getLastPushOfContentUpdateMillis());
		
	}
	
	@Test(expected = java.lang.NullPointerException.class)
	public void testMarkUserIPhoneDetailsAsProcessed_userIPhoneDetailsIsNull_Fail()
			throws Exception {
		
		UserIPhoneDetails userIPhoneDetails = null;
		long nearestLatestPublishTimeMillis = 1L;
		
		userIPhoneDetailsServiceFixture.markUserIPhoneDetailsAsProcessed(userIPhoneDetails, nearestLatestPublishTimeMillis);
	}
}