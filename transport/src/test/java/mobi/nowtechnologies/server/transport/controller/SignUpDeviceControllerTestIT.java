package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SignUpDeviceControllerTestIT extends AbstractControllerTestIT {

    @Resource
    private DeviceUserDataRepository deviceUserDataRepository;

    @Test
    public void testSignUpDevice_v6d0AndJsonAndAccCheckInfo_Success() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
        AccountCheckDto dto = getAccCheckContent(resultActions);
        String storedToken = dto.userToken;
        String userName = dto.userName;
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions accountCheckCall = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());
        checkAccountCheck(resultActions, accountCheckCall);
    }

    @Test
    public void signUpDevice_6d1_WithXtifyShouldCreateDeviceUserData() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String xtifyToken = "dsfhosduyajdfy78cyuaidyfo67vc6754g5";

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
                        .param("XTIFY_TOKEN", xtifyToken)
        ).andExpect(status().isOk());
        AccountCheckDto dto = getAccCheckContent(resultActions);
        String storedToken = dto.userToken;
        String userName = dto.userName;
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions accountCheckCall = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());
        checkAccountCheck(resultActions, accountCheckCall);

        DeviceUserData found = deviceUserDataRepository.findByXtifyToken(xtifyToken);
        assertNotNull(found);
        assertEquals(deviceUID, found.getDeviceUid());
        assertEquals(xtifyToken, found.getXtifyToken());
    }


    @Test
    public void signUpDevice_6d1_WithoutXtifyShouldNotCreateDeviceUserData() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.1";
        String communityUrl = "o2";

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
        AccountCheckDto dto = getAccCheckContent(resultActions);

        User one = userRepository.findOne(dto.userName, communityUrl);
        assertNotNull(one);
        DeviceUserData found = deviceUserDataRepository.find(one.getId(), communityUrl, deviceUID);
        assertNull(found);
    }

    @Test
    public void signUpDevice_6d0_WithXtifyShouldNotCreateDeviceUserData() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String xtifyToken = "dsfhosduyajdfy78cyuaidyfo67vc6754g5";

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
                        .param("XTIFY_TOKEN", xtifyToken)
        ).andExpect(status().isOk());
        AccountCheckDto dto = getAccCheckContent(resultActions);

        User one = userRepository.findOne(dto.userName, communityUrl);
        assertNotNull(one);
        DeviceUserData found = deviceUserDataRepository.find(one.getId(), communityUrl, deviceUID);
        assertNull(found);
    }

    @Test
    public void testSignUpDevice_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.0";
        String communityUrl = "o2";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isInternalServerError()).andDo(print());
    }

    @Test
    public void testSignUpDeviceV5d3_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.3";
        String communityUrl = "o2";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpDevice_404_Failure() throws Exception {
        String deviceType = "ANDROID";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isNotFound());
    }


    @Test
    public void testSignUpDeviceInDifferentFormats() throws Exception {
        String deviceUID = "viktestdevice";
        String deviceType = "ANDROID";
        String apiVersion = "3.6";
        String communityUrl = "o2";
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", "CNBETA")
                        .param("API_VERSION", "V1.1")
                        .param("COMMUNITY_NAME", "o2")
                        .param("DEVICE_MODEL", "model")
        ).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_XML)).andDo(print());
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", "CNBETA")
                        .param("API_VERSION", "V1.1")
                        .param("COMMUNITY_NAME", "o2")
                        .param("DEVICE_MODEL", "model")
                        .header("Accept","application/json")
        ).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andDo(print());
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
                        .param("APP_VERSION", "CNBETA")
                        .param("API_VERSION", "V1.1")
                        .param("COMMUNITY_NAME", "o2")
                        .param("DEVICE_MODEL", "model")
                        .header("Accept","*/*")
        ).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_XML)).andDo(print());

    }

}
