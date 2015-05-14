package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.ITunesPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.repository.PaymentDetailsRepository;
import mobi.nowtechnologies.server.service.itunes.AppStoreReceiptParser;
import static mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus.NONE;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
/**
 * Author: Gennadii Cherniaiev Date: 4/15/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class ITunesPaymentDetailsServiceTest {
    @Mock
    PaymentDetailsRepository paymentDetailsRepository;
    @Mock
    PaymentDetailsService paymentDetailsService;
    @Mock
    AppStoreReceiptParser appStoreReceiptParser;
    @Mock
    private PaymentPolicyService paymentPolicyService;
    @Mock
    UserService userService;
    @InjectMocks
    ITunesPaymentDetailsService iTunesPaymentDetailsService;
    @Mock
    User user;
    @Mock
    Community community;
    @Mock
    ITunesPaymentDetails currentPaymentDetails;
    @Mock
    PaymentPolicy currentPaymentPolicy;
    @Captor
    ArgumentCaptor<ITunesPaymentDetails> paymentDetailsCaptor;
    int retriesOnError = 3;

    @Before
    public void setUp() throws Exception {
        iTunesPaymentDetailsService.setRetriesOnError(retriesOnError);
        when(user.getCurrentPaymentDetails()).thenReturn(currentPaymentDetails);
        when(user.getCommunity()).thenReturn(community);
        when(currentPaymentDetails.getPaymentPolicy()).thenReturn(currentPaymentPolicy);
    }


    @Test
    public void assignReceiptToUserWithActiveITunesPaymentDetailsWithTheSameReceiptAndSameProductId() throws Exception {
        final String appStoreReceipt = "SOME RECEIPT";
        final String appStoreProductId = "PRODUCT ID";

        when(user.hasActiveITunesPaymentDetails()).thenReturn(true);
        when(currentPaymentDetails.getAppStroreReceipt()).thenReturn(appStoreReceipt);
        when(appStoreReceiptParser.getProductId(appStoreReceipt)).thenReturn(appStoreProductId);
        when(currentPaymentPolicy.getAppStoreProductId()).thenReturn(appStoreProductId);

        iTunesPaymentDetailsService.createNewOrUpdatePaymentDetails(user, appStoreReceipt, appStoreReceiptParser.getProductId(appStoreReceipt));

        verify(appStoreReceiptParser).getProductId(appStoreReceipt);
        verify(currentPaymentDetails, never()).updateAppStroreReceipt(anyString());
        verify(paymentDetailsRepository, never()).save(any(PaymentDetails.class));
        verify(userService, never()).updateUser(any(User.class));
        verify(paymentDetailsService, never()).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(userService, never()).skipFreeTrial(user);
    }

    @Test
    public void assignReceiptToUserWithActiveITunesPaymentDetailsWithDifferentReceiptAndSameProductId() throws Exception {
        final String newReceipt = "NEW RECEIPT";
        final String storedReceipt = "STORED RECEIPT";
        final String appStoreProductId = "PRODUCT ID";
        final long currentPaymentDetailsId = 100L;

        when(user.hasActiveITunesPaymentDetails()).thenReturn(true);
        when(currentPaymentDetails.getAppStroreReceipt()).thenReturn(storedReceipt);
        when(appStoreReceiptParser.getProductId(newReceipt)).thenReturn(appStoreProductId);
        when(currentPaymentPolicy.getAppStoreProductId()).thenReturn(appStoreProductId);
        ITunesPaymentDetails updated = mock(ITunesPaymentDetails.class);
        when(currentPaymentDetails.getI()).thenReturn(currentPaymentDetailsId);
        when(paymentDetailsRepository.findOne(currentPaymentDetailsId)).thenReturn(updated);

        iTunesPaymentDetailsService.createNewOrUpdatePaymentDetails(user, newReceipt, appStoreReceiptParser.getProductId(newReceipt));

        verify(appStoreReceiptParser).getProductId(newReceipt);
        verify(paymentDetailsRepository).findOne(currentPaymentDetailsId);
        verify(updated).updateAppStroreReceipt(newReceipt);
        verify(paymentDetailsRepository).save(updated);

        verify(userService, never()).updateUser(any(User.class));
        verify(paymentDetailsService, never()).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(userService, never()).skipFreeTrial(user);
    }

    @Test
    public void assignReceiptToUserWithActiveITunesPaymentDetailsWithDifferentReceiptAndDifferentProductId() throws Exception {
        final String newReceipt = "NEW RECEIPT";
        final String storedReceipt = "STORED RECEIPT";
        final String newAppStoreProductId = "NEW PRODUCT ID";
        final String storedAppStoreProductId = "STORED PRODUCT ID";

        when(user.hasActiveITunesPaymentDetails()).thenReturn(true);
        when(user.isOnFreeTrial()).thenReturn(false);
        when(currentPaymentDetails.getAppStroreReceipt()).thenReturn(storedReceipt);
        when(appStoreReceiptParser.getProductId(newReceipt)).thenReturn(newAppStoreProductId);
        when(currentPaymentPolicy.getAppStoreProductId()).thenReturn(storedAppStoreProductId);
        doReturn(null).when(paymentDetailsRepository).save(paymentDetailsCaptor.capture());
        PaymentPolicy newPaymentPolicy = mock(PaymentPolicy.class);
        when(paymentPolicyService.findByCommunityAndAppStoreProductId(community, newAppStoreProductId)).thenReturn(newPaymentPolicy);

        iTunesPaymentDetailsService.createNewOrUpdatePaymentDetails(user, newReceipt, appStoreReceiptParser.getProductId(newReceipt));

        ITunesPaymentDetails created = paymentDetailsCaptor.getValue();
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(userService, never()).skipFreeTrial(user);
        verify(paymentDetailsRepository).save(created);
        verify(userService).updateUser(user);
        verify(paymentPolicyService).findByCommunityAndAppStoreProductId(community, newAppStoreProductId);
        verify(appStoreReceiptParser).getProductId(newReceipt);

        assertEquals(newReceipt, created.getAppStroreReceipt());
        assertEquals(user, created.getOwner());
        assertEquals(retriesOnError, created.getRetriesOnError());
        assertEquals(newPaymentPolicy, created.getPaymentPolicy());

        assertEquals(NONE, created.getLastPaymentStatus());
        assertTrue(0 < created.getCreationTimestampMillis());
        assertTrue(created.isActivated());

        assertEquals(0, created.getMadeAttempts());
        assertEquals(0, created.getMadeRetries());;
    }

    @Test
    public void assignReceiptToFreeTrialUserWithoutActiveITunesPaymentDetails() throws Exception {
        final String newReceipt = "NEW RECEIPT";
        final String productId = "PRODUCT ID";

        when(user.hasActiveITunesPaymentDetails()).thenReturn(false);
        when(user.isOnFreeTrial()).thenReturn(true);
        doReturn(null).when(paymentDetailsRepository).save(paymentDetailsCaptor.capture());
        when(appStoreReceiptParser.getProductId(newReceipt)).thenReturn(productId);
        PaymentPolicy paymentPolicy = mock(PaymentPolicy.class);
        when(paymentPolicyService.findByCommunityAndAppStoreProductId(community, productId)).thenReturn(paymentPolicy);

        iTunesPaymentDetailsService.createNewOrUpdatePaymentDetails(user, newReceipt, appStoreReceiptParser.getProductId(newReceipt));

        ITunesPaymentDetails created = paymentDetailsCaptor.getValue();
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(userService).skipFreeTrial(user);
        verify(paymentDetailsRepository).save(created);
        verify(userService).updateUser(user);
        verify(paymentPolicyService).findByCommunityAndAppStoreProductId(community, productId);
        verify(appStoreReceiptParser).getProductId(newReceipt);

        assertEquals(newReceipt, created.getAppStroreReceipt());
        assertEquals(user, created.getOwner());
        assertEquals(retriesOnError, created.getRetriesOnError());
        assertEquals(paymentPolicy, created.getPaymentPolicy());

        assertEquals(NONE, created.getLastPaymentStatus());
        assertTrue(0 < created.getCreationTimestampMillis());
        assertTrue(created.isActivated());

        assertEquals(0, created.getMadeAttempts());
        assertEquals(0, created.getMadeRetries());
    }

    @Test
    public void assignReceiptToNotFreeTrialUserWithoutActiveITunesPaymentDetails() throws Exception {
        final String newReceipt = "NEW RECEIPT";
        final String productId = "PRODUCT ID";

        when(user.hasActiveITunesPaymentDetails()).thenReturn(false);
        when(user.isOnFreeTrial()).thenReturn(false);
        doReturn(null).when(paymentDetailsRepository).save(paymentDetailsCaptor.capture());
        when(appStoreReceiptParser.getProductId(newReceipt)).thenReturn(productId);
        PaymentPolicy paymentPolicy = mock(PaymentPolicy.class);
        when(paymentPolicyService.findByCommunityAndAppStoreProductId(community, productId)).thenReturn(paymentPolicy);

        iTunesPaymentDetailsService.createNewOrUpdatePaymentDetails(user, newReceipt, appStoreReceiptParser.getProductId(newReceipt));

        ITunesPaymentDetails created = paymentDetailsCaptor.getValue();
        verify(paymentDetailsService).deactivateCurrentPaymentDetailsIfOneExist(user, "Commit new payment details");
        verify(userService, never()).skipFreeTrial(user);
        verify(paymentDetailsRepository).save(created);
        verify(userService).updateUser(user);
        verify(paymentPolicyService).findByCommunityAndAppStoreProductId(community, productId);
        verify(appStoreReceiptParser).getProductId(newReceipt);
    }
}