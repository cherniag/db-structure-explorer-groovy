package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.Refund;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.RefundRepository;
import mobi.nowtechnologies.server.service.RefundService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 18:57
 */
public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefundServiceImpl.class);

    RefundRepository refundRepository;

    public void setRefundRepository(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Refund logOnTariffMigration(User userWithOldTariffOnOldBoughtPeriod, Tariff newUserTariff, PaymentDetails newPaymentDetails) {
        Refund resultRefund = Refund.nullObject();
        Tariff olUserTariff = userWithOldTariffOnOldBoughtPeriod.getTariff();

        boolean isOn4gVideoAudioBoughtPeriod = userWithOldTariffOnOldBoughtPeriod.isOn4GVideoAudioBoughtPeriod();
        
		if (Tariff._4G.equals(olUserTariff) && Tariff._3G.equals(newUserTariff) && isOn4gVideoAudioBoughtPeriod) {
            LOGGER.info("Attempt to log about skipping Video Audio bought period [{}] on tariff migration from 4G to 3G. The nextSubPayment was [{}]", isOn4gVideoAudioBoughtPeriod, userWithOldTariffOnOldBoughtPeriod.getNextSubPayment());

            logSkippedBoughtPeriod(userWithOldTariffOnOldBoughtPeriod);
        } else {
			boolean isOnAudioBoughtPeriod = userWithOldTariffOnOldBoughtPeriod.isOnAudioBoughtPeriod();
			boolean isNewPaymentDetails4gVideoAudioSubscription = newPaymentDetails.getPaymentPolicy().is4GVideoAudioSubscription();
			if (isOnAudioBoughtPeriod && isNewPaymentDetails4gVideoAudioSubscription) {
			    LOGGER.info("Attempt to log about skipping Audio bought period [{}] on subscription migration to 4G Video Audio [{}]. The nextSubPayment was [{}]", isOnAudioBoughtPeriod, isNewPaymentDetails4gVideoAudioSubscription, userWithOldTariffOnOldBoughtPeriod.getNextSubPayment());

			    logSkippedBoughtPeriod(userWithOldTariffOnOldBoughtPeriod);
			} else {
			    LOGGER.info("Skip logging about skipped bought period because none ot the following conditions are not met: 1. The old tariff [{}] is 4G, new tariff [{}] is 3G, user is Audio Video bought period [{}]; 2. The user is on Audio bought period [{}], user migrates on 4G Video Audio Subscription [{}]", olUserTariff, newUserTariff, isOn4gVideoAudioBoughtPeriod, isOnAudioBoughtPeriod, isNewPaymentDetails4gVideoAudioSubscription);
			}
		}
        return resultRefund;
    }

    private Refund logSkippedBoughtPeriod(User userWithOldTariffOnOldBoughtPeriod) {
        Refund refund = new Refund();
        refund.user = userWithOldTariffOnOldBoughtPeriod;
        refund.paymentDetails = userWithOldTariffOnOldBoughtPeriod.getLastSuccessfulPaymentDetails();
        refund.logTimeMillis = Utils.getEpochMillis();
        refund.nextSubPaymentMillis = userWithOldTariffOnOldBoughtPeriod.getNextSubPaymentAsDate().getTime();

        refund = refundRepository.save(refund);

        LOGGER.info("Attempt to log data for refunding [{}]", refund);
        return refund;
    }
}
