package mobi.nowtechnologies.server.shared.message;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class CommunityResourceBundleMessageSourceImpl extends ReloadableResourceBundleMessageSource implements CommunityResourceBundleMessageSource {
	public static final String DEFAULT_COMMUNITY_DELIM = "_";
	
	private String communityDelim = DEFAULT_COMMUNITY_DELIM;
	
	@Override
	public String getMessage(String community, String code, Object[] args, String defaultMessage, Locale locale) {
		Locale communityLocale = new Locale(community);
		if(locale != null)
			communityLocale = new Locale(community+communityDelim+locale.getLanguage(), locale.getCountry(), locale.getVariant());
		
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
		Locale communityLocale = (null==community)?new Locale(""):new Locale(community);
		if(locale != null)
			communityLocale = new Locale(community+communityDelim+locale.getLanguage(), locale.getCountry(), locale.getVariant());
		
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