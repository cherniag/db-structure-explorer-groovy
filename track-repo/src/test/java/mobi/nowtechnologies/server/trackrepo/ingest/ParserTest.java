package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.Parser;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * User: Titov Mykhaylo (titov)
 * 02.09.13 15:42
 */
public abstract class ParserTest {

    protected Parser parserFixture;
    protected XpathEngine xpathEngine;
    protected File xmlFile;
    protected Document document;
    protected HashMap xmlPrefixMap;

    @Before
    public void setUp() throws FileNotFoundException {
        createParser();

        xmlPrefixMap = new HashMap();
        populateXmlPrefixMap();

        xpathEngine = XMLUnit.newXpathEngine();
        xpathEngine.setNamespaceContext(new SimpleNamespaceContext(xmlPrefixMap));
    }

    protected abstract void createParser() throws FileNotFoundException;

    protected abstract void populateXmlPrefixMap();

    protected Document getDocument() throws IOException, SAXException {
        return XMLUnit.buildControlDocument(new InputSource(new FileInputStream(xmlFile)));
    }

    protected String evaluate(String expression) throws XpathException {
        return xpathEngine.evaluate(expression, document);
    }
}
