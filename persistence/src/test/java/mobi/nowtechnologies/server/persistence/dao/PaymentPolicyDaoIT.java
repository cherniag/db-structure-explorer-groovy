package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;

import javax.annotation.Resource;

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
@ContextConfiguration(locations = {"/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentPolicyDaoIT {

    @Resource(name = "persistence.PaymentPolicyDao")
    private PaymentPolicyDao paymentPolicyDao;

    @Test
    public void testGetPaymentPolicy_2() throws Exception {

        Integer communityId = 5;
        String paymentType = UserRegInfo.PaymentType.PAY_PAL;

        PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
        assertNotNull(paymentPolicy);

        assertEquals(communityId, paymentPolicy.getCommunityId());
        assertEquals(UserRegInfo.PaymentType.PAY_PAL, paymentPolicy.getPaymentType());
        assertEquals(null, paymentPolicy.getOperatorId());
    }

    @Test
    public void testGetPaymentPolicy_3() throws Exception {

        Integer communityId = 5;
        String paymentType = UserRegInfo.PaymentType.CREDIT_CARD;

        PaymentPolicy paymentPolicy = paymentPolicyDao.getPaymentPolicy(0, paymentType, communityId);
        assertNotNull(paymentPolicy);

        assertEquals(communityId, paymentPolicy.getCommunityId());
        assertEquals(UserRegInfo.PaymentType.CREDIT_CARD, paymentPolicy.getPaymentType());
        assertEquals(null, paymentPolicy.getOperatorId());
    }

}