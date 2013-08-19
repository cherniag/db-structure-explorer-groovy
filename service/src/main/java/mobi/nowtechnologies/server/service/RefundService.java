package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Refund;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.ActionReason;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 16:17
 */
public interface RefundService {
    Refund logSkippedBoughtPeriod(User userWithOldTariffOnOldBoughtPeriod, ActionReason actionReason);
}
