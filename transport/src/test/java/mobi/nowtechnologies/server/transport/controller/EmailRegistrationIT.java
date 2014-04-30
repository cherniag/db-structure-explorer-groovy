package mobi.nowtechnologies.server.transport.controller;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.service.MailTemplateProcessor;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.transport.service.TimestampExtFileNameFilter;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static mobi.nowtechnologies.server.persistence.domain.Community.HL_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.DeviceType.ANDROID;
import static mobi.nowtechnologies.server.persistence.domain.Promotion.ADD_FREE_WEEKS_PROMOTION;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType.NEWS;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.PENDING_ACTIVATION;
import static mobi.nowtechnologies.server.shared.enums.ChgPosition.DOWN;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.UserStatus.LIMITED;
import static mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class EmailRegistrationIT extends AbstractControllerTestIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("serviceMessageSource")
    private CommunityResourceBundleMessageSource messageSource;

    @Autowired
    private ActivationEmailRepository activationEmailRepository;

    @Value("${sms.temporaryFolder}")
    private File temporaryFolder;

    private static final String DEVICE_UID_1 = "htc1";
    private static final String DEVICE_UID_2 = "htc2";
    private static final String EMAIL_1 = "a@gmail.com";
    private static final String EMAIL_2 = "b@gmail.com";
    private static final String DISABLED = "_disabled_at_";

    @Test
    public void testNewUser() throws Exception {
        registerFirstUserOnDevice();
    }

    @Test
    public void testAnotherEmailOnSameDevice() throws Exception {
        User user = registerFirstUserOnDevice();

        registerSecondUserOnDevice(user);
    }

    @Test
    public void testSameEmailOnDifferentDevice() throws Exception {
        User user = registerFirstUserOnDevice();

        registerFirstUserOnAnotherDevice(user);
    }

    @Test
    public void testNewUserWrongEmailActivation() throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_1);

        User user = checkUserAfterSignupDevice(DEVICE_UID_1);

        long time = System.currentTimeMillis();

        MvcResult mvcResult = emailGenerate(user, EMAIL_1);
        ActivationEmail activationEmail = checkEmail((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0], time, EMAIL_1, user.getDeviceType().getName());

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromoError(activationEmail, timestamp, userToken);

        user = userRepository.findOne(EMAIL_1, HL_COMMUNITY_REWRITE_URL);
        assertNull(user);
    }

    @Test
    public void testSkipRegistration() throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_1);
        User user = checkUserAfterSignupDevice(DEVICE_UID_1);

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        checkGetChart(user, timestamp, userToken);

        checkGetNews(user, timestamp, userToken);
    }

    @Test
    public void testRegisterAfterEmailSent() throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_1);

        User user = checkUserAfterSignupDevice(DEVICE_UID_1);

        long time = System.currentTimeMillis();

        MvcResult mvcResult = emailGenerate(user, EMAIL_1);
        ActivationEmail activationEmail = checkEmail((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0], time, EMAIL_1, user.getDeviceType().getName());

        user = userRepository.findOne(DEVICE_UID_1, user.getCommunityRewriteUrl());

        assertEquals(PENDING_ACTIVATION, user.getActivationStatus());

        signUpDevice(DEVICE_UID_1);

        user = userRepository.findOne(DEVICE_UID_1, user.getCommunityRewriteUrl());

        assertEquals(ActivationStatus.REGISTERED, user.getActivationStatus());

    }

    private void registerFirstUserOnAnotherDevice(User user) throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_2);
        User userOnAnotherDevice = checkUserAfterSignupDevice(DEVICE_UID_2);
        user = userRepository.findOne(user.getId());
        checkActivatedUser(user, EMAIL_1);

        long time = System.currentTimeMillis();
        MvcResult mvcResult = emailGenerate(userOnAnotherDevice, EMAIL_1);
        ActivationEmail activationEmail = checkEmail(((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0]), time, EMAIL_1, user.getDeviceType().getName());

        userOnAnotherDevice = userService.findByName(userOnAnotherDevice.getUserName());
        assertEquals(PENDING_ACTIVATION, userOnAnotherDevice.getActivationStatus());

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromo(activationEmail, timestamp, userToken);
        User secondUser = userRepository.findOne(EMAIL_1, HL_COMMUNITY_REWRITE_URL);
        checkActivatedUser(secondUser, EMAIL_1);
        user = userRepository.findOne(user.getId());
        assertEquals(DEVICE_UID_2, user.getDeviceUID());

        assertNull(userRepository.findOne(DEVICE_UID_1, HL_COMMUNITY_REWRITE_URL));

        userToken = Utils.createTimestampToken(user.getToken(), timestamp);

        checkGetChart(user, timestamp, userToken);
        checkGetNews(user, timestamp, userToken);
    }

    private void registerSecondUserOnDevice(User user) throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_1);

        User registeredUser = checkUserAfterSignupDevice(DEVICE_UID_1);

        User activatedUser = userRepository.findOne(user.getUserName(), HL_COMMUNITY_REWRITE_URL);
        assertTrue(activatedUser.getDeviceUID().contains(DISABLED));

        long time = System.currentTimeMillis();

        MvcResult mvcResult = emailGenerate(registeredUser, EMAIL_2);

        registeredUser = userService.findByName(registeredUser.getUserName());
        assertEquals(PENDING_ACTIVATION, registeredUser.getActivationStatus());

        ActivationEmail activationEmail = checkEmail(((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0]), time, EMAIL_2, activatedUser.getDeviceType().getName());

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromo(activationEmail, timestamp, userToken);

        registeredUser = userRepository.findOne(EMAIL_2, HL_COMMUNITY_REWRITE_URL);
        checkActivatedUser(registeredUser, EMAIL_2);
        assertNull(userRepository.findOne(DEVICE_UID_1, HL_COMMUNITY_REWRITE_URL));
        User oldUser = userRepository.findOne(user.getUserName(), HL_COMMUNITY_REWRITE_URL);
        assertEquals(SUBSCRIBED.name(), oldUser.getStatus().getName());
        assertTrue(oldUser.getDeviceUID().contains(DISABLED));

        userToken = Utils.createTimestampToken(registeredUser.getToken(), timestamp);

        checkGetChart(registeredUser, timestamp, userToken);
        checkGetNews(registeredUser, timestamp, userToken);
    }

    private User registerFirstUserOnDevice() throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_1);

        User user = checkUserAfterSignupDevice(DEVICE_UID_1);

        long time = System.currentTimeMillis();

        MvcResult mvcResult = emailGenerate(user, EMAIL_1);
        ActivationEmail activationEmail = checkEmail((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0], time, EMAIL_1, user.getDeviceType().getName());

        user = userService.findByName(user.getUserName());
        assertEquals(PENDING_ACTIVATION, user.getActivationStatus());

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromo(activationEmail, timestamp, userToken);

        user = userRepository.findOne(EMAIL_1, HL_COMMUNITY_REWRITE_URL);
        checkActivatedUser(user, EMAIL_1);

        userToken = Utils.createTimestampToken(user.getToken(), timestamp);

        checkGetChart(user, timestamp, userToken);
        checkGetNews(user, timestamp, userToken);
        return user;
    }

    private ActivationEmail checkEmail(Long activationEmailId, long time, String email, String deviceType) throws IOException {
        String community = "hl_uk";

        String from = messageSource.getMessage(community, "activation.email.from", null, null, null);
        String subject = messageSource.getMessage(community, "activation.email.subject", null, null, null);
        String body = messageSource.getMessage(community, deviceType + ".activation.email.body", null, null, null);
        Map<String, String> params = new HashMap<String, String>();
        ActivationEmail activationEmail = activationEmailRepository.findOne(activationEmailId);
        params.put(ActivationEmail.ID, activationEmail.getId().toString());
        params.put(ActivationEmail.TOKEN, activationEmail.getToken());

        File file = temporaryFolder.listFiles(new TimestampExtFileNameFilter(time))[0];
        List<String> text = Files.readLines(file, Charsets.UTF_8);
        assertTrue(text.contains("from: " + from));
        assertTrue(text.contains("to: " + email));
        assertTrue(text.contains("subject: " + MailTemplateProcessor.processTemplateString(subject, params)));
        assertTrue(text.contains("body: " + MailTemplateProcessor.processTemplateString(body, params)));

        assertFalse(activationEmail.isActivated());
        return activationEmail;
    }

    private String signUpDevice(String deviceUID) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/hl_uk/5.3/SIGN_UP_DEVICE")
                .param("DEVICE_TYPE", ANDROID)
                .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andExpect(xpath("/response/user/status").string(LIMITED.name()))
                .andExpect(xpath("/response/user/deviceType").string(ANDROID)).andReturn();
        return getUserToken(mvcResult);
    }

    private String getUserToken(MvcResult mvcResult) {
        AccountCheckDto accCheckDto = (AccountCheckDto) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0];
        return accCheckDto.userToken;
    }

    private void applyInitPromo(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/hl_uk/4.0/SIGN_IN_EMAIL")
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("EMAIL_ID", activationEmail.getId().toString())
                .param("EMAIL", activationEmail.getEmail())
                .param("TOKEN", activationEmail.getToken())
                .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isOk());
    }

    private MvcResult emailGenerate(User user, String email) throws Exception {
        MvcResult mvcResult;
        mvcResult = mockMvc.perform(post("/hl_uk/4.0/EMAIL_GENERATE.json")
                .param("EMAIL", email)
                .param("USER_NAME", user.getUserName())
                .param("DEVICE_UID", user.getDeviceUID())).andExpect(status().isOk()).andReturn();
        return mvcResult;
    }

    private User checkUserAfterSignupDevice(String deviceUID) {
        User user = userRepository.findOne(deviceUID, HL_COMMUNITY_REWRITE_URL);
        assertEquals(LIMITED.name(), user.getStatus().getName());
        assertEquals(deviceUID, user.getUserName());
        assertEquals(HL_COMMUNITY_REWRITE_URL, user.getUserGroup().getCommunity().getRewriteUrlParameter());
        return user;
    }

    private void applyInitPromoError(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/hl_uk/4.0/SIGN_IN_EMAIL")
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("EMAIL_ID", activationEmail.getId().toString())
                .param("EMAIL", EMAIL_2)
                .param("TOKEN", "ttt")
                .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isInternalServerError());
    }

    private void checkActivatedUser(User user, String firstUserEmail) {
        assertEquals(SUBSCRIBED.name(), user.getStatus().getName());
        assertEquals(firstUserEmail, user.getUserName());
        assertEquals(HL_COMMUNITY_REWRITE_URL, user.getUserGroup().getCommunity().getRewriteUrlParameter());
    }

    private void checkGetNews(User user, String timestamp, String userToken) throws Exception {
        mockMvc.perform(
                post("/hl_uk/5.5/GET_NEWS.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", user.getDeviceUID())
        ).andExpect(status().isOk()).andExpect(jsonPath("$.response..items").exists()).
                andExpect(jsonPath("$.response..news").exists()).
                andExpect(jsonPath("$.response..user").exists());
    }

    private void checkGetChart(User user, String timestamp, String userToken) throws Exception {
        mockMvc.perform(
                post("/hl_uk/5.5/GET_CHART.json")
                        .param("USER_NAME", user.getUserName())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", user.getDeviceUID())
        ).andExpect(status().isOk()).andExpect(jsonPath("response.data[1].chart.tracks[0].media").value("US-UM7-11-00061"));
    }


    @After
    public void tearDown() throws IOException {
        if (temporaryFolder.exists()) {
            FileUtils.deleteDirectory(temporaryFolder);
        }
    }
}
