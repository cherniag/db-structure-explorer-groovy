package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.ReactivationUserInfo;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.repository.ReactivationUserInfoRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.annotation.Resource;

import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplyInitPromoControllerTestIT extends AbstractControllerTestIT{

    @Resource
    private UserStatusRepository userStatusRepository;

    @Resource
    private ReactivationUserInfoRepository reactivationUserInfoRepository;

    @Test
    public void checkApplyInitPromo_Success_LatestVersion() throws Exception {
        //given
        String userName = "imei_351722057812748";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = LATEST_SERVER_API_VERSION;
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(true));

        //when
        user = userService.findByName(user.getMobile());
        Assert.assertEquals(13, days(user.getNextSubPayment()));
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        Assert.assertEquals(O2, user.getProvider());
        Assert.assertEquals(PAYG, user.getContract());

        verify(o2ProviderServiceSpy, times(1)).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        verify(updateO2UserTaskSpy, times(0)).handleUserUpdate(any(User.class));

        mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", user.getMobile())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(false));
    }

    @Test
    public void givenValidO2Token_whenAPPLY_PROMO_v3d6_PromoPhoneNumber() throws Exception {
        //given
        String userName = "imei_351722057812750";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.6";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                User user = (User)invocation.getArguments()[0];
                junit.framework.Assert.assertNotNull(user);

                return null;
            }
        }).when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(true));

        //when
        user = userService.findByName(user.getMobile());
        Assert.assertEquals(13, days(user.getNextSubPayment()));
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        Assert.assertEquals(O2, user.getProvider());
        Assert.assertEquals(PAYM, user.getContract());
        Assert.assertEquals(CONSUMER, user.getSegment());
        Assert.assertEquals(ContractChannel.DIRECT, user.getContractChannel());

        verify(o2ProviderServiceSpy, times(1)).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        verify(updateO2UserTaskSpy, times(1)).handleUserUpdate(any(User.class));

        mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", user.getMobile())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andDo(print()).
                andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(false));

    }

    @Test
    public void givenValidO2Token_whenAPPLY_PROMO_thenBigPromotionSetAndAccCheckInfo() throws Exception {
        //given
        String userName = "imei_351722057812748";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                User user = (User)invocation.getArguments()[0];
                junit.framework.Assert.assertNotNull(user);

                return null;
            }
        }).when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(true));

        //when
        user = userService.findByName(user.getMobile());
        Assert.assertEquals(13, days(user.getNextSubPayment()));
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        Assert.assertEquals(O2, user.getProvider());
        Assert.assertEquals(PAYG, user.getContract());
        Assert.assertEquals(CONSUMER, user.getSegment());
        Assert.assertEquals(ContractChannel.DIRECT, user.getContractChannel());

        verify(o2ProviderServiceSpy, times(1)).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        verify(updateO2UserTaskSpy, times(1)).handleUserUpdate(any(User.class));

        mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", user.getMobile())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("response.data[0].user.hasPotentialPromoCodePromotion").value(false));

    }

    @Test
    public void givenValidO2Token_whenUserWithPhoneExistsAndRegistrationFromNewDevice_thenReturnOldUserWithNewDeviceAndRemoveSecondUser() throws Exception {
        //given
        String userName = "imei_351722057812749";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "11111111-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk());

        //when
        User mobileUser = userService.findByName("+447111111111");

        Assert.assertEquals(user.getDevice(), mobileUser.getDevice());
        Assert.assertEquals(user.getDeviceUID(), mobileUser.getDeviceUID());
        Assert.assertEquals(user.getDeviceModel(), mobileUser.getDeviceModel());
        Assert.assertEquals(user.getDeviceType(), mobileUser.getDeviceType());

        user = userService.findByName(userName);
        Assert.assertNull(user);

        verify(o2ProviderServiceSpy, times(1)).getUserDetails(eq(otac), eq(mobileUser.getMobile()), any(Community.class));
        verify(updateO2UserTaskSpy, times(1)).handleUserUpdate(any(User.class));
    }

    @Test
    public void givenValidO2Token_whenUserReInstallAppWithOldPhoneNumber_then_ReturnAUserWithOldPhoneNumberAndAppliedPromo() throws Exception {
        //given
        String userName = "+447111111111";
        String oldUserName = "b88106713409e92622461a876abcd74c";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        user.setUserName(oldUserName);
        user.setNextSubPayment(0);
        userRepository.save(user);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", oldUserName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk());

        //when
        User mobileUser = userService.findByName(userName);

        Assert.assertEquals(user.getDevice(), mobileUser.getDevice());
        Assert.assertEquals(user.getDeviceUID(), mobileUser.getDeviceUID());
        Assert.assertEquals(user.getDeviceModel(), mobileUser.getDeviceModel());
        Assert.assertEquals(user.getDeviceString(), mobileUser.getDeviceString());
        Assert.assertEquals(ActivationStatus.ACTIVATED, mobileUser.getActivationStatus());
        Assert.assertEquals(13, days(mobileUser.getNextSubPayment()));

        verify(o2ProviderServiceSpy, times(1)).getUserDetails(eq(otac), eq(mobileUser.getMobile()), any(Community.class));
        verify(updateO2UserTaskSpy, times(1)).handleUserUpdate(any(User.class));
    }

    @Test
    public void applyInitPromo_whenUserCallMethodTwice_then_ReturnActivationError() throws Exception {
        //given
        String userName = "+447733333333";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        user.setActivationStatus(ActivationStatus.ACTIVATED);
        userRepository.save(user);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isForbidden());

        //when
        user = userService.findByName(userName);
        Assert.assertEquals(user.getUserName(), "+447733333333");
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
    }

    @Test
    public void applyInitPromo_whenUserReInstallAppWithNewPhoneNumber_then_ReturnAUserWithNewPhoneNumber() throws Exception {
    	//given
        String userName = "999a72f8864fd5c23957beef9d99656568";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk());

        user = userService.findByName(user.getMobile());

        //when
        Assert.assertNotNull(user);
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrong_then_401() throws Exception {
        //given
        String userName = "+447766666666";
        String apiVersion = "3.9";
        User user = prepareUserForApplyInitPromo(userName);
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        userName = "+4477xxxxxxxxxx";

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrongV3d9_then_400() throws Exception {
        //given
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "";
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrongV5d3_then_400() throws Exception {
        //given
        String apiVersion = "5.3";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "";
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrong_then_404() throws Exception {
        //given
        String userName = "+447766666666";
        String apiVersion = "3.5";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "";
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isNotFound());
    }


    @Test
    public void applyInitPromoWithReactivation() throws Exception {
        //given
        String userName = "999a72f8864fd5c23957beef9d99656568";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "6.0";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));
        ReactivationUserInfo reactivationUserInfo = new ReactivationUserInfo();
        reactivationUserInfo.setUser(user);
        reactivationUserInfo.setReactivationRequest(true);
        reactivationUserInfoRepository.save(reactivationUserInfo);
        //then
        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk());

        user = userService.findByName(user.getMobile());

        //when
        Assert.assertNotNull(user);
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        reactivationUserInfo = reactivationUserInfoRepository.findByUser(user);
        assertFalse(reactivationUserInfo.isReactivationRequest());
    }

    @Test
    public void testApplyInitPromoWithMergeAccountsForXML() throws Exception {
        //given
        String userName = "999a72f8864fd5c23957beef9d99656568";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk()).andExpect(xpath(AccountCheckResponseConstants.USER_XML_PATH + "/firstActivation").booleanValue(true));
    }


    @Test
    public void testApplyInitPromoWithMergeAccountsForJSON() throws Exception {
        //given
        String userName = "999a72f8864fd5c23957beef9d99656568";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        ProviderUserDetails providerUserDetails = new ProviderUserDetails();
        providerUserDetails.withContract("PAYG").withOperator("o2");
        doReturn(providerUserDetails).when(o2ProviderServiceSpy).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        doNothing().when(updateO2UserTaskSpy).handleUserUpdate(any(User.class));

        mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk()).andExpect(jsonPath(AccountCheckResponseConstants.USER_JSON_PATH + ".firstActivation").value(true));
    }

    private User prepareUserForApplyInitPromo(String userName){
        User user = userService.findByName(userName);

        user.setActivationStatus(ActivationStatus.ENTERED_NUMBER);
        user.setProvider(ProviderType.O2);
        user.setSegment(CONSUMER);
        user.setContract(Contract.PAYG);
        user.setTariff(Tariff._3G);
        user.setContractChannel(ContractChannel.DIRECT);

        UserStatus userStatus = userStatusRepository.findByName(UserStatus.LIMITED);
        user.setStatus(userStatus);

        return userService.updateUser(user);
    }

    private int days(long nextSubPayment) {
        DateTimeZone timeZone = DateTimeZone.forID("UTC");
        return Days.daysBetween(new DateTime(System.currentTimeMillis(), timeZone), new DateTime(nextSubPayment * 1000, timeZone)).getDays();
    }
}
