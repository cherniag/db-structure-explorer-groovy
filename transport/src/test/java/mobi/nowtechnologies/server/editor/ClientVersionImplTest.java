package mobi.nowtechnologies.server.editor;

import mobi.nowtechnologies.server.service.versioncheck.ClientVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientVersionImplTest {
    @Test
    public void testFromWhenThreeDigits() throws Exception {
        // given
        final String versionString = "1.5.2";

        // when
        ClientVersion from = ClientVersionImpl.from(versionString);

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
        ClientVersion from = ClientVersionImpl.from(versionString);

        // then
        assertEquals(1, from.major());
        assertEquals(5, from.minor());
        assertEquals(0, from.revision());
    }

    @Test
    public void testFromWhenThreeDigitsAndQualifier() throws Exception {
        // given
        final String versionString = "1.5.2.RELEASE";

        // when
        ClientVersion from = ClientVersionImpl.from(versionString);

        // then
        assertEquals(1, from.major());
        assertEquals(5, from.minor());
        assertEquals(0, from.revision());
        assertEquals("RELEASE", from.qualifier());
    }

    @Test
    public void testFromWhenTwoDigitsAndQualifier() throws Exception {
        // given
        final String versionString = "1.5.SNAPSHOT";

        // when
        ClientVersion from = ClientVersionImpl.from(versionString);

        // then
        assertEquals(1, from.major());
        assertEquals(5, from.minor());
        assertEquals(0, from.revision());
        assertEquals("SNAPSHOT", from.qualifier());
    }
}
