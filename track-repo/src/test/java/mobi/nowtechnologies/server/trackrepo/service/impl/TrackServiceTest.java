package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import mobi.nowtechnologies.server.trackrepo.utils.EncodeManager;
import mobi.nowtechnologies.server.trackrepo.utils.ExternalCommandThread;

import javax.servlet.ServletContext;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.support.ServletContextResource;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import org.springframework.mock.web.MockServletContext;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
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

    private static final String ITUNES_SCRIPT_PATH = "bin/scripts/itunes.sh";

    @InjectMocks
    @Spy
    TrackServiceImpl serviceSpy = new TrackServiceImpl();
    @Mock
    ExternalCommandThread externalCommandThreadMock;
    @Mock
    CloudFileService cloudFileServiceMock;
    @Mock
    TrackRepository trackRepositoryMock;
    @Mock
    EncodeManager encodeManager;
    private Track track;

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

        whenNew(ExternalCommandThread.class).withNoArguments().thenReturn(externalCommandThreadMock);

        when(trackRepositoryMock.find(any(String.class), any(Pageable.class))).thenAnswer(new Answer<List<Track>>() {
            @Override
            public List<Track> answer(InvocationOnMock invocation) throws Throwable {
                String query = (String) invocation.getArguments()[0];
                query = query != null ?
                        query.replaceAll("%", ".*") :
                        "";

                boolean matched = track.getArtist().matches(query) || track.getTitle().matches(query) || track.getIsrc().matches(query);

                return matched ?
                       Collections.singletonList(track) :
                       Collections.<Track>emptyList();
            }
        });
        when(trackRepositoryMock.find(any(SearchTrackDto.class), any(Pageable.class))).thenAnswer(new Answer<Page<Track>>() {
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

                boolean matched = (artist == null || track.getArtist().matches(artist));
                matched = matched && (title == null || track.getTitle().matches(title));
                matched = matched && (isrc == null || track.getIsrc().matches(isrc));
                matched = matched && (ingestTo == null || track.getIngestionDate().before(ingestTo));
                matched = matched && (ingestFrom == null || track.getIngestionDate().after(ingestFrom));
                matched = matched && (ingester == null || track.getIngestor().matches(ingester));

                return new PageImpl<>(matched ?
                                      Collections.singletonList(track) :
                                      Collections.<Track>emptyList(), page, 1);
            }
        });
        when(trackRepositoryMock.findOneWithCollections(any(Long.class))).thenAnswer(new Answer<Track>() {
            @Override
            public Track answer(InvocationOnMock invocation) throws Throwable {
                Long id = (Long) invocation.getArguments()[0];
                return track.getId().equals(id) ?
                       track :
                       null;
            }
        });
        when(trackRepositoryMock.findOne(any(Long.class))).thenAnswer(new Answer<Track>() {
            @Override
            public Track answer(InvocationOnMock invocation) throws Throwable {
                Long id = (Long) invocation.getArguments()[0];
                return track.getId().equals(id) ?
                       track :
                       null;
            }
        });
        when(trackRepositoryMock.save(any(Track.class))).thenAnswer(new Answer<Track>() {
            @Override
            public Track answer(InvocationOnMock invocation) throws Throwable {
                return track;
            }
        });

        ServletContext servletContext = new MockServletContext();

        serviceSpy.setItunesScript(new ServletContextResource(servletContext, ITUNES_SCRIPT_PATH));

        serviceSpy.init();
    }

    @Test
    public void findTest() {
        Pageable page = new PageRequest(0, 30);
        SearchTrackDto searchTrackDto = new SearchTrackDto();
        searchTrackDto.setArtist(ARTIST_VALUE);
        Page<Track> tracks = serviceSpy.find(searchTrackDto, page);
        assertNotNull(tracks);
        assertEquals(tracks.getContent().size(), 1);
        searchTrackDto = new SearchTrackDto();
        searchTrackDto.setTitle(TITLE_VALUE);
        tracks = serviceSpy.find(searchTrackDto, page);
        assertNotNull(tracks);
        assertEquals(tracks.getContent().size(), 1);
        searchTrackDto = new SearchTrackDto();
        searchTrackDto.setIsrc(ISRC_VALUE);
        tracks = serviceSpy.find(searchTrackDto, page);
        assertNotNull(tracks);
        assertEquals(tracks.getContent().size(), 1);
        searchTrackDto = new SearchTrackDto();
        searchTrackDto.setArtist(ARTIST_VALUE);
        searchTrackDto.setTitle(TITLE_VALUE);
        searchTrackDto.setIsrc(ISRC_VALUE);
        tracks = serviceSpy.find(searchTrackDto, page);
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
    public void pullTest() {
        track.setStatus(TrackStatus.ENCODED);

        Track track = serviceSpy.pull(ID_VALUE);
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
        }).when(serviceSpy).createVideo(any(Track.class));

        Track track = serviceSpy.pull(ID_VALUE);
        assertNotNull(track);
        assertEquals(track.getId(), ID_VALUE);
        assertEquals(track.getTitle(), TITLE_VALUE);
        assertEquals(track.getArtist(), ARTIST_VALUE);
        assertEquals(track.getIsrc(), ISRC_VALUE);
        assertEquals(track.getIngestor(), INGESTOR_VALUE);
        assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
        assertEquals(track.getStatus(), TrackStatus.PUBLISHED);
        assertNotNull(videoFile.getExternalId());

        verify(serviceSpy, times(1)).createVideo(any(Track.class));
        verify(cloudFileServiceMock, times(1)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + ImageResolution.SIZE_22.getSuffix() + "." + FileType.IMAGE.getExt()));
        verify(cloudFileServiceMock, times(1)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + ImageResolution.SIZE_21.getSuffix() + "." + FileType.IMAGE.getExt()));
        verify(cloudFileServiceMock, times(0)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + "." + FileType.MOBILE_AUDIO.getExt()));
        verify(cloudFileServiceMock, times(0)).copyFile(anyString(), anyString(), anyString(), eq(track.getUniqueTrackId() + "." + FileType.MOBILE_ENCODED.getExt()));

    }
}
