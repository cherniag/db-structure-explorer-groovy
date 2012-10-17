package mobi.nowtechnologies.server.shared.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anton
 * 
 */
public class URLValidation {
	private final static String URL_PATTERN = 
		"^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	private final static Pattern pattern = Pattern.compile(URL_PATTERN);

	public static boolean validate(final String url) {
		if (url == null)
			throw new NullPointerException("The parameter url is null");
		Matcher matcher = pattern.matcher(url);
		return matcher.matches();
	}
}
