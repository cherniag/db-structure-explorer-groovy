package mobi.nowtechnologies.server.shared;

/**
 * AppConstants
 * 
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
public final class AppConstants {
	public static final String X_REAL_IP_HEADER_PARAM = "X-Real-IP";
	
	public static final String UTF_8 = "UTF-8";
	
	public static final String NOT_AVAILABLE = "NOTAVAILABLE";
	
	public static final String STATUS_OK = "OK";
	public static final String STATUS_REGISTERED = "REGISTERED";	
	public static final String STATUS_PENDING = "PENDING";
	public static final String STATUS_FAIL="FAIL";
	public static final String STATUS_USER_CONFIRMED="USER_CONFIRMED";

	public static final String GEO_IP_FILE_NAME = "GeoIP.dat";
	public static final String SEPARATOR = System.getProperty("file.separator");
	
	public static final String CURRENCY_GBP = "GBP";
	
	public static final String OADC_FREE = "chartsnow";
	
	public static final String SMS_FREE_MSG = "sms.freeMsg";
	public static final String SMS_WEEK_REMINDER_MSG = "sms.weekReminder";
	public static final String PSMS_MSG = "sms.psms";
	
	
	public static final String FB_URL_USER_CREDENTIONS = "https://graph.facebook.com/me";
	public static final String FB_URL_ACCESS_TOKEN = "https://graph.facebook.com/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s";
	
	public static final int TMP_PASSWORD_LENGTH = 6;
	
	private AppConstants() {}
}
