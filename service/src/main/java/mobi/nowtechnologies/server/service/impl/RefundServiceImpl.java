package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Refund;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.RefundRepository;
import mobi.nowtechnologies.server.service.RefundService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActionReason;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.USER_DOWNGRADED_TARIFF;
import static mobi.nowtechnologies.server.shared.enums.ActionReason.VIDEO_AUDIO_FREE_TRIAL_ACTIVATION;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Titov Mykhaylo (titov) 15.07.13 18:57
 */
public class RefundServiceImpl implements RefundService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefundServiceImpl.class);

    RefundRepository refundRepository;

    public void setRefundRepository(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Refund logSkippedBoughtPeriod(User userWithOldTariffOnOldBoughtPeriod, ActionReason actionReason) {
        final Refund resultRefund;
        if (USER_DOWNGRADED_TARIFF.equals(actionReason)) {
            resultRefund = logSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G(userWithOldTariffOnOldBoughtPeriod, actionReason);
        }
        else if (VIDEO_AUDIO_FREE_TRIAL_ACTIVATION.equals(actionReason)) {
            resultRefund = logSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio(userWithOldTariffOnOldBoughtPeriod, actionReason);
        }
        else {
            throw new IllegalArgumentException("Unknown refund direction [" + actionReason + "]");
        }
        return resultRefund;
    }

    private Refund logSkippedAudioBoughtPeriodOnTariffMigrationFrom3GTo4GVideoAudio(User userOnOldBoughtPeriod, ActionReason actionReason) {
        Refund resultRefund = Refund.nullObject();

        boolean isOnAudioBoughtPeriod = userOnOldBoughtPeriod.isOnAudioBoughtPeriod();

        if (isOnAudioBoughtPeriod) {
            LOGGER.info("Attempt to log about skipping Audio bought period [{}] on subscription migration to 4G Video Audio. The nextSubPayment was [{}]", isOnAudioBoughtPeriod,
                        userOnOldBoughtPeriod.getNextSubPayment());

            resultRefund = log(userOnOldBoughtPeriod, actionReason);
        }
        else {
            LOGGER.info("Skip logging about skipped bought period because the following conditions are not met: the user is on Audio bought period [{}], user migrates on 4G Video Audio Subscription",
                        isOnAudioBoughtPeriod);
        }
        return resultRefund;
    }

    private Refund logSkippedVideoAudioBoughtPeriodOnTariffMigrationFrom4GTo3G(User userWithOldTariffOnOldBoughtPeriod, ActionReason actionReason) {
        Refund resultRefund = Refund.nullObject();
        Tariff olUserTariff = userWithOldTariffOnOldBoughtPeriod.getTariff();

        boolean isOn4gVideoAudioBoughtPeriod = userWithOldTariffOnOldBoughtPeriod.isOn4GVideoAudioBoughtPeriod();

        if (_4G.equals(olUserTariff) && isOn4gVideoAudioBoughtPeriod) {
            LOGGER.info("Attempt to log about skipping Video Audio bought period [{}] on tariff migration from 4G to 3G. The nextSubPayment was [{}]", isOn4gVideoAudioBoughtPeriod,
                        userWithOldTariffOnOldBoughtPeriod.getNextSubPayment());

            resultRefund = log(userWithOldTariffOnOldBoughtPeriod, actionReason);
        }
        else {
            LOGGER.info("Skip logging about skipped bought period because the following condition are not met: the old tariff [{}] is 4G, new tariff is 3G, user is Audio Video bought period [{}]",
                        olUserTariff, isOn4gVideoAudioBoughtPeriod);
        }

        return resultRefund;
    }

    private Refund log(User user, ActionReason actionReason) {
        Refund refund = new Refund();
        refund.user = user;
        refund.paymentDetails = user.getLastSuccessfulPaymentDetails();
        refund.logTimeMillis = Utils.getEpochMillis();
        refund.nextSubPaymentMillis = user.getNextSubPaymentAsDate().getTime();
        refund.actionReason = actionReason;

        refund = refundRepository.save(refund);

        LOGGER.info("Attempt to log data for refunding [{}]", refund);
        return refund;
    }
}
