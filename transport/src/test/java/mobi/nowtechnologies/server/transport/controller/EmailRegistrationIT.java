package mobi.nowtechnologies.server.transport.controller;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.MailTemplateProcessor;
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
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
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
    private ApplicationContext applicationContext;

    private MockMvc mockMvc;

    @Value("${sms.temporaryFolder}")
    private File temporaryFolder;

    @Test
    public void testNewUser() throws Exception {
        mockMvc.perform(post("/o2/4.0/SIGN_UP_DEVICE")
                .param("DEVICE_TYPE", DeviceType.ANDROID)
                .param("COMMUNITY_NAME", Community.O2_COMMUNITY_REWRITE_URL)
                .param("DEVICE_UID", "HTC1")
                .param("API_VERSION", "4.0")
                .param("APP_VERSION", "4.3"))
                .andExpect(status().isOk())
                .andExpect(xpath("/response/user/status").string(UserStatus.LIMITED.name()))
                .andExpect(xpath("/response/user/deviceType").string(DeviceType.ANDROID));

        User user = userRepository.findOne("htc1", Community.O2_COMMUNITY_REWRITE_URL);
        assertEquals(UserStatus.LIMITED.name(), user.getStatus().getName());
        assertEquals("htc1", user.getUserName());
        assertEquals(Community.O2_COMMUNITY_REWRITE_URL, user.getUserGroup().getCommunity().getRewriteUrlParameter());
        assertNotNull(user);

        long time = System.currentTimeMillis();

        String email = "a@gmail.com";
        mockMvc.perform(post("/o2/4.0/EMAIL_GENERATE")
                .param("EMAIL", email)
                .param("USER_NAME", "htc1")
                .param("DEVICE_UID", "HTC1")).andExpect(status().isOk());

        String community = "o2";

        String from = messageSource.getMessage(community, "activation.email.from", null, null, null);
        String subject = messageSource.getMessage(community, "activation.email.subject", null, null, null);
        String body = messageSource.getMessage(community, "activation.email.body", null, null, null);
        Map<String, String> params = new HashMap<String, String>();

        File file = temporaryFolder.listFiles(new TimestampExtFileNameFileter(time))[0];
        List<String> text = Files.readLines(file, Charsets.UTF_8);
        assertTrue(text.contains("from: " + from));
        assertTrue(text.contains("to: " + email));
        assertTrue(text.contains("subject: " + MailTemplateProcessor.processTemplateString(subject, params)));
        assertTrue(text.contains("body: " + MailTemplateProcessor.processTemplateString(body, params)));
    }

    @Before
    public void setUp() {
        mockMvc = webApplicationContextSetup((WebApplicationContext) applicationContext).build();
    }

    @After
    public void tearDown() throws IOException {
        if (temporaryFolder.exists()) {
            FileUtils.deleteDirectory(temporaryFolder);
        }
    }
}
