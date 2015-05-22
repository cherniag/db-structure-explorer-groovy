package mobi.nowtechnologies.applicationtests.services.subscribe;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.PhoneNumberCreator;
import mobi.nowtechnologies.applicationtests.services.http.paypal.PayPalHttpService;
import mobi.nowtechnologies.applicationtests.services.job.JobService;
import mobi.nowtechnologies.applicationtests.services.repeat.RepeatService;
import mobi.nowtechnologies.applicationtests.services.repeat.Repeatable;
import mobi.nowtechnologies.applicationtests.services.repeat.UserRepeatable;
import mobi.nowtechnologies.applicationtests.services.ui.WebPortalService;
import mobi.nowtechnologies.common.util.DateTimeUtils;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.SubmittedPaymentRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;

@Service
public class SubscriptionService {

    @Resource(name = "mno.RestTemplate")
    RestTemplate restTemplate;
    @Value("${environment.url}")
    String environmentUrl;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    WebPortalService webPortalService;
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    UserStatusRepository userStatusRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    PayPalHttpService payPalHttpService;
    @Resource
    JobService jobService;
    @Resource
    RepeatService repeatService;
    @Resource
    PhoneNumberCreator phoneNumberCreator;
    @Resource
    PendingPaymentRepository pendingPaymentRepository;
    @Resource
    SubmittedPaymentRepository submittedPaymentRepository;

    private Logger logger = LoggerFactory.getLogger(getClass());

    //
    // API
    //
    //
    // Unsubscribe
    //
    public void unsubscribe(UserDeviceData deviceData, PhoneState phoneState) {
        webPortalService.submitUnsubscribeRequest(deviceData, phoneState);
    }

    @Transactional(value = "applicationTestsTransactionManager")
    public void pay(final User user, Date now) {
        final int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(now.getTime());

        User currentUser = userRepository.findOne(user.getId());
        setNextSubPaymentInThePast(currentUser, seconds);
        userRepository.saveAndFlush(currentUser);

        jobService.startPaymentJob();

        Repeatable<User> repeatable = new UserRepeatable(userRepository, currentUser);
        User afterPayment = repeatService.repeat(repeatable);
        assertTrue(afterPayment.getNextSubPayment() > currentUser.getNextSubPayment());
    }

    public void limitAccess(User user, Date now) {
        final long millis = now.getTime();
        final int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis);

        setNextSubPaymentInThePast(user, seconds);
        setFreeTrialExpireInThePast(user, millis);
        user.setStatus(userStatusRepository.findByName(UserStatus.LIMITED));
        userRepository.save(user);
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (currentPaymentDetails != null) {
            currentPaymentDetails.setActivated(false);
            currentPaymentDetails.setDisableTimestampMillis(millis);
            currentPaymentDetails.setDescriptionError("Deactivated by FAT");
            paymentDetailsRepository.save(currentPaymentDetails);
        }
    }

    private void setNextSubPaymentInThePast(User user, int nowInSeconds) {
        if (user.getNextSubPayment() > nowInSeconds) {
            user.setNextSubPayment(nowInSeconds - 1);
        }
    }

    private void setFreeTrialExpireInThePast(User user, long nowInMillis) {
        if (user.getFreeTrialExpiredMillis() > nowInMillis) {
            user.setFreeTrialExpiredMillis(nowInMillis - 1);
        }
    }

    @Transactional(value = "applicationTestsTransactionManager")
    public void setCurrentPaymentDetailsStatus(User user, PaymentDetailsStatus paymentDetailsStatus) {
        List<PaymentPolicy> paymentPolicies = paymentPolicyRepository.findPaymentPolicies(user.getCommunity(),
                                                                                          user.getProvider(),
                                                                                          user.getSegment(),
                                                                                          user.getContract(),
                                                                                          user.getTariff(),
                                                                                          Arrays.asList(AUDIO, VIDEO_AND_AUDIO));

        final PaymentPolicy paymentPolicy = paymentPolicies.get(0);

        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (currentPaymentDetails == null) {
            currentPaymentDetails = new PaymentDetails();
            currentPaymentDetails.setOwner(user);
            currentPaymentDetails.setPaymentPolicy(paymentPolicy);
            user.setCurrentPaymentDetails(currentPaymentDetails);
        }
        currentPaymentDetails.setActivated(true);
        currentPaymentDetails.setLastPaymentStatus(paymentDetailsStatus);
        paymentDetailsRepository.save(currentPaymentDetails);
        userRepository.save(user);

        if(paymentDetailsStatus == PaymentDetailsStatus.AWAITING) {
            PendingPayment pendingPayment = createPendingPayment(user, PaymentDetailsType.FIRST, paymentPolicy);
            pendingPaymentRepository.save(pendingPayment);
        }

        if(paymentDetailsStatus == PaymentDetailsStatus.ERROR) {
            SubmittedPayment pendingPayment = createSubmittedPayment(user, paymentPolicy);
            submittedPaymentRepository.save(pendingPayment);
        }
    }

    private PendingPayment createPendingPayment(User user, PaymentDetailsType type, PaymentPolicy paymentPolicy) {
        final Date now = new Date();
        final Date expired = DateUtils.addHours(now, 1);

        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();

        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setPaymentDetails(currentPaymentDetails);
        pendingPayment.setPaymentSystem(currentPaymentDetails.getPaymentType());
        pendingPayment.setAmount(paymentPolicy.getSubcost());
        pendingPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
        pendingPayment.setPeriod(paymentPolicy.getPeriod());
        pendingPayment.setUser(user);
        pendingPayment.setExternalTxId("");
        pendingPayment.setTimestamp(now.getTime());
        pendingPayment.setExpireTimeMillis(expired.getTime());
        pendingPayment.setType(type);

        return pendingPayment;
    }

    private SubmittedPayment createSubmittedPayment(User user, PaymentPolicy paymentPolicy) {
        final Date now = new Date();
        final Date expireTime = DateUtils.addHours(now, 1);

        SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setNextSubPayment(DateTimeUtils.millisToIntSeconds(expireTime.getTime()));
        submittedPayment.setExternalTxId("ex-tx-" + UUID.randomUUID().toString());
        submittedPayment.setAppStoreOriginalTransactionId("orig-tx-" + UUID.randomUUID().toString());
        submittedPayment.setStatus(PaymentDetailsStatus.SUCCESSFUL);
        submittedPayment.setUser(user);
        submittedPayment.setTimestamp(now.getTime());
        submittedPayment.setAmount(paymentPolicy.getSubcost());
        submittedPayment.setCurrencyISO(paymentPolicy.getCurrencyISO());
        submittedPayment.setPaymentSystem(PaymentDetails.ITUNES_SUBSCRIPTION);
        submittedPayment.setBase64EncodedAppStoreReceipt("app-" + UUID.randomUUID().toString());
        submittedPayment.setPeriod(paymentPolicy.getPeriod());
        submittedPayment.setPaymentPolicy(paymentPolicy);
        return submittedPayment;
    }

}
