package mobi.nowtechnologies.server.shared.dto;

import javax.xml.bind.annotation.XmlRootElement;

import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

/**
 * AccountCheck
 * 
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 * 
 */
@XmlRootElement(name = "user")
public class AccountCheckDTO {
	private String displayName;
	private byte subBalance;
	private String status;
	private String deviceType;
	private String deviceUID;
	private int chartTimestamp;
	@Deprecated
	private byte chartItems;
	private int newsTimestamp;
	@Deprecated
	private byte newsItems;
	private String drmType;
	private byte drmValue;

	private String phoneNumber;
	private Integer operator;
	private String paymentStatus;
	private String paymentType;
	private boolean paymentEnabled;

	private String rememberMeToken;

	private String userName;
	private String userToken;

	private int timeOfMovingToLimitedStatusSeconds;

	private String promotionLabel;

	private boolean fullyRegistred;
	private OAuthProvider oAuthProvider;
	private boolean isPromotedDevice;
	private int promotedWeeks;
	private boolean hasPotentialPromoCodePromotion;
	
	private boolean hasOffers;
	private boolean isFreeTrial;
	private PaymentDetailsStatus lastPaymentStatus;
	
	private int nextSubPaymentSeconds;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public byte getSubBalance() {
		return subBalance;
	}

	public void setSubBalance(byte subBalance) {
		this.subBalance = subBalance;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceUID() {
		return deviceUID;
	}

	public void setDeviceUID(String deviceUID) {
		this.deviceUID = deviceUID;
	}

	public int getChartTimestamp() {
		return chartTimestamp;
	}

	public void setChartTimestamp(int chartTimestamp) {
		this.chartTimestamp = chartTimestamp;
	}

	@Deprecated
	public byte getChartItems() {
		return chartItems;
	}

	@Deprecated
	public void setChartItems(byte chartItems) {
		this.chartItems = chartItems;
	}

	public int getNewsTimestamp() {
		return newsTimestamp;
	}

	public void setNewsTimestamp(int newsTimestamp) {
		this.newsTimestamp = newsTimestamp;
	}

	@Deprecated
	public byte getNewsItems() {
		return newsItems;
	}

	@Deprecated
	public void setNewsItems(byte newsItems) {
		this.newsItems = newsItems;
	}

	public String getDrmType() {
		return drmType;
	}

	public void setDrmType(String drmType) {
		this.drmType = drmType;
	}

	public byte getDrmValue() {
		return drmValue;
	}

	public void setDrmValue(byte drmValue) {
		this.drmValue = drmValue;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Integer getOperator() {
		return operator;
	}

	public void setOperator(Integer operator) {
		this.operator = operator;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;

	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentEnabled(boolean paymentEnabled) {
		this.paymentEnabled = paymentEnabled;
	}

	public boolean getPaymentEnabled() {
		return paymentEnabled;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public int getTimeOfMovingToLimitedStatusSeconds() {
		return timeOfMovingToLimitedStatusSeconds;
	}

	public void setTimeOfMovingToLimitedStatusSeconds(int timeOfMovingToLimitedStatusSeconds) {
		this.timeOfMovingToLimitedStatusSeconds = timeOfMovingToLimitedStatusSeconds;
	}

	public void setRememberMeToken(String rememberMeToken) {
		this.rememberMeToken = rememberMeToken;
	}

	public String getRememberMeToken() {
		return rememberMeToken;
	}

	public String getPromotionLabel() {
		return promotionLabel;
	}

	public void setPromotionLabel(String promotionLabel) {
		this.promotionLabel = promotionLabel;
	}

	public boolean isFullyRegistred() {
		return fullyRegistred;
	}

	public void setFullyRegistred(boolean fullyRegistred) {
		this.fullyRegistred = fullyRegistred;
	}

	public boolean isPromotedDevice() {
		return isPromotedDevice;
	}

	public void setPromotedDevice(boolean isPromotedDevice) {
		this.isPromotedDevice = isPromotedDevice;
	}

	public int getPromotedWeeks() {
		return promotedWeeks;
	}

	public void setPromotedWeeks(int promotedWeeks) {
		this.promotedWeeks = promotedWeeks;
	}

	public OAuthProvider getoAuthProvider() {
		return oAuthProvider;
	}

	public void setoAuthProvider(OAuthProvider oAuthProvider) {
		this.oAuthProvider = oAuthProvider;
	}
	
	public boolean isHasPotentialPromoCodePromotion() {
		return hasPotentialPromoCodePromotion;
	}

	public void setHasPotentialPromoCodePromotion(
			boolean hasPotentialPromoCodePromotion) {
		this.hasPotentialPromoCodePromotion = hasPotentialPromoCodePromotion;
	}

	public boolean isHasOffers() {
		return hasOffers;
	}

	public void setHasOffers(boolean hasOffers) {
		this.hasOffers = hasOffers;
	}
	
	public boolean isFreeTrial() {
		return isFreeTrial;
	}

	public void setFreeTrial(boolean isFreeTrial) {
		this.isFreeTrial = isFreeTrial;
	}
	
	public PaymentDetailsStatus getLastPaymentStatus() {
		return lastPaymentStatus;
	}

	public void setLastPaymentStatus(PaymentDetailsStatus lastPaymentStatus) {
		this.lastPaymentStatus = lastPaymentStatus;
	}

	public int getNextSubPaymentSeconds() {
		return nextSubPaymentSeconds;
	}

	public void setNextSubPaymentSeconds(int nextSubPaymentSeconds) {
		this.nextSubPaymentSeconds = nextSubPaymentSeconds;
	}

	@Override
	public String toString() {
		return "AccountCheckDTO [userName=" + userName + ", deviceUID=" + deviceUID + ", subBalance=" + subBalance + ", rememberMeToken=" + rememberMeToken
				+ ", status=" + status + ", paymentEnabled=" + paymentEnabled + ", paymentStatus=" + paymentStatus + ", lastPaymentStatus=" + lastPaymentStatus
				+ ", fullyRegistred=" + fullyRegistred + ", nextSubPaymentSeconds=" + nextSubPaymentSeconds + ", isFreeTrial=" + isFreeTrial
				+ ", isPromotedDevice=" + isPromotedDevice + ", hasPotentialPromoCodePromotion=" + hasPotentialPromoCodePromotion + ", promotedWeeks="
				+ promotedWeeks + ", paymentType=" + paymentType + ", hasOffers=" + hasOffers + ", promotionLabel=" + promotionLabel
				+ ", timeOfMovingToLimitedStatusSeconds=" + timeOfMovingToLimitedStatusSeconds + ", chartItems=" + chartItems + ", chartTimestamp="
				+ chartTimestamp + ", deviceType=" + deviceType + ", displayName=" + displayName + ", drmType=" + drmType + ", drmValue=" + drmValue
				+ ", newsItems=" + newsItems + ", newsTimestamp=" + newsTimestamp + ", oAuthProvider=" + oAuthProvider + ", operator=" + operator
				+ ", phoneNumber=" + phoneNumber + ", userToken=" + userToken + "]";
	}	

}
