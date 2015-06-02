package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.ITunesPaymentDetailsService;
import mobi.nowtechnologies.server.service.behavior.PaymentTimeService;
import mobi.nowtechnologies.server.service.itunes.AppStoreReceiptParser;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import java.util.Date;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Author: Gennadii Cherniaiev Date: 6/2/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class ITunesPaymentDetailsManagerTest {
    @Mock
    PaymentTimeService paymentTimeService;
    @Mock
    ITunesPaymentDetailsService iTunesPaymentDetailsService;
    @Mock
    AppStoreReceiptParser appStoreReceiptParser;
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    ITunesService iTunesService;
    @InjectMocks
    ITunesPaymentDetailsManager iTunesPaymentDetailsManager;

    @Mock
    private User user;
    @Mock
    private ITunesPaymentDetailsManager.NextRetryInfo nextRetryInfo;
    @Mock
    private ITunesPaymentDetails iTunesPaymentDetails;
    @Mock
    private PaymentPolicy paymentPolicy;

    @Before
    public void setUp() throws Exception {
        when(iTunesPaymentDetails.getPaymentPolicy()).thenReturn(paymentPolicy);
    }


    @Test
    public void processSubscriptionFromOldApi() throws Exception {
        final String appStoreReceipt = "receipt";
        final boolean isNewApi = false;

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesService).processInAppSubscription(user, appStoreReceipt);
    }

    @Test
    public void processSubscriptionFromNewApiAndUserWithoutActiveIPaymentDetails() throws Exception {
        final String appStoreReceipt = "receipt";
        final String productId = "productId";
        final boolean isNewApi = true;
        final Date expireDate = new Date();

        when(appStoreReceiptParser.getProductId(appStoreReceipt)).thenReturn(productId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(false).thenReturn(true);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(iTunesPaymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.NONE);
        when(paymentTimeService.getNextRetryTimeForITunesPayment(eq(user), any(Date.class))).thenReturn(expireDate);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService).createNewPaymentDetails(user, appStoreReceipt, productId);
        verify(paymentTimeService).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
        verify(nextRetryInfo).setNextRetry(expireDate);
    }

    @Test
    public void processSubscriptionFromNewApiAndUserWithActiveIPaymentDetailsWithTheSameReceipt() throws Exception {
        final String appStoreReceipt = "receipt";
        final String productId = "productId";
        final boolean isNewApi = true;

        when(appStoreReceiptParser.getProductId(appStoreReceipt)).thenReturn(productId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(true);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(paymentPolicy.getAppStoreProductId()).thenReturn(productId);
        when(iTunesPaymentDetails.getAppStoreReceipt()).thenReturn(appStoreReceipt);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService, never()).createNewPaymentDetails(user, appStoreReceipt, productId);
        verify(paymentTimeService, never()).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
        verify(paymentDetailsRepository, never()).save(iTunesPaymentDetails);
    }

    @Test
    public void processSubscriptionFromNewApiAndUserWithActiveIPaymentDetailsWithAnotherReceipt() throws Exception {
        final String receivedAppStoreReceipt = "receivedAppStoreReceipt";
        final String storedAppStoreReceipt = "storedAppStoreReceipt";
        final String productId = "productId";
        final boolean isNewApi = true;

        when(appStoreReceiptParser.getProductId(receivedAppStoreReceipt)).thenReturn(productId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(true);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(paymentPolicy.getAppStoreProductId()).thenReturn(productId);
        when(iTunesPaymentDetails.getAppStoreReceipt()).thenReturn(storedAppStoreReceipt);

        iTunesPaymentDetailsManager.processITunesSubscription(user, receivedAppStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService, never()).createNewPaymentDetails(user, receivedAppStoreReceipt, productId);
        verify(paymentTimeService, never()).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
        verify(iTunesPaymentDetails).updateAppStroreReceipt(receivedAppStoreReceipt);
        verify(paymentDetailsRepository).save(iTunesPaymentDetails);
    }

    @Test
    public void processSubscriptionFromNewApiAndUserWithActiveIPaymentDetailsWithAnotherProductId() throws Exception {
        final String appStoreReceipt = "receipt";
        final String receivedProductId = "receivedProductId";
        final String actualProductId = "actualProductId";
        final boolean isNewApi = true;

        when(appStoreReceiptParser.getProductId(appStoreReceipt)).thenReturn(receivedProductId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(true);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(paymentPolicy.getAppStoreProductId()).thenReturn(actualProductId);
        when(iTunesPaymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.SUCCESSFUL);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService).createNewPaymentDetails(user, appStoreReceipt, receivedProductId);
        verify(paymentTimeService, never()).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
    }

    //
    // Migrate users
    //
    @Test
    public void processSubscriptionFromNewApiAndMigrateUserWithoutReceipt() throws Exception {
        final String appStoreReceipt = null;
        final String existingAppStoreReceipt = null;
        final String productId = "productId";
        final boolean isNewApi = true;
        final Date expireDate = new Date();

        when(user.hasActiveITunesPaymentDetails()).thenReturn(false);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(user.hasITunesSubscription()).thenReturn(false);
        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.getBase64EncodedAppStoreReceipt()).thenReturn(existingAppStoreReceipt);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(paymentDetailsRepository.countITunesPaymentDetails(user)).thenReturn(0L);
        when(iTunesPaymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.NONE);
        when(paymentTimeService.getNextRetryTimeForITunesPayment(eq(user), any(Date.class))).thenReturn(expireDate);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService, never()).createNewPaymentDetails(user, existingAppStoreReceipt, productId);
        verify(paymentTimeService, never()).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
    }

    @Test
    public void processSubscriptionFromNewApiAndMigrateUserThatHasSubscription() throws Exception {
        final String appStoreReceipt = null;
        final String existingAppStoreReceipt = "existingAppStoreReceipt";
        final String productId = "productId";
        final boolean isNewApi = true;
        final Date expireDate = new Date();

        when(appStoreReceiptParser.getProductId(existingAppStoreReceipt)).thenReturn(productId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(false).thenReturn(true);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(true);
        when(user.hasITunesSubscription()).thenReturn(true);
        when(user.getBase64EncodedAppStoreReceipt()).thenReturn(existingAppStoreReceipt);
        when(iTunesPaymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.NONE);
        when(paymentTimeService.getNextRetryTimeForITunesPayment(eq(user), any(Date.class))).thenReturn(expireDate);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService).createNewPaymentDetails(user, existingAppStoreReceipt, productId);
        verify(paymentTimeService, never()).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
    }

    @Test
    public void processSubscriptionFromNewApiAndMigrateUserThatHadSubscription() throws Exception {
        final String appStoreReceipt = null;
        final String existingAppStoreReceipt = "existingAppStoreReceipt";
        final String productId = "productId";
        final boolean isNewApi = true;
        final Date expireDate = new Date();
        final long iTunesPaymentDetailsWereNotCreatedYet = 0L;

        when(appStoreReceiptParser.getProductId(existingAppStoreReceipt)).thenReturn(productId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(true);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(user.hasITunesSubscription()).thenReturn(false);
        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.getBase64EncodedAppStoreReceipt()).thenReturn(existingAppStoreReceipt);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(paymentDetailsRepository.countITunesPaymentDetails(user)).thenReturn(iTunesPaymentDetailsWereNotCreatedYet);
        when(iTunesPaymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.NONE);
        when(paymentTimeService.getNextRetryTimeForITunesPayment(eq(user), any(Date.class))).thenReturn(expireDate);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService).createNewPaymentDetails(user, existingAppStoreReceipt, productId);
        verify(paymentTimeService).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
        verify(nextRetryInfo).setNextRetry(expireDate);
    }

    @Test
    public void processSubscriptionFromNewApiAndMigrateUserThatHadSubscriptionAndWasMigratedAlready() throws Exception {
        final String appStoreReceipt = null;
        final String existingAppStoreReceipt = "existingAppStoreReceipt";
        final String productId = "productId";
        final boolean isNewApi = true;
        final Date expireDate = new Date();
        final long iTunesPaymentDetailsWereCreatedAlready = 1L;

        when(appStoreReceiptParser.getProductId(existingAppStoreReceipt)).thenReturn(productId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(false);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(user.hasITunesSubscription()).thenReturn(false);
        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.getBase64EncodedAppStoreReceipt()).thenReturn(existingAppStoreReceipt);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(paymentDetailsRepository.countITunesPaymentDetails(user)).thenReturn(iTunesPaymentDetailsWereCreatedAlready);
        when(iTunesPaymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.NONE);
        when(paymentTimeService.getNextRetryTimeForITunesPayment(eq(user), any(Date.class))).thenReturn(expireDate);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService, never()).createNewPaymentDetails(user, existingAppStoreReceipt, productId);
        verify(paymentTimeService, never()).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
    }

    @Test
    public void processSubscriptionFromNewApiAndMigrateUserThatHadITunesSubscriptionButNowHasAnotherSubscription() throws Exception {
        final String appStoreReceipt = null;
        final String existingAppStoreReceipt = "existingAppStoreReceipt";
        final String productId = "productId";
        final boolean isNewApi = true;
        final Date expireDate = new Date();
        final long iTunesPaymentDetailsWereNotCreatedYet = 0L;

        when(appStoreReceiptParser.getProductId(existingAppStoreReceipt)).thenReturn(productId);
        when(user.hasActiveITunesPaymentDetails()).thenReturn(false);
        when(user.hasActivePaymentDetails()).thenReturn(true);
        when(user.isNextSubPaymentInTheFuture()).thenReturn(false);
        when(user.hasITunesSubscription()).thenReturn(false);
        when(user.getBase64EncodedAppStoreReceipt()).thenReturn(existingAppStoreReceipt);
        when(user.getCurrentPaymentDetails()).thenReturn(iTunesPaymentDetails);
        when(paymentDetailsRepository.countITunesPaymentDetails(user)).thenReturn(iTunesPaymentDetailsWereNotCreatedYet);
        when(iTunesPaymentDetails.getLastPaymentStatus()).thenReturn(PaymentDetailsStatus.NONE);
        when(paymentTimeService.getNextRetryTimeForITunesPayment(eq(user), any(Date.class))).thenReturn(expireDate);

        iTunesPaymentDetailsManager.processITunesSubscription(user, appStoreReceipt, isNewApi, nextRetryInfo);

        verify(iTunesPaymentDetailsService, never()).createNewPaymentDetails(user, existingAppStoreReceipt, productId);
        verify(paymentTimeService, never()).getNextRetryTimeForITunesPayment(eq(user), any(Date.class));
    }
}