package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.OtacValidationService;
import mobi.nowtechnologies.server.service.VFOtacValidationService;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;

/**
 * User: Titov Mykhaylo (titov)
 * 27.09.13 14:42
 */
public class OtacValidationServiceImpl implements OtacValidationService{

    private O2ProviderService o2ProviderService;
    private VFOtacValidationService vfOtacValidationService;

    public void setO2ProviderService(O2ProviderService o2ProviderService) {
        this.o2ProviderService = o2ProviderService;
    }

    public void setVfOtacValidationService(VFOtacValidationService vfOtacValidationService) {
        this.vfOtacValidationService = vfOtacValidationService;
    }

    @Override
    public ProviderUserDetails validate(String otac, String phoneNumber, Community community) {
        if (community.isO2Community()) return o2ProviderService.getUserDetails(otac, phoneNumber, community);
        else if (community.isVFNZCommunity()) return vfOtacValidationService.validate(otac, phoneNumber, community);
        else throw new UnsupportedOperationException("Unknown community [" + community + "]");
    }
}
