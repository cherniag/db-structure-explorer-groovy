package mobi.nowtechnologies.server.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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
import mobi.nowtechnologies.server.service.MessageService;
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
@PrepareForTest({ NotificationJob.class, CommunityDao.class, Utils.class, Push.class, PushedNotification.class, Vector.class, Pageable.class })
public class NotificationJobTest {

	private NotificationJob notificationJobSpy;
	private UserIPhoneDetailsService userIPhoneDetailsServiceMock;
	private ChartDetailService chartDetailServiceMock;
	private Resource keystoreMock;
	private final boolean production = false;
	private final String password = "";
	private final int numberOfThreads = Integer.MIN_VALUE;
	private String communityName = "";
	private MessageService messageServiceMock;

	@Before
	public void setUp()
			throws Exception {
		notificationJobSpy = spy(new NotificationJob());
		
		chartDetailServiceMock = mock(ChartDetailService.class);
		userIPhoneDetailsServiceMock = mock(UserIPhoneDetailsService.class);
		keystoreMock = mock(Resource.class);
		messageServiceMock = mock(MessageService.class);
		
		notificationJobSpy.setNumberOfThreads(numberOfThreads);
		notificationJobSpy.setPassword(password);
		notificationJobSpy.setProduction(production);
		notificationJobSpy.setChartDetailService(chartDetailServiceMock);
		notificationJobSpy.setUserIPhoneDetailsService(userIPhoneDetailsServiceMock);
		notificationJobSpy.setKeystore(keystoreMock);
		notificationJobSpy.setUserIPhoneDetailsListFetchSize(Integer.MAX_VALUE);
		notificationJobSpy.setMessageService(messageServiceMock);
	}

	@Test
	public void testNotificationJob_Constructor_Success()
			throws Exception {
		NotificationJob notificationJob = new NotificationJob();
		
		assertNotNull(notificationJob);
	}

	@Test
	public void testSetChartDetailService_Success()
			throws Exception {
		ChartDetailService chartDetailService = new ChartDetailService();

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setChartDetailService(chartDetailService);
		assertNotNull(notificationJob.chartDetailService);

	}

	@Test
	public void testSetCommunityName_Success()
			throws Exception {

		String communityName = "";

		Community community = CommunityFactory.createCommunity();

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(community);

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setCommunityName(communityName);

		assertNotNull(notificationJob.community);
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void testSetCommunityName_communityNameIsNull_Fail()
			throws Exception {
		String communityName = null;

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setCommunityName(communityName);

	}

	@Test(expected = java.lang.NullPointerException.class)
	public void testSetCommunityName_NoSuchCommunity_Fail()
			throws Exception {

		String communityName = "";

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(null);

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setCommunityName(communityName);
	}

	@Test
	public void testSetKeystore_Success()
			throws Exception {
		Resource keystore = new ClassPathResource("");

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setKeystore(keystore);

		assertNotNull(notificationJob.keystore);

	}

	@Test
	public void testSetNumberOfThreads_Success()
			throws Exception {
		int numberOfThreads = 1;

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setNumberOfThreads(numberOfThreads);
		assertEquals(numberOfThreads, notificationJob.numberOfThreads);

	}

	@Test
	public void testSetPassword_Success()
			throws Exception {
		String password = "";

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setPassword(password);
		assertEquals(password, notificationJob.password);

	}

	@Test
	public void testSetProduction_Success()
			throws Exception {
		boolean production = true;

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setProduction(production);
		assertEquals(production, notificationJob.production);

	}

	@Test
	public void testSetUserIPhoneDetailsService_Success()
			throws Exception {
		UserIPhoneDetailsService userIPhoneDetailsService = new UserIPhoneDetailsService();

		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setUserIPhoneDetailsService(userIPhoneDetailsService);
		assertEquals(userIPhoneDetailsService, notificationJob.userIPhoneDetailsService);

	}
	
	@Test
	public void testsetUserIPhoneDetailsListFetchSize_Success()
			throws Exception {
		int userIPhoneDetailsListFetchSize = Integer.MAX_VALUE;
		
		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setUserIPhoneDetailsListFetchSize(userIPhoneDetailsListFetchSize );
		
		assertEquals(userIPhoneDetailsListFetchSize, notificationJob.userIPhoneDetailsListFetchSize);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testsetUserIPhoneDetailsListFetchSize_userIPhoneDetailsListFetchSizeIs0_Fail()
			throws Exception {
		int userIPhoneDetailsListFetchSize = 0;
		
		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setUserIPhoneDetailsListFetchSize(userIPhoneDetailsListFetchSize );
		
		assertEquals(userIPhoneDetailsListFetchSize, notificationJob.userIPhoneDetailsListFetchSize);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testsetUserIPhoneDetailsListFetchSize_userIPhoneDetailsListFetchSizeLessThanZero_Fail()
			throws Exception {
		int userIPhoneDetailsListFetchSize = Integer.MIN_VALUE;
		
		NotificationJob notificationJob = new NotificationJob();
		
		notificationJob.setUserIPhoneDetailsListFetchSize(userIPhoneDetailsListFetchSize );
	}
	
	@Test
	public void testProcess_Success()
			throws Exception {
		Community community = CommunityFactory.createCommunity();

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(community);
		
		notificationJobSpy.setCommunityName(communityName);
		
		Long nearestLatestPublishTimeMillis = Long.MIN_VALUE;

		final int userIPhoneDetailsListSize = 4;
		List<UserIPhoneDetails> userIPhoneDetailsList = UserIPhoneDetailsFactory.createUserIPhoneDetailsList(userIPhoneDetailsListSize);

		when(userIPhoneDetailsServiceMock.getUserIPhoneDetailsListForPushNotification(eq(community), eq(nearestLatestPublishTimeMillis), any(Pageable.class))).thenReturn(userIPhoneDetailsList);

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

		notificationJobSpy.proccess(nearestLatestPublishTimeMillis, pushedNotifications);

		verify(userIPhoneDetailsServiceMock, times(1)).getUserIPhoneDetailsListForPushNotification(eq(community), eq(nearestLatestPublishTimeMillis), any(Pageable.class));

		for (int i = 0; i < successfulPushedNotificationsSize; i++) {
			final UserIPhoneDetails userIPhoneDetails = userIPhoneDetailsList.get(i);

			verify(userIPhoneDetailsServiceMock, times(1)).markUserIPhoneDetailsAsProcessed(userIPhoneDetails, nearestLatestPublishTimeMillis);
		}

	}
	
	@Test(expected=NullPointerException.class)
	public void testProcess_nearestLatestPublishTimeMillisIsNull_Failure()
			throws Exception {		
		Long nearestLatestPublishTimeMillis = null;
		List<PushedNotification> pushedNotifications = Collections.<PushedNotification> emptyList();

		notificationJobSpy.proccess(nearestLatestPublishTimeMillis, pushedNotifications);
	}

	@Test
	public void testExecute_Success()
			throws Exception {
		Community community = CommunityFactory.createCommunity();

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(community);
		
		notificationJobSpy.setCommunityName(communityName);

		long epochMillis = Long.MAX_VALUE;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Long nearestLatestPublishTimeMillis = Long.MIN_VALUE;

		doReturn(nearestLatestPublishTimeMillis).when(notificationJobSpy).findNearestLatestMaxPublishTimeMillis(epochMillis);

		List<PushedNotification> pushedNotifications = Collections.<PushedNotification> emptyList();
		
		doReturn(pushedNotifications).when(notificationJobSpy).proccess(nearestLatestPublishTimeMillis, pushedNotifications);
		
		notificationJobSpy.execute();

		verify(notificationJobSpy, times(1)).findNearestLatestMaxPublishTimeMillis(epochMillis);
		verify(notificationJobSpy, times(1)).proccess(nearestLatestPublishTimeMillis, pushedNotifications);

	}
	
	@Test
	public void testExecute_nearestLatestPublishTimeMillisIsNull_Success()
			throws Exception {
		Community community = CommunityFactory.createCommunity();

		mockStatic(CommunityDao.class);
		when(CommunityDao.getCommunity(communityName)).thenReturn(community);
		
		notificationJobSpy.setCommunityName(communityName);

		long epochMillis = Long.MAX_VALUE;

		mockStatic(Utils.class);
		when(Utils.getEpochMillis()).thenReturn(epochMillis);

		Long nearestLatestPublishTimeMillis = null;

		doReturn(nearestLatestPublishTimeMillis).when(notificationJobSpy).findNearestLatestMaxPublishTimeMillis(epochMillis);

		List<PushedNotification> pushedNotifications = Collections.<PushedNotification> emptyList();
		
		doReturn(pushedNotifications).when(notificationJobSpy).proccess(nearestLatestPublishTimeMillis, pushedNotifications);
		
		notificationJobSpy.execute();

		verify(notificationJobSpy, times(1)).findNearestLatestMaxPublishTimeMillis(epochMillis);
		verify(notificationJobSpy, times(0)).proccess(nearestLatestPublishTimeMillis, pushedNotifications);

	}
	
	@Test
	public void testFindNearestLatestMaxPublishTimeMillis_Success(){
		long epochMillis = Long.MAX_VALUE;
		
		Long nearestLatestChartPublishTimeMillis = Long.MAX_VALUE;
		Long nearestLatesNewsPublishTimeMillis = Long.MIN_VALUE;
		
		when(chartDetailServiceMock.findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis))).thenReturn(nearestLatestChartPublishTimeMillis);
		when(messageServiceMock.findNearestLatestPublishDate(any(Community.class), eq(epochMillis))).thenReturn(nearestLatesNewsPublishTimeMillis);
		
		Long nearestLatestMaxPublishTimeMillis = notificationJobSpy.findNearestLatestMaxPublishTimeMillis(epochMillis);
		
		assertNotNull(nearestLatestMaxPublishTimeMillis);
		assertEquals(nearestLatestChartPublishTimeMillis, nearestLatestMaxPublishTimeMillis);
		
		verify(chartDetailServiceMock, times(1)).findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis));
		verify(messageServiceMock, times(1)).findNearestLatestPublishDate(any(Community.class), eq(epochMillis));
		
	}
	
	@Test
	public void testFindNearestLatestMaxPublishTimeMillis_nearestLatestChartPublishTimeMillisIsNull_Success(){
		long epochMillis = Long.MAX_VALUE;
		
		Long nearestLatestChartPublishTimeMillis = null;
		Long nearestLatesNewsPublishTimeMillis = Long.MIN_VALUE;
		
		when(chartDetailServiceMock.findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis))).thenReturn(nearestLatestChartPublishTimeMillis);
		when(messageServiceMock.findNearestLatestPublishDate(any(Community.class), eq(epochMillis))).thenReturn(nearestLatesNewsPublishTimeMillis);
		
		Long nearestLatestMaxPublishTimeMillis = notificationJobSpy.findNearestLatestMaxPublishTimeMillis(epochMillis);
		
		assertNotNull(nearestLatestMaxPublishTimeMillis);
		assertEquals(nearestLatesNewsPublishTimeMillis, nearestLatestMaxPublishTimeMillis);
		
		verify(chartDetailServiceMock, times(1)).findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis));
		verify(messageServiceMock, times(1)).findNearestLatestPublishDate(any(Community.class), eq(epochMillis));
		
	}
	
	@Test
	public void testFindNearestLatestMaxPublishTimeMillis_nearestLatesNewsPublishTimeMillisIsNull_Success(){
		long epochMillis = Long.MAX_VALUE;
		
		Long nearestLatestChartPublishTimeMillis = Long.MIN_VALUE;
		Long nearestLatesNewsPublishTimeMillis = null;
		
		when(chartDetailServiceMock.findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis))).thenReturn(nearestLatestChartPublishTimeMillis);
		when(messageServiceMock.findNearestLatestPublishDate(any(Community.class), eq(epochMillis))).thenReturn(nearestLatesNewsPublishTimeMillis);
		
		Long nearestLatestMaxPublishTimeMillis = notificationJobSpy.findNearestLatestMaxPublishTimeMillis(epochMillis);
		
		assertNotNull(nearestLatestMaxPublishTimeMillis);
		assertEquals(nearestLatestChartPublishTimeMillis, nearestLatestMaxPublishTimeMillis);
		
		verify(chartDetailServiceMock, times(1)).findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis));
		verify(messageServiceMock, times(1)).findNearestLatestPublishDate(any(Community.class), eq(epochMillis));
		
	}
	
	@Test
	public void testFindNearestLatestMaxPublishTimeMillis_nearestLatesNewsPublishTimeMillisAndNearestLatestChartPublishTimeMillisAreNull_Success(){
		long epochMillis = Long.MAX_VALUE;
		
		Long nearestLatestChartPublishTimeMillis = null;
		Long nearestLatesNewsPublishTimeMillis = null;
		
		when(chartDetailServiceMock.findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis))).thenReturn(nearestLatestChartPublishTimeMillis);
		when(messageServiceMock.findNearestLatestPublishDate(any(Community.class), eq(epochMillis))).thenReturn(nearestLatesNewsPublishTimeMillis);
		
		Long nearestLatestMaxPublishTimeMillis = notificationJobSpy.findNearestLatestMaxPublishTimeMillis(epochMillis);
		
		assertNull(nearestLatestMaxPublishTimeMillis);	
		
		verify(chartDetailServiceMock, times(1)).findNearestLatestPublishTimeMillis(any(Community.class), eq(epochMillis));
		verify(messageServiceMock, times(1)).findNearestLatestPublishDate(any(Community.class), eq(epochMillis));
	}
}