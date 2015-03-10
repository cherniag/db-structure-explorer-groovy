package mobi.nowtechnologies.server.service.o2.impl;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.Resource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.support.MarshallingUtils;

public class WebServiceGateway extends WebServiceGatewaySupport {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

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
        try {
            LOGGER.info("send And Receive " + endpoint);
            JAXBElement<T> element = (JAXBElement<T>) getWebServiceTemplate().marshalSendAndReceive(endpoint, requestPayload, defaultWebServiceMessageHandler);

            return element.getValue();
        } catch (SoapFaultClientException e) {
            LOGGER.error("Error sendAndReceive " + endpoint + " " + e + " " + e.getWebServiceMessage(), e);
            SoapFaultDetailElement detailElement = e.getSoapFault().getFaultDetail().getDetailEntries().next();
            SoapFaultException faultExcp = new SoapFaultException((SoapMessage) e.getWebServiceMessage());
            try {
                JAXBElement<?> element = (JAXBElement<?>) MarshallingUtils.unmarshal(getUnmarshaller(), new SourceWebServiceMessage(detailElement.getSource()));
                faultExcp.setSoapFaultObject(element.getValue());
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
            throw faultExcp;
        }
    }

    private class SourceWebServiceMessage implements WebServiceMessage {

        private Source payloadSource;

        public SourceWebServiceMessage(Source payloadSource) {
            this.payloadSource = payloadSource;
        }

        @Override
        public Source getPayloadSource() {
            return payloadSource;
        }

        @Override
        public Result getPayloadResult() {
            return null;
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
        }
    }
}
