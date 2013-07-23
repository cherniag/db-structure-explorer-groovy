package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.Refund;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 16:17
 */
public interface RefundService {

    Refund logSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio(User userWithOldTariffOnOldBoughtPeriod, PaymentPolicy newPaymentPolicy);

    Refund logSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G(User userWithOldTariffOnOldBoughtPeriod, Tariff newUserTariff);
}
