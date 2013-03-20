package mobi.nowtechnologies.server.service.o2.impl;

import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

public class SoapFaultException extends SoapFaultClientException {
	private static final long serialVersionUID = 1L;

	private Object soapFaultObject;

	public SoapFaultException(SoapMessage faultMessage) {
		super(faultMessage);
	}

	public Object getSoapFaultObject() {
		return soapFaultObject;
	}

	public void setSoapFaultObject(Object soapFaultObject) {
		this.soapFaultObject = soapFaultObject;
	}
}
