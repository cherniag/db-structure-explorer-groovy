package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.service.exception.ExternalServiceException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

import com.rackspacecloud.client.cloudfiles.FilesClient;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CloudFileServiceImplTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    @Mock
    FilesClient filesClient;
    @InjectMocks
    CloudFileServiceImpl cloudFileService;

    @Before
    public void setUp() throws Exception {
        when(filesClient.login()).thenReturn(true);

        cloudFileService.setCopyFileOnCloudAttemptCount(1);
    }

    @Test
    public void copyFileOnCloud() throws Exception {
        String srcContainerName = "srcContainerName";
        String srcFileName = "srcFileName";
        String targetContainerName = "targetContainerName";
        String targetFileName = "targetFileName";

        boolean copyFile = cloudFileService.copyFile(srcContainerName, srcFileName, targetContainerName, targetFileName);

        assertTrue(copyFile);
        verify(filesClient).copyObject(srcContainerName, srcFileName, targetContainerName, targetFileName);
    }

    @Test
    public void copyFileOnCloudIfFirstAttemptsFailed() throws Exception {
        String srcContainerName = "srcContainerName";
        String srcFileName = "srcFileName";
        String targetContainerName = "targetContainerName";
        String targetFileName = "targetFileName";
        when(filesClient.copyObject(srcContainerName, srcFileName, targetContainerName, targetFileName)).thenThrow(new RuntimeException()).thenReturn("result");
        cloudFileService.setCopyFileOnCloudAttemptCount(2);

        boolean copyFile = cloudFileService.copyFile(srcContainerName, srcFileName, targetContainerName, targetFileName);

        assertTrue(copyFile);
        verify(filesClient, times(2)).copyObject(srcContainerName, srcFileName, targetContainerName, targetFileName);
    }

    @Test(expected = ExternalServiceException.class)
    public void copyFileOnCloudIfAllAttemptsFailed() throws Exception {
        String srcContainerName = "srcContainerName";
        String srcFileName = "srcFileName";
        String targetContainerName = "targetContainerName";
        String targetFileName = "targetFileName";
        when(filesClient.copyObject(srcContainerName, srcFileName, targetContainerName, targetFileName)).thenThrow(new RuntimeException()).thenThrow(new RuntimeException());
        cloudFileService.setCopyFileOnCloudAttemptCount(2);

        cloudFileService.copyFile(srcContainerName, srcFileName, targetContainerName, targetFileName);
    }

    @Test
    public void uploadFileOnCloud() throws Exception {
        File file = testFolder.newFile("music.mp3");
        String fileName = "fileName";
        String contentType = "contentType";
        String destinationContainer = "destinationContainer";

        boolean uploadFile = cloudFileService.uploadFile(file, fileName, contentType, destinationContainer);

        assertTrue(uploadFile);
        verify(filesClient).storeStreamedObject(eq(destinationContainer), any(FileInputStream.class), eq(contentType), eq(fileName), eq(Collections.<String, String>emptyMap()));
    }

    @Test(expected = ExternalServiceException.class)
    public void uploadFileOnCloudIfAllAttemptsFailed() throws Exception {
        File file = testFolder.newFile("music.mp3");
        String fileName = "fileName";
        String contentType = "contentType";
        String destinationContainer = "destinationContainer";
        when(filesClient.storeStreamedObject(eq(destinationContainer), any(FileInputStream.class), eq(contentType), eq(fileName), eq(Collections.<String, String>emptyMap())))
            .thenThrow(new RuntimeException());

        cloudFileService.uploadFile(file, fileName, contentType, destinationContainer);
    }
}