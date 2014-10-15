package mobi.nowtechnologies.applicationtests.features.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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