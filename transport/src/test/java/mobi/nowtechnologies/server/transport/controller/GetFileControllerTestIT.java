package mobi.nowtechnologies.server.transport.controller;

import com.google.common.io.Files;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetFileControllerTestIT extends AbstractControllerTestIT {


    @Value("${store.path}\\image\\US-UM7-11-00061S.jpg")
    private File file;


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

		mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
                        .header("Content-Type", "text/xml").
                        header("Content-Length", "0")
        ).andExpect(status().isOk()).andDo(print()).
                andExpect(content().contentType(MediaType.TEXT_PLAIN)).
                andExpect(content().string("http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=2599461121001&pubId=2368678501001"));
    }

    @Test
    public void testGetFile_O2_v6d1_Success() throws Exception {
        String userName = "+447111111114";
        String fileType = "VIDEO";
        String apiVersion = "6.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
                        .header("Content-Type", "text/xml").
                        header("Content-Length", "0")
        ).andExpect(status().isOk()).andDo(print()).
                andExpect(content().contentType(MediaType.TEXT_PLAIN)).
                andExpect(content().string("http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=2599461121001&pubId=2368678501001"));
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

		mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
                        .header("Content-Type", "text/xml").
                        header("Content-Length", "0")
        ).andExpect(status().isOk()).andDo(print()).
                andExpect(content().string("http://brightcove.vo.llnwd.net/e1/uds/pd/2368678501001/2368678501001_2599463153001_Signs.mp4"));
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

        mockMvc.perform(
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


        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("TYPE", fileType)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetFile_O2_v5d3_400_Failure() throws Exception {
        String userName = "+447xxxxxxxxxxxx";
        String fileType = "VIDEO";
        String apiVersion = "5.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);


        mockMvc.perform(
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

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("TYPE", fileType)
        ).andExpect(status().isNotFound());
    }


    @Test
    public void testGetFile_JpegImage_With_Success() throws Exception {
        String userName = "+447111111114";
        String fileType = "IMAGE_SMALL";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "US-UM7-11-00061";

        byte[] fileContent = Files.toByteArray(file);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("ID", mediaId)
                        .param("TYPE", fileType)
        ).andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.IMAGE_JPEG)).
                andExpect(content().bytes(fileContent));
    }
    
}
