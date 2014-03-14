package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import org.junit.Test;

import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.FIRST;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetailsType.RETRY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Titov Mykhaylo (titov) on 14.03.14.
 */
public class SubmittedPaymentTest {

    @Test
    public void shouldIncrementMadeAttemptsForSubmittedPaymentWithRETRYType(){
        //given
        PaymentDetails paymentDetails = new PaymentDetails().withMadeAttempts(2).withMadeRetries(1);

        SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setPaymentDetails(paymentDetails);
        submittedPayment.setType(RETRY);

        //when
        int madeAttempts = submittedPayment.incrementMadeAttemptsForRetry();

        //then
        assertThat(madeAttempts, is(2));
        assertThat(paymentDetails.getMadeRetries(), is(2));
    }

    @Test
    public void shouldNotIncrementMadeAttemptsForSubmittedPaymentWithNotRETRYType(){
        //given
        PaymentDetails paymentDetails = new PaymentDetails().withMadeAttempts(0).withMadeRetries(0);

        SubmittedPayment submittedPayment = new SubmittedPayment();
        submittedPayment.setPaymentDetails(paymentDetails);
        submittedPayment.setType(FIRST);

        //when
        int madeAttempts = submittedPayment.incrementMadeAttemptsForRetry();

        //then
        assertThat(madeAttempts, is(0));
        assertThat(paymentDetails.getMadeRetries(), is (0));
    }
}
