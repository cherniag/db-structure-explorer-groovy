package mobi.nowtechnologies.server.service.message;

import static org.junit.Assert.assertEquals;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSourceImpl;

import org.junit.Before;
import org.junit.Test;

public class CommunityResourceBundleMessageSourceTest {
	private static final String PROPERTY_CODE = "promoted.device.models";
	private static final String PROPERTY_VALUE = "";
	private static final String SAMSUNG_COMMUNITY = "samsung";
	private static final String NOT_SAMSUNG_COMMUNITY = "notsamsung";
	private static final String SAMSUNG_PROPERTY_VALUE = "12345";

	private CommunityResourceBundleMessageSourceImpl messageSource;

	@Before
	public void initMessageSource() {
		messageSource = new CommunityResourceBundleMessageSourceImpl();
		messageSource.setBasenames(new String[] { "classpath:services_test" });
		messageSource.setDefaultEncoding("utf8");
		messageSource.setCacheSeconds(180);
		messageSource.setUseCodeAsDefaultMessage(true);
	}

	@Test
	public void getMessage() {
		String msg = messageSource.getMessage(SAMSUNG_COMMUNITY, PROPERTY_CODE, null, null, null);
		assertEquals(SAMSUNG_PROPERTY_VALUE, msg);

		msg = messageSource.getMessage(NOT_SAMSUNG_COMMUNITY, PROPERTY_CODE, null, null, null);
		assertEquals(PROPERTY_VALUE, msg);
	}
	
	@Test
	public void getPinSmsText_Successful() {
		String code2 = Utils.getRandomString(4);
		String code = "4567";
		Object[] args = {code};
		String msg = messageSource.getMessage("sms.freeMsg", args, null);
		assertEquals("FreeMsg:\nHere is your PSMS PIN code:\n"+code+".\nPlease use this code to complete your subscription", msg);
		assertEquals(4, code2.length());
	}
}