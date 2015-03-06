package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import org.jsmpp.bean.DeliverSm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MTVNZPaymentSMSMessageProcessorTest {
    @Mock
    MTVNZPaymentSystemService mtvnzPaymentSystemService;
    @Mock
    MTVNZPaymentResponseParser mtvnzPaymentResponseParser;
    @Mock
    NZSubscriberInfoRepository nzSubscriberInfoRepository;
    @Mock
    PendingPaymentService pendingPaymentService;
    @InjectMocks
    MTVNZPaymentSMSMessageProcessor mtvnzPaymentSMSMessageProcessor;

    @Before
    public void setUp() throws Exception {
        Set<String> numbers = new HashSet<>();
        numbers.add("1234");
        mtvnzPaymentSMSMessageProcessor.setPaymentShortCodes(numbers);
    }

    @Test
    public void testSupports() throws Exception {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.isSmscDeliveryReceipt()).thenReturn(true);
        when(deliverSm.getDestAddress()).thenReturn("1234");

        final boolean supports = mtvnzPaymentSMSMessageProcessor.supports(deliverSm);

        assertTrue(supports);
    }

    @Test
    public void testSupportsWrongNumber() throws Exception {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.isSmscDeliveryReceipt()).thenReturn(true);
        when(deliverSm.getDestAddress()).thenReturn("5678");

        final boolean supports = mtvnzPaymentSMSMessageProcessor.supports(deliverSm);

        assertFalse(supports);
    }

    @Test
    public void testSupportsNotReceipt() throws Exception {
        DeliverSm deliverSm = mock(DeliverSm.class);
        when(deliverSm.isSmscDeliveryReceipt()).thenReturn(false);
        when(deliverSm.getDestAddress()).thenReturn("1234");

        final boolean supports = mtvnzPaymentSMSMessageProcessor.supports(deliverSm);

        assertFalse(supports);
    }

    @Test
    public void testParseAndProcess() throws Exception {
        final String number = "+64123456789";
        final int userId = 100;

        DeliverSm deliverSm = mock(DeliverSm.class);
        MTVNZResponse response = mock(MTVNZResponse.class);
        when(response.getPhoneNumber()).thenReturn(number);
        when(mtvnzPaymentResponseParser.parse(deliverSm)).thenReturn(response);

        NZSubscriberInfo nzSubscriberInfo = mock(NZSubscriberInfo.class);
        when(nzSubscriberInfo.getUserId()).thenReturn(userId);
        when(nzSubscriberInfoRepository.findSubscriberInfoByMsisdn(number)).thenReturn(nzSubscriberInfo);

        PendingPayment pendingPayment = mock(PendingPayment.class);
        when(pendingPayment.getPaymentSystem()).thenReturn(PaymentDetails.MTVNZ_PSMS_TYPE);
        List<PendingPayment> pendingPayments = Arrays.asList(pendingPayment);
        when(pendingPaymentService.getPendingPayments(userId)).thenReturn(pendingPayments);


        mtvnzPaymentSMSMessageProcessor.parserAndProcess(deliverSm);

        verify(mtvnzPaymentResponseParser).parse(deliverSm);
        verify(nzSubscriberInfoRepository).findSubscriberInfoByMsisdn(number);
        verify(pendingPaymentService).getPendingPayments(userId);
        verify(mtvnzPaymentSystemService).commitPayment(pendingPayment, response);
    }
}
