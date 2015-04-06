package mobi.nowtechnologies.server.shared;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Maksym Chernolevskyi (maksym)
 */
public class Utils {

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#WEEK_SECONDS} instead.
     */
    @Deprecated
    public static final int WEEK_SECONDS = 7 * 86400;
    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#DAY_MILLISECONDS} instead.
     */
    @Deprecated
    public static final int DAY_MILLISECONDS = 86400000;
    public static final int PIN_LENGTH = 4;
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
    private static final String SALT = "8z54YKmns9Qz";
    private static final long MILLISECONDS_IN_SECOND = 1000L;
    private static final String charset = "0123456789";
    private static Pattern VERSION_NUMBER_PATTERN = Pattern.compile("(\\d+)\\.((?:\\d+\\.{0,1})+)");

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#truncatedToSeconds(java.util.Date)} instead.
     */
    @Deprecated
    public static int truncatedToSeconds(Date date) {
        return (int) (date.getTime() / 1000);
    }

    public static String truncateToLengthWithEnding(String str, int maxLength, String ending) {
        if (str.length() > maxLength) {
            str = str.substring(0, maxLength) + ending;
        }
        return str;
    }

    public static String getRandomString(int length) {
        Random rand = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String createStoredToken(String username, String password) {
        return md5(SALT + password + SALT + username + SALT);
    }

    public static String createTimestampToken(String token, String timestamp) {
        return md5(SALT + token + SALT + timestamp + SALT);
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#getEpochSeconds()} instead.
     */
    @Deprecated
    public static int getEpochSeconds() {
        return (int) (System.currentTimeMillis() / MILLISECONDS_IN_SECOND);
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#getEpochDays()} instead.
     */
    @Deprecated
    public static int getEpochDays() {
        return toEpochDays(System.currentTimeMillis());
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#toEpochDays(long)} instead.
     */
    @Deprecated
    public static int toEpochDays(long millis) {
        return (int) (millis / DAY_MILLISECONDS);
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#getEpochMillis()} instead.
     */
    @Deprecated
    public static long getEpochMillis() {
        return System.currentTimeMillis();
    }

    public static Integer getBigRandomInt() {
        return 10000000 + new Random().nextInt(9999999);
    }

    public static String generateRandom4DigitsPIN() {
        return RandomStringUtils.randomNumeric(PIN_LENGTH);
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#getDateFromInt(int)} instead.
     */
    @Deprecated
    public static Date getDateFromInt(int intDate) {
        return new Date(((long) intDate) * 1000L);
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#secondsToMillis(long)} instead.
     */
    @Deprecated
    public static long secondsToMillis(long seconds) {
        return SECONDS.toMillis(seconds);
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#millisToSeconds(long)} instead.
     */
    @Deprecated
    public static long millisToSeconds(long millis) {
        return MILLISECONDS.toSeconds(millis);
    }

    /**
     * @deprecated Use {@link mobi.nowtechnologies.common.util.DateTimeUtils#millisToIntSeconds(long)} instead.
     */
    @Deprecated
    public static int millisToIntSeconds(long millis) {
        return (int) millisToSeconds(millis);
    }

    public static int getNewNextSubPayment(int nextSubPayment) {
        LOGGER.debug("input parameters nextSubPayment: [{}]", nextSubPayment);

        int result;

        if (nextSubPayment >= getEpochSeconds()) {
            result = nextSubPayment;
        } else {
            result = getNextSubPaymentAccoringToPaymentPolicy(getEpochSeconds());
        }

        LOGGER.info("next subpayment was [{}], now [{}]", new Object[] {getDateFromInt(nextSubPayment), getDateFromInt(result)});
        LOGGER.debug("Output parameter result=[{}]", result);
        return result;
    }

    private static int getNextSubPaymentAccoringToPaymentPolicy(int nextSubPayment) {
        int result = nextSubPayment + WEEK_SECONDS;
        LOGGER.debug("Output parameter result=[{}]", result);
        return result;
    }

    public static int getTimeOfMovingToLimitedStatus(int nextSubPayment, int subBalance) {
        LOGGER.debug("input parameters nextSubPayment, subBalance: [{}], [{}]", nextSubPayment, subBalance);

        if (nextSubPayment < Utils.getEpochSeconds()) {
            return nextSubPayment;
        }

        int timeOfMovingToLimitedStatusSeconds = nextSubPayment + subBalance * WEEK_SECONDS;

        LOGGER.debug("Output parameter timeOfMovingToLimitedStatusSeconds=[{}]", timeOfMovingToLimitedStatusSeconds);
        return timeOfMovingToLimitedStatusSeconds;
    }

    public static String getIpFromRequest(HttpServletRequest request) {
        LOGGER.debug("input parameters request: [{}]", request);
        String remoteAddr = request.getHeader(AppConstants.X_REAL_IP_HEADER_PARAM);
        if (null == remoteAddr) {
            remoteAddr = request.getRemoteAddr();
        }
        LOGGER.debug("Output parameter remoteAddr=[{}]", remoteAddr);
        return remoteAddr;
    }

    public static int getMonthlyNextSubPayment(int nextSubPayment) {
        LOGGER.debug("input parameters nextSubPayment: [{}]", nextSubPayment);

        int epochSeconds = Utils.getEpochSeconds();

        int startTimeSeconds = nextSubPayment;
        if (startTimeSeconds < epochSeconds) {
            startTimeSeconds = epochSeconds;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(startTimeSeconds * MILLISECONDS_IN_SECOND);
        int dayOfMonthBefore = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.MONTH, 1);

        int dayOfMonthAfter = calendar.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonthBefore != dayOfMonthAfter) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        int monthlyNextSubPayment = (int) (calendar.getTimeInMillis() / MILLISECONDS_IN_SECOND);

        LOGGER.debug("Output parameter monthlyNextSubPayment=[{}]", monthlyNextSubPayment);
        return monthlyNextSubPayment;
    }

    public static int getMajorVersionNumber(String version) {
        int majorVersionNumber;
        Matcher matcher = VERSION_NUMBER_PATTERN.matcher(version);

        if (matcher.matches()) {
            majorVersionNumber = Integer.valueOf(matcher.group(1));
        } else {
            throw new RuntimeException("Couldn't get major version number for [" + version + "] version");
        }

        return majorVersionNumber;
    }

    public static BigDecimal getVersionNumber(String version) {
        BigDecimal versionNumber;
        Matcher matcher = VERSION_NUMBER_PATTERN.matcher(version);

        if (matcher.matches()) {
            versionNumber = new BigDecimal(matcher.group(1) + "." + matcher.group(2).replaceAll("\\.", ""));
        } else {
            throw new IllegalArgumentException("Couldn't get version number for [" + version + "] version");
        }

        return versionNumber;
    }

    public static boolean isMajorVersionNumberLessThan(int majorVersionNumber, String version) {
        int parsedMajorVersionNumber = Utils.getMajorVersionNumber(version);
        return parsedMajorVersionNumber < majorVersionNumber;
    }

    public static int compareVersions(String version1, String version2) {
        return getVersionNumber(version1).compareTo(getVersionNumber(version2));
    }


    public static String preFormatCurrency(BigDecimal amount) {
        String moneyString = formatCurrencyWithoutCurrencySymbol(amount);
        return removeZerosFromRoundedAmount(moneyString);
    }

    private static String formatCurrencyWithoutCurrencySymbol(BigDecimal amount) {
        DecimalFormat fmt = (DecimalFormat) NumberFormat.getCurrencyInstance(ENGLISH);
        DecimalFormatSymbols symbols = fmt.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        fmt.setDecimalFormatSymbols(symbols);
        return fmt.format(amount);
    }

    private static String removeZerosFromRoundedAmount(String moneyString) {
        int centsIndex = moneyString.lastIndexOf(".00");
        if (centsIndex != -1) {
            moneyString = moneyString.substring(0, centsIndex);
        }
        return moneyString;
    }


    public static String decodeUrl(String url) {
        if (!StringUtils.isEmpty(url)) {
            try {
                return new URLCodec().decode(url);
            } catch (DecoderException e) {
                return url;
            }
        }
        return url;
    }

}
