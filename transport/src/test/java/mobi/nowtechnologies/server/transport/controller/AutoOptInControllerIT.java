package mobi.nowtechnologies.server.transport.controller;

import com.google.gson.JsonObject;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import static mobi.nowtechnologies.server.shared.dto.OAuthProvider.NONE;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * User: Titov Mykhaylo (titov)
 * User: Kolpakov Alexsandr (akolpakov)
 * 05.09.13 15:44
 */
public class AutoOptInControllerIT extends AbstractControllerTestIT{

    @Test
    public void shouldAutoOptInAndVersionMore40() throws Exception {
        //given
        String userName = "+447111111114";
        String appVersion = "6.0";
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "b88106713409e92622461a876abcd74b";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        String otac = null;

        //then
        ResultActions resultActions = mockMvc.perform(
                post("/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
                        .param("DEVICE_UID", deviceUid)
        ).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(true));

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();
        JsonObject resultJsonObject = getAccCheckContent(resultJson);
        AccountCheckDTO accountCheckDTO = gson.fromJson(resultJsonObject, AccountCheckDTO.class);

        assertEquals(null, accountCheckDTO.displayName);
        assertEquals("SUBSCRIBED", accountCheckDTO.status);
        assertEquals(userName, accountCheckDTO.userName);
        assertEquals("b88106713409e92622461a876abcd74b", accountCheckDTO.deviceUID);
        assertEquals(storedToken, accountCheckDTO.userToken);
        assertEquals("IOS", accountCheckDTO.deviceType);
        assertNotNull(accountCheckDTO.rememberMeToken);
        assertEquals("O2_PSMS", accountCheckDTO.paymentType);
        assertEquals("+447111111114", accountCheckDTO.phoneNumber);
        assertEquals(0, accountCheckDTO.subBalance);
        assertEquals(null, accountCheckDTO.paymentStatus);
        assertEquals(new Integer(1), accountCheckDTO.operator);
        assertEquals(true, accountCheckDTO.paymentEnabled);
        assertEquals("PLAYS", accountCheckDTO.drmType);
        assertEquals(100, accountCheckDTO.drmValue);
        assertEquals(PaymentDetailsStatus.NONE, accountCheckDTO.lastPaymentStatus);
        assertEquals(false, accountCheckDTO.promotedDevice);
        assertEquals(true, accountCheckDTO.freeTrial);
        assertEquals(1321452650, accountCheckDTO.chartTimestamp);
        assertEquals(21, accountCheckDTO.chartItems);
        assertEquals(1317300123, accountCheckDTO.newsTimestamp);
        assertEquals(10, accountCheckDTO.newsItems);
        assertEquals(null, accountCheckDTO.promotionLabel);
        assertEquals(true, accountCheckDTO.fullyRegistred);
        assertEquals(2, accountCheckDTO.promotedWeeks);
        assertEquals(NONE, accountCheckDTO.oAuthProvider);
        assertEquals(true, accountCheckDTO.hasPotentialPromoCodePromotion);
        assertEquals(false, accountCheckDTO.hasOffers);
        assertEquals(ActivationStatus.ACTIVATED, accountCheckDTO.activation);
        assertEquals("com.musicqubed.o2.autorenew.test", accountCheckDTO.appStoreProductId);
        assertEquals(O2.getKey(), accountCheckDTO.provider);
        assertEquals(PAYM, accountCheckDTO.contract);
        assertEquals(CONSUMER, accountCheckDTO.segment);
        assertEquals(_3G, accountCheckDTO.tariff);
        assertEquals(0, accountCheckDTO.graceCreditSeconds);
        assertEquals(true, accountCheckDTO.canGetVideo);
        assertEquals(false, accountCheckDTO.canPlayVideo);
        assertEquals(false, accountCheckDTO.hasAllDetails);
        assertEquals(true, accountCheckDTO.showFreeTrial);
        assertEquals(false, accountCheckDTO.canActivateVideoTrial);
        assertEquals(false, accountCheckDTO.eligibleForVideo);
        assertEquals(null, accountCheckDTO.lastSubscribedPaymentSystem);
        assertEquals(null, accountCheckDTO.subscriptionChanged);
        assertEquals(false, accountCheckDTO.subjectToAutoOptIn);


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
        String appVersion = "4.2";
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
