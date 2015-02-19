package mobi.nowtechnologies.server.apptests;

import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.impl.NZSubscriberInfoGateway;
import mobi.nowtechnologies.server.service.nz.impl.NZSubscriberInfoServiceImpl;
import org.apache.commons.lang3.StringUtils;
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
    private String notAvailableSuffix = "666";
    private String notFoundSuffix = "888";

    @Override
    public NZSubscriberResult getSubscriberResult(String msisdn) {
        if(msisdn.endsWith(notFoundSuffix)) {
            throw new SoapFaultClientException(getSoapFaultMessage(NZSubscriberInfoServiceImpl.NOT_FOUND_TOKEN));
        }

        if (msisdn.endsWith(notAvailableSuffix)) {
            throw new SoapFaultClientException(getSoapFaultMessage("Test reason."));
        }

        return new NZSubscriberResult("Prepay", "Vodafone", "300001121", "Simplepostpay_CCRoam");
    }

    public String generateSuccessMsisdn() {
        String time = StringUtils.reverse("" + System.nanoTime()).substring(0, 5);
        return "64" + time + "777";
    }

    public String generateNotFoundMsisdn() {
        String time = StringUtils.reverse("" + System.nanoTime()).substring(0, 5);
        return "64" + time + notFoundSuffix;
    }

    public String generateNotAvailableMsisdn() {
        String time = StringUtils.reverse("" + System.nanoTime()).substring(0, 5);
        return "64" + time + notAvailableSuffix;
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
