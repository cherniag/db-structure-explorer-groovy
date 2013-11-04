package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.jsmpp.JSMPPGateway;
import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MOMessage;
import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.smpp.AbstractSMPPGateway;

import java.util.ArrayList;
import java.util.List;

public class SMSMessageProcessorContainer extends DeliverSmMessageProcessor {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected List<SMSMessageProcessor> processors;

    protected List<AbstractSMPPGateway> gateways;

    private boolean alreadyCalled;

    @Override
    public void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage) {
        LOGGER.info("SMSMessageProcessorContainer has got sms message: [{}]", new Object[]{inboundMessage});

        processMessage(deliverSm, inboundMessage);

        int i = 0;
        for(AbstractSMPPGateway gateway : gateways){
            if(i > 0)
                gateway.incInboundMessageCount();

            i++;
        }
    }

    protected void processMessage(final DeliverSm deliverSm, Object message){
        for (SMSMessageProcessor processor : processors) {
            if(processor.supports(deliverSm))
                processor.parserAndProcess(message);
        }
    }

    @Override
    public void processStatusReportMessage(DeliverSm deliverSm) {
        LOGGER.info("SMSMessageProcessorContainer has got sms delivery receipt: [{}]", new Object[]{deliverSm});

        processMessage(deliverSm, deliverSm);
    }

    public void setMessageProcessors(List<SMSMessageProcessor> processors) {
        this.processors = processors;
    }

    public void setJsmppGateway(AbstractSMPPGateway jsmppGateway) {
        if (!alreadyCalled) {
            gateways = new ArrayList<AbstractSMPPGateway>();
            super.setJsmppGateway((JSMPPGateway)jsmppGateway);
        }
        gateways.add(jsmppGateway);
        alreadyCalled = true;
    }
}