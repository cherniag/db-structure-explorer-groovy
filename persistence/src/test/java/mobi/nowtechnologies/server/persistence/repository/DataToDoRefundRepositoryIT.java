package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.DataToDoRefund;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;

import static junit.framework.Assert.*;

/**
 * User: Titov Mykhaylo (titov)
 * 16.07.13 10:28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
public class DataToDoRefundRepositoryIT {

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Resource(name = "paymentDetailsRepository")
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource(name = "dataToDoRefundRepository")
    DataToDoRefundRepository dataToDoRefundRepository;

    private DataToDoRefund actualDataToDoRefund;
    private DataToDoRefund dataToDoRefund;

    private void prepareDataForSave() {
        dataToDoRefund = new DataToDoRefund();
        dataToDoRefund.nextSubPaymentMillis = Long.MAX_VALUE;
        dataToDoRefund.paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        dataToDoRefund.logTimeMillis = Utils.getEpochMillis();
        dataToDoRefund.user = UserFactory.createUser();

        dataToDoRefund.user = userRepository.save(dataToDoRefund.user);

        dataToDoRefund.paymentDetails = paymentDetailsRepository.save(dataToDoRefund.paymentDetails);
    }

    private void validateSaving() {
        assertNotNull(actualDataToDoRefund);
        assertEquals(dataToDoRefund.getPaymentDetailsId(), actualDataToDoRefund.getPaymentDetailsId());
        assertEquals(dataToDoRefund.getUserId(), actualDataToDoRefund.getUserId());
        assertEquals(dataToDoRefund.logTimeMillis, actualDataToDoRefund.logTimeMillis);
        assertEquals(dataToDoRefund.nextSubPaymentMillis, actualDataToDoRefund.nextSubPaymentMillis);
    }

    private void validateFindOne() {
        validateSaving();
    }


    private void prepareDateForFindOne() {
        prepareDataForSave();

        actualDataToDoRefund = dataToDoRefundRepository.save(dataToDoRefund);
    }

    @Test
    public void testSave_Success(){
        prepareDataForSave();

        actualDataToDoRefund = dataToDoRefundRepository.save(dataToDoRefund);

        validateSaving();
    }

    @Test
    public void testFindOne_Success(){
        prepareDateForFindOne();

        actualDataToDoRefund = dataToDoRefundRepository.findOne(actualDataToDoRefund.id);

        validateFindOne();
    }
}
