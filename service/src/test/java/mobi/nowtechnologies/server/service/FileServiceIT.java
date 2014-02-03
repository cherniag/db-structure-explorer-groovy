package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml",
        "/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class FileServiceIT {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileServiceIT.class.getName());

    @Autowired
    private FileService service;

	private static FileService fileService;
	
	private static final User user = new User();
	
	static {
		user.setId(1);
		//userWithCommunity.setDeviceType((byte) 0);
	}

    @Test
    public void verifyThatGetFolderReturbOnlyExistingFiles(){
        File file = new File(service.getFolder("audio"));
        assertTrue(file.exists());
    }


	@Test
	public void testGetFile_WhenImageExists()
		throws Exception {
		String mediaId = "US-UM7-11-00061";
		FileService.FileType fileType = FileService.FileType.IMAGE_RESOLUTION;
		int userId = 1;
		String fileResolution = "fileResolution";

		File result = service.getFile(mediaId, fileType, fileResolution, user);
		assertNotNull(result);
		assertTrue(result.exists());
	}

	@Test(expected = ServiceException.class)
    @Ignore
	public void testGetFile_WhenImageDoesNotExist()
		throws Exception {
		String mediaId = "47";
		FileService.FileType fileType = FileService.FileType.IMAGE_RESOLUTION;
		int userId = 1;
		String fileResolution = "wrong";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
		assertNull(result);
	}

	@Test
    @Ignore
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

	@Test(expected = ServiceException.class)
    @Ignore
	public void testGetFile_WhenMediaFileDoesNotExists()
		throws Exception {
		String mediaId = "49";
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = 1;
		String fileResolution = "fileResolution";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
		assertNull(result);
	}

	@Test(expected = ServiceException.class)
    @Ignore
	public void testGetFile_WhenMediaIdIsNull()
		throws Exception {
		String mediaId = null;
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = 1;
		String fileResolution = "";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

	@Test(expected = ServiceException.class)
    @Ignore
	public void testGetFile_WhenFileTypeIsNull()
		throws Exception {
		String mediaId = "";
		FileService.FileType fileType = null;
		int userId = 1;
		String fileResolution = "";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

	@Test(expected = java.lang.NumberFormatException.class)
    @Ignore
	public void testGetFile_InvalidMediaId()
		throws Exception {
		String mediaId = "";
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = 1;
		String fileResolution = "";

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

	@Test(expected = PersistenceException.class)
    @Ignore
	public void testGetFile_WrongUserId()
		throws Exception {
		String mediaId = "47";
		FileService.FileType fileType = FileService.FileType.AUDIO;
		int userId = -1;
		String fileResolution = null;

		File result = fileService.getFile(mediaId, fileType, fileResolution, user);
	}

}