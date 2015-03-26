package mobi.nowtechnologies.server.trackrepo.ingest.universal;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

/**
 * User: sanya Date: 7/10/13 Time: 9:25 AM
 */
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

        assertEquals(false, result.get("ROCRP1002941").explicit);
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        assertEquals(true, result.get("GBSXS1100209").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsNotTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        assertEquals(false, result.get("ROCRP1002941").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        assertEquals(true, result.get("GBSXS1100209").explicit);
    }

    @Test
    public void testLoadXml_IsPrdExplicitAndIsEmptyTrackExplicit_Success() throws Exception {
        String code = "05037128167052";
        String drop = "3000007191632";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        assertEquals(true, result.get("ROCRP1002948").explicit);
    }

    @Test
    public void testLoadXml_IsNotPrdExplicitAndIsEmptyTrackExplicit_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        assertEquals(false, result.get("ROCRP1002948").explicit);
    }

    @Test
    public void testLoadXml_Audio_Success() throws Exception {
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        assertEquals(8, result.size());

        DropTrack track = result.get("ROCRP1002948");

        assertEquals(DropTrack.Type.INSERT, track.type);
        assertEquals("05037128167051", track.productCode);
        assertEquals("Get Back (ASAP)", track.title);
        assertEquals("Extended Mix", track.subTitle);
        assertEquals("Alexandra Stan", track.artist);
        assertEquals("Dance", track.genre);
        assertEquals("(C) 2011 3Beat Productions Limited, under Exclusive License to All Around The World Ltd.", track.copyright);
        assertEquals("ROCRP1002948", track.isrc);
        assertEquals("2011", track.year);
        assertEquals(null, track.label);
        assertEquals("05037128167051", track.physicalProductId);
        assertEquals("Get Back (ASAP)", track.album);
        assertEquals(null, track.info);
        assertEquals(true, track.licensed);
        assertEquals(false, track.exists);
        assertEquals(false, track.explicit);
        assertEquals("05037128167051", track.productId);

        assertEquals(1, track.territories.size());

        DropTerritory territory = track.territories.get(0);
        assertEquals("GB", territory.country);
        assertEquals("Universal Music TV", territory.label);
        assertEquals(null, territory.currency);
        assertEquals(null, territory.price);
        assertEquals("ROCRP1002948", territory.reportingId);
        assertEquals("Universal Music TV Campaign Division", territory.distributor);
        assertEquals(false, territory.takeDown);
        assertEquals("STAP", territory.priceCode);
        assertEquals(null, territory.dealReference);
        assertEquals(null, territory.publisher);
    }

    @Test
    public void testLoadXml_Video_Success() throws Exception {
        String code = "00602537560646";
        String drop = "3000016480865";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();


        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        assertEquals(1, result.size());

        DropTrack track = result.get("FRUV71300321");

        assertEquals(DropTrack.Type.INSERT, track.type);
        assertEquals("00602537560646", track.productCode);
        assertEquals("Jamaïque", track.title);
        assertEquals("", track.subTitle);
        assertEquals("Psy 4 De La Rime", track.artist);
        assertEquals("Rap", track.genre);
        assertEquals("(C) 2013 Def Jam Recordings France", track.copyright);
        assertEquals("FRUV71300321", track.isrc);
        assertEquals("2013", track.year);
        assertEquals(null, track.label);
        assertEquals("00602537560646", track.physicalProductId);
        assertEquals("Jamaïque", track.album);
        assertEquals(null, track.info);
        assertEquals(true, track.licensed);
        assertEquals(false, track.exists);
        assertEquals(false, track.explicit);
        assertEquals("00602537560646", track.productId);

        assertEquals(1, track.territories.size());

        DropTerritory territory = track.territories.get(0);
        assertEquals("GB", territory.country);
        assertEquals("UMC", territory.label);
        assertEquals("09-Sep-2013", fixture.dateFormat.format(territory.startdate));
        assertEquals(null, territory.currency);
        assertEquals(null, territory.price);
        assertEquals("FRUV71300321", territory.reportingId);
        assertEquals("UMC (Universal Music Catalogue)", territory.distributor);
        assertEquals(false, territory.takeDown);
        assertEquals("STVTP", territory.priceCode);
        assertEquals(null, territory.dealReference);
        assertEquals(null, territory.publisher);
    }

    @Test
    public void testIngest_Audio_Success() throws Exception {
        DropData drop = new DropData();
        drop.name = "3000007191631";
        drop.date = new Date();

        Map<String, DropTrack> result = fixture.ingest(drop);

        assertEquals(8, result.size());

        DropTrack track = result.get("ROCRP1002948");

        assertEquals(4, track.files.size());

        DropAssetFile downloadFile = track.files.get(0);
        assertTrue(downloadFile.file.endsWith("UMG_audtrk_05037128167051_01_003_185.mp3"));
        assertEquals(AssetFile.FileType.DOWNLOAD, downloadFile.type);
        assertEquals("9ac82d0a3fb1dc43aef97dcf2c28b3e3", downloadFile.md5);
        assertEquals("ROCRP1002948", downloadFile.isrc);
        assertEquals(266000, downloadFile.duration.intValue());

        DropAssetFile previewFile = track.files.get(1);
        assertTrue(previewFile.file.endsWith("UMG_audclp_05037128167051_01_003_1129.mp4"));
        assertEquals(AssetFile.FileType.PREVIEW, previewFile.type);
        assertEquals("f3623b0b4bd4ae98a77fe8554ae3f7ef", previewFile.md5);
        assertEquals("ROCRP1002948", previewFile.isrc);
        assertEquals(266000, previewFile.duration.intValue());

        DropAssetFile mobileFile = track.files.get(2);
        assertTrue(mobileFile.file.endsWith("UMG_audtrk_05037128167051_01_003_1129.mp4"));
        assertEquals(AssetFile.FileType.MOBILE, mobileFile.type);
        assertEquals("8908fb2efc6fe2954537c8a1745163f6", mobileFile.md5);
        assertEquals("ROCRP1002948", mobileFile.isrc);
        assertEquals(266000, mobileFile.duration.intValue());

        DropAssetFile imageFile = track.files.get(3);
        assertTrue(imageFile.file.endsWith("UMG_cvrart_05037128167051_01_RGB72_1200x1200_10452455688.jpg"));
        assertEquals(AssetFile.FileType.IMAGE, imageFile.type);
        assertEquals("1acbb74d56b81ab6e1b4c0bdee6f0c3e", imageFile.md5);
        assertEquals(null, imageFile.isrc);
        assertEquals(null, imageFile.duration);
    }

    @Test
    public void testIngest_Video_Success() throws Exception {
        DropData drop = new DropData();
        drop.name = "3000016480865";
        drop.date = new Date();

        Map<String, DropTrack> result = fixture.ingest(drop);

        assertEquals(1, result.size());

        DropTrack track = result.get("FRUV71300321");

        assertEquals(2, track.files.size());

        DropAssetFile downloadFile = track.files.get(0);
        assertTrue(downloadFile.file.endsWith("UMG_vidtrk_00602537560646_01_001_20601.mp4"));
        assertEquals(AssetFile.FileType.VIDEO, downloadFile.type);
        assertEquals("4e92a9f57a74bf9146f0e627820a81c3", downloadFile.md5);
        assertEquals("FRUV71300321", downloadFile.isrc);
        assertEquals(262000, downloadFile.duration.intValue());

        DropAssetFile previewFile = track.files.get(1);
        assertTrue(previewFile.file.endsWith("UMG_vidtrkimg_00602537560646_01_001_RGB300_1400x1400.jpg"));
        assertEquals(AssetFile.FileType.IMAGE, previewFile.type);
        assertEquals("94e48b35182db59bb6faf176e728cc5b", previewFile.md5);
        assertEquals(null, previewFile.isrc);
        assertEquals(null, previewFile.duration);
    }

    @Test
    public void checkIngestingContentWithSeveralTerritories() throws Exception {
        DropData dropData = new DropData();
        dropData.name = "3000018255385";
        dropData.date = new Date();

        Map<String, DropTrack> resultDropTracks = fixture.ingest(dropData);
        assertThat(resultDropTracks, notNullValue());
        assertThat(resultDropTracks.keySet(), hasSize(1));

        DropTrack dropTrack = resultDropTracks.get("GBUV71200558");
        assertThat(dropTrack, notNullValue());

        List<DropTerritory> dropTrackTerritories = dropTrack.getTerritories();
        assertThat(dropTrackTerritories, notNullValue());
        assertThat(dropTrackTerritories, hasSize(2));
        assertThat(dropTrackTerritories, UniversalParserTest.TerritoryMatcher.hasTerritoryWithCountry("GB"));
        assertThat(dropTrackTerritories, UniversalParserTest.TerritoryMatcher.hasTerritoryWithCountry("NZ"));
    }

    @Test
    public void shouldParseAlbum() {
        //given
        String code = "05037128167051";
        String drop = "3000007191631";
        Map<String, List<DropAssetFile>> fulfillmentFiles = new HashMap<String, List<DropAssetFile>>();

        //when
        Map<String, DropTrack> result = fixture.loadXml(drop, code, fulfillmentFiles);

        //then
        for (DropTrack dropTrack : result.values()) {
            assertThat(dropTrack.album, is("Get Back (ASAP)"));
        }
    }

    static class TerritoryMatcher extends TypeSafeMatcher<Collection<DropTerritory>> {

        private String country;

        public TerritoryMatcher(String country) {
            this.country = country;
        }

        @Factory
        public static <T> Matcher<Collection<DropTerritory>> hasTerritoryWithCountry(String country) {
            return new TerritoryMatcher(country);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Drop territories does not contain country");
        }

        @Override
        protected boolean matchesSafely(Collection<DropTerritory> dropTerritory) {
            for (DropTerritory territory : dropTerritory) {
                if (Objects.equals(country, territory.country)) {
                    return true;
                }
            }
            return false;
        }

    }
}
