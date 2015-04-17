package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.sms.SMSResponse;

import com.sentaca.spring.smpp.mt.MTMessage;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;

import org.powermock.modules.junit4.PowerMockRunner;

/**
 * User: Alexsandr_Kolpakov Date: 10/7/13 Time: 4:07 PM
 */

@RunWith(PowerMockRunner.class)
public class VFNZSMSGatewayServiceImplTest {

    private VFNZSMSGatewayServiceImpl fixture;

    @Mock
    private SMPPServiceImpl smppServiceMock;

    @Before
    public void setUp() {
        fixture = new VFNZSMSGatewayServiceImpl();
        fixture.setSmppService(smppServiceMock);
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

        SMSResponse successResponse = new SMSResponse() {
            @Override
            public boolean isSuccessful() {
                return true;
            }

            @Override
            public String getDescriptionError() {
                return null;
            }
        };

        Mockito.doReturn(successResponse).when(smppServiceMock).sendMessage(Matchers.argThat(msgMatcher));

        SMSResponse result = fixture.send(dest, msg, source);

        Assert.assertEquals(true, result.isSuccessful());

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

        SMSResponse failResponse = new SMSResponse() {
            @Override
            public boolean isSuccessful() {
                return false;
            }

            @Override
            public String getDescriptionError() {
                return "getDescriptionError";
            }
        };

        Mockito.doReturn(failResponse).when(smppServiceMock).sendMessage(Matchers.argThat(msgMatcher));

        SMSResponse result = fixture.send(dest, msg, source);

        Assert.assertEquals(false, result.isSuccessful());

        Mockito.verify(smppServiceMock, Mockito.times(1)).sendMessage(Matchers.argThat(msgMatcher));
    }
}
