package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * User: sanya
 * Date: 7/9/13
 * Time: 1:31 PM
 */
@RunWith(PowerMockRunner.class)
public class DDEXParserTest {

    private DDEXParser ddexParserFixture;

    @Test
    public void testLoadXml_IsExplicit_Success() throws Exception {
        File xmlFile = new ClassPathResource("media/warner_cdu/new_release/20111011_0926_13/075679971517/075679971517.xml").getFile();

        Map<String, DropTrack> result = ddexParserFixture.loadXml(xmlFile);

        for (String key : result.keySet()){
           if(key.startsWith("USAT21001777A10302B0001239466Eclass mobi.nowtechnologies.server.trackrepo.ingest.warner.SonyDDEXParser"))
              assertEquals(true, result.get(key).explicit);
        }
    }

    @Test
    public void testGetDrops_NotAuto_Successful(){
        List<DropData> result = ddexParserFixture.getDrops(false);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(true, result.get(0).getName().endsWith("20130625123358187"));
    }

    @Test
    public void testGetDrops_Auto_Successful(){
        List<DropData> result = ddexParserFixture.getDrops(true);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(true, result.get(0).getName().endsWith("20130625123358187"));
    }

    @Test
    public void testIngest_Successful() throws IOException {
        File dropFolder = new ClassPathResource("media/sony_cdu/ddex/20130625123358187").getFile();

        final DropData drop = new DropData();
        drop.setDate(new Date());
        drop.setName(dropFolder.getAbsolutePath());

        Map<String, DropTrack> dropTracks1 = new HashMap<String, DropTrack>();
        dropTracks1.put("isrc1", new DropTrack());
        Map<String, DropTrack> dropTracks2 = new HashMap<String, DropTrack>();
        dropTracks2.put("isrc2", new DropTrack());
        Map<String, DropTrack> dropTracks3 = new HashMap<String, DropTrack>();
        dropTracks3.put("isrc3", new DropTrack());
        Map<String, DropTrack> dropTracks4 = new HashMap<String, DropTrack>();
        dropTracks3.put("isrc4", new DropTrack());

        Mockito.doReturn(dropTracks1).when(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A0000244390N"+File.separator+"A10301A0000244390N.xml");
            }
        }));
        Mockito.doReturn(dropTracks2).when(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A0001406903U"+File.separator+"A10301A0001406903U.xml");
            }
        }));
        Mockito.doReturn(dropTracks3).when(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A0001640650S"+File.separator+"A10301A0001640650S.xml");
            }
        }));
        Mockito.doReturn(dropTracks4).when(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A00012459223"+File.separator+"A10301A00012459223.xml");
            }
        }));

        Map<String, DropTrack> result = ddexParserFixture.ingest(drop);

        assertNotNull(result);
        assertEquals(4, result.size());

        Mockito.verify(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A0000244390N"+File.separator+"A10301A0000244390N.xml");
            }
        }));
        Mockito.verify(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A0001406903U"+File.separator+"A10301A0001406903U.xml");
            }
        }));
        Mockito.verify(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A0001640650S"+File.separator+"A10301A0001640650S.xml");
            }
        }));
        Mockito.verify(ddexParserFixture).loadXml(Matchers.argThat(new ArgumentMatcher<File>() {
            @Override
            public boolean matches(Object o) {
                return ((File)o).getAbsolutePath().equals(drop.getName()+File.separator+"A10301A00012459223"+File.separator+"A10301A00012459223.xml");
            }
        }));
    }

    @Before
    public void setUp() throws Exception {
        ddexParserFixture = Mockito.spy(new SonyDDEXParser("classpath:media/sony_cdu/ddex/"));
    }

}
