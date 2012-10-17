package mobi.nowtechnologies.server.shared.message;

import java.util.Locale;

public interface CommunityResourceBundleMessageSource {
	
	public String getMessage(String community, String code, Object[] args, String defaultMessage, Locale locale);
	
	public String getMessage(String community, String code, Object[] args, Locale locale) throws CommunityNoSuchMessageException;
}