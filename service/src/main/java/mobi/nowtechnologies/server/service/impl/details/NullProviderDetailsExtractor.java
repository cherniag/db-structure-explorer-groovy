package mobi.nowtechnologies.server.service.impl.details;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;

/**
 * Created by Oleg Artomov on 5/15/2014.
 */
public class NullProviderDetailsExtractor implements ProviderDetailsExtractor {

    @Override
    public ProviderUserDetails getUserDetails(String otac, String phoneNumber, Community community) {
        return new ProviderUserDetails();
    }
}
