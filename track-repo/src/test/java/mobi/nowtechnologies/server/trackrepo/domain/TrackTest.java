package mobi.nowtechnologies.server.trackrepo.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class TrackTest {

    @Test
    public void shouldAssignNegativeTags() {
        //given
        Set<NegativeTag> negativeTags = new HashSet<NegativeTag>(asList(new NegativeTag(), new NegativeTag()));
        Track track = new Track().withNegativeTags(negativeTags);
        Set<String> negativeTagSet = singleton("a");

        //when
        Track actualTrack = track.assignNegativeTags(negativeTagSet);

        //then
        assertThat(actualTrack.getNegativeTags().size(), is(1));
        assertThat(actualTrack.getNegativeTags(), is(negativeTags));
        assertThat(actualTrack.getNegativeTags().iterator().next().getTag(), is("a"));
    }

    @Test
    public void shouldConfirmVideo() {
        //given
        Track track = new Track();

        AssetFile assetFileMock = mock(AssetFile.class);
        when(assetFileMock.getType()).thenReturn(AssetFile.FileType.VIDEO);

        track.setFiles(Collections.singleton(assetFileMock));

        //when
        boolean video = track.isVideo();

        //then
        assertTrue(video);
    }

    @Test
    public void shouldConfirmNotVideo() {
        //given
        Track track = new Track();

        AssetFile assetFileMock = mock(AssetFile.class);
        when(assetFileMock.getType()).thenReturn(AssetFile.FileType.IMAGE);

        track.setFiles(Collections.singleton(assetFileMock));

        //when
        boolean video = track.isVideo();

        //then
        assertFalse(video);
    }
}