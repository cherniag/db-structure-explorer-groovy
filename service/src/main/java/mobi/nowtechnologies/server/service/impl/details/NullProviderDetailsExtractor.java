package mobi.nowtechnologies.server.service.impl.details;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;

import static mobi.nowtechnologies.server.dto.ProviderUserDetails.NULL_PROVIDER_USER_DETAILS;

/**
 * Created by Oleg Artomov on 5/15/2014.
 */
public class NullProviderDetailsExtractor implements ProviderDetailsExtractor {

    @Override
    public ProviderUserDetails getUserDetails(String otac, String phoneNumber, Community community) {
        return NULL_PROVIDER_USER_DETAILS;
    }
}
