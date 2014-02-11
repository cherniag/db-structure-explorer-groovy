package mobi.nowtechnologies.server.transport.controller;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ActivationEmailRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.MailTemplateProcessor;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.transport.service.TimestampExtFileNameFileter;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.server.setup.MockMvcBuilders.webApplicationContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:transport-servlet-test.xml",
        "classpath:task-processors.xml",
        "classpath:META-INF/service-test.xml",
        "classpath:META-INF/service-mocked-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/dao-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/shared.xml"}, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.AccCheckController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class EmailRegistrationIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("serviceMessageSource")
    private CommunityResourceBundleMessageSource messageSource;

    @Autowired
    private ActivationEmailRepository activationEmailRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private WebApplicationContext applicationContext;

    private MockMvc mockMvc;

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
                .getObject()[0], time, EMAIL_1);

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromoError(activationEmail, timestamp, userToken);

        user = userRepository.findOne(EMAIL_1, Community.O2_COMMUNITY_REWRITE_URL);
        assertNull(user);
    }

    private void registerFirstUserOnAnotherDevice(User user) throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_2);
        User userOnAnotherDevice = checkUserAfterSignupDevice(DEVICE_UID_2);
        user = userRepository.findOne(user.getId());
        checkActivatedUser(user, EMAIL_1);

        long time = System.currentTimeMillis();
        MvcResult mvcResult = emailGenerate(userOnAnotherDevice, EMAIL_1);
        ActivationEmail activationEmail = checkEmail(((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0]), time, EMAIL_1);

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromo(activationEmail, timestamp, userToken);
        User secondUser = userRepository.findOne(EMAIL_1, Community.O2_COMMUNITY_REWRITE_URL);
        checkActivatedUser(secondUser, EMAIL_1);
        assertEquals(DEVICE_UID_2, user.getDeviceUID());

        assertNull(userRepository.findOne(DEVICE_UID_1, Community.O2_COMMUNITY_REWRITE_URL));
    }

    private void registerSecondUserOnDevice(User user) throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_1);

        checkUserAfterSignupDevice(DEVICE_UID_1);
        // it's needed to update user
        entityManager.clear();

        User activatedUser = userRepository.findOne(user.getUserName(), Community.O2_COMMUNITY_REWRITE_URL);
        assertTrue(activatedUser.getDeviceUID().contains(DISABLED));

        long time = System.currentTimeMillis();

        MvcResult mvcResult = emailGenerate(user, EMAIL_2);

        ActivationEmail activationEmail = checkEmail(((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0]), time, EMAIL_2);

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromo(activationEmail, timestamp, userToken);

        User secondUser = userRepository.findOne(EMAIL_2, Community.O2_COMMUNITY_REWRITE_URL);
        checkActivatedUser(secondUser, EMAIL_2);
        assertNull(userRepository.findOne(DEVICE_UID_1, Community.O2_COMMUNITY_REWRITE_URL));
        User oldUser = userRepository.findOne(user.getUserName(), Community.O2_COMMUNITY_REWRITE_URL);
        assertEquals(UserStatus.SUBSCRIBED.name(), oldUser.getStatus().getName());
        assertTrue(oldUser.getDeviceUID().contains(DISABLED));
    }

    private User registerFirstUserOnDevice() throws Exception {
        String storedToken = signUpDevice(DEVICE_UID_1);

        User user = checkUserAfterSignupDevice(DEVICE_UID_1);

        long time = System.currentTimeMillis();

        MvcResult mvcResult = emailGenerate(user, EMAIL_1);
        ActivationEmail activationEmail = checkEmail((Long) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0], time, EMAIL_1);

        String timestamp = "2011_12_26_07_04_23";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        applyInitPromo(activationEmail, timestamp, userToken);

        user = userRepository.findOne(EMAIL_1, Community.O2_COMMUNITY_REWRITE_URL);
        checkActivatedUser(user, EMAIL_1);
        return user;
    }

    private ActivationEmail checkEmail(Long activationEmailId, long time, String email) throws IOException {
        String community = "o2";

        String from = messageSource.getMessage(community, "activation.email.from", null, null, null);
        String subject = messageSource.getMessage(community, "activation.email.subject", null, null, null);
        String body = messageSource.getMessage(community, "activation.email.body", null, null, null);
        Map<String, String> params = new HashMap<String, String>();
        ActivationEmail activationEmail = activationEmailRepository.findOne(activationEmailId);
        params.put(ActivationEmail.ID, activationEmail.getId().toString());
        params.put(ActivationEmail.TOKEN, activationEmail.getToken());

        File file = temporaryFolder.listFiles(new TimestampExtFileNameFileter(time))[0];
        List<String> text = Files.readLines(file, Charsets.UTF_8);
        assertTrue(text.contains("from: " + from));
        assertTrue(text.contains("to: " + email));
        assertTrue(text.contains("subject: " + MailTemplateProcessor.processTemplateString(subject, params)));
        assertTrue(text.contains("body: " + MailTemplateProcessor.processTemplateString(body, params)));

        assertFalse(activationEmail.isActivated());
        return activationEmail;
    }

    private String signUpDevice(String deviceUID) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/o2/6.0/SIGN_UP_DEVICE")
                .param("DEVICE_TYPE", DeviceType.ANDROID)
                .param("DEVICE_UID", deviceUID))
                .andExpect(status().isOk())
                .andExpect(xpath("/response/user/status").string(UserStatus.LIMITED.name()))
                .andExpect(xpath("/response/user/deviceType").string(DeviceType.ANDROID)).andReturn();
        return getUserToken(mvcResult);
    }

    private String getUserToken(MvcResult mvcResult) {
        AccountCheckDto accCheckDto = (AccountCheckDto) ((Response) mvcResult.getModelAndView().getModel().get("response"))
                .getObject()[0];
        return accCheckDto.userToken;
    }

    private void applyInitPromo(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/o2/4.0/EMAIL_CONFIRM_APPLY_INIT_PROMO")
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("EMAIL_ID", activationEmail.getId().toString())
                .param("EMAIL", activationEmail.getEmail())
                .param("TOKEN", activationEmail.getToken())
                .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isOk());
    }

    private MvcResult emailGenerate(User user, String email) throws Exception {
        MvcResult mvcResult;
        mvcResult = mockMvc.perform(post("/o2/4.0/EMAIL_GENERATE.json")
                .param("EMAIL", email)
                .param("USER_NAME", user.getUserName())
                .param("DEVICE_UID", user.getDeviceUID())).andExpect(status().isOk()).andReturn();
        return mvcResult;
    }

    private User checkUserAfterSignupDevice(String deviceUID) {
        User user = userRepository.findOne(deviceUID, Community.O2_COMMUNITY_REWRITE_URL);
        assertEquals(UserStatus.LIMITED.name(), user.getStatus().getName());
        assertEquals(deviceUID, user.getUserName());
        assertEquals(Community.O2_COMMUNITY_REWRITE_URL, user.getUserGroup().getCommunity().getRewriteUrlParameter());
        return user;
    }

    private void applyInitPromoError(ActivationEmail activationEmail, String timestamp, String userToken) throws Exception {
        mockMvc.perform(post("/o2/4.0/EMAIL_CONFIRM_APPLY_INIT_PROMO")
                .param("USER_TOKEN", userToken)
                .param("TIMESTAMP", timestamp)
                .param("EMAIL_ID", activationEmail.getId().toString())
                .param("EMAIL", EMAIL_2)
                .param("TOKEN", "ttt")
                .param("DEVICE_UID", activationEmail.getDeviceUID())).andExpect(status().isInternalServerError());
    }

    private void checkActivatedUser(User user, String firstUserEmail) {
        assertEquals(UserStatus.SUBSCRIBED.name(), user.getStatus().getName());
        assertEquals(firstUserEmail, user.getUserName());
        assertEquals(Community.O2_COMMUNITY_REWRITE_URL, user.getUserGroup().getCommunity().getRewriteUrlParameter());
    }

    @Before
    public void setUp() {
        mockMvc = webApplicationContextSetup(applicationContext).build();
    }

    @After
    public void tearDown() throws IOException {
        if (temporaryFolder.exists()) {
            FileUtils.deleteDirectory(temporaryFolder);
        }
    }
}
