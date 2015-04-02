package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoProvider;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.ProviderConnectionException;
import mobi.nowtechnologies.server.service.nz.ProviderNotAvailableException;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Author: Gennadii Cherniaiev Date: 4/2/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class MTVNZPaymentSystemServiceTest {
    private static final String USER_DOES_NOT_BELONG_TO_VF = "User does not belong to VF";
    private static final String VF_DOES_NOT_KNOW_THIS_USER = "MSISDN not found";
    private static final String VODAFONE = "Vodafone";
    private static final String NON_VODAFONE = "Not Vodafone";
    @Mock
    NZSubscriberInfoProvider nzSubscriberInfoProvider;
    @Mock
    MTVNZPaymentHelper mtvnzPaymentHelper;
    @InjectMocks
    MTVNZPaymentSystemService mtvnzPaymentSystemService;

    @Mock
    private PendingPayment pendingPayment;
    @Mock
    private PSMSPaymentDetails paymentDetails;
    @Mock
    private NZSubscriberResult nzSubscriberResult;
    // business fields
    private final String phoneNumber = "+64123456789";
    private final String normalizedPhoneNumber = phoneNumber.replaceFirst("\\+", "");

    @Before
    public void setUp() throws Exception {
        when(pendingPayment.getPaymentDetails()).thenReturn(paymentDetails);
        when(paymentDetails.getPhoneNumber()).thenReturn(phoneNumber);
        when(nzSubscriberInfoProvider.getSubscriberResult(normalizedPhoneNumber)).thenReturn(nzSubscriberResult);
    }

    @Test
    public void startPaymentSuccess() throws Exception {
        when(nzSubscriberResult.getProviderName()).thenReturn(VODAFONE);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        verify(nzSubscriberInfoProvider).getSubscriberResult(normalizedPhoneNumber);
        verify(mtvnzPaymentHelper).startPayment(pendingPayment);
    }

    @Test
    public void startPaymentWhenUserIsNotSubscriber() throws Exception {
        when(nzSubscriberResult.getProviderName()).thenReturn(NON_VODAFONE);

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        verify(nzSubscriberInfoProvider).getSubscriberResult(normalizedPhoneNumber);
        verify(mtvnzPaymentHelper).finishPaymentForNotVFUser(pendingPayment, USER_DOES_NOT_BELONG_TO_VF);
    }

    @Test
    public void startPaymentWhenMSISDNNotFound() throws Exception {
        //given
        when(nzSubscriberInfoProvider.getSubscriberResult(normalizedPhoneNumber)).thenThrow(new MsisdnNotFoundException("cause", new Exception()));

        //when
        mtvnzPaymentSystemService.startPayment(pendingPayment);

        //then
        verify(nzSubscriberInfoProvider).getSubscriberResult(normalizedPhoneNumber);
        verify(mtvnzPaymentHelper).finishPaymentForNotVFUser(pendingPayment, VF_DOES_NOT_KNOW_THIS_USER);
    }

    @Test
    public void startPaymentWhenProviderConnectionProblem() throws Exception {
        when(nzSubscriberInfoProvider.getSubscriberResult(normalizedPhoneNumber)).thenThrow(new ProviderConnectionException("cause", new Exception()));

        mtvnzPaymentSystemService.startPayment(pendingPayment);

        verify(nzSubscriberInfoProvider).getSubscriberResult(normalizedPhoneNumber);
        verify(mtvnzPaymentHelper).skipAttemptWithoutRetryIncrement(pendingPayment, "cause");
    }

    @Test(expected = ProviderNotAvailableException.class)
    public void startPaymentWhenProviderReturnsError() throws Exception {
        when(nzSubscriberInfoProvider.getSubscriberResult(normalizedPhoneNumber)).thenThrow(new ProviderNotAvailableException("cause", new Exception()));

        mtvnzPaymentSystemService.startPayment(pendingPayment);
    }
}