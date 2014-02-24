package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import mobi.nowtechnologies.server.trackrepo.mock.MockWebApplication;
import mobi.nowtechnologies.server.trackrepo.mock.MockWebApplicationContextLoader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/application-test.xml",
		"classpath:META-INF/trackrepo-servlet-test.xml"}, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "trackrepo.IngestTracksWizardController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
public class IngestTracksWizardControllerIT extends TestCase {
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup((WebApplicationContext)applicationContext).build();
    }

	@Test
	public void testGetDrops_Success() throws Exception {
        mockMvc.perform(
                get("/drops.json")
        ).andExpect(status().isOk()).andExpect(jsonPath("$.suid").exists());
	}

    @Ignore @Test
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