package mobi.nowtechnologies.server.persistence.domain.behavior;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BehaviorConfigTypeTest {

    @Test
    public void testIsDefault() throws Exception {
        assertTrue(BehaviorConfigType.DEFAULT.isDefault());
    }
}