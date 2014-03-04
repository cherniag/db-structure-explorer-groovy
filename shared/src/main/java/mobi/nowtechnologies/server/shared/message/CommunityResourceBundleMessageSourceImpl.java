package mobi.nowtechnologies.server.shared.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.trim;


public class CommunityResourceBundleMessageSourceImpl extends ReloadableResourceBundleMessageSource implements CommunityResourceBundleMessageSource {
    private static Logger LOGGER = LoggerFactory.getLogger(CommunityResourceBundleMessageSourceImpl.class);

    public static final String DEFAULT_COMMUNITY_DELIM = "_";

    private String communityDelim = DEFAULT_COMMUNITY_DELIM;
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Date readDate(String community, String code, Date defaults) {
        try {
            String dateString = trim(getMessage(community, code, null, null));
            Date date = format.parse(dateString);
            return date;
        } catch (Exception e) {
            return defaults;
        }
    }

    @Override
    public boolean readBoolean(String community, String code, boolean defaults) {
        try {
            String booleanString = trim(getMessage(community, code, null, String.valueOf(defaults), null));
            boolean booleanValue = Boolean.parseBoolean(booleanString);
            return booleanValue;
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            return defaults;
        }
    }

    @Override
    public int readInt(String code, int defaults) {
        try {
            String pediodString = trim(getMessage(null, code, null, null));
            int period = Integer.parseInt(pediodString);
            return period;
        } catch (RuntimeException e) {
            return defaults;
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
}