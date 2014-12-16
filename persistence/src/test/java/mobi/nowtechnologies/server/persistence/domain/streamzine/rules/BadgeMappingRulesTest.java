package mobi.nowtechnologies.server.persistence.domain.streamzine.rules;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BadgeMappingRulesTest {

    @Test
    public void testAllowed() throws Exception {
        assertTrue(BadgeMappingRules.allowed(ShapeType.WIDE, ContentType.MUSIC, MusicType.TRACK));
        assertTrue(BadgeMappingRules.allowed(ShapeType.WIDE, ContentType.MUSIC, MusicType.PLAYLIST));
        assertTrue(BadgeMappingRules.allowed(ShapeType.WIDE, ContentType.PROMOTIONAL, LinkLocationType.EXTERNAL_AD));

        assertTrue(BadgeMappingRules.allowed(ShapeType.NARROW, ContentType.MUSIC, MusicType.TRACK));
        assertTrue(BadgeMappingRules.allowed(ShapeType.NARROW, ContentType.MUSIC, MusicType.PLAYLIST));

        assertTrue(BadgeMappingRules.allowed(ShapeType.NARROW, ContentType.PROMOTIONAL, LinkLocationType.INTERNAL_AD));
        assertTrue(BadgeMappingRules.allowed(ShapeType.NARROW, ContentType.PROMOTIONAL, LinkLocationType.EXTERNAL_AD));
    }
}