package mobi.nowtechnologies.server.shared.message;

import mobi.nowtechnologies.common.util.LocaleUtils;

import java.util.Locale;
import static java.util.Locale.ENGLISH;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.Mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import static org.hamcrest.core.Is.is;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Locale.class, LocaleUtils.class, CommunityResourceBundleMessageSourceImpl.class, PropertyValueEncryptionUtils.class})
public class CommunityResourceBundleMessageSourceImplTest {

    @Mock
    ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;

    @Mock
    StringEncryptor stringEncryptor;

    @Mock
    LocaleUtils localeUtils;

    @InjectMocks
    CommunityResourceBundleMessageSourceImpl communityResourceBundleMessageSourceSpy;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldGetMessageWhenNoLocale() throws Exception {
        //given
        String community = "o2";
        String code = "some.code";
        Object[] args = new Object[0];
        String defaultMessage = null;
        Locale locale = null;

        Locale localeMock = mock(Locale.class);
        mockStatic(LocaleUtils.class);
        when(LocaleUtils.buildLocale(community, locale)).thenReturn(localeMock);

        String originalValue = "ENC(gg)";
        doReturn(originalValue).when(reloadableResourceBundleMessageSource).getMessage(code, args, defaultMessage, localeMock);

        mockStatic(PropertyValueEncryptionUtils.class);
        when(PropertyValueEncryptionUtils.isEncryptedValue(originalValue)).thenReturn(true);
        when(PropertyValueEncryptionUtils.decrypt(originalValue, stringEncryptor)).thenReturn("gg");

        //when
        String message = communityResourceBundleMessageSourceSpy.getMessage(community, code, args, defaultMessage, locale);

        //then
        assertThat(message, is(originalValue));

        verify(reloadableResourceBundleMessageSource, times(1)).getMessage(code, args, defaultMessage, localeMock);
        verifyStatic();
    }

    @Test
    public void shouldGetMessage() throws Exception {
        //given
        String community = "o2";
        String code = "some.code";
        Object[] args = new Object[0];
        String defaultMessage = null;
        Locale locale = ENGLISH;

        Locale localeMock = mock(Locale.class);
        mockStatic(LocaleUtils.class);
        when(LocaleUtils.buildLocale(community, locale)).thenReturn(localeMock);

        String originalValue = "ENC(gg)";
        doReturn(originalValue).when(reloadableResourceBundleMessageSource).getMessage(code, args, defaultMessage, localeMock);

        mockStatic(PropertyValueEncryptionUtils.class);
        when(PropertyValueEncryptionUtils.isEncryptedValue(originalValue)).thenReturn(true);
        when(PropertyValueEncryptionUtils.decrypt(originalValue, stringEncryptor)).thenReturn("gg");

        //when
        String message = communityResourceBundleMessageSourceSpy.getMessage(community, code, args, defaultMessage, locale);

        //then
        assertThat(message, is(originalValue));

        verify(reloadableResourceBundleMessageSource, times(1)).getMessage(code, args, defaultMessage, localeMock);
        verifyStatic();
    }

    @Test
    public void shouldGetDecryptedMessageWhenNoLocale() throws Exception {
        //given
        String community = "o2";
        String code = "some.code";
        Object[] args = new Object[0];
        String defaultMessage = null;
        Locale locale = null;

        Locale localeMock = mock(Locale.class);
        mockStatic(LocaleUtils.class);
        when(LocaleUtils.buildLocale(community, locale)).thenReturn(localeMock);

        String originalValue = "ENC(gg)";
        doReturn(originalValue).when(reloadableResourceBundleMessageSource).getMessage(code, args, defaultMessage, localeMock);

        mockStatic(PropertyValueEncryptionUtils.class);
        when(PropertyValueEncryptionUtils.isEncryptedValue(originalValue)).thenReturn(true);
        when(PropertyValueEncryptionUtils.decrypt(originalValue, stringEncryptor)).thenReturn("gg");

        //when
        String message = communityResourceBundleMessageSourceSpy.getDecryptedMessage(community, code, args, locale);

        //then
        assertThat(message, is("gg"));

        verify(reloadableResourceBundleMessageSource, times(1)).getMessage(code, args, defaultMessage, localeMock);
        verifyStatic();
    }

    @Test
    public void shouldGetDecryptedMessage() throws Exception {
        //given
        String community = "o2";
        String code = "some.code";
        Object[] args = new Object[0];
        String defaultMessage = null;
        Locale locale = ENGLISH;

        Locale localeMock = mock(Locale.class);
        mockStatic(LocaleUtils.class);
        when(LocaleUtils.buildLocale(community, locale)).thenReturn(localeMock);

        String originalValue = "ENC(gg)";
        doReturn(originalValue).when(reloadableResourceBundleMessageSource).getMessage(code, args, defaultMessage, localeMock);

        mockStatic(PropertyValueEncryptionUtils.class);
        when(PropertyValueEncryptionUtils.isEncryptedValue(originalValue)).thenReturn(true);
        when(PropertyValueEncryptionUtils.decrypt(originalValue, stringEncryptor)).thenReturn("gg");

        //when
        String message = communityResourceBundleMessageSourceSpy.getDecryptedMessage(community, code, args, locale);

        //then
        assertThat(message, is("gg"));

        verify(reloadableResourceBundleMessageSource, times(1)).getMessage(code, args, defaultMessage, localeMock);
        verifyStatic();
    }

    @Test
    public void shouldGetMessageWhenNoDefaultMessageAndNoLocale() throws Exception {
        //given
        String community = "o2";
        String code = "some.code";
        Object[] args = new Object[0];
        Locale locale = null;

        Locale localeMock = mock(Locale.class);
        mockStatic(LocaleUtils.class);
        when(LocaleUtils.buildLocale(community, locale)).thenReturn(localeMock);

        String originalValue = "ENC(gg)";
        doReturn(originalValue).when(reloadableResourceBundleMessageSource).getMessage(code, args, null, localeMock);

        mockStatic(PropertyValueEncryptionUtils.class);
        when(PropertyValueEncryptionUtils.isEncryptedValue(originalValue)).thenReturn(true);
        when(PropertyValueEncryptionUtils.decrypt(originalValue, stringEncryptor)).thenReturn("gg");

        //when
        String message = communityResourceBundleMessageSourceSpy.getMessage(community, code, args, locale);

        //then
        assertThat(message, is(originalValue));

        verify(reloadableResourceBundleMessageSource, times(1)).getMessage(code, args, null, localeMock);
        verifyStatic();
    }

    @Test
    public void shouldGetMessageWhenNoDefaultMessage() throws Exception {
        //given
        String community = "o2";
        String code = "some.code";
        Object[] args = new Object[0];
        Locale locale = ENGLISH;

        Locale localeMock = mock(Locale.class);
        mockStatic(LocaleUtils.class);
        when(LocaleUtils.buildLocale(community, locale)).thenReturn(localeMock);

        String originalValue = "ENC(gg)";
        doReturn(originalValue).when(reloadableResourceBundleMessageSource).getMessage(code, args, null, localeMock);

        mockStatic(PropertyValueEncryptionUtils.class);
        when(PropertyValueEncryptionUtils.isEncryptedValue(originalValue)).thenReturn(true);
        when(PropertyValueEncryptionUtils.decrypt(originalValue, stringEncryptor)).thenReturn("gg");

        //when
        String message = communityResourceBundleMessageSourceSpy.getMessage(community, code, args, locale);

        //then
        assertThat(message, is(originalValue));

        verify(reloadableResourceBundleMessageSource, times(1)).getMessage(code, args, null, localeMock);
        verifyStatic();
    }
}