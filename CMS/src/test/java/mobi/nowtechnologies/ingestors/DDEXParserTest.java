package mobi.nowtechnologies.ingestors;

import mobi.nowtechnologies.ingestors.warner.WarnerParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: sanya
 * Date: 7/9/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
public class DDEXParserTest {

    private DDEXParser fixture;

    @Test
    public void testLoadXml_IsExplicit_Success() throws Exception {
        URL fileURL = this.getClass().getClassLoader().getResource("media/warner_cdu/new_release/20111011_0926_13/075679971517/075679971517.xml");
        String file = new File(fileURL.toURI()).getAbsolutePath();

        Map<String, DropTrack> result = fixture.loadXml(file);

        assertEquals(true, result.get("USAT21001777A10302B0001239466Eclass mobi.nowtechnologies.ingestors.warner.WarnerParser").explicit);
    }


    @Before
    public void setUp() throws Exception {
        fixture = new WarnerParser();

    }

}
