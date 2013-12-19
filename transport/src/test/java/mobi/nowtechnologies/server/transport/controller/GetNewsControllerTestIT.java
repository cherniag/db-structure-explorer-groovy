package mobi.nowtechnologies.server.transport.controller;

import com.google.gson.JsonObject;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class GetNewsControllerTestIT extends AbstractControllerTestIT{

    @Test
    public void testGetNews_v6d0AndJsonAndAccCheckInfo_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_NEWS.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.response..items").exists()).
                andExpect(jsonPath("$.response..news").exists()).
                andExpect(jsonPath("$.response..user").exists());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();

        JsonObject jsonObject = getAccCheckContent(resultJson);

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
    public void testGetNews_401_Failure() throws Exception {
        String userName = "+447xxxxxxxxx";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_NEWS")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetNews_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_NEWS")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testGetNews_404_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

         mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_NEWS")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isNotFound());
    }
}
