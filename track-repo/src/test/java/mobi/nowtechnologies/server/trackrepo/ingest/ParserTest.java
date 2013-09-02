package mobi.nowtechnologies.server.trackrepo.ingest;

import mobi.nowtechnologies.server.trackrepo.ingest.Parser;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: Titov Mykhaylo (titov)
 * 02.09.13 15:42
 */
public abstract class ParserTest {

    protected Parser parserFixture;
    protected XpathEngine xpathEngine;
    protected File xmlFile;
    protected Document document;

    protected Document getDocument() throws IOException, SAXException {
        return XMLUnit.buildControlDocument(new InputSource(new FileInputStream(xmlFile)));
    }

    protected String evaluate(String expression) throws XpathException {
        return xpathEngine.evaluate(expression, document);
    }
}
