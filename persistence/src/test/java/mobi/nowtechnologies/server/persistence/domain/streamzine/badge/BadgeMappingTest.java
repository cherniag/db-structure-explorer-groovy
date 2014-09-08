package mobi.nowtechnologies.server.persistence.domain.streamzine.badge;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class BadgeMappingTest {

    @Test
    public void testGeneral() throws Exception {
        Community c = mock(Community.class);
        FilenameAlias alias = mock(FilenameAlias.class);

        BadgeMapping mapping = BadgeMapping.general(c, alias);

        assertSame(c, mapping.getCommunity());
        assertSame(alias, mapping.getFilenameAlias());
        assertSame(alias, mapping.getOriginalFilenameAlias());
    }

    @Test
    public void testSpecific() throws Exception {
        Resolution r = mock(Resolution.class);
        Community c = mock(Community.class);
        FilenameAlias alias = mock(FilenameAlias.class);

        BadgeMapping mapping = BadgeMapping.specific(r, c, alias);

        assertSame(r, mapping.getResolution());
        assertSame(c, mapping.getCommunity());
        assertNull(mapping.getFilenameAlias());
        assertSame(alias, mapping.getOriginalFilenameAlias());
    }
}