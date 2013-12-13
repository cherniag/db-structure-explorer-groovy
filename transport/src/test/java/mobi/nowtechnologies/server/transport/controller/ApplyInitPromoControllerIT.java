package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.*;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.server.ResultActions;

import javax.annotation.Resource;

import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class ApplyInitPromoControllerIT extends AbstractControllerTestIT{

    @Resource(name = "userRepository")
    UserRepository userRepository;

    @Test
    public void givenValidO2Token_whenAPPLY_PROMO_thenBigPromotionSetAndAccCheckInfo() throws Exception {
        //given
        String userName = "imei_351722057812748";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityName = "o2";
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
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();
        assertTrue(resultJson.contains("\"hasPotentialPromoCodePromotion\":true"));

        //when
        user = userService.findByName(user.getMobile());
        Assert.assertEquals(13, days(user.getNextSubPayment()));
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        Assert.assertEquals(O2, user.getProvider());
        Assert.assertEquals(PAYG, user.getContract());

        verify(o2ProviderServiceSpy, times(1)).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        verify(updateO2UserTaskSpy, times(1)).handleUserUpdate(any(User.class));

        resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", user.getMobile())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultAccCkeckJson = aHttpServletResponse.getContentAsString();
        resultJson = resultJson.replaceAll("\"hasPotentialPromoCodePromotion\":true", "\"hasPotentialPromoCodePromotion\":false");

        assertTrue(resultAccCkeckJson.equals(resultJson));
    }

    @Test
    public void givenValidO2Token_whenAPPLY_PROMOForVersionMore40_thenBigPromotionSetAndAccCheckInfo() throws Exception {
        //given
        String userName = "imei_351722057812748";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "6.0";
        String communityName = "o2";
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
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk());

        MockHttpServletResponse aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultJson = aHttpServletResponse.getContentAsString();
        assertTrue(resultJson.contains("\"hasPotentialPromoCodePromotion\":true"));

        //when
        user = userService.findByName(user.getMobile());
        Assert.assertEquals(13, days(user.getNextSubPayment()));
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());
        Assert.assertEquals(O2, user.getProvider());
        Assert.assertEquals(PAYG, user.getContract());

        verify(o2ProviderServiceSpy, times(1)).getUserDetails(eq(otac), eq(user.getMobile()), any(Community.class));
        verify(updateO2UserTaskSpy, times(0)).handleUserUpdate(any(User.class));

        resultActions = mockMvc.perform(
                post("/"+communityUrl+"/"+apiVersion+"/ACC_CHECK.json")
                        .param("USER_NAME", user.getMobile())
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
        ).andExpect(status().isOk());

        aHttpServletResponse = resultActions.andReturn().getResponse();
        String resultAccCkeckJson = aHttpServletResponse.getContentAsString();
        resultJson = resultJson.replaceAll("\"hasPotentialPromoCodePromotion\":true", "\"hasPotentialPromoCodePromotion\":false");

        assertTrue(resultAccCkeckJson.equals(resultJson));
    }
    
    @Test
    public void givenValidO2Token_whenUserWithPhoneExistsAndREgistrationFromNewDevice_thenReturnOldUserWithNewDeviceAndRemoveSecondUser() throws Exception {
        //given
        String userName = "imei_351722057812749";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityName = "o2";
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
        ResultActions resultActions = mockMvc.perform(
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
        String oldUserName = "+447888888888";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityName = "o2";
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
        ResultActions resultActions = mockMvc.perform(
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
        String communityName = "o2";
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
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO.json")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isOk());

        //when
        user = userService.findByName(userName);
        Assert.assertEquals(user.getUserName(), "+447733333333");
        Assert.assertEquals(ActivationStatus.ACTIVATED, user.getActivationStatus());;
    }

    @Test
    public void applyInitPromo_whenUserReInstallAppWithNewPhoneNumber_then_ReturnAUserWithNewPhoneNumber() throws Exception {
    	//given
        String userName = "+447766666666";
        User user = prepareUserForApplyInitPromo(userName);
        String apiVersion = "3.9";
        String communityName = "o2";
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
        ResultActions resultActions = mockMvc.perform(
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
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = user.getToken();
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);
        userName = "+4477xxxxxxxxxx";

        //then
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void applyInitPromo_whenUserUserNameIsWrong_then_400() throws Exception {
        //given
        String userName = "+447766666666";
        String apiVersion = "3.9";
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "";
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        //then
        ResultActions resultActions = mockMvc.perform(
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
        String communityName = "o2";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "";
        String otac = "00000000-c768-4fe7-bb56-a5e0c722cd44";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        //then
        ResultActions resultActions = mockMvc.perform(
                post("/" + communityUrl + "/" + apiVersion + "/APPLY_INIT_PROMO")
                        .param("USER_NAME", userName)
                        .param("USER_TOKEN", userToken)
                        .param("TIMESTAMP", timestamp)
                        .param("OTAC_TOKEN", otac)
        ).andExpect(status().isNotFound());
    }

    private User prepareUserForApplyInitPromo(String userName){
        User user = userService.findByName(userName);

        user.setActivationStatus(ActivationStatus.ENTERED_NUMBER);
        user.setProvider(ProviderType.O2);
        user.setSegment(SegmentType.CONSUMER);
        user.setContract(Contract.PAYG);
        user.setTariff(Tariff._3G);
        user.setContractChannel(ContractChannel.DIRECT);

        return userService.updateUser(user);
    }
    
    private int days(long nextSubPayment) {

        return Days.daysBetween(new DateTime(System.currentTimeMillis()), new DateTime(nextSubPayment * 1000)).getDays();

    }

}
