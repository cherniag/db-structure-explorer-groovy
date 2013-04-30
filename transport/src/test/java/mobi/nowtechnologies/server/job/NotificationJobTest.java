package mobi.nowtechnologies.server.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javapns.Push;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.PayloadPerDevice;
import javapns.notification.PushedNotification;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetails;
import mobi.nowtechnologies.server.persistence.domain.UserIPhoneDetailsFactory;
import mobi.nowtechnologies.server.service.ChartDetailService;
import mobi.nowtechnologies.server.service.UserIPhoneDetailsService;
import mobi.nowtechnologies.server.shared.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CommunityDao.class, Utils.class, Push.class, PushedNotification.class, Vector.class })
public class NotificationJobTest {

	private NotificationJob notificationJobFixture;
	private UserIPhoneDetailsService userIPhoneDetailsServiceMock;
	private ChartDetailService chartDetailServiceMock;
	private Resource keystoreMock;

	@Before
	public void setUp()
			throws Exception {
		notificationJobFixture = new NotificationJob();
	}

	@Test
	public void testNotificationJob_Constructor_Success()
			throws Exception {
		assertNotNull(notificationJobFixture);
	}

	@Test
	public void testSetChartDetailService_Success()
			throws Exception {
		ChartDetailService chartDetailService = new ChartDetailService();

		notificationJobFixture.setChartDetailService(chartDetailService);
		assertNotNull(notificationJobFixture.chartDetailService);

	}

	@Test
	public void testSetCommunityName_Success()
			throws Exception {

		String communityName = "";

		Community community = CommunityFactory.createCommunity();

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(community);

		notificationJobFixture.setCommunityName(communityName);

		assertNotNull(notificationJobFixture.community);
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void testSetCommunityName_communityNameIsNull_Fail()
			throws Exception {
		String communityName = null;

		notificationJobFixture.setCommunityName(communityName);

	}

	@Test(expected = java.lang.NullPointerException.class)
	public void testSetCommunityName_NoSuchCommunity_Fail()
			throws Exception {

		String communityName = "";

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(null);

		notificationJobFixture.setCommunityName(communityName);
	}

	@Test
	public void testSetKeystore_Success()
			throws Exception {
		Resource keystore = new ClassPathResource("");

		notificationJobFixture.setKeystore(keystore);

		assertNotNull(notificationJobFixture.keystore);

	}

	@Test
	public void testSetNumberOfThreads_Success()
			throws Exception {
		int numberOfThreads = 1;

		notificationJobFixture.setNumberOfThreads(numberOfThreads);
		assertEquals(numberOfThreads, notificationJobFixture.numberOfThreads);

	}

	@Test
	public void testSetPassword_Success()
			throws Exception {
		String password = "";

		notificationJobFixture.setPassword(password);
		assertEquals(password, notificationJobFixture.password);

	}

	@Test
	public void testSetProduction_Success()
			throws Exception {
		boolean production = true;

		notificationJobFixture.setProduction(production);
		assertEquals(production, notificationJobFixture.production);

	}

	@Test
	public void testSetUserIPhoneDetailsService_Success()
			throws Exception {
		UserIPhoneDetailsService userIPhoneDetailsService = new UserIPhoneDetailsService();

		notificationJobFixture.setUserIPhoneDetailsService(userIPhoneDetailsService);
		assertEquals(userIPhoneDetailsService, notificationJobFixture.userIPhoneDetailsService);

	}

	@Test
	public void testExecute_Success()
			throws Exception {
		chartDetailServiceMock = mock(ChartDetailService.class);
		userIPhoneDetailsServiceMock = mock(UserIPhoneDetailsService.class);
		boolean production = false;
		String password = "";
		int numberOfThreads = Integer.MIN_VALUE;
		keystoreMock = mock(Resource.class);
		String communityName = "";

		Community community = CommunityFactory.createCommunity();

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(community);

		notificationJobFixture.setChartDetailService(chartDetailServiceMock);
		notificationJobFixture.setUserIPhoneDetailsService(userIPhoneDetailsServiceMock);
		notificationJobFixture.setCommunityName(communityName);
		notificationJobFixture.setKeystore(keystoreMock);
		notificationJobFixture.setNumberOfThreads(numberOfThreads);
		notificationJobFixture.setPassword(password);
		notificationJobFixture.setProduction(production);

		long epochMillis = Long.MAX_VALUE;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Long nearestLatestPublishTimeMillis = Long.MIN_VALUE;

		when(chartDetailServiceMock.findNearestLatestPublishTimeMillis(community, epochMillis)).thenReturn(nearestLatestPublishTimeMillis);

		final int userIPhoneDetailsListSize = 4;
		List<UserIPhoneDetails> userIPhoneDetailsList = UserIPhoneDetailsFactory.createUserIPhoneDetailsList(userIPhoneDetailsListSize);
		
		Pageable pageable = mock(Pageable.class);

		when(userIPhoneDetailsServiceMock.getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable)).thenReturn(userIPhoneDetailsList);

		mockStatic(Push.class);

		List<PushedNotification> pushedNotifications = Collections.<PushedNotification> emptyList();

		when(Push.payloads(eq(keystoreMock.getInputStream()), eq(password), eq(production),
				eq(numberOfThreads), any(Vector.class))).thenReturn(pushedNotifications);

		final int successfulPushedNotificationsSize = 2;
		int failPushedNotificationsSize = userIPhoneDetailsListSize - successfulPushedNotificationsSize;
		List<PushedNotification> successfulPushedNotifications = new ArrayList<PushedNotification>(successfulPushedNotificationsSize);
		List<PushedNotification> failPushedNotifications = new ArrayList<PushedNotification>(failPushedNotificationsSize);
		
		final User user = UserFactory.createUser();
		for (int i = 0; i < userIPhoneDetailsListSize; i++) {

			final UserIPhoneDetails userIPhoneDetails = userIPhoneDetailsList.get(i);
			userIPhoneDetails.setUser(user);

			Device device = new BasicDevice();
			device.setToken(userIPhoneDetails.getToken());

			PushedNotification pushedNotificationMock = mock(PushedNotification.class);

			when(pushedNotificationMock.getDevice()).thenReturn(device);

			if (i < successfulPushedNotificationsSize) {
				successfulPushedNotifications.add(pushedNotificationMock);

				when(userIPhoneDetailsServiceMock.markUserIPhoneDetailsAsProcessed(userIPhoneDetails, nearestLatestPublishTimeMillis)).thenReturn(userIPhoneDetails);
			} else {
				failPushedNotifications.add(pushedNotificationMock);
			}
		}

		mockStatic(PushedNotification.class);
		when(PushedNotification.findSuccessfulNotifications(pushedNotifications)).thenReturn(successfulPushedNotifications);
		when(PushedNotification.findFailedNotifications(pushedNotifications)).thenReturn(failPushedNotifications);

		notificationJobFixture.execute();

		verify(chartDetailServiceMock, times(1)).findNearestLatestPublishTimeMillis(community, epochMillis);
		verify(userIPhoneDetailsServiceMock, times(1)).getUserIPhoneDetailsListForPushNotification(community, nearestLatestPublishTimeMillis, pageable);

		for (int i = 0; i < successfulPushedNotificationsSize; i++) {
			final UserIPhoneDetails userIPhoneDetails = userIPhoneDetailsList.get(i);

			verify(userIPhoneDetailsServiceMock, times(1)).markUserIPhoneDetailsAsProcessed(userIPhoneDetails, nearestLatestPublishTimeMillis);
		}

	}
}