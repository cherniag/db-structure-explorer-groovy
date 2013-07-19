package mobi.nowtechnologies.server.service.impl;

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
    public Refund logOnTariffMigration(User user) {
        Refund resultRefund = Refund.nullObject();
        Tariff newUserTariff = Tariff._3G;
        Tariff olUserTariff = Tariff._4G;

        if (Tariff._4G.equals(newUserTariff) && Tariff._3G.equals(newUserTariff) && user.isOn4GVideoAudioBoughtPeriod()) {
            LOGGER.info("Attempt to log about skipping Video Audio bought period on tariff migration from 4G to 3G. The nextSubPayment was [{]]", user.getNextSubPayment());

            logSkippedBoughtPeriod(user);
        } else if (Tariff._4G.equals(newUserTariff) && user.isOnAudioBoughtPeriod() && user.has4GVideoAudioSubscription()) {
            LOGGER.info("Attempt to log about skipping Audio bought period on tariff migration from [{}] to 4G Video Audio subscription. The nextSubPayment was [{]]", olUserTariff, user.getNextSubPayment());

            logSkippedBoughtPeriod(user);
        } else {
            LOGGER.info("Skip logging about skipped bought period because none ot the following conditions are not met: 1. The new tariff [{}] is 4G, old tariff [{}] is 3G, user is Audio Video bought period; 2. The new tariff [{}] is 3G, user is on Audio bought period, user has Audio Video Subscription", newUserTariff, olUserTariff, newUserTariff);
        }
        return resultRefund;
    }

    @Transactional(propagation = Propagation.REQUIRED)
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
