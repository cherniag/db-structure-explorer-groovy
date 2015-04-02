package mobi.nowtechnologies.server.shared.log;

import org.apache.log4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {

    public static final String T_PR_RESPONSE = "tPRResponse";
    public static final String T_PR_BODY = "tPRBody";
    public static final String T_PR_NAME_VALUE_PAIRS = "tPRNameValuePairs";
    public static final String T_PR_URL = "tPRUrl";
    public static final String T_PR_RESULT = "tPRResult";
    public static final String T_PR_ERROR_MESSAGE = "tPRErrorMessage";
    public static final String T_PR_EXECUTION_DURATION_MILLIS = "tPRExecutionDurationMillis";
    public static final String T_PR_USER_NAME = "tPRUserName";
    public static final String T_PR_USER_MOBILE = "tPRUserMobile";
    public static final String T_PR_USER_ID = "tPRUserId";
    public static final String LOG_USER_NAME = "userName";
    public static final String LOG_COMMAND = "command";
    public static final String LOG_CLASS = "class";
    public static final String LOG_COMMUNITY = "community";
    public static final String LOG_USER_ID = "userId";
    public static final String LOG_USER_MOBILE = "userMobile";
    public static final String LOG_REMOTE_ADDR = "remoteAddr";
    public static final String LOG_START_TIME_NANO = "startTimeNano";
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);
    private static final String BINDING_RESULT = "bindingResult";

    public static final void putGlobalMDC(Object userId, String userMobile, String userName, String community, String commandName, Class<?> c, String remoteAddr) {
        if (userId != null) {
            MDC.put(LOG_USER_ID, userId);
        }
        if (userMobile != null) {
            MDC.put(LOG_USER_MOBILE, userMobile);
        }
        if (userName != null) {
            MDC.put(LOG_USER_NAME, userName);
        }
        if (community != null) {
            MDC.put(LOG_COMMUNITY, community);
        }
        if (c != null) {
            MDC.put(LOG_CLASS, c);
        }
        if (commandName != null) {
            MDC.put(LOG_COMMAND, commandName);
        }
        MDC.put(LOG_REMOTE_ADDR, remoteAddr);

        final long startTimeNano = System.nanoTime();
        MDC.put(LOG_START_TIME_NANO, startTimeNano);
    }

    public static final void removeGlobalMDC() {
        try {
            if (MDC.get(LOG_USER_ID) != null) {
                MDC.remove(LOG_USER_ID);
            }
            if (MDC.get(LOG_USER_MOBILE) != null) {
                MDC.remove(LOG_USER_MOBILE);
            }
            if (MDC.get(LOG_USER_NAME) != null) {
                MDC.remove(LOG_USER_NAME);
            }
            if (MDC.get(LOG_COMMUNITY) != null) {
                MDC.remove(LOG_COMMUNITY);
            }
            if (MDC.get(LOG_CLASS) != null) {
                MDC.remove(LOG_CLASS);
            }
            if (MDC.get(LOG_COMMAND) != null) {
                MDC.remove(LOG_COMMAND);
            }
            if (MDC.get(BINDING_RESULT) != null) {
                MDC.remove(BINDING_RESULT);
            }
            MDC.remove(LOG_REMOTE_ADDR);
            removeStartTimeNanoMDC();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static final void putClassNameMDC(Class<?> c) {
        MDC.put(LOG_CLASS, c);
    }

    public static final void removeClassNameMDC() {
        if (MDC.get(LOG_CLASS) != null) {
            MDC.remove(LOG_CLASS);
        }
    }

    public static final void putPaymentMDC(String userId, String userName, String communityName, Class<?> c) {
        MDC.put(LOG_USER_ID, userId);
        MDC.put(LOG_USER_NAME, userName);
        MDC.put(LOG_COMMUNITY, communityName);
        putClassNameMDC(c);
    }

    public static final void removePaymentMDC() {
        try {
            if (MDC.get(LOG_USER_ID) != null) {
                MDC.remove(LOG_USER_ID);
            }
            if (MDC.get(LOG_USER_NAME) != null) {
                MDC.remove(LOG_USER_NAME);
            }
            if (MDC.get(LOG_COMMUNITY) != null) {
                MDC.remove(LOG_COMMUNITY);
            }
            removeClassNameMDC();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
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

    public static void put3rdParyRequestProfileSpecificMDC(String userName, String userMobile, Object userId) {
        if (userName != null) {
            MDC.put(T_PR_USER_NAME, userName);
        } else {
            MDC.put(T_PR_USER_NAME, "n/a");
        }
        if (userMobile != null) {
            MDC.put(T_PR_USER_MOBILE, userMobile);
        } else {
            MDC.put(T_PR_USER_MOBILE, "n/a");
        }
        if (userId != null) {
            MDC.put(T_PR_USER_ID, userId);
        } else {
            MDC.put(T_PR_USER_ID, "n/a");
        }
    }

    public static void putSpecificMDC(String userName, String community) {
        if (userName != null) {
            MDC.put(LOG_USER_NAME, userName);
        }
        if (community != null) {
            MDC.put(LOG_COMMUNITY, community);
        }
    }

    public static void removeSpecificMDC() {
        if (MDC.get(LOG_USER_NAME) != null) {
            MDC.remove(LOG_USER_NAME);
        }
        if (MDC.get(LOG_COMMUNITY) != null) {
            MDC.remove(LOG_COMMUNITY);
        }
        if (MDC.get(LOG_USER_MOBILE) != null) {
            MDC.remove(LOG_USER_MOBILE);
        }
        if (MDC.get(LOG_USER_ID) != null) {
            MDC.remove(LOG_USER_ID);
        }
    }

    public static Long getStartTimeNano() {
        return (Long) MDC.get(LOG_START_TIME_NANO);
    }

    public static void removeStartTimeNanoMDC() {
        if (MDC.get(LOG_START_TIME_NANO) != null) {
            MDC.remove(LOG_START_TIME_NANO);
        }
    }

    public static Object getUserId() {
        return MDC.get(LOG_USER_ID);
    }

    public static void set3rdParyRequestProfileMDC(Object executionDurationMillis, Object errorMessage, Object result, Object url, Object nameValuePairs, Object body, Object responseMessage) {
        if (executionDurationMillis != null) {
            MDC.put(T_PR_EXECUTION_DURATION_MILLIS, executionDurationMillis);
        } else {
            MDC.put(T_PR_EXECUTION_DURATION_MILLIS, "n/a");
        }
        if (errorMessage != null) {
            MDC.put(T_PR_ERROR_MESSAGE, errorMessage);
        } else {
            MDC.put(T_PR_ERROR_MESSAGE, "n/a");
        }
        if (result != null) {
            MDC.put(T_PR_RESULT, result);
        } else {
            MDC.put(T_PR_RESULT, "n/a");
        }
        if (url != null) {
            MDC.put(T_PR_URL, url);
        } else {
            MDC.put(T_PR_URL, "n/a");
        }
        if (nameValuePairs != null) {
            MDC.put(T_PR_NAME_VALUE_PAIRS, nameValuePairs);
        } else {
            MDC.put(T_PR_NAME_VALUE_PAIRS, "n/a");
        }
        if (body != null) {
            MDC.put(T_PR_BODY, body);
        } else {
            MDC.put(T_PR_BODY, "n/a");
        }
        if (responseMessage != null) {
            MDC.put(T_PR_RESPONSE, responseMessage);
        } else {
            MDC.put(T_PR_RESPONSE, "n/a");
        }

    }

    public static void removeAll3rdParyRequestProfileMDC() {
        try {
            remove3rdParyRequestProfileMDCWithoutSpecific();
            if (MDC.get(T_PR_USER_NAME) != null) {
                MDC.remove(T_PR_USER_NAME);
            }
            if (MDC.get(T_PR_USER_MOBILE) != null) {
                MDC.remove(T_PR_USER_MOBILE);
            }
            if (MDC.get(T_PR_USER_ID) != null) {
                MDC.remove(T_PR_USER_ID);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public static void putBindingResultMDC(Object value) {
        if (value != null) {
            MDC.put(BINDING_RESULT, value);
        }
    }

    public static Object getBindingResultMDC() {
        return MDC.get(BINDING_RESULT);
    }

    public static void remove3rdParyRequestProfileMDCWithoutSpecific() {
        try {
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
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}