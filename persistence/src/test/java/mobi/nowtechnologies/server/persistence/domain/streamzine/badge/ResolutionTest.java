package mobi.nowtechnologies.server.persistence.domain.streamzine.badge;

import org.junit.*;

public class ResolutionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor() throws Exception {
        new Resolution("un recognized device type", 10, 10);
    }
}