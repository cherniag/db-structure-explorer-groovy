package mobi.nowtechnologies.applicationtests.features.common.client;

import mobi.nowtechnologies.applicationtests.features.social.facebook.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.http.phonenumber.PhoneNumberHttpService;
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

    public void enterPhoneNumber(UserDeviceData userDeviceData){
        final PhoneState state = states.get(userDeviceData);

        phoneNumberHttpService.phoneNumber(userDeviceData, "", state.getAccountCheck().userName, state.getAccountCheck().userToken, "", format);

    }


}
