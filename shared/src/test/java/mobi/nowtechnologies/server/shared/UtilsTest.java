package mobi.nowtechnologies.server.shared;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static mobi.nowtechnologies.server.shared.Utils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class UtilsTest {

	private static final int ONE_DAY_SECONDS = 24 * 60 * 60;
	private static final int WEEK_SECONDS = 7 * ONE_DAY_SECONDS;

	@Test
	@Ignore
	public void testUtils_1() throws Exception {
		Utils result = new Utils();
		assertNotNull(result);
	}

	@Test
	@Ignore
	public void testCreateStoredToken_1() throws Exception {
		String username = "";
		String password = "";

		String result = createStoredToken(username, password);

		assertEquals("4320fb73e5deb16a98f78bec9e522d36", result);
	}

	@Test
	@Ignore
	public void testCreateTimestampToken_1() throws Exception {
		String token = "";
		String timestamp = "";

		String result = createTimestampToken(token, timestamp);

		assertEquals("4320fb73e5deb16a98f78bec9e522d36", result);
	}

	@Test
	@Ignore
	public void testGenerateRandomPIN_1() throws Exception {

		Integer result = generateRandomPIN();

		assertNotNull(result);
		assertEquals("7113", result.toString());
		assertEquals((byte) -55, result.byteValue());
		assertEquals(7113.0, result.doubleValue(), 1.0);
		assertEquals(7113.0f, result.floatValue(), 1.0f);
		assertEquals(7113, result.intValue());
		assertEquals(7113L, result.longValue());
		assertEquals((short) 7113, result.shortValue());
	}

	@Test
	@Ignore
	public void testGetBigRandomInt_1() throws Exception {

		Integer result = getBigRandomInt();

		assertNotNull(result);
		assertEquals("17816073", result.toString());
		assertEquals((byte) 9, result.byteValue());
		assertEquals(1.7816073E7, result.doubleValue(), 1.0);
		assertEquals(1.7816072E7f, result.floatValue(), 1.0f);
		assertEquals(17816073, result.intValue());
		assertEquals(17816073L, result.longValue());
		assertEquals((short) -9719, result.shortValue());
	}

	@Test
	@Ignore
	public void testGetDateFromInt_1() throws Exception {
		int intDate = 1;

		Date result = getDateFromInt(intDate);

		assertNotNull(result);
		assertEquals(DateFormat.getInstance().format(new Date(1000L)), DateFormat.getInstance().format(result));
		assertEquals(1000L, result.getTime());
	}

	@Test
	@Ignore
	public void testGetEpochSeconds_1() throws Exception {

		int result = getEpochSeconds();

		assertEquals(1330681906, result);
	}

	@Test
	@Ignore
	public void testGetNewNextSubPayment_WhenNextSubPaymentIs0_Success() throws Exception {

		int nextSubPayment = 0;

		int timeBeforeProcessingSeconds = getEpochSeconds();
		int result = getNewNextSubPayment(nextSubPayment);

		assertTrue(timeBeforeProcessingSeconds + WEEK_SECONDS <= result);
		assertTrue(getEpochSeconds() + WEEK_SECONDS >= result);
	}

	@Test
	public void testGetNewNextSubPayment_WhenNextSubPaymentOver1Day_Success() throws Exception {
		int nextSubPayment = getEpochSeconds() + ONE_DAY_SECONDS;

		int timeBeforeProcessingSeconds = getEpochSeconds();
		int result = getNewNextSubPayment(nextSubPayment);

		assertTrue(timeBeforeProcessingSeconds + ONE_DAY_SECONDS <= result);
		assertTrue(getEpochSeconds() + ONE_DAY_SECONDS >= result);
	}

	@Test
	public void testGetNewNextSubPayment_WhenNextSubPaymentWas5DayAgo_Success() throws Exception {
		int nextSubPayment = getEpochSeconds()-5 * ONE_DAY_SECONDS;

		int timeBeforeProcessingSeconds = getEpochSeconds();
		
		int result = getNewNextSubPayment(nextSubPayment);

		assertTrue(timeBeforeProcessingSeconds + WEEK_SECONDS <= result);
		assertTrue(getEpochSeconds() + WEEK_SECONDS >= result);
	}

	@Test
	@Ignore
	public void testGetOTACode_1() throws Exception {
		int userId = 1;
		String userName = "";

		String result = getOTACode(userId, userName);

		assertEquals("dd5dacb0ac2a83b510ef8f6197e8f6ad", result);
	}

	@Test(expected = java.lang.NullPointerException.class)
	@Ignore
	public void testGetOTACode_2() throws Exception {
		int userId = 1;
		String userName = null;

		String result = getOTACode(userId, userName);

		assertNotNull(result);
	}

	@Test
	@Ignore
	public void testGetRandomString_1() throws Exception {
		int length = 1;

		String result = getRandomString(length);

		assertEquals("3", result);
	}

	@Test
	@Ignore
	public void testGetRandomString_2() throws Exception {
		int length = 0;

		String result = getRandomString(length);

		assertEquals("", result);
	}

	@Test
	public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentWasOneDayAgo() throws Exception {
		int nextSubPayment = getEpochSeconds() - ONE_DAY_SECONDS;
		int subBalance = 0;

		int result = getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

		assertEquals(nextSubPayment, result);
	}

	@Test
	public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentOverOneDay_Success() throws Exception {
		int nextSubPayment = getEpochSeconds() + ONE_DAY_SECONDS;
		int subBalance = 1;

		int result = getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

		assertEquals(nextSubPayment+WEEK_SECONDS, result);
	}

	@Test
	public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentOverOneDayAndSubBalanceIs5_Success() throws Exception {
		int nextSubPayment = getEpochSeconds() + ONE_DAY_SECONDS;
		int subBalance = 5;

		int result = getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

		assertTrue((nextSubPayment +(subBalance) * WEEK_SECONDS) <= result);
		assertTrue((nextSubPayment +(subBalance) * WEEK_SECONDS) >= result);
	}
	
	@Test
	public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentWasOneDayAgoAndSubBalanceIs5_Success() throws Exception {
		int nextSubPayment = getEpochSeconds() - ONE_DAY_SECONDS;
		int subBalance = 5;

		int result = getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

		assertEquals((nextSubPayment), result);
	}

	@Test
	@Ignore
	public void testMd5_1() throws Exception {
		String input = "";

		String result = md5(input);

		assertEquals("d41d8cd98f00b204e9800998ecf8427e", result);
	}

	@Test
	@Ignore
	public void testMd5_2() throws Exception {
		String input = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

		String result = md5(input);

		assertEquals("5eca9bd3eb07c006cd43ae48dfde7fd3", result);
	}

	@Test
	@Ignore
	public void testMd5_3() throws Exception {
		String input = "";

		String result = md5(input);

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

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(nextSubPayment);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
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

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(nextSubPayment);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
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

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(nextSubPayment);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
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

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(nextSubPayment);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
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
	
	@Test
	public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonth_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception{	
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int currentTimeSeconds = (int) (calendar.getTimeInMillis()/ 1000);

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
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
	public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonthOfLeapYear_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 28);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 29);
		calendar.set(Calendar.YEAR, 2012);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int currentTimeSeconds = (int) (calendar.getTimeInMillis()/ 1000);

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
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
	public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonthOfNotLeapYear_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 28);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 29);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int currentTimeSeconds = (int) (calendar.getTimeInMillis()/ 1000);

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
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
	public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonth_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception{
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59); 
		
		int nextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);

		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 2);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int currentTimeSeconds = (int) (calendar.getTimeInMillis()/ 1000);

		PowerMockito.spy(Utils.class);
		
		PowerMockito.when(getEpochSeconds()).thenReturn(currentTimeSeconds);
		
		int actualMontlyNextSubPayment = getMonthlyNextSubPayment(nextSubPayment);
		
		calendar.clear();
		calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
		calendar.set(Calendar.DAY_OF_MONTH, 2);
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.AM_PM, Calendar.PM);
		calendar.set(Calendar.HOUR, 11);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		
		int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis()/ 1000);
		assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
	}

    @Test
    public void shouldReturnMajorVersionNumber(){
        int majorVersionNumber = getMajorVersionNumber("5.0");
        assertEquals(5, majorVersionNumber);
    }

    @Test(expected = Exception.class)
    public void shouldNotReturnMajorVersionNumber(){
        getMajorVersionNumber(".0");
    }

    @Test
    public void shouldReturnMajorVersionNumberIsLess(){
        assertTrue(isMajorVersionNumberLessThan(4, "3.9"));
    }

    @Test
    public void shouldReturnMajorVersionNumberIsMore(){
        assertFalse(isMajorVersionNumberLessThan(2, "3.9"));
    }

    @Test
    public void shouldReturnMajorVersionNumberIsLess2(){
        assertFalse(isMajorVersionNumberLessThan(4, "4.0"));
    }

    @Test
    public void shouldPrueFormatCurrencyWithCents(){
       //given
        BigDecimal amount = new BigDecimal("1.5");

        //when
        String amountString = Utils.preFormatCurrency(amount);

        //then
        assertThat(amountString, is("1.50"));
    }

    @Test
    public void shouldPrueFormatCurrencyWithOutCents(){
        //given
        BigDecimal amount = new BigDecimal("6");

        //when
        String amountString = Utils.preFormatCurrency(amount);

        //then
        assertThat(amountString, is("6"));
    }
}
