package mobi.nowtechnologies.server.transport.controller;

import com.google.common.io.Files;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetFileControllerTestIT extends AbstractControllerTestIT {


    @Value("${store.path}\\image\\US-UM7-11-00061S.jpg")
    private File file;


    @Resource
    private CloudFileService cloudFileService;

    @Value("${cloudFile.audioContentContainerName}")
    private String audioContentContainerName;

    @Resource
    private TaskExecutor getFileTaskExecutor;


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
    public void testGetFileO2_Success_LatestVersion() throws Exception {
        String userName = "+447111111114";
        String fileType = "VIDEO";
        String apiVersion = LATEST_SERVER_API_VERSION;
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
        deviceType.setI((byte) 7);
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
    public void testGetFile_JpegImage_With_Success_For_One_User() throws Exception {
        String userName = "+447111111114";
        String fileType = "IMAGE_SMALL";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "US-UM7-11-00061";

        byte[] fileContent = Files.toByteArray(file);
        cloudFileService.uploadFile(file, file.getName(), MediaType.IMAGE_JPEG_VALUE, audioContentContainerName);
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

    @Test
    public void testGetFile_JpegImage_With_Success_For_Multiple_Users() throws Exception {
        final String userName = "+447111111114";
        final String fileType = "IMAGE_SMALL" ;
        final String apiVersion = "6.0";
        final String communityUrl = "o2";
        final String timestamp = "2011_12_26_07_04_23";
        final String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        final String userToken = Utils.createTimestampToken(storedToken, timestamp);

        final String mediaId = "US-UM7-11-00061";

        final byte[] fileContent = Files.toByteArray(file);
        cloudFileService.uploadFile(file, file.getName(), MediaType.IMAGE_JPEG_VALUE, audioContentContainerName);
        final List<MvcResult> resultActionsList = new ArrayList<MvcResult>();
        int countOfRequests = 20;
        for (int i = 0, n = countOfRequests; i < n; i++) {
            getFileTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        resultActionsList.add(mockMvc.perform(
                                post("/" + communityUrl + "/" + apiVersion + "/GET_FILE")
                                        .param("USER_NAME", userName)
                                        .param("USER_TOKEN", userToken)
                                        .param("TIMESTAMP", timestamp)
                                        .param("ID", mediaId)
                                        .param("TYPE", fileType)
                        ).andReturn());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        Thread.sleep(30000);
        for (int current = 0; current<countOfRequests; current++){
            MvcResult currentValue = resultActionsList.get(current);
            Assert.assertArrayEquals(fileContent, currentValue.getResponse().getContentAsByteArray());
        }
    }

}
