package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.ReactivationUserInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PromoCodeRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.persistence.repository.ReactivationUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

/**
 * User: Titov Mykhaylo (titov)
 * User: Kolpakov Alexsandr (akolpakov)
 * 05.09.13 15:44
 */
public class AutoOptInControllerTestIT extends AbstractControllerTestIT {


    @Resource
    private UserRepository userRepository;


    @Autowired
    @Qualifier("promotionRepository")
    protected PromotionRepository promotionRepository;

    @Autowired
    @Qualifier("promoCodeRepository")
    protected PromoCodeRepository promoCodeRepository;

    private Promotion promotion;
    private PromoCode promoCode;

    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;


    @Test
    public void shouldAutoOptReactivateUser() throws Exception {
        //given    org.springframework.test.web.server
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = null;
        User user = userRepository.findOne(userName, communityUrl);
        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        //then
        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk());
        reactivationUserInfo = reactivationUserInfoRepository.findByUser(user);
        assertFalse(reactivationUserInfo.isReactivationRequest());
    }

    @Test
    public void shouldAutoOptInAndVersionMore40() throws Exception {
        //given    org.springframework.test.web.server
        String userName = "+447111111114";
        String apiVersion = "5.2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = null;

        //then
        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk()).andDo(print()).andExpect(
                jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(true))
                .andExpect(jsonPath("$.response.data[0].user.displayName").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.status").value("SUBSCRIBED"))
                .andExpect(jsonPath("$.response.data[0].user.deviceUID").value("b88106713409e92622461a876abcd74b"))
                .andExpect(jsonPath("$.response.data[0].user.userToken").value(storedToken))
                .andExpect(jsonPath("$.response.data[0].user.deviceType").value("IOS"))
                .andExpect(jsonPath("$.response.data[0].user.rememberMeToken").exists())
                .andExpect(jsonPath("$.response.data[0].user.paymentType").value("O2_PSMS"))
                .andExpect(jsonPath("$.response.data[0].user.phoneNumber").value("+447111111114"))
                .andExpect(jsonPath("$.response.data[0].user.subBalance").value(0))
                .andExpect(jsonPath("$.response.data[0].user.paymentStatus").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.operator").value(1))
                .andExpect(jsonPath("$.response.data[0].user.paymentEnabled").value(true))
                .andExpect(jsonPath("$.response.data[0].user.drmType").value("PLAYS"))
                .andExpect(jsonPath("$.response.data[0].user.drmValue").value(100))
                .andExpect(jsonPath("$.response.data[0].user.promotedDevice").value(false))
                .andExpect(jsonPath("$.response.data[0].user.freeTrial").value(true))
                .andExpect(jsonPath("$.response.data[0].user.chartTimestamp").value(1321452650))
                .andExpect(jsonPath("$.response.data[0].user.chartItems").value(21))
                .andExpect(jsonPath("$.response.data[0].user.newsTimestamp").value(1317300123))
                .andExpect(jsonPath("$.response.data[0].user.newsItems").value(10))
                .andExpect(jsonPath("$.response.data[0].user.promotionLabel").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.fullyRegistred").value(true))
                .andExpect(jsonPath("$.response.data[0].user.promotedWeeks").value(2))
                .andExpect(jsonPath("$.response.data[0].user.oAuthProvider").value("NONE"))
                .andExpect(jsonPath("$.response.data[0].user.hasPotentialPromoCodePromotion").value(true))
                .andExpect(jsonPath("$.response.data[0].user.hasOffers").value(false))
                .andExpect(jsonPath("$.response.data[0].user.activation").value("ACTIVATED"))
                .andExpect(jsonPath("$.response.data[0].user.appStoreProductId").value("com.musicqubed.o2.autorenew.test"))
                .andExpect(jsonPath("$.response.data[0].user.provider").value(ProviderType.O2.getKey()))
                .andExpect(jsonPath("$.response.data[0].user.contract").value("PAYM"))
                .andExpect(jsonPath("$.response.data[0].user.segment").value("CONSUMER"))
                .andExpect(jsonPath("$.response.data[0].user.tariff").value("_3G"))
                .andExpect(jsonPath("$.response.data[0].user.graceCreditSeconds").value(0))
                .andExpect(jsonPath("$.response.data[0].user.canGetVideo").value(true))
                .andExpect(jsonPath("$.response.data[0].user.canPlayVideo").value(false))
                .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true))
                .andExpect(jsonPath("$.response.data[0].user.showFreeTrial").value(true))
                .andExpect(jsonPath("$.response.data[0].user.canActivateVideoTrial").value(false))
                .andExpect(jsonPath("$.response.data[0].user.eligibleForVideo").value(false))
                .andExpect(jsonPath("$.response.data[0].user.lastSubscribedPaymentSystem").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.subscriptionChanged").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.subjectToAutoOptIn").value(false))
                .andExpect(jsonPath("$.response.data[0].user.userName").value(userName));

        //when
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(false));

    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrong_then_401() throws Exception {
        //given
        String userName = "+447xxxxxxxxx";
        String apiVersion = "4.2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";

        //then
        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrong_then_400() throws Exception {
        //given
        String apiVersion = "4.2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrongV5d3_then_400() throws Exception {
        //given
        String apiVersion = "5.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrong_then_404() throws Exception {
        //given
        String userName = "+447111111114";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isNotFound());
    }


    @Test
    public void shouldAutoOptInAndApplyVidewPromotion() throws Exception {
        //given    org.springframework.test.web.server
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = null;

        User user = userService.findByNameAndCommunity(userName, communityUrl);
        user.setTariff(Tariff._4G);
        user.setSegment(SegmentType.CONSUMER);
        user.setVideoFreeTrialHasBeenActivated(false);
        userRepository.saveAndFlush(user);

        prepare(user);
        long currentTimeInMilliSeconds = Utils.getEpochMillis();
        //then
        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk());

        user = userRepository.findOne(userName, communityUrl);
        assertTrue(user.isVideoFreeTrialHasBeenActivated());
        assertTrue(user.getFreeTrialExpiredMillis() > currentTimeInMilliSeconds);
    }

    @Test
    public void checkAutoOptFirstActivationForJson() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = null;
        User user = userRepository.findOne(userName, communityUrl);
        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk()).andExpect(jsonPath(AccountCheckResponseConstants.USER_JSON_PATH + ".firstActivation").value(true));
    }


    @Test
    public void checkAutoOptFirstActivationForXML() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = null;
        User user = userRepository.findOne(userName, communityUrl);
        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN.xml")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk()).andExpect(xpath(AccountCheckResponseConstants.USER_X_PATH + "/firstActivation").booleanValue(true));
    }

    @Test
    public void shouldAutoOptInAndMaxVerion() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = null;

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk()).andDo(print()).andExpect(
                jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(true))
                .andExpect(jsonPath("$.response.data[0].user.displayName").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.status").value("SUBSCRIBED"))
                .andExpect(jsonPath("$.response.data[0].user.deviceUID").value("b88106713409e92622461a876abcd74b"))
                .andExpect(jsonPath("$.response.data[0].user.userToken").value(storedToken))
                .andExpect(jsonPath("$.response.data[0].user.deviceType").value("IOS"))
                .andExpect(jsonPath("$.response.data[0].user.rememberMeToken").exists())
                .andExpect(jsonPath("$.response.data[0].user.paymentType").value("O2_PSMS"))
                .andExpect(jsonPath("$.response.data[0].user.phoneNumber").value("+447111111114"))
                .andExpect(jsonPath("$.response.data[0].user.subBalance").value(0))
                .andExpect(jsonPath("$.response.data[0].user.paymentStatus").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.operator").value(1))
                .andExpect(jsonPath("$.response.data[0].user.paymentEnabled").value(true))
                .andExpect(jsonPath("$.response.data[0].user.drmType").value("PLAYS"))
                .andExpect(jsonPath("$.response.data[0].user.drmValue").value(100))
                .andExpect(jsonPath("$.response.data[0].user.promotedDevice").value(false))
                .andExpect(jsonPath("$.response.data[0].user.freeTrial").value(true))
                .andExpect(jsonPath("$.response.data[0].user.chartTimestamp").value(1321452650))
                .andExpect(jsonPath("$.response.data[0].user.chartItems").value(21))
                .andExpect(jsonPath("$.response.data[0].user.newsTimestamp").value(1317300123))
                .andExpect(jsonPath("$.response.data[0].user.newsItems").value(10))
                .andExpect(jsonPath("$.response.data[0].user.promotionLabel").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.fullyRegistred").value(true))
                .andExpect(jsonPath("$.response.data[0].user.promotedWeeks").value(2))
                .andExpect(jsonPath("$.response.data[0].user.oAuthProvider").value("NONE"))
                .andExpect(jsonPath("$.response.data[0].user.hasPotentialPromoCodePromotion").value(true))
                .andExpect(jsonPath("$.response.data[0].user.hasOffers").value(false))
                .andExpect(jsonPath("$.response.data[0].user.activation").value("ACTIVATED"))
                .andExpect(jsonPath("$.response.data[0].user.appStoreProductId").value("com.musicqubed.o2.autorenew.test"))
                .andExpect(jsonPath("$.response.data[0].user.provider").value(ProviderType.O2.getKey()))
                .andExpect(jsonPath("$.response.data[0].user.contract").value("PAYM"))
                .andExpect(jsonPath("$.response.data[0].user.segment").value("CONSUMER"))
                .andExpect(jsonPath("$.response.data[0].user.tariff").value("_3G"))
                .andExpect(jsonPath("$.response.data[0].user.graceCreditSeconds").value(0))
                .andExpect(jsonPath("$.response.data[0].user.canGetVideo").value(true))
                .andExpect(jsonPath("$.response.data[0].user.canPlayVideo").value(false))
                .andExpect(jsonPath("$.response.data[0].user.hasAllDetails").value(true))
                .andExpect(jsonPath("$.response.data[0].user.showFreeTrial").value(true))
                .andExpect(jsonPath("$.response.data[0].user.canActivateVideoTrial").value(false))
                .andExpect(jsonPath("$.response.data[0].user.eligibleForVideo").value(false))
                .andExpect(jsonPath("$.response.data[0].user.lastSubscribedPaymentSystem").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.subscriptionChanged").doesNotExist())
                .andExpect(jsonPath("$.response.data[0].user.subjectToAutoOptIn").value(false))
                .andExpect(jsonPath("$.response.data[0].user.userName").value(userName));

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(false));

    }


    @After
    public void tireDown() {
        super.tireDown();
        if (promoCode != null) {
            promoCodeRepository.delete(promoCode);
        }
        if (promotion != null) {
            promotionRepository.delete(promotion);
        }
    }


    private void prepare(User user) {
        int now = (int) (System.currentTimeMillis() / 1000);
        promotion = new Promotion();
        promotion.setEndDate(now + 10000000);
        promotion.setStartDate(now - 10000000);
        promotion.setUserGroup(user.getUserGroup());
        promotion.setMaxUsers(30);
        promotion.setNumUsers(1);
        promotion.setIsActive(true);
        promotion.setType(Promotion.ADD_FREE_WEEKS_PROMOTION);
        promotion = promotionRepository.save(promotion);

        promoCode = new PromoCode();
        promoCode.setCode("o2.consumer.4g.paym.direct");
        promoCode.setMediaType(MediaType.VIDEO_AND_AUDIO);
        promoCode.setPromotion(promotion);
        promoCode = promoCodeRepository.save(promoCode);
        promotion.setPromoCode(promoCode);
    }
}
