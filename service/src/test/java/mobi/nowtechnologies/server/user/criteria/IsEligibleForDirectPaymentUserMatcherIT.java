package mobi.nowtechnologies.server.user.criteria;

import com.google.common.collect.Sets;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.shared.enums.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/10/2014
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/dao-test.xml", "/META-INF/service-test.xml", "/META-INF/shared.xml"})
public class IsEligibleForDirectPaymentUserMatcherIT {
    private static final mobi.nowtechnologies.server.persistence.domain.UserStatus LIMITED = new mobi.nowtechnologies.server.persistence.domain.UserStatus(mobi.nowtechnologies.server.persistence.domain.UserStatus.LIMITED);
    private static final HashSet<String> DIRECT_PAYMENT_TYPES = Sets.newHashSet(PaymentDetails.O2_PSMS_TYPE, PaymentDetails.VF_PSMS_TYPE);
    private static final Tariff TARIFF = Tariff._4G;
    private static final ProviderType PROVIDER = ProviderType.O2;
    private static final Contract CONTRACT = Contract.PAYG;
    private static final SegmentType SEGMENT = SegmentType.BUSINESS;
    @Autowired
    private PaymentPolicyRepository paymentPolicyRepository;

    @Autowired
    private CommunityRepository communityRepository;

    private IsEligibleForDirectPaymentUserMatcher isEligibleForDirectPaymentUserMatcher;
    private Community o2;
    private PaymentPolicy paymentPolicy;
    private UserGroup userGroup;

    @Before
    public void setUp() throws Exception {
        isEligibleForDirectPaymentUserMatcher = new IsEligibleForDirectPaymentUserMatcher(paymentPolicyRepository, DIRECT_PAYMENT_TYPES);
        initCommunityAndUserGroup();
        createAndSavePaymentPolicy();
    }

    @After
    public void tearDown() throws Exception {
        paymentPolicyRepository.delete(paymentPolicy);
    }

    @Test
    public void testMatch() throws Exception {
        User user = new User();
        user.setTariff(TARIFF);
        user.setUserGroup(userGroup);
        user.setProvider(PROVIDER);
        user.setContract(CONTRACT);
        user.setSegment(SEGMENT);

        assertThat(isEligibleForDirectPaymentUserMatcher.match(user), is(true));
    }

    @Test
    public void testNotMatchNullUserGroup() throws Exception {
        User user = new User();
        user.setTariff(TARIFF);
        user.setUserGroup(null);
        user.setProvider(PROVIDER);
        user.setContract(CONTRACT);
        user.setSegment(SEGMENT);

        assertThat(isEligibleForDirectPaymentUserMatcher.match(user), is(false));
    }

    @Test
    public void testNotMatchWrongProviderType() throws Exception {
        User user = new User();
        user.setTariff(TARIFF);
        user.setUserGroup(userGroup);
        user.setProvider(ProviderType.NON_O2);
        user.setContract(CONTRACT);
        user.setSegment(SEGMENT);

        assertThat(isEligibleForDirectPaymentUserMatcher.match(user), is(false));
    }

    @Test
    public void testNotMatchWrongContract() throws Exception {
        User user = new User();
        user.setTariff(TARIFF);
        user.setUserGroup(userGroup);
        user.setProvider(PROVIDER);
        user.setContract(Contract.PAYM);
        user.setSegment(SEGMENT);

        assertThat(isEligibleForDirectPaymentUserMatcher.match(user), is(false));
    }

    @Test
    public void testNotMatchWrongSegment() throws Exception {
        User user = new User();
        user.setTariff(TARIFF);
        user.setUserGroup(userGroup);
        user.setProvider(PROVIDER);
        user.setContract(CONTRACT);
        user.setSegment(SegmentType.CONSUMER);

        assertThat(isEligibleForDirectPaymentUserMatcher.match(user), is(false));
    }

    private void createAndSavePaymentPolicy() {
        paymentPolicy = new PaymentPolicy();
        paymentPolicy.setTariff(TARIFF);
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setCommunity(o2);
        paymentPolicy.setProvider(PROVIDER);
        paymentPolicy.setSegment(SEGMENT);
        paymentPolicy.setPaymentType(PaymentDetails.O2_PSMS_TYPE);
        paymentPolicy.setContract(CONTRACT);
        paymentPolicy.setSubcost(BigDecimal.ONE);
        paymentPolicy.withOnline(true);
        paymentPolicyRepository.save(paymentPolicy);
    }

    private void initCommunityAndUserGroup() {
        o2 = communityRepository.findByRewriteUrlParameter("o2");
        userGroup = new UserGroup();
        userGroup.setCommunity(o2);
    }
}
