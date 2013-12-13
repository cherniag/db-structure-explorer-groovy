package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class GetFileControllerTestIT extends AbstractControllerTestIT {

	@Autowired
	private ChartDetailRepository chartDetailRepository;

    @Test
    public void testGetFile_O2_v6d0_Success() throws Exception {
    	String userName = "+447111111114";
        String fileType = "VIDEO";
		String apiVersion = "6.0";
		String communityUrl = "o2";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();
		
		ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
                        .header("Content-Type", "text/xml").
                        header("Content-Length", "0")
        ).andExpect(status().isOk());
		
		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();
		
        assertTrue(resultXml.startsWith("http://c.brightcove.com/services/mobile/streaming/index/master.m3u8"));
    }

    @Test
    public void testGetFile_O2_v4d0_WindowsPhone_Success() throws Exception {
    	String userName = "+447111111114";
        String fileType = "VIDEO";
		String apiVersion = "4.0";
		String communityUrl = "o2";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";

        DeviceType deviceType = new DeviceType();
        deviceType.setI((byte)7);
        deviceType.setName(DeviceType.WINDOWS_PHONE);

        User user = userService.findByNameAndCommunity(userName, communityUrl);
        user.setDeviceType(deviceType);
        userService.updateUser(user);

		ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
                        .header("Content-Type", "text/xml").
                        header("Content-Length", "0")
        ).andExpect(status().isOk());

		MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
		String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("Signs.mp4"));
    }

    @Test
    public void testGetFile_O2_v4d0_401_Failure() throws Exception {
        String userName = "+447xxxxxxxxxxxx";
        String fileType = "VIDEO";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetFile_O2_v4d0_400_Failure() throws Exception {
        String userName = "+447xxxxxxxxxxxx";
        String fileType = "VIDEO";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("TYPE", fileType)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testGetFile_O2_v4d0_404_Failure() throws Exception {
        String userName = "+447xxxxxxxxxxxx";
        String fileType = "VIDEO";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("TYPE", fileType)
        ).andExpect(status().isNotFound());
    }
    
    private String generateVideoMedia(){
        ChartDetail chartDetail = chartDetailRepository.findOne(22);

        return chartDetail.getMedia().getIsrc();
    }
}
