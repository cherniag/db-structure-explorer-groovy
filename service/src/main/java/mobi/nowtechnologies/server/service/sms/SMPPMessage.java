package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.mt.MTMessage;
import org.jsmpp.bean.SMSCDeliveryReceipt;

/**
 * User: Alexsandr_Kolpakov Date: 10/21/13 Time: 5:33 PM
 */
public class SMPPMessage extends MTMessage {

    private SMSCDeliveryReceipt deliveryReceiptMode;

    public SMPPMessage(String originatingAddress, String destinationAddress, String content, SMSCDeliveryReceipt deliveryReceiptMode, long expireTimeMillis) {
        super(originatingAddress, destinationAddress, content);
        this.deliveryReceiptMode = deliveryReceiptMode;
        setValidityPeriodInHours((int) expireTimeMillis);
    }

    public SMSCDeliveryReceipt getDeliveryReceiptMode() {
        return deliveryReceiptMode;
    }

    public void setDeliveryReceiptMode(SMSCDeliveryReceipt deliveryReceiptMode) {
        this.deliveryReceiptMode = deliveryReceiptMode;
    }
}
