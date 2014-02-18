package mobi.nowtechnologies.server.transport.controller;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
import mobi.nowtechnologies.server.service.facebook.FacebookTemplateCustomizer;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.ResultActions;
import org.springframework.test.web.server.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.client.match.RequestMatchers.*;
import static org.springframework.test.web.client.response.ResponseCreators.withStatus;
import static org.springframework.test.web.client.response.ResponseCreators.withSuccess;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * Created by oar on 2/6/14.
 */
public class SignInFacebookTestIT extends AbstractControllerTestIT {

    @Resource
    private FacebookUserInfoRepository fbDetailsRepository;

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
    private final String facebookEmail = "ol@ukr.net";
    private final String facebookToken = "AA";

    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final String userName = "userName";


    private MockHttpServletRequestBuilder buildApplyFacebookPromoRequest(ResultActions signUpDeviceResultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl, String timestamp, String facebookUserId, String facebookToken) throws IOException {
        String userToken = getUserToken(signUpDeviceResultActions, timestamp);
        String userName = getUserName(signUpDeviceResultActions);
        return post("/" + communityUrl + "/" + apiVersion + "/SIGN_IN_FACEBOOK.json")
                .param("ACCESS_TOKEN", facebookToken)
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("DEVICE_TYPE", deviceType)
                .param("FACEBOOK_USER_ID", facebookUserId)
                .param("USER_NAME", userName)
                .param("DEVICE_UID", deviceUID);
    }

    private String getUserToken(ResultActions resultActions, String timestamp) throws IOException {
        AccountCheckDto dto = getAccCheckContent(resultActions);
        String storedToken = dto.userToken;
        return Utils.createTimestampToken(storedToken, timestamp);
    }

    private String getUserName(ResultActions resultActions) throws IOException {
        AccountCheckDto dto = getAccCheckContent(resultActions);
        return  dto.userName;
    }

    private ResultActions signUpDevice(String deviceUID, String deviceType, String apiVersion, String communityUrl) throws Exception {
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
                RestTemplate mock = template.getRestTemplate();
                MockRestServiceServer mockServer = MockRestServiceServer.createServer(mock);
                String response = "{\n" +
                        "  \"id\": \"" + facebookUserId + "\", \n" +
                        "  \"email\": \"" + facebookEmail + "\", \n" +
                        "  \"first_name\": \"" + firstName + "\", \n" +
                        "  \"last_name\": \"" + lastName + "\", \n" +
                        "  \"link\": \"https://www.facebook.com/blah blah\", \n" +
                        "  \"username\": \"" + userName + "\", \n" +
                        "  \"type\": \"user\"\n" +
                        "}";
                mockServer.expect(requestTo("https://graph.facebook.com/me"))
                        .andExpect(method(HttpMethod.GET)).
                        andExpect(header("Authorization", "OAuth " + facebookToken)).
                        andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
            }
        };
    }

    private FacebookTemplateCustomizer getTemplateCustomizerWithErrorFromFacebook(final String facebookUserId, final String facebookEmail) {
        return new FacebookTemplateCustomizer() {
            @Override
            public void customize(FacebookTemplate template) {
                RestTemplate mock = template.getRestTemplate();
                MockRestServiceServer mockServer = MockRestServiceServer.createServer(mock);
                String response = "{\n" +
                        "   \"error\": {\n" +
                        "      \"message\": \"An active access token must be used to query information about the current user.\",\n" +
                        "      \"type\": \"OAuthException\",\n" +
                        "      \"code\": 2500\n" +
                        "   }\n" +
                        "}";
                mockServer.expect(requestTo("https://graph.facebook.com/me"))
                        .andExpect(method(HttpMethod.GET)).
                        andExpect(header("Authorization", "OAuth " + facebookToken)).
                        andRespond(withStatus(HttpStatus.BAD_REQUEST).body(response).contentType(MediaType.APPLICATION_JSON));
            }
        };
    }

    private void applyInitPromoByEmail(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/o2/4.0/SIGN_IN_EMAIL")
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

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithInvalidFacebookIdSucess() throws Exception {
        final String invalidFacebookUserId = "2";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, facebookToken)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(661))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid user facebook id"));
    }


    @Test
    public void tesLoginWithInvalidFacebookToken() throws Exception {
        final String invalidFacebookUserId = "2";
        facebookService.setTemplateCustomizer(getTemplateCustomizerWithErrorFromFacebook(facebookUserId, facebookEmail));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, facebookToken)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(660))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid authorization token"));
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookWithDifferentAccountsWithSuccess() throws Exception {
        String facebookElementJsonPath = "$.response.data[0].user.userDetails";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath(facebookElementJsonPath + ".socialInfoType").value("Facebook"))
                .andExpect(jsonPath(facebookElementJsonPath + ".facebookId").value(facebookUserId))
                .andExpect(jsonPath(facebookElementJsonPath + ".email").value(facebookEmail))
                .andExpect(jsonPath(facebookElementJsonPath + ".firstName").value(firstName))
                .andExpect(jsonPath(facebookElementJsonPath + ".surname").value(lastName))
                .andExpect(jsonPath(facebookElementJsonPath + ".userName").value(userName))
                .andExpect(jsonPath(facebookElementJsonPath + ".profileUrl").value("https://graph.facebook.com/" + userName + "/picture"))
                .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        final String otherFacebookUserId = "user2";
        final String otherFacebookEmail = "o2@ukr.net";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(otherFacebookUserId, otherFacebookEmail));
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherFacebookUserId, facebookToken)
        ).andExpect(status().isOk()).andDo(print());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        assertEquals(user.getUserName(), otherFacebookEmail);
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getFacebookId(), otherFacebookUserId);
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookFromDifferentDevicesForOneAccountWithSucess() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);

        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForOneAccountTwiceFromSameDeviceWithSucess() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk()).andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }

    @Test
    public void testEmailRegistrationAfterFacebookApply() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk());
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        emailGenerate(user, facebookEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));
    }


    @Test
    public void testFacebookApplyAfterEmailRegistration() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail));
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        emailGenerate(user, facebookEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken)
        ).andExpect(status().isOk());
    }

}
