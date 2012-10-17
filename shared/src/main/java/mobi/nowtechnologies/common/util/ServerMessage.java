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
 * 
 */
public class ServerMessage {
	
	/**
	 * 
	 */
	public static final String EN = "en";

	private static final Map<String,Map<Integer,String>> LOCALIZED_CODE_MESSAGE_MAP;
	
	static{
		Map<String,Map<Integer,String>> map = new HashMap<String,Map<Integer,String>>();
		
		Map<Integer,String> enLocalizedMessageMap = new HashMap<Integer, String>();
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
		
		LOCALIZED_CODE_MESSAGE_MAP=Collections.unmodifiableMap(map);
	}
	
	private final Integer errorCode;

	private final Map<String,String> parameters;
	
	public ServerMessage(Integer aErrorCode, Map<String,String> aParameters){
		if (aErrorCode == null)
			throw new NullPointerException("The parameter aErrorCode is null");
		if (aParameters == null)
			throw new NullPointerException("The parameter aParameters is null");
		errorCode=aErrorCode;
		parameters=aParameters;
	}
	public Integer getErrorCode() {
		return errorCode;
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	//TODO replace all
	public static String getMessage(String locale, Integer errorCode, Map<String,String> parameters){
		if (locale == null)
			throw new NullPointerException("The parameter aLocale is null");
		if (errorCode == null)
			throw new NullPointerException("The parameter errorCode is null");
		if (parameters == null)
			throw new NullPointerException("The parameter aParameters is null");
		
		Map<Integer,String> localizedMessageMap=LOCALIZED_CODE_MESSAGE_MAP.get(locale);
		StringBuilder message=new StringBuilder(localizedMessageMap.get(errorCode));
		
		for (Entry<String, String> parameterEntry : parameters.entrySet()) {
			String key="{"+parameterEntry.getKey()+"}";
			int start =message.indexOf(key);
			if (-1 != start)
				message.replace(start, start+key.length(), parameterEntry.getValue());
		}
		
		return message.toString();	
	}

	public static ServerMessage getMessageOnNullValue(Class aClass,
			String aPropertyName) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		return new ServerMessage(1, parameters);
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object is null";
	}

	public static ServerMessage getMessageOnEmptyValue(Class aClass,
			String aPropertyName) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object is empty";
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		return new ServerMessage(2, parameters);
	}

	public static ServerMessage getMessageOnInvalidValue(Class aClass,
			String aPropertyName, Object aPropertyValue) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object contains invalid value: " + aPropertyValue;
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		parameters.put("propertyValue", aPropertyValue.toString());
		return new ServerMessage(3, parameters);
	}

	public static ServerMessage getMessageOnBigValueLenthValue(Class aClass,
			String aPropertyName, Object aPropertyValue, long aMaxValue) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object contains invalid value: " + aPropertyValue
//				+ ". It more than allowing value:" + aMaxValue;
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		parameters.put("propertyValue", aPropertyValue.toString());
		parameters.put("maxValue", String.valueOf(aMaxValue));
		return new ServerMessage(4, parameters);
	}

	public static ServerMessage getMessageOnSmallValueLenthValue(Class aClass,
			String aPropertyName, Object aPropertyValue, long aMinValue) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object contains invalid value: " + aPropertyValue
//				+ ". It less than allowing value:" + aMinValue;
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		parameters.put("propertyValue", aPropertyValue.toString());
		parameters.put("minValue", String.valueOf(aMinValue));
		return new ServerMessage(5, parameters);
	}

	public static ServerMessage getMessageOnInvalidMonthNumberValue(Class aClass,
			String aPropertyName, int aMotnth) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object contains invalid month number: " + aMotnth;
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		parameters.put("motnth", String.valueOf(aMotnth));
		return new ServerMessage(6, parameters);
	}

	/**
	 * @param aClass
	 * @param aPropertyName
	 * @param aYear
	 * @return
	 */
	public static ServerMessage getMessageOnInvalidYearNumberValue(Class aClass,
			String aPropertyName, int aYear) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object contains invalid year value: " + aExpirationYear;
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		parameters.put("year", String.valueOf(aYear));
		return new ServerMessage(7, parameters);
	}

	/**
	 * @param aClass
	 * @param aPropertyName
	 * @param phoneNumber
	 * @return
	 */
	public static ServerMessage getMessageOnInvalidPhoneNumber(Class aClass,
			String aPropertyName, String phoneNumber) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object contains invalid phone number value: " + phoneNumber;
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		parameters.put("phoneNumber", phoneNumber);
		return new ServerMessage(8, parameters);
	}

	/**
	 * @param aClass
	 * @param aPropertyName
	 * @param aCreditCardNumber
	 * @return
	 */
	public static ServerMessage getMessageOnInvalidCardNumber(Class aClass,
			String aPropertyName, String aCreditCardNumber) {
		if (aPropertyName == null)
			throw new NullPointerException(
					"The parameter aPropertyName is null");
		if (aClass == null)
			throw new NullPointerException("The parameter aClass is null");
//		return "The property '" + aPropertyName + "' in " + aClass
//				+ " object contains invalid credit card number value: "
//				+ aCreditCardNumber;
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", aPropertyName);
		parameters.put("class", aClass.getSimpleName());
		parameters.put("creditCardNumber", aCreditCardNumber);
		return new ServerMessage(9, parameters);
	}
	public static ServerMessage getMessageOnInvalidIssueNumber(Class<?> clazz,
			String propertyName, String cardIssueNumberValue) {
		if (propertyName == null)
			throw new NullPointerException(
					"The parameter propertyName is null");
		if (clazz == null)
			throw new NullPointerException(
					"The parameter clazz is null");
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", propertyName);
		parameters.put("class", clazz.getSimpleName());
		parameters.put("cardIssueNumberValue", cardIssueNumberValue);
		return new ServerMessage(10, parameters);
	}
	public static ServerMessage getMessageOnInvalidMobileOperatorId(Class<?> clazz,
			String propertyName, int operatorId) {
		if (propertyName == null)
			throw new NullPointerException(
					"The parameter propertyName is null");
		if (clazz == null)
			throw new NullPointerException(
					"The parameter clazz is null");
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("propertyName", propertyName);
		parameters.put("class", clazz.getSimpleName());
		parameters.put("operatorId", String.valueOf(operatorId));
		return new ServerMessage(11, parameters);
	}
	
	public static ServerMessage getInvalidPassedStoredToken(String userName, String communityName) {
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("userName", userName);
		parameters.put("communityName", communityName);
		return new ServerMessage(12, parameters);
	}	
	
	public static ServerMessage getMessageOnUnExistUser(String userName, String communityName) {
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("userName", userName);
		parameters.put("communityName", communityName);
		return new ServerMessage(13, parameters);
	}
	
	public static ServerMessage getInvalidPassedStoredTokenForDeviceUID(String deviceUID, String communityRedirectUrl) {
		Map<String,String> parameters = new HashMap<String, String>();
		parameters.put("deviceUID", deviceUID);
		parameters.put("communityRedirectUrl", communityRedirectUrl);
		return new ServerMessage(14, parameters);
	}
	
	public static ServerMessage getMessageOnUserExsist(String userName, String communityName) {
		Map<String,String> parameters = new HashMap<String, String>();
		//parameters.put("userName", userName);
		//parameters.put("communityName", communityName);
		return new ServerMessage(15, parameters);
	}
}