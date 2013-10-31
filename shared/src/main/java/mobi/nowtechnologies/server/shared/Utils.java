package mobi.nowtechnologies.server.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mobi.nowtechnologies.common.util.UserCredentialsUtils.SALT;

/**
 * Utils
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public class Utils {
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	private static final long MILLISECONDS_IN_SECOND = 1000L;
	private static final String charset = "0123456789";
	public static final int WEEK_SECONDS = 7 * 86400;
    public static final int DAY_MILLISECONDS = 86400000;

    private static Pattern MAJOR_VERSION_NUMBER_PATTERN = Pattern.compile("(\\d+)\\..*");

    public static int truncatedToSeconds(Date date){
        return (int)(date.getTime()/1000);
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

	public static int getEpochSeconds() {
		return (int) (System.currentTimeMillis() / MILLISECONDS_IN_SECOND);
	}
	
	public static int getEpochDays() {
		return toEpochDays(System.currentTimeMillis());
	}
	
	public static int toEpochDays(long millis) {
		return (int) (millis / DAY_MILLISECONDS);
	}

	public static long getEpochMillis() {
		return System.currentTimeMillis();
	}

	public static Integer getBigRandomInt() {
		return 10000000 + new Random().nextInt(9999999);
	}

	public static Integer generateRandomPIN() {
		return 7000 + new Random().nextInt(999);
	}

	public static Date getDateFromInt(int intDate) {
		return new Date(((long) intDate) * 1000L);
	}

	public static String getOTACode(int userId, String userName) {
		if (userName == null)
			throw new NullPointerException("The parameter userName is null");
		return md5(userId + userName + SALT);
	}

	public static int getNewNextSubPayment(int nextSubPayment) {
		LOGGER.debug("input parameters nextSubPayment: [{}]", nextSubPayment);

		int result;

		if (nextSubPayment >= getEpochSeconds())
			result = nextSubPayment;
		else
			result = getNextSubPaymentAccoringToPaymentPolicy(getEpochSeconds());

		LOGGER.info(
				"next subpayment was [{}], now [{}]",
				new Object[] {
						getDateFromInt(nextSubPayment),
						getDateFromInt(result)
				});
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

		if (nextSubPayment < Utils.getEpochSeconds())
			return nextSubPayment;

		int timeOfMovingToLimitedStatusSeconds = nextSubPayment + subBalance * WEEK_SECONDS;

		LOGGER.debug("Output parameter timeOfMovingToLimitedStatusSeconds=[{}]", timeOfMovingToLimitedStatusSeconds);
		return timeOfMovingToLimitedStatusSeconds;
	}

	public static String getIpFromRequest(HttpServletRequest request) {
		LOGGER.debug("input parameters request: [{}]", request);
		String remoteAddr = request.getHeader(AppConstants.X_REAL_IP_HEADER_PARAM);
		if (null == remoteAddr)
			remoteAddr = request.getRemoteAddr();
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
	public static boolean datesNotEquals(Date oldTime, Date newTime) {
        return newTime.getTime() != oldTime.getTime();
    }

    public static String substring(String s, int i) {
        if(s == null || s.length() < i)
            return s;
        return s.substring(0,i);
    }

    public static String concatLowerCase(String... strs) {
        StringBuilder buf = new StringBuilder();
        for(String s: strs)
            buf.append(s);
        return buf.toString().toLowerCase();
    }

    public static int getMajorVersionNumber(String version){
        int majorVersionNumber;
        Matcher matcher = MAJOR_VERSION_NUMBER_PATTERN.matcher(version);

        if(matcher.matches()){
            majorVersionNumber = Integer.valueOf(matcher.group(1));
        }else{
            throw new RuntimeException("Couldn't get major version number for [" + version +"] version");
        }

        return majorVersionNumber;
    }

    public static boolean isMajorVersionNumberLessThan(int majorVersionNumber, String version){
        int parsedMajorVersionNumber = Utils.getMajorVersionNumber(version);
        return parsedMajorVersionNumber < majorVersionNumber;
    }
}
