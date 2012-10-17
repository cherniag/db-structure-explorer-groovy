package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.web.MockServletContext;

/**
 * The class <code>FileServiceTest</code> contains tests for the class <code>{@link FileService}</code>.
 *
 * @generatedBy CodePro at 12.07.11 15:44
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class FileServiceTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileServiceTest.class.getName());
	/**
	 * 
	 */
	private static FileService fileService;
	
	private static final User user = new User();
	
	static {
		user.setId(1);
		//user.setDeviceType((byte) 0);
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test(expected = ServiceException.class)
	public void testGetFile_WhenImageExists()
		throws Exception {
		String mediaId = "47";
		FileService.FileType fileType = FileService.FileType.IMAGE_LARGE;
		int userId = 1;
		String fileResolution = "fileResolution";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
		assertNotNull(result);
		assertTrue(result.exists());
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test(expected = ServiceException.class)
	public void testGetFile_WhenImageDoesNotExist()
		throws Exception {
		String mediaId = "47";
		FileService.FileType fileType = FileService.FileType.IMAGE_RESOLUTION;
		int userId = 1;
		String fileResolution = "wrong";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
		assertNull(result);
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test
	public void testGetFile_WhenMediaFileExists()
		throws Exception {
		String mediaId = "47";
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = 1;
		String fileResolution = "fileResolution";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);

		assertNotNull(result);
		assertTrue(result.exists());
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test(expected = ServiceException.class)
	public void testGetFile_WhenMediaFileDoesNotExists()
		throws Exception {
		String mediaId = "49";
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = 1;
		String fileResolution = "fileResolution";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
		assertNull(result);
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test(expected = ServiceException.class)
	public void testGetFile_WhenMediaIdIsNull()
		throws Exception {
		String mediaId = null;
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = 1;
		String fileResolution = "";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test(expected = ServiceException.class)
	public void testGetFile_WhenFileTypeIsNull()
		throws Exception {
		String mediaId = "";
		FileService.FileType fileType = null;
		int userId = 1;
		String fileResolution = "";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test(expected = java.lang.NumberFormatException.class)
	public void testGetFile_InvalidMediaId()
		throws Exception {
		String mediaId = "";
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = 1;
		String fileResolution = "";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

	/**
	 * Run the File getFile(String,FileType,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@Test(expected = PersistenceException.class)
	public void testGetFile_WrongUserId()
		throws Exception {
		String mediaId = "47";
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = -1;
		String fileResolution = null;

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@BeforeClass
	public static void setUp()
		throws Exception {
		new ClassPathXmlApplicationContext(
				new String[] {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" });
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 12.07.11 15:44
	 */
	@AfterClass
	public static void tearDown()
		throws Exception {
		// Add additional tear down code here
	}
}