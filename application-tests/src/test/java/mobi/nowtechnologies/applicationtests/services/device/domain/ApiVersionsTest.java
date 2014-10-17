package mobi.nowtechnologies.applicationtests.services.device.domain;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiVersionsTest {

    @Test
    public void testAbove() throws Exception {
        // given
        List<String> versions = Lists.newArrayList("6.0", "5.2", "6.1");

        // when
        List<String> above = ApiVersions.from(versions).above("6.0");

        // then
        assertTrue(above.contains("6.0"));
        assertTrue(above.contains("6.1"));
        assertEquals(1, above.indexOf("6.1"));
    }

    @Test
    public void testBellow() throws Exception {
        // given
        List<String> versions = Lists.newArrayList("6.0", "5.2", "6.1");

        // when
        List<String> above = ApiVersions.from(versions).bellow("6.0");

        // then
        assertTrue(above.contains("5.2"));
        assertEquals(1, above.size());
    }
}