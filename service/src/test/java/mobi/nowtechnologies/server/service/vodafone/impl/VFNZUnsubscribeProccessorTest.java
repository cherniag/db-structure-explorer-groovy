package mobi.nowtechnologies.server.service.vodafone.impl;

import com.sentaca.spring.smpp.mo.MOMessage;
import junit.framework.Assert;
import mobi.nowtechnologies.server.service.UserService;
import org.jsmpp.bean.DeliverSm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smslib.Message;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/21/13
 * Time: 11:28 AM
 */
@RunWith(PowerMockRunner.class)
public class VFNZUnsubscribeProccessorTest {
    private VFNZUnsubscribeProccessor fixture;

    @Mock
    private UserService userServiceMock;

    @Before
    public void setUp() throws Exception {
        fixture = new VFNZUnsubscribeProccessor();
        fixture.setUserService(userServiceMock);
    }

    @Test
    public void testProcess_Stop1Msg_Success() throws Exception {
        MOMessage moMessage = new MOMessage("6421111111", "4003", "Stop1", Message.MessageEncodings.ENC8BIT);
        fixture.setOperatorName("vf1");
        fixture.setStopText("stop1");

        Mockito.doReturn(null).when(userServiceMock).unsubscribeUser(moMessage.getOriginator(), "vf1");

        fixture.process(moMessage);

        verify(userServiceMock, times(1)).unsubscribeUser(moMessage.getOriginator(), "vf1");
    }

    @Test
    public void testProcess_STOP1Msg_Success() throws Exception {
        MOMessage moMessage = new MOMessage("6421111111", "4003", "STOP1 df", Message.MessageEncodings.ENC8BIT);
        fixture.setOperatorName("vf1");
        fixture.setStopText("stop1");

        Mockito.doReturn(null).when(userServiceMock).unsubscribeUser(moMessage.getOriginator(), "vf1");

        fixture.process(moMessage);

        verify(userServiceMock, times(1)).unsubscribeUser(moMessage.getOriginator(), "vf1");
    }

    @Test
    public void testProcess_stop1Msg_Success() throws Exception {
        MOMessage moMessage = new MOMessage("6421111111", "4003", "stop1 dfd", Message.MessageEncodings.ENC8BIT);
        fixture.setOperatorName("vf1");
        fixture.setStopText("stop1");

        Mockito.doReturn(null).when(userServiceMock).unsubscribeUser(moMessage.getOriginator(), "vf1");

        fixture.process(moMessage);

        verify(userServiceMock, times(1)).unsubscribeUser(moMessage.getOriginator(), "vf1");
    }

    @Test
    public void testProcess_notstop1Msg_Success() throws Exception {
        MOMessage moMessage = new MOMessage("6421111111", "4003", "notstop dddd", Message.MessageEncodings.ENC8BIT);
        fixture.setOperatorName("vf1");
        fixture.setStopText("stop1");

        Mockito.doReturn(null).when(userServiceMock).unsubscribeUser(moMessage.getOriginator(), "vf1");

        fixture.process(moMessage);

        verify(userServiceMock, times(0)).unsubscribeUser(moMessage.getOriginator(), "vf1");
    }

    @Test
    public void testSupports_Supported_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setDestAddress("4003");
        fixture.setSupportedNumbers(new HashSet<String>(Arrays.asList("4003", "5803")));

        boolean result = fixture.supports(deliverSm);

        Assert.assertEquals(true, result);
    }

    @Test
    public void testSupports_NotSupported_NotSupportedNumber_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setDestAddress("4003");
        fixture.setSupportedNumbers(new HashSet<String>(Arrays.asList("4008", "5803")));

        boolean result = fixture.supports(deliverSm);

        Assert.assertEquals(false, result);
    }

    @Test
    public void testSupports_NotSupported_IsDeliverReceipt_Success() throws Exception {
        DeliverSm deliverSm = new DeliverSm();
        deliverSm.setDestAddress("4003");
        deliverSm.setSmscDeliveryReceipt();
        fixture.setSupportedNumbers(new HashSet<String>(Arrays.asList("4003", "5803")));

        boolean result = fixture.supports(deliverSm);

        Assert.assertEquals(false, result);
    }
}
