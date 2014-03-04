package mobi.nowtechnologies.server.trackrepo.controller;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;

import static junit.framework.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Alexander Kolpakov (akolpakov)
 */

public class IngestTracksWizardControllerIT extends AbstractTrackRepoITTest{

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
                .andExpect(jsonPath("$.drops[0].tracks[0].productCode").exists());;
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

    @Test
    @Ignore
    public void testCommitDrops_Success() throws Exception {
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
                post("/drops/commit.json").
                        content(resultJson.getBytes()).
                        accept(MediaType.APPLICATION_JSON).
                        contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultJson = aHttpServletResponse.getContentAsString();

        assertTrue(resultJson.equals("true"));
    }
}