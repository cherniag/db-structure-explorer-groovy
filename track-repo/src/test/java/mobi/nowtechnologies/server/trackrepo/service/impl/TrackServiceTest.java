package mobi.nowtechnologies.server.trackrepo.service.impl;

import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommandThread;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.support.ServletContextResource;

import javax.servlet.ServletContext;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TrackServiceImpl.class)
public class TrackServiceTest {
	private static final String TITLE_VALUE = "Test_Title";
	private static final String ARTIST_VALUE = "Test_Artist";
	private static final String INGESTOR_VALUE = "Test_Ingestor";
	private static final String ISRC_VALUE = "APPCAST";
	private static final Long ID_VALUE = 1L;
	private static final Date INGESTION_DATE_VALUE = new Date();

	private static final String ENCODE_SCRIPT_PATH = "bin/scripts/encode.sh";
	private static final String ITUNES_SCRIPT_PATH = "bin/scripts/itunes.sh";
	private static final String NERO_HOME_PATH = "bin/nero/";
	private static final String ENCODE_DIST_PATH = "publish";
	private static final String WORKDIR_PATH = "work/";
	private static final String PRIVATE_KEY_PATH = "uits/key.der";
	private static final String CLASS_PATH = "lib/";

	private TrackServiceImpl service;
	private Track track;
	private ExternalCommandThread command;

    @Mock
    private CloudFileService cloudFileServiceMock;

	@Before
	public void before() throws Exception {
		track = new Track();
		track.setId(ID_VALUE);
		track.setTitle(TITLE_VALUE);
		track.setArtist(ARTIST_VALUE);
		track.setIngestor(INGESTOR_VALUE);
		track.setIsrc(ISRC_VALUE);
		track.setIngestionDate(INGESTION_DATE_VALUE);
		track.setStatus(TrackStatus.NONE);

		command = mock(ExternalCommandThread.class);
		whenNew(ExternalCommandThread.class).withNoArguments().thenReturn(command);
		doNothing().when(command).run();
		
		TrackRepository trackRepository = mock(TrackRepository.class);
		when(trackRepository.find(any(String.class), any(Pageable.class))).thenAnswer(new Answer<List<Track>>() {
			@Override
			public List<Track> answer(InvocationOnMock invocation) throws Throwable {
				String query = (String) invocation.getArguments()[0];
				query = query != null ? query.replaceAll("%", ".*") : "";

				boolean matched = track.getArtist().matches(query) || track.getTitle().matches(query) || track.getIsrc().matches(query);

				return matched ? Collections.singletonList(track) : Collections.<Track> emptyList();
			}
		});
		when(trackRepository.find(any(SearchTrackDto.class), any(Pageable.class))).thenAnswer(new Answer<Page<Track>>() {
			@Override
			public Page<Track> answer(InvocationOnMock invocation) throws Throwable {
				SearchTrackDto searchTrackDto = (SearchTrackDto) invocation.getArguments()[0];
				Pageable page = (Pageable) invocation.getArguments()[1];
				String artist = searchTrackDto.getArtist();
				String title = searchTrackDto.getTitle();
				String isrc = searchTrackDto.getIsrc();
				Date ingestFrom = searchTrackDto.getIngestFrom();
				Date ingestTo = searchTrackDto.getIngestTo();
				String ingester = searchTrackDto.getIngestor();

				boolean matched = (artist != null ? track.getArtist().matches(artist) : true);
				matched = matched && (title != null ? track.getTitle().matches(title) : true);
				matched = matched && (isrc != null ? track.getIsrc().matches(isrc) : true);
				matched = matched && (ingestTo != null ? track.getIngestionDate().before(ingestTo) : true);
				matched = matched && (ingestFrom != null ? track.getIngestionDate().after(ingestFrom) : true);
				matched = matched && (ingester != null ? track.getIngestor().matches(ingester) : true);

				return new PageImpl<Track>(matched ? Collections.singletonList(track) : Collections.<Track> emptyList(), page, 1);
			}
		});
		when(trackRepository.findOneWithCollections(any(Long.class))).thenAnswer(new Answer<Track>() {
			@Override
			public Track answer(InvocationOnMock invocation) throws Throwable {
				Long id = (Long) invocation.getArguments()[0];
				return track.getId().equals(id) ? track : null;
			}
		});
		when(trackRepository.findOne(any(Long.class))).thenAnswer(new Answer<Track>() {
			@Override
			public Track answer(InvocationOnMock invocation) throws Throwable {
				Long id = (Long) invocation.getArguments()[0];
				return track.getId().equals(id) ? track : null;
			}
		});
		when(trackRepository.save(any(Track.class))).thenAnswer(new Answer<Track>() {
			@Override
			public Track answer(InvocationOnMock invocation) throws Throwable {
				return track;
			}
		});

		ServletContext servletContext = new MockServletContext();
		service = spy(new TrackServiceImpl());
        service.setCloudFileService(cloudFileServiceMock);
		service.setWorkDir(new ServletContextResource(servletContext, WORKDIR_PATH));
		service.setPublishDir(new ServletContextResource(servletContext, ENCODE_DIST_PATH));
		service.setEncodeScript(new ServletContextResource(servletContext, ENCODE_SCRIPT_PATH));
		service.setItunesScript(new ServletContextResource(servletContext, ITUNES_SCRIPT_PATH));
		service.setClasspath(new ServletContextResource(servletContext, CLASS_PATH));
		service.setNeroHome(new ServletContextResource(servletContext, NERO_HOME_PATH));
		service.setPrivateKey(new ServletContextResource(servletContext, PRIVATE_KEY_PATH));
		service.setTrackRepository(trackRepository);

		service.init();
	}

	@Test
	public void findTest() {
		Pageable page = new PageRequest(0, 30);
		SearchTrackDto searchTrackDto = new SearchTrackDto();
		searchTrackDto.setArtist(ARTIST_VALUE);
		Page<Track> tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getContent().size(), 1);
		searchTrackDto = new SearchTrackDto();
		searchTrackDto.setTitle(TITLE_VALUE);
		tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getContent().size(), 1);
		searchTrackDto = new SearchTrackDto();
		searchTrackDto.setIsrc(ISRC_VALUE);
		tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getContent().size(), 1);
		searchTrackDto = new SearchTrackDto();
		searchTrackDto.setArtist(ARTIST_VALUE);
		searchTrackDto.setTitle(TITLE_VALUE);
		searchTrackDto.setIsrc(ISRC_VALUE);
		tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getContent().size(), 1);

		Track track = tracks.getContent().get(0);
		assertEquals(track.getId(), ID_VALUE);
		assertEquals(track.getTitle(), TITLE_VALUE);
		assertEquals(track.getArtist(), ARTIST_VALUE);
		assertEquals(track.getIsrc(), ISRC_VALUE);
		assertEquals(track.getIngestor(), INGESTOR_VALUE);
		assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
		assertEquals(track.getStatus(), TrackStatus.NONE);
	}

	@Test
	@Ignore
	public void encodeTest() throws Exception {	
		Track track = service.encode(ID_VALUE, false, true);

		assertNotNull(track);
		assertEquals(track.getId(), ID_VALUE);
		assertEquals(track.getTitle(), TITLE_VALUE);
		assertEquals(track.getArtist(), ARTIST_VALUE);
		assertEquals(track.getIsrc(), ISRC_VALUE);
		assertEquals(track.getIngestor(), INGESTOR_VALUE);
		assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
		assertEquals(track.getStatus(), TrackStatus.ENCODED);
		
		verify(command, times(23)).addParam(anyString());
		verify(command, times(1)).addParam(eq(ID_VALUE.toString()));
		
	}

	@Test
	public void pullTest() {
		track.setStatus(TrackStatus.ENCODED);

		Track track = service.pull(ID_VALUE);
		assertNotNull(track);
		assertEquals(track.getId(), ID_VALUE);
		assertEquals(track.getTitle(), TITLE_VALUE);
		assertEquals(track.getArtist(), ARTIST_VALUE);
		assertEquals(track.getIsrc(), ISRC_VALUE);
		assertEquals(track.getIngestor(), INGESTOR_VALUE);
		assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
		assertEquals(track.getStatus(), TrackStatus.PUBLISHED);
	}

    @Test
    public void testPull_Video_Success() throws BrightcoveException {
        track.setStatus(TrackStatus.ENCODED);
        final AssetFile videoFile = new AssetFile();
        videoFile.setType(AssetFile.FileType.VIDEO);
        videoFile.setPath("somepath");
        track.setFiles(Collections.singleton(videoFile));

		when(cloudFileServiceMock.copyFile(anyString(), anyString(), anyString(), eq(track.getIsrc() + ImageResolution.SIZE_22.getSuffix() + "." + FileType.IMAGE.getExt()))).thenReturn(true);
		when(cloudFileServiceMock.copyFile(anyString(), anyString(), anyString(), eq(track.getIsrc() + ImageResolution.SIZE_21.getSuffix() + "." + FileType.IMAGE.getExt()))).thenReturn(true);
		when(cloudFileServiceMock.copyFile(anyString(), anyString(), anyString(), eq(track.getIsrc() + "." + FileType.MOBILE_AUDIO.getExt()))).thenReturn(true);
		when(cloudFileServiceMock.copyFile(anyString(), anyString(), anyString(), eq(track.getIsrc() + "." + FileType.MOBILE_ENCODED.getExt()))).thenReturn(true);
		doAnswer(new Answer<AssetFile>() {
			@Override
			public AssetFile answer(InvocationOnMock invocationOnMock) throws Throwable {
				videoFile.setExternalId("343434977432");
				return videoFile;
			}
		}).when(service).createVideo(any(Track.class));

        Track track = service.pull(ID_VALUE);
        assertNotNull(track);
        assertEquals(track.getId(), ID_VALUE);
        assertEquals(track.getTitle(), TITLE_VALUE);
        assertEquals(track.getArtist(), ARTIST_VALUE);
        assertEquals(track.getIsrc(), ISRC_VALUE);
        assertEquals(track.getIngestor(), INGESTOR_VALUE);
        assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
        assertEquals(track.getStatus(), TrackStatus.PUBLISHED);
        assertNotNull(videoFile.getExternalId());

        verify(service, times(1)).createVideo(any(Track.class));
		verify(cloudFileServiceMock, times(1)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + ImageResolution.SIZE_22.getSuffix() + "." + FileType.IMAGE.getExt()));
		verify(cloudFileServiceMock, times(1)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + ImageResolution.SIZE_21.getSuffix() + "." + FileType.IMAGE.getExt()));
		verify(cloudFileServiceMock, times(0)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + "." + FileType.MOBILE_AUDIO.getExt()));
		verify(cloudFileServiceMock, times(0)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + "." + FileType.MOBILE_ENCODED.getExt()));

    }
    
    @Test
    @Ignore
    public void testGetAmazonUrl() {
    	service.setSevenDigitalApiKey("7d85yvex6wmu");
    	service.setSevenDigitalApiUrl("http://api.7digital.com/1.2/track/search?q={query}&oauth_consumer_key={key}");
    	service.setRestTemplate(new RestTemplate());
    	String result = service.getAmazonUrl("AEA040800109111111111");
    	assertNotNull(result);
    }
}
