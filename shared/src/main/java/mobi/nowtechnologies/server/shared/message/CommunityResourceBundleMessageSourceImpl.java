package mobi.nowtechnologies.server.shared.message;

import mobi.nowtechnologies.common.util.LocaleUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.jasypt.properties.PropertyValueEncryptionUtils.decrypt;
import static org.jasypt.properties.PropertyValueEncryptionUtils.isEncryptedValue;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.Assert;


public class CommunityResourceBundleMessageSourceImpl implements CommunityResourceBundleMessageSource {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityResourceBundleMessageSourceImpl.class);

    private ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;
    private StringEncryptor stringEncryptor;

    public void setReloadableResourceBundleMessageSource(ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource) {
        this.reloadableResourceBundleMessageSource = reloadableResourceBundleMessageSource;
    }

    public void setStringEncryptor(StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }

    @Override
    public Date readDate(String community, String code, Date defaults) {
        try {
            String message = getMessage(community, code, null, null);
            return doConvertToDate(message);
        } catch (Exception e) {
            return defaults;
        }
    }

    @Override
    public Date readDate(String community, String code) {
        try {
            String message = getMessage(community, code, null, null);

            Assert.hasText(message);

            return doConvertToDate(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean readBoolean(String community, String code, boolean defaults) {
        try {
            return parseBoolean(trim(getMessage(community, code, null, String.valueOf(defaults), null)));
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            return defaults;
        }
    }

    @Override
    public int readInt(String code, int defaults) {
        return readInt(null, code, defaults, null);
    }

    @Override
    public int readInt(String community, String code, int defaultValue, Locale locale) {
        try {
            return parseInt(trim(getMessage(community, code, null, locale)));
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    @Override
    public String getMessage(String community, String code, Object[] args, Locale locale) {
        return getMessage(community, code, args, null, locale);
    }

    @Override
    public String getMessage(String community, String code, Object[] args, String defaultMessage, Locale locale) {
        Locale communityLocale = LocaleUtils.buildLocale(community, locale);

        return reloadableResourceBundleMessageSource.getMessage(code, args, defaultMessage, communityLocale);
    }

    @Override
    public String getDecryptedMessage(String community, String code, Object[] args, Locale locale) {
        String message = getMessage(community, code, args, locale);
        return convertPropertyValue(message);
    }

    private Date doConvertToDate(String message) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).parse(trim(message));
    }

    private String convertPropertyValue(final String originalValue) {
        if (!isEncryptedValue(originalValue)) {
            return originalValue;
        }
        return decrypt(originalValue, stringEncryptor);
    }
}
