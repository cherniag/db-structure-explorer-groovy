/**
 * 
 */
package mobi.nowtechnologies.server.shared.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class CreditCardNumberValidator {
	private static final String CARD_NUMBER_PATTERN = "\\d{13}|\\d{16}";
	private final static Pattern pattern = Pattern.compile(CARD_NUMBER_PATTERN);
	
	public static boolean validate(String aCardNumber){
		Matcher matcher = pattern.matcher(aCardNumber);
		return matcher.matches();
	}
}
