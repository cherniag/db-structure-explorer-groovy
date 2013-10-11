package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.mo.MOMessage;
import com.sentaca.spring.smpp.mt.MTMessage;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSubscriberData;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSubscriberDataParser;
import mobi.nowtechnologies.server.shared.Processor;
import org.junit.Before;
import org.junit.Test;
import org.smslib.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/7/13
 * Time: 5:34 PM
 */
public class SMSMessageProcessorContainerTest {
    private SMSMessageProcessorContainer fixture;
    private VFNZSubscriberDataParser parserSpy;

    @Before
    public void setUp() throws Exception {
        fixture = spy(new SMSMessageProcessorContainer());
        parserSpy = (spy(new VFNZSubscriberDataParser()));
    }

    @Test
    public void testProcessInboundMessage_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final String msgId = source + ":" + dest;
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final Processor<VFNZSubscriberData> processor = spy(new Processor<VFNZSubscriberData>() {
            {
                messageParser = parserSpy;
            }
            @Override
            public void process(VFNZSubscriberData data) {
                fixture.LOGGER.info("process msg");
            }
        });
        ArrayBlockingQueue<Processor> queue = new ArrayBlockingQueue<Processor>(5);
        queue.offer(processor);
        fixture.processors.put(msgId, queue);

        doReturn(data).when(parserSpy).parse(eq(msg));
        doReturn(processor).when(fixture).getMessageProcessor(eq(moMessage));

        fixture.processInboundMessage(null, moMessage);

        verify(parserSpy, times(1)).parse(eq(msg));
        verify(processor, times(1)).process(eq(data));
        verify(fixture, times(1)).getMessageProcessor(eq(moMessage));
    }

    @Test
    public void testProcessInboundMessage_NoParser_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final String msgId = source + ":" + dest;
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final Processor<MOMessage> processor = spy(new Processor<MOMessage>() {
            @Override
            public void process(MOMessage data) {
                fixture.LOGGER.info("process msg");
            }
        });
        ArrayBlockingQueue<Processor> queue = new ArrayBlockingQueue<Processor>(5);
        queue.offer(processor);
        fixture.processors.put(msgId, queue);

        doReturn(data).when(parserSpy).parse(eq(msg));
        doReturn(processor).when(fixture).getMessageProcessor(eq(moMessage));

        fixture.processInboundMessage(null, moMessage);

        verify(parserSpy, times(0)).parse(eq(msg));
        verify(processor, times(1)).process(eq(moMessage));
        verify(fixture, times(1)).getMessageProcessor(eq(moMessage));
    }

    @Test
    public void testProcessInboundMessage_NotProcessor_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final String msgId = source + ":" + dest;
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final Processor<VFNZSubscriberData> processor = spy(new Processor<VFNZSubscriberData>() {
            {
                messageParser = parserSpy;
            }
            @Override
            public void process(VFNZSubscriberData data) {
                fixture.LOGGER.info("process msg");
            }
        });
        ArrayBlockingQueue<Processor> queue = new ArrayBlockingQueue<Processor>(5);
        queue.offer(processor);
        fixture.processors.put(msgId, queue);

        doReturn(data).when(parserSpy).parse(eq(msg));
        doReturn(null).when(fixture).getMessageProcessor(eq(moMessage));

        fixture.processInboundMessage(null, moMessage);

        verify(parserSpy, times(0)).parse(eq(msg));
        verify(processor, times(0)).process(eq(data));
        verify(fixture, times(1)).getMessageProcessor(eq(moMessage));
    }

    @Test
    public void testSetMessageProcessors_Success() throws Exception {
        final String dest = "4003";
        final String msgId = "*:4003";
        final Processor processor = new Processor() {
            @Override
            public void process(Object data) {
                fixture.LOGGER.info("process msg");
            }
        };
        Map<String, Processor> processorMap = new HashMap<String, Processor>();
        processorMap.put(dest, processor);

        fixture.setMessageProcessors(processorMap);

        assertEquals(1, fixture.processors.size());
        assertEquals(1, ((ArrayBlockingQueue<Processor>)fixture.processors.get(msgId)).size());
        Processor result = ((ArrayBlockingQueue<Processor>)fixture.processors.get(msgId)).poll();
        assertEquals(result, processor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetMessageProcessors_NullorEmptyMap_Failure() throws Exception {
        Map<String, Processor> processorMap = null;

        fixture.setMessageProcessors(processorMap);
    }

        @Test
    public void testRegisterMessageProcessor_OneProcessor_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "Test";
        final String msgId = "4003:64212345678";
        MTMessage mtMessage = new MTMessage(source, dest, msg);
        final Processor processor = new Processor() {
            @Override
            public void process(Object data) {
                fixture.LOGGER.info("process msg");
            }
        };

        fixture.registerMessageProcessor(mtMessage, processor);

        assertEquals(1, fixture.processors.size());
        assertEquals(1, ((ArrayBlockingQueue<Processor>)fixture.processors.get(msgId)).size());
        Processor result = ((ArrayBlockingQueue<Processor>)fixture.processors.get(msgId)).poll();
        assertEquals(result, processor);
    }

    @Test
    public void testRegisterMessageProcessor_TwoProcessors_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "Test";
        final String msgId = "4003:64212345678";
        MTMessage mtMessage = new MTMessage(source, dest, msg);
        final Processor processor = new Processor() {
            @Override
            public void process(Object data) {
                fixture.LOGGER.info("process msg");
            }
        };
        final Processor processor1 = new Processor() {
            @Override
            public void process(Object data) {
                fixture.LOGGER.info("process msg1");
            }
        };
        ArrayBlockingQueue<Processor> queue = new ArrayBlockingQueue<Processor>(5);
        queue.offer(processor1);
        fixture.processors.put(msgId, queue);

        fixture.registerMessageProcessor(mtMessage, processor);

        assertEquals(1, fixture.processors.size());
        assertEquals(2, ((ArrayBlockingQueue<Processor>)fixture.processors.get(msgId)).size());
        Processor result = ((ArrayBlockingQueue<Processor>)fixture.processors.get(msgId)).poll();
        assertEquals(result, processor1);
    }

    @Test
    public void testGetMessageProcessor_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "Test";
        final String msgId = "4003:64212345678";
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final Processor processor = new Processor() {
            @Override
            public void process(Object data) {
                fixture.LOGGER.info("process msg");
            }
        };
        ArrayBlockingQueue<Processor> queue = new ArrayBlockingQueue<Processor>(5);
        queue.offer(processor);
        fixture.processors.put(msgId, queue);

        Processor result = fixture.getMessageProcessor(moMessage);

        assertEquals(processor, result);
    }
}
