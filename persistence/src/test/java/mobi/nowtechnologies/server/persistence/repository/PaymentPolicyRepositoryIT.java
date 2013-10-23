package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static mobi.nowtechnologies.server.shared.enums.MediaType.*;

/**
 * User: Titov Mykhaylo (titov)
 * 12.07.13 10:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml" })
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentPolicyRepositoryIT {

    @Resource(name = "paymentPolicyRepository")
    PaymentPolicyRepository paymentPolicyRepository;

    @Resource(name = "communityRepository")
    CommunityRepository communityRepository;

    @Test
    public void testSave_Success() {
        PaymentPolicy paymentPolicy = createPaymentPolicyWithCommunity().withMediaType(AUDIO);

        PaymentPolicy actualPaymentPolicy = paymentPolicyRepository.save(paymentPolicy);

        validate(paymentPolicy, actualPaymentPolicy);
    }

    @Test
    public void testFindOne_Success() {
        PaymentPolicy paymentPolicy = createPaymentPolicyWithCommunity().withMediaType(AUDIO);

        paymentPolicy = paymentPolicyRepository.save(paymentPolicy);

        PaymentPolicy actualPaymentPolicy = paymentPolicyRepository.findOne(paymentPolicy.getId());

        validate(paymentPolicy, actualPaymentPolicy);
    }

    PaymentPolicy createPaymentPolicyWithCommunity() {
        Community community = communityRepository.findByRewriteUrlParameter("o2");

        PaymentPolicy paymentPolicy = PaymentPolicyFactory.createPaymentPolicy();
        paymentPolicy.setTariff(Tariff._3G);
        paymentPolicy.setCommunity(community);
        return paymentPolicy;
    }

    void validate(PaymentPolicy paymentPolicy, PaymentPolicy actualPaymentPolicy) {
        assertNotNull(actualPaymentPolicy);
        assertNotNull(actualPaymentPolicy.getId());

        assertEquals(paymentPolicy.getAppStoreProductId(), actualPaymentPolicy.getAppStoreProductId());
        assertEquals(paymentPolicy.getContentCategory(), actualPaymentPolicy.getContentCategory());
        assertEquals(paymentPolicy.getContentDescription(), actualPaymentPolicy.getContentDescription());
        assertEquals(paymentPolicy.getContentType(), actualPaymentPolicy.getContentType());
        assertEquals(paymentPolicy.getCurrencyISO(), actualPaymentPolicy.getCurrencyISO());
        assertEquals(paymentPolicy.getSegment(), actualPaymentPolicy.getSegment());
        assertEquals(paymentPolicy.getShortCode(), actualPaymentPolicy.getShortCode());
        assertEquals(paymentPolicy.getSubMerchantId(), actualPaymentPolicy.getSubMerchantId());
        assertEquals(paymentPolicy.getProvider(), actualPaymentPolicy.getProvider());
        assertEquals(paymentPolicy.getTariff(), actualPaymentPolicy.getTariff());
        assertEquals(paymentPolicy.getContract(), actualPaymentPolicy.getContract());
        assertEquals(paymentPolicy.getSubcost(), actualPaymentPolicy.getSubcost());
        assertEquals(paymentPolicy.getSubweeks(), actualPaymentPolicy.getSubweeks());
        assertEquals(paymentPolicy.getPaymentType(), actualPaymentPolicy.getPaymentType());

        Community actualCommunity = actualPaymentPolicy.getCommunity();
        assertNotNull(actualCommunity);
        assertEquals(paymentPolicy.getCommunity().getId(), actualCommunity.getId());
    }
}
