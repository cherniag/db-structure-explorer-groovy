package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import org.junit.Test;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * User: Titov Mykhaylo (titov)
 * User: Kolpakov Alexsandr (akolpakov)
 * 05.09.13 15:44
 */
public class AutoOptInControllerTestIT extends AbstractControllerTestIT{

    @Test
    public void shouldAutoOptInAndVersionMore40() throws Exception {
        //given
        String userName = "+447111111114";
        String apiVersion = "6.0";
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
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
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
}
