package mobi.nowtechnologies.server.trackrepo.ingest;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringBufferInputStream;

public class DtdLoader implements EntityResolver {

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		System.out.println(" DtdLoader "+publicId+" "+ systemId);
		return new InputSource(new StringBufferInputStream(""));
	}

}