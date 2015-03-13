package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.jsmpp.JSMPPGateway;
import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MessageReceiver;
import org.smslib.smpp.AbstractSMPPGateway;

/**
 * User: Alexsandr_Kolpakov Date: 11/4/13 Time: 2:06 PM
 */
public class SMPPMessageReceiver extends MessageReceiver {

    private DeliverSmMessageProcessor deliverSmMessageProcessor;

    public SMPPMessageReceiver(DeliverSmMessageProcessor deliverSmMessageProcessor) {
        super(deliverSmMessageProcessor);
        this.deliverSmMessageProcessor = deliverSmMessageProcessor;
    }

    @Override
    public void init(JSMPPGateway jsmppGateway) {
        if (deliverSmMessageProcessor != null) {
            ((SMSMessageProcessorContainer) deliverSmMessageProcessor).setJsmppGateway((AbstractSMPPGateway) jsmppGateway);
        }
    }

    @Override
    public void setDeliverSmMessageProcessor(DeliverSmMessageProcessor deliverSmMessageProcessor) {
        super.setDeliverSmMessageProcessor(deliverSmMessageProcessor);
        this.deliverSmMessageProcessor = deliverSmMessageProcessor;
    }
}
