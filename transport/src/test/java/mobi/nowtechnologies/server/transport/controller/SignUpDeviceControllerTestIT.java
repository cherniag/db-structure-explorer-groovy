package mobi.nowtechnologies.server.transport.controller;

import com.google.gson.JsonObject;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SignUpDeviceControllerTestIT extends AbstractControllerTestIT{

    @Test
    public void testSignUpDevice_v6d0AndJsonAndAccCheckInfo_Success() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = null;
        String userToken = null;
        String userName = null;

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();
        JsonObject jsonObject = getAccCheckContent(resultJson);
        AccountCheckDTO accountCheckDTO = gson.fromJson(jsonObject, AccountCheckDTO.class);
        storedToken = accountCheckDTO.userToken;
        userName = accountCheckDTO.userName;
        userToken = Utils.createTimestampToken(storedToken, timestamp);

        resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultAccCkeckJson = aHttpServletResponse.getContentAsString();

        assertTrue(resultAccCkeckJson.contains(jsonObject.toString()));
    }

    @Test
    public void testSignUpDevice_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
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
        String deviceType = "ANDROID";
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
