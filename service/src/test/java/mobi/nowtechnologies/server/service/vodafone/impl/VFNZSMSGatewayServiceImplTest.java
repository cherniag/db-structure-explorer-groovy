package mobi.nowtechnologies.server.service.vodafone.impl;

import com.sentaca.spring.smpp.SMPPService;
import com.sentaca.spring.smpp.mt.MTMessage;
import junit.framework.Assert;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessorContainer;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.shared.Processor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/7/13
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(PowerMockRunner.class)
public class VFNZSMSGatewayServiceImplTest {
    private VFNZSMSGatewayServiceImpl fixture;

    @Mock
    private SMPPService smppServiceMock;

    @Mock
    private SMSMessageProcessorContainer smsMessageProcessorContainerMock;

    @Before
    public void setUp(){
        fixture = new VFNZSMSGatewayServiceImpl();
        fixture.setSmppService(smppServiceMock);
        fixture.setSmppMessageProcessorContainer(smsMessageProcessorContainerMock);
    }

    @Test
    public void testSend_WithoutProcessor_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "Test";

        ArgumentMatcher<MTMessage> msgMatcher = new ArgumentMatcher<MTMessage>() {
            @Override
            public boolean matches(Object o) {
                MTMessage arg = (MTMessage)o;

                Assert.assertEquals(msg, arg.getContent());
                Assert.assertEquals(dest, arg.getDestinationAddress());
                Assert.assertEquals(source, arg.getOriginatingAddress());

                return true;
            }
        };

        Mockito.doNothing().when(smppServiceMock).send(Matchers.argThat(msgMatcher));

        SMSResponse result = fixture.send(dest, msg, source);

        Assert.assertEquals(true, result.isSuccessful());
        Assert.assertEquals("Sms was sent successfully from 4003 to +64212345678 with message Test", result.getMessage());

        Mockito.verify(smppServiceMock, Mockito.times(1)).send(Matchers.argThat(msgMatcher));
        Mockito.verify(smsMessageProcessorContainerMock, Mockito.times(0)).registerMessageProcessor(any(MTMessage.class), any(Processor.class));
    }

    @Test
    public void testSend_WithProcessor_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "Test";
        final Processor processor = new Processor() {
            @Override
            public void process(Object data) {
                fixture.LOGGER.info("process msg");
            }
        };

        ArgumentMatcher <MTMessage> msgMatcher = new ArgumentMatcher<MTMessage>() {
            @Override
            public boolean matches(Object o) {
                MTMessage arg = (MTMessage)o;

                Assert.assertEquals(msg, arg.getContent());
                Assert.assertEquals(dest, arg.getDestinationAddress());
                Assert.assertEquals(source, arg.getOriginatingAddress());

                return true;
            }
        };

        Mockito.doNothing().when(smppServiceMock).send(Matchers.argThat(msgMatcher));
        Mockito.doNothing().when(smsMessageProcessorContainerMock).registerMessageProcessor(Matchers.argThat(msgMatcher), any(Processor.class));

        SMSResponse result = fixture.send(dest, msg, source, processor);

        Assert.assertEquals(true, result.isSuccessful());
        Assert.assertEquals("Sms was sent successfully from 4003 to +64212345678 with message Test", result.getMessage());

        Mockito.verify(smppServiceMock, Mockito.times(1)).send(Matchers.argThat(msgMatcher));
        Mockito.verify(smsMessageProcessorContainerMock, Mockito.times(1)).registerMessageProcessor(Matchers.argThat(msgMatcher), any(Processor.class));
    }
}
