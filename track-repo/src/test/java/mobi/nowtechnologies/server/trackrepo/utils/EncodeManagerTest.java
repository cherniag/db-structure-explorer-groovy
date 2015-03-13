package mobi.nowtechnologies.server.trackrepo.utils;

import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.utils.image.ImageGenerator;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.IMAGE;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import org.springframework.core.io.Resource;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EncodeManager.class)
public class EncodeManagerTest {

    @Mock
    File workDirMock;
    @Mock
    Resource workDirResourceMock;
    @Mock
    ImageGenerator imageGeneratorMock;
    @Mock
    UploadToCloudFileManager cloudUploadFileManagerMock;
    @InjectMocks
    EncodeManager encodeManager;

    @Test
    public void shouldDeleteTrackFilesAfterEncode() throws Exception {
        //given
        Track trackMock = mock(Track.class);
        boolean isHighRate = false;
        boolean licensed = false;
        final String uniqueTrackId = "uniqueTrackId_222";

        when(trackMock.isVideo()).thenReturn(true);
        when(trackMock.getFileName(IMAGE)).thenReturn("image");
        when(trackMock.getUniqueTrackId()).thenReturn(uniqueTrackId);

        final String workDirAbsoluteFilePath = "workDir" + File.separator + "absolute" + File.separator + "path";

        when(workDirResourceMock.getFile()).thenReturn(workDirMock);
        when(workDirMock.getAbsolutePath()).thenReturn(workDirAbsoluteFilePath);

        File tmpDirFileMock = mock(File.class);

        Path tmpDirPathMock = mock(Path.class);
        when(tmpDirPathMock.toFile()).thenReturn(tmpDirFileMock);
        when(tmpDirFileMock.toPath()).thenReturn(tmpDirPathMock);

        Path workDirPathMock = mock(Path.class);
        mockStatic(Paths.class);
        when(Paths.get(workDirAbsoluteFilePath + File.separator)).thenReturn(workDirPathMock);

        mockStatic(Files.class);
        when(Files.createTempDirectory(workDirPathMock, String.format("%s_", uniqueTrackId))).thenReturn(tmpDirPathMock);

        mockStatic(FileUtils.class);
        when(FileUtils.deleteQuietly(tmpDirFileMock)).thenReturn(true);

        encodeManager.setWorkDir(workDirResourceMock);

        //when
        encodeManager.encode(trackMock, isHighRate, licensed);

        //then
        verify(cloudUploadFileManagerMock, times(1)).uploadFilesToCloud(eq(trackMock), anyList(), anyList());
        verifyStatic();
    }
}