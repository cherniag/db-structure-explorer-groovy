package mobi.nowtechnologies.server.service.vodafone.impl;

import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Message1_2Impl;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.impl.NZSubscriberInfoGateway;
import org.springframework.core.io.InputStreamSource;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.mime.AttachmentException;
import org.springframework.ws.soap.*;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;

import javax.activation.DataHandler;
import javax.xml.soap.*;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoGatewayMock extends NZSubscriberInfoGateway {

    private static final String PAY_INDICATOR = "payIndicator";
    private static final String PROVIDER_NAME = "providerName";
    private static final String BILLING_ACCOUNT_NAME = "billingAccountName";
    private static final String BILLING_ACCOUNT_NUMBER = "billingAccountNumber";
    private static final String DEFAULT_DATA = "6421111111";

    public static final String FAULT_DATA = "6420000000";

    private static final Map<String, Map<String, String>> testData = new HashMap<String, Map<String, String>>(){{
        put(DEFAULT_DATA, new HashMap<String, String>(){{
            put(PAY_INDICATOR, "Prepay");
            put(PROVIDER_NAME, "Vodafone");
            put(BILLING_ACCOUNT_NAME, "Simplepostpay_CCRoam");
            put(BILLING_ACCOUNT_NUMBER, "300001121");
        }});
    }};

    @Override
    public NZSubscriberResult getSubscriberResult(String msisdn) {
        if(FAULT_DATA.equals(msisdn)){
            throw new SoapFaultClientException(getSoapFaultMessage());
        }

        Map<String, String> data = testData.containsKey(msisdn) ? testData.get(msisdn) : testData.get(DEFAULT_DATA);
        return new NZSubscriberResult(data.get(PAY_INDICATOR), data.get(PROVIDER_NAME), data.get(BILLING_ACCOUNT_NUMBER), data.get(BILLING_ACCOUNT_NAME));
    }

    private SoapMessage getSoapFaultMessage(){
        return new SoapMessage() {
            public SoapEnvelope getEnvelope() throws SoapEnvelopeException {return null;}
            public String getSoapAction() {return null;}
            public void setSoapAction(String soapAction) {}
            public SoapBody getSoapBody() throws SoapBodyException {return null;}
            public SoapHeader getSoapHeader() throws SoapHeaderException {return null;}
            public SoapVersion getVersion() {return null;}
            public Document getDocument() {return null;}
            public void setDocument(Document document) {}
            public boolean hasFault() {return false;}
            public String getFaultReason() {return "Test reason.";}
            public boolean isXopPackage() {return false;}
            public boolean convertToXopPackage() {return false;}
            public Attachment getAttachment(String contentId) throws AttachmentException {return null;}
            public Iterator<Attachment> getAttachments() throws AttachmentException {return null;}
            public Attachment addAttachment(String contentId, File file) throws AttachmentException {return null;}
            public Attachment addAttachment(String contentId, InputStreamSource inputStreamSource, String contentType) {return null;}
            public Attachment addAttachment(String contentId, DataHandler dataHandler) {return null;}
            public Source getPayloadSource() {return null;}
            public Result getPayloadResult() {return null;}
            public void writeTo(OutputStream outputStream) throws IOException {}
        };
    }
}
