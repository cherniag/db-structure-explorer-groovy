/**
 * 
 */
package mobi.nowtechnologies.server.shared.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Titov Mykhaylo (titov)
 * 
 *         Validate international phone numbers. The numbers should start with a
 *         plus sign, followed by the country code and national number
 */
public class PhoneNumberValidator {
	
//	^         # Assert position at the beginning of the string.
//	\+        # Match a literal "+" character.
//	(?:       # Group but don't capture...
//	  [0-9]   #   Match a digit.
//	  \x20    #   Match a space character...
//	    ?     #     Between zero and one time.
//	)         # End the noncapturing group.
//	  {6,14}  #   Repeat the preceding group between 6 and 14 times.
//	[0-9]     # Match a digit.
//	$         # Assert position at the end of the string.

	private static final String PHONE_PATTERN = "^\\+?(?:[0-9] ?){6,14}[0-9]$";
	private final static Pattern pattern = Pattern.compile(PHONE_PATTERN);

	/**
	 * Validate hex with regular expression
	 * 
	 * @param aPhoneNumber
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public static boolean validate(final String aPhoneNumber) {
		if (aPhoneNumber == null)
			throw new NullPointerException("The parameter aPhoneNumber is null");
		Matcher matcher = pattern.matcher(aPhoneNumber);
		return matcher.matches();

	}

}
