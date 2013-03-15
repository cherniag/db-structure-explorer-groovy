package mobi.nowtechnologies.server.service.o2.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;

import org.springframework.core.io.Resource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.security.xwss.callback.KeyStoreCallbackHandler;

import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.XWSSProcessor;
import com.sun.xml.wss.XWSSProcessorFactory;

public class XwssMessageSigner {

	private final XWSSProcessor processor;

	public XwssMessageSigner(Resource policyFile, KeyStoreCallbackHandler keystoreHandler) throws Exception {

		InputStream in = policyFile.getInputStream();
		XWSSProcessorFactory factory = XWSSProcessorFactory.newInstance();
		processor = factory.createProcessorForSecurityConfiguration(in, keystoreHandler);
		in.close();
	}

	public WebServiceMessageCallback getCallback(final Map<QName, String> headers) {

		return new WebServiceMessageCallback() {
			public void doWithMessage(WebServiceMessage message) throws IOException {

				SaajSoapMessage origSaajMessage = (SaajSoapMessage) message;
				SOAPMessage origSoapMessage = origSaajMessage.getSaajMessage();
				SoapHeader soapHeader = origSaajMessage.getSoapHeader();
								
				for (QName header : headers.keySet()) {
					soapHeader.addAttribute(header, headers.get(header));
				}

				ProcessingContext context = new ProcessingContext();

				try {
					context.setSOAPMessage(origSoapMessage);
					SOAPMessage securedSoapMessage = processor.secureOutboundMessage(context);
					origSaajMessage.setSaajMessage(securedSoapMessage);
				} catch (Exception exc) {
					exc.printStackTrace();
					throw new IOException(exc.getMessage());
				}
			}
		};
	}
}
