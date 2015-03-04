package mobi.nowtechnologies.server.trackrepo.ingest.manual;


import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.VIDEO;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.INTERNAL_REPORTED;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.NOT_REPORTED;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.REPORTED_BY_TAGS;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * User: Alexsandr_Kolpakov Date: 7/22/13
 */
public class ManualParserIT {

    private ManualParser manualParserFixture;

    @Before
    public void setUp() throws Exception {
        manualParserFixture = new ManualParser("classpath:media/manual/");
    }

    @Test
    public void shouldIngest() throws Exception {
        //given
        DropData drop = new DropData();
        drop.name = new ClassPathResource("media/manual/020313/020313.csv").getFile().getAbsolutePath();

        //when
        Map<String, DropTrack> result = manualParserFixture.ingest(drop);

        //then
        DropTrack videoTrack = result.get("GBGHC0500038");
        assertEquals("Dear Mama", videoTrack.getTitle());
        assertEquals("2Pac", videoTrack.getArtist());
        assertEquals(videoTrack.getIsrc(), videoTrack.getProductCode());
        assertEquals("Universal", videoTrack.getLabel());

        List<DropTerritory> territories = videoTrack.getTerritories();
        assertEquals(1, territories.size());
        assertEquals("Worldwide", territories.get(0).country);
        assertEquals("Universal", territories.get(0).label);
        assertEquals(INTERNAL_REPORTED, videoTrack.getReportingType());

        DropTrack videoTrack2 = result.get("GBVDC0500038");
        assertEquals("Dear Mama", videoTrack2.getTitle());
        assertEquals("2Pac", videoTrack2.getArtist());
        assertEquals("0123456789", videoTrack2.getProductCode());
        assertEquals("Universal", videoTrack2.getLabel());
        assertEquals(NOT_REPORTED, videoTrack2.getReportingType());

        List<DropAssetFile> dropAssetFiles2 = videoTrack2.getFiles();
        assertEquals(2, dropAssetFiles2.size());
        assertEquals(IMAGE, dropAssetFiles2.get(0).type);
        assertTrue(dropAssetFiles2.get(0).file.endsWith("2Pac.jpg"));
        assertEquals(VIDEO, dropAssetFiles2.get(1).type);
        assertTrue(dropAssetFiles2.get(1).file.endsWith("hits.mpg"));
        assertEquals(20000, dropAssetFiles2.get(1).duration.intValue());

        List<DropTerritory> territories2 = videoTrack2.getTerritories();
        assertEquals(2, territories2.size());
        assertEquals("GB", territories2.get(0).country);
        assertEquals("NZ", territories2.get(1).country);
        assertEquals("Universal", territories2.get(0).label);
        assertEquals("Universal", territories2.get(1).label);

        DropTrack track = result.get("GBVDC0500000");
        assertEquals("Dear Mama", track.getTitle());
        assertEquals("2Pac", track.getArtist());
        assertEquals(track.getIsrc(), track.getProductCode());
        assertEquals("Universal", track.getLabel());
        assertEquals(REPORTED_BY_TAGS, track.getReportingType());
    }
}
