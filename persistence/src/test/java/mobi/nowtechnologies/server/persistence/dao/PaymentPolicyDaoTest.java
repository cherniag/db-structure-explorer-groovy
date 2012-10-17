package mobi.nowtechnologies.server.persistence.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.PaymentSystem;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class <code>PaymentPolicyDaoTest</code> contains tests for the class <code>{@link PaymentPolicyDao}</code>.
 *
 * @generatedBy CodePro at 20.10.11 10:32
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@Ignore
public class PaymentPolicyDaoTest {
	
	@Resource(name = "persistence.PaymentPolicyDao")
	private PaymentPolicyDao paymentPolicyDao;

	/**
	 * Run the List<PaymentPolicy> getPaymentPoliciesGroupdeByPaymentType(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 20.10.11 10:32
	 */
	@Test
	public void testGetPaymentPoliciesGroupdeByPaymentType_communityNameIsEmpty()
		throws Exception {
		String communityName = "";

		List<PaymentPolicy> result = paymentPolicyDao.getPaymentPoliciesGroupdeByPaymentType(communityName);
		assertNotNull(result);
	}

	/**
	 * Run the List<PaymentPolicy> getPaymentPoliciesGroupdeByPaymentType(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 20.10.11 10:32
	 */
	@Test
	public void testGetPaymentPoliciesGroupdeByPaymentType_paymentPoliciesExsist()
		throws Exception {
		String communityName = "Now Music";

		List<PaymentPolicy> paymentPolicies = paymentPolicyDao.getPaymentPoliciesGroupdeByPaymentType(communityName);
		assertNotNull(paymentPolicies);
		
		Set<String> paymentTypes = new HashSet<String>();
		
		for (PaymentPolicy paymentPolicy : paymentPolicies) {
			String paymetType= paymentPolicy.getPaymentType();
			
			assertTrue(!paymentTypes.contains(paymetType));
			paymentTypes.add(paymetType);
		}
	}

	/**
	 * Run the List<PaymentPolicy> getPaymentPoliciesGroupdeByPaymentType(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 20.10.11 10:32
	 */
	@Test(expected = mobi.nowtechnologies.server.persistence.dao.PersistenceException.class)
	public void testGetPaymentPoliciesGroupdeByPaymentType_3()
		throws Exception {
		String communityName = null;

		List<PaymentPolicy> result = paymentPolicyDao.getPaymentPoliciesGroupdeByPaymentType(communityName);
	}
	
	@Test
	public void testGetPaymentPolicy_1() throws Exception {
		
		Integer operatorId=1;
		Integer communityId=5;
		PaymentSystem paymentSystem = PaymentSystem.Mig; 
		
		String paymentType = UserRegInfo.PaymentType.PREMIUM_USER;
		
		//PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentSystem, communityId);
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
		assertNotNull(paymentPolicy);
		
		assertEquals(communityId, paymentPolicy.getCommunityId());
		assertEquals(paymentSystem, paymentPolicy.getPaymentType());
		assertEquals(operatorId, paymentPolicy.getOperatorId());
	}
	
	@Test
	public void testGetPaymentPolicy_2() throws Exception {
		
		Integer communityId=5;
		PaymentSystem paymentSystem = PaymentSystem.PayPal; 
		String paymentType = UserRegInfo.PaymentType.PAY_PAL;
		
		//PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentSystem, communityId);
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
		assertNotNull(paymentPolicy);
		
		assertEquals(communityId, paymentPolicy.getCommunityId());
		assertEquals(paymentSystem, paymentPolicy.getPaymentType());
		assertEquals(null, paymentPolicy.getOperatorId());
	}
	
	@Test
	public void testGetPaymentPolicy_3() throws Exception {
		
		Integer communityId=5;
		PaymentSystem paymentSystem = PaymentSystem.SagePay; 
		String paymentType = UserRegInfo.PaymentType.CREDIT_CARD;
		
		//PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentSystem, communityId);
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
		assertNotNull(paymentPolicy);
		
		assertEquals(communityId, paymentPolicy.getCommunityId());
		assertEquals(paymentSystem, paymentPolicy.getPaymentType());
		assertEquals(null, paymentPolicy.getOperatorId());
	}

}