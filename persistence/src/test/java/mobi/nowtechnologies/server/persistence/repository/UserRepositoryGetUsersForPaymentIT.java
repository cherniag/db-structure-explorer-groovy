package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.domain.PaymentDetailsFactory.paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3;
import static mobi.nowtechnologies.server.persistence.domain.PaymentDetailsFactory.paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful;
import static mobi.nowtechnologies.server.persistence.domain.PaymentPolicyFactory.paymentPolicyWithDefaultNotNullFields;
import static mobi.nowtechnologies.server.persistence.domain.UserFactory.userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Titov Mykhaylo (titov)
 *         07.03.14 20:50
 */
public class UserRepositoryGetUsersForPaymentIT extends AbstractRepositoryIT{

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Resource(name = "paymentDetailsRepository")
    private PaymentDetailsRepository paymentDetailsRepository;

    @Resource(name = "paymentPolicyRepository")
    private PaymentPolicyRepository paymentPolicyRepository;

    @Resource(name = "communityRepository")
    private CommunityRepository communityRepository;

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentPlusAdvancedPaymentSecondsIsInTheFuture(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForPendingPayment(4);

        //then
        assertThat(users.size(), is(0));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentPlusAdvancedPaymentSecondsIsNow(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForPendingPayment(5);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsMoreThen0AndNextSubPaymentPlusAdvancedPaymentSecondsIsInThePast(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10).withTariff(_3G));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForPendingPayment(9);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIs0AndNextSubPaymentIsInTheFuture(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(0));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForPendingPayment(9);

        //then
        assertThat(users.size(), is(0));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIs0AndNextSubPaymentIsInThePast(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(0));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusSuccessful().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForPendingPayment(11);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs0AndNextSubPaymentIsInTheFuture(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(0).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(6);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs1AndNextSubPaymentIsInTheFuture(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(9);

        //then
        assertThat(users.size(), is(0));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs1AndNextSubPaymentIsInThePast(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10).withTariff(_3G));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(11);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs1AndNextSubPaymentIsInThePastAndLastSuccessfulPaymentDetailsAndCurrentAreNotTheSame(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10).withTariff(_3G));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        PaymentDetails lastSuccessfulPaymentDetails = paymentDetailsRepository.save(new PaymentDetails().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(lastSuccessfulPaymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(11);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndNextSubPaymentIsInThePastButNextSubPaymentPlusAfterNextSubPaymentSecondsIsInTheFuture(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(11);

        //then
        assertThat(users.size(), is(0));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndCurrentTimePlusAfterNextSubPaymentSecondsIsInPast(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(16);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldNotFindUserWhenAdvancedPaymentSecondsIs0AndMadeAttemptsIs1AndCurrentTimePlusAfterNextSubPaymentSecondsIsInTheFuture(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(0).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(1).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(paymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(14);

        //then
        assertThat(users.size(), is(0));
    }

    @Test
     public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndCurrentTimePlusAfterNextSubPaymentSecondsIsInPastAndLastSuccessfulPaymentDetailsIsNull(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(null));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(16);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    @Test
    public void shouldFindUserWhenAdvancedPaymentSecondsIsNot0AndMadeAttemptsIs2AndCurrentTimePlusAfterNextSubPaymentSecondsIsInPastAndLastSuccessfulPaymentDetailsAndCurrentAreNotTheSame(){
        //given
        PaymentPolicy paymentPolicy = paymentPolicyRepository.save(paymentPolicyWithDefaultNotNullFieldsAndO2Community().withAdvancedPaymentSeconds(5).withAfterNextSubPaymentSeconds(5));
        User user = userRepository.save(userWithDefaultNotNullFieldsAndSubBalance0AndLastDeviceLogin1AndActivationStatusACTIVATED().withNextSubPayment(10));
        PaymentDetails paymentDetails = paymentDetailsRepository.save(paymentDetailsWithActivatedTrueAndLastPaymentStatusErrorAndRetriesOnError3().withMadeAttempts(2).withPaymentPolicy(paymentPolicy).withOwner(user));
        PaymentDetails lastSuccessfulPaymentDetails = paymentDetailsRepository.save(new PaymentDetails().withPaymentPolicy(paymentPolicy).withOwner(user));
        user = userRepository.save(user.withCurrentPaymentDetails(paymentDetails).withLastSuccessfulPaymentDetails(lastSuccessfulPaymentDetails));

        //when
        List<User> users = userRepository.getUsersForRetryPayment(16);

        //then
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getId(), is(user.getId()));
    }

    public PaymentPolicy paymentPolicyWithDefaultNotNullFieldsAndO2Community(){
        Community o2Community = communityRepository.findByName("o2");
        return paymentPolicyWithDefaultNotNullFields().withCommunity(o2Community);
    }
}
