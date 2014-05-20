package mobi.nowtechnologies.server.service.impl.details;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.VFOtacValidationService;

import javax.annotation.Resource;

/**
 * Created by Oleg Artomov on 5/15/2014.
 */
public class VfNzProviderDetailsExtractor implements ProviderDetailsExtractor {

    @Resource
    private VFOtacValidationService vfOtacValidationService;

    @Override
    public ProviderUserDetails getUserDetails(String otac, String phoneNumber, Community community) {
        return vfOtacValidationService.validate(otac, phoneNumber, community);
    }
}
