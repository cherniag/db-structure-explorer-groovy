package mobi.nowtechnologies.server.trackrepo.controller;

import mobi.nowtechnologies.server.trackrepo.domain.NegativeTag;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static mobi.nowtechnologies.server.trackrepo.domain.AssetFile.FileType.DOWNLOAD;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrackReportingOptionsControllerIT extends AbstractTrackRepoIT {

    @Resource TrackRepository trackRepository;

    @Test
    public void shouldSaveReportingOptions() throws Exception {
        //given
        Track track1 = trackRepository.save(new Track().withNegativeTags(new HashSet<NegativeTag>(asList(new NegativeTag().withTag("d")))).withIngestor("ingestor").withIsrc("isrc").withTitle("title").withArtist("artist").withIngestionDate(new Date()).withMediaType(DOWNLOAD));

        String trackReportingOptionsDto1 = "{ \"trackId\" : " + track1.getId() + ", \"reportingType\" : \"INTERNAL_REPORTED\", \"negativeTags\" : [\"a\"] }";

        //when
        ResultActions resultActions = mockMvc.perform(put("/reportingOptions").content(trackReportingOptionsDto1).accept(APPLICATION_JSON).contentType(APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void shouldReturnErrorWhenReportingTypeIsNull() throws Exception {
        //given
        Track track1 = trackRepository.save(new Track().withNegativeTags(new HashSet<NegativeTag>(asList(new NegativeTag().withTag("d")))).withIngestor("ingestor").withIsrc("isrc").withTitle("title").withArtist("artist").withIngestionDate(new Date()).withMediaType(DOWNLOAD));

        String trackReportingOptionsDto1 = "{ \"trackId\" : " + track1.getId() + ", \"reportingType\" : null, \"negativeTags\" : [\"a\"] }";

        //when
        ResultActions resultActions = mockMvc.perform(put("/reportingOptions").content(trackReportingOptionsDto1).accept(APPLICATION_JSON).contentType(APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(model().errorCount(1));
    }

    @Test
    public void shouldReturnErrorWhenNoReportingType() throws Exception {
        //given
        Track track1 = trackRepository.save(new Track().withNegativeTags(new HashSet<NegativeTag>(asList(new NegativeTag().withTag("d")))).withIngestor("ingestor").withIsrc("isrc").withTitle("title").withArtist("artist").withIngestionDate(new Date()).withMediaType(DOWNLOAD));

        String trackReportingOptionsDto1 = "{ \"trackId\" : " + track1.getId() + ", \"negativeTags\" : [\"a\"] }";

        //when
        ResultActions resultActions = mockMvc.perform(put("/reportingOptions").content(trackReportingOptionsDto1).accept(APPLICATION_JSON).contentType(APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(model().errorCount(1));
    }
}