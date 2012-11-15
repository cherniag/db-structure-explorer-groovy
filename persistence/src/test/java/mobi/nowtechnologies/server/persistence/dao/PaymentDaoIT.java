package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static mobi.nowtechnologies.server.shared.AppConstants.NOT_AVAILABLE;
import static mobi.nowtechnologies.server.shared.AppConstants.STATUS_PENDING;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The class <code>PaymentDaoTest</code> contains tests for the class <code>{@link PaymentDao}</code>.
 *
 * @generatedBy CodePro at 21.10.11 15:25
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentDaoIT {
	
	@Resource(name = "persistence.PaymentDao")
	private PaymentDao paymentDao;
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;

	@Test
	public void getUsersForPendingPayment_Successful() {
		PaymentPolicy paymentPolicy = new PaymentPolicy();
			paymentPolicy.setCurrencyISO("GBP");
			paymentPolicy.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);
			paymentPolicy.setSubcost(BigDecimal.TEN);
			paymentPolicy.setSubweeks((byte)0);
		entityDao.saveEntity(paymentPolicy);
		
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL);
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL);
		createUser(paymentPolicy, PaymentDetailsStatus.AWAITING);
		createUser(paymentPolicy, PaymentDetailsStatus.ERROR);
		createUser(paymentPolicy, PaymentDetailsStatus.EXTERNAL_ERROR);
		createUser(paymentPolicy, PaymentDetailsStatus.NONE);
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL);
		createUser(paymentPolicy, PaymentDetailsStatus.NONE);
		
		List<User> pendingPayments = paymentDao.getUsersForPendingPayment();
		
		Assert.assertNotNull(pendingPayments);
		Assert.assertEquals(5, pendingPayments.size());
	}
	
	private User createUser(PaymentPolicy paymentPolicy, PaymentDetailsStatus lastPaymentStatus) {
		User user = new User();
			user.setUserName(UUID.randomUUID().toString());
			SagePayCreditCardPaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
				currentPaymentDetails.setPaymentPolicy(paymentPolicy);
				currentPaymentDetails.setLastPaymentStatus(lastPaymentStatus);
				currentPaymentDetails.setReleased(false);
				currentPaymentDetails.setActivated(true);
			entityDao.saveEntity(currentPaymentDetails);
			user.addPaymentDetails(currentPaymentDetails);
			
		return (User) entityDao.updateEntity(user);
	}
	
	/**
	 * Run the boolean isUserAlreadyPaidSuccessfully(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 21.10.11 15:25
	 */
	@Test
	@Ignore
	public void testIsUserAlreadyPaidSuccessfully_1()
		throws Exception {
		int userID = -1;

		boolean result = paymentDao.isUserAlreadyPaidSuccessfully(userID);

		assertFalse(result);
	}

	/**
	 * Run the boolean isUserAlreadyPaidSuccessfully(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 21.10.11 15:25
	 */
	@Test
	@Ignore
	public void testIsUserAlreadyPaidSuccessfully_2()
		throws Exception {
		int userID = 9666;
		
		boolean result = paymentDao.isUserAlreadyPaidSuccessfully(userID);

		assertFalse(result);
		
		Payment payment = new CreditCardPayment();
		payment.setExternalTxCode(NOT_AVAILABLE);
		payment.setExternalSecurityKey(NOT_AVAILABLE);
		payment.setInternalTxCode(NOT_AVAILABLE);
		payment.setExternalAuthCode(NOT_AVAILABLE);
		payment.setStatus(AppConstants.STATUS_OK);
		payment.setStatusDetail(STATUS_PENDING);
		payment.setTimestamp(getEpochSeconds());
		payment.setUserUID(userID);
		payment.setDescription("description");
		payment.setRelatedPayment(0);
		payment.setTxType(PaymentDao.TxType.RELEASE.getCode());
		payment.setAmount(0);
		payment.setSubweeks((byte)0);
		
		entityDao.saveEntity(payment);

		result = paymentDao.isUserAlreadyPaidSuccessfully(userID);
		assertTrue(result);
	}

	/**
	 * Run the boolean isUserAlreadyPaidSuccessfully(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 21.10.11 15:25
	 */
	@Test
	@Ignore
	public void testIsUserAlreadyPaidSuccessfully_3()
		throws Exception {
		int userID = 9666;
		boolean result = paymentDao.isUserAlreadyPaidSuccessfully(userID);

		assertFalse(result);
		
		Payment payment = new PremiumUserPayment();
		payment.setExternalTxCode(NOT_AVAILABLE);
		payment.setExternalSecurityKey(NOT_AVAILABLE);
		payment.setInternalTxCode(NOT_AVAILABLE);
		payment.setExternalAuthCode(NOT_AVAILABLE);
		payment.setStatus(AppConstants.STATUS_OK);
		payment.setStatusDetail(STATUS_PENDING);
		payment.setTimestamp(getEpochSeconds());
		payment.setUserUID(userID);
		payment.setDescription("description");
		payment.setRelatedPayment(0);
		payment.setTxType(PaymentDao.TxType.PAYMENT.getCode());
		payment.setAmount(0);
		payment.setSubweeks((byte)0);
		
		entityDao.saveEntity(payment);

		result = paymentDao.isUserAlreadyPaidSuccessfully(userID);
		assertTrue(result);
	}
	
	@Test
	@Ignore
	public void testIsUserAlreadyPaidSuccessfully_4()
		throws Exception {
		int userID = 9666;
		boolean result = paymentDao.isUserAlreadyPaidSuccessfully(userID);

		assertFalse(result);
		
		Payment payment = new PayPalPayment();
		payment.setExternalTxCode(NOT_AVAILABLE);
		payment.setExternalSecurityKey(NOT_AVAILABLE);
		payment.setInternalTxCode(NOT_AVAILABLE);
		payment.setExternalAuthCode(NOT_AVAILABLE);
		payment.setStatus(AppConstants.STATUS_OK);
		payment.setStatusDetail(STATUS_PENDING);
		payment.setTimestamp(getEpochSeconds());
		payment.setUserUID(userID);
		payment.setDescription("description");
		payment.setRelatedPayment(0);
		payment.setTxType(PaymentDao.TxType.PAYMENT.getCode());
		payment.setAmount(0);
		payment.setSubweeks((byte)0);
		
		entityDao.saveEntity(payment);

		result = paymentDao.isUserAlreadyPaidSuccessfully(userID);
		assertTrue(result);
	}
}