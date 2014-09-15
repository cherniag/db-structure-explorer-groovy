package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.http.activate.ApplyInitPromoHttpService;
import mobi.nowtechnologies.applicationtests.services.http.activate.AutoOptInHttpService;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneNumberHttpService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Author: Gennadii Cherniaiev
 * Date: 7/2/2014
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PartnerDeviceSet extends ClientDevicesSet {

    @Resource
    private PhoneNumberHttpService phoneNumberHttpService;

    @Resource
    private AutoOptInHttpService autoOptInHttpService;

    @Resource
    private ApplyInitPromoHttpService applyInitPromoHttpService;

    public void enterPhoneNumber(UserDeviceData userDeviceData, String phoneNumber){
        final PhoneStateImpl state = states.get(userDeviceData);

        state.phoneActivationDto = phoneNumberHttpService.phoneNumber(userDeviceData, phoneNumber, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken, userDeviceData.getFormat());
        state.accountCheck = accountCheckHttpService.accountCheck(userDeviceData, state.getLastAccountCheckResponse().userName, state.getLastAccountCheckResponse().userToken, userDeviceData.getFormat());
    }

    public void activate(UserDeviceData userDeviceData, String otac){
        final PhoneStateImpl state = states.get(userDeviceData);

        if(state.getLastAccountCheckResponse().subjectToAutoOptIn) {
            AccountCheckDTO response = autoOptInHttpService.autoOptIn(state.getLastAccountCheckResponse(), userDeviceData, otac, userDeviceData.getFormat());

            state.accountCheck = response;
            state.activationResponse = response;
        } else {
            AccountCheckDTO response = applyInitPromoHttpService.applyInitPromo(state.getLastAccountCheckResponse(), userDeviceData, otac, userDeviceData.getFormat());
            state.accountCheck = response;
            state.activationResponse = response;
        }
    }
}
