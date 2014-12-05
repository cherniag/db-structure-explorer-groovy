package mobi.nowtechnologies.server.dto.transport;

import mobi.nowtechnologies.server.persistence.domain.User;

/**
 * Created by zam on 11/25/2014.
 */
public interface ReferralContextDtoFactory {
    ReferralContextDto getReferralContextDto(User user);
}
