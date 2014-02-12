package mobi.nowtechnologies.server.transport.controller;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FBUserInfo;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
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
import org.springframework.test.web.server.MvcResult;
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

    @Resource
    private ActivationEmailRepository activationEmailRepository;

    private final String deviceUID = "b88106713409e92622461a876abcd74b";
    private final String deviceType = "ANDROID";
    private final String apiVersion = "6.0";
    private final String communityUrl = "o2";
    private final String timestamp = "2011_12_26_07_04_23";
    private final String facebookUserId = "1";
    private final String facebookEmail =  "ol@ukr.net";
    private final String facebookToken = "AA";

    private MockHttpServletRequestBuilder buildApplyFacebookPromoRequest(ResultActions resultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl, String timestamp, String facebookUserId, String facebookToken) throws UnsupportedEncodingException {
        String userToken = getUserToken(resultActions, timestamp);
        return post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO_FACEBOOK.json")
                .param("ACCESS_TOKEN", facebookToken)
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("DEVICE_TYPE", deviceType)
                .param("FACEBOOK_USER_ID", facebookUserId)
                .param("DEVICE_UID", deviceUID);
    }

    private String getUserToken(ResultActions resultActions, String timestamp) throws UnsupportedEncodingException {
        JSONObject jsonObject = getAccCheckContent(resultActions);
        String storedToken = (String) jsonObject.get("userToken");
        return Utils.createTimestampToken(storedToken, timestamp);
    }

    private ResultActions signupDevice(String deviceUID, String deviceType, String apiVersion, String communityUrl) throws Exception {
        return mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }

    private MvcResult emailGenerate(User user, String email) throws Exception {
        return mockMvc.perform(post("/o2/4.0/EMAIL_GENERATE.json")
                .param("EMAIL", email)
                .param("USER_NAME", user.getUserName())
                .param("DEVICE_UID", user.getDeviceUID())).andExpect(status().isOk()).andReturn();
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


    private void applyInitPromoByEmail(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/o2/4.0/EMAIL_CONFIRM_APPLY_INIT_PROMO")
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("EMAIL_ID", activationEmail.getId().toString())
                .param("EMAIL", activationEmail.getEmail())
                .param("TOKEN", activationEmail.getToken())
                .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isOk());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucess() throws Exception {
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
        final String invalidFacebookUserId = "2";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId,  facebookToken)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookWithDifferentAccountsWithSuccess() throws Exception {
        final String otherFacebookUserId = "user2";
        final String otherFacebookEmail = "o2@ukr.net";
        String facebookElementJsonPath = "$.response.data[0].user.socialInfo[0]";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk()).andDo(print());
        mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", facebookEmail)
                        .param("USER_TOKEN", getUserToken(resultActions, timestamp))
                        .param("TIMESTAMP", timestamp)
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
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FBUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);

        resultActions = signupDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForOneAccountTwiceFromSameDeviceWithSucess() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
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

    @Test
    public void testEmailRegistrationAfterFacebookApply() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
        resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        emailGenerate(user, facebookEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));
    }


    @Test
    public void testFacebookApplyAfterEmailRegistration() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        ResultActions resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        emailGenerate(user, facebookEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signupDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId,  facebookToken)
        ).andExpect(status().isOk());
    }

}
