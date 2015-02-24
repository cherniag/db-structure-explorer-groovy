package mobi.nowtechnologies.server.service.payment.impl;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.persistence.repository.*;
import mobi.nowtechnologies.server.service.payment.PayPalPaymentService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.List;

import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" })
public class PayPalPaymentServiceImpIT {

    @Resource(name = "service.payPalPaymentService")
    private PayPalPaymentService payPalPaymentService;
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
    private SubmittedPaymentRepository submittedPaymentRepository;

    @Test
    public void startPayment() throws Exception {
        String communityRewriteUrl = "mtv1";
        User user = createUser("userName2", "deviceUID2", communityRewriteUrl);
        PaymentPolicy paymentPolicy = createPaymentPolicy(communityRewriteUrl, PaymentPolicyType.RECURRENT);
        PaymentDetails paymentDetails = createPaymentDetails(user, paymentPolicy);
        PendingPayment pendingPayment = createPendingPayment(user, paymentDetails);

        payPalPaymentService.startPayment(pendingPayment);

        User found = userRepository.findOne(user.getId());
        assertTrue(found.isSubscribedStatus());
        assertTrue(found.getNextSubPayment() > (Utils.getEpochSeconds() + 6 * 24 * 60 * 60));
        assertEquals(PaymentDetails.PAYPAL_TYPE, found.getLastSubscribedPaymentSystem());

        PaymentDetails foundPaymentDetails = found.getCurrentPaymentDetails();
        assertTrue(foundPaymentDetails.isActivated());
        assertNull(foundPaymentDetails.getDescriptionError());
        assertEquals(PaymentDetailsStatus.SUCCESSFUL, foundPaymentDetails.getLastPaymentStatus());
        assertTrue(foundPaymentDetails.getDisableTimestampMillis() == 0);
        assertEquals(paymentPolicy.getId(), foundPaymentDetails.getPaymentPolicy().getId());

        List<SubmittedPayment> submittedPayments = submittedPaymentRepository.findByUserIdAndPaymentStatus(Lists.newArrayList(user.getId()), Lists.newArrayList(PaymentDetailsStatus.SUCCESSFUL));
        assertEquals(1, submittedPayments.size());
    }

    private PendingPayment createPendingPayment(User user, PaymentDetails paymentDetails){
        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(paymentDetails);
        pendingPayment.setType(PaymentDetailsType.FIRST);
        pendingPayment.setPaymentSystem(PaymentDetails.PAYPAL_TYPE);
        pendingPayment.setPeriod(paymentDetails.getPaymentPolicy().getPeriod());
        pendingPayment.setAmount(paymentDetails.getPaymentPolicy().getSubcost());
        pendingPayment.setCurrencyISO(paymentDetails.getPaymentPolicy().getCurrencyISO());
        return pendingPaymentRepository.saveAndFlush(pendingPayment);
    }

    private User createUser(String userName, String deviceUID, String communityRewriteUrl) {
        User user = new User();
        user.setDeviceUID(deviceUID);
        user.setUserName(userName);
        UserGroup userGroup = userGroupRepository.findByCommunityRewriteUrl(communityRewriteUrl);
        user.setUserGroup(userGroup);
        user.setDeviceType(DeviceTypeDao.getAndroidDeviceType());
        user.setStatus(UserStatusDao.getSubscribedUserStatus());
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        user = userRepository.saveAndFlush(user);
        return user;
    }

    private PaymentDetails createPaymentDetails(User user, PaymentPolicy paymentPolicy){
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails();
        paymentDetails.setActivated(true);
        paymentDetails.setOwner(user);
        paymentDetails.setPaymentPolicy(paymentPolicy);
        paymentDetails.setBillingAgreementTxId("tx");
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        paymentDetails = paymentDetailsRepository.save(paymentDetails);
        user.setCurrentPaymentDetails(paymentDetails);
        userRepository.saveAndFlush(user);
        return paymentDetails;
    }

    private PaymentPolicy createPaymentPolicy(String communityRewriteUrl, PaymentPolicyType paymentPolicyType){
        PaymentPolicy paymentPolicy = new PaymentPolicy();
        paymentPolicy.setCurrencyISO("GBP");
        paymentPolicy.setPaymentType(UserRegInfo.PaymentType.PAY_PAL);
        paymentPolicy.setSubcost(BigDecimal.valueOf(1.29));
        paymentPolicy.setPeriod(new Period(WEEKS, 1));
        paymentPolicy.setMediaType(MediaType.AUDIO);
        paymentPolicy.setPaymentPolicyType(paymentPolicyType);
        paymentPolicy.setTariff(Tariff._3G);
        paymentPolicy.setCommunity(communityRepository.findByRewriteUrlParameter(communityRewriteUrl));
        return paymentPolicyRepository.saveAndFlush(paymentPolicy);
    }
}