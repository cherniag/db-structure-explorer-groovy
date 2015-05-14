package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionException;
import mobi.nowtechnologies.server.service.itunes.ITunesResponseFormatException;
import mobi.nowtechnologies.server.service.itunes.impl.ITunesResult;
import mobi.nowtechnologies.server.service.payment.ITunesPaymentSystemServiceHelper;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Author: Gennadii Cherniaiev Date: 4/16/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class ITunesPaymentSystemServiceTest {
    @Mock
    ITunesClient iTunesClient;
    @Mock
    CommunityResourceBundleMessageSource messageSource;
    @Mock
    ITunesPaymentSystemServiceHelper helper;
    @InjectMocks
    ITunesPaymentSystemService iTunesPaymentSystemService;

    @Mock
    User user;
    @Mock
    ITunesPaymentDetails paymentDetails;
    @Mock
    PendingPayment pendingPayment;
    @Mock
    ITunesResult iTunesResult;
    @Captor
    ArgumentCaptor<ITunesConnectionConfig> iTunesConnectionConfigArgumentCaptor;

    private String actualReceipt = "ACTUAL_RECEIPT";
    private String communityRewriteUrl = "mtv1";
    String APPLE_IN_APP_I_TUNES_URL = "apple.inApp.iTunesUrl";
    String APPLE_IN_APP_PASSWORD = "apple.inApp.password";
    private String password = "password";
    private String url = "http://itunes.com";

    @Before
    public void setUp() throws Exception {
        when(pendingPayment.getPaymentDetails()).thenReturn(paymentDetails);
        when(paymentDetails.getOwner()).thenReturn(user);
        when(user.getCommunityRewriteUrl()).thenReturn(communityRewriteUrl);
        when(paymentDetails.getAppStroreReceipt()).thenReturn(actualReceipt);
        when(messageSource.getMessage(communityRewriteUrl, APPLE_IN_APP_I_TUNES_URL, null, null)).thenReturn(url);
        when(messageSource.getDecryptedMessage(communityRewriteUrl, APPLE_IN_APP_PASSWORD, null, null)).thenReturn(password);
    }

    @Test
    public void startPaymentSuccessful() throws Exception {
        when(iTunesClient.verifyReceipt(iTunesConnectionConfigArgumentCaptor.capture(), eq(actualReceipt))).thenReturn(iTunesResult);
        when(iTunesResult.isSuccessful()).thenReturn(true);

        iTunesPaymentSystemService.startPayment(pendingPayment);

        verify(helper).confirmPayment(pendingPayment, iTunesResult);

        ITunesConnectionConfig iTunesConnectionConfig = iTunesConnectionConfigArgumentCaptor.getValue();
        assertEquals(password, iTunesConnectionConfig.getPassword());
        assertEquals(url, iTunesConnectionConfig.getUrl());
    }

    @Test
    public void startPaymentWithNotSuccessfulResponse() throws Exception {
        final Integer failResultCode = 210006;
        when(iTunesClient.verifyReceipt(iTunesConnectionConfigArgumentCaptor.capture(), eq(actualReceipt))).thenReturn(iTunesResult);
        when(iTunesResult.isSuccessful()).thenReturn(false);
        when(iTunesResult.getResult()).thenReturn(failResultCode);

        iTunesPaymentSystemService.startPayment(pendingPayment);

        verify(helper).stopSubscription(pendingPayment, "Not valid receipt, status " + failResultCode);

        ITunesConnectionConfig iTunesConnectionConfig = iTunesConnectionConfigArgumentCaptor.getValue();
        assertEquals(password, iTunesConnectionConfig.getPassword());
        assertEquals(url, iTunesConnectionConfig.getUrl());
    }

    @Test
    public void startPaymentWithITunesConnectionException() throws Exception {
        final String message = iTunesPaymentSystemService.COULDN_T_CONNECT_TO_APP_STORE;
        when(iTunesClient.verifyReceipt(iTunesConnectionConfigArgumentCaptor.capture(), eq(actualReceipt))).thenThrow(new ITunesConnectionException(message));
        when(iTunesResult.isSuccessful()).thenReturn(false);

        iTunesPaymentSystemService.startPayment(pendingPayment);

        verify(helper).failAttempt(pendingPayment, message);

        ITunesConnectionConfig iTunesConnectionConfig = iTunesConnectionConfigArgumentCaptor.getValue();
        assertEquals(password, iTunesConnectionConfig.getPassword());
        assertEquals(url, iTunesConnectionConfig.getUrl());
    }

    @Test
    public void startPaymentWithITunesResponseFormatException() throws Exception {
        final String message = iTunesPaymentSystemService.UNKNOWN_APP_STORE_RESPONSE_FORMAT;
        when(iTunesClient.verifyReceipt(iTunesConnectionConfigArgumentCaptor.capture(), eq(actualReceipt))).thenThrow(new ITunesResponseFormatException(message));
        when(iTunesResult.isSuccessful()).thenReturn(false);

        iTunesPaymentSystemService.startPayment(pendingPayment);

        verify(helper).stopSubscription(pendingPayment, message);

        ITunesConnectionConfig iTunesConnectionConfig = iTunesConnectionConfigArgumentCaptor.getValue();
        assertEquals(password, iTunesConnectionConfig.getPassword());
        assertEquals(url, iTunesConnectionConfig.getUrl());
    }


    @Test
    public void commitPayment() throws Exception {
        iTunesPaymentSystemService.commitPayment(pendingPayment, getResponse());

        verify(helper).failAttempt(pendingPayment, "getDescriptionError");
    }

    private PaymentSystemResponse getResponse() {
        return new PaymentSystemResponse() {
            @Override
            public boolean isSuccessful() {
                return false;
            }

            @Override
            public String getDescriptionError() {
                return "getDescriptionError";
            }
        };
    }
}