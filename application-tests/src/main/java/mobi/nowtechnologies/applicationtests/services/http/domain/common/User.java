package mobi.nowtechnologies.applicationtests.services.http.domain.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import mobi.nowtechnologies.applicationtests.services.http.domain.common.UserDetails;

/**
 * @author kots
 * @since 8/20/2014.
 */
public class User {

    private String displayName;
    private Integer subBalance;
    private String status;
    private String deviceType;
    private String deviceUID;
    private Integer chartTimestamp;
    private Integer chartItems;
    private Integer newsTimestamp;
    private Integer newsItems;
    private String drmType;
    private Integer drmValue;
    private String phoneNumber;
    private Integer operator;
    private String paymentStatus;
    private String paymentType;
    private Boolean paymentEnabled;
    private String rememberMeToken;
    private String userName;
    private String userToken;
    private Integer timeOfMovingToLimitedStatusSeconds;
    private Boolean fullyRegistred;
    private String oAuthProvider;
    private Boolean promotedDevice;
    private Integer promotedWeeks;
    private Boolean hasPotentialPromoCodePromotion;
    private Boolean hasOffers;
    private Boolean freeTrial;
    private Integer nextSubPaymentSeconds;
    private String activation;
    private String provider;
    private String tariff;
    private Integer graceCreditSeconds;
    private Boolean canGetVideo;
    private Boolean canPlayVideo;
    private Boolean hasAllDetails;
    private Boolean showFreeTrial;
    private Boolean canActivateVideoTrial;
    private Boolean eligibleForVideo;
    private Boolean subjectToAutoOptIn;
    private UserDetails userDetails;
    private String firstActivation;
    private String appStoreProductId;

    public String getAppStoreProductId() {
        return appStoreProductId;
    }

    public void setAppStoreProductId(String appStoreProductId) {
        this.appStoreProductId = appStoreProductId;
    }

    public String getFirstActivation() {
        return firstActivation;
    }

    public void setFirstActivation(String firstActivation) {
        this.firstActivation = firstActivation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getSubBalance() {
        return subBalance;
    }

    public void setSubBalance(Integer subBalance) {
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

    public Integer getChartTimestamp() {
        return chartTimestamp;
    }

    public void setChartTimestamp(Integer chartTimestamp) {
        this.chartTimestamp = chartTimestamp;
    }

    public Integer getChartItems() {
        return chartItems;
    }

    public void setChartItems(Integer chartItems) {
        this.chartItems = chartItems;
    }

    public Integer getNewsTimestamp() {
        return newsTimestamp;
    }

    public void setNewsTimestamp(Integer newsTimestamp) {
        this.newsTimestamp = newsTimestamp;
    }

    public Integer getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(Integer newsItems) {
        this.newsItems = newsItems;
    }

    public String getDrmType() {
        return drmType;
    }

    public void setDrmType(String drmType) {
        this.drmType = drmType;
    }

    public Integer getDrmValue() {
        return drmValue;
    }

    public void setDrmValue(Integer drmValue) {
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

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Boolean getPaymentEnabled() {
        return paymentEnabled;
    }

    public void setPaymentEnabled(Boolean paymentEnabled) {
        this.paymentEnabled = paymentEnabled;
    }

    public String getRememberMeToken() {
        return rememberMeToken;
    }

    public void setRememberMeToken(String rememberMeToken) {
        this.rememberMeToken = rememberMeToken;
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

    public Integer getTimeOfMovingToLimitedStatusSeconds() {
        return timeOfMovingToLimitedStatusSeconds;
    }

    public void setTimeOfMovingToLimitedStatusSeconds(Integer timeOfMovingToLimitedStatusSeconds) {
        this.timeOfMovingToLimitedStatusSeconds = timeOfMovingToLimitedStatusSeconds;
    }

    public Boolean getFullyRegistred() {
        return fullyRegistred;
    }

    public void setFullyRegistred(Boolean fullyRegistred) {
        this.fullyRegistred = fullyRegistred;
    }

    @JsonProperty("oAuthProvider")
    public String getOAuthProvider() {
        return oAuthProvider;
    }

    @JsonProperty("oAuthProvider")
    public void setOAuthProvider(String oAuthProvider) {
        this.oAuthProvider = oAuthProvider;
    }

    public Boolean getPromotedDevice() {
        return promotedDevice;
    }

    public void setPromotedDevice(Boolean promotedDevice) {
        this.promotedDevice = promotedDevice;
    }

    public Integer getPromotedWeeks() {
        return promotedWeeks;
    }

    public void setPromotedWeeks(Integer promotedWeeks) {
        this.promotedWeeks = promotedWeeks;
    }

    public Boolean getHasPotentialPromoCodePromotion() {
        return hasPotentialPromoCodePromotion;
    }

    public void setHasPotentialPromoCodePromotion(Boolean hasPotentialPromoCodePromotion) {
        this.hasPotentialPromoCodePromotion = hasPotentialPromoCodePromotion;
    }

    public Boolean getHasOffers() {
        return hasOffers;
    }

    public void setHasOffers(Boolean hasOffers) {
        this.hasOffers = hasOffers;
    }

    public Boolean getFreeTrial() {
        return freeTrial;
    }

    public void setFreeTrial(Boolean freeTrial) {
        this.freeTrial = freeTrial;
    }

    public Integer getNextSubPaymentSeconds() {
        return nextSubPaymentSeconds;
    }

    public void setNextSubPaymentSeconds(Integer nextSubPaymentSeconds) {
        this.nextSubPaymentSeconds = nextSubPaymentSeconds;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public Integer getGraceCreditSeconds() {
        return graceCreditSeconds;
    }

    public void setGraceCreditSeconds(Integer graceCreditSeconds) {
        this.graceCreditSeconds = graceCreditSeconds;
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

    public Boolean getEligibleForVideo() {
        return eligibleForVideo;
    }

    public void setEligibleForVideo(Boolean eligibleForVideo) {
        this.eligibleForVideo = eligibleForVideo;
    }

    public Boolean getSubjectToAutoOptIn() {
        return subjectToAutoOptIn;
    }

    public void setSubjectToAutoOptIn(Boolean subjectToAutoOptIn) {
        this.subjectToAutoOptIn = subjectToAutoOptIn;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
