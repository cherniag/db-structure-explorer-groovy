package mobi.nowtechnologies.server.shared.message;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.jasypt.properties.PropertyValueEncryptionUtils.decrypt;
import static org.jasypt.properties.PropertyValueEncryptionUtils.isEncryptedValue;


public class CommunityResourceBundleMessageSourceImpl implements CommunityResourceBundleMessageSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityResourceBundleMessageSourceImpl.class);

    private static final String DEFAULT_COMMUNITY_DELIMITER = "_";
    public static final String DATE_FORMAT = "dd-MM-yyyy";
    private final Locale DEFAULT_LOCALE = new Locale("");

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
        Locale communityLocale = getCommunityLocale(community, locale);

        return reloadableResourceBundleMessageSource.getMessage(code, args, defaultMessage, communityLocale);
    }

    @Override
    public String getDecryptedMessage(String community, String code, Object[] args, Locale locale) {
        String message = getMessage(community, code, args, locale);
        return convertPropertyValue(message);
    }

    private Locale getCommunityLocale(String community, Locale locale) {
        Locale communityLocale = isNull(community) ? DEFAULT_LOCALE : new Locale(community);
        if (isNotNull(locale))
            communityLocale = new Locale(community + DEFAULT_COMMUNITY_DELIMITER + locale.getLanguage(), locale.getCountry(), locale.getVariant());
        return communityLocale;
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
