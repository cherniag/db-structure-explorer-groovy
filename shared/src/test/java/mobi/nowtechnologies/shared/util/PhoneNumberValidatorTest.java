package mobi.nowtechnologies.shared.util;

import java.util.Arrays;
import java.util.Collection;

import mobi.nowtechnologies.server.shared.util.PhoneNumberValidator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class <code>PhoneNumberValidatorTest</code> contains tests for the class
 * <code>{@link PhoneNumberValidator}</code>.
 * 
 * @author Titov Mykhaylo (titov)
 */
@RunWith(value = Parameterized.class)
public class PhoneNumberValidatorTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PhoneNumberValidatorTest.class.getName());
	private String phoneNumber;
	private boolean expectedValue;

	/**
	 * @param aPhoneNumber
	 */
	public PhoneNumberValidatorTest(String aPhoneNumber, boolean aExpectedValue) {
		super();
		this.phoneNumber = aPhoneNumber;
		this.expectedValue = aExpectedValue;
	}

	@Parameters
	public static Collection dataParameters() {
		return Arrays.asList(new Object[][] {
				{ "+380913008666", true },// Ukraine
				{ "+38091 3008666", true },// Ukraine
				{ "3(809)13008666", false },// Ukraine
				{ "+3(809)13008666", false },// Ukraine
				{ "+3(809)-130-08-666", false },// Ukraine
				{ "+14082223344", true },// USA
				{ "+447621234567", true },// GB
				{ "+33643123456", true },// French
				{ "+493098765432101", true },// Germany
				{ "+3805012345677", true },// Ukraine
				{ "+3 805012345677", true },// Ukraine
				{ "+3 8 05012345677", true },// Ukraine
				{ "+4 930 98765432101", true },// Germany
				{ "+4 9 3 0 9 8 7 6 5 432101", true },// Germany
				{ "00447580381128", true },// Germany
				});
	}

	@Test
	public void testValidate() {
		boolean isValid = PhoneNumberValidator.validate(phoneNumber);
		if (expectedValue!=isValid)
			LOGGER.error("Expected value: " + expectedValue
					+ " but test returned " + isValid +" for "+phoneNumber);
		Assert.assertEquals(expectedValue, isValid);
	}
}