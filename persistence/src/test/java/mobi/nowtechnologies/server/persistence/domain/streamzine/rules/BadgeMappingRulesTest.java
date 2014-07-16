package mobi.nowtechnologies.server.persistence.domain.streamzine.rules;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.ContentType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.junit.Test;
import org.modelmapper.internal.util.Assert;

public class BadgeMappingRulesTest {

    @Test
    public void testAllowed() throws Exception {
        Assert.isTrue(BadgeMappingRules.allowed(ShapeType.WIDE, ContentType.MUSIC, MusicType.TRACK));
        Assert.isTrue(BadgeMappingRules.allowed(ShapeType.WIDE, ContentType.MUSIC, MusicType.PLAYLIST));

        Assert.isTrue(BadgeMappingRules.allowed(ShapeType.NARROW, ContentType.MUSIC, MusicType.TRACK));
        Assert.isTrue(BadgeMappingRules.allowed(ShapeType.NARROW, ContentType.MUSIC, MusicType.PLAYLIST));

        Assert.isTrue(BadgeMappingRules.allowed(ShapeType.NARROW, ContentType.PROMOTIONAL, LinkLocationType.INTERNAL_AD));
    }
}