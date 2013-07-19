package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Refund;
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

    @Resource(name = "refundRepository")
    RefundRepository refundRepository;

    private Refund actualRefund;
    private Refund refund;

    private void prepareDataForSave() {
        refund = new Refund();
        refund.nextSubPaymentMillis = Long.MAX_VALUE;
        refund.paymentDetails = O2PSMSPaymentDetailsFactory.createO2PSMSPaymentDetails();
        refund.logTimeMillis = Utils.getEpochMillis();
        refund.user = UserFactory.createUser();

        refund.user = userRepository.save(refund.user);

        refund.paymentDetails = paymentDetailsRepository.save(refund.paymentDetails);
    }

    private void validateSaving() {
        assertNotNull(actualRefund);
        assertEquals(refund.getPaymentDetailsId(), actualRefund.getPaymentDetailsId());
        assertEquals(refund.getUserId(), actualRefund.getUserId());
        assertEquals(refund.logTimeMillis, actualRefund.logTimeMillis);
        assertEquals(refund.nextSubPaymentMillis, actualRefund.nextSubPaymentMillis);
    }

    private void validateFindOne() {
        validateSaving();
    }


    private void prepareDateForFindOne() {
        prepareDataForSave();

        actualRefund = refundRepository.save(refund);
    }

    @Test
    public void testSave_Success(){
        prepareDataForSave();

        actualRefund = refundRepository.save(refund);

        validateSaving();
    }

    @Test
    public void testFindOne_Success(){
        prepareDateForFindOne();

        actualRefund = refundRepository.findOne(actualRefund.id);

        validateFindOne();
    }
}
