package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.TimeService;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.factory.TrackFactory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestSessionClosedException;
import mobi.nowtechnologies.server.trackrepo.ingest.IngestWizardData;
import mobi.nowtechnologies.server.trackrepo.ingest.Ingestor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;

/**
 * User: Alexsandr_Kolpakov Date: 7/17/13 Time: 12:25 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class IngestServiceImplTest {

    @Mock
    TimeService timeServiceMock;

    @InjectMocks
    @Spy
    IngestServiceImpl ingestServiceSpy;

    @Test
    public void testAddOrUpdateTerritories_NotNullTers_Success() throws Exception {
        Track track = TrackFactory.anyTrack();
        track.setTerritories(new HashSet<Territory>());

        List<DropTerritory> dropTerritories = new ArrayList<>();
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

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        doReturn(true).when(ingestServiceSpy).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class), any(Ingestor.class));

        boolean result = ingestServiceSpy.addOrUpdateTerritories(track, dropTerritories, ingestor);

        assertTrue(result);
        Assert.assertEquals("GB, UA", track.getTerritoryCodes());
        Assert.assertEquals("Label1", track.getLabel());

        verify(ingestServiceSpy, times(2)).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class), any(Ingestor.class));
    }

    @Test
    public void testAddOrUpdateTerritories_NullTers_Success() throws Exception {
        Track track = TrackFactory.anyTrack();

        List<DropTerritory> dropTerritories = new ArrayList<>();
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

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        doReturn(true).when(ingestServiceSpy).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class), any(Ingestor.class));

        boolean result = ingestServiceSpy.addOrUpdateTerritories(track, dropTerritories, ingestor);

        assertTrue(result);
        Assert.assertEquals("GB, UA", track.getTerritoryCodes());
        Assert.assertEquals("Label1", track.getLabel());

        verify(ingestServiceSpy, times(2)).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class), any(Ingestor.class));
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

        boolean result = ingestServiceSpy.addOrUpdateFiles(track, dropFiles, false, Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13);

        assertTrue(result);
        Assert.assertEquals(audioDropFile.file, track.getMediaFile().getPath());
        Assert.assertEquals(dropFile.file, track.getCoverFile().getPath());

        verify(ingestServiceSpy, times(2)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean(), eq(Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13));
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

        boolean result = ingestServiceSpy.addOrUpdateFiles(track, dropFiles, false, Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13);

        assertTrue(result);
        Assert.assertEquals(audioDropFile.file, track.getMediaFile().getPath());
        Assert.assertEquals(audioDropFile.type, track.getMediaType());
        Assert.assertEquals(dropFile.file, track.getCoverFile().getPath());

        verify(ingestServiceSpy, times(3)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean(), eq(Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13));
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

        boolean result = ingestServiceSpy.addOrUpdateFiles(track, dropFiles, false, Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13);

        assertTrue(result);
        Assert.assertEquals(videoDropFile.file, track.getMediaFile().getPath());
        Assert.assertEquals(videoDropFile.type, track.getMediaType());
        Assert.assertEquals(dropFile.file, track.getCoverFile().getPath());

        verify(ingestServiceSpy, times(2)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean(), eq(Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13));
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

        boolean result = ingestServiceSpy.addOrUpdateFiles(track, dropFiles, false, Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13);

        assertFalse(result);
        Assert.assertEquals(null, track.getMediaFile());
        Assert.assertEquals(null, track.getCoverFile());

        verify(ingestServiceSpy, times(1)).addOrUpdateFile(any(Set.class), any(DropAssetFile.class), anyBoolean(), eq(Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13));
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

        boolean result = ingestServiceSpy.addOrUpdateFile(track.getFiles(), dropFile, true, Ingestor.WARNER);

        assertTrue(result);
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

        boolean result = ingestServiceSpy.addOrUpdateFile(track.getFiles(), dropFile, false, Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13);

        assertFalse(result);
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

        boolean result = ingestServiceSpy.addOrUpdateFile(track.getFiles(), dropFile, true, Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13);

        assertTrue(result);
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

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        doReturn(false).when(ingestServiceSpy).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class), any(Ingestor.class));

        boolean result = ingestServiceSpy.addOrUpdateTerritories(track, dropTerritories, ingestor);

        assertFalse(result);
        Assert.assertEquals("", track.getTerritoryCodes());
        Assert.assertEquals(null, track.getLabel());

        verify(ingestServiceSpy, times(2)).addOrUpdateTerritory(any(Set.class), any(DropTerritory.class), any(Ingestor.class));
    }

    @Test
    public void testUpdateIngestData_NullDataAndFreeBuffer_Success() throws Exception {
        IngestWizardData data = null;
        Long curTime = System.currentTimeMillis();
        curTime = curTime - curTime % 100000;

        IngestWizardData result = ingestServiceSpy.updateIngestData(data, false);

        Assert.assertNotNull(result);
        Long suid = new Long(result.getSuid());
        Assert.assertEquals(curTime.longValue(), suid - suid % 100000);
        Assert.assertEquals(1, ingestServiceSpy.ingestDataBuffer.size());
    }

    @Test
    public void testUpdateIngestData_NullDataAndFullBufferWithExpiredData_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        for (int i = 1; i < IngestServiceImpl.MAX_SIZE_DATA_BUFFER; i++) {
            IngestWizardData data = new IngestWizardData();
            data.setSuid(String.valueOf(curTime + i * 1000));
            ingestServiceSpy.ingestDataBuffer.put(data.getSuid(), data);
        }
        IngestWizardData dataExpired = new IngestWizardData();
        dataExpired.setSuid(String.valueOf(curTime - IngestServiceImpl.EXPIRE_PERIOD_BUFFER * 2));
        ingestServiceSpy.ingestDataBuffer.put(dataExpired.getSuid(), dataExpired);

        IngestWizardData data = null;

        IngestWizardData result = ingestServiceSpy.updateIngestData(data, false);

        Assert.assertNull(ingestServiceSpy.ingestDataBuffer.get(dataExpired.getSuid()));
        Assert.assertEquals(IngestServiceImpl.MAX_SIZE_DATA_BUFFER, ingestServiceSpy.ingestDataBuffer.size());
    }

    @Test
    public void testUpdateIngestData_NullDataAndFullBufferWithoutExpiredData_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        for (int i = 1; i <= IngestServiceImpl.MAX_SIZE_DATA_BUFFER; i++) {
            IngestWizardData data = new IngestWizardData();
            data.setSuid(String.valueOf(curTime - i * 1000));
            ingestServiceSpy.ingestDataBuffer.put(data.getSuid(), data);
        }

        IngestWizardData data = null;

        IngestWizardData result = ingestServiceSpy.updateIngestData(data, false);

        Assert.assertNull(ingestServiceSpy.ingestDataBuffer.get(curTime - IngestServiceImpl.MAX_SIZE_DATA_BUFFER * 1000));
        Assert.assertEquals(IngestServiceImpl.MAX_SIZE_DATA_BUFFER, ingestServiceSpy.ingestDataBuffer.size());
    }

    @Test(expected = IngestSessionClosedException.class)
    public void testUpdateIngestData_NotNullDataNotInBuffer_Failure() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data = new IngestWizardData();

        ingestServiceSpy.updateIngestData(data, false);
    }

    @Test
    public void testUpdateIngestData_NotNullDataAndRemoveAfter_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data1 = new IngestWizardData();
        data1.setSuid(String.valueOf(curTime - 1000));
        ingestServiceSpy.ingestDataBuffer.put(data1.getSuid(), data1);

        IngestWizardData data = new IngestWizardData();
        data.setSuid(data1.getSuid());

        IngestWizardData result = ingestServiceSpy.updateIngestData(data, true);

        Assert.assertSame(data1, result);
        Assert.assertEquals(0, ingestServiceSpy.ingestDataBuffer.size());
    }

    @Test
    public void testUpdateIngestData_NotNullDataAndNotRemoveAfter_Success() throws Exception {
        Long curTime = System.currentTimeMillis();
        IngestWizardData data1 = new IngestWizardData();
        data1.setSuid(String.valueOf(curTime - 1000));
        ingestServiceSpy.ingestDataBuffer.put(data1.getSuid(), data1);

        IngestWizardData data = new IngestWizardData();
        data.setSuid(data1.getSuid());

        IngestWizardData result = ingestServiceSpy.updateIngestData(data, false);

        Assert.assertSame(data1, result);
        Assert.assertEquals(1, ingestServiceSpy.ingestDataBuffer.size());
    }

    @Test
     public void shouldReturnTrueWithoutTerritoriesChangesWhenDropTerritoryDoesNotHaveCountry() {
        //given
        Set<Territory> territories = Collections.emptySet();

        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.country = null;

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        //when
        boolean addOrUpdateTerritory = ingestServiceSpy.addOrUpdateTerritory(territories, dropTerritory, ingestor);

        //then
        assertThat(addOrUpdateTerritory, is(true));
        assertThat(territories.size(), is(0));
    }

    @Test
    public void shouldReturnTrueAndAddNewTerritoryInToTerritoriesWhenNoSuchCountryInTerritories() {
        //given
        Set<Territory> territories = new HashSet<>();

        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.country = "country";
        dropTerritory.distributor = "distributor";
        dropTerritory.label = "label";
        dropTerritory.publisher = "publisher";
        dropTerritory.currency = "currency";
        dropTerritory.price = Float.MAX_VALUE;
        dropTerritory.startdate = new Date(0);
        dropTerritory.reportingId = "reportingId";
        dropTerritory.dealReference = "dealReference";

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        Date dateMock = mock(Date.class);
        when(timeServiceMock.now()).thenReturn(dateMock);

        //when
        boolean addOrUpdateTerritory = ingestServiceSpy.addOrUpdateTerritory(territories, dropTerritory, ingestor);

        //then
        assertThat(addOrUpdateTerritory, is(true));
        assertThat(territories.size(), is(1));

        Territory territory = territories.iterator().next();
        assertThat(territory.getCode(), is(dropTerritory.country));
        assertThat(territory.getCreateDate(), is(dateMock));
        assertThat(territory.getDistributor(), is(dropTerritory.distributor));
        assertThat(territory.getPublisher(), is(dropTerritory.publisher));
        assertThat(territory.getLabel(), is(dropTerritory.label));
        assertThat(territory.getCurrency(), is(dropTerritory.currency));
        assertThat(territory.getPrice(), is(dropTerritory.price));
        assertThat(territory.getStartDate(), is(dropTerritory.startdate));
        assertThat(territory.getReportingId(), is(dropTerritory.reportingId));
        assertThat(territory.getPriceCode(), is(dropTerritory.priceCode));
        assertThat(territory.getDealReference(), is(dropTerritory.dealReference));
        assertThat(territory.isDeleted(), is(false));
        assertThat(territory.getDeleteDate(), is(nullValue()));
    }

    @Test
    public void shouldReturnTrueAndModifyTerritoryInTerritoriesWhenNoSuchCountryInTerritories() {
        //given
        Date terrCreateDate = new Date();

        Territory terr = new Territory();
        terr.setCode("country");
        terr.setCreateDate(terrCreateDate);

        Set<Territory> territories = new HashSet<>();
        territories.add(terr);

        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.country = terr.getCode();
        dropTerritory.distributor = "distributor";
        dropTerritory.label = "label";
        dropTerritory.publisher = "publisher";
        dropTerritory.currency = "currency";
        dropTerritory.price = Float.MAX_VALUE;
        dropTerritory.startdate = new Date(0);
        dropTerritory.reportingId = "reportingId";
        dropTerritory.dealReference = "dealReference";

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        Date dateMock = mock(Date.class);
        when(timeServiceMock.now()).thenReturn(dateMock);

        //when
        boolean addOrUpdateTerritory = ingestServiceSpy.addOrUpdateTerritory(territories, dropTerritory, ingestor);

        //then
        assertThat(addOrUpdateTerritory, is(true));
        assertThat(territories.size(), is(1));

        Territory territory = territories.iterator().next();
        assertThat(territory.getCode(), is(dropTerritory.country));
        assertThat(territory.getCreateDate(), is(terrCreateDate));
        assertThat(territory.getDistributor(), is(dropTerritory.distributor));
        assertThat(territory.getPublisher(), is(dropTerritory.publisher));
        assertThat(territory.getLabel(), is(dropTerritory.label));
        assertThat(territory.getCurrency(), is(dropTerritory.currency));
        assertThat(territory.getPrice(), is(dropTerritory.price));
        assertThat(territory.getStartDate(), is(dropTerritory.startdate));
        assertThat(territory.getReportingId(), is(dropTerritory.reportingId));
        assertThat(territory.getPriceCode(), is(dropTerritory.priceCode));
        assertThat(territory.getDealReference(), is(dropTerritory.dealReference));
        assertThat(territory.isDeleted(), is(false));
        assertThat(territory.getDeleteDate(), is(nullValue()));
    }

    @Test
    public void shouldReturnTrueAndModifyTerritoryInTerritoriesWhenSuchCountryAlreadyExistsAndTakeDownIsFalse() {
        //given
        Date terrCreateDate = new Date();

        Territory terr = new Territory();
        terr.setCode("country");
        terr.setCreateDate(terrCreateDate);

        Set<Territory> territories = new HashSet<>();
        territories.add(terr);

        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.country = terr.getCode();
        dropTerritory.distributor = "distributor";
        dropTerritory.label = "label";
        dropTerritory.publisher = "publisher";
        dropTerritory.currency = "currency";
        dropTerritory.price = Float.MAX_VALUE;
        dropTerritory.startdate = new Date(0);
        dropTerritory.reportingId = "reportingId";
        dropTerritory.dealReference = "dealReference";
        dropTerritory.takeDown = false;

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        Date dateMock = mock(Date.class);
        when(timeServiceMock.now()).thenReturn(dateMock);

        //when
        boolean addOrUpdateTerritory = ingestServiceSpy.addOrUpdateTerritory(territories, dropTerritory, ingestor);

        //then
        assertThat(addOrUpdateTerritory, is(true));
        assertThat(territories.size(), is(1));

        Territory territory = territories.iterator().next();
        assertThat(territory.getCode(), is(dropTerritory.country));
        assertThat(territory.getCreateDate(), is(terrCreateDate));
        assertThat(territory.getDistributor(), is(dropTerritory.distributor));
        assertThat(territory.getPublisher(), is(dropTerritory.publisher));
        assertThat(territory.getLabel(), is(dropTerritory.label));
        assertThat(territory.getCurrency(), is(dropTerritory.currency));
        assertThat(territory.getPrice(), is(dropTerritory.price));
        assertThat(territory.getStartDate(), is(dropTerritory.startdate));
        assertThat(territory.getReportingId(), is(dropTerritory.reportingId));
        assertThat(territory.getPriceCode(), is(dropTerritory.priceCode));
        assertThat(territory.getDealReference(), is(dropTerritory.dealReference));
        assertThat(territory.isDeleted(), is(false));
        assertThat(territory.getDeleteDate(), is(nullValue()));
    }

    @Test
    public void shouldReturnTrueAndModifyTerritoryInTerritoriesWhenSuchCountryAlreadyExistsAndTakeDownIsTrue() {
        //given
        Date terrCreateDate = new Date();
        String distributor = "";
        String publisher = "";
        String label = "";
        String currency = "";
        float price = Float.MIN_VALUE;
        Date startDate = new Date();
        String reportingId = "";
        String priceCode = "";
        String dealReference = "";

        Territory terr = new Territory();
        terr.setCode("country");
        terr.setCreateDate(terrCreateDate);
        terr.setDistributor(distributor);
        terr.setPublisher(publisher);
        terr.setLabel(label);
        terr.setCurrency(currency);
        terr.setPrice(price);
        terr.setStartDate(startDate);
        terr.setReportingId(reportingId);
        terr.setPriceCode(priceCode);
        terr.setDealReference(dealReference);

        Set<Territory> territories = new HashSet<>();
        territories.add(terr);

        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.country = terr.getCode();
        dropTerritory.distributor = "distributor";
        dropTerritory.label = "label";
        dropTerritory.publisher = "publisher";
        dropTerritory.currency = "currency";
        dropTerritory.price = Float.MAX_VALUE;
        dropTerritory.startdate = new Date(0);
        dropTerritory.reportingId = "reportingId";
        dropTerritory.dealReference = "dealReference";
        dropTerritory.takeDown = true;

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        Date dateMock = mock(Date.class);
        when(timeServiceMock.now()).thenReturn(dateMock);

        //when
        boolean addOrUpdateTerritory = ingestServiceSpy.addOrUpdateTerritory(territories, dropTerritory, ingestor);

        //then
        assertThat(addOrUpdateTerritory, is(false));
        assertThat(territories.size(), is(1));

        Territory territory = territories.iterator().next();
        assertThat(territory.getCode(), is(dropTerritory.country));
        assertThat(territory.getCreateDate(), is(terrCreateDate));
        assertThat(territory.getDistributor(), is(distributor));
        assertThat(territory.getPublisher(), is(publisher));
        assertThat(territory.getLabel(), is(label));
        assertThat(territory.getCurrency(), is(currency));
        assertThat(territory.getPrice(), is(price));
        assertThat(territory.getStartDate(), is(startDate));
        assertThat(territory.getReportingId(), is(reportingId));
        assertThat(territory.getPriceCode(), is(priceCode));
        assertThat(territory.getDealReference(), is(dealReference));
        assertThat(territory.isDeleted(), is(true));
        assertThat(territory.getDeleteDate(), is(dateMock));
    }

    @Test
    public void shouldReturnTrueAndModifyTerritoryInTerritoriesWhenTerritoryIsWorldWideAndIngestorIsUNIVERSALAndTakeDownIsTrue() {
        //given
        Date terrCreateDate = new Date();
        String distributor = "";
        String publisher = "";
        String label = "";
        String currency = "";
        float price = Float.MIN_VALUE;
        Date startDate = new Date();
        String reportingId = "";
        String priceCode = "";
        String dealReference = "";
        String country = "country";

        Territory terr = new Territory();
        terr.setCode(country);
        terr.setCreateDate(terrCreateDate);
        terr.setDistributor(distributor);
        terr.setPublisher(publisher);
        terr.setLabel(label);
        terr.setCurrency(currency);
        terr.setPrice(price);
        terr.setStartDate(startDate);
        terr.setReportingId(reportingId);
        terr.setPriceCode(priceCode);
        terr.setDealReference(dealReference);

        Set<Territory> territories = new HashSet<>();
        territories.add(terr);

        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.country = "WorldWide";
        dropTerritory.distributor = "distributor";
        dropTerritory.label = "label";
        dropTerritory.publisher = "publisher";
        dropTerritory.currency = "currency";
        dropTerritory.price = Float.MAX_VALUE;
        dropTerritory.startdate = new Date(0);
        dropTerritory.reportingId = "reportingId";
        dropTerritory.dealReference = "dealReference";
        dropTerritory.takeDown = true;

        Ingestor ingestor = Ingestor.UNIVERSAL_DDEX_3_7_ASSET_AND_METADATA_1_13;

        Date dateMock = mock(Date.class);
        when(timeServiceMock.now()).thenReturn(dateMock);

        //when
        boolean addOrUpdateTerritory = ingestServiceSpy.addOrUpdateTerritory(territories, dropTerritory, ingestor);

        //then
        assertThat(addOrUpdateTerritory, is(true));
        assertThat(territories.size(), is(2));

        for (Territory territory : territories) {
            if (territory.getCode().equals(country)) {
                assertThat(territory.getCreateDate(), is(terrCreateDate));
                assertThat(territory.getDistributor(), is(distributor));
                assertThat(territory.getPublisher(), is(publisher));
                assertThat(territory.getLabel(), is(label));
                assertThat(territory.getCurrency(), is(currency));
                assertThat(territory.getPrice(), is(price));
                assertThat(territory.getStartDate(), is(startDate));
                assertThat(territory.getReportingId(), is(reportingId));
                assertThat(territory.getPriceCode(), is(priceCode));
                assertThat(territory.getDealReference(), is(dealReference));
                assertThat(territory.isDeleted(), is(false));
                assertThat(territory.getDeleteDate(), is(nullValue()));
            }else{
                assertThat(territory.getCode(), is("WorldWide"));
                assertThat(territory.getCreateDate(), is(dateMock));
                assertThat(territory.getDistributor(), is(dropTerritory.distributor));
                assertThat(territory.getPublisher(), is(dropTerritory.publisher));
                assertThat(territory.getLabel(), is(dropTerritory.label));
                assertThat(territory.getCurrency(), is(dropTerritory.currency));
                assertThat(territory.getPrice(), is(dropTerritory.price));
                assertThat(territory.getStartDate(), is(dropTerritory.startdate));
                assertThat(territory.getReportingId(), is(dropTerritory.reportingId));
                assertThat(territory.getPriceCode(), is(dropTerritory.priceCode));
                assertThat(territory.getDealReference(), is(dropTerritory.dealReference));
                assertThat(territory.isDeleted(), is(false));
                assertThat(territory.getDeleteDate(), is(nullValue()));
            }
        }
    }

    @Test
    public void shouldReturnFalseAndModifyAllTerritoriesInTerritoriesWhenTerritoryIsWorldWideAndIngestorIsEMI_UMGAndTakeDownIsTrue() {
        //given
        Date terrCreateDate = new Date();
        String country = "country";
        String distributor = "";
        String publisher = "";
        String label = "";
        String currency = "";
        float price = Float.MIN_VALUE;
        Date startDate = new Date();
        String reportingId = "";
        String priceCode = "";
        String dealReference = "";

        Territory terr = new Territory();
        terr.setCode(country);
        terr.setCreateDate(terrCreateDate);
        terr.setDistributor(distributor);
        terr.setPublisher(publisher);
        terr.setLabel(label);
        terr.setCurrency(currency);
        terr.setPrice(price);
        terr.setStartDate(startDate);
        terr.setReportingId(reportingId);
        terr.setPriceCode(priceCode);
        terr.setDealReference(dealReference);

        Set<Territory> territories = new HashSet<>();
        territories.add(terr);

        DropTerritory dropTerritory = new DropTerritory();
        dropTerritory.country = "WorldWide";
        dropTerritory.distributor = "distributor";
        dropTerritory.label = "label";
        dropTerritory.publisher = "publisher";
        dropTerritory.currency = "currency";
        dropTerritory.price = Float.MAX_VALUE;
        dropTerritory.startdate = new Date(0);
        dropTerritory.reportingId = "reportingId";
        dropTerritory.dealReference = "dealReference";
        dropTerritory.takeDown = true;

        Ingestor ingestor = Ingestor.EMI_UMG;

        Date dateMock = mock(Date.class);
        when(timeServiceMock.now()).thenReturn(dateMock);

        //when
        boolean addOrUpdateTerritory = ingestServiceSpy.addOrUpdateTerritory(territories, dropTerritory, ingestor);

        //then
        assertThat(addOrUpdateTerritory, is(false));
        assertThat(territories.size(), is(1));

        Territory territory = territories.iterator().next();
        assertThat(territory.getCode(), is(country));
        assertThat(territory.getCreateDate(), is(terrCreateDate));
        assertThat(territory.getDistributor(), is(distributor));
        assertThat(territory.getPublisher(), is(publisher));
        assertThat(territory.getLabel(), is(label));
        assertThat(territory.getCurrency(), is(currency));
        assertThat(territory.getPrice(), is(price));
        assertThat(territory.getStartDate(), is(startDate));
        assertThat(territory.getReportingId(), is(reportingId));
        assertThat(territory.getPriceCode(), is(priceCode));
        assertThat(territory.getDealReference(), is(dealReference));
        assertThat(territory.isDeleted(), is(true));
        assertThat(territory.getDeleteDate(), is(dateMock));
    }
}
