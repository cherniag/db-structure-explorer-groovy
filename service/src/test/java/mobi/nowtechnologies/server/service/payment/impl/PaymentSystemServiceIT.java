package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatusType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SagePayCreditCardPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserGroupRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.service.payment.PaymentTestUtils;
import mobi.nowtechnologies.server.service.payment.SagePayPaymentService;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.payment.response.SagePayResponse;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.server.shared.enums.DurationUnit.WEEKS;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class PaymentSystemServiceIT {

    @Resource(name = "service.sagePayPaymentService")
    private SagePayPaymentService paymentService;

    @Resource
    UserStatusRepository userStatusRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    PaymentDetailsRepository paymentDetailsRepository;

    @Resource
    PendingPaymentRepository pendingPaymentRepository;

    @Resource
    UserGroupRepository userGroupRepository;

    @Resource
    CommunityRepository communityRepository;

    @Test
    public void commitSagePayPayment_Successful() throws Exception {
        // Preparations for test
        PaymentSystemResponse response =
            new SagePayResponse(PaymentTestUtils.createBasicResponse(HttpServletResponse.SC_OK, "StatusDetail=0000 : The Authorisation was Successful.\nTxAuthNo=12123123\nAVSCV2=SECURITY CODE " +
                                                                                                "MATCH " +
                                                                                                "ONLY\n3DSecureStatus=NOTCHECKED\nVPSTxId=123123123\nStatus=OK\nAddressResult=NOTMATCHED\nPostCodeResult=MATCHED\nCV2Result=MATCHED\nSecurityKey=123234234\nVPSProtocol=2.23"));

        User user = new User();
        user.setUserName(UUID.randomUUID().toString());
        user.setStatus(userStatusRepository.findByName(UserStatusType.LIMITED.name()));
        user.setActivationStatus(ActivationStatus.ACTIVATED);
        SagePayCreditCardPaymentDetails currentPaymentDetails = new SagePayCreditCardPaymentDetails();
        currentPaymentDetails.setLastPaymentStatus(PaymentDetailsStatus.NONE);
        currentPaymentDetails.setReleased(true);
        userRepository.save(user);
        currentPaymentDetails.setOwner(user);
        paymentDetailsRepository.save(currentPaymentDetails);

        userRepository.save(user);

        UserGroup userGroup = UserGroupFactory.createUserGroup(communityRepository.findByRewriteUrlParameter(Community.O2_COMMUNITY_REWRITE_URL));
        user.setUserGroup(userGroup);

        userGroupRepository.save(userGroup);
        userRepository.save(user);

        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setAmount(new BigDecimal(10));
        pendingPayment.setCurrencyISO("GBP");
        pendingPayment.setInternalTxId(UUID.randomUUID().toString());
        pendingPayment.setPaymentSystem(PaymentDetails.SAGEPAY_CREDITCARD_TYPE);
        pendingPayment.setPeriod(new Period().withDuration(2).withDurationUnit(WEEKS));
        pendingPayment.setTimestamp(System.currentTimeMillis());
        pendingPayment.setUser(user);
        pendingPayment.setPaymentDetails(currentPaymentDetails);
        pendingPaymentRepository.save(pendingPayment);

        // Invocation of test method
        SubmittedPayment submittedPayment = paymentService.commitPayment(pendingPayment, response);

        // Asserts
        Assert.assertNotNull(submittedPayment);
        Assert.assertEquals(pendingPayment.getAmount(), submittedPayment.getAmount());
        Assert.assertEquals(null, submittedPayment.getDescriptionError());
        Assert.assertEquals(PaymentDetailsStatus.SUCCESSFUL, submittedPayment.getStatus());
    }
}