package mobi.nowtechnologies.server.trackrepo.ingest.manual;


import mobi.nowtechnologies.server.trackrepo.ingest.DropAssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTerritory;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.IMAGE;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.VIDEO;

/**
 * User: Alexsandr_Kolpakov
 * Date: 7/22/13
 */
public class ManualParserIT {
    private ManualParser fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new ManualParser("classpath:media/manual/");
    }

    @Test
    public void testIngest_VideoTrack_Success() throws Exception {
        URL fileURL = this.getClass().getClassLoader().getResource("media/manual/020313/020313.csv");
        String file = new File(fileURL.toURI()).getAbsolutePath();
        DropData drop = new DropData();
        drop.name = file;

        Map<String, DropTrack> result = fixture.ingest(drop);

        //#Dear Mama#2Pac#GBGHC0500038#hits.m4a#2Pac.jpg###Universal
        DropTrack videoTrack = result.get("GBGHC0500038");
        Assert.assertEquals("Dear Mama", videoTrack.getTitle());
        Assert.assertEquals("2Pac", videoTrack.getArtist());
        Assert.assertEquals(videoTrack.getIsrc(), videoTrack.getProductCode());
        Assert.assertEquals("Universal", videoTrack.getLabel());

        List<DropTerritory> territories = videoTrack.getTerritories();
        Assert.assertEquals(1, territories.size());
        Assert.assertEquals("Worldwide", territories.get(0).country);
        Assert.assertEquals("Universal", territories.get(0).label);


        //#Dear Mama#2Pac#GBVDC0500038##2Pac.jpg#0123456789#GB, NZ#Universal##hits.mpg#20000
        DropTrack videoTrack2 = result.get("GBVDC0500038");
        Assert.assertEquals("Dear Mama", videoTrack2.getTitle());
        Assert.assertEquals("2Pac", videoTrack2.getArtist());
        Assert.assertEquals("0123456789", videoTrack2.getProductCode());
        Assert.assertEquals("Universal", videoTrack2.getLabel());

        List<DropAssetFile> dropAssetFiles2 = videoTrack2.getFiles();
        Assert.assertEquals(2, dropAssetFiles2.size());
        Assert.assertEquals(IMAGE, dropAssetFiles2.get(0).type);
        Assert.assertTrue(dropAssetFiles2.get(0).file.endsWith("2Pac.jpg"));
        Assert.assertEquals(VIDEO, dropAssetFiles2.get(1).type);
        Assert.assertTrue(dropAssetFiles2.get(1).file.endsWith("hits.mpg"));
        Assert.assertEquals(20000, dropAssetFiles2.get(1).duration.intValue());

        List<DropTerritory> territories2 = videoTrack2.getTerritories();
        Assert.assertEquals(2, territories2.size());
        Assert.assertEquals("GB", territories2.get(0).country);
        Assert.assertEquals("NZ", territories2.get(1).country);
        Assert.assertEquals("Universal", territories2.get(0).label);
        Assert.assertEquals("Universal", territories2.get(1).label);
    }
}
