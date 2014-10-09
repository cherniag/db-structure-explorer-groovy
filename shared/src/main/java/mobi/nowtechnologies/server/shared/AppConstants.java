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

	public static final String GEO_IP_FILE_NAME = "GeoIP.dat";
	public static final String SEPARATOR = System.getProperty("file.separator");
	
	public static final String FB_URL_USER_CREDENTIALS = "https://graph.facebook.com/me";

	public static final int TMP_PASSWORD_LENGTH = 6;

    public static final String COMMUNITY_URI_PARAM = "community";
    public static final String DEFAULT_COMMUNITY_COOKIE_NAME = "_chartsnow_community";
    public static final String PHONE_NUMBER_REQ_PARAM_NAME = "phoneNumber";
}
