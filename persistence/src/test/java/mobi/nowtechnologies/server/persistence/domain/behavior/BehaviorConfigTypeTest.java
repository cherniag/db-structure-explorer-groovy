package mobi.nowtechnologies.server.persistence.domain.behavior;

import org.junit.*;
import static org.junit.Assert.*;

public class BehaviorConfigTypeTest {

    @Test
    public void testIsDefault() throws Exception {
        assertTrue(BehaviorConfigType.DEFAULT.isDefault());
    }
}