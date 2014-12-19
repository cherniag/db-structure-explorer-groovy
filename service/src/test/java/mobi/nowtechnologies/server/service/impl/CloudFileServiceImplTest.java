package mobi.nowtechnologies.server.service.impl;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import mobi.nowtechnologies.server.service.exception.ExternalServiceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CloudFileServiceImplTest {
    @Mock
    FilesClient filesClient;

    @InjectMocks
    CloudFileServiceImpl cloudFileService;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

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
        when(filesClient.copyObject(srcContainerName, srcFileName, targetContainerName, targetFileName))
                .thenThrow(new RuntimeException())
                .thenReturn("result");
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
        when(filesClient.copyObject(srcContainerName, srcFileName, targetContainerName, targetFileName))
                .thenThrow(new RuntimeException())
                .thenThrow(new RuntimeException());
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