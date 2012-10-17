package mobi.nowtechnologies.server.shared.message;

import java.util.Locale;

public class CommunityNoSuchMessageException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CommunityNoSuchMessageException(String community, String code, Locale locale) {
		super("No message found under code '" + code + "' for locale '" + locale + "' in community'" + community + "'.");
	}

	public CommunityNoSuchMessageException(String community, String code) {
		super("No message found under code '" + code + "' for locale '" + Locale.getDefault() + "' in community'" + community + "'." );
	}
}
