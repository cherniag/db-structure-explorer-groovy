package mobi.nowtechnologies.server.shared;

import static mobi.nowtechnologies.common.util.UserCredentialsUtils.SALT;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils
 * 
 * @author Maksym Chernolevskyi (maksym)
 *
 */
public class Utils {
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class
			.getName());
	private static final long MILLISECONDS_IN_SECOND = 1000L;
	private static final String charset = "0123456789";
	public static final int WEEK_SECONDS = 7 * 86400;
	 
    public static String getRandomString(int length) {
        Random rand = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int pos = rand.nextInt(charset.length());
            sb.append(charset.charAt(pos));
        }
        return sb.toString();
    }

	/**
	 * This functions returns a MD5 hash based on the input String
	 * 
	 * @param input
	 *            String to be MD5
	 * @return the MD5 hash
	 */
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
		return md5(userId+userName+SALT);
	}
	
	public static int getNewNextSubPayment(int nextSubPayment) {
		LOGGER.debug("input parameters nextSubPayment: [{}]", nextSubPayment);
		
		int result;
		
		if (nextSubPayment>=getEpochSeconds()) result=nextSubPayment;
		else result = getNextSubPaymentAccoringToPaymentPolicy(getEpochSeconds());
		
		LOGGER.info(
				"next subpayment was [{}], now [{}]",
				new Object[]{
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
	
	public static int getTimeOfMovingToLimitedStatus(int nextSubPayment, int subBalance){
		LOGGER.debug("input parameters nextSubPayment, subBalance: [{}], [{}]", nextSubPayment, subBalance);
		
		if (nextSubPayment<Utils.getEpochSeconds()) return nextSubPayment;
		
		int timeOfMovingToLimitedStatusSeconds = nextSubPayment + subBalance*WEEK_SECONDS;
		/* PRO-710: add all balance weeks to the chart subscription end date
		for (int i = 0; i < subBalance-1; i++) {
			timeOfMovingToLimitedStatusSeconds = getNextSubPaymentAccoringToPaymentPolicy(timeOfMovingToLimitedStatusSeconds);
		}
		*/
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
	
	
/*	public static void main(String[] args) {
		final String USER_NAME = "paku@ciklum.net";
		final String PASSWORD = "123456";
		final String TIMESTAMP="1";
		
		System.out.println(createStoredToken(USER_NAME, PASSWORD));
		System.out.println(createTimestampToken(
				createStoredToken(USER_NAME, PASSWORD), 
				TIMESTAMP));
		
		//System.out.println(getOTACode(132,"richard.monday@chartsnow.mobi"));
	}*/

}