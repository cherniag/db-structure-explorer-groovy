package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The class <code>TrackDtoExtTest</code> contains tests for the class <code>{@link TrackDtoMapper}</code>.
 *
 * @generatedBy CodePro at 11/13/12 3:16 PM
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
public class TrackDtoExtTest {
	/**
	 * Run the TrackDtoMapper(Track) constructor test none territories.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testTrackDtoExt_NotNullFiles()
		throws Exception {
        AssetFile mediaFile = new AssetFile();
        mediaFile.setId(1L);
        AssetFile coverFile = new AssetFile();
        coverFile.setId(2L);

		Track track = new Track();
		track.setLicensed(new Boolean(true));
		track.setExplicit(new Boolean(true));
		track.setStatus(TrackStatus.ENCODED);
		track.setYear("");
		track.setResolution(AudioResolution.RATE_256);
		track.setIngestionDate(new Date());
		track.setIngestor("");
		track.setTitle("");
		track.setCopyright("");
		track.setGenre("");
		track.setIsrc("");
		track.setTerritories(new HashSet<Territory>());
		track.setItunesUrl("");
		track.setAlbum("");
		track.setIngestionUpdateDate(new Date());
		track.setSubTitle("");
		track.setProductCode("");
		track.setArtist("");
		track.setInfo("");
		track.setProductId("");
        track.setTerritoryCodes("GB, UA, US, NL");
        track.setCoverFile(coverFile);
        track.setMediaFile(mediaFile);

		TrackDtoMapper result = new TrackDtoMapper(track);

		assertNotNull(result);
		assertEquals(track.getId(), result.getId());
		assertEquals(track.getYear(), result.getYear());
		assertEquals(track.getInfo(), result.getInfo());
		assertEquals(null, result.getLabel());
		assertEquals(track.getProductId(), result.getProductId());
		assertEquals(track.getTitle(), result.getTitle());
		assertEquals(track.getCopyright(), result.getCopyright());
		assertEquals(track.getGenre(), result.getGenre());
		assertEquals(track.getIsrc(), result.getIsrc());
		assertEquals(track.getArtist(), result.getArtist());
		assertEquals(track.getPublishDate(), result.getPublishDate());
		assertEquals(track.getAlbum(), result.getAlbum());
		assertEquals(track.getLicensed(), result.getLicensed());
		assertEquals(track.getExplicit(), result.getExplicit());
		assertEquals(track.getIngestor(), result.getIngestor());
		assertEquals(track.getSubTitle(), result.getSubTitle());
		assertEquals(track.getProductCode(), result.getProductCode());
		assertEquals(track.getTerritoryCodes(), result.getTerritories());
		assertEquals(track.getCoverFile().getId().toString(), result.getCoverFileName());
		assertEquals(track.getMediaFile().getId().toString(), result.getMediaFileName());
		assertEquals(track.getItunesUrl(), result.getItunesUrl());
		assertEquals(null, result.getReleaseDate());
	}

    @Test
    public void testTrackDtoExt_NullFiles_Success()
            throws Exception {
        AssetFile mediaFile = null;
        AssetFile coverFile = null;

        Track track = new Track();
        track.setCoverFile(coverFile);
        track.setMediaFile(mediaFile);

        TrackDtoMapper result = new TrackDtoMapper(track);

        assertNotNull(result);
        assertEquals("0", result.getCoverFileName());
        assertEquals("0", result.getMediaFileName());
    }

	/**
	 * Run the List<TrackDtoMapper> toList(List<Track>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToList()
		throws Exception {
		List<Track> tracks = new LinkedList<Track>();

		List<TrackDtoMapper> result = TrackDtoMapper.toList(tracks);

		assertNotNull(result);
		assertEquals(tracks.size(), result.size());
	}
	
	/**
	 * Run the PageListDto<TrackDtoMapper> toPage(Page<Track>) method test odd page size.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToPage_OddPageSize()
		throws Exception {
		Page<Track> tracks = new PageImpl<Track>(new LinkedList<Track>(), new PageRequest(1, 10), 42);

		PageListDto<TrackDtoMapper> result = TrackDtoMapper.toPage(tracks);

		assertNotNull(result);
		assertEquals(tracks.getContent().size(), result.getList().size());
		assertEquals(tracks.getNumber(), result.getPage());
		assertEquals(tracks.getSize(), result.getSize());
		assertEquals(5, result.getTotal());
	}
	
	/**
	 * Run the PageListDto<TrackDtoMapper> toPage(Page<Track>) method test even page size.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToPage_EvenPageSize()
		throws Exception {
		Page<Track> tracks = new PageImpl<Track>(new LinkedList<Track>(), new PageRequest(1, 10), 40);

		PageListDto<TrackDtoMapper> result = TrackDtoMapper.toPage(tracks);

		assertNotNull(result);
		assertEquals(4, result.getTotal());
	}

	/**
	 * Run the PageListDto<TrackDtoMapper> toPage(Page<Track>) method test failure.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test(expected = ArithmeticException.class)
	public void testToPage_ZeroPageSize()
		throws Exception {
		Page<Track> tracks = new PageImpl<Track>(new LinkedList<Track>());

		PageListDto<TrackDtoMapper> result = TrackDtoMapper.toPage(tracks);

		assertNotNull(result);
		
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Before
	public void setUp()
		throws Exception {
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@After
	public void tearDown()
		throws Exception {
	}
}