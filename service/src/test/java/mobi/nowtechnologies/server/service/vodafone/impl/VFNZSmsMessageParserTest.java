package mobi.nowtechnologies.server.service.vodafone.impl;

import junit.framework.Assert;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.junit.Before;
import org.junit.Test;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/7/13
 * Time: 2:13 PM
 */
public class VFNZSmsMessageParserTest {

    private VFNZSubscriberDataParser fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new VFNZSubscriberDataParser();
    }

    @Test
    public void testParse_onNet_Success() throws Exception {
        String data = "onNet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(VF, result.getProvider());
    }

    @Test
    public void testParse_onnet_Success() throws Exception {
        String data = "onnet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(VF, result.getProvider());
    }

    @Test
    public void testParse_offNet_Success() throws Exception {
        String data = "offNet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(NON_VF, result.getProvider());
    }

    @Test
    public void testParse_offnet_Success() throws Exception {
        String data = "offnet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(NON_VF, result.getProvider());
    }
}
