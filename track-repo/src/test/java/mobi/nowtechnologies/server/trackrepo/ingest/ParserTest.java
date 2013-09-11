package mobi.nowtechnologies.server.trackrepo.ingest;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * User: Titov Mykhaylo (titov)
 * 02.09.13 15:42
 */
public abstract class ParserTest<T extends IParser> {

    protected final static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    protected T parserFixture;
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

    protected Element getChildNodesElement(Node node) {
        return (Element) node.getChildNodes();
    }

    protected String getElementValue(Element element, String tagName) {
        Node item = element.getElementsByTagName(tagName).item(0);
        if (item != null)
            return item.getTextContent();
        return null;
    }

    protected String getIsrc(Element releaseIdNChildElement) {
        return getElementValue(releaseIdNChildElement, "ISRC");
    }

    protected String getProprietaryId(Element releaseIdNChildElement) {
        return getElementValue(releaseIdNChildElement, "ProprietaryId");
    }
}
