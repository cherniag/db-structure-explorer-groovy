package mobi.nowtechnologies.server.service.o2.impl;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.soap.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

public class O2WebServiceMessageHandler implements WebServiceMessageCallback{
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private QName soaConsumerTransactionIDQName = new QName("http://soa.o2.co.uk/coredata_1", "SOAConsumerTransactionID", "cor");
	private QName securityQName = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse");
	
	private String soaConsumerTransactionID;
	private String username;
	private String password;
	
	@Override
	public void doWithMessage(WebServiceMessage message) throws IOException {

		SaajSoapMessage origSaajMessage = (SaajSoapMessage) message;
		SOAPMessage origSoapMessage = origSaajMessage.getSaajMessage();
		try {
			SOAPHeader soapHeader = origSoapMessage.getSOAPHeader();
			
			// add userToken header element
            final SOAPElement security = soapHeader.addChildElement(securityQName);
            final SOAPElement userToken = security.addChildElement("UsernameToken", securityQName.getPrefix());
            userToken.addChildElement("Username", securityQName.getPrefix()).addTextNode(username);
            userToken.addChildElement("Password", securityQName.getPrefix()).addTextNode(password);
			
            // add soaConsumerTransactionID header element
			soapHeader.addChildElement(soaConsumerTransactionIDQName).addTextNode(soaConsumerTransactionID);
		} catch (SOAPException e) {
			LOGGER.error(e.getMessage(), e);
			throw new IOException(e.getMessage());
		}
	}

	public void setSoaConsumerTransactionID(String soaConsumerTransactionID) {
		this.soaConsumerTransactionID = soaConsumerTransactionID;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
}
