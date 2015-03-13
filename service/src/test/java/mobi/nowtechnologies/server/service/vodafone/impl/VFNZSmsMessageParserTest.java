package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.shared.enums.ProviderType;

import com.sentaca.spring.smpp.mo.MOMessage;
import org.smslib.Message;

import org.junit.*;

/**
 * User: Alexsandr_Kolpakov Date: 10/7/13 Time: 2:13 PM
 */
public class VFNZSmsMessageParserTest {

    private VFNZSubscriberDataParser fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new VFNZSubscriberDataParser();
    }

    @Test
    public void testParse_onNet_Success() throws Exception {
        MOMessage data = createMessage("onNet");

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.VF, result.getProvider());
        Assert.assertEquals("+6421111111", result.getPhoneNumber());
    }

    @Test
    public void testParse_onnet_Success() throws Exception {
        MOMessage data = createMessage("onnet");

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.VF, result.getProvider());
        Assert.assertEquals("+6421111111", result.getPhoneNumber());
    }

    @Test
    public void testParse_offNet_Success() throws Exception {
        MOMessage data = createMessage("offNet");

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.NON_VF, result.getProvider());
        Assert.assertEquals("+6421111111", result.getPhoneNumber());
    }

    @Test
    public void testParse_offnet_Success() throws Exception {
        MOMessage data = createMessage("offnet");

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.NON_VF, result.getProvider());
        Assert.assertEquals("+6421111111", result.getPhoneNumber());
    }

    private MOMessage createMessage(String message) {
        return new MOMessage("5803", "6421111111", message, Message.MessageEncodings.ENC8BIT);
    }
}
