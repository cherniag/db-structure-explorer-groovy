package mobi.nowtechnologies.server.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertNotNull;

/**
 * @generatedBy CodePro at 01.07.11 9:41
 * @author Titov Mykhaylo (titov)
 */
@Ignore
public class MediaServiceTest {
	private static MediaService mediaService;

	@Test
	public void testMediaService()
		throws Exception {
		assertNotNull(mediaService);
	}


	@BeforeClass
	public static void setUp()
		throws Exception {
		ClassPathXmlApplicationContext appServiceContext = new ClassPathXmlApplicationContext(
				new String[] {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" });
		mediaService = (MediaService) appServiceContext.getBean("service.MediaService");
	}
}