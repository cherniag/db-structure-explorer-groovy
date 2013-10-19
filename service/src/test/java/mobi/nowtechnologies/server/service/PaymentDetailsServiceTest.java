package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.payment.O2PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

/**
 * @author Titov Mykhaylo (titov)
 *         19.10.13 19:55
 */
@RunWith(PowerMockRunner.class)
public class PaymentDetailsServiceTest {

    PaymentDetailsService paymentDetailsServiceFixture;

    @Mock
    PaymentDetailsRepository paymentDetailsRepositoryMock;

    public void setUp(){
        paymentDetailsServiceFixture =new PaymentDetailsService();
    }

    @Test
    public void shouldFindFailurePaymentPaymentDetailsWithNoNotification(){
        //given
        String communityUrl ="";
        Pageable pageable = new PageRequest(0,1);

        List<PaymentDetails> expectedPaymentDetailsList = Collections.<PaymentDetails>singletonList(new O2PSMSPaymentDetails());

        doReturn(expectedPaymentDetailsList).when(paymentDetailsRepositoryMock).findFailurePaymentPaymentDetailsWithNoNotification(communityUrl, pageable);;

        //when
        List<PaymentDetails> paymentDetailsList = paymentDetailsServiceFixture.findFailurePaymentPaymentDetailsWithNoNotification(communityUrl, pageable);

        //then
        assertThat(paymentDetailsList, is(expectedPaymentDetailsList));
    }
}

