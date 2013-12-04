package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.dao.CommunityDao;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserGroupDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.web.server.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.*;

import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
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
@MockWebApplication(name = "transport.AccCheckController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class AccCheckControllerTestIT {

    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("service.UserService")
    private UserService userService;

    @Resource
    private UserRepository userRepository;

    @Autowired
    private ChartRepository chartRepository;
    @Autowired
    private ChartDetailRepository chartDetailRepository;


    @Before
    public void setUp() {
        mockMvc = webApplicationContextSetup((WebApplicationContext) applicationContext).build();
    }

    @Test
    public void testAccountCheckForO2Client_WithSelectedCharts_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "3.9";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        List<Chart> charts = new ArrayList<Chart>();
        Chart chart = chartRepository.findOne(5);
        charts.add(chart);
        User user = userService.findByNameAndCommunity(userName, communityName);
        user.setSelectedCharts(charts);
        userService.updateUser(user);
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)).
                andExpect(status().isOk()).
                andExpect(xpath("/response/user/playlist/id").number(5d)).
                andExpect(xpath("/response/user/playlist/type").string("BASIC_CHART"));
    }

    @Test
    public void testAccountCheckForO2Client_WithLockedTracks_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "3.9";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ChartDetail chartDetail = chartDetailRepository.findOne(22);
        chartDetail.setLocked(true);
        chartDetailRepository.save(chartDetail);
        User user = userService.findByNameAndCommunity(userName, communityName);
        UserStatus userStatus = new UserStatus();
        userStatus.setI((byte) 10);
        user.setStatus(userStatus);
        userService.updateUser(user);

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).
                andDo(print()).
                andExpect(xpath("/response/user/lockedTrack[1]/media").string("US-UM7-11-00061"));

    }

    @Test
    public void testAccountCheckForO2Client_WithIOS7DeviceUID_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "3.8";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk()).
                andExpect(xpath("/response/user/deviceUID").string("b88106713409e92622461a876abcd74b"));
    }

    @Test
    public void testAccountCheckForO2Client_greaterOrEquals3d9_IOS_WithNotCorrectDeviceUID_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "3.9";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String deviceUID = "fail";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk()).andExpect(xpath("/response/user/deviceUID").booleanValue(false));

        apiVersion = "4.0";

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk()).
                andExpect(xpath("/response/user/deviceUID").string("fail"));
    }

    @Test
    public void testAccountCheckForFVClient_HasAllDetails_Success() throws Exception {
        String userName = "+642102247311";
        String apiVersion = "5.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userService.findByNameAndCommunity(userName, communityName);
        user.setProvider(NON_VF);
        userService.updateUser(user);
        mockMvc.perform(
                post("/somekey/" + communityUrl + "/" + apiVersion + "/ACC_CHECK")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk()).
                andExpect(xpath("/response/user/hasAllDetails").booleanValue(true)).
                andExpect(xpath("/response/user/canGetVideo").booleanValue(false));

    }


    @Test
    public void testAccountCheckForITunesClientWhichDoesntHaveLockedTracks() throws Exception {
        final String userName = "+447111111118";

        //given
        User entity = UserFactory.createUser()
                .withSegment(SegmentType.CONSUMER)
                .withContract(Contract.PAYM)
                .withProvider(ProviderType.O2)
                .withNextSubPayment(new Date(1000L * 1988143200))
                .withUserName(userName)
                .withDeviceUID("b88106713409e92822461a876abcd74c")
                .withDeviceUID("d")
                .withMobile("+447111111112")
                .withUserGroup(UserGroupDao.getUSER_GROUP_MAP_COMMUNITY_ID_AS_KEY().get(CommunityDao.getCommunity("o2").getId()));
        entity.setToken("f701af8d07e5c95d3f5cf3bd9a62344d");
        entity.setStatus(UserStatusDao.getUserStatusMapIdAsKey().get((byte)10));
        entity.setDevice("");
        entity.setDeviceType(DeviceTypeDao.getDeviceTypeMapIdAsKeyAndDeviceTypeValue().get((byte)5));
        entity.setDeviceString("IOS");
        entity.setLastDeviceLogin(1893448800);
        entity.setLastWebLogin(1893448800);
        entity.setTempToken("f701af8d07e5c95d3f5cf3bd9a62344d");
        entity.setOperator(1);
        entity.setLastSubscribedPaymentSystem("iTunesSubscription");

        userRepository.save(entity);

        String apiVersion = "3.8";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK")
                        .param("COMMUNITY_NAME", communityName)
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUID)
        ).andExpect(status().isOk()).andDo(print()).
                andExpect(xpath("/response/user/lockedTrack/media").nodeCount(0));
    }





}