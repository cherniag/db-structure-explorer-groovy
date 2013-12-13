package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class AccCheckControllerTestIT extends AbstractControllerTestIT{
	
	@Autowired
	private ChartRepository chartRepository;

	@Autowired
	private ChartDetailRepository chartDetailRepository;

    @Test
    public void testAccountCheckForO2Client_WithSelectedCharts_Success() throws Exception {
    	String userName = "+447111111114";
		String apiVersion = "3.9";
		String communityName = "o2";
		String communityUrl = "o2";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);
		
		List<Chart> charts = new ArrayList<Chart>();
		Chart chart = chartRepository.findOne(5);
		charts.add(chart);
		User user = userService.findByNameAndCommunity(userName, communityName);
		user.setSelectedCharts(charts);
		userService.updateUser(user);
		
		ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());
		
		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();
		
        assertTrue(resultXml.contains("<playlist><id>5</id><type>BASIC_CHART</type></playlist>"));
    }
    
    @Test
    public void testAccountCheckForO2Client_WithLockedTracks_Success() throws Exception {
    	String userName = "+447111111114";
    	String apiVersion = "3.9";
    	String communityName = "o2";
    	String communityUrl = "o2";
    	String timestamp = "2011_12_26_07_04_23";
    	String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
    	String userToken = Utils.createTimestampToken(storedToken, timestamp);
    	
    	ChartDetail chartDetail = chartDetailRepository.findOne(22);
    	chartDetail.setLocked(true);
    	chartDetailRepository.save(chartDetail);
    	User user = userService.findByNameAndCommunity(userName, communityName);
    	UserStatus userStatus = new UserStatus();
    	userStatus.setI((byte)10);
		user.setStatus(userStatus);
		userService.updateUser(user);
    	
    	ResultActions resultActions = mockMvc.perform(
    			post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
    			.param("COMMUNITY_NAME", communityName)
    			.param("USER_NAME", userName)
    			.param("USER_TOKEN", userToken)
    			.param("TIMESTAMP", timestamp)
    			).andExpect(status().isOk());
    	
    	MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
    	String resultXml = aHttpServletResponse.getContentAsString();
    	
    	assertTrue(resultXml.contains("<lockedTrack><media>US-UM7-11-00061</media></lockedTrack>"));
    }

    @Test
    public void testAccountCheckForO2Client_WithIOS7DeviceUID_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "3.8";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";

        ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<deviceUID>b88106713409e92622461a876abcd74b</deviceUID>"));
    }

    @Test
    public void testAccountCheckForO2Client_greaterOrEquals3d9_IOS_WithNotCorrectDeviceUID_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "3.9";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String deviceUID = "fail";

        ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<deviceUID>fail</deviceUID>"));

        apiVersion = "4.0";

        resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<deviceUID>fail</deviceUID>"));
    }

    @Test
    public void testAccountCheckForFVClient_HasAllDetails_Success() throws Exception {
        String userName = "+642102247311";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userService.findByNameAndCommunity(userName, communityName);
        user.setProvider(NON_VF);
        userService.updateUser(user);

        ResultActions resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<hasAllDetails>true</hasAllDetails>"));
        assertTrue(resultXml.contains("<canGetVideo>false</canGetVideo>"));
    }

    @Test
    public void testAccountCheckForFVClient_HasAllDetails_JsonFormatAndAdditionalUIDAndVersionMore50_Success() throws Exception {
        String userName = "+642102247311";
        String apiVersion = "6.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userService.findByNameAndCommunity(userName, communityName);
        user.setProvider(NON_VF);
        userService.updateUser(user);

        ResultActions resultActions = mockMvc.perform(
                post("/AUID/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();

        assertTrue(resultJson.contains("\"hasAllDetails\":true"));
        assertTrue(resultJson.contains("\"canGetVideo\":false"));
    }

    @Test
    public void testAccountCheck_404_Failure() throws Exception {
        String userName = "+642102247311";
        String apiVersion = "3.5";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/AUID/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isNotFound());
    }

    @Test
    public void testAccountCheck_400_Failure() throws Exception {
        String userName = "+642102247311";
        String apiVersion = "4.0";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/AUID/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testAccountCheck_401_Failure() throws Exception {
        String userName = "+6421xxxxxxxx";
        String apiVersion = "4.0";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/AUID/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isUnauthorized());
    }
}