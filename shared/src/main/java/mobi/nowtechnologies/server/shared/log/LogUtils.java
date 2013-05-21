package mobi.nowtechnologies.server.shared.log;

import mobi.nowtechnologies.server.shared.Utils;

import org.apache.log4j.MDC;

public class LogUtils {

	public static final String T_PR_RESPONSE = "tPRResponse";
	public static final String T_PR_BODY = "tPRBody";
	public static final String T_PR_NAME_VALUE_PAIRS = "tPRNameValuePairs";
	public static final String T_PR_URL = "tPRUrl";
	public static final String T_PR_RESULT = "tPRResult";
	public static final String T_PR_ERROR_MESSAGE = "tPRErrorMessage";
	public static final String T_PR_EXECUTION_DURATION_MILLIS = "tPRExecutionDurationMillis";
	public static final String LOG_USER_NAME = "userName";
	public static final String LOG_COMMAND = "command";
	public static final String LOG_CLASS = "class";
	public static final String LOG_MESSAGEID = "MESSAGEID";
	public static final String LOG_COMMUNITY = "community";
	public static final String LOG_USER_ID = "userId";
	public static final String LOG_USER_MOBILE = "userMobile";
	public static final String LOG_REMOTE_ADDR = "remoteAddr";
	public static final String LOG_START_TIME_MILLIS = "startTimeMillis";

	public static final void putGlobalMDC(Object userId, String userMobile, String userName, String community, String commandName, Class<?> c, String remoteAddr) {
		if (userId != null) {
			MDC.put(LOG_USER_ID, userId);
		}
		if (userMobile != null) {
			MDC.put(LOG_USER_MOBILE, userMobile);
		}
		if (userName != null)
			MDC.put(LOG_USER_NAME, userName);
		if (community != null)
			MDC.put(LOG_COMMUNITY, community);
		if (c != null)
			MDC.put(LOG_CLASS, c);
		if (commandName != null)
			MDC.put(LOG_COMMAND, commandName);
		MDC.put(LOG_REMOTE_ADDR, remoteAddr);

		final long startTimeMillis = Utils.getEpochMillis();
		MDC.put(LOG_START_TIME_MILLIS, startTimeMillis);
	}

	public static final void removeGlobalMDC() {
		if (MDC.get(LOG_USER_ID) != null) {
			MDC.remove(LOG_USER_ID);
		}
		if (MDC.get(LOG_USER_MOBILE) != null) {
			MDC.remove(LOG_USER_MOBILE);
		}
		if (MDC.get(LOG_USER_NAME) != null)
			MDC.remove(LOG_USER_NAME);
		if (MDC.get(LOG_COMMUNITY) != null)
			MDC.remove(LOG_COMMUNITY);
		if (MDC.get(LOG_CLASS) != null)
			MDC.remove(LOG_CLASS);
		if (MDC.get(LOG_COMMAND) != null)
			MDC.remove(LOG_COMMAND);
		MDC.remove(LOG_REMOTE_ADDR);
	}

	public static final void putClassNameMDC(Class<?> c) {
		MDC.put(LOG_CLASS, c);
	}

	public static final void removeClassNameMDC() {
		if (MDC.get(LOG_CLASS) != null)
			MDC.remove(LOG_CLASS);
	}

	public static final void putPaymentMDC(String userId, String userName, String communityName, Class<?> c) {
		MDC.put(LOG_USER_ID, userId);
		MDC.put(LOG_USER_NAME, userName);
		MDC.put(LOG_COMMUNITY, communityName);
		putClassNameMDC(c);
	}

	public static final void removePaymentMDC() {
		if (MDC.get(LOG_USER_ID) != null)
			MDC.remove(LOG_USER_ID);
		if (MDC.get(LOG_USER_NAME) != null)
			MDC.remove(LOG_USER_NAME);
		if (MDC.get(LOG_COMMUNITY) != null)
			MDC.remove(LOG_COMMUNITY);
		removeClassNameMDC();
	}

	public static void putSpecificMDC(String userName, String userMobile, Object userId) {
		if (userName != null) {
			MDC.put(LOG_USER_NAME, userName);
		}
		if (userMobile != null) {
			MDC.put(LOG_USER_MOBILE, userMobile);
		}
		if (userId != null) {
			MDC.put(LOG_USER_ID, userId);
		}
	}

	public static void putSpecificMDC(String userName, String community) {
		if (userName != null)
			MDC.put(LOG_USER_NAME, userName);
		if (community != null)
			MDC.put(LOG_COMMUNITY, community);
	}

	public static void removeSpecificMDC() {
		if (MDC.get(LOG_USER_NAME) != null)
			MDC.remove(LOG_USER_NAME);
		if (MDC.get(LOG_COMMUNITY) != null)
			MDC.remove(LOG_COMMUNITY);
		if (MDC.get(LOG_USER_MOBILE) != null)
			MDC.remove(LOG_USER_MOBILE);
		if (MDC.get(LOG_USER_ID) != null)
			MDC.remove(LOG_USER_ID);
	}

	public static Long getStartTimeMillis() {
		return (Long) MDC.get(LOG_START_TIME_MILLIS);
	}

	public static void removeStartTimeMillisMDC() {
		if (MDC.get(LOG_START_TIME_MILLIS) != null)
			MDC.remove(LOG_START_TIME_MILLIS);
	}

	public static Object getUserId() {
		return MDC.get(LOG_USER_ID);
	}

	public static void set3rdParyRequestProfileMDC(Object executionDurationMillis, Object errorMessage, Object result, Object url, Object nameValuePairs, Object body, Object responseMessage) {
		if (executionDurationMillis != null) {
			MDC.put(T_PR_EXECUTION_DURATION_MILLIS, executionDurationMillis);
		}
		if (errorMessage != null) {
			MDC.put(T_PR_ERROR_MESSAGE, errorMessage);
		}
		if (result != null) {
			MDC.put(T_PR_RESULT, result);
		}
		if (url != null) {
			MDC.put(T_PR_URL, url);
		}
		if (nameValuePairs != null) {
			MDC.put(T_PR_NAME_VALUE_PAIRS, nameValuePairs);
		}
		if (body != null) {
			MDC.put(T_PR_BODY, body);
		}
		if (responseMessage != null) {
			MDC.put(T_PR_RESPONSE, responseMessage);
		}

	}

	public static void remove3rdParyRequestProfileMDC() {
		if (MDC.get(T_PR_EXECUTION_DURATION_MILLIS) != null) {
			MDC.remove(T_PR_EXECUTION_DURATION_MILLIS);
		}
		if (MDC.get(T_PR_ERROR_MESSAGE) != null) {
			MDC.remove(T_PR_ERROR_MESSAGE);
		}
		if (MDC.get(T_PR_RESULT) != null) {
			MDC.remove(T_PR_RESULT);
		}
		if (MDC.get(T_PR_URL) != null) {
			MDC.remove(T_PR_URL);
		}
		if (MDC.get(T_PR_NAME_VALUE_PAIRS) != null) {
			MDC.remove(T_PR_NAME_VALUE_PAIRS);
		}
		if (MDC.get(T_PR_BODY) != null) {
			MDC.remove(T_PR_BODY);
		}
		if (MDC.get(T_PR_RESPONSE) != null) {
			MDC.remove(T_PR_RESPONSE);
		}

	}
}