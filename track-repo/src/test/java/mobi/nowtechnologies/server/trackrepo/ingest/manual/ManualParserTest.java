package mobi.nowtechnologies.server.trackrepo.ingest.manual;

import junit.framework.Assert;
import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;
import mobi.nowtechnologies.server.trackrepo.ingest.DropData;
import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 7/22/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManualParserTest {
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

        DropTrack videoTrack = result.get("GBVDC0500038");
        Assert.assertNotNull(videoTrack);
        Assert.assertNotNull(videoTrack.getFiles());
        Assert.assertEquals(2, videoTrack.getFiles().size());
        Assert.assertEquals(AssetFile.FileType.VIDEO, videoTrack.getFiles().get(1).type);
    }
}
