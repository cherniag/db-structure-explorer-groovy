package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/10/13
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
public class UniversalParserTest {
    private UniversalParser fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new UniversalParser("classpath:media/universal_cdu/");
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsNotTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(false, result.get("ROCRP1002941").explicit);
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(true, result.get("GBSXS1100209").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsNotTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(false, result.get("ROCRP1002941").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(true, result.get("GBSXS1100209").explicit);
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsEmptyTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(true, result.get("ROCRP1002948").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsEmptyTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(false, result.get("ROCRP1002948").explicit);
    }

    @Test
    public void testLoadXml_Audio_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(8, result.size());

        DropTrack track = result.get("ROCRP1002948");

        Assert.assertEquals(DropTrack.Type.INSERT, track.type);
        Assert.assertEquals("05037128167051", track.productCode);
        Assert.assertEquals("Get Back (ASAP)", track.title);
        Assert.assertEquals("Extended Mix", track.subTitle);
        Assert.assertEquals("Alexandra Stan", track.artist);
        Assert.assertEquals("Dance", track.genre);
        Assert.assertEquals("(C) 2011 3Beat Productions Limited, under Exclusive License to All Around The World Ltd.", track.copyright);
        Assert.assertEquals("ROCRP1002948", track.isrc);
        Assert.assertEquals("2011", track.year);
        Assert.assertEquals(null, track.label);
        Assert.assertEquals("05037128167051", track.physicalProductId);
        Assert.assertEquals(null, track.album);
        Assert.assertEquals(null, track.info);
        Assert.assertEquals(true, track.licensed);
        Assert.assertEquals(false, track.exists);
        Assert.assertEquals(false, track.explicit);
        Assert.assertEquals("05037128167051", track.productId);

        Assert.assertEquals(1, track.territories.size());

        DropTerritory territory = track.territories.get(0);
        Assert.assertEquals("GB", territory.country);
        Assert.assertEquals("Universal Music TV", territory.label);
        Assert.assertEquals(null, territory.currency);
        Assert.assertEquals(null, territory.price);
        Assert.assertEquals("ROCRP1002948", territory.reportingId);
        Assert.assertEquals("Universal Music TV Campaign Division", territory.distributor);
        Assert.assertEquals(false, territory.takeDown);
        Assert.assertEquals("STAP", territory.priceCode);
        Assert.assertEquals(null, territory.dealReference);
        Assert.assertEquals(null, territory.publisher);
    }

    @Test
    public void testLoadXml_Video_Success() throws Exception {
        String code = "00602537560646";
        String drop = "3000016480865";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        Assert.assertEquals(1, result.size());

        DropTrack track = result.get("FRUV71300321");

        Assert.assertEquals(DropTrack.Type.INSERT, track.type);
        Assert.assertEquals("00602537560646", track.productCode);
        Assert.assertEquals("Jamaïque", track.title);
        Assert.assertEquals("", track.subTitle);
        Assert.assertEquals("Psy 4 De La Rime", track.artist);
        Assert.assertEquals("Rap", track.genre);
        Assert.assertEquals("(C) 2013 Def Jam Recordings France", track.copyright);
        Assert.assertEquals("FRUV71300321", track.isrc);
        Assert.assertEquals("2013", track.year);
        Assert.assertEquals(null, track.label);
        Assert.assertEquals("00602537560646", track.physicalProductId);
        Assert.assertEquals(null, track.album);
        Assert.assertEquals(null, track.info);
        Assert.assertEquals(true, track.licensed);
        Assert.assertEquals(false, track.exists);
        Assert.assertEquals(false, track.explicit);
        Assert.assertEquals("00602537560646", track.productId);

        Assert.assertEquals(1, track.territories.size());

        DropTerritory territory = track.territories.get(0);
        Assert.assertEquals("GB", territory.country);
        Assert.assertEquals("UMC", territory.label);
        Assert.assertEquals("09-Sep-2013", fixture.dateFormat.format(territory.startdate));
        Assert.assertEquals(null, territory.currency);
        Assert.assertEquals(null, territory.price);
        Assert.assertEquals("FRUV71300321", territory.reportingId);
        Assert.assertEquals("UMC (Universal Music Catalogue)", territory.distributor);
        Assert.assertEquals(false, territory.takeDown);
        Assert.assertEquals("STVTP", territory.priceCode);
        Assert.assertEquals(null, territory.dealReference);
        Assert.assertEquals(null, territory.publisher);
    }

    @Test
    public void testIngest_Audio_Success() throws Exception {
        DropData drop = new DropData();
        drop.name = "3000007191631";
        drop.date = new Date();

        Map<String, DropTrack> result = fixture.ingest(drop);

        Assert.assertEquals(8, result.size());

        DropTrack track = result.get("ROCRP1002948");

        Assert.assertEquals(4, track.files.size());

        DropAssetFile downloadFile = track.files.get(0);
        Assert.assertTrue(downloadFile.file.endsWith("UMG_audtrk_05037128167051_01_003_185.mp3"));
        Assert.assertEquals(AssetFile.FileType.DOWNLOAD, downloadFile.type);
        Assert.assertEquals("9ac82d0a3fb1dc43aef97dcf2c28b3e3", downloadFile.md5);
        Assert.assertEquals("ROCRP1002948", downloadFile.isrc);
        Assert.assertEquals(266000, downloadFile.duration.intValue());

        DropAssetFile previewFile = track.files.get(1);
        Assert.assertTrue(previewFile.file.endsWith("UMG_audclp_05037128167051_01_003_1129.mp4"));
        Assert.assertEquals(AssetFile.FileType.PREVIEW, previewFile.type);
        Assert.assertEquals("f3623b0b4bd4ae98a77fe8554ae3f7ef", previewFile.md5);
        Assert.assertEquals("ROCRP1002948", previewFile.isrc);
        Assert.assertEquals(266000, previewFile.duration.intValue());

        DropAssetFile mobileFile = track.files.get(2);
        Assert.assertTrue(mobileFile.file.endsWith("UMG_audtrk_05037128167051_01_003_1129.mp4"));
        Assert.assertEquals(AssetFile.FileType.MOBILE, mobileFile.type);
        Assert.assertEquals("8908fb2efc6fe2954537c8a1745163f6", mobileFile.md5);
        Assert.assertEquals("ROCRP1002948", mobileFile.isrc);
        Assert.assertEquals(266000, mobileFile.duration.intValue());

        DropAssetFile imageFile = track.files.get(3);
        Assert.assertTrue(imageFile.file.endsWith("UMG_cvrart_05037128167051_01_RGB72_1200x1200_10452455688.jpg"));
        Assert.assertEquals(AssetFile.FileType.IMAGE, imageFile.type);
        Assert.assertEquals("1acbb74d56b81ab6e1b4c0bdee6f0c3e", imageFile.md5);
        Assert.assertEquals(null, imageFile.isrc);
        Assert.assertEquals(null, imageFile.duration);
    }

    @Test
    public void testIngest_Video_Success() throws Exception {
        DropData drop = new DropData();
        drop.name = "3000016480865";
        drop.date = new Date();

        Map<String, DropTrack> result = fixture.ingest(drop);

        Assert.assertEquals(1, result.size());

        DropTrack track = result.get("FRUV71300321");

        Assert.assertEquals(2, track.files.size());

        DropAssetFile downloadFile = track.files.get(0);
        Assert.assertTrue(downloadFile.file.endsWith("UMG_vidtrk_00602537560646_01_001_20601.mp4"));
        Assert.assertEquals(AssetFile.FileType.VIDEO, downloadFile.type);
        Assert.assertEquals("4e92a9f57a74bf9146f0e627820a81c3", downloadFile.md5);
        Assert.assertEquals("FRUV71300321", downloadFile.isrc);
        Assert.assertEquals(262000, downloadFile.duration.intValue());

        DropAssetFile previewFile = track.files.get(1);
        Assert.assertTrue(previewFile.file.endsWith("UMG_vidtrkimg_00602537560646_01_001_RGB300_1400x1400.jpg"));
        Assert.assertEquals(AssetFile.FileType.IMAGE, previewFile.type);
        Assert.assertEquals("94e48b35182db59bb6faf176e728cc5b", previewFile.md5);
        Assert.assertEquals(null, previewFile.isrc);
        Assert.assertEquals(null, previewFile.duration);
    }
}
