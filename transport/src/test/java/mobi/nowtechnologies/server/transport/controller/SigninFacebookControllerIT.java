package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.ReactivationUserInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.ReactivationUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.social.domain.GenderType;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository;
import mobi.nowtechnologies.server.social.domain.SocialNetworkType;
import mobi.nowtechnologies.server.social.service.facebook.FacebookClient;
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService;
import static mobi.nowtechnologies.server.transport.controller.AccountCheckResponseConstants.USER_DETAILS_JSON_PATH;
import static mobi.nowtechnologies.server.transport.controller.AccountCheckResponseConstants.USER_DETAILS_XML_PATH;
import static mobi.nowtechnologies.server.transport.controller.AccountCheckResponseConstants.USER_JSON_PATH;
import static mobi.nowtechnologies.server.transport.controller.AccountCheckResponseConstants.USER_XML_PATH;

import javax.annotation.Resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.collect.Iterables;

import org.springframework.http.MediaType;
import org.springframework.social.facebook.api.GraphApi;

import org.junit.*;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;


/**
 * Created by oar on 2/6/14.
 */
public class SigninFacebookControllerIT extends AbstractControllerTestIT {

    public static final GenderType GENDER = GenderType.MALE;
    public static final String COUNTRY = "USSR";
    private final Date currentDate = new Date();
    private final String deviceUID = "b88106713409e92622461a876abcd74b";
    private final String deviceType = "ANDROID";
    private final String apiVersion = "5.2";
    private final String communityUrl = "hl_uk";
    private final String timestamp = "2011_12_26_07_04_23";
    private final String fbUserId = "1";
    private final String fbEmail = "ol@ukr.net";
    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final String locationInResponse = "Kyiv";
    private final String fbToken = new AppTestFacebookTokenService().buildToken(doCreateAccessTokenInfo(fbUserId, fbEmail));
    private final String fbInvalidToken = new AppTestFacebookTokenService().buildTokenWithTokenError(doCreateAccessTokenInfo(fbUserId, fbEmail));
    @Resource
    private SocialNetworkInfoRepository socialNetworkInfoRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private ActivationEmailRepository activationEmailRepository;
    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;
    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;

    private SocialNetworkInfo doCreateAccessTokenInfo(String fbUserId, String otherFacebookEmail) {
        SocialNetworkInfo info = new SocialNetworkInfo(SocialNetworkType.FACEBOOK);
        info.setBirthday(currentDate);
        info.setEmail(otherFacebookEmail);
        info.setGenderType(GENDER);
        info.setCity(locationInResponse);
        info.setCountry(COUNTRY);
        info.setUserName(fbUserId);
        info.setSocialNetworkId(fbUserId);
        info.setFirstName(firstName);
        info.setLastName(lastName);
        return info;
    }

    @Test
    public void testSignUpAndApplyPromoForFacebook_LatestVersion() throws Exception {
        final String apiVersion = LATEST_SERVER_API_VERSION;
        String widthHeight = "720x1280";

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
        mockMvc.perform(get("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", user.getUserName()).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp)
                                                                                      .param("DEVICE_UID", deviceUID).param("WIDTHXHEIGHT", widthHeight)).andExpect(status().isOk());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessWithJSON() throws Exception {


        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", user.getUserName()).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp)
                                                                                       .param("DEVICE_UID", deviceUID)).andExpect(status().isOk());
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessWithXML() throws Exception {

        String facebookElementXPath = AccountCheckResponseConstants.USER_DETAILS_XML_PATH;
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)).andExpect(status().isOk()).andDo(print())
               .andExpect(xpath(facebookElementXPath + "/socialInfoType").string("Facebook")).andExpect(xpath(facebookElementXPath + "/facebookId").string(fbUserId))
               .andExpect(xpath(facebookElementXPath + "/email").string(fbEmail)).andExpect(xpath(facebookElementXPath + "/firstName").string(firstName))
               .andExpect(xpath(facebookElementXPath + "/surname").string(lastName)).andExpect(xpath(facebookElementXPath + "/userName").string(fbUserId))
               .andExpect(xpath(facebookElementXPath + "/profileUrl").string(GraphApi.GRAPH_API_URL + fbUserId + "/picture?type=large"))
               .andExpect(xpath(facebookElementXPath + "/location").string(locationInResponse));
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessWithXMLWithOnlyOneCity() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)).andExpect(status().isOk()).andDo(print())
               .andExpect(xpath(USER_DETAILS_XML_PATH + "/socialInfoType").string("Facebook")).andExpect(xpath(USER_DETAILS_XML_PATH + "/facebookId").string(fbUserId))
               .andExpect(xpath(USER_DETAILS_XML_PATH + "/email").string(fbEmail)).andExpect(xpath(USER_DETAILS_XML_PATH + "/firstName").string(firstName))
               .andExpect(xpath(USER_DETAILS_XML_PATH + "/surname").string(lastName)).andExpect(xpath(USER_DETAILS_XML_PATH + "/userName").string(fbUserId))
               .andExpect(xpath(USER_DETAILS_XML_PATH + "/profileUrl").string(GraphApi.GRAPH_API_URL + fbUserId + "/picture?type=large"))
               .andExpect(xpath(USER_DETAILS_XML_PATH + "/location").string(locationInResponse)).andExpect(xpath(USER_DETAILS_XML_PATH + "/gender").string("MALE"))
               .andExpect(xpath(USER_DETAILS_XML_PATH + "/birthDay").string(new SimpleDateFormat(FacebookClient.DATE_FORMAT).format(currentDate)));
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForWithEmptyEmail() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithInvalidFacebookId() throws Exception {
        final String invalidFacebookUserId = "2";

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, fbToken, true))
               .andExpect(status().isForbidden()).andDo(print()).andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(661))
               .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid user facebook id"));
    }


    @Test
    public void testLoginWithInvalidFacebookToken() throws Exception {
        final String invalidFacebookUserId = "2";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, fbInvalidToken, true))
               .andExpect(status().isForbidden()).andDo(print()).andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(660))
               .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid authorization token"));
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookWithDifferentAccountsWithSuccess() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk()).andDo(print())
               .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".socialInfoType").value("Facebook")).andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".facebookId").value(fbUserId))
               .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".email").value(fbEmail)).andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".firstName").value(firstName))
               .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".surname").value(lastName)).andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".userName").value(fbUserId))
               .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".location").value(locationInResponse))
               .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".profileUrl").value(GraphApi.GRAPH_API_URL + fbUserId + "/picture?type=large"))
               .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".gender").value("MALE"))
               .andExpect(jsonPath(USER_DETAILS_JSON_PATH + ".birthDay").value(new SimpleDateFormat(FacebookClient.DATE_FORMAT).format(currentDate)))
               .andExpect(jsonPath(USER_JSON_PATH + ".hasAllDetails").value(true));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        final String otherFacebookUserId = "user2";
        final String otherFacebookEmail = "o2@ukr.net";
        String fbToken = new AppTestFacebookTokenService().buildToken(doCreateAccessTokenInfo(otherFacebookUserId, otherFacebookEmail));

        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherFacebookUserId, fbToken, true)).andExpect(status().isOk())
               .andDo(print());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(otherFacebookEmail, fbDetails.getEmail());
        assertEquals(otherFacebookUserId, fbDetails.getSocialNetworkId());
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookFromDifferentDevicesForOneAccountWithSuccess() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());

        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForOneAccountTwiceFromSameDeviceWithSuccess() throws Exception {

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk()).andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
    }

    @Test
    public void testEmailRegistrationAfterFacebookApply() throws Exception {

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        emailGenerate(user, fbEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));
        user = userRepository.findByUserNameAndCommunityUrl(fbEmail, communityUrl);
        String userToken = Utils.createTimestampToken(user.getToken(), timestamp);
        mockMvc.perform(post("/" + communityUrl + "/5.5/GET_CHART.json").param("USER_NAME", fbEmail).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isOk()).andDo(print()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath(AccountCheckResponseConstants.USER_JSON_PATH).exists());
    }


    @Test
    public void testFacebookApplyAfterEmailRegistration() throws Exception {

        User user = userRepository
            .save(createUser().withDeviceUID(deviceUID).withUserGroup(userGroupRepository.findOne(9)));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        emailGenerate(user, fbEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());

        mockMvc.perform(post("/" + communityUrl + "/5.5/GET_CHART.json").param("USER_NAME", fbEmail).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isOk()).andDo(print()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath(AccountCheckResponseConstants.USER_JSON_PATH).exists());

    }

    private User createUser() {
        User user = UserFactory.userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED();
        user.setUserGroup(userGroupRepository.findOne(7));
        return user;
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSuccessAndCheckReactivation() throws Exception {
        String needCheckReactivationApiVersion = "6.0";

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, needCheckReactivationApiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, needCheckReactivationApiVersion, communityUrl, timestamp, fbUserId, fbToken, true))
               .andExpect(status().isOk());
        assertNull(reactivationUserInfoRepository.isUserShouldBeReactivated(user));
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", user.getUserName()).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp)
                                                                                       .param("DEVICE_UID", deviceUID)).andExpect(status().isOk());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucessForDifferentCommunities() throws Exception {
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(anyString(), anyString(), any(Community.class));


        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", user.getUserName()).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp)
                                                                                       .param("DEVICE_UID", deviceUID)).andExpect(status().isOk());
        String deviceUIDForO2 = "b88106713409e92622461a876abcd7";
        String communityO2 = "o2";
        resultActions = signUpDevice(deviceUIDForO2, deviceType, apiVersion, communityO2);
        userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUIDForO2, deviceType, apiVersion, communityO2, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());
        mockMvc.perform(post("/" + communityO2 + "/" + apiVersion + "/GET_CHART.json").param("USER_NAME", user.getUserName()).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp)
                                                                                      .param("DEVICE_UID", deviceUIDForO2)).andExpect(status().isOk());

    }

    @Test
    public void testMergeAccountsAndCheckFlagInResponseForXML() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)).andExpect(status().isOk())
               .andExpect(xpath(USER_XML_PATH + "/firstActivation").booleanValue(true));
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, false)).andDo(print())
               .andExpect(status().isOk()).andExpect(xpath(USER_XML_PATH + "/firstActivation").booleanValue(false));
    }


    @Test
    public void testMergeAccountsAndCheckFlagInResponseForJson() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.FACEBOOK);
        assertEquals(fbEmail, fbDetails.getEmail());
        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbToken, true)).andDo(print())
               .andExpect(status().isOk()).andExpect(jsonPath(USER_JSON_PATH + ".firstActivation").value(false));
    }

    private MockHttpServletRequestBuilder buildApplyFacebookPromoRequest(ResultActions signUpDeviceResultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl,
                                                                         String timestamp, String facebookUserId, String facebookToken, boolean jsonRequest) throws IOException {
        String userToken = getUserToken(signUpDeviceResultActions, timestamp);
        String userName = getAccCheckContent(signUpDeviceResultActions).userName;
        String extension = jsonRequest ?
                           ".json" :
                           "";
        return post("/" + communityUrl + "/" + apiVersion + "/SIGN_IN_FACEBOOK" + extension).param("ACCESS_TOKEN", facebookToken).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp)
                                                                                            .param("DEVICE_TYPE", deviceType).param("FACEBOOK_USER_ID", facebookUserId).param("USER_NAME", userName)
                                                                                            .param("DEVICE_UID", deviceUID);
    }

    private String getUserToken(ResultActions resultActions, String timestamp) throws IOException {
        AccountCheckDto dto = getAccCheckContent(resultActions);
        String storedToken = dto.userToken;
        return Utils.createTimestampToken(storedToken, timestamp);
    }

    private ResultActions signUpDevice(String deviceUID, String deviceType, String apiVersion, String communityUrl) throws Exception {
        return mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/SIGN_UP_DEVICE.json").param("DEVICE_TYPE", deviceType).param("DEVICE_UID", deviceUID)).andExpect(status().isOk());
    }

    private MvcResult emailGenerate(User user, String email) throws Exception {
        return mockMvc.perform(post("/" +
                                    "" + communityUrl + "/4.0/EMAIL_GENERATE.json").param("EMAIL", email).param("USER_NAME", user.getDeviceUID()).param("DEVICE_UID", user.getDeviceUID()))
                      .andExpect(status().isOk()).andReturn();
    }

    private void applyInitPromoByEmail(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/" + communityUrl + "/4.0/SIGN_IN_EMAIL").param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("EMAIL_ID", activationEmail.getId().toString())
                                                                       .param("EMAIL", activationEmail.getEmail()).param("TOKEN", activationEmail.getToken())
                                                                       .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isOk());
    }
}
