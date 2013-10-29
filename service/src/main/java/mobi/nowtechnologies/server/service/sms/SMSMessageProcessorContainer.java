package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MOMessage;
import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SMSMessageProcessorContainer extends DeliverSmMessageProcessor {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected List<SMSMessageProcessor> processors;

    @Override
    public void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage) {
        LOGGER.info("SMSMessageProcessorContainer has got sms message: [{}]", new Object[]{inboundMessage});

        processMessage(deliverSm, inboundMessage);
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
}