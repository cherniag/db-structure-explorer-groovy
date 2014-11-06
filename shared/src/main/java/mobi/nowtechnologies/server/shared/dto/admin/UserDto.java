package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.UserStatus;
import mobi.nowtechnologies.server.shared.enums.UserType;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static mobi.nowtechnologies.server.shared.Utils.truncatedToSeconds;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserDto {

	public static final String USER_DTO_LIST = "USER_DTO_LIST";

	public static final String USER_DTO = "USER_DTO";

	private Integer id;

	private String address1;

	private String address2;

	private boolean canContact;

	private String city;

	private String code;

	private int country;

	private String device;

	private String deviceString;

	private byte deviceTypeId;

	private String deviceModel;

	private String deviceType;

	private String displayName;

	private String firstName;

	private byte freeBalance;

	private String ipAddress;

	private Date lastDeviceLogin;

	private String lastName;

	private int lastPaymentTx;

	private Date lastWebLogin;

	private String mobile;

	private Date nextSubPayment;

	private String postcode;

	private String sessionID;

	private byte userStatusId;

	private UserStatus userStatus;

	private int subBalance;

	private String tempToken;

	private String title;

	private String token;

	private int userGroupId;

	private String userGroup;

	private String userName;

	private UserType userType;

	private int operator;

	private String pin;

	private String paymentType;

	private boolean paymentEnabled;

	private int numPsmsRetries;

	private List<PaymentDetailsDto> paymentDetailsDtos;

	private PaymentDetailsDto currentPaymentDetailsDto;

	private PromotionDto potentialPromotionDto;

	private PromotionDto potentialPromoCodePromotionDto;

	private Integer potentialPromoCodePromotionId;

	// TODO: added for release 3.4.1 for Samsung. This field should be deleted
	// in next releases.
	private boolean isFreeTrial;

	private String newStoredToken;

	private String conformStoredToken;

	private int paymentStatus;

	private Date lastSuccessfulPaymentTime;

	private String facebookId;

	private String deviceUID;

	private Date firstDeviceLogin;

	private Date firstUserLogin;

	private Long currentPaymentDetailsId;

	private BigDecimal amountOfMoneyToUserNotification;

	private Integer potentialPromotionId;

	private Date lastSuccesfullPaymentSmsSendingTimestamp;
    private int freeTrialExpiredMillis;

    public UserDto(Integer id, String address1, String address2, boolean canContact, String city, String code, int country, String device, String deviceString,
			byte deviceTypeId, String deviceModel, String deviceType, String displayName, String firstName, byte freeBalance, String ipAddress,
			Date lastDeviceLogin, String lastName, int lastPaymentTx, Date lastWebLogin, String mobile, Date nextSubPayment, String postcode, String sessionID,
			byte userStatusId, UserStatus userStatus, int subBalance, String tempToken, String title, String token, byte userGroupId, String userGroup,
			String userName, UserType userType, int operator, String pin, String paymentType, boolean paymentEnabled, int numPsmsRetries,
			List<PaymentDetailsDto> paymentDetailsDtos, PaymentDetailsDto currentPaymentDetailsDto, PromotionDto potentialPromotionDto,
			PromotionDto potentialPromoCodePromotionDto, Integer potentialPromoCodePromotionId, boolean isFreeTrial, String newStoredToken,
			String conformStoredToken, int paymentStatus, Date lastSuccessfulPaymentTime, String facebookId, String deviceUID, Date firstDeviceLogin,
			Date firstUserLogin, Long currentPaymentDetailsId, BigDecimal amountOfMoneyToUserNotification, Integer potentialPromotionId, Date lastSuccesfullPaymentSmsSendingTimestamp) {
		super();
		this.id = id;
		this.address1 = address1;
		this.address2 = address2;
		this.canContact = canContact;
		this.city = city;
		this.code = code;
		this.country = country;
		this.device = device;
		this.deviceString = deviceString;
		this.deviceTypeId = deviceTypeId;
		this.deviceModel = deviceModel;
		this.deviceType = deviceType;
		this.displayName = displayName;
		this.firstName = firstName;
		this.freeBalance = freeBalance;
		this.ipAddress = ipAddress;
		this.lastDeviceLogin = lastDeviceLogin;
		this.lastName = lastName;
		this.lastPaymentTx = lastPaymentTx;
		this.lastWebLogin = lastWebLogin;
		this.mobile = mobile;
		this.nextSubPayment = nextSubPayment;
		this.postcode = postcode;
		this.sessionID = sessionID;
		this.userStatusId = userStatusId;
		this.userStatus = userStatus;
		this.subBalance = subBalance;
		this.tempToken = tempToken;
		this.title = title;
		this.token = token;
		this.userGroupId = userGroupId;
		this.userGroup = userGroup;
		this.userName = userName;
		this.userType = userType;
		this.operator = operator;
		this.pin = pin;
		this.paymentType = paymentType;
		this.paymentEnabled = paymentEnabled;
		this.numPsmsRetries = numPsmsRetries;
		this.paymentDetailsDtos = paymentDetailsDtos;
		this.currentPaymentDetailsDto = currentPaymentDetailsDto;
		this.potentialPromotionDto = potentialPromotionDto;
		this.potentialPromoCodePromotionDto = potentialPromoCodePromotionDto;
		this.potentialPromoCodePromotionId = potentialPromoCodePromotionId;
		this.isFreeTrial = isFreeTrial;
		this.newStoredToken = newStoredToken;
		this.conformStoredToken = conformStoredToken;
		this.paymentStatus = paymentStatus;
		this.lastSuccessfulPaymentTime = lastSuccessfulPaymentTime;
		this.facebookId = facebookId;
		this.deviceUID = deviceUID;
		this.firstDeviceLogin = firstDeviceLogin;
		this.firstUserLogin = firstUserLogin;
		this.currentPaymentDetailsId = currentPaymentDetailsId;
		this.amountOfMoneyToUserNotification = amountOfMoneyToUserNotification;
		this.potentialPromotionId = potentialPromotionId;
		this.lastSuccesfullPaymentSmsSendingTimestamp = lastSuccesfullPaymentSmsSendingTimestamp;
	}

	public UserDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public boolean isCanContact() {
		return canContact;
	}

	public void setCanContact(boolean canContact) {
		this.canContact = canContact;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getCountry() {
		return country;
	}

	public void setCountry(int country) {
		this.country = country;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDeviceString() {
		return deviceString;
	}

	public void setDeviceString(String deviceString) {
		this.deviceString = deviceString;
	}

	public byte getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(byte deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public byte getFreeBalance() {
		return freeBalance;
	}

	public void setFreeBalance(byte freeBalance) {
		this.freeBalance = freeBalance;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Date getLastDeviceLogin() {
		return lastDeviceLogin;
	}

	public void setLastDeviceLogin(Date lastDeviceLogin) {
		this.lastDeviceLogin = lastDeviceLogin;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getLastPaymentTx() {
		return lastPaymentTx;
	}

	public void setLastPaymentTx(int lastPaymentTx) {
		this.lastPaymentTx = lastPaymentTx;
	}

	public Date getLastWebLogin() {
		return lastWebLogin;
	}

	public void setLastWebLogin(Date lastWebLogin) {
		this.lastWebLogin = lastWebLogin;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getNextSubPayment() {
		return nextSubPayment;
	}

	public void setNextSubPayment(Date nextSubPayment) {
		this.nextSubPayment = new Date(Utils.truncatedToSeconds(nextSubPayment)*1000L);
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public byte getUserStatusId() {
		return userStatusId;
	}

	public void setUserStatusId(byte userStatusId) {
		this.userStatusId = userStatusId;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public int getSubBalance() {
		return subBalance;
	}

	public void setSubBalance(int subBalance) {
		this.subBalance = subBalance;
	}

	public String getTempToken() {
		return tempToken;
	}

	public void setTempToken(String tempToken) {
		this.tempToken = tempToken;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public boolean getPaymentEnabled() {
		return paymentEnabled;
	}

	public void setPaymentEnabled(boolean paymentEnabled) {
		this.paymentEnabled = paymentEnabled;
	}

	public int getNumPsmsRetries() {
		return numPsmsRetries;
	}

	public void setNumPsmsRetries(int numPsmsRetries) {
		this.numPsmsRetries = numPsmsRetries;
	}

	public List<PaymentDetailsDto> getPaymentDetailsDtos() {
		return paymentDetailsDtos;
	}

	public void setPaymentDetailsDtos(List<PaymentDetailsDto> paymentDetailsDtos) {
		this.paymentDetailsDtos = paymentDetailsDtos;
	}

	public PaymentDetailsDto getCurrentPaymentDetailsDto() {
		return currentPaymentDetailsDto;
	}

	public void setCurrentPaymentDetailsDto(PaymentDetailsDto currentPaymentDetailsDto) {
		this.currentPaymentDetailsDto = currentPaymentDetailsDto;
	}

	public PromotionDto getPotentialPromotionDto() {
		return potentialPromotionDto;
	}

	public void setPotentialPromotionDto(PromotionDto potentialPromotionDto) {
		this.potentialPromotionDto = potentialPromotionDto;
	}

	public PromotionDto getPotentialPromoCodePromotionDto() {
		return potentialPromoCodePromotionDto;
	}

	public void setPotentialPromoCodePromotionDto(PromotionDto potentialPromoCodePromotionDto) {
		this.potentialPromoCodePromotionDto = potentialPromoCodePromotionDto;
	}

	public Integer getPotentialPromoCodePromotionId() {
		return potentialPromoCodePromotionId;
	}

	public void setPotentialPromoCodePromotionId(Integer potentialPromoCodePromotionId) {
		this.potentialPromoCodePromotionId = potentialPromoCodePromotionId;
	}

	public boolean getIsFreeTrial() {
		return isFreeTrial;
	}

	public void setFreeTrial(boolean isFreeTrial) {
		this.isFreeTrial = isFreeTrial;
	}

	public String getNewStoredToken() {
		return newStoredToken;
	}

	public void setNewStoredToken(String newStoredToken) {
		this.newStoredToken = newStoredToken;
	}

	public String getConformStoredToken() {
		return conformStoredToken;
	}

	public void setConformStoredToken(String conformStoredToken) {
		this.conformStoredToken = conformStoredToken;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Date getLastSuccessfulPaymentTime() {
		return lastSuccessfulPaymentTime;
	}

	public void setLastSuccessfulPaymentTime(Date lastSuccessfulPaymentTime) {
		this.lastSuccessfulPaymentTime = lastSuccessfulPaymentTime;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getDeviceUID() {
		return deviceUID;
	}

	public void setDeviceUID(String deviceUID) {
		this.deviceUID = deviceUID;
	}

	public Date getFirstDeviceLogin() {
		return firstDeviceLogin;
	}

	public void setFirstDeviceLogin(Date firstDeviceLogin) {
		this.firstDeviceLogin = firstDeviceLogin;
	}

	public Date getFirstUserLogin() {
		return firstUserLogin;
	}

	public void setFirstUserLogin(Date firstUserLogin) {
		this.firstUserLogin = firstUserLogin;
	}

	public Long getCurrentPaymentDetailsId() {
		return currentPaymentDetailsId;
	}

	public void setCurrentPaymentDetailsId(Long currentPaymentDetailsId) {
		this.currentPaymentDetailsId = currentPaymentDetailsId;
	}

	public BigDecimal getAmountOfMoneyToUserNotification() {
		return amountOfMoneyToUserNotification;
	}

	public void setAmountOfMoneyToUserNotification(BigDecimal amountOfMoneyToUserNotification) {
		this.amountOfMoneyToUserNotification = amountOfMoneyToUserNotification;
	}

	public Integer getPotentialPromotionId() {
		return potentialPromotionId;
	}

	public void setPotentialPromotionId(Integer potentialPromotionId) {
		this.potentialPromotionId = potentialPromotionId;
	}

	public Date getLastSuccesfullPaymentSmsSendingTimestamp() {
		return lastSuccesfullPaymentSmsSendingTimestamp;
	}

	public void setLastSuccesfullPaymentSmsSendingTimestamp(Date lastSuccesfullPaymentSmsSendingTimestamp) {
		this.lastSuccesfullPaymentSmsSendingTimestamp = lastSuccesfullPaymentSmsSendingTimestamp;
	}

    public UserDto withNextSubPayment(Date nextSubPayment) {
        setNextSubPayment(nextSubPayment);
        return this;
    }

    public int getFreeTrialExpiredMillis() {
        return freeTrialExpiredMillis;
    }

    public Date getFreeTrialExpiredAsDate() {
        return new Date(freeTrialExpiredMillis*1000L);
    }

    public UserDto withFreeTrialExpiredMillis(Date time) {
        this.freeTrialExpiredMillis = truncatedToSeconds(time);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("address1", address1)
                .append("address2", address2)
                .append("canContact", canContact)
                .append("city", city)
                .append("code", code)
                .append("country", country)
                .append("device", device)
                .append("deviceString", deviceString)
                .append("deviceTypeId", deviceTypeId)
                .append("deviceModel", deviceModel)
                .append("deviceType", deviceType)
                .append("displayName", displayName)
                .append("firstName", firstName)
                .append("freeBalance", freeBalance)
                .append("ipAddress", ipAddress)
                .append("lastDeviceLogin", lastDeviceLogin)
                .append("lastName", lastName)
                .append("lastPaymentTx", lastPaymentTx)
                .append("lastWebLogin", lastWebLogin)
                .append("mobile", mobile)
                .append("nextSubPayment", nextSubPayment)
                .append("postcode", postcode)
                .append("sessionID", sessionID)
                .append("userStatusId", userStatusId)
                .append("userStatus", userStatus)
                .append("subBalance", subBalance)
                .append("tempToken", tempToken)
                .append("title", title)
                .append("token", token)
                .append("userGroupId", userGroupId)
                .append("userGroup", userGroup)
                .append("userName", userName)
                .append("userType", userType)
                .append("operator", operator)
                .append("pin", pin)
                .append("paymentType", paymentType)
                .append("paymentEnabled", paymentEnabled)
                .append("numPsmsRetries", numPsmsRetries)
                .append("paymentDetailsDtos", paymentDetailsDtos)
                .append("currentPaymentDetailsDto", currentPaymentDetailsDto)
                .append("potentialPromotionDto", potentialPromotionDto)
                .append("potentialPromoCodePromotionDto", potentialPromoCodePromotionDto)
                .append("potentialPromoCodePromotionId", potentialPromoCodePromotionId)
                .append("isFreeTrial", isFreeTrial)
                .append("newStoredToken", newStoredToken)
                .append("conformStoredToken", conformStoredToken)
                .append("paymentStatus", paymentStatus)
                .append("lastSuccessfulPaymentTime", lastSuccessfulPaymentTime)
                .append("facebookId", facebookId)
                .append("deviceUID", deviceUID)
                .append("firstDeviceLogin", firstDeviceLogin)
                .append("firstUserLogin", firstUserLogin)
                .append("currentPaymentDetailsId", currentPaymentDetailsId)
                .append("amountOfMoneyToUserNotification", amountOfMoneyToUserNotification)
                .append("potentialPromotionId", potentialPromotionId)
                .append("lastSuccesfullPaymentSmsSendingTimestamp", lastSuccesfullPaymentSmsSendingTimestamp)
                .append("freeTrialExpiredMillis", freeTrialExpiredMillis)
                .toString();
    }
}
