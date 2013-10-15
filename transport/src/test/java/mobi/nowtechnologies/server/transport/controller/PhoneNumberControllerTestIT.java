package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.webApplicationContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:transport-servlet-test.xml",
		"classpath:META-INF/service-test.xml",
		"classpath:META-INF/soap.xml",
		"classpath:META-INF/dao-test.xml",
		"classpath:META-INF/soap.xml",
		"classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.AccCheckController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
//@Transactional
public class PhoneNumberControllerTestIT {
	
	private MockMvc mockMvc;

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	@Qualifier("service.UserService")
	private UserService userService;
	
	@Autowired
	private ChartRepository chartRepository;

	@Autowired
	private ChartDetailRepository chartDetailRepository;

	
    @Before
    public void setUp() {
        mockMvc = webApplicationContextSetup((WebApplicationContext)applicationContext).build();
    }

    @Test
    public void testActivatePhoneNumber_O2_Success() throws Exception {
        String userName = "+447111111114";
        String phone = "+447111111114";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions resultActions = mockMvc.perform(
                post("/some_key/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<activation>ENTERED_NUMBER</activation><phoneNumber>+447111111114</phoneNumber>"));

        resultActions = mockMvc.perform(
                post("/someid/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<provider>o2</provider>"));
    }

    @Test
    public void testActivatePhoneNumber_NZ_VF_Success() throws Exception {
    	String userName = "+642111111111";
    	String phone = "+642111111111";
		String apiVersion = "5.0";
		String communityName = "vf_nz";
		String communityUrl = "vf_nz";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);
		
		ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk());
		
		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();
		
        assertTrue(resultXml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+642111111111</phoneNumber></phoneActivation></response>"));

        resultActions = mockMvc.perform(
                post("/someid/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<provider>vf</provider>"));
    }

    @Test
    public void testActivatePhoneNumber_NZ_NON_VF_Success() throws Exception {
        String userName = "+64279000456";
        String phone = "+64279000456";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/PHONE_NUMBER")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("PHONE", phone)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><response><phoneActivation><activation>ENTERED_NUMBER</activation><phoneNumber>+64279000456</phoneNumber></phoneActivation></response>"));

        resultActions = mockMvc.perform(
                post("/someid/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<provider>non-vf</provider>"));
    }
}