package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.MessageActionType;
import mobi.nowtechnologies.server.shared.enums.MessageType;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.util.Collections;

import static mobi.nowtechnologies.server.shared.enums.MessageActionType.A_SPECIFIC_TRACK;
import static mobi.nowtechnologies.server.shared.enums.MessageType.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetNewsControllerTestIT extends AbstractControllerTestIT{

    @Resource
    CommunityRepository communityRepository;

    @Test
    public void testGetNews_LatestVersion() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        long newsPublishTimestamp = System.currentTimeMillis() - 1000L;
        Message message = createMessage(communityUrl, "title", NOTIFICATION, true, "body_latest", MessageActionType.SUBSCRIPTION_PAGE, "action", newsPublishTimestamp);

        mockMvc.perform(
                get("/" + communityUrl + "/" + apiVersion + "/GET_NEWS.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        )
                .andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.response..items").exists())
                .andExpect(jsonPath("$.response..user").exists())
                .andExpect(jsonPath("$.response..news").exists())
                .andExpect(jsonPath("$.response.data[1].news.items[?(@.body == '" + message.getBody() + "')]").exists())
                .andExpect(jsonPath("$.response.data[1].news.items[?(@.body == '" + message.getBody() + "')].detail").value(message.getTitle()))
                .andExpect(jsonPath("$.response.data[1].news.items[?(@.body == '" + message.getBody() + "')].messageType").value(message.getMessageType().name()))
                .andExpect(jsonPath("$.response.data[1].news.items[?(@.body == '" + message.getBody() + "')].timestampMilis").value(message.getPublishTimeMillis()));
    }

    @Test
    public void shouldReturnBannerMessage() throws Exception {
        //given
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        Message message = createMessage(communityUrl, "title", LIMITED_BANNER, true, "body", A_SPECIFIC_TRACK, "action", System.currentTimeMillis() - 1000L);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_NEWS.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        );

        //then
        resultActions.
                andExpect(status().isOk()).andDo(print()).
                andExpect(jsonPath("$.response.data[1].news.items[0].messageType", is(message.getMessageType().name()))).
                andExpect(jsonPath("$.response.data[1].news.items[0].actionType", is(message.getActionType().name()))).
                andExpect(jsonPath("$.response.data[1].news.items[0].action", is(message.getAction()))).
                andExpect(jsonPath("$.response.data[1].news.items[0].body", is(message.getBody())));
    }

    @Test
    public void shouldNotReturnBannerMessage() throws Exception {
        //given
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        long publishTimestamp = System.currentTimeMillis() - 1000L;
        createMessage(communityUrl, "title", AD, true, "body", A_SPECIFIC_TRACK, "action", publishTimestamp);
        createMessage(communityUrl, "title", NOTIFICATION, true, "body", A_SPECIFIC_TRACK, "action", publishTimestamp);
        createMessage(communityUrl, "title", FREE_TRIAL_BANNER, true, "body", A_SPECIFIC_TRACK, "action", publishTimestamp);

        //when
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_NEWS.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        );

        //then
        resultActions.
                andExpect(status().isOk()).andDo(print()).
                andExpect(jsonPath("$.response.data[1].news.items[?(@.messageType == '" + FREE_TRIAL_BANNER + "')]", is(Collections.emptyList()))).
                andExpect(jsonPath("$.response.data[1].news.items[?(@.messageType == '"+ LIMITED_BANNER+"')]", is(Collections.emptyList()))).
                andExpect(jsonPath("$.response.data[1].news.items[?(@.messageType == '"+ SUBSCRIBED_BANNER +"')]", is(Collections.emptyList()))).
                andExpect(jsonPath("$.response.data[1].news.items[?(@.messageType == '" + NEWS + "')]", is(not(Collections.emptyList())))).
                andExpect(jsonPath("$.response.data[1].news.items[?(@.messageType == '" + NOTIFICATION + "')]", is(not(Collections.emptyList())))).
                andExpect(jsonPath("$.response.data[1].news.items[?(@.messageType == '" + RICH_POPUP + "')]", is(not(Collections.emptyList())))).
                andExpect(jsonPath("$.response.data[1].news.items[?(@.messageType == '" + AD + "')]", is(not(Collections.emptyList()))));
    }

    @Test
    public void testGetNews_v5d1AndJsonAndAccCheckInfo_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.1";
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


        ResultActions accountCheckCall = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andDo(print());
        checkAccountCheck(resultActions, accountCheckCall);
    }

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


        ResultActions accountCheckCall = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andDo(print());
        checkAccountCheck(resultActions, accountCheckCall);
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
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetNewsV5d3_400_Failure() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "5.3";
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


    private Message createMessage(String communityUrl, String title, MessageType messageType, boolean activated, String body, MessageActionType messageActionType, String action, long publishTimestamp) {
        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        Message message = new Message()
                .withTitle(title)
                .withMessageType(messageType)
                .withActivated(activated)
                .withCommunity(community)
                .withBody(body)
                .withActionType(messageActionType)
                .withAction(action)
                .withPublishTimeMillis(publishTimestamp);
        return messageRepository.save(message);
    }

}
