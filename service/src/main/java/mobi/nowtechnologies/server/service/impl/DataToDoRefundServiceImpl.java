package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.DataToDoRefund;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DataToDoRefundRepository;
import mobi.nowtechnologies.server.service.DataToDoRefundService;
import mobi.nowtechnologies.server.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 18:57
 */
public class DataToDoRefundServiceImpl implements DataToDoRefundService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DataToDoRefundServiceImpl.class);

    DataToDoRefundRepository dataToDoRefundRepository;

    public void setDataToDoRefundRepository(DataToDoRefundRepository dataToDoRefundRepository) {
        this.dataToDoRefundRepository = dataToDoRefundRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DataToDoRefund logOnTariffMigration(User user) {
        DataToDoRefund resultDataToDoRefund = DataToDoRefund.nullObject();
        if(!user.isTariffChanged()){
            if (user.isUnsubscribedWithFullAccess()){
                resultDataToDoRefund = logUnSubscribeData(user);
            }else{
                LOGGER.info("Don't logging data for refunding 'case of no remaining subscription days");
            }
        }else{
            LOGGER.info("Don't logging data for refunding 'case of no tariff migration");
        }
        return resultDataToDoRefund;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private DataToDoRefund logUnSubscribeData(User user) {
        DataToDoRefund dataToDoRefund = new DataToDoRefund();
        dataToDoRefund.user = user;
        dataToDoRefund.paymentDetails = user.getCurrentPaymentDetails();
        dataToDoRefund.logTimeMillis = Utils.getEpochMillis();
        dataToDoRefund.nextSubPaymentMillis = user.getNextSubPaymentAsDate().getTime();

        dataToDoRefund = dataToDoRefundRepository.save(dataToDoRefund);

        LOGGER.info("Attempt to log data for refunding [{}]", dataToDoRefund);
        return dataToDoRefund;
    }
}
