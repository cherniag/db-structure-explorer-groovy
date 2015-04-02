package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;

import com.google.common.collect.Sets;
import org.jsmpp.bean.DeliverSm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MTVNZPaymentSMSProcessorTest {

    private static final String SUPPORTED_SHORT_CODE = "1234";
    private static final String NOT_SUPPORTED_SHORT_CODE = "5678";
    @Mock
    MTVNZPaymentSystemService mtvnzPaymentSystemService;
    @Mock
    MTVNZPaymentResponseParser mtvnzPaymentResponseParser;
    @Mock
    UserRepository userRepository;
    @Mock
    PendingPaymentService pendingPaymentService;
    @InjectMocks
    MTVNZPaymentSMSProcessor mtvnzPaymentSMSProcessor;
    @Mock
    private User user;
    @Mock
    private DeliverSm deliverSm;
    @Mock
    private PendingPayment pendingPayment;
    @Mock
    private MTVNZResponse response;

    @Before
    public void setUp() throws Exception {
        mtvnzPaymentSMSProcessor.setPaymentShortCodes(Sets.newHashSet(SUPPORTED_SHORT_CODE));
    }

    @Test
    public void testSupports() throws Exception {
        when(deliverSm.isSmscDeliveryReceipt()).thenReturn(true);
        when(deliverSm.getDestAddress()).thenReturn(SUPPORTED_SHORT_CODE);

        final boolean supports = mtvnzPaymentSMSProcessor.supports(deliverSm);

        assertTrue(supports);
    }

    @Test
    public void testSupportsWrongNumber() throws Exception {
        when(deliverSm.isSmscDeliveryReceipt()).thenReturn(true);
        when(deliverSm.getDestAddress()).thenReturn(NOT_SUPPORTED_SHORT_CODE);

        final boolean supports = mtvnzPaymentSMSProcessor.supports(deliverSm);

        assertFalse(supports);
    }

    @Test
    public void testSupportsNotReceipt() throws Exception {
        when(deliverSm.isSmscDeliveryReceipt()).thenReturn(false);
        when(deliverSm.getDestAddress()).thenReturn(SUPPORTED_SHORT_CODE);

        final boolean supports = mtvnzPaymentSMSProcessor.supports(deliverSm);

        assertFalse(supports);
    }

    @Test
    public void testParseAndProcess() throws Exception {
        final String number = "+64123456789";
        final int userId = 100;

        when(mtvnzPaymentResponseParser.parse(deliverSm)).thenReturn(response);
        when(response.getPhoneNumber()).thenReturn(number);
        when(userRepository.findByMobileAndCommunity(number, Community.MTV_NZ_COMMUNITY_REWRITE_URL)).thenReturn(Collections.singletonList(user));
        when(user.getId()).thenReturn(userId);

        when(pendingPayment.getPaymentSystem()).thenReturn(PaymentDetails.MTVNZ_PSMS_TYPE);
        List<PendingPayment> pendingPayments = Collections.singletonList(pendingPayment);
        when(pendingPaymentService.getPendingPayments(userId)).thenReturn(pendingPayments);

        mtvnzPaymentSMSProcessor.parserAndProcess(deliverSm);

        verify(mtvnzPaymentResponseParser).parse(deliverSm);
        verify(userRepository).findByMobileAndCommunity(number, Community.MTV_NZ_COMMUNITY_REWRITE_URL);
        verify(pendingPaymentService).getPendingPayments(userId);
        verify(mtvnzPaymentSystemService).commitPayment(pendingPayment, response);
    }
}
