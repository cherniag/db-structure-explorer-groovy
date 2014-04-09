package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import org.junit.Test;

import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.EXTERNAL_ERROR;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.SUCCESSFUL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Titov Mykhaylo (titov)
 *         08.03.14 10:05
 */
public class PaymentDetailsTest {

    @Test
    public void shouldCreatePaymentDetailsWithMadeRetriesIs0AndMadeAttemptsIs0(){
        //given
        PaymentDetails paymentDetails;

        //when
        paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR();

        //then
        assertThat(paymentDetails.getMadeAttempts(), is(0));
        assertThat(paymentDetails.getMadeRetries(), is(0));
    }

    @Test
    public void shouldNotIncrementMadeAttemptsWhenRetriesOnErrorIs3AndMadeRetriesIs0AndLastPaymentStatusIsEXTERNAL_ERROR(){
        //given
        PaymentDetails paymentDetails = new PaymentDetails().withLastPaymentStatus(EXTERNAL_ERROR).withRetriesOnError(3).withMadeRetries(0);

        //when
        int madeAttempts = paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();

        //then
        assertThat(madeAttempts, is(0));
        assertThat(paymentDetails.getMadeRetries(), is(1));
    }

    @Test
     public void shouldNotIncrementMadeAttemptsWhenLastPaymentStatusIsSUCCESSFUL(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithLastPaymentStatusSUCCESSFUL().withRetriesOnError(3).withMadeAttempts(0).withMadeRetries(0);

        //when
        int madeAttempts = paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();

        //then
        assertThat(madeAttempts, is(0));
        assertThat(paymentDetails.getMadeRetries(), is(1));
    }

    @Test
    public void shouldNotIncrementMadeAttemptsWhenRetriesOnErrorIs3AndMadeRetriesIs0(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withRetriesOnError(3).withMadeRetries(0);

        //when
        int madeAttempts = paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();

        //then
        assertThat(madeAttempts, is(0));
        assertThat(paymentDetails.getMadeRetries(), is(1));
    }

    @Test
    public void shouldIncrementMadeAttemptsWhenRetriesOnErrorIs3AndMadeRetriesIs2(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withRetriesOnError(3).withMadeRetries(2);

        //when
        int madeAttempts = paymentDetails.incrementMadeAttemptsAccordingToMadeRetries();

        //then
        assertThat(madeAttempts, is(1));
        assertThat(paymentDetails.getMadeRetries(), is(0));
    }

    @Test
     public void shouldResetMadeRetries(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(2).withRetriesOnError(3).withMadeRetries(3);

        //when
        paymentDetails.resetMadeAttempts();

        //then
        assertThat(paymentDetails.getMadeAttempts(), is(0));
        assertThat(paymentDetails.getMadeRetries(), is(0));
    }

    @Test
    public void shouldResetMadeRetriesForFirstPayment(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(2).withRetriesOnError(3).withMadeRetries(3);

        //when
        paymentDetails.resetMadeAttemptsForFirstPayment();

        //then
        assertThat(paymentDetails.getMadeAttempts(), is(0));
        assertThat(paymentDetails.getMadeRetries(), is(-1));
    }

    @Test
    public void shouldNotSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIs0AndAfterNextSubPaymentSecondsIs0AndMadeAttemptsIs0(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(0).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(0)
                .withAfterNextSubPaymentSeconds(0));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(false));
    }

    @Test
    public void shouldNotSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIsNot0AndAfterNextSubPaymentSecondsIs0AndMadeAttemptsIs1(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(1).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(1)
                .withAfterNextSubPaymentSeconds(0));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(false));
    }

    @Test
    public void shouldSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIs0AndAfterNextSubPaymentSecondsIs0AndMadeAttemptsIs2(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(2).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(1)
                .withAfterNextSubPaymentSeconds(0));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(true));
    }

    @Test
    public void shouldNotSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIs0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs0(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(0).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(0)
                .withAfterNextSubPaymentSeconds(1));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(false));
    }

    @Test
    public void shouldNotSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIs0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs1(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(1).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(0)
                .withAfterNextSubPaymentSeconds(1));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(false));
    }

    @Test
    public void shouldSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIs0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs2(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(2).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(0)
                .withAfterNextSubPaymentSeconds(1));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(true));
    }

    @Test
    public void shouldNotSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIsNot0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs0(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(0).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(1)
                .withAfterNextSubPaymentSeconds(1));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(false));
    }

    @Test
    public void shouldNotSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIsNot0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs1(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(1).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(1)
                .withAfterNextSubPaymentSeconds(1));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(false));
    }

    @Test
    public void shouldNotSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIsNot0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs2(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(2).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(1)
                .withAfterNextSubPaymentSeconds(1));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(false));
    }

    @Test
    public void shouldSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIsNot0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs3(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(3).withPaymentPolicy(new PaymentPolicy().withAdvancedPaymentSeconds(1)
                .withAfterNextSubPaymentSeconds(1));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(true));
    }

    @Test
    public void shouldSaidThatItShouldBeUnSubscribedWhenAdvancedPaymentSecondsIsNot0AndAfterNextSubPaymentSecondsIsNot0AndMadeAttemptsIs1AndLastSuccessfulPaymentDetailsAndCurrentAreNotTheSame(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withI(0L).withMadeAttempts(1).withPaymentPolicy(new PaymentPolicy()
                .withAdvancedPaymentSeconds(1)
                .withAfterNextSubPaymentSeconds(1));
        paymentDetails.getOwner().withLastSuccessfulPaymentDetails(new PaymentDetails().withI(1L));

        //when
        boolean shouldBeUnSubscribed = paymentDetails.shouldBeUnSubscribed();

        //then
        assertThat(shouldBeUnSubscribed, is(true));
    }

    @Test
    public void shouldNotSaidThatCurrentAttemptFailedWhenMadeAttemptsIs0(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(0);

        //when
        boolean isCurrentAttemptFailed = paymentDetails.isCurrentAttemptFailed();

        //then
        assertThat(isCurrentAttemptFailed, is(false));
    }

    @Test
    public void shouldNotSaidThatCurrentAttemptFailedWhenMadeRetriesIsNot0(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(1).withMadeRetries(1);

        //when
        boolean isCurrentAttemptFailed = paymentDetails.isCurrentAttemptFailed();

        //then
        assertThat(isCurrentAttemptFailed, is(false));
    }

    @Test
    public void shouldNotSaidThatCurrentAttemptFailedWhenLastPaymentStatusIsSuccessful(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithLastPaymentStatusSUCCESSFUL().withMadeAttempts(1).withMadeRetries(0);

        //when
        boolean isCurrentAttemptFailed = paymentDetails.isCurrentAttemptFailed();

        //then
        assertThat(isCurrentAttemptFailed, is(false));
    }

    @Test
    public void shouldSaidThatCurrentAttemptFailedWhenMadeAttemptsMoreThan0AndMadeRetriesIs0AndLastPaymentStatusIsError(){
        //given
        PaymentDetails paymentDetails = paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR().withMadeAttempts(1).withMadeRetries(0);

        //when
        boolean isCurrentAttemptFailed = paymentDetails.isCurrentAttemptFailed();

        //then
        assertThat(isCurrentAttemptFailed, is(true));
    }

    private PaymentDetails paymentDetailsWithLastPaymentStatusSUCCESSFUL() {
        return new PaymentDetails().withLastPaymentStatus(SUCCESSFUL);
    }

    private PaymentDetails paymentDetailsWithSomeOwnerAndLastPaymentStatusERROR() {
        return new PaymentDetails().withOwner(new User()).withLastPaymentStatus(ERROR);
    }
}
