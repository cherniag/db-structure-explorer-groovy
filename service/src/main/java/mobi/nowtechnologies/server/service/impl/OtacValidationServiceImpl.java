package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.OtacValidationService;
import mobi.nowtechnologies.server.service.VFOtacValidationService;

/**
 * User: Titov Mykhaylo (titov)
 * 27.09.13 14:42
 */
public class OtacValidationServiceImpl implements OtacValidationService{

    private O2ClientService o2ClientService;
    private VFOtacValidationService vfOtacValidationService;

    public void setO2ClientService(O2ClientService o2ClientService) {
        this.o2ClientService = o2ClientService;
    }

    public void setVfOtacValidationService(VFOtacValidationService vfOtacValidationService) {
        this.vfOtacValidationService = vfOtacValidationService;
    }

    @Override
    public ProviderUserDetails validate(String otac, String phoneNumber, Community community) {
        if (community.isO2Community()) return o2ClientService.getUserDetails(otac, phoneNumber);
        else if (community.isVFNZCommunity()) return vfOtacValidationService.validate(otac, phoneNumber, community);
        else throw new UnsupportedOperationException("Unknown community [" + community + "]");
    }
}
