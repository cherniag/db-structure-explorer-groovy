package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.factory.TrackDtoFactory;
import mobi.nowtechnologies.server.persistence.domain.Artist;
import mobi.nowtechnologies.server.persistence.domain.Genre;
import mobi.nowtechnologies.server.persistence.domain.Media;
import mobi.nowtechnologies.server.persistence.domain.MediaFile;
import mobi.nowtechnologies.server.persistence.repository.ArtistRepository;
import mobi.nowtechnologies.server.persistence.repository.GenreRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaFileRepository;
import mobi.nowtechnologies.server.persistence.repository.MediaRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.Resolution;
import mobi.nowtechnologies.server.trackrepo.TrackRepositoryHttpClientImpl;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.ResourceFileDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.shared.testcases.TestCase;
import mobi.nowtechnologies.shared.testcases.TestCases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.mockito.runners.*;
import org.mockito.stubbing.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

// @author Alexander Kolpakov (akolpakov)
@RunWith(MockitoJUnitRunner.class)
public class TrackRepoServiceImplTest {

    static final Logger LOGGER = LoggerFactory.getLogger(TrackRepoServiceImplTest.class);

    static final String ENCODE_METHOD = "encode";
    static final String PULL_METHOD = "pull";
    static final String FIND_METHOD = "find";

    TestCases<Long, TrackDto> mapTrackById = new TestCases<Long, TrackDto>();
    TestCases<String, PageListDto<TrackDto>> mapTrackByQuery = new TestCases<String, PageListDto<TrackDto>>();
    TestCases<SearchTrackDto, PageListDto<TrackDto>> mapTrackByProperties = new TestCases<SearchTrackDto, PageListDto<TrackDto>>();
    TestCases<String, Media> mapMediaByIsrc = new TestCases<String, Media>();
    TestCases<String, MediaFile> mapMediaFileByName = new TestCases<String, MediaFile>();
    TestCases<String, Artist> mapArtistByName = new TestCases<String, Artist>();
    TestCases<String, Artist> mapArtistByRealName = new TestCases<String, Artist>();
    TestCases<String, Genre> mapGenreByName = new TestCases<String, Genre>();

    @Mock
    TrackRepositoryHttpClientImpl client;
    @Mock
    ArtistRepository artistRepository;
    @Mock
    MediaRepository mediaRepository;
    @Mock
    GenreRepository genreRepository;
    @Mock
    MediaFileRepository mediaFileRepository;
    TrackRepoServiceImpl fixture;

    @Test
    public void checkiTunesURLBeforeCutover() throws Exception {
        long timeStamp = System.currentTimeMillis();
        fixture.setiTunesLinkFormatCutoverTimeMillis(timeStamp + 10000);
        TrackDto trackDto = new TrackDto();
        trackDto.setId(1L);
        final String iTunesURL = "https://itunes.apple.com/gb/album/hung-up/id91992239?i=91989806&uo=4";
        trackDto.setItunesUrl(iTunesURL);
        fixture.pull(trackDto);
        ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(captor.capture());
        assertThat(captor.getValue().getiTunesUrl(), startsWith("http://clkuk.tradedoubler.com"));
        assertThat(captor.getValue().getiTunesUrl(), containsString(iTunesURL.replace("&", "%26")));
    }

    @Test
    public void checkiTunesURLAfterCutover() throws Exception {
        long timeStamp = System.currentTimeMillis();
        fixture.setiTunesLinkFormatCutoverTimeMillis(timeStamp - 10000);
        TrackDto trackDto = new TrackDto();
        trackDto.setId(1L);
        final String iTunesURL = "https://itunes.apple.com/gb/album/hung-up/id91992239?i=91989806&uo=4";
        trackDto.setItunesUrl(iTunesURL);
        fixture.pull(trackDto);
        ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(captor.capture());
        assertThat(captor.getValue().getiTunesUrl(), equalTo(iTunesURL));
    }

    @Test
    public void checkiTunesURLWithoutCutover() throws Exception {
        TrackDto trackDto = new TrackDto();
        trackDto.setId(1L);
        final String iTunesURL = "https://itunes.apple.com/gb/album/hung-up/id91992239?i=91989806&uo=4";
        trackDto.setItunesUrl(iTunesURL);
        fixture.pull(trackDto);
        ArgumentCaptor<Media> captor = ArgumentCaptor.forClass(Media.class);
        verify(mediaRepository).save(captor.capture());
        assertThat(captor.getValue().getiTunesUrl(), equalTo(iTunesURL));
    }

    @Test
    public void testGetDrops_Success() throws Exception {
        IngestWizardDataDto data = new IngestWizardDataDto();

        when(client.getDrops()).thenReturn(data);

        IngestWizardDataDto result = fixture.getDrops();

        assertSame(data, result);

        verify(client, times(1)).getDrops();
    }

    @Test
    public void testSelectDrops_Success() throws Exception {
        IngestWizardDataDto data = new IngestWizardDataDto();

        when(client.selectDrops(any(IngestWizardDataDto.class))).thenReturn(data);

        IngestWizardDataDto result = fixture.selectDrops(data);

        assertSame(data, result);

        verify(client, times(1)).selectDrops(any(IngestWizardDataDto.class));
    }

    @Test
    public void testSelectTrackDrops_Success() throws Exception {
        IngestWizardDataDto data = new IngestWizardDataDto();

        when(client.selectTrackDrops(any(IngestWizardDataDto.class))).thenReturn(data);

        IngestWizardDataDto result = fixture.selectTrackDrops(data);

        assertSame(data, result);

        verify(client, times(1)).selectTrackDrops(any(IngestWizardDataDto.class));
    }

    @Test
    public void testCommitDrops_Success() throws Exception {
        IngestWizardDataDto data = new IngestWizardDataDto();

        when(client.commitDrops(any(IngestWizardDataDto.class))).thenReturn(true);

        Boolean result = fixture.commitDrops(data);

        assertEquals(true, result);

        verify(client, times(1)).commitDrops(any(IngestWizardDataDto.class));
    }

    @Test
    public void testEncode() throws Exception {
        int i = 1;
        for (TestCase<Long, TrackDto> testcase : mapTrackById.getAll(ENCODE_METHOD)) {
            TrackDto expectedTrack = testcase.getOutput(0);
            TrackDto track = new TrackDto();
            track.setResolution(AudioResolution.RATE_48);
            track.setLicensed(true);
            track.setId(testcase.getInput());
            if (expectedTrack != null) {
                track = fixture.encode(track);
            } else {
                try {
                    track = fixture.encode(track);
                } catch (ServiceException e) {
                    track = null;
                    LOGGER.error(e.getMessage(), e);
                }
            }

            assertEquals(expectedTrack, track);

            verify(client, times(i)).encodeTrack(anyLong(), eq(false), eq(true));
            i++;
        }
    }

    @Test
    public void testFindByQuery() throws Exception {
        Pageable page = new PageRequest(0, 30);
        int i = 1;
        for (TestCase<String, PageListDto<TrackDto>> testcase : mapTrackByQuery.getAll(FIND_METHOD)) {
            PageListDto<TrackDto> expected = testcase.getOutput(0);
            PageListDto<TrackDto> result = fixture.find(testcase.getInput(), page);

            assertEquals(expected, result);

            verify(client, times(i)).search(anyString(), any(Pageable.class));
            i++;
        }
    }

    @Test
    public void testFindByProperties() throws Exception {
        Pageable page = new PageRequest(0, 30);
        int i = 1;
        for (TestCase<SearchTrackDto, PageListDto<TrackDto>> testcase : mapTrackByProperties.getAll(FIND_METHOD)) {
            PageListDto<TrackDto> expected = testcase.getOutput(0);
            PageListDto<TrackDto> result = fixture.find(testcase.getInput(), page);

            assertEquals(expected, result);

            verify(client, times(i)).search(any(SearchTrackDto.class), any(Pageable.class));
            i++;
        }
    }

    @Test
    public void testPull_Audio_Success() throws Exception {
        int i = 0, j = 0;

        for (TestCase<Long, TrackDto> testcase : mapTrackById.getAll(PULL_METHOD)) {
            TrackDto expectedTrack = testcase.getOutput(0);
            TrackDto track = new TrackDto();
            track.setId(testcase.getInput());

            i++;
            if (expectedTrack != null) {
                j++;
                track = fixture.pull(track);

                verify(mediaRepository, times(j)).findByTrackId(any(Long.class));
                verify(mediaRepository, times(j)).save(any(Media.class));
                verify(genreRepository, times(j)).findByName(anyString());
                verify(mediaFileRepository, times(j * 6)).findByName(any(String.class));
                verify(mediaFileRepository, times(j * 6)).save(any(MediaFile.class));
                verify(artistRepository, times(j)).findByName(any(String.class));
                //verify(artistRepository, times(j)).getByRealName(any(String.class), any(Pageable.class));
                verify(artistRepository, times(j)).save(any(Artist.class));
            } else {
                try {
                    track = fixture.pull(track);
                } catch (ServiceException e) {
                    track = null;
                    LOGGER.error(e.getMessage(), e);
                }
            }

            assertEquals(expectedTrack, track);

            verify(client, times(i)).pullTrack(anyLong());
        }
    }

    @Test
    public void testPull_Video_Success() throws Exception {

        TestCase<Long, TrackDto> testcase = mapTrackById.getAll(PULL_METHOD).iterator().next();
        TrackDto expectedTrack = testcase.getOutput(0);
        Iterator<ResourceFileDto> i = expectedTrack.getFiles().iterator();
        int j = 0;
        while (i.hasNext() && j < 7) {
            i.next();
            i.remove();
            j++;
        }
        expectedTrack.getFiles().add(createResourceFile(FileType.VIDEO, AudioResolution.RATE_ORIGINAL, 0, expectedTrack.getIsrc(), null));

        TrackDto track = new TrackDto();

        track.setId(testcase.getInput());

        track = fixture.pull(track);

        verify(mediaRepository, times(1)).findByTrackId(any(Long.class));
        verify(mediaRepository, times(1)).save(any(Media.class));
        verify(genreRepository, times(1)).findByName(anyString());
        verify(mediaFileRepository, times(1)).findByName(eq(expectedTrack.getIsrc()));
        verify(mediaFileRepository, times(1)).findByName(eq(expectedTrack.getIsrc() + ImageResolution.SIZE_22.getSuffix() + "." + FileType.IMAGE.getExt()));
        verify(mediaFileRepository, times(1)).findByName(eq(expectedTrack.getIsrc() + ImageResolution.SIZE_21.getSuffix() + "." + FileType.IMAGE.getExt()));
        verify(mediaFileRepository, times(1)).findByName(eq(expectedTrack.getIsrc() + ImageResolution.SIZE_ORIGINAL.getSuffix() + "." + FileType.IMAGE.getExt()));
        verify(mediaFileRepository, times(0)).findByName(eq(expectedTrack.getIsrc() + AudioResolution.RATE_ORIGINAL.getSuffix() + "." + FileType.DOWNLOAD.getExt()));
        verify(mediaFileRepository, times(0)).findByName(eq(expectedTrack.getIsrc() + AudioResolution.RATE_PREVIEW.getSuffix() + "." + FileType.MOBILE_AUDIO.getExt()));
        verify(mediaFileRepository, times(0)).findByName(eq(expectedTrack.getIsrc() + AudioResolution.RATE_PREVIEW.getSuffix() + "." + FileType.MOBILE_HEADER.getExt()));
        verify(mediaFileRepository, times(4)).save(any(MediaFile.class));
        verify(artistRepository, times(1)).findByName(any(String.class));
        verify(artistRepository, times(1)).save(any(Artist.class));
        verify(client, times(1)).pullTrack(anyLong());

        assertEquals(expectedTrack, track);

    }

    @Before
    public void setUp() throws Exception {
        this.setUpTestData();

        when(client.search(any(String.class), any(Pageable.class))).thenAnswer(new Answer<PageListDto<TrackDto>>() {
            @Override
            public PageListDto<TrackDto> answer(InvocationOnMock invocation) throws Throwable {
                String query = (String) invocation.getArguments()[0];
                return mapTrackByQuery.get(FIND_METHOD, query, 0);
            }
        });
        when(client.search(any(SearchTrackDto.class), any(Pageable.class))).thenAnswer(new Answer<PageListDto<TrackDto>>() {
            @Override
            public PageListDto<TrackDto> answer(InvocationOnMock invocation) throws Throwable {
                SearchTrackDto query = (SearchTrackDto) invocation.getArguments()[0];
                return mapTrackByProperties.get(FIND_METHOD, query, 0);
            }
        });
        when(client.encodeTrack(any(Long.class), any(Boolean.class), any(Boolean.class))).thenAnswer(new Answer<TrackDto>() {
            @Override
            public TrackDto answer(InvocationOnMock invocation) throws Throwable {
                Long id = (Long) invocation.getArguments()[0];
                return mapTrackById.get(ENCODE_METHOD, id, 0);
            }
        });
        when(client.pullTrack(any(Long.class))).thenAnswer(new Answer<TrackDto>() {
            @Override
            public TrackDto answer(InvocationOnMock invocation) throws Throwable {
                Long id = (Long) invocation.getArguments()[0];
                return mapTrackById.get(PULL_METHOD, id, 0);
            }
        });
        when(artistRepository.findByNames(any(String.class), any(Pageable.class))).thenAnswer(new Answer<List<Artist>>() {
            @Override
            public List<Artist> answer(InvocationOnMock invocation) throws Throwable {
                String name = (String) invocation.getArguments()[0];
                Artist artist = mapArtistByRealName.get(name, 0);
                return artist != null ?
                       Collections.singletonList(artist) :
                       Collections.<Artist>emptyList();
            }
        });
        when(artistRepository.findByName(any(String.class))).thenAnswer(new Answer<Artist>() {
            @Override
            public Artist answer(InvocationOnMock invocation) throws Throwable {
                String name = (String) invocation.getArguments()[0];
                return mapArtistByName.get(name, 0);
            }
        });
        when(artistRepository.save(any(Artist.class))).thenAnswer(new Answer<Artist>() {
            @Override
            public Artist answer(InvocationOnMock invocation) throws Throwable {
                return (Artist) invocation.getArguments()[0];
            }
        });

        when(genreRepository.findByName(any(String.class))).thenAnswer(new Answer<Genre>() {
            @Override
            public Genre answer(InvocationOnMock invocation) throws Throwable {
                String name = (String) invocation.getArguments()[0];
                return mapGenreByName.get(name, 0);
            }
        });
        when(genreRepository.save(any(Genre.class))).thenAnswer(new Answer<Genre>() {
            @Override
            public Genre answer(InvocationOnMock invocation) throws Throwable {
                return (Genre) invocation.getArguments()[0];
            }
        });

        when(mediaFileRepository.findByName(any(String.class))).thenAnswer(new Answer<MediaFile>() {
            @Override
            public MediaFile answer(InvocationOnMock invocation) throws Throwable {
                String name = (String) invocation.getArguments()[0];
                return mapMediaFileByName.get(name, 0);
            }
        });
        when(mediaFileRepository.save(any(MediaFile.class))).thenAnswer(new Answer<MediaFile>() {
            @Override
            public MediaFile answer(InvocationOnMock invocation) throws Throwable {
                return (MediaFile) invocation.getArguments()[0];
            }
        });

        when(mediaRepository.findByIsrc(any(String.class))).thenAnswer(new Answer<List<Media>>() {
            @Override
            public List<Media> answer(InvocationOnMock invocation) throws Throwable {
                String isrc = (String) invocation.getArguments()[0];
                return Arrays.asList(mapMediaByIsrc.get(isrc, 0));
            }
        });
        when(mediaRepository.save(any(Media.class))).thenAnswer(new Answer<Media>() {
            @Override
            public Media answer(InvocationOnMock invocation) throws Throwable {
                return (Media) invocation.getArguments()[0];
            }
        });

        fixture = new TrackRepoServiceImpl();
        fixture.setGenreRepository(genreRepository);
        fixture.setArtistRepository(artistRepository);
        fixture.setMediaFileRepository(mediaFileRepository);
        fixture.setClient(client);
        fixture.setMediaRepository(mediaRepository);
    }

    @SuppressWarnings("unchecked")
    public void setUpTestData() throws Exception {
        TrackDto track = new TrackDto();
        track.setId(1L);
        track.setIsrc("APPCAST");
        track.setArtist("APPCAST Artist");
        track.setTitle("APPCAST Title");
        track.setGenre("APPCAST Genre");
        track.setIngestor("APPCAST Ingestor");
        track.setStatus(TrackStatus.NONE);
        track.setPublishDate(new Date());
        track.setIngestionDate(new Date());
        track.setItunesUrl("http:\\itunes.com");

        String isrc = track.getIsrc();

        String mp3hash = "asfjjldfgoookgdgjjkkdgklldfl;;dfllgort";
        String aac48hash = "ertretyrtyhvfhgfhjhhjghjhjghjhjghjjgjd";
        String aac96hash = "qwertyuioppsdfgeeehhjjsdddssdddfffdfas";

        List<ResourceFileDto> files = new ArrayList<ResourceFileDto>(15);
        files.add(createResourceFile(FileType.MOBILE_HEADER, AudioResolution.RATE_48, 10000, isrc, null));
        files.add(createResourceFile(FileType.MOBILE_HEADER, AudioResolution.RATE_96, 10000, isrc, null));
        files.add(createResourceFile(FileType.MOBILE_HEADER, AudioResolution.RATE_PREVIEW, 10000, isrc, null));
        files.add(createResourceFile(FileType.MOBILE_AUDIO, AudioResolution.RATE_48, 10000, isrc, aac48hash));
        files.add(createResourceFile(FileType.MOBILE_AUDIO, AudioResolution.RATE_96, 10000, isrc, aac96hash));
        files.add(createResourceFile(FileType.MOBILE_AUDIO, AudioResolution.RATE_PREVIEW, 10000, isrc, null));
        files.add(createResourceFile(FileType.DOWNLOAD, AudioResolution.RATE_ORIGINAL, 10000, isrc, mp3hash));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_ORIGINAL, 10000, isrc, null));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_LARGE, 10000, isrc, null));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_SMALL, 10000, isrc, null));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_22, 10000, isrc, null));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_21, 10000, isrc, null));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_11, 10000, isrc, null));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_6, 10000, isrc, null));
        files.add(createResourceFile(FileType.IMAGE, ImageResolution.SIZE_3, 10000, isrc, null));
        track.setFiles(files);

        TrackDto encodedTrack = new TrackDto(track);
        encodedTrack.setStatus(TrackStatus.ENCODED);
        encodedTrack.setFiles(null);
        mapTrackById.add(new TestCase<Long, TrackDto>(ENCODE_METHOD, 1, track.getId(), encodedTrack));
        mapTrackById.add(new TestCase<Long, TrackDto>(ENCODE_METHOD, 1, 2L, (TrackDto[]) null));

        TrackDto pullTrack = new TrackDto(track);
        pullTrack.setStatus(TrackStatus.ENCODED);
        mapTrackById.add(new TestCase<Long, TrackDto>(PULL_METHOD, 1, track.getId(), pullTrack));
        mapTrackById.add(new TestCase<Long, TrackDto>(PULL_METHOD, 1, 2L, (TrackDto[]) null));

        TrackDto findTrack = new TrackDto(track);
        findTrack.setFiles(null);
        mapTrackByQuery.add(new TestCase<String, PageListDto<TrackDto>>(FIND_METHOD, 1, track.getArtist(), TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByQuery.add(new TestCase<String, PageListDto<TrackDto>>(FIND_METHOD, 1, track.getTitle(), TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByQuery.add(new TestCase<String, PageListDto<TrackDto>>(FIND_METHOD, 1, track.getIsrc(), TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByQuery.add(new TestCase<String, PageListDto<TrackDto>>(FIND_METHOD, 1, "none", TrackDtoFactory.getEmptyTrackPage()));

        SearchTrackDto search = new SearchTrackDto();
        search.setIsrc(track.getIsrc());
        search.setArtist(track.getArtist());
        search.setTitle(track.getTitle());
        search.setIngestor(track.getIngestor());
        search.setIngestTo(new Date(track.getIngestionDate().getTime() + 100000000000L));
        search.setIngestFrom(new Date(track.getIngestionDate().getTime() - 100000000000L));
        SearchTrackDto search1 = new SearchTrackDto();
        search1.setIsrc(track.getIsrc());
        SearchTrackDto search2 = new SearchTrackDto();
        search2.setArtist(track.getArtist());
        SearchTrackDto search3 = new SearchTrackDto();
        search3.setTitle(track.getTitle());
        SearchTrackDto search4 = new SearchTrackDto();
        search4.setIngestor(track.getIngestor());
        SearchTrackDto search5 = new SearchTrackDto();
        search5.setIngestTo(new Date(track.getIngestionDate().getTime() + 100000000000L));
        SearchTrackDto search6 = new SearchTrackDto();
        search6.setIngestFrom(new Date(track.getIngestionDate().getTime() - 100000000000L));

        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, search, TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, search1, TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, search2, TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, search3, TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, search4, TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, search5, TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, search6, TrackDtoFactory.getTrackPage(findTrack, 1)));
        mapTrackByProperties.add(new TestCase<SearchTrackDto, PageListDto<TrackDto>>(FIND_METHOD, 1, new SearchTrackDto(), TrackDtoFactory.getEmptyTrackPage()));
    }

    private ResourceFileDto createResourceFile(FileType type, Resolution resolution, Integer size, String isrc, String mediaHash) throws IOException {
        ResourceFileDto resourceFileDto = new ResourceFileDto(type, resolution, isrc, mediaHash);
        resourceFileDto.setSize(size);

        return resourceFileDto;
    }

    @Test
    public void shouldAssignReportingOptions() {
        //given
        TrackReportingOptionsDto trackReportingOptionsDto = new TrackReportingOptionsDto();

        ResponseEntity responseEntity = mock(ResponseEntity.class);

        when(client.assignReportingOptions(trackReportingOptionsDto)).thenReturn(responseEntity);

        //when
        fixture.assignReportingOptions(trackReportingOptionsDto);

        //then
        verify(client, times(1)).assignReportingOptions(trackReportingOptionsDto);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeExceptionWhenCanNotAssignReportingOptions() {
        //given
        TrackReportingOptionsDto trackReportingOptionsDto = new TrackReportingOptionsDto();

        when(client.assignReportingOptions(trackReportingOptionsDto)).thenThrow(new RuntimeException());

        //when
        fixture.assignReportingOptions(trackReportingOptionsDto);
    }

    @After
    public void tearDown() throws Exception {
        mapTrackById.clear();
        mapTrackByQuery.clear();
        mapTrackByProperties.clear();
        mapArtistByName.clear();
        mapArtistByRealName.clear();
        mapGenreByName.clear();
        mapMediaByIsrc.clear();
        mapMediaFileByName.clear();
    }
}