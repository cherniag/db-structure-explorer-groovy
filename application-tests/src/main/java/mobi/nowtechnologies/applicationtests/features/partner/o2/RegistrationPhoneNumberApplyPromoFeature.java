package mobi.nowtechnologies.applicationtests.features.partner.o2;

import mobi.nowtechnologies.applicationtests.features.common.client.PartnerDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.DictionaryTransformer;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.services.db.UserDbService;
import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.OtacCodeCreator;
import mobi.nowtechnologies.applicationtests.services.helper.PhoneNumberCreator;
import mobi.nowtechnologies.applicationtests.services.runner.Invoker;
import mobi.nowtechnologies.applicationtests.services.runner.Runner;
import mobi.nowtechnologies.applicationtests.services.runner.RunnerService;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import javax.annotation.Resource;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import org.springframework.stereotype.Component;

import static org.junit.Assert.*;

/**
 * Author: Gennadii Cherniaiev Date: 7/2/2014
 */
@Component
public class RegistrationPhoneNumberApplyPromoFeature {

    @Resource
    PartnerDeviceSet partnerDeviceSet;
    @Resource
    UserDeviceDataService userDeviceDataService;
    @Resource
    PhoneNumberCreator phoneNumberCreator;
    @Resource
    OtacCodeCreator otacCodeCreator;
    @Resource
    UserDbService userDbService;

    List<UserDeviceData> userDeviceDatas;

    @Resource
    RunnerService runnerService;
    Runner runner;


    @Given("^First time user with device using JSON and XML formats for (.+) and (\\w+) community and (.+) available$")
    public void given(@Transform(DictionaryTransformer.class) Word versions, String community, @Transform(DictionaryTransformer.class) Word deviceTypes) {
        userDeviceDatas = userDeviceDataService.table(versions.list(), community, deviceTypes.list());
    }

    @When("^User registers using device$")
    public void whenUserRegisters() {
        runner = runnerService.create(userDeviceDatas);
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                partnerDeviceSet.singup(userDeviceData);
            }
        });
    }

    @Then("^User should be registered in system$")
    public void thenUserShouldRegistered() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
                assertFalse(phoneState.getLastAccountCheckResponse().userName.isEmpty());
            }
        });
    }

    @When("^User sends o2 valid phone number$")
    public void whenUserSendsPhoneNumber() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                String phoneNumber = phoneNumberCreator.createO2ValidPhoneNumber(ProviderType.O2, SegmentType.BUSINESS, Contract.PAYG, Tariff._4G, ContractChannel.DIRECT);
                partnerDeviceSet.enterPhoneNumber(userDeviceData, phoneNumber);
            }
        });
    }

    @Then("^User should receive (\\w+) activation status in phone number response$")
    public void thenUserShouldReceiveStateInResponse(final ActivationStatus activationStatus) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
                assertEquals(activationStatus, phoneState.getPhoneActivationResponse().getActivation());
            }
        });
    }

    @And("^User should have (\\w+) activation status in database")
    public void thenUserShouldReceiveStateInDatabase(final ActivationStatus activationStatus) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
                User user = userDbService.findUser(phoneState, userDeviceData);
                assertEquals(activationStatus, user.getActivationStatus());
            }
        });
    }

    @When("^User sends valid OTAC for applying promo$")
    public void whenUserSendsValidOTAC() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
                String otac = otacCodeCreator.generateValidOtac(phoneState.getLastAccountCheckResponse());
                partnerDeviceSet.activate(userDeviceData, otac);
            }
        });
    }

    @Then("^User should receive (\\w+) activation status in activation response$")
    public void thenUserShouldHaveStatusInResponse(final ActivationStatus activationStatus) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
                assertEquals(activationStatus, phoneState.getActivationResponse().activation);
            }
        });
    }

    @And("^promo should be applied$")
    public void thenUserShouldBeActivatedAndPromoShouldBeApplied() {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
                User user = userDbService.findUser(phoneState, userDeviceData);
                assertNotNull(user.getLastPromo());
            }
        });
    }

    @And("^promo should have (\\w+) media type$")
    public void andPromoShouldHaveMediaType(final MediaType mediaType) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                PhoneState phoneState = partnerDeviceSet.getPhoneState(userDeviceData);
                User user = userDbService.findUser(phoneState, userDeviceData);
                assertEquals(mediaType, user.getLastPromo().getMediaType());
            }
        });
    }

    @When("^User sends o2 valid phone number with provider (\\w+) and segment (\\w+) and tariff (\\w+)$")
    public void whenUserSendsPhoneNumberConsumer(final ProviderType providerType, final SegmentType consumer, final Tariff tariff) {
        runner.parallel(new Invoker<UserDeviceData>() {
            @Override
            public void invoke(UserDeviceData userDeviceData) {
                final Contract anyContract = Contract.PAYG;
                final ContractChannel anyContractChannel = ContractChannel.DIRECT;
                String phoneNumber = phoneNumberCreator.createO2ValidPhoneNumber(providerType, consumer, anyContract, tariff, anyContractChannel);
                partnerDeviceSet.enterPhoneNumber(userDeviceData, phoneNumber);
            }
        });
    }
}
