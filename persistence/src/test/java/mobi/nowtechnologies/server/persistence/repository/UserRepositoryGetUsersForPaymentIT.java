package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import static mobi.nowtechnologies.server.persistence.domain.PaymentDetailsFactory.paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3;
import static mobi.nowtechnologies.server.persistence.domain.PaymentDetailsFactory.paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful;
import static mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory.paymentPolicyWithDefaultNotNullFields;
import static mobi.nowtechnologies.server.persistence.domain.UserFactory.userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;

import javax.annotation.Resource;

import java.util.List;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.junit.*;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

//  @author Titov Mykhaylo (titov) 07.03.14 20:50
public class UserRepositoryGetUsersForPaymentIT extends AbstractRepositoryIT {

    @Resource
    UserRepository userRepository;
    @Resource
    UserGroupRepository userGroupRepository;
    @Resource
    PaymentDetailsRepository paymentDetailsRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    CommunityRepository communityRepository;

    private Pageable pageable = new PageRequest(0, 35, Sort.Direction.ASC, "nextSubPayment");

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentMinusAdvancedPaymentSecondsIsInTheFuture() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(4, pageable);

        //then
        List<User> users = usersPage.getContent();
        assertThat(users, is(empty()));
    }

    @Test
    public void shouldNotFindFreeTrialUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentMinusAdvancedPaymentSecondsIsNow() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        int nextSubPayment = 10;
        User user = userRepository.save(creteUser().withNextSubPayment(nextSubPayment)
                                                                                                                                   .withFreeTrialExpiredMillis(SECONDS.toMillis(nextSubPayment)));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(null));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(5, pageable);

        //then
        List<User> users = usersPage.getContent();
        assertThat(users, is(empty()));
    }

    @Test
    public void shouldNotFindFreeTrialUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentMinusAdvancedPaymentSecondsInThePastButNextSubPaymentInTheFuture() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        int nextSubPayment = 10;
        User user = userRepository.save(creteUser().withNextSubPayment(nextSubPayment)
                                                                                                                                   .withFreeTrialExpiredMillis(SECONDS.toMillis(nextSubPayment)));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(null));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(6, pageable);

        //then
        List<User> users = usersPage.getContent();
        assertThat(users, is(empty()));
    }

    @Test
    public void shouldNotFindFreeTrialUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentIsNow() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        int nextSubPayment = 10;
        User user = userRepository.save(creteUser().withNextSubPayment(nextSubPayment)
                                                                                                                                   .withFreeTrialExpiredMillis(SECONDS.toMillis(nextSubPayment)));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(null));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(10, pageable);

        //then
        List<User> users = usersPage.getContent();
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentMinusAdvancedPaymentSecondsIsNow() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        int nextSubPayment = 10;
        User user = userRepository.save(creteUser().withNextSubPayment(nextSubPayment)
                                                                                                                                   .withFreeTrialExpiredMillis(SECONDS.toMillis(nextSubPayment - 1)));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(6, pageable);

        //then
        List<User> users = usersPage.getContent();
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentMinusAdvancedPaymentSecondsIsInThePast() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user =
            userRepository.save(creteUser().withNextSubPayment(10).withFreeTrialExpiredMillis(5000L).withTariff(_3G));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(9, pageable);

        //then
        List<User> users = usersPage.getContent();

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIs0AndNextSubPaymentIsInTheFuture() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(0));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(9, pageable);

        //then
        List<User> users = usersPage.getContent();

        assertThat(users, is(empty()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIs0AndNextSubPaymentIsInThePast() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(0));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersPage = userRepository.findUsersForPendingPayment(11, pageable);

        //then
        List<User> users = usersPage.getContent();

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    private User creteUser() {
        User user = userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED();
        user.setUserGroup(userGroupRepository.findOne(7));
        return user;
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs0AndNextSubPaymentIsInTheFuture() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(0).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(6, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs1AndNextSubPaymentIsInTheFuture() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(9, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users, is(empty()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs1AndNextSubPaymentIsInThePast() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10).withTariff(_3G));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(11, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs1AndNextSubPaymentIsInThePastAndLastSuccessfulPaymentDetailsAndCurrentAreNotTheSame() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10).withTariff(_3G));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        PaymentDetails lastSuccessfulPaymentDetails = paymentDetailsRepository.save(new PaymentDetails().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(lastSuccessfulPaymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(11, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndNextSubPaymentIsInThePastButNextSubPaymentMinusAfterNextSubPaymentSecondsIsInTheFuture() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(11, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users, is(empty()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndCurrentTimeMinusAfterNextSubPaymentSecondsIsInPast() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(16, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIs0AndMadeAttemptsIs1AndCurrentTimeMinusAfterNextSubPaymentSecondsIsInTheFuture() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(0).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(14, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users, is(empty()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndCurrentTimeMinusAfterNextSubPaymentSecondsIsInPastAndLastSuccessfulPaymentDetailsIsNull() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        User user2 =
            userRepository.save(creteUser().withUserName("sdsf").withDeviceUID("vdfgjdfuy").withNextSubPayment(5));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(null));
        user2 = userRepository.save(user2.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(null));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(16, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users.size(), is(2));
        assertThat(users.get(0).getId(), is(user2.getId()));
        assertThat(users.get(1).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndCurrentTimeMinusAfterNextSubPaymentSecondsIsInPastAndLastSuccessfulPaymentDetailsAndCurrentAreNotTheSame() {
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(creteUser().withNextSubPayment(10));
        PaymentDetails paymentDetails =
            paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        PaymentDetails lastSuccessfulPaymentDetails = paymentDetailsRepository.save(new PaymentDetails().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(lastSuccessfulPaymentDetails));

        //when
        Page<User> usersForRetryPaymentPage = userRepository.findUsersForRetryPayment(16, pageable);

        //then
        List<User> users = usersForRetryPaymentPage.getContent();

        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    public PaymentPolicy paymentPolicyWithDefaultNotNullFieldsAndO2Community() {
        Community o2Community = communityRepository.findByName("o2");
        return paymentPolicyWithDefaultNotNullFields().withCommunity(o2Community);
    }
}
