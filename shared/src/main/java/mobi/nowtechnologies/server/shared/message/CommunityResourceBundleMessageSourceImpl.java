package mobi.nowtechnologies.server.shared.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.trim;


public class CommunityResourceBundleMessageSourceImpl extends ReloadableResourceBundleMessageSource implements CommunityResourceBundleMessageSource {
    private static Logger LOGGER = LoggerFactory.getLogger(CommunityResourceBundleMessageSourceImpl.class);

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String DEFAULT_COMMUNITY_DELIM = "_";

    private String communityDelim = DEFAULT_COMMUNITY_DELIM;

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
            String booleanString = trim(getMessage(community, code, null, String.valueOf(defaults), null));
            return  Boolean.parseBoolean(booleanString);
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
            String periodString = trim(getMessage(community, code, null, locale));
            return Integer.parseInt(periodString);
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    @Override
    public String getMessage(String community, String code, Object[] args, String defaultMessage, Locale locale) {
        Locale communityLocale = new Locale(community);
        if (locale != null)
            communityLocale = new Locale(community + communityDelim + locale.getLanguage(), locale.getCountry(), locale.getVariant());

        String msg = getMessageInternal(code, args, communityLocale);
        if (msg != null) {
            return msg;
        }

        if (defaultMessage == null) {
            String fallback = getMessageInternal(code, args, communityLocale);
            if (fallback != null) {
                return fallback;
            }

            fallback = getDefaultMessage(code);
            if (fallback != null) {
                return fallback;
            }
        }

        return renderDefaultMessage(defaultMessage, args, communityLocale);
    }

    @Override
    public String getMessage(String community, String code, Object[] args, Locale locale) throws CommunityNoSuchMessageException {
        Locale communityLocale = (null == community) ? new Locale("") : new Locale(community);
        if (locale != null)
            communityLocale = new Locale(community + communityDelim + locale.getLanguage(), locale.getCountry(), locale.getVariant());

        String msg = getMessageInternal(code, args, communityLocale);
        if (msg != null) {
            return msg;
        }

        String fallback = getMessageInternal(code, args, communityLocale);
        if (fallback != null) {
            return fallback;
        }

        fallback = getDefaultMessage(code);
        if (fallback != null) {
            return fallback;
        }

        throw new CommunityNoSuchMessageException(community, code, locale);
    }

    public String getCommunityDelim() {
        return communityDelim;
    }

    public void setCommunityDelim(String communityDelim) {
        this.communityDelim = communityDelim != null ? communityDelim : DEFAULT_COMMUNITY_DELIM;
    }

    private Date doConvertToDate(String message) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).parse(trim(message));
    }
}
