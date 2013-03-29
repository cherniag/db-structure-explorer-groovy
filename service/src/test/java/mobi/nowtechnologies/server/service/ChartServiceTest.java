package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import mobi.nowtechnologies.server.persistence.domain.*;
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
	public void testGetChartsByCommunity_NullName_Success()
		throws Exception {
		String communityUrl = "chartsnow";
		String communityName = null;
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));

		List<Chart> result = fixture.getChartsByCommunity(communityUrl, communityName);

		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(1)).getByCommunityURL(anyString());
	}

	@Test
	public void testGetChartsByCommunity_NullUrl_Success()
		throws Exception {
		String communityUrl = null;
		String communityName = "chartsnow";
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));
		
		List<Chart> result = fixture.getChartsByCommunity(communityUrl, communityName);

		assertNotNull(result);
		assertEquals(1, result.size());
		
		verify(mockChartRepository, times(1)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
	}
	
	@Test
	public void testGetChartsByCommunity_NullUrlAndNullName_Success()
		throws Exception {
		String communityUrl = null;
		String communityName = null;
				
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Collections.singletonList(new Chart()));
		when(mockChartRepository.getByCommunityURL(anyString())).thenReturn(Collections.singletonList(new Chart()));

		List<Chart> result = fixture.getChartsByCommunity(communityUrl, communityName);

		assertNotNull(result);
		assertEquals(0, result.size());
		
		verify(mockChartRepository, times(0)).getByCommunityName(anyString());
		verify(mockChartRepository, times(0)).getByCommunityURL(anyString());
	}
	
	@Test
	public void testProcessGetChartCommand_Success()
		throws Exception {
		String communityName = "chartsnow";
		
		Media media = getMediaInstance(1);
		
		Chart basicChart = getChartInstance(1, ChartType.BASIC_CHART);
		Chart topChart = getChartInstance(2, ChartType.HOT_TRACKS);
		Chart otherChart = getChartInstance(3, ChartType.OTHER_CHART);
		
		ChartDetail basicChartDetail = getChartDetailInstance(0, 1, media, basicChart);
		ChartDetail topChartDetail = getChartDetailInstance(0, 2, media, topChart);
		ChartDetail otherChartDetail = getChartDetailInstance(0, 3, media, otherChart);
		
		when(mockChartRepository.getByCommunityName(anyString())).thenReturn(Arrays.asList(basicChart, topChart, otherChart));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)1), anyBoolean())).thenReturn(Arrays.asList(basicChartDetail));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)2), anyBoolean())).thenReturn(Arrays.asList(topChartDetail));
		when(mockChartDetailService.findChartDetailTree(any(User.class), eq((byte)3), anyBoolean())).thenReturn(Arrays.asList(otherChartDetail));
		when(mockMessageSource.getMessage(anyString(), anyString(), any(Object[].class), anyString(), any(Locale.class))).thenReturn("defaultAmazonUrl");
		
		Object[] result = fixture.processGetChartCommand(testUser, communityName, true);

		assertNotNull(result);
		
		PlaylistDto[] playlists = ((ChartDto)result[1]).getPlaylistDtos();
		assertNotNull(playlists);
		assertEquals(3, playlists.length);
		assertEquals(basicChart.getName(), playlists[0].getPlaylistTitle());
		assertEquals(topChart.getName(), playlists[1].getPlaylistTitle());
		assertEquals(otherChart.getName(), playlists[2].getPlaylistTitle());
		assertEquals(basicChart.getSubtitle(), playlists[0].getSubtitle());
		assertEquals(topChart.getSubtitle(), playlists[1].getSubtitle());
		assertEquals(otherChart.getSubtitle(), playlists[2].getSubtitle());
		assertEquals(basicChart.getImageFileName(), playlists[0].getImage());
		assertEquals(topChart.getImageFileName(), playlists[1].getImage());
		assertEquals(otherChart.getImageFileName(), playlists[2].getImage());
		assertEquals(basicChart.getI().byteValue(), playlists[0].getId().byteValue());
		assertEquals(topChart.getI().byteValue(), playlists[1].getId().byteValue());
		assertEquals(otherChart.getI().byteValue(), playlists[2].getId().byteValue());

		ChartDetailDto[] list = ((ChartDto)result[1]).getChartDetailDtos();
		assertNotNull(list);
		assertEquals(3, list.length);
		assertEquals(ChartDetailDto.class, list[0].getClass());
		assertEquals(ChartDetailDto.class, list[1].getClass());
		assertEquals(ChartDetailDto.class, list[2].getClass());
		assertEquals(1, list[0].getPosition());
		assertEquals(42, list[1].getPosition());
		assertEquals(53, list[2].getPosition());
		assertEquals(basicChart.getI().byteValue(), list[0].getPlaylistId().byteValue());
		assertEquals(topChart.getI().byteValue(), list[1].getPlaylistId().byteValue());
		assertEquals(otherChart.getI().byteValue(), list[2].getPlaylistId().byteValue());
		
		verify(mockChartRepository).getByCommunityName(anyString());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)1), anyBoolean());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)2), anyBoolean());
		verify(mockChartDetailService).findChartDetailTree(any(User.class), eq((byte)3), anyBoolean());
	}
	
	@Test
	public void testUpdateChart_Success()
		throws Exception {		
		Chart chart = getChartInstance(1, ChartType.BASIC_CHART);
		MultipartFile imageFile = new MockMultipartFile("file", "1".getBytes());
		
		when(mockChartRepository.updateFields(eq(chart.getI()), eq(chart.getName()), eq(chart.getSubtitle()), eq(chart.getImageFileName()))).thenReturn(1);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		Chart result = fixture.updateChart(chart, imageFile);

		assertNotNull(result);
		assertEquals(chart.getName(), result.getName());
		assertEquals(chart.getSubtitle(), result.getSubtitle());
		assertEquals(chart.getImageFileName(), result.getImageFileName());
		
		verify(mockChartRepository, times(1)).updateFields(eq(chart.getI()), eq(chart.getName()), eq(chart.getSubtitle()), eq(chart.getImageFileName()));
		verify(mockCloudFileService, times(1)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_FileNull_Success()
		throws Exception {		
		Chart chart = getChartInstance(1, ChartType.BASIC_CHART);
		MultipartFile imageFile = new MockMultipartFile("file", "".getBytes());
		
		when(mockChartRepository.updateFields(eq(chart.getI()), eq(chart.getName()), eq(chart.getSubtitle()), eq(chart.getImageFileName()))).thenReturn(1);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		Chart result = fixture.updateChart(chart, imageFile);

		assertNotNull(result);
		assertEquals(chart.getName(), result.getName());
		assertEquals(chart.getSubtitle(), result.getSubtitle());
		assertEquals(chart.getImageFileName(), result.getImageFileName());
		
		verify(mockChartRepository, times(1)).updateFields(eq(chart.getI()), eq(chart.getName()), eq(chart.getSubtitle()), eq(chart.getImageFileName()));
		verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_FileEmpty_Success()
		throws Exception {		
		Chart chart = getChartInstance(1, ChartType.BASIC_CHART);
		MultipartFile imageFile = null;
		
		when(mockChartRepository.updateFields(eq(chart.getI()), eq(chart.getName()), eq(chart.getSubtitle()), eq(chart.getImageFileName()))).thenReturn(1);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		Chart result = fixture.updateChart(chart, imageFile);

		assertNotNull(result);
		assertEquals(chart.getName(), result.getName());
		assertEquals(chart.getSubtitle(), result.getSubtitle());
		assertEquals(chart.getImageFileName(), result.getImageFileName());
		
		verify(mockChartRepository, times(1)).updateFields(eq(chart.getI()), eq(chart.getName()), eq(chart.getSubtitle()), eq(chart.getImageFileName()));
		verify(mockCloudFileService, times(0)).uploadFile(any(MultipartFile.class), anyString());
	}
	
	@Test
	public void testUpdateChart_ChartNull_Failure()
		throws Exception {		
		Chart chart = null;
		MultipartFile imageFile = new MockMultipartFile("file", "1".getBytes());
		
		when(mockChartRepository.updateFields(anyByte(), anyString(), anyString(), anyString())).thenReturn(1);
		when(mockCloudFileService.uploadFile(any(MultipartFile.class), anyString())).thenReturn(true);
		
		Chart result = fixture.updateChart(chart, imageFile);

		assertNull(result);
		
		verify(mockChartRepository, times(0)).updateFields(anyByte(), anyString(), anyString(), anyString());
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
		
		fixture = new ChartService();
		fixture.setChartRepository(mockChartRepository);	
		fixture.setUserService(mockUserService);
		fixture.setMessageSource(mockMessageSource);
		fixture.setChartDetailService(mockChartDetailService);
		fixture.setCloudFileService(mockCloudFileService);
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
	
	private Chart getChartInstance(int i, ChartType chartType) {
		final Chart chart = new Chart();
		chart.setI((byte) i);
		chart.setSubtitle("Subtitle");
		chart.setName("Name");
		chart.setImageFileName("ImageFileName");
		chart.setGenre(new Genre());
		chart.setType(chartType);
		return chart;
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