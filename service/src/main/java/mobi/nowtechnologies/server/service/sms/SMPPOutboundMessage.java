package mobi.nowtechnologies.server.service.sms;

import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.smslib.OutboundBinaryMessage;

/**
 * User: Alexsandr_Kolpakov Date: 10/21/13 Time: 5:35 PM
 */
public class SMPPOutboundMessage extends OutboundBinaryMessage {

    private SMSCDeliveryReceipt deliveryReceiptMode;

    public SMSCDeliveryReceipt getDeliveryReceiptMode() {
        return deliveryReceiptMode;
    }

    public void setDeliveryReceiptMode(SMSCDeliveryReceipt deliveryReceiptMode) {
        this.deliveryReceiptMode = deliveryReceiptMode;
    }
}
