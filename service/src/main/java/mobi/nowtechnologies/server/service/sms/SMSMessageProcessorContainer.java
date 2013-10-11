package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MOMessage;
import com.sentaca.spring.smpp.mt.MTMessage;
import mobi.nowtechnologies.server.shared.Parser;
import mobi.nowtechnologies.server.shared.Processor;
import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class SMSMessageProcessorContainer extends DeliverSmMessageProcessor {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    protected final String ALL_NUMBERS = "*";

    private static final int MAX_QUEUE_SIZE = 10;
    protected ConcurrentHashMap<String, ArrayBlockingQueue<Processor>> processors = new ConcurrentHashMap<String, ArrayBlockingQueue<Processor>>();

    @Override
    public void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage) {
        LOGGER.info("SMSMessageProcessorContainer has got sms message: [{}]", new Object[]{inboundMessage});

        Processor processor = getMessageProcessor(inboundMessage);

        if(processor == null){
            String msgId = getMessageId(ALL_NUMBERS, inboundMessage.getDestAddress());
            processor = getMessageProcessor(msgId);
        }

        if(processor != null){
            Parser<String, ?> parser = processor.getMessageParser();
            Object data = inboundMessage;
            if(parser != null){
                data = parser.parse(inboundMessage.getText());
            }

            processor.process(data);
        } else {
            LOGGER.info("SMSMessageProcessorContainer doesn't have any processor for message with messageId = [{}]", new Object[]{getMessageId(inboundMessage.getOriginator(), inboundMessage.getDestAddress())});
        }
    }

    public void registerMessageProcessor(String msgId, Processor processor) {

        ArrayBlockingQueue<Processor> queue = null;
        synchronized (processors) {
            queue = processors.get(msgId);
            if (queue == null) {
                queue = new ArrayBlockingQueue<Processor>(MAX_QUEUE_SIZE, true);
                processors.put(msgId, queue);
            }
        }

        queue.offer(processor);

        LOGGER.info("SMSMessageProcessorContainer has registered with messageId = [{}]", new Object[]{msgId});
    }

    public Processor getMessageProcessor(MOMessage moMessage) {
        String msgId = getMessageId(moMessage.getOriginator(), moMessage.getDestAddress());

        return getMessageProcessor(msgId);
    }

    public Processor getMessageProcessor(String msgId) {
        ArrayBlockingQueue<Processor> queue = processors.get(msgId);
        Processor processor = queue != null ? queue.poll() : null;

        if(processor != null)
            LOGGER.info("SMSMessageProcessorContainer has found processor: [{}] for messageId = [{}]", new Object[]{processor, msgId});
        else
            LOGGER.info("SMSMessageProcessorContainer hasn't found processor for messageId = [{}]", new Object[]{msgId});

        return processor;
    }

    public void registerMessageProcessor(MTMessage message, Processor processor) {
        String msgId = getMessageId(message.getOriginatingAddress(), message.getDestinationAddress());

        registerMessageProcessor(msgId, processor);
    }

    public void setMessageProcessors(Map<String, Processor> processorMap){
        if(processorMap == null || processorMap.isEmpty()){
            throw new IllegalArgumentException("The processorMap shouldn't be null or empty");
        }

        for (String phoneNumber : processorMap.keySet()) {
            String msgId = getMessageId("*", phoneNumber);

            registerMessageProcessor(msgId, processorMap.get(phoneNumber));
        }
    }

    public String getMessageId(String src, String dest) {
        return src.replaceFirst("\\+","") + ":" + dest.replaceFirst("\\+","");
    }
}