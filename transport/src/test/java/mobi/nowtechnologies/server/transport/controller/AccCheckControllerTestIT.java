package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.Chart;
import mobi.nowtechnologies.server.persistence.domain.ChartDetail;
import mobi.nowtechnologies.server.persistence.domain.ReactivationUserInfo;
import mobi.nowtechnologies.server.persistence.domain.UrbanAirshipToken;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.ChartDetailRepository;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.ReactivationUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UrbanAirshipTokenRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.Utils.createTimestampToken;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.NON_VF;
import static mobi.nowtechnologies.server.transport.controller.core.CommonController.OAUTH_REALM_USERS;
import static mobi.nowtechnologies.server.transport.controller.core.CommonController.WWW_AUTHENTICATE_HEADER;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.hamcrest.CustomMatcher;

@Transactional
public class AccCheckControllerTestIT extends AbstractControllerTestIT {
    @Resource
    private ChartRepository chartRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private ChartDetailRepository chartDetailRepository;
    @Resource(name = "communityRepository")
    private CommunityRepository communityRepository;
    @Resource
    private UrbanAirshipTokenRepository urbanAirshipTokenRepository;
    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;
    @Resource
    private UserGroupRepository userGroupRepository;
    @Value("${context.request.expires.minutes.time.range}")
    private int[] timeRange;

    @Test
    public void testAccCheck_LatestVersion() throws Exception {
        String userName = "+447111111110";
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);
        setUserSelectedCharts(user, 5);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityUrl)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.response.data[0].user.userName").value("+447111111110"))
               .andExpect(jsonPath("$.response.data[0].user.status").value("SUBSCRIBED"))
               .andExpect(jsonPath("$.response.data[0].user.deviceType").value("IOS"))
               .andExpect(jsonPath("$.response.data[0].user.deviceUID").value("11111111111111111111111111111111111"))
               .andExpect(jsonPath("$.response.data[0].user.phoneNumber").value("+447111111110"))
               .andExpect(jsonPath("$.response.data[0].user.userToken").value("f701af8d07e5c95d3f5cf3bd9a62344d"))
               .andExpect(jsonPath("$.response.data[0].user.nextSubPaymentSeconds").value(1988143200))
               .andExpect(jsonPath("$.response.data[0].user.activation").value("ACTIVATED"))
               .andExpect(jsonPath("$.response.data[0].user.provider").value("o2"))
               .andExpect(jsonPath("$.response.data[0].user.contract").value("PAYM"))
               .andExpect(jsonPath("$.response.data[0].user.segment").value("CONSUMER"))
               .andExpect(jsonPath("$.response.data[0].user.tariff").value("_3G"))
               .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true))
               .andExpect(jsonPath("$.response.data[0].user.oneTimePayment").value(false))
               .andExpect(jsonPath("$.response.data[0].user.playlists[0].id").value(5));
    }

    @Test
    public void testAccCheck_OnetimePayment() throws Exception {
        String apiVersion = "6.11";

        String communityUrl = "hl_uk";
        String userName = "zam1@ukr.net";
        String timestamp = "" + new Date().getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);
        long purchase_date_ms = DateTimeUtils.moveDate(new Date(), DateTimeUtils.GMT_TIME_ZONE_ID, -1, DurationUnit.DAYS).getTime();
        String appStoreProductId = "com.musicqubed.ios.hl-uk.onetime.0";

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityUrl)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp)
                                                                                       .param("TRANSACTION_RECEIPT",
                                                                                              String.format("onetime:200:0:%s:1000000137405769:%s", appStoreProductId, purchase_date_ms)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.response.data[0].user.deviceType").value("IOS"))
               .andExpect(jsonPath("$.response.data[0].user.oneTimePayment").value(true));
    }

    @Test
    public void testAccCheck_CreateITunesPaymentDetails() throws Exception {
        String apiVersion = "6.12";

        String communityUrl = "hl_uk";
        String userName = "zam2@ukr.net";
        Date now = new Date();
        String timestamp = "" + now.getTime();
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = createTimestampToken(storedToken, timestamp);
        long purchase_date_ms = DateTimeUtils.moveDate(now, DateTimeUtils.GMT_TIME_ZONE_ID, -1, DurationUnit.DAYS).getTime();
        String appStoreProductId = "com.musicqubed.ios.hl-uk.onetime.0";

        long expectedExpiresTime = new Date().getTime() + TimeUnit.MINUTES.toMillis(timeRange[0]);
        String roundedExpectedExpiresTime = String.valueOf(expectedExpiresTime).substring(0, 9);
        String appStoreReceipt = String.format("onetime:200:0:%s:1000000137405769:%s", appStoreProductId, purchase_date_ms);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityUrl)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp)
                                                                                       .param("TRANSACTION_RECEIPT", appStoreReceipt))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.response.data[0].user.deviceType").value("IOS"))
               .andExpect(jsonPath("$.response.data[0].user.paymentType").value("ITUNES_SUBSCRIPTION"))
               .andExpect(jsonPath("$.response.data[0].user.lastPaymentStatus").value("NONE"))
               .andExpect(header().string("Expires", getStartsWithMatcher(roundedExpectedExpiresTime)));

        User found = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);
        ITunesPaymentDetails currentPaymentDetails = found.getCurrentPaymentDetails();
        assertTrue(currentPaymentDetails.isActivated());
        assertEquals(PaymentDetails.ITUNES_SUBSCRIPTION, currentPaymentDetails.getPaymentType());
        assertEquals(PaymentDetailsStatus.NONE, currentPaymentDetails.getLastPaymentStatus());
        assertEquals(appStoreReceipt, currentPaymentDetails.getAppStoreReceipt());
        assertEquals(appStoreProductId, currentPaymentDetails.getPaymentPolicy().getAppStoreProductId());
    }

    @Test
    public void testAccCheckUrbanAirshipTokenIsStored() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String urbanAirshipToken = "test-urban-airship-token";

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);
        setUserSelectedCharts(user, 5);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityUrl)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp)
                                                                                       .param("UA_TOKEN", urbanAirshipToken)).andExpect(status().isOk());

        UrbanAirshipToken tokenFromDB = urbanAirshipTokenRepository.findDataByUserId(user.getId());

        assertEquals(tokenFromDB.getToken(), urbanAirshipToken);
    }

    @Test
    public void testAccountCheckForO2Client_WithSelectedCharts_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);
        setUserSelectedCharts(user, 5);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).
                   andExpect(xpath("/response/user/playlist/id").number(5d)).
                   andExpect(xpath("/response/user/playlist/type").string("BASIC_CHART"));
    }


    @Test
    public void testAccountCheckWhenNeedReactivation() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);

        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityName)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp))
               .
                   andExpect(status().isForbidden())
               .andDo(print())
               .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(604))
               .andExpect(jsonPath("$.response.data[0].errorMessage.displayMessage").value("Reactivation required"));
        reactivationUserInfo = reactivationUserInfoRepository.findByUser(user);
        reactivationUserInfo.setReactivationRequest(false);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk());
    }


    @Test
    public void testAccountCheckWhenNeedReactivationForOldVersion() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "5.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);

        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityName)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void testAccountCheckForO2Client_WithLockedTracks_Success() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ChartDetail chartDetail = chartDetailRepository.findOne(22);
        chartDetail.setLocked(true);
        chartDetailRepository.save(chartDetail);
        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        UserStatus userStatus = new UserStatus();
        userStatus.setI((byte) 10);
        user.setStatus(userStatus);
        userService.updateUser(user);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)).andExpect(status().isOk()).
                   andDo(print()).
                   andExpect(xpath("/response/user/lockedTrack[1]/media").string("US-UM7-11-00061_2"));
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

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)
                                                                                  .param("DEVICE_UID", deviceUID)).andExpect(status().isOk()).
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

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)
                                                                                  .param("DEVICE_UID", deviceUID))
               .andExpect(status().isOk())
               .andExpect(xpath("/response/user/deviceUID").booleanValue(false));

        apiVersion = "4.0";

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)
                                                                                  .param("DEVICE_UID", deviceUID)).andExpect(status().isOk()).
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

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        user.setProvider(NON_VF);
        userService.updateUser(user);

        mockMvc.perform(post("/somekey/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("USER_NAME", userName)
                                                                                          .param("USER_TOKEN", userToken)
                                                                                          .param("TIMESTAMP", timestamp)
                                                                                          .param("DEVICE_UID", deviceUID)).andExpect(status().isOk()).
                   andExpect(xpath("/response/user/hasAllDetails").booleanValue(true)).
                   andExpect(xpath("/response/user/canGetVideo").booleanValue(false));
    }

    @Test
    public void testAccountCheckForFVClient_HasAllDetails_JsonFormatAndAdditionalUIDAndVersionMore50_Success() throws Exception {
        String userName = "+642102247311";
        String apiVersion = "6.0";
        String communityName = "vf_nz";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        user.setProvider(NON_VF);
        userService.updateUser(user);

        mockMvc.perform(post("/AUID/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("USER_NAME", userName)
                                                                                            .param("USER_TOKEN", userToken)
                                                                                            .param("TIMESTAMP", timestamp)
                                                                                            .param("DEVICE_UID", deviceUID)).andExpect(status().isOk());
    }

    @Test
    public void testAccountCheck_404_Failure() throws Exception {
        String userName = "+642102247311";
        String apiVersion = "3.5";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/AUID/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("USER_NAME", userName)
                                                                                            .param("USER_TOKEN", userToken)
                                                                                            .param("TIMESTAMP", timestamp)
                                                                                            .param("DEVICE_UID", deviceUID)).andExpect(status().isNotFound());
    }

    @Test
    public void testAccountCheckv4d0_400_Failure() throws Exception {
        String apiVersion = "4.0";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/AUID/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isInternalServerError());
    }

    @Test
    public void testAccountCheckv5d3_400_Failure() throws Exception {
        String apiVersion = "5.3";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/AUID/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("USER_TOKEN", userToken).param("TIMESTAMP", timestamp).param("DEVICE_UID", deviceUID))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void testAccountCheckV4d0_401_Failure() throws Exception {
        String userName = "+6421xxxxxxxx";
        String apiVersion = "4.0";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/AUID/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("USER_NAME", userName)
                                                                                            .param("USER_TOKEN", userToken)
                                                                                            .param("TIMESTAMP", timestamp)
                                                                                            .param("DEVICE_UID", deviceUID))
               .andExpect(status().isUnauthorized())
               .andExpect(header().string(WWW_AUTHENTICATE_HEADER, OAUTH_REALM_USERS))
               .andExpect(jsonPath("$.response.data[0].errorMessage.displayMessage").value("Bad user credentials"));
    }

    @Test
    public void testAccountCheck_401_Failure() throws Exception {
        String userName = "+6421xxxxxxxx";
        String apiVersion = "5.3";
        String communityUrl = "vf_nz";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/AUID/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("USER_NAME", userName)
                                                                                            .param("USER_TOKEN", userToken)
                                                                                            .param("TIMESTAMP", timestamp)
                                                                                            .param("DEVICE_UID", deviceUID))
               .andExpect(status().isUnauthorized())
               .andExpect(jsonPath("$.response.data[0].errorMessage.displayMessage").value("user login/pass check failed for [+6421xxxxxxxx] username and community [vf_nz]"));
    }

    @Test
    public void testAccountCheckForITunesClientWhichDoesNotHaveLockedTracks() throws Exception {
        final String userName = "+447111111118";
        UserStatus userStatus = new UserStatus();
        userStatus.setI((byte) 10);

        //given
        User entity = UserFactory.createUser(ActivationStatus.ACTIVATED)
                                 .withSegment(SegmentType.CONSUMER)
                                 .withContract(Contract.PAYM)
                                 .withProvider(ProviderType.O2)
                                 .withContractChannel(ContractChannel.DIRECT)
                                 .withTariff(Tariff._3G)
                                 .withNextSubPayment(new Date(1000L * 1988143200))
                                 .withUserName(userName)
                                 .withDeviceUID("b88106713409e92822461a876abcd74c")
                                 .withDeviceUID("d")
                                 .withMobile("+447111111118")
                                 .withUserGroup(userGroupRepository.findByCommunity(communityRepository.findByName("o2")));
        entity.setToken("f701af8d07e5c95d3f5cf3bd9a62344d");
        entity.setStatus(userStatus);
        entity.setDevice("");
        entity.setDeviceType(DeviceTypeCache.getDeviceTypeMapIdAsKeyAndDeviceTypeValue().get((byte) 5));
        entity.setDeviceString("IOS");
        entity.setLastDeviceLogin(1893448800);
        entity.setLastWebLogin(1893448800);
        entity.setTempToken("f701af8d07e5c95d3f5cf3bd9a62344d");
        entity.setOperator(1);
        entity.setActivationStatus(ActivationStatus.ACTIVATED);
        entity.setLastSubscribedPaymentSystem("iTunesSubscription");

        userRepository.save(entity);

        String apiVersion = "3.8";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String deviceUID = "0f607264fc6318a92b9e13c65db7cd3c";
        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)
                                                                                  .param("DEVICE_UID", deviceUID)).andExpect(status().isOk()).andDo(print()).
                   andExpect(xpath("/response/user/lockedTrack/media").nodeCount(0));
    }


    @Test
    public void testAccountCheckWhenNoProviderForUser() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        user.setProvider(null);
        userService.updateUser(user);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityName)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp))
               .
                   andExpect(status().isForbidden())
               .andDo(print())
               .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(604))
               .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("error.604.activation.status.ACTIVATED.invalid.userDetails"))
               .andExpect(jsonPath("$.response.data[0].errorMessage.displayMessage").value("User activation status [ACTIVATED] is invalid. User must have all user details"));
    }


    @Test
    public void testAccountCheckWhenUserIsNotActivated() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityName);
        user.setMobile("1");
        userService.updateUser(user);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityName)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp))
               .
                   andExpect(status().isForbidden())
               .andDo(print())
               .andExpect(jsonPath("$.response.data[0].errorMessage.errorCode").value(604))
               .andExpect(jsonPath("$.response.data[0].errorMessage.message").value("error.604.activation.status.ACTIVATED.invalid.userName"))
               .andExpect(jsonPath("$.response.data[0].errorMessage.displayMessage").value("User activation status [ACTIVATED] is invalid. User must have activated userName"));
    }


    @Test
    public void testNoFirstActivationFlagInXML() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK").param("COMMUNITY_NAME", communityName)
                                                                                  .param("USER_NAME", userName)
                                                                                  .param("USER_TOKEN", userToken)
                                                                                  .param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).
                   andExpect(xpath(AccountCheckResponseConstants.USER_XML_PATH + "/firstActivation").doesNotExist());
    }


    @Test
    public void testNoFirstActivationFlagInJSON() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "4.0";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json").param("COMMUNITY_NAME", communityName)
                                                                                       .param("USER_NAME", userName)
                                                                                       .param("USER_TOKEN", userToken)
                                                                                       .param("TIMESTAMP", timestamp)).
                   andExpect(status().isOk()).
                   andExpect(jsonPath(AccountCheckResponseConstants.USER_JSON_PATH + ".firstActivation").doesNotExist());
    }


    private CustomMatcher<String> getStartsWithMatcher(final String now) {
        return new CustomMatcher<String>("Expires header should start with : " + now) {
            @Override
            public boolean matches(Object o) {
                return ((String)o).startsWith(now);
            }
        };
    }

    private void setUserSelectedCharts(User user, int selectedChartId) {
        List<Chart> charts = new ArrayList<Chart>();
        Chart chart = chartRepository.findOne(selectedChartId);
        charts.add(chart);
        user.setSelectedCharts(charts);
        userService.updateUser(user);
    }

}

