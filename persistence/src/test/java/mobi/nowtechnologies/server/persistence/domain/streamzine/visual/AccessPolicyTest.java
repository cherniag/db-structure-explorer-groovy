package mobi.nowtechnologies.server.persistence.domain.streamzine.visual;

import junit.framework.Assert;
import org.junit.Test;

public class AccessPolicyTest {

    @Test
    public void testIsVipMediaContent() throws Exception {
        Assert.assertTrue(AccessPolicy.enabledForVipOnly().isVipMediaContent());
    }
}