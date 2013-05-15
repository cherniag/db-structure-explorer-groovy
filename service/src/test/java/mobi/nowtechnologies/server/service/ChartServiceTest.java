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

	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullName_Success()
		throws Exception {
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		String communityUrl = "chartsnow";
		String communityName = null;
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName);

		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(1)).getByCommunityURL(anyString());
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullUrl_Success()
		throws Exception {
		List<ChartDetail> chartDetails = Collections.singletonList(ChartDetailFactory.createChartDetail());
		String communityUrl = null;
		String communityName = "chartsnow";
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName);

		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(1)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetChartsByCommunity_NullUrlAndNullName_Success()
		throws Exception {
		List<ChartDetail> chartDetails = Collections.EMPTY_LIST;
		String communityUrl = null;
		String communityName = null;
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		doReturn(chartDetails).when(fixture).getChartDetails(any(List.class), any(Date.class));
		
		List<ChartDetail> result = fixture.getChartsByCommunity(communityUrl, communityName);

		assertNotNull(result);
		assertEquals(0, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
		verify(fixture, times(1)).getChartDetails(any(List.class), any(Date.class));
	}
	
	@Test
	public void testProcessGetChartCommand_Success()
		throws Exception {
		String communityName = "chartsnow";
		
		Media media = getMediaInstance(1);
		
		ChartDetail basicChart = ChartDetailFactory.createChartDetail();
		basicChart.getChart().setType(ChartType.BASIC_CHART);
		basicChart.getChart().setI((byte)1);		
		ChartDetail topChart = ChartDetailFactory.createChartDetail();
		topChart.getChart().setType(ChartType.HOT_TRACKS);
		topChart.getChart().setI((byte)2);
		ChartDetail otherChart = ChartDetailFactory.createChartDetail();
		otherChart.getChart().setType(ChartType.OTHER_CHART);
		otherChart.getChart().setI((byte)3);
		
		ChartDetail basicChartDetail = getChartDetailInstance(0, 1, media, basicChart.getChart());
		ChartDetail topChartDetail = getChartDetailInstance(0, 2, media, topChart.getChart());
		ChartDetail otherChartDetail = getChartDetailInstance(0, 3, media, otherChart.getChart());
		
		doReturn(Arrays.asList(basicChart, topChart, otherChart)).when(fixture).getChartsByCommunity(eq((String)null), anyString());
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)1), anyBoolean())).thenReturn(Arrays.asList(basicChartDetail));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)2), anyBoolean())).thenReturn(Arrays.asList(topChartDetail));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)3), anyBoolean())).thenReturn(Arrays.asList(otherChartDetail));
		when(mockMessageSource.getMessage(anyString(), anyString(), any(Object[].class), anyString(), any(Locale.class))).thenReturn("defaultAmazonUrl");
		
		Object[] result = fixture.processGetChartCommand(testUser, communityName, true);

		assertNotNull(result);
		
		PlaylistDto[] playlists = ((ChartDto)result[1]).getPlaylistDtos();
		assertNotNull(playlists);
		assertEquals(3, playlists.length);
		assertEquals(basicChart.getTitle(), playlists[0].getPlaylistTitle());
		assertEquals(topChart.getTitle(), playlists[1].getPlaylistTitle());
		assertEquals(otherChart.getTitle(), playlists[2].getPlaylistTitle());
		assertEquals(basicChart.getSubtitle(), playlists[0].getSubtitle());
		assertEquals(topChart.getSubtitle(), playlists[1].getSubtitle());
		assertEquals(otherChart.getSubtitle(), playlists[2].getSubtitle());
		assertEquals(basicChart.getImageFileName(), playlists[0].getImage());
		assertEquals(topChart.getImageFileName(), playlists[1].getImage());
		assertEquals(otherChart.getImageFileName(), playlists[2].getImage());
		assertEquals(basicChart.getChart().getI().byteValue(), playlists[0].getId().byteValue());
		assertEquals(topChart.getChart().getI().byteValue(), playlists[1].getId().byteValue());
		assertEquals(otherChart.getChart().getI().byteValue(), playlists[2].getId().byteValue());

		ChartDetailDto[] list = ((ChartDto)result[1]).getChartDetailDtos();
		assertNotNull(list);
		assertEquals(3, list.length);
		assertEquals(ChartDetailDto.class, list[0].getClass());
		assertEquals(ChartDetailDto.class, list[1].getClass());
		assertEquals(ChartDetailDto.class, list[2].getClass());
		assertEquals(1, list[0].getPosition());
		assertEquals(42, list[1].getPosition());
		assertEquals(53, list[2].getPosition());
		assertEquals(basicChart.getChart().getI().byteValue(), list[0].getPlaylistId().byteValue());
		assertEquals(topChart.getChart().getI().byteValue(), list[1].getPlaylistId().byteValue());
		assertEquals(otherChart.getChart().getI().byteValue(), list[2].getPlaylistId().byteValue());
		
		verify(fixture).getChartsByCommunity(eq((String)null), anyString());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)1), anyBoolean());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)2), anyBoolean());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)3), anyBoolean());
	}
	
	@Test
	public void testUpdateChart_Success()
		throws Exception {		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		MultipartFile imageFile = new MockMultipartFile("file", "1".getBytes());
		
		when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		ChartDetail result = fixture.updateChart(chartDetail, imageFile);

		assertNotNull(result);
		assertEquals(chartDetail.getTitle(), result.getTitle());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
		
		verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
		verify(mockCloudFileService, times(1)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_FileNull_Success()
		throws Exception {		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		MultipartFile imageFile = new MockMultipartFile("file", "".getBytes());
		
		when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		ChartDetail result = fixture.updateChart(chartDetail, imageFile);

		assertNotNull(result);
		assertEquals(chartDetail.getTitle(), result.getTitle());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
		
		verify(mockChartDetailRepository, times(1)).save(eq(chartDetail));
		verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_FileEmpty_Success()
		throws Exception {		
		ChartDetail chartDetail = ChartDetailFactory.createChartDetail();
		MultipartFile imageFile = null;
		
		when(mockChartDetailRepository.save(eq(chartDetail))).thenReturn(chartDetail);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		ChartDetail result = fixture.updateChart(chartDetail, imageFile);

		assertNotNull(result);
		assertEquals(chartDetail.getTitle(), result.getTitle());
		assertEquals(chartDetail.getSubtitle(), result.getSubtitle());
		assertEquals(chartDetail.getImageFileName(), result.getImageFileName());
		
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