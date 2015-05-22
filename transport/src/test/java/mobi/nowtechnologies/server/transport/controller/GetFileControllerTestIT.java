package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.device.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.CloudFileService;
import mobi.nowtechnologies.server.service.FileService;
import mobi.nowtechnologies.server.service.impl.CloudFileServiceImpl;
import mobi.nowtechnologies.server.shared.Utils;

import javax.annotation.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.Set;

import com.brightcove.proserve.mediaapi.wrapper.ReadApi;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.Video;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.MediaDeliveryEnum;
import com.brightcove.proserve.mediaapi.wrapper.apiobjects.enums.VideoFieldEnum;
import com.brightcove.proserve.mediaapi.wrapper.exceptions.BrightcoveException;
import com.google.common.io.Files;
import com.rackspacecloud.client.cloudfiles.FilesNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;

import org.junit.*;
import org.springframework.test.web.servlet.ResultMatcher;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetFileControllerTestIT extends AbstractControllerTestIT {

    ReadApi brightcoveReadServiceMock;
    @Resource(name = "service.FileService")
    FileService fileService;
    @Value("${store.path}\\image\\US-UM7-11-00061S.jpg")
    private File file;
    @Resource
    private CloudFileService cloudFileService;
    @Value("${cloudFile.audioContentContainerName}")
    private String audioContentContainerName;

    @Test
    public void testGetFileO2_LatestVersion() throws Exception {
        String apiVersion = LATEST_SERVER_API_VERSION;

        String userName = "+447111111114";
        String fileType = "VIDEO";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();

        brightcoveReadServiceMock = new ReadApi() {
            @Override
            public Video FindVideoByReferenceId(String readToken, String referenceId, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, MediaDeliveryEnum mediaDelivery)
                throws BrightcoveException {
                Video video = new Video();
                video.setFlvUrl("http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=2599461121001&pubId=2368678501001");
                return video;
            }
        };

        fileService.setBrightcoveReadService(brightcoveReadServiceMock);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_FILE").param("USER_NAME", userName)
                                                                                 .param("USER_TOKEN", userToken)
                                                                                 .param("TIMESTAMP", timestamp)
                                                                                 .param("ID", mediaId)
                                                                                 .param("TYPE", fileType)
                                                                                 .header("Content-Type", "text/xml")
                                                                                 .
                                                                                     header("Content-Length", "0")).andExpect(status().isOk()).
                   andExpect(content().contentType(MediaType.TEXT_PLAIN)).
                   andExpect(content().string("http://c.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=2599461121001&pubId=2368678501001"));
    }

    @Test
    public void testGetFile_O2_v6d0_Success() throws Exception {
        String apiVersion = "6.0";

        String userName = "+447111111114";
        String fileType = "VIDEO";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_FILE").param("USER_NAME", userName)
                                                                                 .param("USER_TOKEN", userToken)
                                                                                 .param("TIMESTAMP", timestamp)
                                                                                 .param("ID", mediaId)
                                                                                 .param("TYPE", fileType)
                                                                                 .header("Content-Type", "text/xml")
                                                                                 .
                                                                                     header("Content-Length", "0")).andExpect(status().isOk()).
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

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);
        user.setDeviceType(deviceType);
        userService.updateUser(user);

        brightcoveReadServiceMock = new ReadApi() {
            @Override
            public Video FindVideoByReferenceId(String readToken, String referenceId, EnumSet<VideoFieldEnum> videoFields, Set<String> customFields, MediaDeliveryEnum mediaDelivery)
                throws BrightcoveException {
                Video video = new Video();
                video.setFlvUrl("http://brightcove.vo.llnwd.net/e1/uds/pd/2368678501001/2368678501001_2599463153001_Signs.mp4");
                return video;
            }
        };

        fileService.setBrightcoveReadService(brightcoveReadServiceMock);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_FILE").param("USER_NAME", userName)
                                                                                 .param("USER_TOKEN", userToken)
                                                                                 .param("TIMESTAMP", timestamp)
                                                                                 .param("ID", mediaId)
                                                                                 .param("TYPE", fileType)
                                                                                 .header("Content-Type", "text/xml")
                                                                                 .
                                                                                     header("Content-Length", "0")).andExpect(status().isOk()).
                   andExpect(content().string("http://brightcove.vo.llnwd.net/e1/uds/pd/2368678501001/2368678501001_2599463153001_Signs.mp4"));
    }

    @Test
    public void testGetFile_O2_v4d0_401_Failure() throws Exception {
        String apiVersion = "4.0";
        ResultMatcher statusToCheck = status().isUnauthorized();

        String userName = "+447xxxxxxxxxxxx";
        String fileType = "VIDEO";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String mediaId = "VIDEO160822";//generateVideoMedia();

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_FILE").param("USER_NAME", userName)
                                                                                 .param("USER_TOKEN", userToken)
                                                                                 .param("TIMESTAMP", timestamp)
                                                                                 .param("ID", mediaId)
                                                                                 .param("TYPE", fileType)).andExpect(statusToCheck);
    }

    @Test
    public void testGetFile_O2_v4d0_400_Failure() throws Exception {
        String apiVersion = "4.0";
        ResultMatcher statusToCheck = status().isInternalServerError();

        assertGetFileStatusForApiVersion(apiVersion, statusToCheck);
    }

    @Test
    public void testGetFile_O2_v5d3_400_Failure() throws Exception {
        String apiVersion = "5.3";
        ResultMatcher statusToCheck = status().isBadRequest();

        assertGetFileStatusForApiVersion(apiVersion, statusToCheck);
    }

    @Test
    public void testGetFile_O2_v4d0_404_Failure() throws Exception {
        String apiVersion = "3.5";
        ResultMatcher statusToCheck = status().isNotFound();

        assertGetFileStatusForApiVersion(apiVersion, statusToCheck);
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

        CloudFileService cloudFileServiceMock = new CloudFileServiceImpl() {
            @Override
            public InputStream getInputStream(String destinationContainer, String fileName) throws FilesNotFoundException {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new FilesNotFoundException(null, null, null);
                }
            }
        };
        fileService.setCloudFileService(cloudFileServiceMock);

        byte[] fileContent = Files.toByteArray(file);
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_FILE").param("USER_NAME", userName)
                                                                                 .param("USER_TOKEN", userToken)
                                                                                 .param("TIMESTAMP", timestamp)
                                                                                 .param("ID", mediaId)
                                                                                 .param("TYPE", fileType)).andExpect(status().isOk()).
                   andExpect(content().contentType(MediaType.IMAGE_JPEG)).
                   andExpect(content().bytes(fileContent));
    }

    private void assertGetFileStatusForApiVersion(String apiVersion, ResultMatcher statusToCheck) throws Exception {
        String userName = "+447xxxxxxxxxxxx";
        String fileType = "VIDEO";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_FILE").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("TYPE", fileType))
               .andExpect(statusToCheck);
    }
}
