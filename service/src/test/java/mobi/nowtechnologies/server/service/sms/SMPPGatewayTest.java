package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.BindConfiguration;
import com.sentaca.spring.smpp.mo.MessageReceiver;
import com.sentaca.spring.smpp.monitoring.SMPPMonitoringAgent;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/22/13
 * Time: 10:37 AM
 */
@RunWith(PowerMockRunner.class)
public class SMPPGatewayTest {
    private SMPPGateway fixture;

    @Mock
    private BindConfiguration bindConfigurationMock;

    @Mock
    private MessageReceiver messageReceiverMock;

    @Mock
    private SMPPMonitoringAgent smppMonitoringAgentMock;


    @Before
    public void setUp() throws Exception {
        fixture = new SMPPGateway(bindConfigurationMock, messageReceiverMock, smppMonitoringAgentMock, false);
    }

    @Test
    public void testFormatTimeFromMillis_NotMillis_Success() throws Exception {
        int delay = 500;

        String result = fixture.formatTimeFromMillis(delay);

        Assert.assertEquals("000000000000000R", result);
    }

    @Test
    public void testFormatTimeFromMillis_Seconds_Success() throws Exception {
        int delay = 20000;

        String result = fixture.formatTimeFromMillis(delay);

        Assert.assertEquals("000000000020000R", result);
    }

    @Test
    public void testFormatTimeFromMillis_Minutes_Success() throws Exception {
        int delay = 1200000;

        String result = fixture.formatTimeFromMillis(delay);

        Assert.assertEquals("000000002000000R", result);
    }

    @Test
    public void testFormatTimeFromMillis_Hours_Success() throws Exception {
        int delay = 3600000;

        String result = fixture.formatTimeFromMillis(delay);

        Assert.assertEquals("000000010000000R", result);
    }
}
