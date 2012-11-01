package mobi.nowtechnologies.java.server.track_repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.*;

import javax.servlet.ServletContext;

import mobi.nowtechnologies.server.shared.dto.*;
import mobi.nowtechnologies.server.shared.dto.admin.SearchTrackDto;
import mobi.nowtechnologies.server.track_repo.domain.Track;
import mobi.nowtechnologies.server.track_repo.repository.TrackRepository;
import mobi.nowtechnologies.server.track_repo.service.impl.TrackServiceImpl;
import mobi.nowtechnologies.server.track_repo.utils.ExternalCommandThread;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.ServletContextResource;

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

		ExternalCommandThread command = mock(ExternalCommandThread.class);
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
		when(trackRepository.find(any(SearchTrackDto.class), any(Pageable.class))).thenAnswer(new Answer<List<Track>>() {
			@Override
			public List<Track> answer(InvocationOnMock invocation) throws Throwable {
				SearchTrackDto searchTrackDto = (SearchTrackDto) invocation.getArguments()[0];
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

				return matched ? Collections.singletonList(track) : Collections.<Track> emptyList();
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
		service = new TrackServiceImpl();
		service.setWorkDir(new ServletContextResource(servletContext, WORKDIR_PATH));
		service.setEncodeDestination(new ServletContextResource(servletContext, ENCODE_DIST_PATH));
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
		PageListDto<TrackDto> tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getList().size(), 1);
		searchTrackDto = new SearchTrackDto();
		searchTrackDto.setTitle(TITLE_VALUE);
		tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getList().size(), 1);
		searchTrackDto = new SearchTrackDto();
		searchTrackDto.setIsrc(ISRC_VALUE);
		tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getList().size(), 1);
		searchTrackDto = new SearchTrackDto();
		searchTrackDto.setArtist(ARTIST_VALUE);
		searchTrackDto.setTitle(TITLE_VALUE);
		searchTrackDto.setIsrc(ISRC_VALUE);
		tracks = service.find((SearchTrackDto)searchTrackDto, page);
		assertNotNull(tracks);
		assertEquals(tracks.getList().size(), 1);

		TrackDto track = tracks.getList().get(0);
		assertEquals(track.getId(), ID_VALUE);
		assertEquals(track.getTitle(), TITLE_VALUE);
		assertEquals(track.getArtist(), ARTIST_VALUE);
		assertEquals(track.getIsrc(), ISRC_VALUE);
		assertEquals(track.getIngestor(), INGESTOR_VALUE);
		assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
		assertEquals(track.getStatus(), TrackStatus.NONE);
	}

	@Test
	public void encodeTest() throws Exception {
		TrackDto track = service.encode(ID_VALUE, false, true);

		assertNotNull(track);
		assertEquals(track.getId(), ID_VALUE);
		assertEquals(track.getTitle(), TITLE_VALUE);
		assertEquals(track.getArtist(), ARTIST_VALUE);
		assertEquals(track.getIsrc(), ISRC_VALUE);
		assertEquals(track.getIngestor(), INGESTOR_VALUE);
		assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
		assertEquals(track.getStatus(), TrackStatus.ENCODED);
	}

	@Test
	public void pullTest() {
		track.setStatus(TrackStatus.ENCODED);

		TrackDto track = service.pull(ID_VALUE);
		assertNotNull(track);
		assertEquals(track.getId(), ID_VALUE);
		assertEquals(track.getTitle(), TITLE_VALUE);
		assertEquals(track.getArtist(), ARTIST_VALUE);
		assertEquals(track.getIsrc(), ISRC_VALUE);
		assertEquals(track.getIngestor(), INGESTOR_VALUE);
		assertEquals(track.getIngestionDate(), INGESTION_DATE_VALUE);
		assertEquals(track.getStatus(), TrackStatus.ENCODED);

		Iterator<ResourceFileDto> i = track.getFiles().iterator();

		ResourceFileDto file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.MOBILE_HEADER.name());
		assertEquals(file.getResolution(), AudioResolution.RATE_48.name());
		assertEquals(file.getSize(), new Integer(74744));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.MOBILE_HEADER.name());
		assertEquals(file.getResolution(), AudioResolution.RATE_96.name());
		assertEquals(file.getSize(), new Integer(74744));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.MOBILE_HEADER.name());
		assertEquals(file.getResolution(), AudioResolution.RATE_PREVIEW.name());
		assertEquals(file.getSize(), new Integer(41487));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.MOBILE_AUDIO.name());
		assertEquals(file.getResolution(), AudioResolution.RATE_48.name());
		assertEquals(file.getSize(), new Integer(2164382));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.MOBILE_AUDIO.name());
		assertEquals(file.getResolution(), AudioResolution.RATE_96.name());
		assertEquals(file.getSize(), new Integer(4330804));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.MOBILE_AUDIO.name());
		assertEquals(file.getResolution(), AudioResolution.RATE_PREVIEW.name());
		assertEquals(file.getSize(), new Integer(361306));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.DOWNLOAD.name());
		assertEquals(file.getResolution(), AudioResolution.RATE_ORIGINAL.name());
		assertEquals(file.getSize(), new Integer(12937059));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_ORIGINAL.name());
		assertEquals(file.getSize(), new Integer(0));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_LARGE.name());
		assertEquals(file.getSize(), new Integer(6003));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_SMALL.name());
		assertEquals(file.getSize(), new Integer(1541));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_22.name());
		assertEquals(file.getSize(), new Integer(6003));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_21.name());
		assertEquals(file.getSize(), new Integer(2120));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_11.name());
		assertEquals(file.getSize(), new Integer(2120));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_6.name());
		assertEquals(file.getSize(), new Integer(1317));

		file = i.next();
		assertNotNull(file);
		assertEquals(file.getType(), FileType.IMAGE.name());
		assertEquals(file.getResolution(), ImageResolution.SIZE_3.name());
		assertEquals(file.getSize(), new Integer(7550));
	}
}