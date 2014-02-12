package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FBUserInfo;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.FBUserInfoRepository;
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
import org.springframework.test.web.server.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Created by oar on 2/6/14.
 */
public class ApplyInitPromoFacebookTestIT extends AbstractControllerTestIT {

    @Resource
    private FBUserInfoRepository fbDetailsRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private CommunityRepository communityRepository;

    @Resource
    private FacebookService facebookService;

    private MockHttpServletRequestBuilder buildApplyFacebookPromoRequest(ResultActions resultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl, String timestamp, String facebookUserId, String facebookToken) throws UnsupportedEncodingException {
        JSONObject jsonObject = getAccCheckContent(resultActions);
        String storedToken = (String) jsonObject.get("userToken");
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        return post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                .param("ACCESS_TOKEN", facebookToken)
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("DEVICE_TYPE", deviceType)
                .param("FACEBOOK_USER_ID", facebookUserId)
                .param("DEVICE_UID", deviceUID);
    }

    private ResultActions signupDevice(String deviceUID, String deviceType, String apiVersion, String communityUrl) throws Exception {
        return mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }

    private FacebookTemplateCustomizer getTemplateCustomizer(final String facebookUserId, final String facebookEmail) {
        return new FacebookTemplateCustomizer() {
            @Override
            public void customize(FacebookTemplate template) {
                RestTemplate mock = Mockito.mock(RestTemplate.class);
                FacebookProfile profile = new FacebookProfile(facebookUserId, "username", "name", "firstName", "lastName", "gender", null);
                ReflectionTestUtils.setField(profile, "email", facebookEmail);
                URI uri = URIBuilder.fromUri("https://graph.facebook.com/me").build();
                Mockito.when(mock.getForObject(Mockito.eq(uri), Mockito.eq(FacebookProfile.class))).thenReturn(profile);
                ReflectionTestUtils.setField(template, "restTemplate", mock);
            }
        };
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        final String facebookUserId = "1";
        final String facebookEmail =  "ol@ukr.net";
        String facebookToken = "AA";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FBUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithInvalidFacebookIdSucess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        final String facebookUserId = "1";
        final String email = "ol@ukr.net";
        final String invalidFacebookUserId = "2";
        String facebookToken = "AA";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, email));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId,  facebookToken)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookWithDifferentAccountsWithSuccess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        final String facebookUserId = "user1";
        final String firstEmail = "ol@ukr.net";
        final String otherFacebookUserId = "user2";
        final String otherFacebookEmail = "o2@ukr.net";
        String facebookToken = "AA";
        String facebookElementJsonPath = "$.response.data[0].user.socialInfo[0]";

        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, firstEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath(facebookElementJsonPath + ".socialInfoType").value("Facebook"))
                .andExpect(jsonPath(facebookElementJsonPath + ".facebookId").value(facebookUserId))
                .andExpect(jsonPath(facebookElementJsonPath + ".email").value("ol@ukr.net"))
                .andExpect(jsonPath(facebookElementJsonPath + ".firstName").value("firstName"))
                .andExpect(jsonPath(facebookElementJsonPath + ".surname").value("lastName"))
                .andExpect(jsonPath(facebookElementJsonPath+ ".userName").value("username"))
                .andExpect(jsonPath(facebookElementJsonPath + ".profileUrl").value("https://graph.facebook.com/username/picture"))
                .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true));

        resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        facebookService.setTemplateCustomizer(getTemplateCustomizer(otherFacebookUserId, otherFacebookEmail));
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherFacebookUserId,  facebookToken)
        ).andExpect(status().isOk()).andDo(print());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        assertEquals(user.getUserName(), otherFacebookEmail);
        FBUserInfo fbDetails = fbDetailsRepository.findForUser(user);
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
        final String email = "ol@ukr.net";
        String facebookToken = "AA";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, email));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FBUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), email);

        resultActions = signupDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), email);
    }



    @Test
    public void testSignUpAndApplyPromoForFacebookForOneAccountTwiceFromSameDeviceWithSucess() throws Exception {
        String deviceUID = "b88106713409e92622461a876abcd74b";
        String deviceType = "ANDROID";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        final String facebookEmail = "ol@ukr.net";
        final String facebookUserId = "1";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String facebookToken = "AA";
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FBUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
        resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk()).andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }


}
