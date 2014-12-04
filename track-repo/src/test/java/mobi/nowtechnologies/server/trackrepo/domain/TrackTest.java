package mobi.nowtechnologies.server.trackrepo.domain;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.INTERNAL_REPORTED;
import static org.junit.Assert.*;

import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
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
}