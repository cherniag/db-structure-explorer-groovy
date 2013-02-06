package mobi.nowtechnologies.server.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

/**
 * The class <code>UtilsTest</code> contains tests for the class
 * <code>{@link Utils}</code>.
 * 
 * @generatedBy CodePro at 02.03.12 11:51
 * @author Titov Mykhaylo (titov)
 * @version $Revision: 1.0 $
 */
public class UtilsTest {

	private static final long MILISECONDS_IN_SECONDS = 1000L;
	private static final int ONE_DAY_SECONDS = 24 * 60 * 60;
	private static final int WEEK_SECONDS = 7 * ONE_DAY_SECONDS;

	/**
	 * Run the Utils() constructor test.
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testUtils_1() throws Exception {
		Utils result = new Utils();
		assertNotNull(result);
		// add additional test code here
	}

	/**
	 * Run the String createStoredToken(String,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testCreateStoredToken_1() throws Exception {
		String username = "";
		String password = "";

		String result = Utils.createStoredToken(username, password);

		// add additional test code here
		assertEquals("4320fb73e5deb16a98f78bec9e522d36", result);
	}

	/**
	 * Run the String createTimestampToken(String,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testCreateTimestampToken_1() throws Exception {
		String token = "";
		String timestamp = "";

		String result = Utils.createTimestampToken(token, timestamp);

		// add additional test code here
		assertEquals("4320fb73e5deb16a98f78bec9e522d36", result);
	}

	/**
	 * Run the Integer generateRandomPIN() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGenerateRandomPIN_1() throws Exception {

		Integer result = Utils.generateRandomPIN();

		// add additional test code here
		assertNotNull(result);
		assertEquals("7113", result.toString());
		assertEquals((byte) -55, result.byteValue());
		assertEquals(7113.0, result.doubleValue(), 1.0);
		assertEquals(7113.0f, result.floatValue(), 1.0f);
		assertEquals(7113, result.intValue());
		assertEquals(7113L, result.longValue());
		assertEquals((short) 7113, result.shortValue());
	}

	/**
	 * Run the Integer getBigRandomInt() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGetBigRandomInt_1() throws Exception {

		Integer result = Utils.getBigRandomInt();

		// add additional test code here
		assertNotNull(result);
		assertEquals("17816073", result.toString());
		assertEquals((byte) 9, result.byteValue());
		assertEquals(1.7816073E7, result.doubleValue(), 1.0);
		assertEquals(1.7816072E7f, result.floatValue(), 1.0f);
		assertEquals(17816073, result.intValue());
		assertEquals(17816073L, result.longValue());
		assertEquals((short) -9719, result.shortValue());
	}

	/**
	 * Run the Date getDateFromInt(int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGetDateFromInt_1() throws Exception {
		int intDate = 1;

		Date result = Utils.getDateFromInt(intDate);

		// add additional test code here
		assertNotNull(result);
		assertEquals(DateFormat.getInstance().format(new Date(1000L)), DateFormat.getInstance().format(result));
		assertEquals(1000L, result.getTime());
	}

	/**
	 * Run the int getEpochSeconds() method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGetEpochSeconds_1() throws Exception {

		int result = Utils.getEpochSeconds();

		// add additional test code here
		assertEquals(1330681906, result);
	}

	/**
	 * Run the int getNewNextSubPayment(int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGetNewNextSubPayment_WhenNextSubPaymentIs0_Success() throws Exception {

		int nextSubPayment = 0;

		int timeBeforeProcessingSeconds = Utils.getEpochSeconds();
		int result = Utils.getNewNextSubPayment(nextSubPayment);

		assertTrue(timeBeforeProcessingSeconds + WEEK_SECONDS <= result);
		assertTrue(Utils.getEpochSeconds() + WEEK_SECONDS >= result);
	}

	/**
	 * Run the int getNewNextSubPayment(int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	public void testGetNewNextSubPayment_WhenNextSubPaymentOver1Day_Success() throws Exception {
		int nextSubPayment = Utils.getEpochSeconds() + ONE_DAY_SECONDS;

		int timeBeforeProcessingSeconds = Utils.getEpochSeconds();
		int result = Utils.getNewNextSubPayment(nextSubPayment);

		assertTrue(timeBeforeProcessingSeconds + ONE_DAY_SECONDS <= result);
		assertTrue(Utils.getEpochSeconds() + ONE_DAY_SECONDS >= result);
	}

	/**
	 * Run the int getNewNextSubPayment(int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	public void testGetNewNextSubPayment_WhenNextSubPaymentWas5DayAgo_Success() throws Exception {
		int nextSubPayment = Utils.getEpochSeconds()-5 * ONE_DAY_SECONDS;

		int timeBeforeProcessingSeconds = Utils.getEpochSeconds();
		
		int result = Utils.getNewNextSubPayment(nextSubPayment);

		assertTrue(timeBeforeProcessingSeconds + WEEK_SECONDS <= result);
		assertTrue(Utils.getEpochSeconds() + WEEK_SECONDS >= result);
	}

	/**
	 * Run the String getOTACode(int,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGetOTACode_1() throws Exception {
		int userId = 1;
		String userName = "";

		String result = Utils.getOTACode(userId, userName);

		// add additional test code here
		assertEquals("dd5dacb0ac2a83b510ef8f6197e8f6ad", result);
	}

	/**
	 * Run the String getOTACode(int,String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test(expected = java.lang.NullPointerException.class)
	@Ignore
	public void testGetOTACode_2() throws Exception {
		int userId = 1;
		String userName = null;

		String result = Utils.getOTACode(userId, userName);

		// add additional test code here
		assertNotNull(result);
	}

	/**
	 * Run the String getRandomString(int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGetRandomString_1() throws Exception {
		int length = 1;

		String result = Utils.getRandomString(length);

		// add additional test code here
		assertEquals("3", result);
	}

	/**
	 * Run the String getRandomString(int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testGetRandomString_2() throws Exception {
		int length = 0;

		String result = Utils.getRandomString(length);

		// add additional test code here
		assertEquals("", result);
	}

	/**
	 * Run the int getTimeOfMovingToLimitedStatus(int,int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentWasOneDayAgo() throws Exception {
		int nextSubPayment = Utils.getEpochSeconds() - ONE_DAY_SECONDS;
		int subBalance = 0;

		int result = Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

		// add additional test code here
		assertEquals(nextSubPayment, result);
	}

	/**
	 * Run the int getTimeOfMovingToLimitedStatus(int,int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentOverOneDay_Success() throws Exception {
		int nextSubPayment = Utils.getEpochSeconds() + ONE_DAY_SECONDS;
		int subBalance = 1;

		int result = Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

		// add additional test code here
		assertEquals(nextSubPayment+WEEK_SECONDS, result);
	}

	/**
	 * Run the int getTimeOfMovingToLimitedStatus(int,int) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentOverOneDayAndSubBalanceIs5_Success() throws Exception {
		int nextSubPayment = Utils.getEpochSeconds() + ONE_DAY_SECONDS;
		int subBalance = 5;

		int result = Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

		// add additional test code here
		assertTrue((nextSubPayment +(subBalance) * WEEK_SECONDS) <= result);
		assertTrue((nextSubPayment +(subBalance) * WEEK_SECONDS) >= result);
	}

	/**
	 * Run the String md5(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testMd5_1() throws Exception {
		String input = "";

		String result = Utils.md5(input);

		// add additional test code here
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", result);
	}

	/**
	 * Run the String md5(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testMd5_2() throws Exception {
		String input = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

		String result = Utils.md5(input);

		// add additional test code here
		assertEquals("5eca9bd3eb07c006cd43ae48dfde7fd3", result);
	}

	/**
	 * Run the String md5(String) method test.
	 * 
	 * @throws Exception
	 * 
	 * @generatedBy CodePro at 02.03.12 11:51
	 */
	@Test
	@Ignore
	public void testMd5_3() throws Exception {
		String input = "";

		String result = Utils.md5(input);

		// add additional test code here
		assertEquals("d41d8cd98f00b204e9800998ecf8427e", result);
	}
	
	@Test
	public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonth_Success() throws Exception{	
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		int actualMontlyNextSubPayment = Utils.getMontlyNextSubPayment(nextSubPayment);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
	}
	
	@Test
	public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonthOfLeapYear_Success() throws Exception{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 29);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		int actualMontlyNextSubPayment = Utils.getMontlyNextSubPayment(nextSubPayment);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 29);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
	}
	
	@Test
	public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonthOfNotLeapYear_Success() throws Exception{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 29);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		int actualMontlyNextSubPayment = Utils.getMontlyNextSubPayment(nextSubPayment);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
	}
	
	@Test
	public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonth_Success() throws Exception{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59); 
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		int actualMontlyNextSubPayment = Utils.getMontlyNextSubPayment(nextSubPayment);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
	}
}
