package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

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

	@Test
	public void testGetPaymentPoliciesGroupdeByPaymentType_paymentPoliciesExsist() throws Exception {
        Community community = new Community().withRewriteUrl("Now Music");

		List<PaymentPolicy> paymentPolicies = paymentPolicyDao.getPaymentPoliciesGroupdeByPaymentType(community);
		assertNotNull(paymentPolicies);
		
		Set<String> paymentTypes = new HashSet<String>();
		
		for (PaymentPolicy paymentPolicy : paymentPolicies) {
			String paymetType= paymentPolicy.getPaymentType();
			
			assertTrue(!paymentTypes.contains(paymetType));
			paymentTypes.add(paymetType);
		}
	}

	@Test
	public void testGetPaymentPolicy_1() throws Exception {
		
		Integer operatorId=1;
		Integer communityId=5;
		String paymentSystem = "Mig";
		
		String paymentType = UserRegInfo.PaymentType.PREMIUM_USER;
		
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
		assertNotNull(paymentPolicy);
		
		assertEquals(communityId, paymentPolicy.getCommunityId());
		assertEquals(paymentSystem, paymentPolicy.getPaymentType());
		assertEquals(operatorId, paymentPolicy.getOperatorId());
	}
	
	@Test
	public void testGetPaymentPolicy_2() throws Exception {
		
		Integer communityId=5;
		String  paymentSystem = "PayPal";
		String paymentType = UserRegInfo.PaymentType.PAY_PAL;
		
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
		assertNotNull(paymentPolicy);
		
		assertEquals(communityId, paymentPolicy.getCommunityId());
		assertEquals(paymentSystem, paymentPolicy.getPaymentType());
		assertEquals(null, paymentPolicy.getOperatorId());
	}
	
	@Test
	public void testGetPaymentPolicy_3() throws Exception {
		
		Integer communityId=5;
		String  paymentSystem = "SagePay";
		String paymentType = UserRegInfo.PaymentType.CREDIT_CARD;
		
		PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
		assertNotNull(paymentPolicy);
		
		assertEquals(communityId, paymentPolicy.getCommunityId());
		assertEquals(paymentSystem, paymentPolicy.getPaymentType());
		assertEquals(null, paymentPolicy.getOperatorId());
	}

}