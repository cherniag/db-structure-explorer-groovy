package mobi.nowtechnologies.server.service;

import static mobi.nowtechnologies.server.shared.Utils.getBigRandomInt;
import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * MigServiceTest
 *
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@Ignore
public class MigServiceTest extends TestCase {
	private static final String mobile = "00447580381128";
	private static final int operator = 1;
	
	private MigService service;

	@Override
	public void setUp()
		throws Exception {
		new ClassPathXmlApplicationContext(
				new String[] {"/META-INF/dao-test.xml", "/META-INF/service-test.xml" });
	}
	
	@Test
	public void testMigScenario() throws InterruptedException {
		service.sendFreeSms("" + getBigRandomInt(), operator, mobile, "Test free");
		service.sendPremiumSms("" + getBigRandomInt(), operator, mobile, "Test premium", "80988");
	}
}