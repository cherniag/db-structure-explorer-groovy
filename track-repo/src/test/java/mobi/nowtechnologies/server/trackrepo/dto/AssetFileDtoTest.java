package mobi.nowtechnologies.server.trackrepo.dto;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.enums.FileType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * The class <code>AssetFileDtoTest</code> contains tests for the class <code>{@link AssetFileDto}</code>.
 *
 * @generatedBy CodePro at 11/13/12 3:16 PM
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
public class AssetFileDtoTest {
	/**
	 * Run the AssetFileDto(AssetFile) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testAssetFileDto()
		throws Exception {
		AssetFile file = new AssetFile();
		file.setMd5("");
		file.setPath("");
		file.setType(mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.DOWNLOAD);

		AssetFileDto result = new AssetFileDto(file);

		assertEquals(AssetFileDto.toFileType(file.getType()), result.getType());
		assertEquals(file.getPath(), result.getPath());
		assertEquals(null, result.getContent());
		assertEquals(file.getMd5(), result.getMd5());
	}

	/**
	 * Run the FileType toFileType(FileType) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToFileType_Download()
		throws Exception {
		mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType fileType = mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.DOWNLOAD;

		FileType result = AssetFileDto.toFileType(fileType);

		assertNotNull(result);
		assertEquals(FileType.ORIGINAL_MP3, result);
	}

	/**
	 * Run the FileType toFileType(FileType) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToFileType_Image()
		throws Exception {
		mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType fileType = mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.IMAGE;

		FileType result = AssetFileDto.toFileType(fileType);

		assertNotNull(result);
		assertEquals(FileType.IMAGE, result);
	}

	/**
	 * Run the FileType toFileType(FileType) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToFileType_Mobile()
		throws Exception {
		mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType fileType = mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.MOBILE;

		FileType result = AssetFileDto.toFileType(fileType);

		assertNotNull(result);
		assertEquals(FileType.ORIGINAL_ACC, result);
	}

	/**
	 * Run the FileType toFileType(FileType) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToFileType_Preview()
		throws Exception {
		mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType fileType = mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.PREVIEW;

		FileType result = AssetFileDto.toFileType(fileType);

		assertNotNull(result);
		assertEquals(FileType.ORIGINAL_ACC, result);
	}

	@Test
	public void testToFileType_Video()
		throws Exception {
		mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType fileType = AssetFile.FileType.VIDEO;

		FileType result = AssetFileDto.toFileType(fileType);

		assertNotNull(result);
		assertEquals(FileType.VIDEO, result);
	}

	/**
	 * Run the List<AssetFileDto> toList(List<AssetFile>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 11/13/12 3:16 PM
	 */
	@Test
	public void testToList()
		throws Exception {
		List<AssetFile> files = new LinkedList<AssetFile>();

		List<AssetFileDto> result = AssetFileDto.toList(files);

		assertNotNull(result);
		assertEquals(files.size(), result.size());
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