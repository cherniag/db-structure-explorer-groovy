/**
 *
 */

package mobi.nowtechnologies.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author Titov Mykhaylo (titov)
 */
public class ServerMessage {

    /**
     *
     */
    public static final String EN = "en";

    private static final Map<String, Map<Integer, String>> LOCALIZED_CODE_MESSAGE_MAP;

    static {
        Map<String, Map<Integer, String>> map = new HashMap<String, Map<Integer, String>>();

        Map<Integer, String> enLocalizedMessageMap = new HashMap<Integer, String>();
        enLocalizedMessageMap.put(1, "The property '{propertyName}' in {class} object is null");
        enLocalizedMessageMap.put(2, "The property '{propertyName}' in {class} object is empty");
        enLocalizedMessageMap.put(3, "The property '{propertyName}' in {class} object contains invalid value: {propertyValue}");
        enLocalizedMessageMap.put(4, "The property '{propertyName}' in {class} object contains invalid value: {propertyValue}. It more than allowing value: {maxValue}");
        enLocalizedMessageMap.put(5, "The property '{propertyName}' in {class} object contains invalid value: {propertyValue}. It less than allowing value: {minValue}");
        enLocalizedMessageMap.put(6, "The property '{propertyName}' in {class} object contains invalid month number: {motnth}");
        enLocalizedMessageMap.put(7, "The property '{propertyName}' in {class} object contains invalid year value: {year}");
        enLocalizedMessageMap.put(8, "The property '{propertyName}' in {class} object contains invalid phone number value: {phoneNumber}");
        enLocalizedMessageMap.put(9, "The property '{propertyName}' in {class} object contains invalid credit card number value: {creditCardNumber}");
        enLocalizedMessageMap.put(10, "The property '{propertyName}' in {class} object contains invalid credit card issue number value: {cardIssueNumberValue}");
        enLocalizedMessageMap.put(11, "The property '{propertyName}' in {class} object contains invalid database operator id value: {operatorId}");
        enLocalizedMessageMap.put(12, "user login/pass check failed for [{userName}] username and community [{communityName}]");
        enLocalizedMessageMap.put(13, "user login/pass check failed for [{userName}] username and community [{communityName}]");
        enLocalizedMessageMap.put(14, "user login/pass check failed for [{deviceUID}] deviceUID and communityRedirectUrl [{communityRedirectUrl}]");
        enLocalizedMessageMap.put(15, "This email is already exists");

        map.put(EN, enLocalizedMessageMap);

        LOCALIZED_CODE_MESSAGE_MAP = Collections.unmodifiableMap(map);
    }

    private final Integer errorCode;

    private final Map<String, String> parameters;

    public ServerMessage(Integer aErrorCode, Map<String, String> aParameters) {
        if (aErrorCode == null) {
            throw new NullPointerException("The parameter aErrorCode is null");
        }
        if (aParameters == null) {
            throw new NullPointerException("The parameter aParameters is null");
        }
        errorCode = aErrorCode;
        parameters = aParameters;
    }

    //TODO replace all
    public static String getMessage(String locale, Integer errorCode, Map<String, String> parameters) {
        if (locale == null) {
            throw new NullPointerException("The parameter aLocale is null");
        }
        if (errorCode == null) {
            throw new NullPointerException("The parameter errorCode is null");
        }
        if (parameters == null) {
            throw new NullPointerException("The parameter aParameters is null");
        }

        Map<Integer, String> localizedMessageMap = LOCALIZED_CODE_MESSAGE_MAP.get(locale);
        StringBuilder message = new StringBuilder(localizedMessageMap.get(errorCode));

        for (Entry<String, String> parameterEntry : parameters.entrySet()) {
            String key = "{" + parameterEntry.getKey() + "}";
            int start = message.indexOf(key);
            if (-1 != start) {
                message.replace(start, start + key.length(), parameterEntry.getValue());
            }
        }

        return message.toString();
    }

    public static ServerMessage getInvalidPassedStoredToken(String userName, String communityName) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("userName", userName);
        parameters.put("communityName", communityName);
        return new ServerMessage(12, parameters);
    }

    public static ServerMessage getMessageOnUnExistUser(String userName, String communityName) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("userName", userName);
        parameters.put("communityName", communityName);
        return new ServerMessage(13, parameters);
    }

    public static ServerMessage getInvalidPassedStoredTokenForDeviceUID(String deviceUID, String communityRedirectUrl) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("deviceUID", deviceUID);
        parameters.put("communityRedirectUrl", communityRedirectUrl);
        return new ServerMessage(14, parameters);
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

}