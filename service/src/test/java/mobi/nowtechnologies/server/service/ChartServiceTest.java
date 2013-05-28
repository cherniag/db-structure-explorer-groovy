package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.enums.ChartType;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RunWith(MockitoJUnitRunner.class)
public class ChartServiceTest {

	private ChartService fixture;
	
	@Mock
	private ChartRepository mockChartRepository;

	@Mock
	private ChartDetailRepository mockChartDetailRepository;
	
	@Mock
	private UserService mockUserService;
	
	@Mock
	private CommunityResourceBundleMessageSource mockMessageSource;
	
	@Mock
	private ChartDetailService mockChartDetailService;
	
	@Mock
	private CloudFileService mockCloudFileService;
	
	//test data
	private User testUser;

	@Test
	public void testSelectChartByType_NotNullChartNotNullUserNotNullSelectedCharts_Success()
			throws Exception {
		List<Chart> charts = Arrays.asList(ChartFactory.createChart(), ChartFactory.createChart());
		charts.get(1).setType(ChartType.OTHER_CHART);
		
		Chart selectedChart = ChartFactory.createChart();
		selectedChart.setI((byte)2);
		User user = UserFactory.createUser();
		user.setSelectedCharts(charts);
		
		when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(selectedChart);
		when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(user);
		when(mockUserService.updateUser(eq(user))).thenReturn(user);
		
		User result = fixture.selectChartByType(user.getId(), selectedChart.getI().intValue());
		
		assertNotNull(result);
		assertEquals(user.getId(), result.getId());
		assertEquals(2, result.getSelectedCharts().size());
		assertEquals(selectedChart, result.getSelectedCharts().get(1));
		
		verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
		verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
		verify(mockUserService, times(1)).updateUser(eq(user));
	}
	
	@Test
	public void testSelectChartByType_NotNullChartNotNullUserNullSelectedCharts_Success()
			throws Exception {
		Chart selectedChart = ChartFactory.createChart();
		selectedChart.setI((byte)2);
		User user = UserFactory.createUser();
		user.setSelectedCharts(null);
		
		when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(selectedChart);
		when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(user);
		when(mockUserService.updateUser(eq(user))).thenReturn(user);
		
		User result = fixture.selectChartByType(user.getId(), selectedChart.getI().intValue());
		
		assertNotNull(result);
		assertEquals(user.getId(), result.getId());
		assertEquals(1, result.getSelectedCharts().size());
		assertEquals(selectedChart, result.getSelectedCharts().get(0));
		
		verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
		verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
		verify(mockUserService, times(1)).updateUser(eq(user));
	}
	
	@Test
	public void testSelectChartByType_NullChartNotNullUser_Success()
			throws Exception {
		List<Chart> charts = Arrays.asList(ChartFactory.createChart(), ChartFactory.createChart());
		charts.get(1).setType(ChartType.OTHER_CHART);
		
		Chart selectedChart = ChartFactory.createChart();
		selectedChart.setI((byte)2);
		User user = UserFactory.createUser();
		user.setSelectedCharts(charts);
		
		when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(null);
		when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(user);
		when(mockUserService.updateUser(eq(user))).thenReturn(user);
		
		User result = fixture.selectChartByType(user.getId(), selectedChart.getI().intValue());
		
		assertNotNull(result);
		assertEquals(user.getId(), result.getId());
		assertEquals(2, result.getSelectedCharts().size());
		assertEquals(charts.get(1), result.getSelectedCharts().get(1));
		
		verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
		verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
		verify(mockUserService, times(0)).updateUser(eq(user));
	}
	
	@Test
	public void testSelectChartByType_NotNullChartNullUser_Success()
			throws Exception {
		List<Chart> charts = Arrays.asList(ChartFactory.createChart(), ChartFactory.createChart());
		charts.get(1).setType(ChartType.OTHER_CHART);
		
		Chart selectedChart = ChartFactory.createChart();
		selectedChart.setI((byte)2);
		User user = UserFactory.createUser();
		user.setSelectedCharts(charts);
		
		when(mockChartRepository.findOne(eq(selectedChart.getI()))).thenReturn(selectedChart);
		when(mockUserService.getUserWithSelectedCharts(eq(user.getId()))).thenReturn(null);
		when(mockUserService.updateUser(eq(user))).thenReturn(user);
		
		User result = fixture.selectChartByType(user.getId(), selectedChart.getI().intValue());
		
		assertNull(result);
		
		verify(mockChartRepository, times(1)).findOne(eq(selectedChart.getI()));
		verify(mockUserService, times(1)).getUserWithSelectedCharts(eq(user.getId()));
		verify(mockUserService, times(0)).updateUser(eq(user));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullNameNotNullType_Success()
			throws Exception {
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		String communityUrl = "chartsnow";
		String communityName = null;
		ChartType chartType = ChartType.OTHER_CHART;
		
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURLAndChartType(anyString(), any(ChartType.class))).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class), eq(false));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName, chartType);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
		verify(mockChartRepository, times(1)).getByCommunityURLAndChartType(anyString(), any(ChartType.class));
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullNameNullType_Success()
		throws Exception {
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		String communityUrl = "chartsnow";
		String communityName = null;
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class), eq(false));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName, null);

		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(1)).getByCommunityURL(anyString());
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullUrlNotNullType_Success()
			throws Exception {
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		String communityUrl = null;
		String communityName = "chartsnow";
		ChartType chartType = ChartType.OTHER_CHART;
		
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityNameAndChartType(anyString(), any(ChartType.class))).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class), eq(false));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName, chartType);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
		verify(mockChartRepository, times(1)).getByCommunityNameAndChartType(anyString(), any(ChartType.class));
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullUrlNullType_Success()
		throws Exception {
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		String communityUrl = null;
		String communityName = "chartsnow";
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class), eq(false));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName, null);

		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(1)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullUrlAndNullNameNullType_Success()
		throws Exception {
		List<ChartDetail> chartDetails = Collections.EMPTY_LIST;
		String communityUrl = null;
		String communityName = null;
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class), eq(false));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName, null);

		assertNotNull(result);
		assertEquals(0, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class), eq(false));
	}
	
	@Test
	public void testProcessGetChartCommand_Success()
		throws Exception {
		String communityName = "chartsnow";
		
		Media media = getMediaInstance(1);
		
		ChartDetail basicChart = ChartDetailFactory.createChartDetail();
		basicChart.getChart().setType(ChartType.BASIC_CHART);
		basicChart.getChart().setI((byte)1);	
		basicChart.setDefaultChart(true);
		ChartDetail basicChart1 = ChartDetailFactory.createChartDetail();
		basicChart1.getChart().setType(ChartType.BASIC_CHART);
		basicChart1.getChart().setI((byte)5);		
		ChartDetail topChart = ChartDetailFactory.createChartDetail();
		topChart.getChart().setType(ChartType.HOT_TRACKS);
		topChart.getChart().setI((byte)2);
		ChartDetail otherChart1 = ChartDetailFactory.createChartDetail();
		otherChart1.getChart().setType(ChartType.OTHER_CHART);
		otherChart1.getChart().setI((byte)3);
		otherChart1.setDefaultChart(true);
		ChartDetail otherChart2 = ChartDetailFactory.createChartDetail();
		otherChart2.getChart().setType(ChartType.OTHER_CHART);
		otherChart2.getChart().setI((byte)4);
		
		testUser.setSelectedCharts(Arrays.asList(otherChart2.getChart(), basicChart1.getChart()));
		
		ChartDetail basicChartDetail = getChartDetailInstance(0, 1, media, basicChart.getChart());
		ChartDetail basicChartDetail1 = getChartDetailInstance(0, 1, media, basicChart1.getChart());
		ChartDetail topChartDetail = getChartDetailInstance(0, 2, media, topChart.getChart());
		ChartDetail otherChartDetail1 = getChartDetailInstance(0, 3, media, otherChart1.getChart());
		ChartDetail otherChartDetail2 = getChartDetailInstance(0, 3, media, otherChart2.getChart());
		
		doReturn(Arrays.asList(basicChart, basicChart1, topChart, otherChart2, otherChart1)).when(fixture).getChartsByCommunity(eq((String)null), anyString(), any(ChartType.class));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)1), anyBoolean())).thenReturn(Arrays.asList(basicChartDetail));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)2), anyBoolean())).thenReturn(Arrays.asList(topChartDetail));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)3), anyBoolean())).thenReturn(Arrays.asList(otherChartDetail1));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)4), anyBoolean())).thenReturn(Arrays.asList(otherChartDetail2));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)5), anyBoolean())).thenReturn(Arrays.asList(basicChartDetail1));
		when(mockMessageSource.getMessage(anyString(), anyString(), any(Object[].class), anyString(), any(Locale.class))).thenReturn("defaultAmazonUrl");
		
		Object[] result = fixture.processGetChartCommand(testUser, communityName, true);

		assertNotNull(result);
		
		PlaylistDto[] playlists = ((ChartDto)result[1]).getPlaylistDtos();
		assertNotNull(playlists);
		assertEquals(3, playlists.length);
		assertEquals(basicChart1.getTitle(), playlists[0].getPlaylistTitle());
		assertEquals(topChart.getTitle(), playlists[1].getPlaylistTitle());
		assertEquals(otherChart2.getTitle(), playlists[2].getPlaylistTitle());
		assertEquals(basicChart1.getSubtitle(), playlists[0].getSubtitle());
		assertEquals(topChart.getSubtitle(), playlists[1].getSubtitle());
		assertEquals(otherChart2.getSubtitle(), playlists[2].getSubtitle());
		assertEquals(basicChart1.getImageFileName(), playlists[0].getImage());
		assertEquals(topChart.getImageFileName(), playlists[1].getImage());
		assertEquals(otherChart2.getImageFileName(), playlists[2].getImage());
		assertEquals(basicChart1.getChart().getI().byteValue(), playlists[0].getId().byteValue());
		assertEquals(topChart.getChart().getI().byteValue(), playlists[1].getId().byteValue());
		assertEquals(otherChart2.getChart().getI().byteValue(), playlists[2].getId().byteValue());

		ChartDetailDto[] list = ((ChartDto)result[1]).getChartDetailDtos();
		assertNotNull(list);
		assertEquals(3, list.length);
		assertEquals(ChartDetailDto.class, list[0].getClass());
		assertEquals(ChartDetailDto.class, list[1].getClass());
		assertEquals(ChartDetailDto.class, list[2].getClass());
		assertEquals(1, list[0].getPosition());
		assertEquals(42, list[1].getPosition());
		assertEquals(53, list[2].getPosition());
		assertEquals(basicChart1.getChart().getI().byteValue(), list[0].getPlaylistId().byteValue());
		assertEquals(topChart.getChart().getI().byteValue(), list[1].getPlaylistId().byteValue());
		assertEquals(otherChart2.getChart().getI().byteValue(), list[2].getPlaylistId().byteValue());
		
		verify(fixture).getChartsByCommunity(eq((String)null), anyString(), any(ChartType.class));
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)5), anyBoolean());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)2), anyBoolean());
		verify(mockChartDetailService, times(0)).findChartDetailTree(any(User.class), eq((byte)3), anyBoolean());
		verify(mockChartDetailService, times(0)).findChartDetailTree(any(User.class), eq((byte)1), anyBoolean());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)4), anyBoolean());
	}
	
	@Test
	public void testUpdateChart_Success()
		throws Exception {		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
		chartDetail1.setI(chartDetail.getI());
		chartDetail1.setVersion(5);
		MultipartFile imageFile = new MockMultipartFile("file", "1".getBytes());
		
		when(mockChartDetailRepository.findOne(eq(chartDetail.getI()))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		ChartDetail result = fixture.updateChart(chartDetail, imageFile);

		assertNotNull(result);
		assertEquals(chartDetail1.getVersion(), result.getVersion());
		assertEquals(chartDetail.getTitle(), result.getTitle());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
		
		verify(mockChartDetailRepository, times(1)).findOne(eq(chartDetail.getI()));
		verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
		verify(mockCloudFileService, times(1)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_FileNull_Success()
		throws Exception {		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		chartDetail.setI(null);
		MultipartFile imageFile = new MockMultipartFile("file", "".getBytes());
		
		when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		ChartDetail result = fixture.updateChart(chartDetail, imageFile);

		assertNotNull(result);
		assertEquals(chartDetail.getTitle(), result.getTitle());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
		
		verify(mockChartDetailRepository, times(0)).findOne(eq(chartDetail.getI()));
		verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
		verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_FileEmpty_Success()
		throws Exception {		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		chartDetail.setI(null);
		MultipartFile imageFile = null;
		
		when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		ChartDetail result = fixture.updateChart(chartDetail, imageFile);

		assertNotNull(result);
		assertEquals(chartDetail.getTitle(), result.getTitle());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
		
		verify(mockChartDetailRepository, times(0)).findOne(eq(chartDetail.getI()));
		verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
		verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_ChartNull_Failure()
		throws Exception {		
		ChartDetail chartDetail = null;
		MultipartFile imageFile = new MockMultipartFile("file", "1".getBytes());
		
		when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		ChartDetail result = fixture.updateChart(chartDetail, imageFile);

		assertNull(result);
		
		verify(mockChartDetailRepository, times(0)).save(eq(chartDetail));
		verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testGetChartDetails_IsChoosedChartDetails_Success()
			throws Exception {		
		ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
		chartDetail1.getChart().setI((byte)1);
		ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
		chartDetail2.getChart().setI((byte)2);
		ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
		chartDetail3.getChart().setI((byte)3);
		List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
		Long nearestLatestDate = new Date().getTime()-10000;
		Date choosenDate = new Date();
		
		when(mockChartDetailRepository.findNearestLatestPublishDate(anyLong(), anyByte())).thenReturn(nearestLatestDate);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate))).thenReturn(chartDetail2);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate))).thenReturn(chartDetail3);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()))).thenReturn(chartDetail2);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()))).thenReturn(chartDetail3);
		
		List<ChartDetail> result = fixture.getChartDetails(charts, choosenDate, false);
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(chartDetail1.getI(), result.get(0).getI());
		assertEquals(chartDetail2.getI(), result.get(1).getI());
		assertEquals(chartDetail3.getI(), result.get(2).getI());
		assertEquals(chartDetail1.getTitle(), result.get(0).getTitle());
		assertEquals(chartDetail2.getTitle(), result.get(1).getTitle());
		assertEquals(chartDetail3.getTitle(), result.get(2).getTitle());
		
		verify(mockChartDetailRepository, times(0)).findNearestLatestPublishDate(anyLong(), anyByte());
		verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()));
	}
	
	@Test
	public void testGetChartDetails_CloneIsLatestDetailsNotChoosedChartDetails_Success()
			throws Exception {		
		ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
		chartDetail1.getChart().setI((byte)1);
		ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
		chartDetail2.getChart().setI((byte)2);
		ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
		chartDetail3.getChart().setI((byte)3);
		List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
		Long nearestLatestDate = new Date().getTime()-100000;
		Date choosenDate = new Date();
		
		when(mockChartDetailRepository.findNearestLatestChartPublishDate(anyLong(), anyByte())).thenReturn(nearestLatestDate);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate))).thenReturn(chartDetail2);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate))).thenReturn(chartDetail3);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()))).thenReturn(null);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()))).thenReturn(null);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()))).thenReturn(null);
		
		List<ChartDetail> result = fixture.getChartDetails(charts, choosenDate, true);
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(null, result.get(0).getI());
		assertEquals(null, result.get(1).getI());
		assertEquals(null, result.get(2).getI());
		assertEquals(chartDetail1.getTitle(), result.get(0).getTitle());
		assertEquals(chartDetail2.getTitle(), result.get(1).getTitle());
		assertEquals(chartDetail3.getTitle(), result.get(2).getTitle());
		
		verify(mockChartDetailRepository, times(3)).findNearestLatestChartPublishDate(anyLong(), anyByte());
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()));
	}
	
	@Test
	public void testGetChartDetails_NotCloneIsLatestDetailsNotChoosedChartDetails_Success()
			throws Exception {		
		ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
		chartDetail1.getChart().setI((byte)1);
		ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
		chartDetail2.getChart().setI((byte)2);
		ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
		chartDetail3.getChart().setI((byte)3);
		List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
		Long nearestLatestDate = new Date().getTime()-100000;
		Date choosenDate = new Date();
		
		when(mockChartDetailRepository.findNearestLatestChartPublishDate(anyLong(), anyByte())).thenReturn(nearestLatestDate);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate))).thenReturn(chartDetail2);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate))).thenReturn(chartDetail3);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()))).thenReturn(null);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()))).thenReturn(null);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()))).thenReturn(null);
		
		List<ChartDetail> result = fixture.getChartDetails(charts, choosenDate, false);
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(chartDetail1.getI(), result.get(0).getI());
		assertEquals(chartDetail2.getI(), result.get(1).getI());
		assertEquals(chartDetail3.getI(), result.get(2).getI());
		assertEquals(chartDetail1.getTitle(), result.get(0).getTitle());
		assertEquals(chartDetail2.getTitle(), result.get(1).getTitle());
		assertEquals(chartDetail3.getTitle(), result.get(2).getTitle());
		
		verify(mockChartDetailRepository, times(3)).findNearestLatestChartPublishDate(anyLong(), anyByte());
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()));
	}
	
	@Test
	public void testGetChartDetails_NotLatestDetailsNotChoosedChartDetails_Success()
			throws Exception {		
		ChartDetail chartDetail1 = ChartDetailFactory.createChartDetail();
		chartDetail1.getChart().setI((byte)1);
		ChartDetail chartDetail2 = ChartDetailFactory.createChartDetail();
		chartDetail2.getChart().setI((byte)2);
		ChartDetail chartDetail3 = ChartDetailFactory.createChartDetail();
		chartDetail3.getChart().setI((byte)3);
		List<Chart> charts = Arrays.asList(chartDetail1.getChart(), chartDetail2.getChart(), chartDetail3.getChart());
		Long nearestLatestDate = null;
		Date choosenDate = new Date();
		
		when(mockChartDetailRepository.findNearestLatestChartPublishDate(anyLong(), anyByte())).thenReturn(nearestLatestDate);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate))).thenReturn(chartDetail1);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()))).thenReturn(null);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()))).thenReturn(null);
		when(mockChartDetailRepository.findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()))).thenReturn(null);
		
		List<ChartDetail> result = fixture.getChartDetails(charts, choosenDate, false);
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(null, result.get(0).getTitle());
		assertEquals(null, result.get(1).getTitle());
		assertEquals(null, result.get(2).getTitle());
		
		verify(mockChartDetailRepository, times(3)).findNearestLatestChartPublishDate(anyLong(), anyByte());
		verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(0)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(nearestLatestDate));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)1), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)2), eq(choosenDate.getTime()));
		verify(mockChartDetailRepository, times(1)).findChartWithDetailsByChartAndPublishTimeMillis(eq((byte)3), eq(choosenDate.getTime()));
	}
	
	

	@Before
	public void setUp()
		throws Exception {
		
		testUser = new User(){
			private static final long serialVersionUID = 1L;

			public AccountCheckDTO toAccountCheckDTO(String rememberMeToken, List<String> appStoreProductIds) {
				return new AccountCheckDTO();
			}
		};
		testUser.setId(1);
		when(mockUserService.findUserTree(anyInt())).thenReturn(testUser);
		
		fixture = spy(new ChartService());
		fixture.setChartRepository(mockChartRepository);	
		fixture.setUserService(mockUserService);
		fixture.setMessageSource(mockMessageSource);
		fixture.setChartDetailService(mockChartDetailService);
		fixture.setCloudFileService(mockCloudFileService);
		fixture.setChartDetailRepository(mockChartDetailRepository);
	}

	@After
	public void tearDown()
		throws Exception {
	}
	
	private ChartDetail getChartDetailInstance(final long publishTimeMillis, int i, Media media, final Chart chart) {
		ChartDetail originalChartDetail = new ChartDetail();
		originalChartDetail.setChannel("channel" + i);
		originalChartDetail.setChart(chart);
		originalChartDetail.setChgPosition(ChgPosition.DOWN);
		originalChartDetail.setI(i);
		originalChartDetail.setInfo("info" + i);
		originalChartDetail.setMedia(media);
		originalChartDetail.setPosition((byte) i);
		originalChartDetail.setPrevPosition((byte) 0);
		originalChartDetail.setPublishTimeMillis(publishTimeMillis);
		originalChartDetail.setVersion(i);
		return originalChartDetail;
	}
	
	private Media getMediaInstance(int i) {
		Genre genre = new Genre();
		
		MediaFile mediaFile = new MediaFile();
		mediaFile.setI(1);
		mediaFile.setFilename("Some filename");
		
		Artist artist = new Artist();
		artist.setI(1);
		artist.setName("Some artist name");
		
		Media media = new Media();
		media.setGenre(genre);
		media.setArtist(artist);
		media.setImageFileSmall(mediaFile);
		media.setAudioFile(mediaFile);
		media.setImageFIleLarge(mediaFile);
		media.setHeaderFile(mediaFile);
		
		Drm drm = new Drm();
		drm.setDrmValue((byte)1);
		drm.setDrmType(new DrmType());
		media.setDrms(Collections.singletonList(drm));
		
		media.setI(i);
		return media;
	}
}