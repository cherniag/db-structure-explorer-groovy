package mobi.nowtechnologies.server.shared.message;

import java.util.Date;
import java.util.Locale;

public interface CommunityResourceBundleMessageSource {

    Locale DEFAULT_LOCALE = new Locale("");

    String getMessage(String community, String code, Object[] args, String defaultMessage, Locale locale);

    String getMessage(String community, String code, Object[] args, Locale locale);

    int readInt(String code, int defaults);

    int readInt(String community, String code, int defaultValue, Locale locale);

    Date readDate(String community, String code, Date defaults);

    Date readDate(String community, String code);

    boolean readBoolean(String community, String code, boolean defaults);

    String getDecryptedMessage(String community, String code, Object[] args, Locale locale);
}