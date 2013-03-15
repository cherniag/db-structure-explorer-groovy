package mobi.nowtechnologies.server.service.o2.impl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class WebServiceGateway extends WebServiceGatewaySupport {

	private XwssMessageSigner xwssMessageSigner = null;
	private Map<QName, String> headers = new HashMap<QName, String>();
	
	public WebServiceGateway() {
		super();
		
		QName soaConsumerTransactionIDQName = new QName("http://soa.o2.co.uk/coredata_1", "SOAConsumerTransactionID", "cor");
		headers.put(soaConsumerTransactionIDQName, "0000111122223333:musicqubed.test");
	}

	public void setXwssMessageSigner(XwssMessageSigner signer) {
		this.xwssMessageSigner = signer;
	}

	public Object sendAndReceive(String endpoint, Object requestPayload) {
		return getWebServiceTemplate().marshalSendAndReceive(endpoint, requestPayload, xwssMessageSigner.getCallback(headers));
	}
}
