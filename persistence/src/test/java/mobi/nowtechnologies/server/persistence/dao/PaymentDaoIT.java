package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.shared.AppConstants;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = false)
@Transactional
public class PaymentDaoIT {
	
	@Resource(name = "persistence.PaymentDao")
	private PaymentDao paymentDao;
	
	@Resource(name = "persistence.EntityDao")
	private EntityDao entityDao;
	

	@Test
	public void getUsersForPendingPayment_Successful() {
		Community community = CommunityDao.getCommunity("CN Commercial Beta");
		
		PaymentPolicy paymentPolicy = new PaymentPolicy();
		paymentPolicy.setAvailableInStore(true);
		paymentPolicy.setCommunity(community);
		paymentPolicy.setCurrencyISO("GBP");
		paymentPolicy.setPaymentType(UserRegInfo.PaymentType.CREDIT_CARD);
		paymentPolicy.setSubcost(BigDecimal.TEN);
		paymentPolicy.setSubweeks((byte) 0);
		entityDao.saveEntity(paymentPolicy);
		
		byte chartsNowUserGroupId = 3;
		byte o2UserGroupId = 7;
		
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL, chartsNowUserGroupId, 1359547315, null);
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL, chartsNowUserGroupId, 1359547315, "o2");
		createUser(paymentPolicy, PaymentDetailsStatus.AWAITING, chartsNowUserGroupId, 1359547315, null);
		createUser(paymentPolicy, PaymentDetailsStatus.ERROR, chartsNowUserGroupId, 1359547315, "");
		createUser(paymentPolicy, PaymentDetailsStatus.NONE, chartsNowUserGroupId, 1359547315, "");
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL, chartsNowUserGroupId, 1359547315, "");
		createUser(paymentPolicy, PaymentDetailsStatus.NONE, chartsNowUserGroupId, 1359547315, "o2");
		createUser(paymentPolicy, PaymentDetailsStatus.EXTERNAL_ERROR, chartsNowUserGroupId, 1359547315,"");
		createUser(paymentPolicy, null, chartsNowUserGroupId, 1359547315, "o2");
			
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL, o2UserGroupId, Utils.getEpochSeconds() + 60*60, null);
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL, o2UserGroupId, Utils.getEpochSeconds() + 60*60, "o2");
		createUser(paymentPolicy, PaymentDetailsStatus.AWAITING, o2UserGroupId, Utils.getEpochSeconds() + 60*60, null);
		createUser(paymentPolicy, PaymentDetailsStatus.ERROR, o2UserGroupId, Utils.getEpochSeconds() + 60*60, "");
		createUser(paymentPolicy, PaymentDetailsStatus.NONE, o2UserGroupId, Utils.getEpochSeconds() + 60*60, "");
		createUser(paymentPolicy, PaymentDetailsStatus.SUCCESSFUL, o2UserGroupId, Utils.getEpochSeconds() + 86400 + 60 * 60, "");
		createUser(paymentPolicy, PaymentDetailsStatus.NONE, o2UserGroupId, Utils.getEpochSeconds() + 60*60, "o2");
		createUser(paymentPolicy, PaymentDetailsStatus.EXTERNAL_ERROR, o2UserGroupId, Utils.getEpochSeconds() + 60*60, "");
		createUser(paymentPolicy, null, o2UserGroupId, Utils.getEpochSeconds() + 60*60, "o2");
		createUser(paymentPolicy, PaymentDetailsStatus.NONE, o2UserGroupId, Utils.getEpochSeconds()- 60 * 60, "");
		
		List<User> pendingPayments = paymentDao.getUsersForPendingPayment();
		
		Assert.assertNotNull(pendingPayments);
		Assert.assertEquals(8, pendingPayments.size());
	}
	
	private User createUser(PaymentPolicy paymentPolicy, PaymentDetailsStatus lastPaymentStatus, byte usergroupId, int nextSubPayment, String provider) {
		UserGroup userGroup = entityDao.findById(UserGroup.class, usergroupId);
		
		User user = new User();
		user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
		user.setUserName(UUID.randomUUID().toString());
		user.setUserGroup(userGroup);
		user.setLastDeviceLogin(55);
		user.setStatus(UserStatusDao.getLimitedUserStatus());
		user.setNextSubPayment(nextSubPayment);
		user.setProvider(provider);
		entityDao.saveEntity(user);

		if (lastPaymentStatus!=null){
			SagePayCreditCardPaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
			currentPaymentDetails.setPaymentPolicy(paymentPolicy);
			currentPaymentDetails.setLastPaymentStatus(lastPaymentStatus);
			currentPaymentDetails.setReleased(false);
			currentPaymentDetails.setActivated(true);
			currentPaymentDetails.setOwner(user);
			entityDao.saveEntity(currentPaymentDetails);

			user.setCurrentPaymentDetails(currentPaymentDetails);
			user = entityDao.updateEntity(user);
		}

		return user;
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