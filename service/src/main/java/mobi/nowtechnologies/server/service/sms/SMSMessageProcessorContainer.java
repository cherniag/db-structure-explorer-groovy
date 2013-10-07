package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MOMessage;
import com.sentaca.spring.smpp.mt.MTMessage;
import mobi.nowtechnologies.server.shared.Parser;
import mobi.nowtechnologies.server.shared.Processor;
import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SMSMessageProcessorContainer<T> extends DeliverSmMessageProcessor {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final int MAX_QUEUE_SIZE = 10;
    protected ConcurrentHashMap<String, ArrayBlockingQueue<Processor<T>>> processors = new ConcurrentHashMap<String, ArrayBlockingQueue<Processor<T>>>();
    private Parser<String, T> messageParser;

    @Override
    public void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage) {
        LOGGER.info("SMSMessageProcessorContainer has got sms message: [{}]", new Object[]{inboundMessage});

        T data = messageParser.parse(inboundMessage.getText());

        Processor<T> processor = getMessageProcessor(inboundMessage);
        processor.process(data);
    }

    public void registerMessageProcessor(MTMessage message, Processor<T> processor) {
        String msgId = getMessageId(message.getOriginatingAddress(), message.getDestinationAddress());

        ArrayBlockingQueue<Processor<T>> queue = null;
        synchronized (processors) {
            queue = processors.get(msgId);
            if (queue == null) {
                queue = new ArrayBlockingQueue<Processor<T>>(MAX_QUEUE_SIZE, true);
                processors.put(msgId, queue);
            }
        }

        queue.offer(processor);

        LOGGER.info("SMSMessageProcessorContainer has registered message: [{}] with messageId = [{}]", new Object[]{message, msgId});
    }

    public Processor<T> getMessageProcessor(MOMessage message) {
        String msgId = getMessageId(message.getOriginator(), message.getDestAddress());

        Processor<T> processor = processors.get(msgId).poll();

        if(processor != null)
            LOGGER.info("SMSMessageProcessorContainer has found processor: [{}] for messageId = [{}]", new Object[]{processor, msgId});
        else
            LOGGER.info("SMSMessageProcessorContainer hasn't found processor for messageId = [{}]", new Object[]{msgId});

        return processor;
    }

    public String getMessageId(String src, String dest) {
        return src + ":" + dest;
    }

    public void setMessageParser(Parser<String, T> messageParser) {
        this.messageParser = messageParser;
    }
}