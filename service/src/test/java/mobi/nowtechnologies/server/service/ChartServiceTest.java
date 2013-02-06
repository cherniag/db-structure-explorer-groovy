package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.enums.ChgPosition;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
		when(mockChartDetailService.findChartDetailTreeAndUpdateDrm(any(User.class), eq((byte)1))).thenReturn(Arrays.asList(basicChartDetail));
		when(mockChartDetailService.findChartDetailTreeAndUpdateDrm(any(User.class), eq((byte)2))).thenReturn(Arrays.asList(topChartDetail));
		when(mockChartDetailService.findChartDetailTreeAndUpdateDrm(any(User.class), eq((byte)3))).thenReturn(Arrays.asList(otherChartDetail));
		when(mockMessageSource.getMessage(anyString(), anyString(), any(Object[].class), anyString(), any(Locale.class))).thenReturn("defaultAmazonUrl");
		
		Object[] result = fixture.processGetChartCommand(testUser, communityName);

		assertNotNull(result);
		
		ChartDetailDto[] list = ((ChartDto)result[1]).getChartDetailDtos();
		assertNotNull(list);
		assertEquals(3, list.length);
		assertEquals(ChartDetailDto.class, list[0].getClass());
		assertEquals(BonusChartDetailDto.class, list[1].getClass());
		assertEquals(BonusChartDetailDto.class, list[2].getClass());
		assertEquals(1, list[0].getPosition());
		assertEquals(42, list[1].getPosition());
		assertEquals(53, list[2].getPosition());
		
		verify(mockChartRepository).getByCommunityName(anyString());
		verify(mockChartDetailService).findChartDetailTreeAndUpdateDrm(any(User.class), eq((byte)1));
		verify(mockChartDetailService).findChartDetailTreeAndUpdateDrm(any(User.class), eq((byte)2));
		verify(mockChartDetailService).findChartDetailTreeAndUpdateDrm(any(User.class), eq((byte)3));
	}

	@Before
	public void setUp()
		throws Exception {
		
		testUser = new User(){
			private static final long serialVersionUID = 1L;

			public AccountCheckDTO toAccountCheckDTO(String rememberMeToken) {
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