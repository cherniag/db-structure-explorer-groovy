package mobi.nowtechnologies.server.trackrepo.service.impl;

import junit.framework.Assert;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.factory.TrackFactory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestSessionClosedException;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/17/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
public class IngestServiceImplTest {
    private IngestServiceImpl fixture;

    @Before
    public void setUp() throws Exception {
        fixture = spy(new IngestServiceImpl());
    }

    @Test
    public void testAddOrUpdateTerritories_NotNullTers_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setTerritories(new HashSet<Territory>());

        List<DropTerritory> dropTerritories = new ArrayList<DropTerritory>();
        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.startdate = new Date();
        dropTerritory.label = "Label1";
        dropTerritory.country = "GB";
        dropTerritories.add(dropTerritory);
        dropTerritory = new DropTerritory();
        dropTerritory.startdate = new Date();
        dropTerritory.label = "Label2";
        dropTerritory.country = "UA";
        dropTerritories.add(dropTerritory);

        doReturn(true).when(fixture).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class));

        boolean result = fixture.addOrUpdateTerritories(track, dropTerritories);

        Assert.assertTrue(result);
        Assert.assertEquals("GB, UA", track.getTerritoryCodes());
        Assert.assertEquals("Label1", track.getLabel());

        verify(fixture, times(2)).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class));
    }

    @Test
    public void testAddOrUpdateTerritories_NullTers_Success() throws Exception {
        Track track = TrackFactory.anyTrack();

        List<DropTerritory> dropTerritories = new ArrayList<DropTerritory>();
        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.startdate = new Date();
        dropTerritory.label = "Label1";
        dropTerritory.country = "GB";
        dropTerritories.add(dropTerritory);
        dropTerritory = new DropTerritory();
        dropTerritory.startdate = new Date();
        dropTerritory.label = "Label2";
        dropTerritory.country = "UA";
        dropTerritories.add(dropTerritory);

        doReturn(true).when(fixture).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class));

        boolean result = fixture.addOrUpdateTerritories(track, dropTerritories);

        Assert.assertTrue(result);
        Assert.assertEquals("GB, UA", track.getTerritoryCodes());
        Assert.assertEquals("Label1", track.getLabel());

        verify(fixture, times(2)).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class));
    }

    @Test
    public void testAddOrUpdateFiles_NullFilesAndAudioTrack_Success() throws Exception {
        Track track = TrackFactory.anyTrack();

        List<DropAssetFile> dropFiles = new ArrayList<DropAssetFile>();
        DropAssetFile dropFile = new DropAssetFile();
        dropFile.file = "path/image";
        dropFile.type = AssetFile.FileType.IMAGE;
        dropFile.isrc = "dffffff";
        dropFiles.add(dropFile);
        DropAssetFile audioDropFile = new DropAssetFile();
        audioDropFile.file = "path/audio";
        audioDropFile.type = AssetFile.FileType.DOWNLOAD;
        audioDropFile.isrc = "dffffff";
        dropFiles.add(audioDropFile);

        boolean result = fixture.addOrUpdateFiles(track, dropFiles, false);

        Assert.assertTrue(result);
        Assert.assertEquals(audioDropFile.file, track.getMediaFile().getPath());
        Assert.assertEquals(dropFile.file, track.getCoverFile().getPath());

        verify(fixture, times(2)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean());
    }

    @Test
    public void testAddOrUpdateFiles_NotNullFilesAndAudioTrack_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setFiles(new HashSet<AssetFile>());

        List<DropAssetFile> dropFiles = new ArrayList<DropAssetFile>();
        DropAssetFile dropFile = new DropAssetFile();
        dropFile.file = "path/image";
        dropFile.type = AssetFile.FileType.IMAGE;
        dropFile.isrc = "dffffff";
        dropFiles.add(dropFile);
        DropAssetFile audioDropFile = new DropAssetFile();
        audioDropFile.file = "path/audio";
        audioDropFile.type = AssetFile.FileType.DOWNLOAD;
        audioDropFile.isrc = "dffffff";
        dropFiles.add(audioDropFile);
        DropAssetFile videoDropFile = new DropAssetFile();
        videoDropFile.file = "path/video";
        videoDropFile.type = AssetFile.FileType.VIDEO;
        videoDropFile.isrc = "dffffff";
        dropFiles.add(videoDropFile);

        boolean result = fixture.addOrUpdateFiles(track, dropFiles, false);

        Assert.assertTrue(result);
        Assert.assertEquals(audioDropFile.file, track.getMediaFile().getPath());
        Assert.assertEquals(audioDropFile.type, track.getMediaType());
        Assert.assertEquals(dropFile.file, track.getCoverFile().getPath());

        verify(fixture, times(3)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean());
    }

    @Test
    public void testAddOrUpdateFiles_NotNullFilesAndVideoTrack_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setFiles(new HashSet<AssetFile>());

        List<DropAssetFile> dropFiles = new ArrayList<DropAssetFile>();
        DropAssetFile dropFile = new DropAssetFile();
        dropFile.file = "path/image";
        dropFile.type = AssetFile.FileType.IMAGE;
        dropFile.isrc = "dffffff";
        dropFiles.add(dropFile);
        DropAssetFile videoDropFile = new DropAssetFile();
        videoDropFile.file = "path/video";
        videoDropFile.type = AssetFile.FileType.VIDEO;
        videoDropFile.isrc = "dffffff";
        dropFiles.add(videoDropFile);

        boolean result = fixture.addOrUpdateFiles(track, dropFiles, false);

        Assert.assertTrue(result);
        Assert.assertEquals(videoDropFile.file, track.getMediaFile().getPath());
        Assert.assertEquals(videoDropFile.type, track.getMediaType());
        Assert.assertEquals(dropFile.file, track.getCoverFile().getPath());

        verify(fixture, times(2)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean());
    }

    @Test
    public void testAddOrUpdateFiles_DontUpdateFile_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setFiles(new HashSet<AssetFile>());
        AssetFile assetFile = new AssetFile();
        assetFile.setPath("path/image");
        assetFile.setType(AssetFile.FileType.IMAGE);
        track.getFiles().add(assetFile);

        List<DropAssetFile> dropFiles = new ArrayList<DropAssetFile>();
        DropAssetFile dropFile = new DropAssetFile();
        dropFile.file = "path/image";
        dropFile.type = AssetFile.FileType.IMAGE;
        dropFile.isrc = "dffffff";
        dropFiles.add(dropFile);
        DropAssetFile videoDropFile = new DropAssetFile();
        videoDropFile.file = "path/video";
        videoDropFile.type = AssetFile.FileType.VIDEO;
        videoDropFile.isrc = "dffffff";
        dropFiles.add(videoDropFile);

        boolean result = fixture.addOrUpdateFiles(track, dropFiles, false);

        Assert.assertFalse(result);
        Assert.assertEquals(null, track.getMediaFile());
        Assert.assertEquals(null, track.getCoverFile());

        verify(fixture, times(1)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean());
    }

    @Test
    public void testAddOrUpdateFile_FileFound_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setFiles(new HashSet<AssetFile>());
        AssetFile assetFile = new AssetFile();
        assetFile.setId(1L);
        assetFile.setPath("path/video");
        assetFile.setDuration(1000);
        assetFile.setType(AssetFile.FileType.VIDEO);

        track.getFiles().add(assetFile);

        DropAssetFile dropFile = new DropAssetFile();
        dropFile.file = "path/video1";
        dropFile.type = AssetFile.FileType.VIDEO;
        dropFile.isrc = "dffffff";
        dropFile.duration = 100;

        boolean result = fixture.addOrUpdateFile(track.getFiles(), dropFile, true);

        Assert.assertTrue(result);
        AssetFile videoFile = track.getFile(AssetFile.FileType.VIDEO);
        Assert.assertNotNull(videoFile.getId());
        Assert.assertEquals(dropFile.duration, videoFile.getDuration());
        Assert.assertEquals(dropFile.file, videoFile.getPath());
    }

    @Test
    public void testAddOrUpdateFile_FileFoundNotUpdate_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setFiles(new HashSet<AssetFile>());
        AssetFile assetFile = new AssetFile();
        assetFile.setId(1L);
        assetFile.setPath("path/video");
        assetFile.setDuration(1000);
        assetFile.setType(AssetFile.FileType.VIDEO);

        track.getFiles().add(assetFile);

        DropAssetFile dropFile = new DropAssetFile();
        dropFile.file = "path/video1";
        dropFile.type = AssetFile.FileType.VIDEO;
        dropFile.isrc = "dffffff";
        dropFile.duration = 100;

        boolean result = fixture.addOrUpdateFile(track.getFiles(), dropFile, false);

        Assert.assertFalse(result);
        AssetFile videoFile = track.getFile(AssetFile.FileType.VIDEO);
        Assert.assertNotNull(videoFile.getId());
        Assert.assertEquals("path/video", videoFile.getPath());
        Assert.assertEquals(1000, videoFile.getDuration().intValue());
    }

    @Test
    public void testAddOrUpdateFile_FileNotFound_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setFiles(new HashSet<AssetFile>());
        AssetFile assetFile = new AssetFile();
        assetFile.setId(1L);
        assetFile.setPath("path/image");
        assetFile.setType(AssetFile.FileType.IMAGE);

        track.getFiles().add(assetFile);

        DropAssetFile dropFile = new DropAssetFile();
        dropFile.file = "path/video1";
        dropFile.type = AssetFile.FileType.VIDEO;
        dropFile.isrc = "dffffff";
        dropFile.duration = 100;

        boolean result = fixture.addOrUpdateFile(track.getFiles(), dropFile, true);

        Assert.assertTrue(result);
        AssetFile videoFile = track.getFile(AssetFile.FileType.VIDEO);
        Assert.assertNull(videoFile.getId());
        Assert.assertEquals(dropFile.duration, videoFile.getDuration());
        Assert.assertEquals(dropFile.file, videoFile.getPath());
    }

    @Test
    public void testAddOrUpdateTerritories_WithTakeDown_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setTerritories(new HashSet<Territory>());

        List<DropTerritory> dropTerritories = new ArrayList<DropTerritory>();
        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.startdate = new Date();
        dropTerritory.label = "Label1";
        dropTerritory.country = "GB";
        dropTerritories.add(dropTerritory);
        dropTerritory = new DropTerritory();
        dropTerritory.startdate = new Date();
        dropTerritory.label = "Label2";
        dropTerritory.country = "UA";
        dropTerritories.add(dropTerritory);

        doReturn(false).when(fixture).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class));

        boolean result = fixture.addOrUpdateTerritories(track, dropTerritories);

        Assert.assertFalse(result);
        Assert.assertEquals("", track.getTerritoryCodes());
        Assert.assertEquals(null, track.getLabel());

        verify(fixture, times(2)).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class));
    }

    @Test
    public void testUpdateIngestData_NullDataAndFreeBuffer_Success() throws Exception {
        IngestWizardData data = null;
        Long curTime = System.currentTimeMillis();
        curTime = curTime - curTime % 100000;

        IngestWizardData result = fixture.updateIngestData(data, false);

        Assert.assertNotNull(result);
        Long suid = new Long(result.getSuid());
        Assert.assertEquals(curTime.longValue(), suid - suid % 100000);
        Assert.assertEquals(1, fixture.ingestDataBuffer.size());
    }

    @Test
    public void testUpdateIngestData_NullDataAndFullBufferWithExpiredData_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        for (int i = 1; i < IngestServiceImpl.MAX_SIZE_DATA_BUFFER; i++) {
            IngestWizardData data = new IngestWizardData();
            data.setSuid(String.valueOf(curTime + i * 1000));
            fixture.ingestDataBuffer.put(data.getSuid(), data);
        }
        IngestWizardData dataExpired = new IngestWizardData();
        dataExpired.setSuid(String.valueOf(curTime - IngestServiceImpl.EXPIRE_PERIOD_BUFFER * 2));
        fixture.ingestDataBuffer.put(dataExpired.getSuid(), dataExpired);

        IngestWizardData data = null;

        IngestWizardData result = fixture.updateIngestData(data, false);

        Assert.assertNull(fixture.ingestDataBuffer.get(dataExpired.getSuid()));
        Assert.assertEquals(IngestServiceImpl.MAX_SIZE_DATA_BUFFER, fixture.ingestDataBuffer.size());
    }

    @Test
    public void testUpdateIngestData_NullDataAndFullBufferWithoutExpiredData_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        for (int i = 1; i <= IngestServiceImpl.MAX_SIZE_DATA_BUFFER; i++) {
            IngestWizardData data = new IngestWizardData();
            data.setSuid(String.valueOf(curTime - i * 1000));
            fixture.ingestDataBuffer.put(data.getSuid(), data);
        }

        IngestWizardData data = null;

        IngestWizardData result = fixture.updateIngestData(data, false);

        Assert.assertNull(fixture.ingestDataBuffer.get(curTime - IngestServiceImpl.MAX_SIZE_DATA_BUFFER * 1000));
        Assert.assertEquals(IngestServiceImpl.MAX_SIZE_DATA_BUFFER, fixture.ingestDataBuffer.size());
    }

    @Test(expected = IngestSessionClosedException.class)
    public void testUpdateIngestData_NotNullDataNotInBuffer_Failure() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data = new IngestWizardData();

        fixture.updateIngestData(data, false);
    }

    @Test
    public void testUpdateIngestData_NotNullDataAndRemoveAfter_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data1 = new IngestWizardData();
        data1.setSuid(String.valueOf(curTime - 1000));
        fixture.ingestDataBuffer.put(data1.getSuid(), data1);

        IngestWizardData data = new IngestWizardData();
        data.setSuid(data1.getSuid());

        IngestWizardData result = fixture.updateIngestData(data, true);

        Assert.assertSame(data1, result);
        Assert.assertEquals(0, fixture.ingestDataBuffer.size());
    }

    @Test
    public void testUpdateIngestData_NotNullDataAndNotRemoveAfter_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data1 = new IngestWizardData();
        data1.setSuid(String.valueOf(curTime - 1000));
        fixture.ingestDataBuffer.put(data1.getSuid(), data1);

        IngestWizardData data = new IngestWizardData();
        data.setSuid(data1.getSuid());

        IngestWizardData result = fixture.updateIngestData(data, false);

        Assert.assertSame(data1, result);
        Assert.assertEquals(1, fixture.ingestDataBuffer.size());
    }
}
