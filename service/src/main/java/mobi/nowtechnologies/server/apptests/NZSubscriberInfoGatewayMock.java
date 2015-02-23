package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.impl.NZSubscriberInfoGateway;
import mobi.nowtechnologies.server.service.nz.impl.NZSubscriberInfoServiceImpl;
import org.springframework.core.io.InputStreamSource;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.mime.AttachmentException;
import org.springframework.ws.soap.*;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Document;

import javax.activation.DataHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;


/**
 * @author Anton Zemliankin
 */
public class NZSubscriberInfoGatewayMock extends NZSubscriberInfoGateway {
    public static int notAvailablePrefix = 6;
    public static int doesNotBelong = 9;

    @Override
    public NZSubscriberResult getSubscriberResult(String msisdn) {
        final String vodafoneMsisdnPrefix = "64";

        if(!msisdn.startsWith(vodafoneMsisdnPrefix)) {
            throw new SoapFaultClientException(getSoapFaultMessage(NZSubscriberInfoServiceImpl.NOT_FOUND_TOKEN));
        }

        final String notFoundPrefix = vodafoneMsisdnPrefix + NZSubscriberInfoGatewayMock.doesNotBelong;
        if(msisdn.startsWith(notFoundPrefix)) {
            return new NZSubscriberResult("Prepay", "Unknown Operator", "300001121", "Simplepostpay_CCRoam");
        }

        final String notAvailablePrefix = vodafoneMsisdnPrefix + NZSubscriberInfoGatewayMock.notAvailablePrefix;
        if (msisdn.startsWith(notAvailablePrefix)) {
            throw new SoapFaultClientException(getSoapFaultMessage("Test reason."));
        }

        return new NZSubscriberResult("Prepay", "Vodafone", "300001121", "Simplepostpay_CCRoam");
    }

     private SoapMessage getSoapFaultMessage(final String faultReason) {
        return new SoapMessage() {
            public SoapEnvelope getEnvelope() throws SoapEnvelopeException {
                return null;
            }

            public String getSoapAction() {
                return null;
            }

            public void setSoapAction(String soapAction) {
            }

            public SoapBody getSoapBody() throws SoapBodyException {
                return null;
            }

            public SoapHeader getSoapHeader() throws SoapHeaderException {
                return null;
            }

            public SoapVersion getVersion() {
                return null;
            }

            public Document getDocument() {
                return null;
            }

            public void setDocument(Document document) {
            }

            public boolean hasFault() {
                return false;
            }

            public String getFaultReason() {
                return faultReason;
            }

            public boolean isXopPackage() {
                return false;
            }

            public boolean convertToXopPackage() {
                return false;
            }

            public Attachment getAttachment(String contentId) throws AttachmentException {
                return null;
            }

            public Iterator<Attachment> getAttachments() throws AttachmentException {
                return null;
            }

            public Attachment addAttachment(String contentId, File file) throws AttachmentException {
                return null;
            }

            public Attachment addAttachment(String contentId, InputStreamSource inputStreamSource, String contentType) {
                return null;
            }

            public Attachment addAttachment(String contentId, DataHandler dataHandler) {
                return null;
            }

            public Source getPayloadSource() {
                return null;
            }

            public Result getPayloadResult() {
                return null;
            }

            public void writeTo(OutputStream outputStream) throws IOException {
            }
        };
    }

}
