package mobi.nowtechnologies.server.shared.dto;

import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.enums.SubscriptionDirection;

import javax.xml.bind.annotation.XmlRootElement;

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
    private ActivationStatus activation;
    
    private String appStoreProductId;
    
    private String provider;
    private String contract;
    private String segment;
    private int graceCreditSeconds;

    private Boolean canGetVideo;
    private Boolean canPlayVideo;
    private Boolean hasAllDetails;
    private Boolean showFreeTrial;
    private Boolean canActivateVideoTrial;

	private String lastSubscribedPaymentSystem;
    private SubscriptionDirection subscriptionChanged;

    public AccountCheckDTO(){
		
	}
	
	public AccountCheckDTO(AccountCheckDTO accountCheckDTO) 
	{
	    this.displayName = accountCheckDTO.displayName;
	    this.subBalance = accountCheckDTO.subBalance;
	    this.status = accountCheckDTO.status;
	    this.deviceType = accountCheckDTO.deviceType;
	    this.deviceUID = accountCheckDTO.deviceUID;
	    this.chartTimestamp = accountCheckDTO.chartTimestamp;
	    this.chartItems = accountCheckDTO.chartItems;
	    this.newsTimestamp = accountCheckDTO.newsTimestamp;
	    this.newsItems = accountCheckDTO.newsItems;
	    this.drmType = accountCheckDTO.drmType;
	    this.drmValue = accountCheckDTO.drmValue;
	    this.phoneNumber = accountCheckDTO.phoneNumber;
	    this.operator = accountCheckDTO.operator;
	    this.paymentStatus = accountCheckDTO.paymentStatus;
	    this.paymentType = accountCheckDTO.paymentType;
	    this.paymentEnabled = accountCheckDTO.paymentEnabled;
	    this.rememberMeToken = accountCheckDTO.rememberMeToken;
	    this.userName = accountCheckDTO.userName;
	    this.userToken = accountCheckDTO.userToken;
	    this.timeOfMovingToLimitedStatusSeconds = accountCheckDTO.timeOfMovingToLimitedStatusSeconds;
	    this.promotionLabel = accountCheckDTO.promotionLabel;
	    this.fullyRegistred = accountCheckDTO.fullyRegistred;
	    this.oAuthProvider = accountCheckDTO.oAuthProvider;
	    this.isPromotedDevice = accountCheckDTO.isPromotedDevice;
	    this.promotedWeeks = accountCheckDTO.promotedWeeks;
	    this.hasPotentialPromoCodePromotion = accountCheckDTO.hasPotentialPromoCodePromotion;
	    this.hasOffers = accountCheckDTO.hasOffers;
	    this.isFreeTrial = accountCheckDTO.isFreeTrial;
	    this.lastPaymentStatus = accountCheckDTO.lastPaymentStatus;
	    this.nextSubPaymentSeconds = accountCheckDTO.nextSubPaymentSeconds;
	    this.activation = accountCheckDTO.activation;
	    this.appStoreProductId = accountCheckDTO.appStoreProductId;
	    this.provider = accountCheckDTO.provider;
	    this.contract = accountCheckDTO.contract;
	    this.segment = accountCheckDTO.segment;
	    this.graceCreditSeconds = accountCheckDTO.graceCreditSeconds;
	    this.lastSubscribedPaymentSystem = accountCheckDTO.lastSubscribedPaymentSystem;
	}

	public int getGraceCreditSeconds() {
		return graceCreditSeconds;
	}

	public void setGraceCreditSeconds(int graceCreditSeconds) {
		this.graceCreditSeconds = graceCreditSeconds;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

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

    public ActivationStatus getActivation() {
        return activation;
    }

    public void setActivation(ActivationStatus activation) {
        this.activation = activation;
    }

    public String getAppStoreProductId() {
		return appStoreProductId;
	}

	public void setAppStoreProductId(String appStoreProductId) {
		this.appStoreProductId = appStoreProductId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public String getLastSubscribedPaymentSystem() {
		return lastSubscribedPaymentSystem;
	}

	public void setLastSubscribedPaymentSystem(String lastSubscribedPaymentSystem) {
		this.lastSubscribedPaymentSystem = lastSubscribedPaymentSystem;
	}

	@Override
	public String toString() {
		return "AccountCheckDTO [displayName=" + displayName + ", subBalance=" + subBalance + ", status=" + status + ", deviceType=" + deviceType + ", deviceUID=" + deviceUID + ", chartTimestamp="
				+ chartTimestamp + ", chartItems=" + chartItems + ", newsTimestamp=" + newsTimestamp + ", newsItems=" + newsItems + ", drmType=" + drmType + ", drmValue=" + drmValue
				+ ", phoneNumber=" + phoneNumber + ", operator=" + operator + ", paymentStatus=" + paymentStatus + ", paymentType=" + paymentType + ", paymentEnabled=" + paymentEnabled
				+ ", rememberMeToken=" + rememberMeToken + ", userName=" + userName + ", userToken=" + userToken + ", timeOfMovingToLimitedStatusSeconds=" + timeOfMovingToLimitedStatusSeconds
				+ ", promotionLabel=" + promotionLabel + ", fullyRegistred=" + fullyRegistred + ", oAuthProvider=" + oAuthProvider + ", isPromotedDevice=" + isPromotedDevice + ", promotedWeeks="
				+ promotedWeeks + ", hasPotentialPromoCodePromotion=" + hasPotentialPromoCodePromotion + ", hasOffers=" + hasOffers + ", isFreeTrial=" + isFreeTrial + ", lastPaymentStatus="
				+ lastPaymentStatus + ", nextSubPaymentSeconds=" + nextSubPaymentSeconds + ", activation=" + activation + ", appStoreProductId=" + appStoreProductId + ", provider=" + provider
				+ ", contract=" + contract + ", segment=" + segment + ", graceCreditSeconds=" + graceCreditSeconds + ", lastSubscribedPaymentSystem=" + lastSubscribedPaymentSystem + "]";
	}

    public Boolean getCanGetVideo() {
        return canGetVideo;
    }

    public void setCanGetVideo(Boolean canGetVideo) {
        this.canGetVideo = canGetVideo;
    }

    public Boolean getCanPlayVideo() {
        return canPlayVideo;
    }

    public void setCanPlayVideo(Boolean canPlayVideo) {
        this.canPlayVideo = canPlayVideo;
    }

    public Boolean getHasAllDetails() {
        return hasAllDetails;
    }

    public void setHasAllDetails(Boolean hasAllDetails) {
        this.hasAllDetails = hasAllDetails;
    }

    public Boolean getShowFreeTrial() {
        return showFreeTrial;
    }

    public void setShowFreeTrial(Boolean showFreeTrial) {
        this.showFreeTrial = showFreeTrial;
    }

    public Boolean getCanActivateVideoTrial() {
        return canActivateVideoTrial;
    }

    public void setCanActivateVideoTrial(Boolean canActivateVideoTrial) {
        this.canActivateVideoTrial = canActivateVideoTrial;
    }

    public void setSubscriptionChanged(SubscriptionDirection subscriptionChanged) {
        this.subscriptionChanged = subscriptionChanged;
    }

    public SubscriptionDirection getSubscriptionChanged() {
        return subscriptionChanged;
    }
}
