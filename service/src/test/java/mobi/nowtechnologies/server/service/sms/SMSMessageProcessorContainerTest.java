package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.mo.MOMessage;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSubscriberData;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSubscriberDataParser;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.jsmpp.bean.DeliverSm;
import org.junit.Before;
import org.junit.Test;
import org.smslib.Message;

import java.util.ArrayList;
import java.util.Collections;

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
    public void testProcessInboundMessage_Supported_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final BasicSMSMessageProcessor<VFNZSubscriberData> processor = spy(new BasicSMSMessageProcessor<VFNZSubscriberData>() {
            @Override
            public void process(VFNZSubscriberData data) {
                fixture.LOGGER.info("process msg");
            }

            @Override
            public boolean supports(DeliverSm deliverSm) {
                return true;
            }
        });
        processor.withMessageParser(parserSpy);
        fixture.setMessageProcessors(new ArrayList<SMSMessageProcessor>(Collections.singletonList(processor)));

        doReturn(data).when(parserSpy).parse(eq(moMessage));

        fixture.processInboundMessage(null, moMessage);

        verify(parserSpy, times(1)).parse(eq(moMessage));
        verify(processor, times(1)).process(eq(data));
    }

    @Test
    public void testProcessInboundMessage_Supported_NoParser_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final BasicSMSMessageProcessor<MOMessage> processor = spy(new BasicSMSMessageProcessor<MOMessage>() {
            @Override
            public boolean supports(DeliverSm deliverSm) {
                return true;
            }

            @Override
            public void process(MOMessage data) {
                fixture.LOGGER.info("process msg");
            }
        });
        fixture.setMessageProcessors(new ArrayList<SMSMessageProcessor>(Collections.singletonList(processor)));

        doReturn(data).when(parserSpy).parse(eq(moMessage));

        fixture.processInboundMessage(null, moMessage);

        verify(parserSpy, times(0)).parse(eq(moMessage));
        verify(processor, times(1)).process(eq(moMessage));
    }

    @Test
    public void testProcessInboundMessage_Supported_NoParser_NotProcessed_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final SMSMessageProcessor<VFNZSubscriberData> processor = spy(new BasicSMSMessageProcessor<VFNZSubscriberData>() {
            @Override
            public boolean supports(DeliverSm deliverSm) {
                return true;
            }

            @Override
            public void process(VFNZSubscriberData data) {
                fixture.LOGGER.info("process msg");
            }
        });
        fixture.setMessageProcessors(new ArrayList<SMSMessageProcessor>(Collections.singletonList(processor)));

        doReturn(data).when(parserSpy).parse(eq(moMessage));

        fixture.processInboundMessage(null, moMessage);

        verify(parserSpy, times(0)).parse(eq(moMessage));
        verify(processor, times(0)).process(any(VFNZSubscriberData.class));
    }

    @Test
    public void testProcessInboundMessage_NotSupported_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        final BasicSMSMessageProcessor<VFNZSubscriberData> processor = spy(new BasicSMSMessageProcessor<VFNZSubscriberData>() {
            @Override
            public boolean supports(DeliverSm deliverSm) {
                return false;
            }

            @Override
            public void process(VFNZSubscriberData data) {
                fixture.LOGGER.info("process msg");
            }
        });
        fixture.setMessageProcessors(new ArrayList<SMSMessageProcessor>(Collections.singletonList(processor)));

        doReturn(data).when(parserSpy).parse(eq(moMessage));

        fixture.processInboundMessage(null, moMessage);

        verify(parserSpy, times(0)).parse(eq(moMessage));
        verify(processor, times(0)).process(any(VFNZSubscriberData.class));
    }

    @Test
    public void testProcessInboundMessage_NoParser_Supported_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "onnet";
        final VFNZSubscriberData data = new VFNZSubscriberData();
        data.setProvider(ProviderType.VF);
        MOMessage moMessage = new MOMessage(source, dest, msg, Message.MessageEncodings.ENC7BIT);
        DeliverSm deliverSm = new DeliverSm();
        final BasicSMSMessageProcessor<DeliverSm> processor = spy(new BasicSMSMessageProcessor<DeliverSm>() {
            @Override
            public void process(DeliverSm data) {
                fixture.LOGGER.info("process msg");
            }

            @Override
            public boolean supports(DeliverSm deliverSm) {
                return true;
            }
        });
        fixture.setMessageProcessors(new ArrayList<SMSMessageProcessor>(Collections.singletonList(processor)));

        doReturn(data).when(parserSpy).parse(eq(moMessage));

        fixture.processStatusReportMessage(deliverSm);

        verify(parserSpy, times(0)).parse(eq(moMessage));
        verify(processor, times(1)).process(eq(deliverSm));
    }

}
