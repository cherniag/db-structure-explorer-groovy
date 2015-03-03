package mobi.nowtechnologies.server.persistence.domain.streamzine.visual;

import org.junit.*;

import junit.framework.Assert;

public class AccessPolicyTest {

    @Test
    public void testIsVipMediaContent() throws Exception {
        Assert.assertTrue(AccessPolicy.enabledForVipOnly().isVipMediaContent());
    }
}