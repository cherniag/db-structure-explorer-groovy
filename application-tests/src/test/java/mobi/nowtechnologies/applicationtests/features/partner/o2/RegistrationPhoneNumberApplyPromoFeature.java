package mobi.nowtechnologies.applicationtests.features.partner.o2;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.DeviceTypesTransformer;
import mobi.nowtechnologies.applicationtests.features.common.VersionsTransformer;
import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.OtacCodeCreator;
import mobi.nowtechnologies.applicationtests.services.helper.PhoneNumberCreator;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/2/2014
 */
@Component
public class RegistrationPhoneNumberApplyPromoFeature {

    @Resource
    private PartnerDeviceSet partnerDeviceSet;

    @Resource
    private UserDeviceDataService userDeviceDataService;

    @Resource
    private PhoneNumberCreator phoneNumberCreator;

    @Resource
    private OtacCodeCreator otacCodeCreator;

    @Resource
    private UserDbService userDbService;

    private List<UserDeviceData> userDeviceDatas;

    @Given("^First time user with device using (\\w+) format for all o2 (\\w+) and (\\w+) community and all (\\w+) available$")
    public void given(RequestFormat requestFormat,
                      @Transform(VersionsTransformer.class) List<HasVersion> versions,
                      String community,
                      @Transform(DeviceTypesTransformer.class) List<String> deviceTypes){
        partnerDeviceSet.setFormat(requestFormat);
        userDeviceDatas = userDeviceDataService.table(versions, community, deviceTypes);
    }

    @When("^User registers using device$")
    public void whenUserRegisters(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            partnerDeviceSet.singup(userDeviceData);
        }
    }

    @Then("^User should be registered in system$")
    public void thenUserShouldRegistered(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            assertFalse(phoneState.getLastAccountCheckResponse().userName.isEmpty());
        }

    }

    @When("^User sends o2 valid phone number$")
    public void whenUserSendsPhoneNumber(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            String phoneNumber = phoneNumberCreator.createValidPhoneNumber(ProviderType.O2, SegmentType.BUSINESS, Contract.PAYG, Tariff._4G, ContractChannel.DIRECT);
            partnerDeviceSet.enterPhoneNumber(userDeviceData, phoneNumber);
        }
    }

    @Then("^User should receive (\\w+) activation status in phone number response$")
    public void thenUserShouldReceiveStateInResponse(ActivationStatus activationStatus){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            assertEquals(activationStatus, phoneState.getPhoneActivationResponse().getActivation());
        }
    }

    @And("^User should have (\\w+) activation status in database")
    public void thenUserShouldReceiveStateInDatabase(ActivationStatus activationStatus){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), userDeviceData.getCommunityUrl());
            assertEquals(activationStatus, user.getActivationStatus());
        }
    }

    @When("^User sends valid OTAC for applying promo$")
    public void whenUserSendsValidOTAC(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            String otac = otacCodeCreator.generateValidOtac(phoneState.getLastAccountCheckResponse());
            partnerDeviceSet.activate(userDeviceData, otac);
        }
    }

    @Then("^User should receive (\\w+) activation status in activation response$")
    public void thenUserShouldHaveStatusInResponse(ActivationStatus activationStatus){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            assertEquals(activationStatus, phoneState.getActivationResponse().activation);
        }
    }

    @And("^promo should be applied$")
    public void thenUserShouldBeActivatedAndPromoShouldBeApplied(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), userDeviceData.getCommunityUrl());
            assertNotNull(user.getLastPromo());
        }
    }

    @And("^promo should have (\\w+) media type$")
    public void andPromoShouldHaveMediaType(MediaType mediaType){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            User user = userDbService.getUserByDeviceUIDAndCommunity(phoneState.getDeviceUID(), userDeviceData.getCommunityUrl());
            assertEquals(mediaType, user.getLastPromo().getMediaType());
        }
    }

    @When("^User sends o2 valid phone number with provider (\\w+) and segment (\\w+) and tariff (\\w+)$")
    public void whenUserSendsPhoneNumberConsumer(ProviderType providerType, SegmentType consumer, Tariff tariff){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            final Contract anyContract = Contract.PAYG;
            final ContractChannel anyContractChannel = ContractChannel.DIRECT;
            String phoneNumber = phoneNumberCreator.createValidPhoneNumber(providerType, consumer, anyContract, tariff, anyContractChannel);
            partnerDeviceSet.enterPhoneNumber(userDeviceData, phoneNumber);
        }
    }
}