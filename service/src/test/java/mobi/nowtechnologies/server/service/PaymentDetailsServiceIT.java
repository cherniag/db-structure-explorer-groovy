package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.MigPaymentDetailsFactory;
import mobi.nowtechnologies.server.persistence.domain.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentDetailsServiceIT {

	@Resource(name = "service.PaymentDetailsService")
	private PaymentDetailsService paymentDetailsService;

	@Resource(name = "service.EntityService")
	private EntityService entityService;

	@Test
	public void test_findPaymentDetails_Success() {
		final String phoneNumber = "00447585927651";
		final String migOperator = "MIG00VU";

		final String migPhoneNumber = migOperator + "." + phoneNumber;
		final String o2PsmsPhoneNumber = "+" + phoneNumber;

		MigPaymentDetails migPaymentDetails = MigPaymentDetailsFactory.createMigPaymentDetails();
		migPaymentDetails.setMigPhoneNumber(migPhoneNumber);
		migPaymentDetails.setActivated(true);
		migPaymentDetails.setCreationTimestampMillis(0L);
		migPaymentDetails.setDisableTimestampMillis(0L);
		migPaymentDetails.setMadeRetries(0);
		migPaymentDetails.setRetriesOnError(0);

		entityService.saveEntity(migPaymentDetails);

		O2PSMSPaymentDetails o2PSMSPaymentDetails = new O2PSMSPaymentDetails();
		o2PSMSPaymentDetails.setPhoneNumber(o2PsmsPhoneNumber);
		o2PSMSPaymentDetails.setActivated(true);
		o2PSMSPaymentDetails.setCreationTimestampMillis(0L);
		o2PSMSPaymentDetails.setDisableTimestampMillis(0L);
		o2PSMSPaymentDetails.setMadeRetries(0);
		o2PSMSPaymentDetails.setRetriesOnError(0);

		entityService.saveEntity(o2PSMSPaymentDetails);

		List<PaymentDetails> paymentDetailsList = paymentDetailsService.findPaymentDetails(migOperator, phoneNumber);

		assertNotNull(paymentDetailsList);

		assertEquals(1, paymentDetailsList.size());
			
		assertEquals(migPaymentDetails.getI(), paymentDetailsList.get(0).getI());
		
		paymentDetailsList = paymentDetailsService.findPaymentDetails("o2", phoneNumber);

		assertNotNull(paymentDetailsList);

		assertEquals(1, paymentDetailsList.size());
			
		assertEquals(o2PSMSPaymentDetails.getI(), paymentDetailsList.get(0).getI());
	}

}
