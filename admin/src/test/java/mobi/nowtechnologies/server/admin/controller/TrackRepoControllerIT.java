package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.shared.dto.PageListDto;
import mobi.nowtechnologies.server.trackrepo.dto.TrackDto;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.ui.ModelMap;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrackRepoControllerIT extends AbstractAdminITTest {

    @Test
    @Ignore
    public void verifyThatTrackCanBeFetchedByGenre() throws Exception {
        String genre_1 = "genre_1";
        ResultActions resultActions = mockMvc.perform(
                get("/tracks/list")
                        .param("genre", genre_1)
                        .param("page.page", "1")
        ).andExpect(status().isOk());

        List<TrackDto> list = getTrackDtoList(resultActions);
        assertEquals(1, list.size());
        TrackDto first = list.get(0);
        assertEquals(genre_1, first.getGenre());
    }

    @Test
    @Ignore
    public void verifyThatTrackCanBeFetchedByAlbumName() throws Exception {
        String album_1 = "album_1";
        ResultActions resultActions = mockMvc.perform(
                get("/tracks/list")
                        .param("album", album_1)
                        .param("page.page", "1")
        ).andExpect(status().isOk());

        List<TrackDto> list = getTrackDtoList(resultActions);
        assertEquals(1, list.size());
        TrackDto first = list.get(0);
        assertEquals(album_1, first.getAlbum());
    }

    @Test
    @Ignore
    public void verifyThatTrackCanBeFetchedByIngestor() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/tracks/list")
                        .param("ingestor", "ingestor_1")
                        .param("page.page", "1")
        )
                .andExpect(status().isOk())
                .andExpect(view().name("tracks/tracks"));
        List<TrackDto> list = getTrackDtoList(resultActions);

        assertEquals(1, list.size());
        TrackDto first = list.get(0);
        assertEquals("ingestor_1", first.getIngestor());
    }

    @Test
    public void shouldReturnErrorWhenReportingTypeIsNull() throws Exception {
        //given

        String trackReportingOptionsDto1 = "{ \"trackId\" : 666, \"reportingType\" : null, \"negativeTags\" : [\"a\"] }";

        //when
        ResultActions resultActions = mockMvc.perform(put("/reportingOptions").content(trackReportingOptionsDto1).accept(APPLICATION_JSON).contentType(APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(model().errorCount(1));
    }

    @Test
    public void shouldReturnErrorWhenNoReportingType() throws Exception {
        //given

        String trackReportingOptionsDto1 = "{ \"trackId\" : 666, \"negativeTags\" : [\"a\"] }";

        //when
        ResultActions resultActions = mockMvc.perform(put("/reportingOptions").content(trackReportingOptionsDto1).accept(APPLICATION_JSON).contentType(APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(model().errorCount(1));
    }

    private List<TrackDto> getTrackDtoList(ResultActions resultActions) {
        ModelMap modelMap = resultActions.andReturn().getModelAndView().getModelMap();
        PageListDto<TrackDto> tracks = (PageListDto<TrackDto>) modelMap.get(PageListDto.PAGE_LIST_DTO);
        assertNotNull(tracks);
        return tracks.getList();
    }

}
