package mobi.nowtechnologies.server.trackrepo.ingest.sony;

import mobi.nowtechnologies.server.trackrepo.ingest.DropTrack;

import java.io.File;
import java.net.URL;

import org.junit.*;
import org.junit.runner.*;

import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created with IntelliJ IDEA. User: sanya Date: 7/10/13 Time: 8:32 AM To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
public class SonyParserTest {

    private SonyParser fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SonyParser("classpath:media/sony_cdu/");
    }

    @Test
    public void testLoadXml_IsExplicit_Success() throws Exception {
        URL fileURL = this.getClass().getClassLoader().getResource("media/sony_cdu/2472000/000/000/000/000/092/056/56/00000000000009205656.xml");
        String file = new File(fileURL.toURI()).getAbsolutePath();

        DropTrack result = fixture.loadXml(file);

        Assert.assertEquals(true, result.explicit);
    }
}
