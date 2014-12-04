package mobi.nowtechnologies.server.trackrepo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mobi.nowtechnologies.server.trackrepo.domain.Territory;
import mobi.nowtechnologies.server.trackrepo.domain.Track;
import mobi.nowtechnologies.server.trackrepo.dto.DropDto;
import mobi.nowtechnologies.server.trackrepo.dto.IngestWizardDataDto;
import mobi.nowtechnologies.server.trackrepo.repository.TrackRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @author Alexander Kolpakov (akolpakov)
public class IngestTracksWizardControllerIT extends AbstractTrackRepoIT {

    @Resource
    private TrackRepository trackRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetDrops_Success() throws Exception {
        mockMvc.perform(
                get("/drops.json")
        ).andExpect(status().isOk()).andExpect(jsonPath("$.suid").exists());
    }

    @Test
    public void testSelectDrops_Success() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/drops.json")
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();
        resultJson = resultJson.replaceAll("\"selected\":false", "\"selected\":true");

        mockMvc.perform(
                post("/drops/select.json").
                        content(resultJson.getBytes()).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.suid").exists())
                .andExpect(jsonPath("$.drops[0].tracks[0].productCode").exists());
        ;
    }

    @Test
    @Ignore
    public void testSelectTrackDrops_Success() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/drops.json")
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();
        resultJson = resultJson.replaceAll("\"selected\":false", "\"selected\":true");

        resultActions = mockMvc.perform(
                post("/drops/select.json").
                        content(resultJson.getBytes()).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultJson = aHttpServletResponse.getContentAsString();
        resultJson = resultJson.replaceAll("\"INSERT\",\"selected\":true", "\"INSERT\",\"selected\":false");

        resultActions = mockMvc.perform(
                post("/drops/tracks/select.json").
                        content(resultJson.getBytes()).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultJson = aHttpServletResponse.getContentAsString();

        assertTrue(resultJson.contains("suid"));
        assertTrue(resultJson.contains("\"INSERT\",\"selected\":false"));
    }


    private String markAllTracksAsSelected(ResultActions resultActions) throws Exception {
        IngestWizardDataDto dto = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), IngestWizardDataDto.class);
        for (DropDto currentDrop : dto.getDrops()) {
            currentDrop.setSelected(true);
        }
        return objectMapper.writeValueAsString(dto);
    }

    @Test
    public void testCommitDropsForUniversal_Success() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/drops.json")
                        .param("ingestors", "UNIVERSAL")
        ).andExpect(status().isOk());
        resultActions = mockMvc.perform(
                post("/drops/select.json").
                        content(markAllTracksAsSelected(resultActions)).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        resultActions = mockMvc.perform(
                post("/drops/commit.json").
                        content(markAllTracksAsSelected(resultActions)).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assertTrue(resultActions.andReturn().getResponse().getContentAsString().equals("true"));
        Track track = trackRepository.findByISRC("GBUV71200558");
        Set<Territory> ters = track.getTerritories();
        assertEquals(2, ters.size());
    }

    @Test
    public void testCommitDropsForMOS_Success() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/drops.json")
                        .param("ingestors", "MOS")
        ).andExpect(status().isOk());
        resultActions = mockMvc.perform(
                post("/drops/select.json").
                        content(markAllTracksAsSelected(resultActions)).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        resultActions = mockMvc.perform(
                post("/drops/commit.json").
                        content(markAllTracksAsSelected(resultActions)).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        assertTrue(resultActions.andReturn().getResponse().getContentAsString().equals("true"));
        Track track = trackRepository.findByISRC("GB3FT1300026");
        Set<Territory> ters = track.getTerritories();
        assertEquals(2, ters.size());
    }

}