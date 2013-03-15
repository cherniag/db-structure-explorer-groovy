package mobi.nowtechnologies.server.service.o2.impl;

import java.io.IOException;

import javax.xml.bind.JAXBElement;

import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class WebServiceGateway extends WebServiceGatewaySupport {
	
	private WebServiceMessageCallback defaultWebServiceMessageHandler;

	public void setDefaultWebServiceMessageHandler(WebServiceMessageCallback defaultWebServiceMessageHandler) {
		this.defaultWebServiceMessageHandler = defaultWebServiceMessageHandler;
	}

	public void setKeystoreLocation(Resource keystoreLocation) throws IOException {
		System.setProperty("javax.net.ssl.keyStore", keystoreLocation.getFile().getAbsolutePath()); 
	}

	public void setKeystorePassword(String keystorePassword) {
		System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);
	}

	@SuppressWarnings("unchecked")
	public <T> T sendAndReceive(String endpoint, Object requestPayload) {
		JAXBElement<T> element = (JAXBElement<T>)getWebServiceTemplate().marshalSendAndReceive(endpoint, requestPayload, defaultWebServiceMessageHandler);
		return element.getValue();
	}
}
