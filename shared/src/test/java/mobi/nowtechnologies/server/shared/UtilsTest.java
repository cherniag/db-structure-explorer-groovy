package mobi.nowtechnologies.server.shared;

import static mobi.nowtechnologies.server.shared.Utils.compareVersions;
import static mobi.nowtechnologies.server.shared.Utils.createStoredToken;
import static mobi.nowtechnologies.server.shared.Utils.createTimestampToken;
import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;
import static mobi.nowtechnologies.server.shared.Utils.getMajorVersionNumber;
import static mobi.nowtechnologies.server.shared.Utils.getMonthlyNextSubPayment;
import static mobi.nowtechnologies.server.shared.Utils.getNewNextSubPayment;
import static mobi.nowtechnologies.server.shared.Utils.getTimeOfMovingToLimitedStatus;
import static mobi.nowtechnologies.server.shared.Utils.isMajorVersionNumberLessThan;
import static mobi.nowtechnologies.server.shared.Utils.md5;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.*;
import org.junit.runner.*;
import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.is;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Titov Mykhaylo (titov)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class UtilsTest {

    private static final int ONE_DAY_SECONDS = 24 * 60 * 60;
    private static final int WEEK_SECONDS = 7 * ONE_DAY_SECONDS;

    @Test
    public void testCreateStoredToken_1() throws Exception {
        String username = "";
        String password = "";

        String result = createStoredToken(username, password);

        assertEquals("4320fb73e5deb16a98f78bec9e522d36", result);
    }

    @Test
    public void testCreateTimestampToken_1() throws Exception {
        String token = "";
        String timestamp = "";

        String result = createTimestampToken(token, timestamp);

        assertEquals("4320fb73e5deb16a98f78bec9e522d36", result);
    }


    @Test
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
        int nextSubPayment = getEpochSeconds() - 5 * ONE_DAY_SECONDS;

        int timeBeforeProcessingSeconds = getEpochSeconds();

        int result = getNewNextSubPayment(nextSubPayment);

        assertTrue(timeBeforeProcessingSeconds + WEEK_SECONDS <= result);
        assertTrue(getEpochSeconds() + WEEK_SECONDS >= result);
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

        assertEquals(nextSubPayment + WEEK_SECONDS, result);
    }

    @Test
    public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentOverOneDayAndSubBalanceIs5_Success() throws Exception {
        int nextSubPayment = getEpochSeconds() + ONE_DAY_SECONDS;
        int subBalance = 5;

        int result = getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

        assertTrue((nextSubPayment + (subBalance) * WEEK_SECONDS) <= result);
        assertTrue((nextSubPayment + (subBalance) * WEEK_SECONDS) >= result);
    }

    @Test
    public void testGetTimeOfMovingToLimitedStatus_WhenNextSubPaymentWasOneDayAgoAndSubBalanceIs5_Success() throws Exception {
        int nextSubPayment = getEpochSeconds() - ONE_DAY_SECONDS;
        int subBalance = 5;

        int result = getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance);

        assertEquals((nextSubPayment), result);
    }

    @Test
    public void testMd5_1() throws Exception {
        String input = "";

        String result = md5(input);

        assertEquals("d41d8cd98f00b204e9800998ecf8427e", result);
    }

    @Test
    public void testMd5_2() throws Exception {
        String input = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        String result = md5(input);

        assertEquals("5eca9bd3eb07c006cd43ae48dfde7fd3", result);
    }

    @Test
    public void testMd5_3() throws Exception {
        String input = "";

        String result = md5(input);

        assertEquals("d41d8cd98f00b204e9800998ecf8427e", result);
    }

    @Test
    public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonth_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }

    @Test
    public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonthOfLeapYear_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }

    @Test
    public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonthOfNotLeapYear_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }

    @Test
    public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonth_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }

    @Test
    public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonth_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

        calendar.clear();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int currentTimeSeconds = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }

    @Test
    public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonthOfLeapYear_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

        calendar.clear();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int currentTimeSeconds = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }

    @Test
    public void testGetMontlyNextSubPayment_DateIsAbsentInTheNextMonthOfNotLeapYear_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 28);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

        calendar.clear();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 29);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int currentTimeSeconds = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }


    @Test
    public void testGetMontlyNextSubPayment_DateIsPresentInTheNextMonth_NextSubPaymentIsLessThanCurrentTime_Success() throws Exception {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int nextSubPayment = (int) (calendar.getTimeInMillis() / 1000);

        calendar.clear();
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        int currentTimeSeconds = (int) (calendar.getTimeInMillis() / 1000);

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

        int expectedMontlyNextSubPayment = (int) (calendar.getTimeInMillis() / 1000);
        assertEquals(expectedMontlyNextSubPayment, actualMontlyNextSubPayment);
    }

    @Test
    public void shouldReturnMajorVersionNumber() {
        int majorVersionNumber = getMajorVersionNumber("5.0");
        assertEquals(5, majorVersionNumber);
    }

    @Test(expected = Exception.class)
    public void shouldNotReturnMajorVersionNumber() {
        getMajorVersionNumber(".0");
    }

    @Test
    public void shouldReturnMajorVersionNumberIsLess() {
        assertTrue(isMajorVersionNumberLessThan(4, "3.9"));
    }

    @Test
    public void shouldReturnMajorVersionPriority() {
        assertEquals(1, compareVersions("3.9", "3.8.1"));
    }

    @Test
    public void shouldReturnMinorVersionPriority() {
        assertEquals(-1, compareVersions("3.8", "3.8.1"));
    }

    @Test
    public void shouldReturnEqualVersionPriority() {
        assertEquals(0, compareVersions("4.0", "4.0.0"));
    }

    @Test
    public void shouldReturnMajorVersionNumberIsMore() {
        assertFalse(isMajorVersionNumberLessThan(2, "3.9"));
    }

    @Test
    public void shouldReturnMajorVersionNumberIsLess2() {
        assertFalse(isMajorVersionNumberLessThan(4, "4.0"));
    }

    @Test
    public void shouldPrueFormatCurrencyWithCents() {
        //given
        BigDecimal amount = new BigDecimal("1.5");

        //when
        String amountString = Utils.preFormatCurrency(amount);

        //then
        assertThat(amountString, is("1.50"));
    }

    @Test
    public void shouldPrueFormatCurrencyWithOutCents() {
        //given
        BigDecimal amount = new BigDecimal("6");

        //when
        String amountString = Utils.preFormatCurrency(amount);

        //then
        assertThat(amountString, is("6"));
    }

    @Test
    public void testGenerateRandom4DigitsPIN() {
        Set<String> codes = new HashSet<String>(7);
        for (int i = 0; i < 7; i++) {
            codes.add(Utils.generateRandom4DigitsPIN());
        }
        // if strings are all the same then hashset will contain only 1 string
        assertTrue("There are the same codes", codes.size() > 1);
    }
}
