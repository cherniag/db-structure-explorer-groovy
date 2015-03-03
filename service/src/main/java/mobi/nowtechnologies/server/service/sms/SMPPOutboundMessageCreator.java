package mobi.nowtechnologies.server.service.sms;

import java.io.IOException;

import com.sentaca.spring.smpp.mt.DefaultOutboundMessageCreator;
import com.sentaca.spring.smpp.mt.MTMessage;
import org.smslib.OutboundBinaryMessage;
import org.smslib.OutboundMessage;

/**
 * User: Alexsandr_Kolpakov Date: 10/21/13 Time: 5:33 PM
 */
public class SMPPOutboundMessageCreator extends DefaultOutboundMessageCreator {

    @Override
    public OutboundMessage toOutboundMessage(MTMessage message) throws IOException {
        OutboundBinaryMessage binaryMsg = (OutboundBinaryMessage) super.toOutboundMessage(message);

        if (message instanceof SMPPMessage) {
            SMPPOutboundMessage msg = new SMPPOutboundMessage();
            msg.setDataBytes(binaryMsg.getDataBytes());
            msg.setRecipient(binaryMsg.getRecipient());
            msg.setEncoding(binaryMsg.getEncoding());
            msg.setFrom(binaryMsg.getFrom());
            msg.setValidityPeriod(binaryMsg.getValidityPeriod());

            SMPPMessage smppMessage = (SMPPMessage) message;
            msg.setDeliveryReceiptMode(smppMessage.getDeliveryReceiptMode());
            binaryMsg = msg;
        }

        return binaryMsg;
    }
}
