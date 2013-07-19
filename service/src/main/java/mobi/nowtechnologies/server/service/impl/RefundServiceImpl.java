package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Refund;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.RefundRepository;
import mobi.nowtechnologies.server.service.RefundService;
import mobi.nowtechnologies.server.shared.Utils;
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

//    @Transactional(propagation = Propagation.REQUIRED)
 //   public Refund logOnTariffMigration(User user) {
//        Refund resultDataToDoRefund = Refund.nullObject();
//        if(user.hasTariff(newUserTariff)){
//            if (user.getLastSuccessfulPaymentDetails().getPaymentPolicy().getContentCategory().equals(PaymentPolicy.VIDEO_AND_AUDIO)){
//
//            if (user.isUnsubscribedWithFullAccess()){
//                resultDataToDoRefund = logUnSubscribeData(user);
//            }else{
//                LOGGER.info("Don't logging data for refunding 'case of no remaining subscription days");
//            }
//        }else{
//            LOGGER.info("Don't logging data for refunding 'case of no tariff migration");
//        }
//        }
//        return resultDataToDoRefund;
//    }

    public Refund logOnTariffMigration(User user) {
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private Refund logUnSubscribeData(User user) {
        Refund refund = new Refund();
        refund.user = user;
        refund.paymentDetails = user.getCurrentPaymentDetails();
        refund.logTimeMillis = Utils.getEpochMillis();
        refund.nextSubPaymentMillis = user.getNextSubPaymentAsDate().getTime();

        refund = refundRepository.save(refund);

        LOGGER.info("Attempt to log data for refunding [{}]", refund);
        return refund;
    }
}
