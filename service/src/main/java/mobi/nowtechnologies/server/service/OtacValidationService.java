package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.dto.ProviderUserDetails;

/**
 * User: Titov Mykhaylo (titov)
 * 27.09.13 10:18
 */
public interface OtacValidationService {

    ProviderUserDetails validate(String otac, String phoneNumber);
}
