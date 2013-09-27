package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.OtacValidationService;
import mobi.nowtechnologies.server.service.o2.impl.O2ClientServiceImpl;

/**
 * User: Titov Mykhaylo (titov)
 * 27.09.13 14:42
 */
public class OtacValidationServiceImpl implements OtacValidationService{

    private O2ClientService o2ClientService;

    public void setO2ClientService(O2ClientService o2ClientService) {
        this.o2ClientService = o2ClientService;
    }

    @Override
    public ProviderUserDetails validate(String otac, String phoneNumber) {
        return o2ClientService.getUserDetails(otac, phoneNumber);
    }
}
