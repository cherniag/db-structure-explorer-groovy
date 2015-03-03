package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessorContainer;
import mobi.nowtechnologies.server.service.sms.SMSResponse;

import com.sentaca.spring.smpp.mt.MTMessage;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

import junit.framework.Assert;

/**
 * User: Alexsandr_Kolpakov Date: 10/7/13 Time: 4:07 PM
 */

@RunWith(PowerMockRunner.class)
public class VFNZSMSGatewayServiceImplTest {

    private VFNZSMSGatewayServiceImpl fixture;

    @Mock
    private SMPPServiceImpl smppServiceMock;

    @Mock
    private SMSMessageProcessorContainer smsMessageProcessorContainerMock;

    @Before
    public void setUp() {
        fixture = new VFNZSMSGatewayServiceImpl();
        fixture.setSmppService(smppServiceMock);
        fixture.setSmppMessageProcessorContainer(smsMessageProcessorContainerMock);
    }

    @Test
    public void testSend_WithoutProcessor_SuccessResult_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "Test";

        ArgumentMatcher<MTMessage> msgMatcher = new ArgumentMatcher<MTMessage>() {
            @Override
            public boolean matches(Object o) {
                MTMessage arg = (MTMessage) o;

                Assert.assertEquals(msg, arg.getContent());
                Assert.assertEquals(dest, arg.getDestinationAddress());
                Assert.assertEquals(source, arg.getOriginatingAddress());

                return true;
            }
        };

        Mockito.doReturn(true).when(smppServiceMock).sendMessage(Matchers.argThat(msgMatcher));

        SMSResponse result = fixture.send(dest, msg, source);

        Assert.assertEquals(true, result.isSuccessful());
        Assert.assertEquals("Sms was sent successfully from [4003] to [+64212345678] with message [Test]", result.getMessage());

        Mockito.verify(smppServiceMock, Mockito.times(1)).sendMessage(Matchers.argThat(msgMatcher));
    }

    @Test
    public void testSend_WithoutProcessor_FailureResult_Success() throws Exception {
        final String source = "4003";
        final String dest = "+64212345678";
        final String msg = "Test";

        ArgumentMatcher<MTMessage> msgMatcher = new ArgumentMatcher<MTMessage>() {
            @Override
            public boolean matches(Object o) {
                MTMessage arg = (MTMessage) o;

                Assert.assertEquals(msg, arg.getContent());
                Assert.assertEquals(dest, arg.getDestinationAddress());
                Assert.assertEquals(source, arg.getOriginatingAddress());

                return true;
            }
        };

        Mockito.doReturn(false).when(smppServiceMock).sendMessage(Matchers.argThat(msgMatcher));

        SMSResponse result = fixture.send(dest, msg, source);

        Assert.assertEquals(false, result.isSuccessful());
        Assert.assertEquals("Sms was sent unsuccessfully from [4003] to [+64212345678] with message [Test]", result.getMessage());

        Mockito.verify(smppServiceMock, Mockito.times(1)).sendMessage(Matchers.argThat(msgMatcher));
    }
}
