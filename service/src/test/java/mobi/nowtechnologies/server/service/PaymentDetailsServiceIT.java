package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.VFPSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentDetailsServiceIT {

    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource
    private UserRepository userRepository;

    @Test
    public void test_findActivatedPaymentDetails_Success() {
        final String phoneNumber = "00447585927651";
        final String migOperator = "MIG00VU";

        final String migPhoneNumber = migOperator + "." + phoneNumber;
        final String o2PsmsPhoneNumber = "+" + phoneNumber;

        User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user = userRepository.saveAndFlush(user);

        MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
        migPaymentDetails.setMigPhoneNumber(migPhoneNumber);
        migPaymentDetails.setActivated(true);
        migPaymentDetails.setCreationTimestampMillis(0L);
        migPaymentDetails.setDisableTimestampMillis(0L);
        migPaymentDetails.withMadeRetries(0);
        migPaymentDetails.setRetriesOnError(0);
        migPaymentDetails.setOwner(user);
        paymentDetailsRepository.save(migPaymentDetails);

        O2PSMSPaymentDetails o2PSMSPaymentDetails = new O2PSMSPaymentDetails();
        o2PSMSPaymentDetails.setPhoneNumber(o2PsmsPhoneNumber);
        o2PSMSPaymentDetails.setActivated(true);
        o2PSMSPaymentDetails.setCreationTimestampMillis(0L);
        o2PSMSPaymentDetails.setDisableTimestampMillis(0L);
        o2PSMSPaymentDetails.withMadeRetries(0);
        o2PSMSPaymentDetails.setRetriesOnError(0);
        o2PSMSPaymentDetails.setOwner(user);
        paymentDetailsRepository.save(o2PSMSPaymentDetails);

        VFPSMSPaymentDetails vfpsmsPaymentDetails = new VFPSMSPaymentDetails();
        vfpsmsPaymentDetails.setPhoneNumber(o2PsmsPhoneNumber);
        vfpsmsPaymentDetails.setActivated(true);
        vfpsmsPaymentDetails.setCreationTimestampMillis(0L);
        vfpsmsPaymentDetails.setDisableTimestampMillis(0L);
        vfpsmsPaymentDetails.withMadeRetries(0);
        vfpsmsPaymentDetails.setRetriesOnError(0);
        vfpsmsPaymentDetails.setOwner(user);
        paymentDetailsRepository.save(vfpsmsPaymentDetails);

        List<PaymentDetails> paymentDetailsList = paymentDetailsRepository.findActivatedPaymentDetails(migOperator, phoneNumber);

        assertNotNull(paymentDetailsList);

        assertEquals(1, paymentDetailsList.size());

        assertEquals(migPaymentDetails.getI(), paymentDetailsList.get(0).getI());

        paymentDetailsList = paymentDetailsRepository.findActivatedPaymentDetails("o2", phoneNumber);

        assertNotNull(paymentDetailsList);

        assertEquals(1, paymentDetailsList.size());

        assertEquals(o2PSMSPaymentDetails.getI(), paymentDetailsList.get(0).getI());

        paymentDetailsList = paymentDetailsRepository.findActivatedPaymentDetails("vf", phoneNumber);

        assertNotNull(paymentDetailsList);

        assertEquals(1, paymentDetailsList.size());

        assertEquals(vfpsmsPaymentDetails.getI(), paymentDetailsList.get(0).getI());
    }

}
