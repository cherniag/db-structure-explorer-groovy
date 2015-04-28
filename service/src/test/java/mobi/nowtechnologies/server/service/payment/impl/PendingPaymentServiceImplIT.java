package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.device.domain.DeviceTypeCache;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.PayPalPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
public class PendingPaymentServiceImplIT {

    @Resource
    private UserGroupRepository userGroupRepository;
    @Resource
    private UserRepository userRepository;
    @Resource
    private PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    private PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    private CommunityRepository communityRepository;
    @Resource
    private PendingPaymentRepository pendingPaymentRepository;
    @Resource
    private PendingPaymentService pendingPaymentService;
    @Resource
    UserStatusRepository userStatusRepository;

    @Before
    public void setUp() throws Exception {
        // disable payment for all users from init.sql
        for (User user : userRepository.findAll()) {
            user.setNextSubPayment(Integer.MAX_VALUE);
            userRepository.save(user);
        }
    }

    @Test
    public void testCreatePendingPaymentsForOneTimePaymentPolicy() throws Exception {
        final String communityRewriteUrl = "mtv1";
        final String userName = Utils.getRandomUUID();
        final String deviceUID = Utils.getRandomUUID();
        User user = createUser(userName, deviceUID, communityRewriteUrl);
        PaymentPolicy paymentPolicy = createPaymentPolicy(communityRewriteUrl, PaymentPolicyType.ONETIME);
        createPaymentDetails(user, paymentPolicy, PaymentDetailsStatus.SUCCESSFUL);

        List<PendingPayment> pendingPayments = pendingPaymentService.createPendingPayments();

        assertTrue(pendingPayments.isEmpty());
        user = userRepository.findOne(user.getId());
        assertFalse(user.getCurrentPaymentDetails().isActivated());
        assertEquals("One time payment policy", user.getCurrentPaymentDetails().getDescriptionError());

        List<PendingPayment> foundPendingPayment = pendingPaymentRepository.findByUserId(user.getId());
        assertTrue(foundPendingPayment.isEmpty());
    }

    @Test
    public void testCreatePendingPaymentsForRecurrentPaymentPolicy() throws Exception {
        final String communityRewriteUrl = "mtv1";
        final String userName = Utils.getRandomUUID();
        final String deviceUID = Utils.getRandomUUID();
        User user = createUser(userName, deviceUID, communityRewriteUrl);
        PaymentPolicy paymentPolicy = createPaymentPolicy(communityRewriteUrl, PaymentPolicyType.RECURRENT);
        createPaymentDetails(user, paymentPolicy, PaymentDetailsStatus.NONE);

        List<PendingPayment> pendingPayments = pendingPaymentService.createPendingPayments();

        assertFalse(pendingPayments.isEmpty());
        user = userRepository.findOne(user.getId());
        assertTrue(user.getCurrentPaymentDetails().isActivated());

        PendingPayment pendingPayment = pendingPayments.get(0);
        assertEquals(PaymentDetailsStatus.AWAITING, pendingPayment.getPaymentDetails().getLastPaymentStatus());
        assertEquals(user.getId(), pendingPayment.getUser().getId());
        assertEquals(user.getCurrentPaymentDetails().getI(), pendingPayment.getPaymentDetails().getI());

        List<PendingPayment> foundPendingPayment = pendingPaymentRepository.findByUserId(user.getId());
        assertFalse(foundPendingPayment.isEmpty());
    }

    private User createUser(String userName, String deviceUID, String communityRewriteUrl) {
        User user = new User();
        user.setDeviceUID(deviceUID);
        user.setUserName(userName);
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeCache.getAndroidDeviceType());
        user.setStatus(userStatusRepository.findByName(UserStatusType.LIMITED.name()));
        user.setLastDeviceLogin(1);
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        user = userRepository.saveAndFlush(user);
        return user;
    }

    private PaymentDetails createPaymentDetails(User user, PaymentPolicy paymentPolicy, PaymentDetailsStatus lastPaymentStatus) {
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails();
        paymentDetails.setActivated(true);
        paymentDetails.setOwner(user);
        paymentDetails.setPaymentPolicy(paymentPolicy);
        paymentDetails.setBillingAgreementTxId("tx");
        paymentDetails.setLastPaymentStatus(lastPaymentStatus);
        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        user.setCurrentPaymentDetails(paymentDetails);
        userRepository.saveAndFlush(user);
        return paymentDetails;
    }


    private PaymentPolicy createPaymentPolicy(String communityRewriteUrl, PaymentPolicyType paymentPolicyType) {
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setCurrencyISO("GBP");
        paymentPolicy.setPaymentType(UserRegInfo.PaymentType.PAY_PAL);
        paymentPolicy.setSubcost(BigDecimal.valueOf(1.29));
        paymentPolicy.setPeriod(new Period(WEEKS, 1));
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setPaymentPolicyType(paymentPolicyType);
        paymentPolicy.setTariff(Tariff._3G);
        paymentPolicy.setCommunity(communityRepository.findByRewriteUrlParameter(communityRewriteUrl));
        paymentPolicy.setStartDateTime(new Date(0L));
        paymentPolicy.setEndDateTime(new Date(Long.MAX_VALUE));
        return paymentPolicyRepository.saveAndFlush(paymentPolicy);
    }
}
