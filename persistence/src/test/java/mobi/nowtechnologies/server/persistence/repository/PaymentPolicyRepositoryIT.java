package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import static mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails.O2_PSMS_TYPE;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.PAYPAL_TYPE;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.GOOGLE_PLUS;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.BUSINESS;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * User: Titov Mykhaylo (titov) 12.07.13 10:08
 */
public class PaymentPolicyRepositoryIT extends AbstractRepositoryIT {

    @Resource(name = "paymentPolicyRepository")
    PaymentPolicyRepository paymentPolicyRepository;

    @Resource(name = "communityRepository")
    CommunityRepository communityRepository;
    private PaymentPolicy paymentPolicy;
    private Community o2Community;

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

    @Test
    public void shouldReturnOneDefaultO2PsmsPaymentPolicy() {
        //given
        paymentPolicy = paymentPolicyRepository
            .save(createPaymentPolicyWithCommunity().withPaymentType(O2_PSMS_TYPE).withProvider(O2).withMediaType(AUDIO).withContract(PAYG).withSegment(BUSINESS).withTariff(_3G).withDefault(true));

        //when
        PaymentPolicy actualPaymentPolicy = paymentPolicyRepository.findDefaultO2PsmsPaymentPolicy(o2Community, O2, BUSINESS, PAYG, _3G);

        //then
        assertNotNull(actualPaymentPolicy);
        assertEquals(paymentPolicy.getId(), actualPaymentPolicy.getId());
    }

    @Test
    public void shouldReturnOneDefaultO2PsmsPaymentPolicyWithNullProviderSegmentContract() {
        //given
        paymentPolicy = paymentPolicyRepository
            .save(createPaymentPolicyWithCommunity().withPaymentType(O2_PSMS_TYPE).withProvider(null).withMediaType(AUDIO).withContract(null).withSegment(null).withTariff(_3G).withDefault(true));

        //when
        PaymentPolicy actualPaymentPolicy = paymentPolicyRepository.findDefaultO2PsmsPaymentPolicy(o2Community, O2, BUSINESS, PAYG, _3G);

        //then
        assertNotNull(actualPaymentPolicy);
        assertEquals(paymentPolicy.getId(), actualPaymentPolicy.getId());
    }

    @Test
    public void testGetPaymentPoliciesForOnlinePolicy() {
        //given
        paymentPolicy = paymentPolicyRepository
            .save(createPaymentPolicyWithCommunity().withPaymentType(PAYPAL_TYPE).withProvider(GOOGLE_PLUS).withMediaType(AUDIO).withContract(null).withSegment(null).withTariff(_3G).withDefault(true))
            .withOnline(true);
        PaymentPolicy result = paymentPolicyRepository.findPaymentPolicy(o2Community, GOOGLE_PLUS, PAYPAL_TYPE);
        assertEquals(result, paymentPolicy);
    }

    @Test
    public void testGetPaymentPoliciesForNotOnlinePolicy() {
        //given
        paymentPolicy = paymentPolicyRepository
            .save(createPaymentPolicyWithCommunity().withPaymentType(PAYPAL_TYPE).withProvider(GOOGLE_PLUS).withMediaType(AUDIO).withContract(null).withSegment(null).withTariff(_3G).withDefault(true))
            .withOnline(false);
        PaymentPolicy result = paymentPolicyRepository.findPaymentPolicy(o2Community, GOOGLE_PLUS, PAYPAL_TYPE);
        assertNull(result);
    }

    @Test
    public void shouldNotFindAppStoreProductIdWithStartDateInTheFutureByCommunityAndAppStoreProductIdIsNotNull() {
        //given
        PaymentPolicy paymentPolicy = createPaymentPolicyWithCommunity();
        paymentPolicy.setAppStoreProductId("appStoreProductId");
        paymentPolicy.setStartDateTime(new Date(Long.MAX_VALUE));
        paymentPolicy.setEndDateTime(new Date(Long.MAX_VALUE));

        paymentPolicyRepository.save(paymentPolicy);

        //when
        List<String> appStoreProductIds = paymentPolicyRepository.findAppStoreProductIdsByCommunityAndAppStoreProductIdIsNotNull(paymentPolicy.getCommunity());

        //then
        for (String appStoreProductId : appStoreProductIds) {
            assertFalse(paymentPolicy.getAppStoreProductId().equals(appStoreProductId));
        }

    }

    PaymentPolicy createPaymentPolicyWithCommunity() {
        o2Community = communityRepository.findByRewriteUrlParameter("o2");

        PaymentPolicy paymentPolicy = PaymentPolicyFactory.paymentPolicyWithDefaultNotNullFields();
        paymentPolicy.setTariff(_3G);
        paymentPolicy.setCommunity(o2Community);
        paymentPolicy.setMediaType(MediaType.AUDIO);
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
        assertEquals(paymentPolicy.getPeriod(), actualPaymentPolicy.getPeriod());
        assertEquals(paymentPolicy.getPaymentType(), actualPaymentPolicy.getPaymentType());

        Community actualCommunity = actualPaymentPolicy.getCommunity();
        assertNotNull(actualCommunity);
        assertEquals(paymentPolicy.getCommunity().getId(), actualCommunity.getId());
    }
}
