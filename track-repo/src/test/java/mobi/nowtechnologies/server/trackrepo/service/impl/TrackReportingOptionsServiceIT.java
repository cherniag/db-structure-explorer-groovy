package mobi.nowtechnologies.server.trackrepo.service.impl;

import mobi.nowtechnologies.server.trackrepo.controller.AbstractTrackRepoIT;
import mobi.nowtechnologies.server.trackrepo.domain.NegativeTag;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.TrackReportingOptionsDto;
import mobi.nowtechnologies.server.trackrepo.enums.ReportingType;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.DOWNLOAD;
import static mobi.nowtechnologies.server.trackrepo.enums.ReportingType.INTERNAL_REPORTED;

import javax.annotation.Resource;

import java.util.Date;
import java.util.HashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.core.Is.is;

public class TrackReportingOptionsServiceIT extends AbstractTrackRepoIT {

    @Resource
    TrackReportingOptionsService trackReportingOptionsService;
    @Resource
    TrackRepository trackRepository;

    @Test
    public void shouldAssignReportingOptions() {
        //given
        Track track = trackRepository.save(
            new Track().withNegativeTags(new HashSet<NegativeTag>(asList(new NegativeTag().withTag("b")))).withIngestor("ingestor").withIsrc("isrc").withTitle("title").withArtist("artist")
                       .withIngestionDate(new Date()).withMediaType(DOWNLOAD));

        ReportingType reportingType = INTERNAL_REPORTED;
        String negativeTagString = "a";
        TrackReportingOptionsDto trackReportingOptionsDto = new TrackReportingOptionsDto().withTrackId(track.getId()).withNegativeTags(singleton(negativeTagString)).withReportingType(reportingType);

        //when
        Track actualTrack = trackReportingOptionsService.assignReportingOptions(trackReportingOptionsDto);

        //then
        assertThat(actualTrack.getReportingType(), is(reportingType));
        assertThat(actualTrack.getNegativeTags().size(), is(1));
        assertThat(actualTrack.getNegativeTags().iterator().next().getTag(), is(negativeTagString));
    }
}