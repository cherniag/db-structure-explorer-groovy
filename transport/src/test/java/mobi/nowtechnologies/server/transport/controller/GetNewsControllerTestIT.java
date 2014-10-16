package mobi.nowtechnologies.server.transport.controller;

import com.google.common.net.HttpHeaders;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Message;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;

import static mobi.nowtechnologies.server.shared.enums.MessageActionType.A_SPECIFIC_TRACK;
import static mobi.nowtechnologies.server.shared.enums.MessageType.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.springframework.test.web.servlet.request.ExtMockMvcRequestBuilders.extGet;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GetNewsControllerTestIT extends AbstractControllerTestIT{

    @Autowired CommunityRepository communityRepository;

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

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        Message message = messageRepository.save(new Message().withTitle("title").withMessageType(LIMITED_BANNER).withActivated(true).withCommunity(community).withBody("body").withActionType(A_SPECIFIC_TRACK).withAction("action"));

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

        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        messageRepository.save(new Message().withTitle("title").withMessageType(AD).withActivated(true).withCommunity(community).withBody("body").withActionType(A_SPECIFIC_TRACK).withAction("action"));
        messageRepository.save(new Message().withTitle("title").withMessageType(NOTIFICATION).withActivated(true).withCommunity(community).withBody("body").withActionType(A_SPECIFIC_TRACK).withAction("action"));
        messageRepository.save(new Message().withTitle("title").withMessageType(FREE_TRIAL_BANNER).withActivated(true).withCommunity(community).withBody("body").withActionType(A_SPECIFIC_TRACK).withAction("action"));

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
    public void testGetNewAndJsonAndAccCheckInfo_62() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.2";
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


    @Test
    public void testGetNewsFor63WithCheckIfModified_Success() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        long lastValue = 1315686788000L;
        mockMvc.perform(
                extGet("/" + communityUrl + "/" + apiVersion + "/GET_NEWS.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .headers(getHttpHeadersWithIfModifiedSince(0)))
                .andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.response..items").exists())
                .andExpect(jsonPath("$.response..news").exists())
                .andExpect(header().longValue(HttpHeaders.LAST_MODIFIED, lastValue));
        mockMvc.perform(
                extGet("/" + communityUrl + "/" + apiVersion + "/GET_NEWS.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
                        .headers(getHttpHeadersWithIfModifiedSince(lastValue)))
                .andExpect(status().isNotModified()).andDo(print())
                .andExpect(content().string(""));

    }


    @Test
    public void testGetNewsFor63WithoutCheckIfModified_Fail() throws Exception {
        String userName = "+447111111114";
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String apiVersion = "6.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        mockMvc.perform(
                extGet("/" + communityUrl + "/" + apiVersion + "/GET_NEWS.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID))
                .andExpect(status().isBadRequest());

    }

}
