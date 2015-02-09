package mobi.nowtechnologies.server.service.itunes.impl;

import mobi.nowtechnologies.server.persistence.domain.*;
import mobi.nowtechnologies.server.service.itunes.ITunesClient;
import mobi.nowtechnologies.server.service.itunes.ITunesConnectionConfig;
import mobi.nowtechnologies.server.service.itunes.ITunesResult;
import mobi.nowtechnologies.server.service.itunes.payment.ITunesPaymentService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.Locale;

import static org.junit.Assert.assertSame;
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
        userGroup.setCommunity(CommunityFactory.createCommunity());

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(1);
        user.setBase64EncodedAppStoreReceipt(null);
        user.setUserGroup(userGroup);

        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL);
        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD);

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.isSuccessful()).thenReturn(true);

        when(iTunesClient.verifyReceipt(configArgumentCaptor.capture(), eq(receipt))).thenReturn(iTunesResult);

        doNothing().when(iTunesPaymentService).createSubmittedPayment(user, receipt, iTunesResult, iTunesPaymentService);

        iTunesService.processInAppSubscription(user, receipt);

        verify(iTunesClient, times(1)).verifyReceipt(configArgumentCaptor.capture(), eq(receipt));
        verify(iTunesResult, times(1)).isSuccessful();
        verify(iTunesPaymentService, times(1)).createSubmittedPayment(user, receipt, iTunesResult, iTunesPaymentService);

        ITunesConnectionConfig config = configArgumentCaptor.getValue();
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, config.getUrl());
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, config.getPassword());
        verify(messageSource, times(2)).getMessage(eq(user.getCommunityRewriteUrl()), anyString(), isNull(Object[].class), isNull(Locale.class));

        verifyNoMoreInteractions(iTunesClient, iTunesPaymentService, messageSource);
    }

    @Test
    public void testSuccess_TokenAndStoredTokenNotNull() throws Exception {
        final String receipt = "receipt";

        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(CommunityFactory.createCommunity());

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(1);
        user.setBase64EncodedAppStoreReceipt("setBase64EncodedAppStoreReceipt");
        user.setUserGroup(userGroup);

        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL);
        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD);

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.isSuccessful()).thenReturn(true);

        when(iTunesClient.verifyReceipt(configArgumentCaptor.capture(), eq(receipt))).thenReturn(iTunesResult);

        doNothing().when(iTunesPaymentService).createSubmittedPayment(user, receipt, iTunesResult, iTunesPaymentService);

        iTunesService.processInAppSubscription(user, receipt);

        verify(iTunesClient, times(1)).verifyReceipt(configArgumentCaptor.capture(), eq(receipt));
        verify(iTunesResult, times(1)).isSuccessful();
        verify(iTunesPaymentService, times(1)).createSubmittedPayment(user, receipt, iTunesResult, iTunesPaymentService);

        ITunesConnectionConfig config = configArgumentCaptor.getValue();
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, config.getUrl());
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, config.getPassword());
        verify(messageSource, times(2)).getMessage(eq(user.getCommunityRewriteUrl()), anyString(),  isNull(Object[].class), isNull(Locale.class));

        verifyNoMoreInteractions(iTunesClient, iTunesPaymentService, messageSource);
    }

    @Test
    public void testFailure() throws Exception {
        final UserGroup userGroup = UserGroupFactory.createUserGroup();
        userGroup.setCommunity(CommunityFactory.createCommunity());

        final User user = UserFactory.createUser(ActivationStatus.ACTIVATED);
        user.setId(1);
        user.setBase64EncodedAppStoreReceipt("setBase64EncodedAppStoreReceipt");
        user.setUserGroup(userGroup);

        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL);
        when(messageSource.getMessage(user.getCommunityRewriteUrl(), ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, null, null)).thenReturn(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD);

        ITunesResult iTunesResult = mock(ITunesResult.class);
        when(iTunesResult.isSuccessful()).thenReturn(false);

        when(iTunesClient.verifyReceipt(any(ITunesConnectionConfig.class), eq(user.getBase64EncodedAppStoreReceipt()))).thenReturn(iTunesResult);

        doNothing().when(iTunesPaymentService).createSubmittedPayment(user, user.getBase64EncodedAppStoreReceipt(), iTunesResult, iTunesPaymentService);

        iTunesService.processInAppSubscription(user, null);

        verify(iTunesClient, times(1)).verifyReceipt(configArgumentCaptor.capture(), eq(user.getBase64EncodedAppStoreReceipt()));
        verify(iTunesResult, times(1)).isSuccessful();

        ITunesConnectionConfig config = configArgumentCaptor.getValue();
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_I_TUNES_URL, config.getUrl());
        assertSame(ITunesConnectionConfig.APPLE_IN_APP_PASSWORD, config.getPassword());
        verify(messageSource, times(2)).getMessage(eq(user.getCommunityRewriteUrl()), anyString(),  isNull(Object[].class), isNull(Locale.class));

        verifyNoMoreInteractions(iTunesClient, iTunesPaymentService, messageSource);
    }

}