package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import mobi.nowtechnologies.server.trackrepo.enums.TrackStatus;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.ui.ModelMap;

import java.util.Date;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.server.setup.MockMvcBuilders.xmlConfigSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-test.xml"})
public class TrackRepoControllerIT {

    MockMvc mockMvc;

    @Autowired
    TrackRepository trackRepository;

    @Test
    public void verifyThatTrackCanBeFetchedByIngestor() throws  Exception{

        mockMvc = xmlConfigSetup("classpath:META-INF/dao-test.xml",
                "classpath:META-INF/service-test.xml",
                "classpath:META-INF/shared.xml",
                "classpath:admin-test.xml",
                "classpath:WEB-INF/security.xml",
                "classpath:application-test.xml")
                .configureWebAppRootDir("admin/src/main/webapp/", false).build();

        trackRepository.save(createTrack());

        ResultActions resultActions = mockMvc.perform(
                get("/tracks/list")
                        .param("ingestor", "ingestor_1")
                        .param("page.page", "1")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("tracks/tracks"));
        ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();
        PageListDto<TrackDto> tracks = (PageListDto<TrackDto>)modelMap.get(PageListDto.PAGE_LIST_DTO);
        Assert.assertNotNull(tracks);
        Assert.assertTrue(tracks.getList().size() > 0);
    }

    private Track createTrack() {
        Track track = new Track();
        track.setIngestor("ingestor_1");
        track.setIsrc("isrc_1");
        track.setTitle("title_1");
        track.setArtist("artist_1");
        track.setIngestionDate(new Date());
        track.setStatus(TrackStatus.ENCODED);

        track.setAlbum("Albom_1");
        track.setGenre("Rock");
        return track;
    }

}
