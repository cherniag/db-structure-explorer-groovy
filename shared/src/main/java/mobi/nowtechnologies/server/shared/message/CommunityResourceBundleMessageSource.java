package mobi.nowtechnologies.server.shared.message;

import java.util.Locale;

public interface CommunityResourceBundleMessageSource {
	
	String getMessage(String community, String code, Object[] args, String defaultMessage, Locale locale);
	
	String getMessage(String community, String code, Object[] args, Locale locale) throws CommunityNoSuchMessageException;

    int readInt(String code, int defaults);
}