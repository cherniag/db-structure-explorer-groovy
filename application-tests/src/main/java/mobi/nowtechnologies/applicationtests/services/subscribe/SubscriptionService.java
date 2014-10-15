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
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.persistence.repository.UserStatusRepository;
import mobi.nowtechnologies.server.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

@Service
public class SubscriptionService {
    private Logger logger = LoggerFactory.getLogger(getClass());

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
    PayPalHttpService payPalHttpService;
    @Resource
    JobService jobService;
    @Resource
    RepeatService repeatService;
    @Resource
    PhoneNumberCreator phoneNumberCreator;

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
    public void pay(final User user) {
        User currentUser = userRepository.findOne(user.getId());
        setNextSubPaymentInThePast(currentUser);
        userRepository.saveAndFlush(currentUser);

        jobService.startPaymentJob();

        Repeatable<User> repeatable = new UserRepeatable(userRepository, currentUser);
        User afterPayment = repeatService.repeat(repeatable);
        assertTrue(afterPayment.getNextSubPayment() > currentUser.getNextSubPayment());
    }

    public void limitAccess(User user){
        setNextSubPaymentInThePast(user);
        setFreeTrialExpireInThePast(user);
        user.setStatus(userStatusRepository.findByName(UserStatus.LIMITED));
        userRepository.save(user);
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if (currentPaymentDetails != null) {
            currentPaymentDetails.setActivated(false);
            currentPaymentDetails.setDisableTimestampMillis(Utils.getEpochMillis());
            currentPaymentDetails.setDescriptionError("Deactivated by FAT");
            paymentDetailsRepository.save(currentPaymentDetails);
        }
    }

    private void setNextSubPaymentInThePast(User user) {
        if (user.getNextSubPayment() > Utils.getEpochSeconds()) {
            user.setNextSubPayment(Utils.getEpochSeconds() - 1);
        }
    }

    private void setFreeTrialExpireInThePast(User user) {
        if (user.getFreeTrialExpiredMillis() > Utils.getEpochMillis()) {
            user.setFreeTrialExpiredMillis(Utils.getEpochMillis() - 1);
        }
    }


}
