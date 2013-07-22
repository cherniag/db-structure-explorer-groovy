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
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.dto.ResourceFileDto;
import mobi.nowtechnologies.server.trackrepo.dto.SearchTrackDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.impl.TrackRepositoryHttpClientImpl;
import mobi.nowtechnologies.shared.testcases.TestCase;
import mobi.nowtechnologies.shared.testcases.TestCases;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * The class <code>TrackRepoServiceImplTest</code> contains tests for the class <code>{@link TrackRepoServiceImpl}</code>.
 * 
 * @generatedBy CodePro at 8/13/12 1:26 PM
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith(MockitoJUnitRunner.class)
public class TrackRepoServiceImplTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrackRepoServiceImplTest.class);
	
	private static final String ENCODE_METHOD = "encode";
	private static final String PULL_METHOD = "pull";
	private static final String FIND_METHOD = "find";

	private TestCases<Long, TrackDto> mapTrackById = new TestCases<Long, TrackDto>();
	private TestCases<String, PageListDto<TrackDto>> mapTrackByQuery = new TestCases<String, PageListDto<TrackDto>>();
	private TestCases<SearchTrackDto, PageListDto<TrackDto>> mapTrackByProperties = new TestCases<SearchTrackDto, PageListDto<TrackDto>>();
	private TestCases<String, Media> mapMediaByIsrc = new TestCases<String, Media>();
	private TestCases<String, MediaFile> mapMediaFileByName = new TestCases<String, MediaFile>();
	private TestCases<String, Artist> mapArtistByName = new TestCases<String, Artist>();
	private TestCases<String, Artist> mapArtistByRealName = new TestCases<String, Artist>();
	private TestCases<String, Genre> mapGenreByName = new TestCases<String, Genre>();

	@Mock
	private TrackRepositoryHttpClientImpl client;
	@Mock
	private ArtistRepository artistRepository;
	@Mock
	private MediaRepository mediaRepository;
	@Mock
	private GenreRepository genreRepository;
	@Mock
	private MediaFileRepository mediaFileRepository;
	private TrackRepoServiceImpl fixture;


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

	/**
	 * Run the TrackDto encode(Long) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 8/13/12 1:26 PM
	 */
	@Test
	public void testEncode() throws Exception {
		int i = 1;
		for (TestCase<Long, TrackDto> testcase : mapTrackById.getAll(ENCODE_METHOD))
		{
			TrackDto expectedTrack = testcase.getOutput(0);
			TrackDto track = new TrackDto();
			track.setResolution(AudioResolution.RATE_48);
			track.setLicensed(true);
			track.setId(testcase.getInput());
			if(expectedTrack != null){
				track = fixture.encode(track);
			}else{
				try{
					track = fixture.encode(track);
				}catch (ServiceException e) {
					track = null;
					LOGGER.error(e.getMessage(), e);
				}
			}

			assertEquals(expectedTrack, track);

			verify(client, times(i)).encodeTrack(anyLong(), eq(false), eq(true));
			i++;
		}
	}

	/**
	 * Run the PageListDto<TrackDto> find(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 8/13/12 1:26 PM
	 */
	@Test
	public void testFindByQuery() throws Exception {
		Pageable page = new PageRequest(0, 30);
		int i = 1;
		for (TestCase<String, PageListDto<TrackDto>> testcase : mapTrackByQuery.getAll(FIND_METHOD))
		{
			PageListDto<TrackDto> expected = testcase.getOutput(0);
			PageListDto<TrackDto> result = fixture.find(testcase.getInput(), page);

			assertEquals(expected, result);

			verify(client, times(i)).search(anyString(), any(Pageable.class));
			i++;
		}
	}

	/**
	 * Run the PageListDto<TrackDto> find(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 8/13/12 1:26 PM
	 */
	@Test
	public void testFindByProperties() throws Exception {
		Pageable page = new PageRequest(0, 30);
		int i = 1;
		for (TestCase<SearchTrackDto, PageListDto<TrackDto>> testcase : mapTrackByProperties.getAll(FIND_METHOD))
		{
			PageListDto<TrackDto> expected = testcase.getOutput(0);
			PageListDto<TrackDto> result = fixture.find(testcase.getInput(), page);

			assertEquals(expected, result);
			
			verify(client, times(i)).search(any(SearchTrackDto.class), any(Pageable.class));
			i++;
		}
	}

	/**
	 * Run the TrackDto pull(Long) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 8/13/12 1:26 PM
	 */
	@Test
	public void testPull() throws Exception {
		int i = 0,j =0;

		for (TestCase<Long, TrackDto> testcase : mapTrackById.getAll(PULL_METHOD))
		{
			TrackDto expectedTrack = testcase.getOutput(0);
			TrackDto track = new TrackDto();
			track.setId(testcase.getInput());
			
			i++;
			if(expectedTrack != null){
				j++;
				track = fixture.pull(track);
				
				verify(mediaRepository, times(j)).getByIsrc(any(String.class));
				verify(mediaRepository, times(j)).save(any(Media.class));
				verify(genreRepository, times(j)).getByName(anyString());
				verify(mediaFileRepository, times(j*6)).getByName(any(String.class));
				verify(mediaFileRepository, times(j*6)).save(any(MediaFile.class));
				verify(artistRepository, times(j)).getByName(any(String.class));
				//verify(artistRepository, times(j)).getByRealName(any(String.class), any(Pageable.class));
				verify(artistRepository, times(j)).save(any(Artist.class));
			}else{
				try{
					track = fixture.pull(track);
				}catch (ServiceException e) {
					track = null;
					LOGGER.error(e.getMessage(), e);
				}
			}
			
			assertEquals(expectedTrack, track);
			
			verify(client, times(i)).pullTrack(anyLong());
		}
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 * 
	 * @generatedBy CodePro at 8/13/12 1:26 PM
	 */
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
		when(artistRepository.getByNames(any(String.class), any(Pageable.class))).thenAnswer(new Answer<List<Artist>>() {
			@Override
			public List<Artist> answer(InvocationOnMock invocation) throws Throwable {
				String name = (String) invocation.getArguments()[0];
				Artist artist = mapArtistByRealName.get(name, 0);
				return artist != null ? Collections.singletonList(artist) : Collections.<Artist>emptyList();
			}
		});
		when(artistRepository.getByName(any(String.class))).thenAnswer(new Answer<Artist>() {
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

		when(genreRepository.getByName(any(String.class))).thenAnswer(new Answer<Genre>() {
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

		when(mediaFileRepository.getByName(any(String.class))).thenAnswer(new Answer<MediaFile>() {
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

		when(mediaRepository.getByIsrc(any(String.class))).thenAnswer(new Answer<Media>() {
			@Override
			public Media answer(InvocationOnMock invocation) throws Throwable {
				String isrc = (String) invocation.getArguments()[0];
				return mapMediaByIsrc.get(isrc, 0);
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

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 * 
	 * @generatedBy CodePro at 8/13/12 1:26 PM
	 */
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