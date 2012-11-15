package mobi.nowtechnologies.server.shared.message;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

public class CommunityResourceBundleMessageSourceImpl1 extends ReloadableResourceBundleMessageSource {
	public static final String DEFAULT_COMMUNITY_DELIM = ".";
	
	private String communityDelim = DEFAULT_COMMUNITY_DELIM;
	
	public final String getMessage(String community, String code, Object[] args, String defaultMessage, Locale locale) {
		String fullCode = community+communityDelim+code;
		String msg = getMessageInternal(fullCode, args, locale);
		if (msg != null) {
			return msg;
		}
		
		if (defaultMessage == null) {
			String fallback = getMessageInternal(code, args, locale);
			if (fallback != null) {
				return fallback;
			}
			
			fallback = getDefaultMessage(code);
			if (fallback != null) {
				return fallback;
			}
		}
		
		return renderDefaultMessage(defaultMessage, args, locale);
	}

	public final String getMessage(String community, String code, Object[] args, Locale locale) throws CommunityNoSuchMessageException {
		String fullCode = community+communityDelim+code;
		String msg = getMessageInternal(fullCode, args, locale);
		if (msg != null) {
			return msg;
		}
		
		String fallback = getMessageInternal(code, args, locale);
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