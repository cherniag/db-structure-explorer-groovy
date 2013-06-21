package mobi.nowtechnologies.ingestors;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DebugErrorHandler implements ErrorHandler{

	public void error(SAXParseException exception) throws SAXException {
		System.out.println("Error "+exception.getMessage());
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		System.out.println("Fatal Error "+exception.getMessage());
		
	}

	public void warning(SAXParseException exception) throws SAXException {
		System.out.println("Warning  "+exception.getMessage());
		
	}

}
