package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.User;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class FileServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceIT.class.getName());
    private static final User user = new User();

    static {
        user.setId(1);
        //userWithCommunity.setDeviceType((byte) 0);
    }

    @Autowired
    private FileService service;

    @Test
    public void verifyThatGetFolderReturbOnlyExistingFiles() {
        File file = new File(service.getFolder("audio"));
        assertTrue(file.exists());
    }


    @Test
    public void testGetFile_WhenImageExists() throws Exception {
        String mediaId = "US-UM7-11-00061";
        FileService.FileType fileType = FileService.FileType.IMAGE_RESOLUTION;
        int userId = 1;
        String fileResolution = "fileResolution";

        File result = service.getFile(mediaId, fileType, fileResolution, user);
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFile_WhenImageDoesNotExist() throws Exception {
        String mediaId = "US-UM7-11-00061";
        FileService.FileType fileType = FileService.FileType.IMAGE_RESOLUTION;
        String fileResolution = "wrong";

        File result = service.getFile(mediaId, fileType, fileResolution, user);
        assertNull(result);
    }

    @Test
    public void testGetFile_WhenMediaFileExists() throws Exception {
        String mediaId = "US-UM7-11-00061";
        FileService.FileType fileType = FileService.FileType.AUDIO;
        String fileResolution = "fileResolution";

        File result = service.getFile(mediaId, fileType, fileResolution, user);

        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFile_WhenMediaFileDoesNotExists() throws Exception {
        String mediaId = "US-UM7-11-00067";
        FileService.FileType fileType = FileService.FileType.AUDIO;
        int userId = 1;
        String fileResolution = "fileResolution";

        File result = service.getFile(mediaId, fileType, fileResolution, user);
        assertNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFile_WhenMediaIdIsNull() throws Exception {
        String mediaId = null;
        FileService.FileType fileType = FileService.FileType.AUDIO;
        String fileResolution = "";

        File result = service.getFile(mediaId, fileType, fileResolution, user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFile_WhenFileTypeIsNull() throws Exception {
        String mediaId = "";
        FileService.FileType fileType = null;
        String fileResolution = "";

        File result = service.getFile(mediaId, fileType, fileResolution, user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFile_InvalidMediaId() throws Exception {
        String mediaId = "";
        FileService.FileType fileType = FileService.FileType.AUDIO;
        String fileResolution = "";

        File result = service.getFile(mediaId, fileType, fileResolution, user);
    }
}