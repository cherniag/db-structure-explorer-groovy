package mobi.nowtechnologies.server.persistence.domain.streamzine.types;

import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.MusicType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.NewsType;

import org.junit.*;
import static org.junit.Assert.*;

public class TypeToSubTypePairTest {

    @Test
    public void testRestoreSubType() throws Exception {
        assertEquals(MusicType.TRACK, TypeToSubTypePair.restoreSubType(ContentType.MUSIC, MusicType.TRACK.name()));
        assertEquals(MusicType.PLAYLIST, TypeToSubTypePair.restoreSubType(ContentType.MUSIC, MusicType.PLAYLIST.name()));
        assertEquals(MusicType.MANUAL_COMPILATION, TypeToSubTypePair.restoreSubType(ContentType.MUSIC, MusicType.MANUAL_COMPILATION.name()));

        assertEquals(LinkLocationType.EXTERNAL_AD, TypeToSubTypePair.restoreSubType(ContentType.PROMOTIONAL, LinkLocationType.EXTERNAL_AD.name()));
        assertEquals(LinkLocationType.INTERNAL_AD, TypeToSubTypePair.restoreSubType(ContentType.PROMOTIONAL, LinkLocationType.INTERNAL_AD.name()));

        assertEquals(NewsType.LIST, TypeToSubTypePair.restoreSubType(ContentType.NEWS, NewsType.LIST.name()));
        assertEquals(NewsType.STORY, TypeToSubTypePair.restoreSubType(ContentType.NEWS, NewsType.STORY.name()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRestoreException() throws Exception {
        assertEquals(MusicType.TRACK, TypeToSubTypePair.restoreSubType(ContentType.MUSIC, "bullshit"));
    }
}