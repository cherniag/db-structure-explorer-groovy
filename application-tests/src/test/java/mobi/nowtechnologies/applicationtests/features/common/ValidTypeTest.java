package mobi.nowtechnologies.applicationtests.features.common;

import org.junit.*;
import static org.junit.Assert.*;

public class ValidTypeTest {

    @Test
    public void testGetNotValid() throws Exception {
        String data = "some_data";
        String spoiledData = ValidType.NotValid.decide(data);

        assertNotEquals(spoiledData, data);
        assertEquals(spoiledData.length(), data.length());
    }

    @Test
    public void testGetValid() throws Exception {
        String data = "some_data";
        String spoiledData = ValidType.Valid.decide(data);

        assertEquals(spoiledData, data);
    }
}