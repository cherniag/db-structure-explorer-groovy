package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.FBDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.FBDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
import mobi.nowtechnologies.server.service.facebook.FacebookTemplateCustomizer;
import mobi.nowtechnologies.server.shared.Utils;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.support.URIBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.server.ResultActions;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Created by oar on 2/6/14.
 */
public class ApplyInitPromoFacebookTestIT extends AbstractControllerTestIT {

    @Resource
    private FBDetailsRepository fbDetailsRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private CommunityRepository communityRepository;

    @Resource
    private FacebookService facebookService;

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        final String facebookuserId = "1";

        facebookService.setTemplateCustomizer(new FacebookTemplateCustomizer() {
            @Override
            public void customize(FacebookTemplate template) {
                RestTemplate mock = Mockito.mock(RestTemplate.class);
                FacebookProfile profile = new FacebookProfile(facebookuserId, "username", "name", "firstName", "lastName", "gender", null);
                ReflectionTestUtils.setField(profile, "email", "ol@ukr.net");
                URI uri = URIBuilder.fromUri("https://graph.facebook.com/me").build();
                Mockito.when(mock.getForObject(Mockito.eq(uri), Mockito.eq(FacebookProfile.class))).thenReturn(profile);
                ReflectionTestUtils.setField(template, "restTemplate", mock);
            }
        });

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        JSONObject jsonObject = getAccCheckContent(resultActions);
        String storedToken = (String) jsonObject.get("userToken");
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String facebookToken = "AA";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                        .param("ACCESS_TOKEN", facebookToken)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_TYPE", deviceType)
                        .param("FACEBOOK_USER_ID", facebookuserId)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FBDetails fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), "ol@ukr.net");
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithInvalidFacebookIdSucess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        final String facebookUserId = "1";
        final String invalidFacebookUserId = "2";
        ReflectionTestUtils.setField(facebookService, "templateCustomizer", new FacebookTemplateCustomizer() {
            @Override
            public void customize(FacebookTemplate template) {
                RestTemplate mock = Mockito.mock(RestTemplate.class);
                FacebookProfile profile = new FacebookProfile(facebookUserId, "username", "name", "firstName", "lastName", "gender", null);
                ReflectionTestUtils.setField(profile, "email", "ol@ukr.net");
                URI uri = URIBuilder.fromUri("https://graph.facebook.com/me").build();
                Mockito.when(mock.getForObject(Mockito.eq(uri), Mockito.eq(FacebookProfile.class))).thenReturn(profile);
                ReflectionTestUtils.setField(template, "restTemplate", mock);
            }
        });

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        JSONObject jsonObject = getAccCheckContent(resultActions);
        String storedToken = (String) jsonObject.get("userToken");
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String facebookToken = "AA";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                        .param("ACCESS_TOKEN", facebookToken)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_TYPE", deviceType)
                        .param("FACEBOOK_USER_ID", invalidFacebookUserId)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isInternalServerError());
    }



    @Test
    public void testSignUpAndApplyPromoForFacebookWithDifferentAccountsWithSucess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";

        final String facebookUserId = "user1";

        final String otherFacebookUserId = "user2";

        facebookService.setTemplateCustomizer(new FacebookTemplateCustomizer() {
            @Override
            public void customize(FacebookTemplate template) {
                RestTemplate mock = Mockito.mock(RestTemplate.class);
                FacebookProfile profile = new FacebookProfile(facebookUserId, "username", "name", "firstName", "lastName", "gender", null);
                ReflectionTestUtils.setField(profile, "email", "ol@ukr.net");
                URI uri = URIBuilder.fromUri("https://graph.facebook.com/me").build();
                Mockito.when(mock.getForObject(Mockito.eq(uri), Mockito.eq(FacebookProfile.class))).thenReturn(profile);
                ReflectionTestUtils.setField(template, "restTemplate", mock);
            }
        });

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        JSONObject jsonObject = getAccCheckContent(resultActions);
        String storedToken = (String) jsonObject.get("userToken");
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String facebookToken = "AA";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                        .param("ACCESS_TOKEN", facebookToken)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_TYPE", deviceType)
                        .param("FACEBOOK_USER_ID", facebookUserId)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());


        facebookService.setTemplateCustomizer(new FacebookTemplateCustomizer() {
            @Override
            public void customize(FacebookTemplate template) {
                RestTemplate mock = Mockito.mock(RestTemplate.class);
                FacebookProfile profile = new FacebookProfile(otherFacebookUserId, "username1", "name1", "firstName1", "lastName", "gender", null);
                ReflectionTestUtils.setField(profile, "email", "ol@ukr.net");
                URI uri = URIBuilder.fromUri("https://graph.facebook.com/me").build();
                Mockito.when(mock.getForObject(Mockito.eq(uri), Mockito.eq(FacebookProfile.class))).thenReturn(profile);
                ReflectionTestUtils.setField(template, "restTemplate", mock);
            }
        });

        jsonObject = getAccCheckContent(resultActions);
        storedToken = (String) jsonObject.get("userToken");
        userToken = Utils.createTimestampToken(storedToken, timestamp);
        facebookToken = "AA";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                        .param("ACCESS_TOKEN", facebookToken)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_TYPE", deviceType)
                        .param("FACEBOOK_USER_ID", otherFacebookUserId)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FBDetails fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getFacebookId(), otherFacebookUserId);
    }



    @Test
    public void testSignUpAndApplyPromoForFacebookFromDifferentDevicesForOneAccountWithSucess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        final String facebookUserId = "1";

        facebookService.setTemplateCustomizer(new FacebookTemplateCustomizer() {
            @Override
            public void customize(FacebookTemplate template) {
                RestTemplate mock = Mockito.mock(RestTemplate.class);
                FacebookProfile profile = new FacebookProfile(facebookUserId, "username", "name", "firstName", "lastName", "gender", null);
                ReflectionTestUtils.setField(profile, "email", "ol@ukr.net");
                URI uri = URIBuilder.fromUri("https://graph.facebook.com/me").build();
                Mockito.when(mock.getForObject(Mockito.eq(uri), Mockito.eq(FacebookProfile.class))).thenReturn(profile);
                ReflectionTestUtils.setField(template, "restTemplate", mock);
            }
        });

        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());

        JSONObject jsonObject = getAccCheckContent(resultActions);
        String storedToken = (String) jsonObject.get("userToken");
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String facebookToken = "AA";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                        .param("ACCESS_TOKEN", facebookToken)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_TYPE", deviceType)
                        .param("FACEBOOK_USER_ID", facebookUserId)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FBDetails fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), "ol@ukr.net");

        resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", otherDeviceUID)
        ).andExpect(status().isOk());

        jsonObject = getAccCheckContent(resultActions);
        storedToken = (String) jsonObject.get("userToken");
        userToken = Utils.createTimestampToken(storedToken, timestamp);
        facebookToken = "AA";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                        .param("ACCESS_TOKEN", facebookToken)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_TYPE", deviceType)
                        .param("FACEBOOK_USER_ID", facebookUserId)
                        .param("DEVICE_UID", otherDeviceUID)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), "ol@ukr.net");

    }


}
