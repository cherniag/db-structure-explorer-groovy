package mobi.nowtechnologies.server.trackrepo.dto.builder;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.ResourceFileDto;
import mobi.nowtechnologies.server.trackrepo.enums.AudioResolution;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import mobi.nowtechnologies.server.trackrepo.enums.ImageResolution;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.ServletContextResource;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The class <code>ResourceFileDtoBuilderTest</code> contains tests for the class <code>{@link ResourceFileDtoBuilder}</code>.
 *
 * @generatedBy CodePro at 11/13/12 2:14 PM
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
public class ResourceFileDtoBuilderTest {
	private static final String ISRC_VALUE = "APPCAST";
	
	private static final String ENCODE_DIST_PATH = "publish";
	private static final String WORKDIR_PATH = "work/";
	
	private ResourceFileDtoBuilder fixture;
	/**
	 * Run the List<ResourceFileDto> build(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 2:14 PM
	 */
	@Test
	public void testBuild_Audio_Success()
		throws Exception {
        Track track = new Track();
        track.setId(777L);
        track.setIsrc(ISRC_VALUE);

        final AssetFile audioFile = new AssetFile();
        audioFile.setType(AssetFile.FileType.DOWNLOAD);
        audioFile.setPath("somepath");
        track.setFiles(Collections.singleton(audioFile));

		List<ResourceFileDto> result = fixture.build(track);

		assertNotNull(result);
		
		Iterator<ResourceFileDto> i = result.iterator();

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

    @Test
    public void testBuild_Video_Success()
            throws Exception {
        Track track = new Track();
        track.setIsrc(ISRC_VALUE);
        track.setId(777L);

        final AssetFile videoFile = new AssetFile();
        videoFile.setType(AssetFile.FileType.VIDEO);
        videoFile.setPath("somepath");
        videoFile.setExternalId("11111111111");
        videoFile.setDuration(20000);
        track.setFiles(Collections.singleton(videoFile));

        List<ResourceFileDto> result = fixture.build(track);

        assertNotNull(result);

        Iterator<ResourceFileDto> i = result.iterator();

        ResourceFileDto file = i.next();
        assertNotNull(file);
        assertEquals(file.getType(), FileType.VIDEO.name());
        assertEquals(file.getResolution(), AudioResolution.RATE_ORIGINAL.name());
        assertEquals(file.getSize().intValue(), 0);
        assertEquals(videoFile.getDuration(), file.getDuration());
        assertEquals(videoFile.getExternalId(), file.getFilename());

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

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 11/13/12 2:14 PM
	 */
	@Before
	public void setUp()
		throws Exception {
		ServletContext servletContext = new MockServletContext();
		fixture = new ResourceFileDtoBuilder();
		
		fixture.setWorkDir(new ServletContextResource(servletContext, WORKDIR_PATH));
		fixture.setPublishDir(new ServletContextResource(servletContext, ENCODE_DIST_PATH));
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 11/13/12 2:14 PM
	 */
	@After
	public void tearDown()
		throws Exception {
	}
}