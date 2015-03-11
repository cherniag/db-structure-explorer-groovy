package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.io.File.separator;

import org.springframework.core.io.ClassPathResource;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

/**
 * User: sanya Date: 7/9/13 Time: 1:31 PM
 */
@RunWith(PowerMockRunner.class)
public class DDEXParserTest {

    private DDEXParser ddexParserFixture;

    @Before
    public void setUp() throws Exception {
        ddexParserFixture = spy(new SonyDDEXParser("classpath:media/sony_cdu/ddex/"));
    }

    @Test
    public void testLoadXml_IsExplicit_Success() throws Exception {
        File xmlFile = new ClassPathResource("media/warner_cdu/new_release/20111011_0926_13/075679971517/075679971517.xml").getFile();

        Map<String, DropTrack> result = ddexParserFixture.loadXml(xmlFile);

        for (String key : result.keySet()) {
            if (key.startsWith("USAT21001777A10302B0001239466Eclass mobi.nowtechnologies.server.trackrepo.ingest.warner.SonyDDEXParser")) {
                assertEquals(true, result.get(key).explicit);
            }
        }
    }

    @Test
    public void testGetDrops_NotAuto_Successful() {
        List<DropData> result = ddexParserFixture.getDrops(false);

        assertNotNull(result);
        assertEquals(3, result.size());

        DropData dropActual = null;
        for (DropData drop : result) {
            if (drop.name.endsWith("20130625123358187")) {
                dropActual = drop;
            }
        }
        assertNotNull(dropActual);
    }

    @Test
    public void testGetDrops_Auto_Successful() {
        List<DropData> result = ddexParserFixture.getDrops(true);

        assertNotNull(result);
        assertEquals(2, result.size());

        DropData dropActual = null;
        for (DropData drop : result) {
            if (drop.name.endsWith("20130625123358187")) {
                dropActual = drop;
            }
        }
        assertNotNull(dropActual);
    }

    @Test
    public void testIngest_Successful() throws IOException {
        File dropFolder = new ClassPathResource("media/sony_cdu/ddex/20130625123358187").getFile();

        final DropData drop = new DropData();
        drop.date = new Date();
        drop.name = dropFolder.getAbsolutePath();

        Map<String, DropTrack> dropTracks1 = new HashMap<String, DropTrack>();
        dropTracks1.put("isrc1", new DropTrack());
        Map<String, DropTrack> dropTracks2 = new HashMap<String, DropTrack>();
        dropTracks2.put("isrc2", new DropTrack());
        Map<String, DropTrack> dropTracks3 = new HashMap<String, DropTrack>();
        dropTracks3.put("isrc3", new DropTrack());
        Map<String, DropTrack> dropTracks4 = new HashMap<String, DropTrack>();
        dropTracks3.put("isrc4", new DropTrack());

        doReturn(dropTracks1).when(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A0000244390N", "A10301A0000244390N.xml")));
        doReturn(dropTracks2).when(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A0001406903U", "A10301A0001406903U.xml")));
        doReturn(dropTracks3).when(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A0001640650S", "A10301A0001640650S.xml")));
        doReturn(dropTracks4).when(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A00012459223", "A10301A00012459223.xml")));

        Map<String, DropTrack> result = ddexParserFixture.ingest(drop);

        assertNotNull(result);
        assertEquals(4, result.size());

        verify(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A0000244390N", "A10301A0000244390N.xml")));
        verify(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A0001406903U", "A10301A0001406903U.xml")));
        verify(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A0001640650S", "A10301A0001640650S.xml")));
        verify(ddexParserFixture).loadXml(argThat(new LoadXmlArgumentMatcher(drop.name, "A10301A00012459223", "A10301A00012459223.xml")));
    }

    private class LoadXmlArgumentMatcher extends ArgumentMatcher<File> {

        private String dropName;
        private String folderName;
        private String fileName;

        private LoadXmlArgumentMatcher(String dropName, String folderName, String fileName) {
            this.dropName = dropName;
            this.folderName = folderName;
            this.fileName = fileName;
        }

        @Override
        public boolean matches(Object o) {
            return ((File) o).getAbsolutePath().equals(dropName + separator + folderName + separator + fileName);
        }
    }

}
