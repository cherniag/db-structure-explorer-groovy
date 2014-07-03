package mobi.nowtechnologies.server.transport.controller;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.persistence.repository.social.GooglePlusUserInfoRepository;
import mobi.nowtechnologies.server.service.social.core.AbstractOAuth2ApiBindingCustomizer;
import mobi.nowtechnologies.server.service.social.facebook.FacebookService;
import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusService;
import mobi.nowtechnologies.server.shared.CgLibHelper;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.util.DateUtils;
import mobi.nowtechnologies.server.transport.controller.facebook.FacebookTemplateCustomizerImpl;
import mobi.nowtechnologies.server.transport.controller.googleplus.GooglePlusTemplateCustomizerImpl;
import mobi.nowtechnologies.server.transport.controller.googleplus.ProblematicGooglePlusTemplateCustomizer;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static mobi.nowtechnologies.server.service.social.googleplus.GooglePlusConstants.GOOGLE_PLUS_URL;
import static mobi.nowtechnologies.server.shared.enums.Gender.MALE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
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
    private FacebookService facebookService;

    @Resource
    private ActivationEmailRepository activationEmailRepository;

    @Resource
    private UserGroupRepository userGroupRepository;

    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;

    private final String deviceUID = "b88106713409e92622461a876abcd74b";
    private final String deviceUIDForO2 = "b88106713409e92622461a876abcd7";
    private final String deviceType = "ANDROID";
    private final String apiVersion = "6.0";
    private final String communityUrl = "hl_uk";
    private final String timestamp = "2011_12_26_07_04_23";
    private final String googlePlusUserId = "1";
    private final String googlePlusEmail = "ol@ukr.net";
    private final String pictureUrl = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg";
    private final String accessToken = "AA";
    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final String gender = "male";
    private final String birthday = "1981-11-07";
    private final String location = "Kiev";
    private final String displayName = "MAX";


    private final String userName = "userName";
    private final String locationFromFacebook = "Kyiv, Ukraine";
    private final String fbUserId = "100";


    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSuccessWithJSON() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl
                (googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId) ), googlePlusService);

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo gpDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSuccessWithJSON_v6_1() throws Exception {
        String apiVersion = "6.1";
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl
                (googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId) ), googlePlusService);

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);

        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());

        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo gpDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }

    private String buildHomepageUrl(String googlePlusUserId) {
        return GOOGLE_PLUS_URL + googlePlusUserId;
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePluskForFirstSignUpWithSuccessWithXML() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
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
                .andExpect(xpath(googlePlusElementXPath + "/profileUrl").string(pictureUrl));
    }


    @Test
    public void testParsingResponseFromGoogleServices() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, false)
        ).andExpect(status().isOk());
        User user = userRepository.findOne(googlePlusEmail, communityUrl);
        GooglePlusUserInfo googlePlusUserInfo = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(googlePlusUserInfo.getEmail(), googlePlusEmail);
        assertEquals(googlePlusUserInfo.getPicture(), pictureUrl);
        assertEquals(googlePlusUserInfo.getGooglePlusId(), googlePlusUserId);
        assertEquals(googlePlusUserInfo.getGivenName(), firstName);
        assertEquals(googlePlusUserInfo.getFamilyName(), lastName);
        assertEquals(googlePlusUserInfo.getGender(), MALE);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(DateUtils.getDateInUTC(googlePlusUserInfo.getBirthday()), DateUtils.getDateInUTC(simpleDateFormat.parse(birthday)));
        assertEquals(googlePlusUserInfo.getHomePage(), buildHomepageUrl(googlePlusUserId));
        assertEquals(googlePlusUserInfo.getLocation(), location);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForWithEmptyEmail() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl("", googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(762))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("email is not specified"));
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithInvalidGooglePlusIdSuccess() throws Exception {
        final String invalidGooglePlusUserId = "2";
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
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
        setTemplateCustomizer(new ProblematicGooglePlusTemplateCustomizer(accessToken), googlePlusService);
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
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath(googlePlusElementJsonPath + ".socialInfoType").value("GooglePlus"))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".email").value(googlePlusEmail))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".firstName").value(firstName))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".surname").value(lastName))
                .andExpect(jsonPath(googlePlusElementJsonPath + ".profileUrl").value(pictureUrl))
                .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true));
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        final String otherGooglePlusUserId = "user2";
        final String otherGooglePlusEmail = "o2@ukr.net";
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(otherGooglePlusEmail, otherGooglePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherGooglePlusUserId, accessToken, true)
        ).andExpect(status().isOk()).andDo(print());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo gpDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(gpDetails.getEmail(), otherGooglePlusEmail);
        assertEquals(gpDetails.getGooglePlusId(), otherGooglePlusUserId);
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePlusFromDifferentDevicesForOneAccountWithSuccess() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo fbDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
        user = userRepository.findOne(googlePlusEmail, communityUrl);
        String userToken1 = Utils.createTimestampToken(user.getToken(), timestamp);
        checkGetChart(userToken1, googlePlusEmail, timestamp, deviceUID, true, communityUrl);

        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
        user = userRepository.findOne(googlePlusEmail, communityUrl);
        String userToken2 = Utils.createTimestampToken(user.getToken(), timestamp);
        checkGetChart(userToken2, googlePlusEmail, timestamp, otherDeviceUID, true, communityUrl);
        checkGetChart(userToken1, googlePlusEmail, timestamp, deviceUID, false, communityUrl);

    }

    @Test
    public void testSignUpAndApplyPromoForGooglePluskForOneAccountTwiceFromSameDeviceWithSuccess() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo fbDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk()).andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(fbDetails.getEmail(), googlePlusEmail);
    }

    @Test
    public void testEmailRegistrationAfterGooglePlusApply() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
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
        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);
    }

    @Test
    public void testGooglePlusApplyAfterEmailRegistration() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        User user = userRepository.save(UserFactory.userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withDeviceUID(deviceUID).withUserGroup(userGroupRepository.findOne(9)));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        emailGenerate(user, googlePlusEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());

        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);
    }

    @Test
    public void testSignUpAndApplyPromoForGooglePlusAfterLoginToFacebook() throws Exception {
        setTemplateCustomizer(new FacebookTemplateCustomizerImpl(userName, firstName, lastName, fbUserId, googlePlusEmail, locationFromFacebook, accessToken), facebookService);

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);

        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, fbUserId, accessToken, true)
        ).andExpect(status().isOk());

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);

        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());


        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        String userToken = Utils.createTimestampToken(user.getToken(), timestamp);
        GooglePlusUserInfo gpDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSuccessAndCheckReactivation() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        assertNull(reactivationUserInfoRepository.isUserShouldBeReactivated(user));
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        GooglePlusUserInfo gpDetails = googlePlusUserInfoRepository.findByUser(user);
        assertEquals(gpDetails.getEmail(), googlePlusEmail);
        checkGetChart(userToken, user.getUserName(), timestamp, deviceUID, true, communityUrl);
    }


    @Test
    public void testSignUpAndApplyPromoForGooglePlusForFirstSignUpWithSucessForDifferentCommunities() throws Exception {
        setTemplateCustomizer(new GooglePlusTemplateCustomizerImpl(googlePlusEmail, googlePlusUserId, firstName, lastName, pictureUrl, accessToken, gender, birthday, location, displayName, buildHomepageUrl(googlePlusUserId)), googlePlusService);
        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(anyString(), anyString(), any(Community.class));

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);

        resultActions = signUpDevice(deviceUIDForO2, deviceType, apiVersion, "o2");
        String userToken1 = getUserToken(resultActions, timestamp);
        mockMvc.perform(
                buildApplyGooglePlusPromoRequest(resultActions, deviceUIDForO2, deviceType, apiVersion,  "o2", timestamp, googlePlusUserId, accessToken, true)
        ).andExpect(status().isOk());
        checkGetChart(userToken1, googlePlusEmail, timestamp, deviceUIDForO2, true, "o2");
        checkGetChart(userToken, googlePlusEmail, timestamp, deviceUID, true, communityUrl);
    }

    private void setTemplateCustomizer(AbstractOAuth2ApiBindingCustomizer customizer, Object target) {
        CgLibHelper helper = new CgLibHelper(target);
        ReflectionTestUtils.setField(helper.getTargetObject(), "templateCustomizer", customizer);
    }

    private void checkGetChart(String userToken, String userName, String timestampValue, String deviceUIDValue, boolean isChartAvailable, String communityUrlValue) throws Exception {
        ResultMatcher statusMatcher = isChartAvailable ? status().isOk() : status().isUnauthorized();
        mockMvc.perform(
                post("/" + communityUrlValue + "/5.5/GET_CHART.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestampValue)
                        .param("DEVICE_UID", deviceUIDValue)
        )
                .andExpect(statusMatcher);
    }

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
                post("/" + communityUrl + "/4.0/EMAIL_GENERATE.json")
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

}
