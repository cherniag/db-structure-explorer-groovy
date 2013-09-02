package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.sony.SonyDDEXParser;
import mobi.nowtechnologies.server.trackrepo.ingest.warner.WarnerParser;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathsEqual;
import static org.junit.Assert.*;

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

        assertEquals(true, result.get("USAT21001777A10302B0001239466Eclass mobi.nowtechnologies.server.trackrepo.ingest.warner.WarnerParser").explicit);
    }

    @Before
    public void setUp() throws Exception {
        ddexParserFixture = new WarnerParser("classpath:media/warner_cdu/new_release/");
    }

}
