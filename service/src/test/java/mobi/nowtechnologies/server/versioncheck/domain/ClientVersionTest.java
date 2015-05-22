package mobi.nowtechnologies.server.versioncheck.domain;

import org.junit.*;
import static org.junit.Assert.*;

public class ClientVersionTest {

    @Test
    public void testFromWhenThreeDigits() throws Exception {
        // given
        final String versionString = "1.5.2";

        // when
        ClientVersion from = ClientVersion.from(versionString);

        // then
        assertEquals(1, from.major());
        assertEquals(5, from.minor());
        assertEquals(2, from.revision());
    }

    @Test
    public void testFromWhenTwoDigits() throws Exception {
        // given
        final String versionString = "1.5";

        // when
        ClientVersion from = ClientVersion.from(versionString);

        // then
        assertEquals(1, from.major());
        assertEquals(5, from.minor());
        assertEquals(0, from.revision());
    }

    @Test
    public void testFromWhenThreeDigitsAndQualifier() throws Exception {
        // given
        final String versionString = "1.5.2-RELEASE";

        // when
        ClientVersion from = ClientVersion.from(versionString);

        // then
        assertEquals(1, from.major());
        assertEquals(5, from.minor());
        assertEquals(2, from.revision());
        assertEquals("RELEASE", from.qualifier());
    }

    @Test
    public void testFromWhenTwoDigitsAndQualifier() throws Exception {
        // given
        final String versionString = "1.5-SNAPSHOT";

        // when
        ClientVersion from = ClientVersion.from(versionString);

        // then
        assertEquals(1, from.major());
        assertEquals(5, from.minor());
        assertEquals(0, from.revision());
        assertEquals("SNAPSHOT", from.qualifier());
    }
}
