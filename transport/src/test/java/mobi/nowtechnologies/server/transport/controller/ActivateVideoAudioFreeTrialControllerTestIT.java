package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PromoCodeRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.After;
import org.junit.Test;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ActivateVideoAudioFreeTrialControllerTestIT extends AbstractControllerTestIT{

    @Resource(name = "promotionRepository")
    protected PromotionRepository promotionRepository;

    @Resource(name = "promoCodeRepository")
    protected PromoCodeRepository promoCodeRepository;

    private Promotion promotion;
    private PromoCode promoCode;

    @Test
    public void testActivateVideoAudioFreeTrial_LatestVersion() throws Exception {
        String userName = "+447111111114";
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userService.findByNameAndCommunity(userName, communityUrl);
        user.setTariff(Tariff._4G);
        user.setSegment(SegmentType.CONSUMER);
        user.setVideoFreeTrialHasBeenActivated(false);
        userService.updateUser(user);

        prepare(user);

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk()).andExpect(jsonPath("response.data[0].user.canPlayVideo").value(true));

        mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andExpect(jsonPath("response.data[0].user.canPlayVideo").value(true));

    }


    @Test
    public void testActivateVideoAudioFreeTrial_EmptyDeviceUIDAndNotEligableForVideo_Failure() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isInternalServerError()).andExpect(xpath("/response/errorMessage/errorCode").number(5001d));

    }

    @Test
    public void testActivateVideoAudioFreeTrial_EmptyDeviceUIDAndNotEligableForVideo_JsonAndAdditionalUIDAndVersionMore50_Failure() throws Exception {
        String userName = "+447111111114";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isInternalServerError()).andExpect(jsonPath("response.data[0].errorMessage.errorCode").value(5001));
    }

    @Test
    public void testActivateVideoAudioFreeTrial_401_Failure() throws Exception {
        String userName = "+447xxxxxxxxx";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testActivateVideoAudioFreeTrialv4d0_400_Failure() throws Exception {
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void testActivateVideoAudioFreeTrialv5d2_400_Failure() throws Exception {
        String apiVersion = "5.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testActivateVideoAudioFreeTrial_404_Failure() throws Exception {
        String userName = "+447xxxxxxxxx";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isNotFound());
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
        int now = (int)(System.currentTimeMillis()/1000);
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
