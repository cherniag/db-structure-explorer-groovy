package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.persistence.domain.CommunityFactory;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.UserGroup;
import mobi.nowtechnologies.server.persistence.domain.UserGroupFactory;
import mobi.nowtechnologies.server.persistence.domain.UserStatus;
import mobi.nowtechnologies.server.service.ITunesPaymentDetailsService;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import java.util.Locale;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ITunesServiceImplTest {

    @Mock
    ITunesClient iTunesClient;
    @Mock
    ITunesPaymentService iTunesPaymentService;
    @Mock
    CommunityResourceBundleMessageSource messageSource;
    @Mock
    ITunesPaymentDetailsService iTunesPaymentDetailsService;
    @InjectMocks
    ITunesServiceImpl iTunesService;
    @Captor
    ArgumentCaptor<ITunesConnectionConfig> configArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSuccess_TokenNotNullAndStoredTokenIsNull() throws Exception {
        final String receipt = "receipt";

        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(CommunityFactory.createCommunity("o2"));

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(1);
        user.setBase64EncodedAppStoreReceipt(null);
        user.setUserGroup(userGroup);

        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL);
        when(messageSource.getDecryptedMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD);

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.isSuccessful()).thenReturn(true);

        when(iTunesClient.verifyReceipt(configArgumentCaptor.capture(), eq(receipt))).thenReturn(iTunesResult);

        doNothing().when(iTunesPaymentService).createSubmittedPayment(user, receipt, iTunesResult);

        iTunesService.processInAppSubscription(user, receipt);

        verify(iTunesClient, times(1)).verifyReceipt(configArgumentCaptor.capture(), eq(receipt));
        verify(iTunesResult, times(1)).isSuccessful();
        verify(iTunesPaymentService, times(1)).createSubmittedPayment(user, receipt, iTunesResult);

        ITunesConnectionConfig config = configArgumentCaptor.getValue();
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, config.getUrl());
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, config.getPassword());
        verify(messageSource, times(1)).getMessage(eq(user.getCommunityRewriteUrl()), anyString(), isNull(Object[].class), isNull(Locale.class));
        verify(messageSource, times(1)).getDecryptedMessage(eq(user.getCommunityRewriteUrl()), anyString(), isNull(Object[].class), isNull(Locale.class));

        verifyNoMoreInteractions(iTunesClient, iTunesPaymentService, messageSource);
    }

    @Test
    public void testSuccess_TokenAndStoredTokenNotNull() throws Exception {
        final String receipt = "receipt";

        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(CommunityFactory.createCommunity("o2"));

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(1);
        user.setBase64EncodedAppStoreReceipt("setBase64EncodedAppStoreReceipt");
        user.setUserGroup(userGroup);

        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL);
        when(messageSource.getDecryptedMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD);

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.isSuccessful()).thenReturn(true);

        when(iTunesClient.verifyReceipt(configArgumentCaptor.capture(), eq(receipt))).thenReturn(iTunesResult);

        doNothing().when(iTunesPaymentService).createSubmittedPayment(user, receipt, iTunesResult);

        iTunesService.processInAppSubscription(user, receipt);

        verify(iTunesClient, times(1)).verifyReceipt(configArgumentCaptor.capture(), eq(receipt));
        verify(iTunesResult, times(1)).isSuccessful();
        verify(iTunesPaymentService, times(1)).createSubmittedPayment(user, receipt, iTunesResult);

        ITunesConnectionConfig config = configArgumentCaptor.getValue();
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, config.getUrl());
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, config.getPassword());
        verify(messageSource, times(1)).getMessage(eq(user.getCommunityRewriteUrl()), anyString(), isNull(Object[].class), isNull(Locale.class));
        verify(messageSource, times(1)).getDecryptedMessage(eq(user.getCommunityRewriteUrl()), anyString(), isNull(Object[].class), isNull(Locale.class));

        verifyNoMoreInteractions(iTunesClient, iTunesPaymentService, messageSource);
    }

    @Test
    public void testFailure() throws Exception {
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(CommunityFactory.createCommunity("o2"));

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(1);
        user.setBase64EncodedAppStoreReceipt("setBase64EncodedAppStoreReceipt");
        user.setUserGroup(userGroup);
        user.getStatus().setName(UserStatus.LIMITED);

        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL);
        when(messageSource.getDecryptedMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD);

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.isSuccessful()).thenReturn(false);

        when(iTunesClient.verifyReceipt(any(ITunesConnectionConfig.class), eq(user.getBase64EncodedAppStoreReceipt()))).thenReturn(iTunesResult);

        doNothing().when(iTunesPaymentService).createSubmittedPayment(user, user.getBase64EncodedAppStoreReceipt(), iTunesResult);

        iTunesService.processInAppSubscription(user, null);

        verify(iTunesClient, times(1)).verifyReceipt(configArgumentCaptor.capture(), eq(user.getBase64EncodedAppStoreReceipt()));
        verify(iTunesResult, times(1)).isSuccessful();

        ITunesConnectionConfig config = configArgumentCaptor.getValue();
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, config.getUrl());
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, config.getPassword());
        verify(messageSource, times(1)).getMessage(eq(user.getCommunityRewriteUrl()), anyString(), isNull(Object[].class), isNull(Locale.class));
        verify(messageSource, times(1)).getDecryptedMessage(eq(user.getCommunityRewriteUrl()), anyString(), isNull(Object[].class), isNull(Locale.class));

        verifyNoMoreInteractions(iTunesClient, iTunesPaymentService, messageSource);
    }

    @Test
    public void processInAppSubscriptionO2CommunityUserWithoutActivePaymentDetails() throws Exception {
        final String receipt = "RECEIPT";
        final User user = mock(User.class);
        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.decideAppReceipt(receipt)).thenReturn(receipt);
        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesClient.verifyReceipt(configArgumentCaptor.capture(), eq(receipt))).thenReturn(iTunesResult);

        iTunesService.processInAppSubscription(user, receipt);

        verify(iTunesPaymentDetailsService, never()).createNewOrUpdatePaymentDetails(eq(user), anyString());
        ITunesConnectionConfig connectionConfig = configArgumentCaptor.getValue();
        verify(iTunesClient, times(1)).verifyReceipt(connectionConfig, receipt);
    }

    @Test
    public void processInAppSubscriptionO2CommunityUserWithActivePaymentDetails() throws Exception {
        final String receipt = "RECEIPT";
        final User user = mock(User.class);
        when(user.isO2CommunityUser()).thenReturn(true);
        when(user.hasActivePaymentDetails()).thenReturn(true);

        iTunesService.processInAppSubscription(user, receipt);

        verify(iTunesPaymentDetailsService, never()).createNewOrUpdatePaymentDetails(eq(user), anyString());
        verify(iTunesClient, never()).verifyReceipt(configArgumentCaptor.capture(), anyString());
    }

    @Test
    public void processInAppSubscriptionO2CommunityUserWithoutActivePaymentDetailsAndNullReceiptAndDoesNotHaveAppReceiptInLimitedStat() throws Exception {
        final String receipt = null;
        final User user = mock(User.class);
        when(user.isO2CommunityUser()).thenReturn(true);
        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.hasAppReceiptAndIsInLimitedState()).thenReturn(false);

        iTunesService.processInAppSubscription(user, null);

        verify(iTunesPaymentDetailsService, never()).createNewOrUpdatePaymentDetails(eq(user), anyString());
        verify(iTunesClient, never()).verifyReceipt(configArgumentCaptor.capture(), anyString());
    }

    @Test
    public void processInAppSubscriptionO2CommunityUserWithoutActivePaymentDetailsAndNullReceiptAndHasAppReceiptInLimitedState() throws Exception {
        final String receipt = "RECEIPT";
        final User user = mock(User.class);
        when(user.isO2CommunityUser()).thenReturn(true);
        when(user.hasActivePaymentDetails()).thenReturn(false);
        when(user.hasAppReceiptAndIsInLimitedState()).thenReturn(true);
        when(user.decideAppReceipt(null)).thenReturn(receipt);
        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesClient.verifyReceipt(configArgumentCaptor.capture(), eq(receipt))).thenReturn(iTunesResult);

        iTunesService.processInAppSubscription(user, null);

        verify(iTunesPaymentDetailsService, never()).createNewOrUpdatePaymentDetails(eq(user), anyString());
        ITunesConnectionConfig connectionConfig = configArgumentCaptor.getValue();
        verify(iTunesClient, times(1)).verifyReceipt(connectionConfig, receipt);
    }
}