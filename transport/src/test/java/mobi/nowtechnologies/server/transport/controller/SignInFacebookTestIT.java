package mobi.nowtechnologies.server.transport.controller;

import com.google.common.collect.Iterables;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.social.FacebookUserInfo;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
import mobi.nowtechnologies.server.service.facebook.FacebookTemplateCustomizer;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType.NEWS;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.DOWN;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


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

    @Resource(name = "promotionRepository")
    private PromotionRepository promotionRepository;

    @Resource(name = "promoCodeRepository")
    private PromoCodeRepository promoCodeRepository;

    @Resource(name = "userGroupRepository")
    private UserGroupRepository userGroupRepository;

    @Resource(name = "chartDetailRepository")
    private ChartDetailRepository chartDetailRepository;

    @Resource(name = "chartRepository")
    private ChartRepository chartRepository;

    @Resource(name = "mediaRepository")
    private MediaRepository mediaRepository;

    @Resource(name = "messageRepository")
    private MessageRepository messageRepository;

    private final String deviceUID = "b88106713409e92622461a876abcd74b";
    private final String deviceType = "ANDROID";
    private final String apiVersion = "5.2";
    private final String communityUrl = "hl_uk";
    private final String timestamp = "2011_12_26_07_04_23";
    private final String facebookUserId = "1";
    private final String facebookEmail = "ol@ukr.net";
    private final String facebookToken = "AA";

    private final String firstName = "firstName";
    private final String lastName = "lastName";
    private final String userName = "userName";
    private final String locationFromFacebook = "Kyiv, Ukraine";
    private final String locationInResponse = "Kyiv";

    private static  int position = 0;
    private static Promotion promotion;
    private static Message message;
    private static ChartDetail chartDetail;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        UserGroup userGroup = userGroupRepository.findOne(9);

        if(isNull(promotion)) {
            promotion = promotionRepository.save(new Promotion().withUserGroup(userGroup).withDescription("").withEndDate(Integer.MAX_VALUE).withIsActive(true).withFreeWeeks((byte) 8).withType(ADD_FREE_WEEKS_PROMOTION));

            promoCodeRepository.save(new PromoCode().withPromotion(promotion).withCode("promo8").withMediaType(AUDIO));
        }
        Community community = userGroup.getCommunity();
        //List<Chart> charts = chartRepository.getByCommunityURL(community.getRewriteUrlParameter());

        //chartDetail = chartDetailRepository.save(new ChartDetail().withChart(charts.get(0)).withMedia(mediaRepository.findOne(50)).withPrevPosition((byte) 1).withChgPosition(DOWN).withChannel("HEATSEEKER"));

        message = messageRepository.save(new Message().withMessageType(NEWS).withPosition(position++).withCommunity(community).withBody("").withPublishTimeMillis(1).withTitle("").withActivated(true));
    }


    private MockHttpServletRequestBuilder buildApplyFacebookPromoRequest(ResultActions signUpDeviceResultActions, String deviceUID, String deviceType, String apiVersion, String communityUrl, String timestamp, String facebookUserId, String facebookToken, boolean jsonRequest) throws IOException {
        String userToken = getUserToken(signUpDeviceResultActions, timestamp);
        String userName = getUserName(signUpDeviceResultActions);
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
        return mockMvc.perform(post("/hl_uk/4.0/EMAIL_GENERATE.json")
                .param("EMAIL", email)
                .param("USER_NAME", user.getDeviceUID())
                .param("DEVICE_UID", user.getDeviceUID())).andExpect(status().isOk()).andReturn();
    }

    private FacebookTemplateCustomizer getTemplateCustomizer(final String facebookUserId,
                                                             final String facebookEmail,
                                                             final String returnedFacebookLocation) {
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
                        "  \"gender\": \"male\", \n" +
                        "  \"birthday\": \"12/01/1990\", \n" +
                        "  \"type\": \"user\"\n" + ", \n" +
                        " \"location\": {\n" +
                        "    \"id\": \"111227078906045\", \n" +
                        "    \"name\": \"" + returnedFacebookLocation + "\" \n" +
                        "  }" +
                        "}";
                mockServer.expect(requestTo("https://graph.facebook.com/me"))
                        .andExpect(method(HttpMethod.GET)).
                        andExpect(header("Authorization", "OAuth " + facebookToken)).
                        andRespond(withSuccess(response, APPLICATION_JSON));
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
                        andRespond(withStatus(HttpStatus.BAD_REQUEST).body(response).contentType(APPLICATION_JSON));
            }
        };
    }

    private void applyInitPromoByEmail(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/hl_uk/4.0/SIGN_IN_EMAIL")
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("EMAIL_ID", activationEmail.getId().toString())
                .param("EMAIL", activationEmail.getEmail())
                .param("TOKEN", activationEmail.getToken())
                .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isOk());
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucess() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));

        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk());
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucessWithXML() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));
        String facebookElementXPath = "//userDetails";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, false)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(xpath(facebookElementXPath + "/socialInfoType").string("Facebook"))
                .andExpect(xpath(facebookElementXPath + "/facebookId").string(facebookUserId))
                .andExpect(xpath(facebookElementXPath + "/email").string(facebookEmail))
                .andExpect(xpath(facebookElementXPath + "/firstName").string(firstName))
                .andExpect(xpath(facebookElementXPath + "/surname").string(lastName))
                .andExpect(xpath(facebookElementXPath + "/userName").string(userName))
                .andExpect(xpath(facebookElementXPath + "/profileUrl").string("https://graph.facebook.com/" + userName + "/picture?type=large"))
                .andExpect(xpath(facebookElementXPath + "/location").string(locationInResponse));
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithSucessWithXMLWithOnlyOneCity() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationInResponse));
        String facebookElementXPath = "//userDetails";
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, false)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(xpath(facebookElementXPath + "/socialInfoType").string("Facebook"))
                .andExpect(xpath(facebookElementXPath + "/facebookId").string(facebookUserId))
                .andExpect(xpath(facebookElementXPath + "/email").string(facebookEmail))
                .andExpect(xpath(facebookElementXPath + "/firstName").string(firstName))
                .andExpect(xpath(facebookElementXPath + "/surname").string(lastName))
                .andExpect(xpath(facebookElementXPath + "/userName").string(userName))
                .andExpect(xpath(facebookElementXPath + "/profileUrl").string("https://graph.facebook.com/" + userName + "/picture?type=large"))
                .andExpect(xpath(facebookElementXPath + "/location").string(locationInResponse))
                .andExpect(xpath(facebookElementXPath + "/gender").string("MALE"))
                .andExpect(xpath(facebookElementXPath + "/birthDay").string("12/01/1990")
                );
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForWithEmptyEmail() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, "", locationFromFacebook));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(662))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("Email is not specified"));
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForFirstSignUpWithInvalidFacebookIdSucess() throws Exception {
        final String invalidFacebookUserId = "2";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, facebookToken, true)
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
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, invalidFacebookUserId, facebookToken, true)
        ).andExpect(status().isForbidden()).andDo(print())
                .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(660))
                .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("invalid authorization token"));
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookWithDifferentAccountsWithSuccess() throws Exception {
        String facebookElementJsonPath = "$.response.data[0].user.userDetails";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath(facebookElementJsonPath + ".socialInfoType").value("Facebook"))
                .andExpect(jsonPath(facebookElementJsonPath + ".facebookId").value(facebookUserId))
                .andExpect(jsonPath(facebookElementJsonPath + ".email").value(facebookEmail))
                .andExpect(jsonPath(facebookElementJsonPath + ".firstName").value(firstName))
                .andExpect(jsonPath(facebookElementJsonPath + ".surname").value(lastName))
                .andExpect(jsonPath(facebookElementJsonPath + ".userName").value(userName))
                .andExpect(jsonPath(facebookElementJsonPath + ".location").value(locationInResponse))
                .andExpect(jsonPath(facebookElementJsonPath + ".profileUrl").value("https://graph.facebook.com/" + userName + "/picture?type=large"))
                .andExpect(jsonPath(facebookElementJsonPath + ".gender").value("MALE"))
                .andExpect(jsonPath(facebookElementJsonPath + ".birthDay").value("12/01/1990"))
                .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        final String otherFacebookUserId = "user2";
        final String otherFacebookEmail = "hl_uk@ukr.net";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(otherFacebookUserId, otherFacebookEmail, locationFromFacebook));
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, otherFacebookUserId, facebookToken, true)
        ).andExpect(status().isOk()).andDo(print());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), otherFacebookEmail);
        assertEquals(fbDetails.getFacebookId(), otherFacebookUserId);
    }


    @Test
    public void testSignUpAndApplyPromoForFacebookFromDifferentDevicesForOneAccountWithSucess() throws Exception {
        String otherDeviceUID = "b88106713409e92622461a876abcd74b1";
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);

        resultActions = signUpDevice(otherDeviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, otherDeviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk());
        user = userRepository.findByDeviceUIDAndCommunity(otherDeviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }

    @Test
    public void testSignUpAndApplyPromoForFacebookForOneAccountTwiceFromSameDeviceWithSucess() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk());
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        FacebookUserInfo fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk()).andDo(print());
        user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        fbDetails = fbDetailsRepository.findForUser(user);
        assertEquals(fbDetails.getEmail(), facebookEmail);
    }

    @Test
    public void testEmailRegistrationAfterFacebookApply() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk());
        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        User user = userRepository.findByDeviceUIDAndCommunity(deviceUID, communityRepository.findByRewriteUrlParameter(communityUrl));
        emailGenerate(user, facebookEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));
        user = userRepository.findOne(facebookEmail, communityUrl);
        String userToken = Utils.createTimestampToken(user.getToken(), timestamp);
        mockMvc.perform(
                post("/" + communityUrl + "/5.5/GET_CHART.json")
                        .param("USER_NAME", facebookEmail)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.response.data[0].user").exists());
    }


    @Test
    public void testFacebookApplyAfterEmailRegistration() throws Exception {
        facebookService.setTemplateCustomizer(getTemplateCustomizer(facebookUserId, facebookEmail, locationFromFacebook));
        User user = userRepository.save(UserFactory.userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withDeviceUID(deviceUID).withUserGroup(userGroupRepository.findOne(9)));
        ResultActions resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        String userToken = getUserToken(resultActions, timestamp);
        emailGenerate(user, facebookEmail);
        ActivationEmail activationEmail = Iterables.getFirst(activationEmailRepository.findAll(), null);
        applyInitPromoByEmail(activationEmail, timestamp, getUserToken(resultActions, timestamp));

        resultActions = signUpDevice(deviceUID, deviceType, apiVersion, communityUrl);
        mockMvc.perform(
                buildApplyFacebookPromoRequest(resultActions, deviceUID, deviceType, apiVersion, communityUrl, timestamp, facebookUserId, facebookToken, true)
        ).andExpect(status().isOk());

        mockMvc.perform(
                post("/" + communityUrl + "/5.5/GET_CHART.json")
                        .param("USER_NAME", facebookEmail)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.response.data[0].user").exists());

    }

}
