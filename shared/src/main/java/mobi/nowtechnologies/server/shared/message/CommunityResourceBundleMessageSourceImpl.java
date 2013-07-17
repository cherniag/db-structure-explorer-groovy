package mobi.nowtechnologies.server.shared.message;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.apache.commons.lang.StringUtils.trim;

public class CommunityResourceBundleMessageSourceImpl extends ReloadableResourceBundleMessageSource implements CommunityResourceBundleMessageSource {
    public static final String DEFAULT_COMMUNITY_DELIM = "_";

    private String communityDelim = DEFAULT_COMMUNITY_DELIM;
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public Date readDate(String code, Date defaults) {
        try {
            String dateString = trim(getMessage(null, code, null, null));
            Date date = format.parse(dateString);
            return date;
        } catch (Exception e) {
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
    public String getMessage(String code, String defaults){
        try {
            return trim(getMessage(null, code, null, null));
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