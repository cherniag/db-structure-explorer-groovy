package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SignUpDeviceControllerTestIT extends AbstractControllerTestIT {

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
    public void testSignUpDevice_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.0";
        String communityUrl = "o2";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isInternalServerError());
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
}
