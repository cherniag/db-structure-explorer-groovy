package mobi.nowtechnologies.server.trackrepo.controller;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.webApplicationContextSetup;

/**
 * The class <code>SignInControllerTest</code> contains tests for the class <code>{@link mobi.nowtechnologies.server.trackrepo.controller.SignInController}</code>.
 *
 * @generatedBy CodePro at 11/13/12 5:09 PM, using the Spring generator
 * @author Alexander Kolpakov (akolpakov)
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/application-test.xml",
		"classpath:META-INF/trackrepo-servlet-test.xml"})
@TransactionConfiguration(transactionManager = "trackRepo.TransactionManager", defaultRollback = true)
@Transactional
public class IngestTracksWizardControllerTestIT extends TestCase {
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        mockMvc = webApplicationContextSetup((WebApplicationContext)applicationContext).build();
    }

	@Test
	public void testGetDrops_Success() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                post("/drops")
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<type>VIDEO_CHART</type>"));
        assertTrue(resultXml.contains("<duration>10000</duration>"));
        assertTrue(!resultXml.contains("<bonusTrack>"));
	}
}