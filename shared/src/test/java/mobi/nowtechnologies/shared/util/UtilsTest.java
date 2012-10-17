package mobi.nowtechnologies.shared.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.nowtechnologies.server.shared.Utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
	
	@Test
	public void getRandomPassword() {
		Pattern pattern = Pattern.compile("\\d{6}");
	    
	      
		String randomPassword = Utils.getRandomString(6);
		
		Matcher matcher = pattern.matcher(randomPassword);
		
		Assert.assertEquals(6, randomPassword.length());
		Assert.assertEquals(true, matcher.matches());
	}
}