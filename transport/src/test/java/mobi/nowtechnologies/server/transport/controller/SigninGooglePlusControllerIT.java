package mobi.nowtechnologies.server.transport.controller;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.transport.controller.googleplus.GooglePlusTemplateCustomizerImpl;
import mobi.nowtechnologies.server.transport.controller.googleplus.ProblematicGooglePlusTemplateCustomizer;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.annotation.Resource;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by oar on 2/6/14.
 */
public class SigninGooglePlusControllerIT extends AbstractControllerTestIT {

    @Resource
    private GooglePlusUserInfoRepository googlePlusUserInfoRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private CommunityRepository communityRepository;

    @Resource
    private GooglePlusService googlePlusService;

    @Resource
    private ActivationEmailRepository activationEmailRepository;

    private final String deviceUID = "b88106713409e92622461a876abcd74b";
    private final String deviceType = "ANDROID";
    private final String apiVersion = "5.2";
    private final String communityUrl = "o2";
    private final String timestamp = "2011_12_26_07_04_23";
    private final String googlePlusUserId = "1";
    private final String googlePlusEmail = "ol@ukr.net";
    private final String pictureUrl = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg";
    private final String accessToken = "AA";
    private final String firstName = "firstName";
    private final String lastName = "lastName";


    private MockHttpServletRequestBuilder buildApplyGooglePlusPromoRequest(ResultActions signUpDeviceResultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl, String timestamp, String googlePlusUserId, String accessToken, boolean jsonRequest) throws IOException {
        String userToken = getUserToken(signUpDeviceResultActions, timestamp);
        String userName = getAccCheckContent(signUpDeviceResultActions).userName;
        String extension = jsonRequest ? ".json" : "";
        return post("/" + communityUrl + "/" + apiVersion + "/SIGN_IN_GOOGLE_PLUS" + extension)
                .param("ACCESS_TOKEN", accessToken)
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("DEVICE_TYPE", deviceType)
                .param("GOOGLE_PLUS_USER_ID", googlePlusUserId)
                .param("USER_NAME", userName)
                .param("DEVICE_UID", deviceUID);
    }

    private String getUserToken(ResultActions resultActions, String timestamp) throws IOException {
        AccountCheckDto dto = getAccCheckContent(resultActions);
        String storedToken = dto.userToken;
        return Utils.createTimestampToken(storedToken, timestamp);
    }

    private ResultActions signUpDevice(String deviceUID, String deviceType, String apiVersion, String communityUrl) throws Exception {
        return mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }

    private MvcResult emailGenerate(User user, String email) throws Exception {
        return mockMvc.perform(
                post("/o2/4.0/EMAIL_GENERATE.json")
                        .param("EMAIL", email)
                        .param("USER_NAME", user.getDeviceUID())
                        .param("DEVICE_UID", user.getDeviceUID())
        ).andExpect(status().isOk()).andReturn();
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
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSuccessWithJSON() throws Exception {
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo gpDetails = googlePlusUserInfoRepository.findForUser(user);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePluskForFirstSignUpWithSuccessWithXML() throws Exception {
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        String googlePlusElementXPath = "//userDetails";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, false)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(xpath(googlePlusElementXPath + "/socialInfoType").string("GooglePlus"))
                .andExpect(xpath(googlePlusElementXPath + "/googlePlusId").string(googlePlusUserId))
                .andExpect(xpath(googlePlusElementXPath + "/email").string(googlePlusEmail))
                .andExpect(xpath(googlePlusElementXPath + "/firstName").string(firstName))
                .andExpect(xpath(googlePlusElementXPath + "/surname").string(lastName))
                .andExpect(xpath(googlePlusElementXPath + "/pictureUrl").string(pictureUrl));
    }



    @Test
    public void testSignUpAndApplyPromoForGooglePlusForWithEmptyEmail() throws Exception {
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl("", googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(762))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("Email is not specified"));
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithInvalidGooglePlusIdSuccess() throws Exception {
        final String invalidGooglePlusUserId = "2";
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidGooglePlusUserId, accessToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(761))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid user google plus id"));
    }



    @Test
    public void testLoginWithInvalidGooglePlusToken() throws Exception {
        final String invalidGooglePlusUserId = "2";
        googlePlusService.setTemplateCustomizer(new ProblematicGooglePlusTemplateCustomizer(accessToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidGooglePlusUserId, accessToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(760))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid authorization token"));
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusWithDifferentAccountsWithSuccess() throws Exception {
        String googlePlusElementJsonPath = "$.response.data[0].user.userDetails";
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath(googlePlusElementJsonPath + ".socialInfoType").value("GooglePlus"))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".email").value(googlePlusEmail))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".firstName").value(firstName))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".surname").value(lastName))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".pictureUrl").value(pictureUrl))
                .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        final String otherGooglePlusUserId = "user2";
        final String otherGooglePlusEmail = "o2@ukr.net";
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(otherGooglePlusEmail, otherGooglePlusUserId, firstName, lastName, pictureUrl, accessToken));
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherGooglePlusUserId, accessToken, true)
        ).andExpect(status().isOk()).andDo(print());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo gpDetails = googlePlusUserInfoRepository.findForUser(user);
        assertEquals(gpDetails.getEmail(), otherGooglePlusEmail);
        assertEquals(gpDetails.getGooglePlusId(), otherGooglePlusUserId);
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePlusFromDifferentDevicesForOneAccountWithSuccess() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo fbDetails = googlePlusUserInfoRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);

        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = googlePlusUserInfoRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePluskForOneAccountTwiceFromSameDeviceWithSuccess() throws Exception {
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo fbDetails = googlePlusUserInfoRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk()).andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = googlePlusUserInfoRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
    }

    @Test
    public void testEmailRegistrationAfterGooglePlusApply() throws Exception {
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        emailGenerate(user, googlePlusEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));
        user = userRepository.findOne(googlePlusEmail, communityUrl);
        String userToken = Utils.createTimestampToken(user.getToken(), timestamp);
        mockMvc.perform(
                post("/" + communityUrl + "/3.8/GET_CHART.json")
                        .param("USER_NAME", googlePlusEmail)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.response.data[0].user").exists());
    }


    @Test
    public void testGooglePlusApplyAfterEmailRegistration() throws Exception {
        googlePlusService.setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken));
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        emailGenerate(user, googlePlusEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());

        mockMvc.perform(
                post("/" + communityUrl + "/3.8/GET_CHART.json")
                        .param("USER_NAME", googlePlusEmail)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.response.data[0].user").exists());
    }

}
