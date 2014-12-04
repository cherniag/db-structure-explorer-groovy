package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.domain.NegativeTag;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;

import static java.util.Collections.singleton;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.INTERNAL_REPORTED;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class TrackReportingOptionsServiceTest {

    @Mock TrackRepository trackRepositoryMock;
    @InjectMocks
    TrackReportingOptionsService trackReportingOptionsService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldAssignReportingOptions() {
        //given
        long trackId = 1;

        ReportingType reportingType = INTERNAL_REPORTED;
        String negativeTagString = "a";
        TrackReportingOptionsDto trackReportingOptionsDto = new TrackReportingOptionsDto().withTrackId(trackId).withNegativeTags(singleton(negativeTagString)).withReportingType(reportingType);

        Track track = new Track().withNegativeTags(new HashSet<NegativeTag>());
        track.setId(trackId);

        when(trackRepositoryMock.findOne(trackId)).thenReturn(track);

        when(trackRepositoryMock.save(track)).thenReturn(track);

        //when
        Track actualTrack = trackReportingOptionsService.assignReportingOptions(trackReportingOptionsDto);

        //then
        assertThat(actualTrack.getReportingType(), is(reportingType));

        assertThat(actualTrack.getNegativeTags().size(), is(1));
        assertThat(actualTrack.getNegativeTags().iterator().next().getTag(), is(negativeTagString));

        verify(trackRepositoryMock).findOne(trackId);
        verify(trackRepositoryMock).save(track);
    }
}