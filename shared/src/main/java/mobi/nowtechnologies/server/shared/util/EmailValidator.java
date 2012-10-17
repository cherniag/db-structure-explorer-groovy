package mobi.nowtechnologies.server.shared.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Titov Mykhaylo (titov)
 * 
 **/

public class EmailValidator {
	private final static String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private final static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	public static boolean validate(final String email) {
		if (email == null)
			throw new NullPointerException("The parameter email is null");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

}
