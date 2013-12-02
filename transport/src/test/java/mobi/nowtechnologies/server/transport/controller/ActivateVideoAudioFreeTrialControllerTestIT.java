package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.server.setup.MockMvcBuilders.webApplicationContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:transport-servlet-test.xml",
        "classpath:META-INF/service-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/dao-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/shared.xml"}, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.ActivateVideoAudioFreeTrialController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
//TODO: Need some changes in H2 connection stuff.
public class ActivateVideoAudioFreeTrialControllerTestIT {

    @Autowired
    private ApplicationContext applicationContext;


    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webApplicationContextSetup((WebApplicationContext) applicationContext).build();
    }


    @Test
    public void testActivateVideoAudioFreeTrial_Success() throws Exception {
        String userName = "+447111111114";
        String appVersion = "4.0";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String url = "/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.set("Accept", "application/xml");
        mockMvc.perform(post(url).headers(httpHeaders).param("APP_VERSION", appVersion).param("USER_NAME", userName).
                param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUid)).andDo(print()).
                andExpect(status().isUnauthorized());
    }


    @Test
    public void testActivateVideoAudioFreeTrial_Success1() throws Exception {
        String userName = "+64279000410";
        String appVersion = "4.0";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd741";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String url = "/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
        httpHeaders.set("Accept", "application/xml");
        mockMvc.perform(post(url).headers(httpHeaders).param("APP_VERSION", appVersion).param("USER_NAME", userName).
                param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUid)).andDo(print()).
                andExpect(status().isOk()).andExpect(xpath("/response/user/canPlayVideo").booleanValue(true));
    }

}
