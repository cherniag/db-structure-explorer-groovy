package mobi.nowtechnologies.server.service.impl.details;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;

import javax.annotation.Resource;

/**
 * Created by Oleg Artomov on 5/15/2014.
 */
public class O2ProviderDetailsExtractor implements ProviderDetailsExtractor {

    @Resource
    private O2ProviderService o2ProviderService;

    @Override
    public ProviderUserDetails getUserDetails(String otac, String phoneNumber, Community community) {
        return o2ProviderService.getUserDetails(otac, phoneNumber, community);
    }
}
