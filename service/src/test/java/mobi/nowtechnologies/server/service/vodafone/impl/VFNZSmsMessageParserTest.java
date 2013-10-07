package mobi.nowtechnologies.server.service.vodafone.impl;

import junit.framework.Assert;
import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/7/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class VFNZSmsMessageParserTest {

    private VFNZSmsMessageParser fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new VFNZSmsMessageParser();
    }

    @Test
    public void testParse_onNet_Success() throws Exception {
        String data = "onNet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.VF, result.getProvider());
    }

    @Test
    public void testParse_onnet_Success() throws Exception {
        String data = "onnet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.VF, result.getProvider());
    }

    @Test
    public void testParse_offNet_Success() throws Exception {
        String data = "offNet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.NON_VF, result.getProvider());
    }

    @Test
    public void testParse_offnet_Success() throws Exception {
        String data = "offnet";

        VFNZSubscriberData result = fixture.parse(data);

        Assert.assertEquals(ProviderType.NON_VF, result.getProvider());
    }
}
