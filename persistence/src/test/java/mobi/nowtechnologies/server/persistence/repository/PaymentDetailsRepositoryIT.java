package mobi.nowtechnologies.server.persistence.repository;

import javax.annotation.Resource;

import mobi.nowtechnologies.server.persistence.dao.EntityDao;
import mobi.nowtechnologies.server.persistence.domain.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(defaultRollback = true)
public class PaymentDetailsRepositoryIT {
	
	@Resource(name = "paymentDetailsRepository")
	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;
		
	private PayPalPaymentDetails getPaymentDetails(String billingAgreement) {
		PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails();
		paymentDetails.setBillingAgreementTxId(billingAgreement); 
		paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
		paymentDetails.setMadeRetries(0);
		paymentDetails.setRetriesOnError(3);
		paymentDetails.setCreationTimestampMillis(System.currentTimeMillis());
		paymentDetails.setActivated(false);
		return paymentDetails;
	}
	
	@Test
	public void savePaymentDetailsWithChangesToUser() {
		
		User user = new User();
			user.setUserName("hello@user.com");
			user.setCity("Kiev");
		entityDao.saveEntity(user);
		
		user.setCity("Lugansk");
		PayPalPaymentDetails paymentDetails = getPaymentDetails("2345-2345-2345-23452-2345");
		paymentDetails.setOwner(user);
		
		paymentDetailsRepository.save(paymentDetails);
		
		
		assertNotNull(paymentDetails.getI());
		assertEquals("Lugansk", user.getCity());
	}
	
	/**
	 * Adding new payment details to user should disable old one and add a new one with activated equals to true
	 */
	@Test
	public void addingNewPaymentDetailsAndToserWithExistingPaymentDetails() {
		User user = new User();
		user.setUserName("hello@user.com");
		user.setCity("Kiev");
			entityDao.saveEntity(user);
			
		PayPalPaymentDetails paymentDetails = getPaymentDetails("2345-2345-2345-23452-2345");
			paymentDetails.setActivated(true);
			paymentDetails.setOwner(user);
			paymentDetailsRepository.save(paymentDetails);
		
		assertEquals(1, user.getPaymentDetailsList().size());
			
		PayPalPaymentDetails newPaymentDetails = getPaymentDetails("1111-2345-2345-23452-2345");
		newPaymentDetails.setActivated(true);
		user.getCurrentPaymentDetails().setActivated(false);
		newPaymentDetails.setOwner(user);
		newPaymentDetails = (PayPalPaymentDetails) paymentDetailsRepository.save(newPaymentDetails);
			
		assertEquals(2, user.getPaymentDetailsList().size());
		for (PaymentDetails pd : user.getPaymentDetailsList()) {
			if ("2345-2345-2345-23452-2345".equals(((PayPalPaymentDetails)pd).getBillingAgreementTxId())) {
				assertEquals(false, pd.isActivated());
			} else {
				assertEquals(true, pd.isActivated());
			}
		}
	}
}