package mobi.nowtechnologies.shared.util;

import mobi.nowtechnologies.server.shared.util.EmailValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * The class <code>EmailValidatorTest</code> contains tests for the class
 * <code>{@link EmailValidator}</code>.
 * 
 * @generatedBy CodePro at 06.07.11 12:25
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@RunWith(value = Parameterized.class)
public class EmailValidatorTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EmailValidatorTest.class.getName());
	private String email;
	private boolean expectedValue;

	/**
	 * @param aEmail
	 */
	public EmailValidatorTest(String aEmail, boolean aExpectedValue) {
		super();
		this.email = aEmail;
		this.expectedValue = aExpectedValue;
	}

	@Parameters
	public static Collection dataParameters() {
		return Arrays.asList(new Object[][] { { "mkyong@yahoo.com", true },
				{ "mkyong-100@yahoo.com", true },
				{ "mkyong.100@yahoo.com", true },
				{ "mkyong111@mkyong.com", true },
				{ "mkyong-100@mkyong.net", true },
				{ "mkyong.100@mkyong.com.au", true }, { "mkyong@1.com", true },
				{ "mkyong@gmail.com.com", true }, { "mkyong", false },
				{ "mkyong@.com.my", false }, { "mkyong123@gmail.a", false },
				{ "mkyong123@.com", false }, { "mkyong123@.com.com", false },
				{ ".mkyong@mkyong.com", false },
				{ "mkyong()*@gmail.com", false }, { "mkyong@%*.com", false },
				{ "mkyong..2002@gmail.com", false },
				{ "mkyong.@gmail.com", false },
				{ "mkyong@mkyong@gmail.com", false },
				{ "mkyong@gmail.com.1a", false } });
	}

	@Test
	public void testValidate() {
		boolean isValid = EmailValidator.isEmail(email);
		if (expectedValue!=isValid)
			LOGGER.error("Expected value: " + expectedValue
					+ " but test returned " + isValid +" for "+email);
		assertEquals(expectedValue, isValid);
	}
}