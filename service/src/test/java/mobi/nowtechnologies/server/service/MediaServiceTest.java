package mobi.nowtechnologies.server.service;

import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * The class <code>MediaServiceTest</code> contains tests for the class <code>{@link MediaService}</code>.
 *
 * @generatedBy CodePro at 01.07.11 9:41
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
@Ignore
public class MediaServiceTest {
	private static MediaService mediaService;
	/**
	 * Run the MediaService() constructor test.
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 */
	@Test
	public void testMediaService()
		throws Exception {
		assertNotNull(mediaService);
		// add additional test code here
	}

/*	*//**
	 * Run the Object[] buyTrack(int,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 *//*
	@Test
	public void testBuyTrack_AlreadyPurchased()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = 47;
		String aCommunityName="CN Commercial Beta";

		Object[] result = mediaService.buyTrack(aUserId, aMediaUID, aCommunityName);
		
		assertNotNull(result);
		assertTrue(2==result.length);
		Class firstElementClass=result[0].getClass();
		assertTrue (firstElementClass.equals(AccountCheckDTO.class)||firstElementClass.equals(BuyTrack.class));
		Class secondElementClass=result[1].getClass();
		assertTrue (secondElementClass.equals(AccountCheckDTO.class)||secondElementClass.equals(BuyTrack.class));
		
		assertFalse(firstElementClass.equals(secondElementClass));
		
		BuyTrack buyTrack;
		if (firstElementClass.equals(BuyTrack.class)){
			buyTrack=(BuyTrack) result[0];
		}else{
			buyTrack=(BuyTrack) result[1];
		}
		assertEquals("ALREADYPURCHASED",buyTrack.getStatusValue());
	}

	*//**
	 * Run the Object[] buyTrack(int,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 *//*
	@Test
	public void testBuyTrack_NotDownloadStatus()
		throws Exception {
		int aUserId = 10;
		int aMediaUID = 47;
		String aCommunityName="CN Commercial Beta";

		Object[] result = mediaService.buyTrack(aUserId, aMediaUID, aCommunityName);

		assertNotNull(result);
		assertTrue(2==result.length);
		Class firstElementClass=result[0].getClass();
		assertTrue (firstElementClass.equals(AccountCheckDTO.class)||firstElementClass.equals(BuyTrack.class));
		Class secondElementClass=result[1].getClass();
		assertTrue (secondElementClass.equals(AccountCheckDTO.class)||secondElementClass.equals(BuyTrack.class));
		assertFalse(firstElementClass.equals(secondElementClass));
		
		BuyTrack buyTrack;
		if (firstElementClass.equals(BuyTrack.class)){
			buyTrack=(BuyTrack) result[0];
		}else{
			buyTrack=(BuyTrack) result[1];
		}
		assertEquals("NOTDOWNLOAD",buyTrack.getStatusValue());
	}

	*//**
	 * Run the Object[] buyTrack(int,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 *//*
	@Test
	@Ignore
	public void testBuyTrack_Ok()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = 1;
		String aCommunityName="CN Commercial Beta";

		Object[] result = mediaService.buyTrack(aUserId, aMediaUID,aCommunityName);

		assertNotNull(result);
		assertTrue(2==result.length);
		Class firstElementClass=result[0].getClass();
		assertTrue (firstElementClass.equals(AccountCheckDTO.class)||firstElementClass.equals(BuyTrack.class));
		Class secondElementClass=result[1].getClass();
		assertTrue (secondElementClass.equals(AccountCheckDTO.class)||secondElementClass.equals(BuyTrack.class));
		assertFalse(firstElementClass.equals(secondElementClass));
		
		BuyTrack buyTrack;
		if (firstElementClass.equals(BuyTrack.class)){
			buyTrack=(BuyTrack) result[0];
		}else{
			buyTrack=(BuyTrack) result[1];
		}
		assertEquals("OK",buyTrack.getStatusValue());
	}

	*//**
	 * Run the Object[] buyTrack(int,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 *//*
	@Test
	@Ignore
	public void testBuyTrack_FailStatus()
		throws Exception {
		int aUserId = 0;
		int aMediaUID = 1;
		String aCommunityName="CN Commercial Beta";

		Object[] result = mediaService.buyTrack(aUserId, aMediaUID,aCommunityName);

		assertNotNull(result);
		assertTrue(2==result.length);
		Class firstElementClass=result[0].getClass();
		assertTrue (firstElementClass.equals(AccountCheckDTO.class)||firstElementClass.equals(BuyTrack.class));
		Class secondElementClass=result[1].getClass();
		assertTrue (secondElementClass.equals(AccountCheckDTO.class)||secondElementClass.equals(BuyTrack.class));
		assertFalse(firstElementClass.equals(secondElementClass));
		
		BuyTrack buyTrack;
		if (firstElementClass.equals(BuyTrack.class)){
			buyTrack=(BuyTrack) result[0];
		}else{
			buyTrack=(BuyTrack) result[1];
		}
		assertEquals("Fail",buyTrack.getStatusValue());
	}
	
	@Test
	public void testFindByIsrc_Success(){
		MediaShallow mediaShallow=mediaService.findByIsrc("USJAY1100032");
		assertNotNull(mediaShallow);
	}
	
	@Test(expected=ServiceException.class)
	public void testFindByIsrc_mediaIsrcIsNull(){
		MediaShallow mediaShallow=mediaService.findByIsrc(null);
		assertNotNull(mediaShallow);
	}

	*//**
	 * Run the Object[] buyTrack(int,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 *//*
	@Test(expected = ServiceException.class)
	public void testBuyTrack_Media_UidLess0()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = -1;
		String aCommunityName="";

		Object[] result = mediaService.buyTrack(aUserId, aMediaUID, aCommunityName);

		// add additional test code here
		assertNotNull(result);
	}

	*//**
	 * Run the Object[] buyTrack(int,int,String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 *//*
	@Test(expected = ServiceException.class)
	public void testBuyTrack_AppVersionIsNull()
		throws Exception {
		int aUserId = 1;
		int aMediaUID = 1;
		String aCommunityName=null;

		Object[] result = mediaService.buyTrack(aUserId, aMediaUID,aCommunityName);

		// add additional test code here
		assertNotNull(result);
	}*/

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 */
	@BeforeClass
	public static void setUp()
		throws Exception {
		ClassPathXmlApplicationContext appServiceContext = new ClassPathXmlApplicationContext(
				new String[] {"/META-INF/dao-test.xml", "/META-INF/service-test.xml","/META-INF/shared.xml" });
		mediaService = (MediaService) appServiceContext.getBean("service.MediaService");
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 01.07.11 9:41
	 */
	@AfterClass
	public static void tearDown()
		throws Exception {
		// Add additional tear down code here
	}
}