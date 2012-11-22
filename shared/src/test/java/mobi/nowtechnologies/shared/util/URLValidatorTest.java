package mobi.nowtechnologies.shared.util;

import mobi.nowtechnologies.server.shared.util.URLValidation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class URLValidatorTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(URLValidatorTest.class.getName());
	private String url;
	private boolean expectedValue;

	/**
	 * @param url
	 */
	public URLValidatorTest(String url, boolean aExpectedValue) {
		super();
		this.url = url;
		this.expectedValue = aExpectedValue;
	}

	@Parameters
	public static Collection dataParameters() {
		return Arrays.asList(new Object[][] { { "http://91.216.137.155:8105/mig/mig-chartsnow/test.asp", true },
				{ "https://91.216.137.155:8105/mig/mig-chartsnow/test.asp", true },
				{ "http://i.ua", true },
				{ "http://91.216.137.155/test.asp", true },
				{ "http://testUrl/test.asp", true },
				{ "http://testUrl/test.asp?param=value", true },
				{ "http://testUrl/test.asp?param=value&param1=value1", true },
				{ "testUrl", false },
				{ "://testUrl", false },
				{ "testUrl.com", false },
				{ ".testUrl.com", false },
				{ "testUrl.234", false },
				{ "10.20.30.4.45", false },
				{ "testUrl\\", false },
				{ "ftb://testUrl", false },
				{ "test.-+", false } });
	}

	@Test
	public void testValidate() {
		boolean isValid = URLValidation.validate(url);
		if (expectedValue!=isValid)
			LOGGER.error("Expected value: " + expectedValue
					+ " but test returned " + isValid +" for "+url);
		Assert.assertEquals(expectedValue, isValid);
	}
}