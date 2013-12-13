package mobi.nowtechnologies.server.transport.controller;

import com.google.gson.JsonObject;
import mobi.nowtechnologies.server.persistence.domain.PromoCode;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.PromoCodeRepository;
import mobi.nowtechnologies.server.persistence.repository.PromotionRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class ActivateVideoAudioFreeTrialControllerTestIT extends AbstractControllerTestIT{
    @Autowired
    @Qualifier("promotionRepository")
    protected PromotionRepository promotionRepository;

    @Autowired
    @Qualifier("promoCodeRepository")
    protected PromoCodeRepository promoCodeRepository;

    @Test
    public void testActivateVideoAudioFreeTrial_WithAccCheckDetailsAndVersionMore50_Success() throws Exception {
        String userName = "+447111111114";
        String appVersion = "6.0";
        String apiVersion = "6.0";
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

        int now = (int)(System.currentTimeMillis()/1000);
        Promotion promotion = new Promotion();
        promotion.setEndDate(now + 10000000);
        promotion.setStartDate(now - 10000000);
        promotion.setUserGroup(user.getUserGroup());
        promotion.setMaxUsers(30);
        promotion.setNumUsers(1);
        promotion.setIsActive(true);
        promotion.setType(Promotion.ADD_FREE_WEEKS_PROMOTION);
        promotion = promotionRepository.save(promotion);

        PromoCode promoCode = new PromoCode();
        promoCode.setCode("o2.consumer.4g.paym.direct");
        promoCode.setMediaType(MediaType.VIDEO_AND_AUDIO);
        promoCode.setPromotion(promotion);
        promoCode = promoCodeRepository.save(promoCode);
        promotion.setPromoCode(promoCode);

        ResultActions resultActions = mockMvc.perform(
        post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultActivateVideoFreeTrialJson = aHttpServletResponse.getContentAsString();
        JsonObject jsonObject = getAccCheckContent(resultActivateVideoFreeTrialJson);

        resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultAccCkeckJson = aHttpServletResponse.getContentAsString();

        assertTrue(resultAccCkeckJson.contains(jsonObject.toString()));
    }

    @Test
    public void testActivateVideoAudioFreeTrial_EmptyDeviceUIDAndNotEligableForVideo_Failure() throws Exception {
    	String userName = "+447111111114";
        String appVersion = "4.0";
		String apiVersion = "4.0";
		String communityUrl = "o2";
		String timestamp = "2011_12_26_07_04_23";
		String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
		String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions resultActions = mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isInternalServerError());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("<errorCode>5001</errorCode>"));
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

        ResultActions resultActions = mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isInternalServerError());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultXml = aHttpServletResponse.getContentAsString();

        assertTrue(resultXml.contains("\"errorCode\":5001"));
    }

    @Test
    public void testActivateVideoAudioFreeTrial_401_Failure() throws Exception {
        String userName = "+447xxxxxxxxx";
        String appVersion = "4.0";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions resultActions = mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testActivateVideoAudioFreeTrial_400_Failure() throws Exception {
        String userName = "+447xxxxxxxxx";
        String appVersion = "4.0";
        String apiVersion = "4.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions resultActions = mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isBadRequest());;
    }

    @Test
    public void testActivateVideoAudioFreeTrial_404_Failure() throws Exception {
        String userName = "+447xxxxxxxxx";
        String appVersion = "4.0";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ResultActions resultActions = mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isNotFound());
    }
}
