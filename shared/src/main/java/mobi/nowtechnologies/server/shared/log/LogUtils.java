package mobi.nowtechnologies.server.shared.log;

import mobi.nowtechnologies.server.shared.Utils;

import org.apache.log4j.MDC;

public class LogUtils {
	
	public static final String LOG_USER_NAME = "userName";
	public static final String LOG_COMMAND = "command";
	public static final String LOG_CLASS = "class";
	public static final String LOG_MESSAGEID = "MESSAGEID";
	public static final String LOG_COMMUNITY = "community";
	public static final String LOG_USER_ID = "userId";
	public static final String LOG_REMOTE_ADDR = "remoteAddr";
	public static final String LOG_START_TIME_NANO = "startTimeNano";
	
	public static final void putGlobalMDC(String userName, String community, String commandName, Class<?> c, String remoteAddr) {
		if (userName!=null) MDC.put(LOG_USER_NAME, userName);
		if (community!=null) MDC.put(LOG_COMMUNITY, community);
  		if (c!=null) MDC.put(LOG_CLASS, c);
  		if (commandName!=null) MDC.put(LOG_COMMAND, commandName);
  		MDC.put(LOG_REMOTE_ADDR, remoteAddr);
  		
		final long startTimeNano = System.nanoTime();
		MDC.put(LOG_START_TIME_NANO, startTimeNano);
	}
	
	public static final void removeGlobalMDC() {
		if (MDC.get(LOG_USER_NAME) != null) MDC.remove(LOG_USER_NAME);
		if (MDC.get(LOG_COMMUNITY) != null) MDC.remove(LOG_COMMUNITY);
		if (MDC.get(LOG_CLASS) != null) MDC.remove(LOG_CLASS);
		if (MDC.get(LOG_COMMAND) != null) MDC.remove(LOG_COMMAND);
		MDC.remove(LOG_REMOTE_ADDR);
		removeStartTimeNanoMDC();
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

	public static void putSpecificMDC(String userName, String community) {
		if (userName!=null) MDC.put(LOG_USER_NAME, userName);
		if (community!=null) MDC.put(LOG_COMMUNITY, community);
	}

	public static void removeSpecificMDC() {
		if (MDC.get(LOG_USER_NAME) != null) MDC.remove(LOG_USER_NAME);
		if (MDC.get(LOG_COMMUNITY) != null) MDC.remove(LOG_COMMUNITY);
	}
	
	public static Long getStartTimeNano() {
		return (Long) MDC.get(LOG_START_TIME_NANO);
	}

	public static void removeStartTimeNanoMDC() {
		if (MDC.get(LOG_START_TIME_NANO) != null)
			MDC.remove(LOG_START_TIME_NANO);
	}
}