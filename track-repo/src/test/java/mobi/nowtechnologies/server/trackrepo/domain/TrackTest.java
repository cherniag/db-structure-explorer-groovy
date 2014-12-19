package mobi.nowtechnologies.server.trackrepo.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
}