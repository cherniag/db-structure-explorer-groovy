package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.behavior.PaymentTimeService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import java.util.Arrays;
import java.util.Date;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentTimeServiceTest {
    private static final int MILLIS_IN_MINUTE = 60000;
    @Mock
    private PendingPaymentRepository pendingPaymentRepository;
    @Mock
    private SubmittedPaymentService submittedPaymentService;
    @InjectMocks
    private PaymentTimeService paymentTimeService;

    @Mock
    private User user;
    @Mock
    private PaymentDetails paymentDetails;
    @Mock
    private PendingPayment pendingPayment;
    @Mock
    private SubmittedPayment submittedPayment;

    private int[] timeRange = new int[]{1,10,60,240,480};

    @Before
    public void setUp() throws Exception {
        Integer id = 100;
        when(user.getCurrentPaymentDetails()).thenReturn(paymentDetails);
        when(user.getId()).thenReturn(id);
        when(pendingPaymentRepository.findByUserId(id)).thenReturn(Arrays.asList(pendingPayment));
        when(submittedPaymentService.getLatest(user)).thenReturn(submittedPayment);
        when(paymentDetails.isActivated()).thenReturn(true);
        paymentTimeService.setTimeRange(timeRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void successfulStatus() throws Exception {
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.SUCCESSFUL);

        paymentTimeService.getNextRetryTime(user, new Date());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deactivatedDetails() throws Exception {
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.AWAITING);
        when(paymentDetails.isActivated()).thenReturn(false);

        paymentTimeService.getNextRetryTime(user, new Date());
    }

    @Test
    public void awaitingStatusFor0Minutes() throws Exception {
        final Date now = new Date();
        final long justNow = now.getTime() - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.AWAITING);
        when(pendingPayment.getTimestamp()).thenReturn(justNow);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertEquals(now.getTime() + MILLIS_IN_MINUTE, lastRetryTime.getTime());
    }

    @Test
    public void awaitingStatusFor1Minute() throws Exception {
        final Date now = new Date();
        final long minuteAgo = now.getTime() - MILLIS_IN_MINUTE - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.AWAITING);
        when(pendingPayment.getTimestamp()).thenReturn(minuteAgo);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertEquals(now.getTime() + 10 * MILLIS_IN_MINUTE, lastRetryTime.getTime());
    }

    @Test
    public void awaitingStatusFor60Minutes() throws Exception {
        final Date now = new Date();
        final long minuteAgo = now.getTime() - 60 * MILLIS_IN_MINUTE - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.AWAITING);
        when(pendingPayment.getTimestamp()).thenReturn(minuteAgo);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertEquals(now.getTime() + 240 * MILLIS_IN_MINUTE, lastRetryTime.getTime());
    }

    @Test
    public void awaitingStatusForMoreThanMaxMinutes() throws Exception {
        final Date now = new Date();
        final long minuteAgo = now.getTime() - 480 * MILLIS_IN_MINUTE - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.AWAITING);
        when(pendingPayment.getTimestamp()).thenReturn(minuteAgo);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertNull(lastRetryTime);
    }

    @Test
    public void errorStatusFor0Minutes() throws Exception {
        final Date now = new Date();
        final long justNow = now.getTime() - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.ERROR);
        when(submittedPayment.getTimestamp()).thenReturn(justNow);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertEquals(now.getTime() + MILLIS_IN_MINUTE, lastRetryTime.getTime());
    }

    @Test
    public void errorStatusFor1Minute() throws Exception {
        final Date now = new Date();
        final long minuteAgo = now.getTime() - MILLIS_IN_MINUTE - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.ERROR);
        when(submittedPayment.getTimestamp()).thenReturn(minuteAgo);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertEquals(now.getTime() + 10 * MILLIS_IN_MINUTE, lastRetryTime.getTime());
    }

    @Test
    public void errorStatusFor60Minutes() throws Exception {
        final Date now = new Date();
        final long minuteAgo = now.getTime() - 60 * MILLIS_IN_MINUTE - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.ERROR);
        when(submittedPayment.getTimestamp()).thenReturn(minuteAgo);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertEquals(now.getTime() + 240 * MILLIS_IN_MINUTE, lastRetryTime.getTime());
    }

    @Test
    public void errorStatusForMoreThanMaxMinutes() throws Exception {
        final Date now = new Date();
        final long minuteAgo = now.getTime() - 480 * MILLIS_IN_MINUTE - 1000L;
        when(paymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.ERROR);
        when(submittedPayment.getTimestamp()).thenReturn(minuteAgo);

        Date lastRetryTime = paymentTimeService.getNextRetryTime(user, now);

        assertNull(lastRetryTime);
    }
}