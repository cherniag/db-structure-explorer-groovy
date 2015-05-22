package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.util.DateTimeUtils;
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
import mobi.nowtechnologies.server.social.service.facebook.impl.mock.AppTestFacebookTokenService;
import mobi.nowtechnologies.server.social.service.googleplus.impl.mock.AppTestGooglePlusTokenService;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.Date;

import com.google.common.collect.Iterables;

import org.junit.*;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;


public class SigninGooglePlusControllerIT extends AbstractControllerTestIT {

    private final String deviceUID = "b88106713409e92622461a876abcd74b";
    private final String deviceType = "ANDROID";
    private final String apiVersion = "6.0";
    private final String communityUrl = "hl_uk";
    private final String timestamp = "2011_12_26_07_04_23";
    private final String googlePlusUserId = "1";
    private final String googlePlusEmail = "ol@ukr.net";
    private final String resultUrlToGooglePlusPicture = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=200";
    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final Date birthday = DateTimeUtils.newDate(7, 11, 1981);
    private final String location = "Kiev";
    private final String accessToken = new AppTestGooglePlusTokenService().buildToken(doCreateAccessTokenInfo(googlePlusUserId, googlePlusEmail, SocialNetworkType.GOOGLE));
    private final String invalidAccessToken = new AppTestGooglePlusTokenService().buildTokenWithTokenError(doCreateAccessTokenInfo(googlePlusUserId, googlePlusEmail, SocialNetworkType.GOOGLE));
    @Resource
    private SocialNetworkInfoRepository socialNetworkInfoRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private ActivationEmailRepository activationEmailRepository;
    @Resource
    private UserGroupRepository userGroupRepository;
    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;

    @Test
    public void testSignUpAndApplyPromoForGooglePlus_LatestVersion() throws Exception {
        String apiVersion = LATEST_SERVER_API_VERSION;
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo gpDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSuccessWithJSON() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo gpDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePluskForFirstSignUpWithSuccessWithXML() throws Exception {
        String googlePlusElementXPath = "//userDetails";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, false)).andExpect(status().isOk())
               .andDo(print()).andExpect(xpath(googlePlusElementXPath + "/socialInfoType").string("GooglePlus")).andExpect(xpath(googlePlusElementXPath + "/googlePlusId").string(googlePlusUserId))
               .andExpect(xpath(googlePlusElementXPath + "/email").string(googlePlusEmail)).andExpect(xpath(googlePlusElementXPath + "/firstName").string(firstName))
               .andExpect(xpath(googlePlusElementXPath + "/surname").string(lastName)).andExpect(xpath(googlePlusElementXPath + "/profileUrl").string(resultUrlToGooglePlusPicture));
    }


    @Test
    public void testParsingResponseFromGoogleServices() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, false)).andExpect(status().isOk());
        User user = userRepository.findByUserNameAndCommunityUrl(googlePlusEmail, communityUrl);
        SocialNetworkInfo googlePlusUserInfo = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(googlePlusUserInfo.getEmail(), googlePlusEmail);
        assertEquals(googlePlusUserInfo.getProfileImageUrl(), resultUrlToGooglePlusPicture);
        assertEquals(googlePlusUserInfo.getSocialNetworkId(), googlePlusUserId);
        assertEquals(googlePlusUserInfo.getFirstName(), firstName);
        assertEquals(googlePlusUserInfo.getLastName(), lastName);
        assertEquals(googlePlusUserInfo.getGenderType(), GenderType.MALE);

        assertEquals(DateTimeUtils.getDateInUTC(googlePlusUserInfo.getBirthday()), DateTimeUtils.getDateInUTC(birthday));
        assertEquals(googlePlusUserInfo.getCity(), location);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForWithEmptyEmail() throws Exception {
        String emptyEmailAccessToken = new AppTestGooglePlusTokenService().buildToken(doCreateAccessTokenInfo(googlePlusUserId, "", SocialNetworkType.GOOGLE));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, emptyEmailAccessToken, true))
               .andExpect(status().isForbidden()).andDo(print()).andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(762))
               .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("email is not specified"));
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithInvalidGooglePlusIdSuccess() throws Exception {
        final String invalidGooglePlusUserId = "2";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidGooglePlusUserId, accessToken, true))
               .andExpect(status().isForbidden()).andDo(print()).andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(761))
               .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid user google plus id"));
    }


    @Test
    public void testLoginWithInvalidGooglePlusToken() throws Exception {
        final String invalidGooglePlusUserId = "2";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidGooglePlusUserId, invalidAccessToken, true))
               .andExpect(status().isForbidden()).andDo(print()).andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(760))
               .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid authorization token"));
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusWithDifferentAccountsWithSuccess() throws Exception {
        String googlePlusElementJsonPath = "$.response.data[0].user.userDetails";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk())
               .andDo(print()).andExpect(jsonPath(googlePlusElementJsonPath + ".socialInfoType").value("GooglePlus")).andExpect(jsonPath(googlePlusElementJsonPath + ".email").value(googlePlusEmail))
               .andExpect(jsonPath(googlePlusElementJsonPath + ".firstName").value(firstName)).andExpect(jsonPath(googlePlusElementJsonPath + ".surname").value(lastName))
               .andExpect(jsonPath(googlePlusElementJsonPath + ".profileUrl").value(resultUrlToGooglePlusPicture)).andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true));
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        final String otherGooglePlusUserId = "user2";
        final String otherGooglePlusEmail = "o2@ukr.net";

        String otherAccessToken = new AppTestGooglePlusTokenService().buildToken(doCreateAccessTokenInfo(otherGooglePlusUserId, otherGooglePlusEmail, SocialNetworkType.GOOGLE));
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherGooglePlusUserId, otherAccessToken, true))
               .andExpect(status().isOk()).andDo(print());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo gpDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(gpDetails.getEmail(), otherGooglePlusEmail);
        assertEquals(gpDetails.getSocialNetworkId(), otherGooglePlusUserId);
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePlusFromDifferentDevicesForOneAccountWithSuccess() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
        user = userRepository.findByUserNameAndCommunityUrl(googlePlusEmail, communityUrl);
        String userToken1 = Utils.createTimestampToken(user.getToken(), timestamp);
        checkGetChart(userToken1, googlePlusEmail, timestamp, deviceUID, true, communityUrl);

        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true))
               .andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
        user = userRepository.findByUserNameAndCommunityUrl(googlePlusEmail, communityUrl);
        String userToken2 = Utils.createTimestampToken(user.getToken(), timestamp);
        checkGetChart(userToken2, googlePlusEmail, timestamp, otherDeviceUID, true, communityUrl);
        checkGetChart(userToken1, googlePlusEmail, timestamp, deviceUID, false, communityUrl);

    }

    @Test
    public void testSignUpAndApplyPromoForGooglePluskForOneAccountTwiceFromSameDeviceWithSuccess() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk())
               .andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
    }

    @Test
    public void testEmailRegistrationAfterGooglePlusApply() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        emailGenerate(user, googlePlusEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));
        user = userRepository.findByUserNameAndCommunityUrl(googlePlusEmail, communityUrl);
        String userToken = Utils.createTimestampToken(user.getToken(), timestamp);
        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);
    }

    @Test
    public void testGooglePlusApplyAfterEmailRegistration() throws Exception {
        User user = userRepository
            .save(createUser().withDeviceUID(deviceUID).withUserGroup(userGroupRepository.findOne(9)));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        emailGenerate(user, googlePlusEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());

        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);
    }

    private User createUser() {return UserFactory.userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED();}

    @Test
    public void testSignUpAndApplyPromoForGooglePlusAfterLoginToFacebook() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String fbUserId = "100";
        String fbAccessToken = new AppTestFacebookTokenService().buildToken(doCreateAccessTokenInfo(fbUserId, googlePlusEmail, SocialNetworkType.FACEBOOK));

        mockMvc.perform(buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, fbAccessToken, true)).andExpect(status().isOk());

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        String userToken = Utils.createTimestampToken(user.getToken(), timestamp);
        SocialNetworkInfo gpDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSuccessAndCheckReactivation() throws Exception {
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());
        assertNull(reactivationUserInfoRepository.isUserShouldBeReactivated(user));
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        SocialNetworkInfo gpDetails = socialNetworkInfoRepository.findByUserIdAndSocialNetworkType(user.getId(), SocialNetworkType.GOOGLE);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSucessForDifferentCommunities() throws Exception {
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(anyString(), anyString(), any(Community.class));

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());
        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);

        String deviceUIDForO2 = "b88106713409e92622461a876abcd7";
        resultActions = signUpDevice(deviceUIDForO2, deviceType, apiVersion, "o2");
        String userToken1 = getUserToken(resultActions, timestamp);
        mockMvc.perform(buildApplyGooglePlusPromoRequest(resultActions, deviceUIDForO2, deviceType, apiVersion, "o2", timestamp, googlePlusUserId, accessToken, true)).andExpect(status().isOk());
        checkGetChart(userToken1, googlePlusEmail, timestamp, deviceUIDForO2, true, "o2");
        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);
    }

    private void checkGetChart(String userToken, String userName, String timestampValue, String deviceUIDValue, boolean isChartAvailable, String communityUrlValue) throws Exception {
        ResultMatcher statusMatcher = isChartAvailable ?
                                      status().isOk() :
                                      status().isUnauthorized();
        mockMvc.perform(
            post("/" + communityUrlValue + "/5.5/GET_CHART.json").param("USER_NAME", userName).param("USER_TOKEN", userToken).param("TIMESTAMP", timestampValue).param("DEVICE_UID", deviceUIDValue))
               .andExpect(statusMatcher);
    }

    private MockHttpServletRequestBuilder buildApplyGooglePlusPromoRequest(ResultActions signUpDeviceResultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl,
                                                                           String timestamp, String googlePlusUserId, String accessToken, boolean jsonRequest) throws IOException {
        String userToken = getUserToken(signUpDeviceResultActions, timestamp);
        String userName = getAccCheckContent(signUpDeviceResultActions).userName;
        String extension = jsonRequest ?
                           ".json" :
                           "";
        return post("/" + communityUrl + "/" + apiVersion + "/SIGN_IN_GOOGLE_PLUS" + extension).param("ACCESS_TOKEN", accessToken).param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp)
                                                                                               .param("DEVICE_TYPE", deviceType).param("GOOGLE_PLUS_USER_ID", googlePlusUserId)
                                                                                               .param("USER_NAME", userName).param("DEVICE_UID", deviceUID);
    }

    private SocialNetworkInfo doCreateAccessTokenInfo(String userId, String email, SocialNetworkType socialNetworkType) {
        SocialNetworkInfo info = new SocialNetworkInfo(socialNetworkType);
        info.setSocialNetworkId(userId);
        info.setEmail(email);
        info.setBirthday(birthday);
        info.setGenderType(GenderType.MALE);
        info.setCity(location);
        info.setFirstName(firstName);
        info.setLastName(lastName);
        info.setUserName("MAX");
        info.setProfileImageUrl("https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=50");
        return info;
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
        return mockMvc.perform(post("/" + communityUrl + "/4.0/EMAIL_GENERATE.json").param("EMAIL", email).param("USER_NAME", user.getDeviceUID()).param("DEVICE_UID", user.getDeviceUID()))
                      .andExpect(status().isOk()).andReturn();
    }

    private void applyInitPromoByEmail(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/" + communityUrl + "/4.0/SIGN_IN_EMAIL").param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("EMAIL_ID", activationEmail.getId().toString())
                                                                       .param("EMAIL", activationEmail.getEmail()).param("TOKEN", activationEmail.getToken())
                                                                       .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isOk());
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

}
