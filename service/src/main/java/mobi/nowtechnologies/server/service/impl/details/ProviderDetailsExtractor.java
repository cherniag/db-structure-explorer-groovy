package mobi.nowtechnologies.server.service.impl.details;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;
import mobi.nowtechnologies.server.persistence.domain.Community;

/**
 * Created by Oleg Artomov on 5/15/2014.
 */
public interface ProviderDetailsExtractor {

    ProviderUserDetails getUserDetails(String otac, String phoneNumber, Community community);
}
