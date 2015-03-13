package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.OtacCodeCreator;
import mobi.nowtechnologies.applicationtests.services.helper.PhoneNumberCreator;
import mobi.nowtechnologies.applicationtests.services.http.activate.ApplyInitPromoHttpService;
import mobi.nowtechnologies.applicationtests.services.http.activate.AutoOptInHttpService;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneNumberHttpService;
import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.ContractChannel;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Author: Gennadii Cherniaiev Date: 7/2/2014
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerDeviceSet extends ClientDevicesSet {

    @Resource
    PhoneNumberHttpService phoneNumberHttpService;
    @Resource
    AutoOptInHttpService autoOptInHttpService;
    @Resource
    ApplyInitPromoHttpService applyInitPromoHttpService;
    @Resource
    PhoneNumberCreator phoneNumberCreator;
    @Resource
    OtacCodeCreator otacCodeCreator;

    public void enterPhoneNumber(UserDeviceData userDeviceData, String phoneNumber) {
        final PhoneStateImpl state = states.get(userDeviceData);

        state.phoneActivationDto =
            phoneNumberHttpService.phoneNumber(userDeviceData, phoneNumber, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken, userDeviceData.getFormat());
        state.accountCheck =
            accountCheckHttpService.accountCheck(userDeviceData, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken, userDeviceData.getFormat());
    }

    public void activate(UserDeviceData userDeviceData, String otac) {
        final PhoneStateImpl state = states.get(userDeviceData);

        if (state.getLastAccountCheckResponse().subjectToAutoOptIn) {
            AccountCheckDto response = autoOptInHttpService.autoOptIn(state.getLastAccountCheckResponse(), userDeviceData, otac, userDeviceData.getFormat());

            state.accountCheck = response;
            state.activationResponse = response;
        } else {
            AccountCheckDto response = applyInitPromoHttpService.applyInitPromo(state.getLastAccountCheckResponse(), userDeviceData, otac, userDeviceData.getFormat());
            state.accountCheck = response;
            state.activationResponse = response;
        }
    }

    public void signUpAndActivate(UserDeviceData userDeviceData) {
        singup(userDeviceData);

        String phoneNumber = phoneNumberCreator.createO2ValidPhoneNumber(ProviderType.O2, SegmentType.BUSINESS, Contract.PAYG, Tariff._4G, ContractChannel.DIRECT);
        enterPhoneNumber(userDeviceData, phoneNumber);

        PhoneState phoneState = getPhoneState(userDeviceData);
        String otac = otacCodeCreator.generateValidOtac(phoneState.getLastAccountCheckResponse());
        activate(userDeviceData, otac);
    }
}
