package mobi.nowtechnologies.applicationtests.features.partner.o2;

import com.google.common.collect.Lists;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import mobi.nowtechnologies.applicationtests.features.common.DeviceTypesTransformer;
import mobi.nowtechnologies.applicationtests.features.common.VersionsTransformer;
import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet;
import mobi.nowtechnologies.applicationtests.features.social.facebook.PhoneState;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertFalse;

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

    private List<UserDeviceData> userDeviceDatas;

    @Given("^First time user with device using (\\w+) format for all o2 (\\w+) and (\\w+) community and all (\\w+) available$")
    public void given(RequestFormat requestFormat,
                      @Transform(VersionsTransformer.class) List<HasVersion> versions,
                      String community,
                      @Transform(DeviceTypesTransformer.class) List<String> deviceTypes){
        userDeviceDatas = userDeviceDataService.table(versions, Lists.newArrayList(community), deviceTypes);
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
            assertFalse(phoneState.getAccountCheck().userName.isEmpty());
        }

    }

    @When("^User sends phone number$")
    public void whenUserSendsPhoneNumber(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            //phone = generator.generate();
            //db.save(phone, "VALID") ;
            partnerDeviceSet.enterPhoneNumber(userDeviceData);
        }
    }

    @Then("^User should be registered in ENTERED_NUMBER state$")
    public void thenUserShouldInEnteredNumberState(){
        for (UserDeviceData userDeviceData : userDeviceDatas) {
            PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
            assertFalse(phoneState.getAccountCheck().userName.isEmpty());
        }

    }
}
