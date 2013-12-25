package mobi.nowtechnologies.server.shared.dto;

import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Titov Mykhaylo (titov)
 * @author Maksym Chernolevskyi (maksym)
 */
@XmlRootElement(name = "user")
@JsonRootName("user")
public class AccountCheckDTO {
	public String displayName;
	public byte subBalance;
	public String status;
	public String deviceType;
	public String deviceUID;
	public int chartTimestamp;
	@Deprecated
	public byte chartItems;
	public int newsTimestamp;
	@Deprecated
	public byte newsItems;
	public String drmType;
	public byte drmValue;

	public String phoneNumber;
	public Integer operator;
	public String paymentStatus;
	public String paymentType;
	public boolean paymentEnabled;

	public String rememberMeToken;

	public String userName;
	public String userToken;

	public int timeOfMovingToLimitedStatusSeconds;

	public String promotionLabel;

	public boolean fullyRegistred;
	public OAuthProvider oAuthProvider;
	public boolean promotedDevice;
	public int promotedWeeks;
	public boolean hasPotentialPromoCodePromotion;
	
	public boolean hasOffers;
	public boolean freeTrial;
	public PaymentDetailsStatus lastPaymentStatus;
	
	public int nextSubPaymentSeconds;
    public ActivationStatus activation;
    
    public String appStoreProductId;
    
    public String provider;
    public Contract contract;
    public SegmentType segment;
    public Tariff tariff;

    public int graceCreditSeconds;

    public Boolean canGetVideo;
    public Boolean canPlayVideo;
    public Boolean hasAllDetails;
    public Boolean showFreeTrial;
    public Boolean canActivateVideoTrial;

    public boolean eligibleForVideo;

    public String lastSubscribedPaymentSystem;
    public SubscriptionDirection subscriptionChanged;
    public boolean subjectToAutoOptIn;

    public transient Object user;

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
	    this.promotedDevice = accountCheckDTO.promotedDevice;
	    this.promotedWeeks = accountCheckDTO.promotedWeeks;
	    this.hasPotentialPromoCodePromotion = accountCheckDTO.hasPotentialPromoCodePromotion;
	    this.hasOffers = accountCheckDTO.hasOffers;
	    this.freeTrial = accountCheckDTO.freeTrial;
	    this.lastPaymentStatus = accountCheckDTO.lastPaymentStatus;
	    this.nextSubPaymentSeconds = accountCheckDTO.nextSubPaymentSeconds;
	    this.activation = accountCheckDTO.activation;
	    this.appStoreProductId = accountCheckDTO.appStoreProductId;
	    this.provider = accountCheckDTO.provider;
	    this.contract = accountCheckDTO.contract;
	    this.segment = accountCheckDTO.segment;
	    this.graceCreditSeconds = accountCheckDTO.graceCreditSeconds;
	    this.lastSubscribedPaymentSystem = accountCheckDTO.lastSubscribedPaymentSystem;

        this.canGetVideo = accountCheckDTO.canGetVideo;
        this.canPlayVideo = accountCheckDTO.canPlayVideo;
        this.canActivateVideoTrial = accountCheckDTO.canActivateVideoTrial;
        this.hasAllDetails = accountCheckDTO.hasAllDetails;
        this.showFreeTrial = accountCheckDTO.showFreeTrial;
        this.subscriptionChanged = accountCheckDTO.subscriptionChanged;

        this.activation = accountCheckDTO.activation;
        this.fullyRegistred = accountCheckDTO.fullyRegistred;
        this.eligibleForVideo = accountCheckDTO.eligibleForVideo;
        this.subjectToAutoOptIn = accountCheckDTO.subjectToAutoOptIn;
        this.tariff = accountCheckDTO.tariff;

        accountCheckDTO.fullyRegistred = EmailValidator.isEmail(userName);
	}

    public AccountCheckDTO withFullyRegistered(boolean isFullyRegistered){
        this.fullyRegistred = isFullyRegistered;
        return this;
    }

    public AccountCheckDTO withHasPotentialPromoCodePromotion(boolean hasPotentialPromoCodePromotion){
        this.hasPotentialPromoCodePromotion = hasPotentialPromoCodePromotion;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("displayName", displayName)
                .append("subBalance", subBalance)
                .append("status", status)
                .append("deviceType", deviceType)
                .append("deviceUID", deviceUID)
                .append("chartTimestamp", chartTimestamp)
                .append("chartItems", chartItems)
                .append("newsTimestamp", newsTimestamp)
                .append("newsItems", newsItems)
                .append("drmType", drmType)
                .append("drmValue", drmValue)
                .append("phoneNumber", phoneNumber)
                .append("operator", operator)
                .append("paymentStatus", paymentStatus)
                .append("paymentType", paymentType)
                .append("paymentEnabled", paymentEnabled)
                .append("rememberMeToken", rememberMeToken)
                .append("userName", userName)
                .append("userToken", userToken)
                .append("timeOfMovingToLimitedStatusSeconds", timeOfMovingToLimitedStatusSeconds)
                .append("promotionLabel", promotionLabel)
                .append("fullyRegistred", fullyRegistred)
                .append("oAuthProvider", oAuthProvider)
                .append("promotedDevice", promotedDevice)
                .append("promotedWeeks", promotedWeeks)
                .append("hasPotentialPromoCodePromotion", hasPotentialPromoCodePromotion)
                .append("hasOffers", hasOffers)
                .append("isFreeTrial", freeTrial)
                .append("lastPaymentStatus", lastPaymentStatus)
                .append("nextSubPaymentSeconds", nextSubPaymentSeconds)
                .append("activation", activation)
                .append("appStoreProductId", appStoreProductId)
                .append("provider", provider)
                .append("contract", contract)
                .append("segment", segment)
                .append("tariff", tariff)
                .append("graceCreditSeconds", graceCreditSeconds)
                .append("canGetVideo", canGetVideo)
                .append("canPlayVideo", canPlayVideo)
                .append("hasAllDetails", hasAllDetails)
                .append("showFreeTrial", showFreeTrial)
                .append("canActivateVideoTrial", canActivateVideoTrial)
                .append("eligibleForVideo", eligibleForVideo)
                .append("lastSubscribedPaymentSystem", lastSubscribedPaymentSystem)
                .append("subscriptionChanged", subscriptionChanged)
                .append("subjectToAutoOptIn", subjectToAutoOptIn)
                .toString();
    }
}
