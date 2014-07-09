package mobi.nowtechnologies.server.transport.controller;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.persistence.repository.social.FacebookUserInfoRepository;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.facebook.FacebookService;
import mobi.nowtechnologies.server.shared.CgLibHelper;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.transport.controller.facebook.FacebookTemplateCustomizerImpl;
import mobi.nowtechnologies.server.transport.controller.facebook.ProblematicFacebookTemplateCustomizer;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.annotation.Resource;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static mobi.nowtechnologies.server.transport.controller.AccountCheckResponseConstants.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by oar on 2/6/14.
 */
public class SigninFacebookControllerIT extends AbstractControllerTestIT {

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

    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;

    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;

    private final String deviceUID = "b88106713409e92622461a876abcd74b";
    private final String deviceType = "ANDROID";
    private final String apiVersion = "5.2";
    private final String communityUrl = "hl_uk";
    private final String timestamp = "2011_12_26_07_04_23";
    private final String fbUserId = "1";
    private final String fbEmail = "ol@ukr.net";
    private final String fbToken = "AA";

    private final String deviceUIDForO2 = "b88106713409e92622461a876abcd7";
    private final String communityO2 = "o2";

    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final String userName = "userName";
    private final String locationFromFacebook = "Kyiv, Ukraine";
    private final String locationInResponse = "Kyiv";


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessWithJSON() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessWithJSON_v6_1() throws Exception {
        String apiVersion = "6.1";
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessWithXML() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        String facebookElementXPath = AccountCheckResponseConstants.USER_DETAILS_XML_PATH;
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(xpath(facebookElementXPath + "/socialInfoType").string("Facebook"))
                .andExpect(xpath(facebookElementXPath + "/facebookId").string(fbUserId))
                .andExpect(xpath(facebookElementXPath + "/email").string(fbEmail))
                .andExpect(xpath(facebookElementXPath + "/firstName").string(firstName))
                .andExpect(xpath(facebookElementXPath + "/surname").string(lastName))
                .andExpect(xpath(facebookElementXPath + "/userName").string(userName))
                .andExpect(xpath(facebookElementXPath + "/profileUrl").string("https://graph.facebook.com/" + userName + "/picture?type=large"))
                .andExpect(xpath(facebookElementXPath + "/location").string(locationInResponse));
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessWithXMLWithOnlyOneCity() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationInResponse, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/socialInfoType").string("Facebook"))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/facebookId").string(fbUserId))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/email").string(fbEmail))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/firstName").string(firstName))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/surname").string(lastName))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/userName").string(userName))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/profileUrl").string("https://graph.facebook.com/" + userName + "/picture?type=large"))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/location").string(locationInResponse))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/gender").string("MALE"))
                .andExpect(xpath(USER_DETAILS_XML_PATH + "/birthDay").string("12/01/1990")
                );
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForWithEmptyEmail() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, "", locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(662))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("email is not specified"));
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithInvalidFacebookIdSuccess() throws Exception {
        final String invalidFacebookUserId = "2";
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, fbToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(661))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid user facebook id"));
    }


    @Test
    public void testLoginWithInvalidFacebookToken() throws Exception {
        final String invalidFacebookUserId = "2";
        setTemplateCustomizer(new ProblematicFacebookTemplateCustomizer(fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, fbToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(660))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid authorization token"));
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookWithDifferentAccountsWithSuccess() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".socialInfoType").value("Facebook"))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".facebookId").value(fbUserId))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".email").value(fbEmail))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".firstName").value(firstName))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".surname").value(lastName))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".userName").value(userName))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".location").value(locationInResponse))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".profileUrl").value("https://graph.facebook.com/" + userName + "/picture?type=large"))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".gender").value("MALE"))
                .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".birthDay").value("12/01/1990"))
                .andExpect(jsonPath(USER_JSON_PATH + ".hasAllDetails").value(true));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        final String otherFacebookUserId = "user2";
        final String otherFacebookEmail = "o2@ukr.net";
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, otherFacebookUserId, otherFacebookEmail, locationFromFacebook, fbToken));
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherFacebookUserId, fbToken, true)
        ).andExpect(status().isOk()).andDo(print());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), otherFacebookEmail);
        assertEquals(fbDetails.getFacebookId(), otherFacebookUserId);
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookFromDifferentDevicesForOneAccountWithSuccess() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);

        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForOneAccountTwiceFromSameDeviceWithSuccess() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk()).andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
    }

    @Test
    public void testEmailRegistrationAfterFacebookApply() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        emailGenerate(user, fbEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));
        user = userRepository.findOne(fbEmail, communityUrl);
        String userToken = Utils.createTimestampToken(user.getToken(), timestamp);
        mockMvc.perform(
                post("/" + communityUrl + "/5.5/GET_CHART.json")
                        .param("USER_NAME", fbEmail)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(AccountCheckResponseConstants.USER_JSON_PATH).exists());
    }


    @Test
    public void testFacebookApplyAfterEmailRegistration() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        User user = userRepository.save(UserFactory.userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withDeviceUID(deviceUID).withUserGroup(userGroupRepository.findOne(9)));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        emailGenerate(user, fbEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());

        mockMvc.perform(
                post("/" + communityUrl + "/5.5/GET_CHART.json")
                        .param("USER_NAME", fbEmail)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(AccountCheckResponseConstants.USER_JSON_PATH).exists());

    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessAndCheckReactivation() throws Exception {
        String needCheckReactivationApiVersion = "6.0";
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, needCheckReactivationApiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, needCheckReactivationApiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        assertNull(reactivationUserInfoRepository.isUserShouldBeReactivated(user));
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucessForDifferentCommunities() throws Exception {
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(anyString(), anyString(), any(Community.class));

        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
        resultActions = signUpDevice(deviceUIDForO2, deviceType, apiVersion, communityO2);
        userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUIDForO2, deviceType, apiVersion, communityO2, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        mockMvc.perform(
                post("/" + communityO2 + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUIDForO2)
        ).andExpect(status().isOk());

    }

    @Test
    public void testMergeAccountsAndCheckFlagInResponseForXML() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)
        ).andExpect(status().isOk()).andExpect(xpath(USER_XML_PATH + "/firstActivation").booleanValue(true));
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)
        ).andDo(print()).andExpect(status().isOk()).andExpect(xpath(USER_XML_PATH + "/firstActivation").booleanValue(false));
    }


    @Test
    public void testMergeAccountsAndCheckFlagInResponseForJson() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, fbEmail, locationFromFacebook, fbToken));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), fbEmail);
        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)
        ).andDo(print()).andExpect(status().isOk()).andExpect(jsonPath(USER_JSON_PATH + ".firstActivation").value(false));
    }


    private MockHttpServletRequestBuilder buildApplyFacebookPromoRequest(ResultActions signUpDeviceResultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl, String timestamp, String facebookUserId, String facebookToken, boolean jsonRequest) throws IOException {
        String userToken = getUserToken(signUpDeviceResultActions, timestamp);
        String userName = getAccCheckContent(signUpDeviceResultActions).userName;
        String extension = jsonRequest ? ".json" : "";
        return post("/" + communityUrl + "/" + apiVersion + "/SIGN_IN_FACEBOOK" + extension)
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

    private ResultActions signUpDevice(String deviceUID, String deviceType, String apiVersion, String communityUrl) throws Exception {
        return mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json")
                        .param("DEVICE_TYPE", deviceType)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }

    private MvcResult emailGenerate(User user, String email) throws Exception {
        return mockMvc.perform(
                post("/" +
                        "" + communityUrl + "/4.0/EMAIL_GENERATE.json")
                        .param("EMAIL", email)
                        .param("USER_NAME", user.getDeviceUID())
                        .param("DEVICE_UID", user.getDeviceUID())
        ).andExpect(status().isOk()).andReturn();
    }

    private void applyInitPromoByEmail(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/" + communityUrl + "/4.0/SIGN_IN_EMAIL")
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("EMAIL_ID", activationEmail.getId().toString())
                .param("EMAIL", activationEmail.getEmail())
                .param("TOKEN", activationEmail.getToken())
                .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isOk());
    }

    private void setTemplateCustomizer(AbstractOAuth2ApiBindingCustomizer customizer) {
        CgLibHelper helper = new CgLibHelper(facebookService);
        ReflectionTestUtils.setField(helper.getTargetObject(), "templateCustomizer", customizer);
    }


}
