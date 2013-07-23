package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Refund logSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio(User userOnOldBoughtPeriod, PaymentPolicy newPaymentPolicy){
        Refund resultRefund = Refund.nullObject();

        boolean isOnAudioBoughtPeriod = userOnOldBoughtPeriod.isOnAudioBoughtPeriod();
        boolean isNewPaymentDetails4gVideoAudioSubscription = newPaymentPolicy.is4GVideoAudioSubscription();

        if (isOnAudioBoughtPeriod && isNewPaymentDetails4gVideoAudioSubscription) {
            LOGGER.info("Attempt to log about skipping Audio bought period [{}] on subscription migration to 4G Video Audio [{}]. The nextSubPayment was [{}]", isOnAudioBoughtPeriod, isNewPaymentDetails4gVideoAudioSubscription, userOnOldBoughtPeriod.getNextSubPayment());

            resultRefund = logSkippedBoughtPeriod(userOnOldBoughtPeriod);
        } else {
            LOGGER.info("Skip logging about skipped bought period because the following conditions are not met: the user is on Audio bought period [{}], user migrates on 4G Video Audio Subscription [{}]", isOnAudioBoughtPeriod, isNewPaymentDetails4gVideoAudioSubscription);
        }
        return resultRefund;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Refund logSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G(User userWithOldTariffOnOldBoughtPeriod, Tariff newUserTariff){
        Refund resultRefund = Refund.nullObject();
        Tariff olUserTariff = userWithOldTariffOnOldBoughtPeriod.getTariff();

        boolean isOn4gVideoAudioBoughtPeriod = userWithOldTariffOnOldBoughtPeriod.isOn4GVideoAudioBoughtPeriod();

        if (Tariff._4G.equals(olUserTariff) && Tariff._3G.equals(newUserTariff) && isOn4gVideoAudioBoughtPeriod) {
            LOGGER.info("Attempt to log about skipping Video Audio bought period [{}] on tariff migration from 4G to 3G. The nextSubPayment was [{}]", isOn4gVideoAudioBoughtPeriod, userWithOldTariffOnOldBoughtPeriod.getNextSubPayment());

            resultRefund = logSkippedBoughtPeriod(userWithOldTariffOnOldBoughtPeriod);
        } else {
            LOGGER.info("Skip logging about skipped bought period because the following condition are not met: the old tariff [{}] is 4G, new tariff [{}] is 3G, user is Audio Video bought period [{}]", olUserTariff, newUserTariff, isOn4gVideoAudioBoughtPeriod);
        }
        return resultRefund;
    }

    private Refund logSkippedBoughtPeriod(User user) {
        Refund refund = new Refund();
        refund.user = user;
        refund.paymentDetails = user.getLastSuccessfulPaymentDetails();
        refund.logTimeMillis = Utils.getEpochMillis();
        refund.nextSubPaymentMillis = user.getNextSubPaymentAsDate().getTime();

        refund = refundRepository.save(refund);

        LOGGER.info("Attempt to log data for refunding [{}]", refund);
        return refund;
    }
}
