package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Objects;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.enums.*;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.OAuthProvider;
import mobi.nowtechnologies.server.shared.dto.web.AccountDto;
import mobi.nowtechnologies.server.shared.dto.web.ContactUsDto;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.enums.PaymentType;
import mobi.nowtechnologies.server.shared.util.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.BUSINESS;
import static mobi.nowtechnologies.server.persistence.domain.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.Utils.toStringIfNull;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Entity
@Table(name = "tb_users", uniqueConstraints = @UniqueConstraint(columnNames = { "deviceUID", "userGroup" }))
@NamedQueries({
        @NamedQuery(name = User.NQ_GET_USERS_FOR_RETRY_PAYMENT, query = "select u from User u join u.currentPaymentDetails as pd where (pd.lastPaymentStatus='ERROR' or pd.lastPaymentStatus='EXTERNAL_ERROR') and pd.madeRetries!=pd.retriesOnError and pd.activated=true and u.lastDeviceLogin!=0",
                hints = { @QueryHint(name = "org.hibernate.cacheMode", value = "IGNORE") }),
        @NamedQuery(name = User.NQ_GET_USER_COUNT_BY_DEVICE_UID_GROUP_STOREDTOKEN, query = "select count(user) from User user where user.deviceUID=? and user.userGroupId=? and token=?"),
        @NamedQuery(name = User.NQ_GET_USER_BY_DEVICE_UID_COMMUNITY_REDIRECT_URL, query = "select user from User user join user.userGroup userGroup join userGroup.community community where user.deviceUID=? and community.rewriteUrlParameter=?"),
        @NamedQuery(name = User.NQ_GET_USER_BY_EMAIL_COMMUNITY_URL, query = "select u from User u where u.userName = ?1 and u.userGroupId=(select userGroup.i from UserGroup userGroup where userGroup.communityId=(select community.id from Community community where community.rewriteUrlParameter=?2))"),
        @NamedQuery(name = User.NQ_FIND_USER_BY_ID, query = "select u from User u where u.id = ?1")
})
public class User implements Serializable {
    private static final long serialVersionUID = 4414398062970887453L;

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    public static final String NQ_GET_USERS_FOR_RETRY_PAYMENT = "getUsersForRetryPayment";
    public static final String NQ_GET_USER_BY_EMAIL_COMMUNITY_URL = "getUserByEmailAndCommunityURL";
    public static final String NQ_GET_USER_COUNT_BY_DEVICE_UID_GROUP_STOREDTOKEN = "getUserCountByDeviceUID_UserGroup_StoredToken";
    public static final String NQ_GET_USER_BY_DEVICE_UID_COMMUNITY_REDIRECT_URL = "getUserByDeviceUIDAndCommunityRedirectUrl";
    public static final String NQ_FIND_USER_BY_ID = "findUserById";

    public static final String NONE = "NONE";

    public static enum Fields {
        userName, mobile, operator, id, paymentStatus, paymentType, paymentEnabled, facebookId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i")
    private int id;

    @Column(name = "address1", columnDefinition = "char(50)")
    private String address1;

    @Column(name = "address2", columnDefinition = "char(50)")
    private String address2;

    private boolean canContact;

    @Column(name = "city", columnDefinition = "char(20)")
    private String city;

    @Column(name = "code", columnDefinition = "char(40)")
    private String code;

    @Column(name = "country", columnDefinition = "smallint(5) unsigned")
    private int country;

    @Column(name = "device", columnDefinition = "char(40)")
    private String device;

    // TODO: no longer need . This field should be deleted in next releases.
    @Column(name = "deviceString", columnDefinition = "char(100)")
    private String deviceString;

    @Column(name = "deviceType", insertable = false, updatable = false)
    private byte deviceTypeId;

    private String deviceModel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deviceType")
    private DeviceType deviceType;

    @Column(name = "displayName", columnDefinition = "char(25)")
    private String displayName;

    @Column(name = "firstName", columnDefinition = "char(40)")
    private String firstName;

    private Long freeTrialExpiredMillis;

    @Column(name = "ipAddress", columnDefinition = "char(40)")
    private String ipAddress;

    private int lastDeviceLogin;

    @Column(name = "lastName", columnDefinition = "char(40)")
    private String lastName;

    private int lastPaymentTx;

    private int lastWebLogin;

    @Column(name = "mobile", columnDefinition = "char(15)")
    private String mobile;

    private int nextSubPayment;

    @Column(name = "postcode", columnDefinition = "char(15)")
    private String postcode;

    @Column(name = "sessionID", columnDefinition = "char(40)")
    private String sessionID;

    @Column(name = "status", insertable = false, updatable = false)
    private byte userStatusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status")
    private UserStatus status;

    private int subBalance;

    @Column(name = "tempToken", columnDefinition = "char(40)")
    private String tempToken;

    @Column(name = "title", columnDefinition = "char(10)")
    private String title;

    @Column(name = "token", columnDefinition = "char(40)")
    private String token;

    @Column(name = "userGroup", insertable = false, updatable = false)
    private byte userGroupId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userGroup", nullable = false)
    private UserGroup userGroup;

    @Column(name = "userName", columnDefinition = "char(40)")
    private String userName;

    private UserType userType;

    private int operator;

    private String pin;

    @Enumerated(EnumType.STRING)
    private mobi.nowtechnologies.server.shared.enums.PaymentType paymentType;

    @Column(name = "activation_status")
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus;

    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private Contract contract;

    @Deprecated
    private boolean paymentEnabled;

    private int numPsmsRetries;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<PaymentDetails> paymentDetailsList;

    @OneToOne
    @JoinColumn(name = "currentPaymentDetailsId", nullable = true, insertable = false, updatable = true)
    private PaymentDetails currentPaymentDetails;

    @Column(name = "currentPaymentDetailsId", nullable = true, insertable = false, updatable = false)
    private Long currentPaymentDetailsId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "potentialPromotion_i", nullable = true)
    private Promotion potentialPromotion;

    @Column(name="potentialPromotion_i", insertable=false, updatable=false)
    private Byte potentialPromotionId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "potentialPromoCodePromotion_i", nullable = true)
    private Promotion potentialPromoCodePromotion;

    @Column(name = "potentialPromoCodePromotion_i", insertable = false, updatable = false)
    private Byte potentialPromoCodePromotionId;

    @Transient
    @XmlTransient
    private String newStoredToken;

    @Transient
    @XmlTransient
    private String conformStoredToken;

    private int paymentStatus;

    private long lastSuccessfulPaymentTimeMillis;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Drm> drms;

    private String facebookId;

    @Column(nullable = true)
    private String deviceUID;

    private Long firstDeviceLoginMillis;

    private Long firstUserLoginMillis;

    private long lastSuccesfullPaymentSmsSendingTimestampMillis;

	@Column(precision=12, scale=2, nullable=false)
    private BigDecimal amountOfMoneyToUserNotification;

    @Column(nullable=true)
    private Long freeTrialStartedTimestampMillis;

    @Lob
    @Column(name="base64_encoded_app_store_receipt")
    private String base64EncodedAppStoreReceipt;

    @Column(name="app_store_original_transaction_id")
    private String appStoreOriginalTransactionId;

    @Column(name="last_subscribed_payment_system")
    private String lastSubscribedPaymentSystem;

    @Column(name="last_payment_try_millis", columnDefinition="BIGINT default 0")
    private long lastPaymentTryMillis;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private SegmentType segment;

    @Column(name="deactivated_o2_psms_grace_credit_millis", columnDefinition="BIGINT default 0")
    private long deactivatedO2PSMSGraceCreditMillis;

    public User() {
        setDisplayName("");
        setTitle("");
        setFirstName("");
        setLastName("");
        setDeviceString("");
        setDevice("");
        setAddress1("");
        setAddress2("");
        setCity("");
        setPostcode("");
        setMobile("");
        setCode("");
        setSessionID("");
        setPin("");
        setTempToken("");
        setPaymentDetailsList(new ArrayList<PaymentDetails>());
        setUserType(UserType.UNDEFINED);
        setAmountOfMoneyToUserNotification(BigDecimal.ZERO);
    }

    public boolean isIOsNonO2ItunesSubscribedUser(){
        return isIOSDevice() && isNonO2User() && isSubscribedByITunes() && isSubscribed();
    }

    public boolean isSubscribedByITunes() {
        return isNotEmpty(lastSubscribedPaymentSystem) && lastSubscribedPaymentSystem.equals(PaymentDetails.ITUNES_SUBSCRIPTION);
    }

    public boolean isIOSDevice(){
        return DeviceTypeDao.getIOSDeviceType().equals(deviceType);
    }

    public boolean isNotIOSDevice() {
        return !isIOSDevice();
    }

    public boolean hasActivePaymentDetails() {
        PaymentDetails details = this.getCurrentPaymentDetails();
        return details != null && details.isActivated();
    }

    public boolean isNonO2User() {
        Community community = this.userGroup.getCommunity();
        String communityUrl = checkNotNull(community.getRewriteUrlParameter());

        if ("o2".equalsIgnoreCase(communityUrl) && (!"o2".equals(this.provider)))
            return true;

        return false;
    }

    public boolean isO2PAYGConsumer() {
        return isO2Consumer() && Contract.PAYG.equals(contract);
    }

    public boolean isO2User() {
        Community community = this.getUserGroup().getCommunity();
        return "o2".equals(this.provider) && "o2".equals(community.getRewriteUrlParameter());
    }

    public boolean isO2Business() {
        return isO2User() && BUSINESS.equals(segment);
    }

    public boolean isO2Consumer() {
        return  isO2User() && CONSUMER.equals(segment);
    }

    public void addPaymentDetails(PaymentDetails paymentDetails) {
        if (null != paymentDetails) {
            this.paymentDetailsList.add(paymentDetails);
            if (paymentDetails.getOwner() != this)
                paymentDetails.setOwner(this);
        }
    }

    public PaymentDetails getPendingPaymentDetails() {
        for (PaymentDetails pd : paymentDetailsList) {
            if (PaymentDetailsStatus.PENDING.equals(pd.getLastPaymentStatus()))
                return pd;
        }
        return null;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress1() {
        return this.address1;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public boolean getCanContact() {
        return this.canContact;
    }

    public void setCanContact(boolean canContact) {
        this.canContact = canContact;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCountry() {
        return this.country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public String getDevice() {
        return this.device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceString() {
        return this.deviceString;
    }

    public void setDeviceString(String deviceString) {
        this.deviceString = deviceString;
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        if (deviceType != null)
            deviceTypeId = deviceType.getI();
    }

    public byte getDeviceTypeId() {
        return deviceTypeId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getLastDeviceLogin() {
        return this.lastDeviceLogin;
    }

    public String getNewStoredToken() {
        return newStoredToken;
    }

    public void setNewStoredToken(String newStoredToken) {
        this.newStoredToken = newStoredToken;
    }

    public void setLastDeviceLogin(int lastDeviceLogin) {
        this.lastDeviceLogin = lastDeviceLogin;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getLastPaymentTx() {
        return this.lastPaymentTx;
    }

    public void setLastPaymentTx(int lastPaymentTx) {
        this.lastPaymentTx = lastPaymentTx;
    }

    public int getLastWebLogin() {
        return this.lastWebLogin;
    }

    public void setLastWebLogin(int lastWebLogin) {
        this.lastWebLogin = lastWebLogin;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getNextSubPayment() {
        return this.nextSubPayment;
    }

    public void setNextSubPayment(int nextSubPayment) {
        this.nextSubPayment = nextSubPayment;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public byte getUserStatusId() {
        return this.userStatusId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus userStatus) {
        this.status = userStatus;
        if (userStatus != null)
            userStatusId = userStatus.getI();
    }

    public String getTempToken() {
        return this.tempToken;
    }

    public void setTempToken(String tempToken) {
        this.tempToken = tempToken;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
        if (userGroup != null)
            userGroupId = userGroup.getI();
    }

    public byte getUserGroupId() {
        return userGroupId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<PaymentDetails> getPaymentDetailsList() {
        return paymentDetailsList;
    }

    public void setPaymentDetailsList(List<PaymentDetails> paymentDetailsList) {
        this.paymentDetailsList = paymentDetailsList;
    }

    public PaymentDetails getCurrentPaymentDetails() {
        return this.currentPaymentDetails;
    }

    public void setCurrentPaymentDetails(PaymentDetails currentPaymentDetails) {
        this.currentPaymentDetails = currentPaymentDetails;
        if (currentPaymentDetails!=null)
            currentPaymentDetailsId = currentPaymentDetails.getI();
    }

    public Long getCurrentPaymentDetailsId() {
        return currentPaymentDetailsId;
    }

    public String getCountryIdString() {
        if (country == -1)
            return NONE;
        return String.valueOf(country);
    }

    public void setCountryIdString(String aCountryId) {
        if (aCountryId == null)
            throw new NullPointerException("The parameter aCountryId is null");
        if (NONE.equals(aCountryId))
            country = -1;
        else
            country = Integer.parseInt(aCountryId);
    }

    public String getDeviceTypeIdString() {
        return deviceType.getName();
    }

    public void setDeviceTypeIdString(String deviceTypeName) {
        if (deviceTypeName == null)
            throw new NullPointerException("The parameter deviceTypeName is null");
        if (DeviceTypeDao.NONE.equals(deviceTypeName))
            deviceType = DeviceTypeDao.getNoneDeviceType();
        else
            setDeviceType(DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue().get(deviceTypeName));
    }

    public String getConformStoredToken() {
        return conformStoredToken;
    }

    public void setConformStoredToken(String conformStoredToken) {
        this.conformStoredToken = conformStoredToken;
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
        return paymentType.toString();
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = PaymentType.valueOf(paymentType);
    }

    /*
     * @deprecated Unused column
     */
    @Deprecated
    public boolean isPaymentEnabled() {
        return paymentEnabled;
    }

    /*
     * @deprecated Unused column
     */
    @Deprecated
    public void setPaymentEnabled(boolean paymentEnabled) {
        this.paymentEnabled = paymentEnabled;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getNumPsmsRetries() {
        return numPsmsRetries;
    }

    public void setNumPsmsRetries(int numPsmsRetries) {
        this.numPsmsRetries = numPsmsRetries;
    }

    public long getLastSuccessfulPaymentTimeMillis() {
        return lastSuccessfulPaymentTimeMillis;
    }

    public void setLastSuccessfulPaymentTimeMillis(long lastSuccessfulPaymentTimeMillis) {
        this.lastSuccessfulPaymentTimeMillis = lastSuccessfulPaymentTimeMillis;
    }

    public void setDrms(List<Drm> drms) {
        this.drms = drms;
    }

    public List<Drm> getDrms() {
        return drms;
    }

    public int getSubBalance() {
        return subBalance;
    }

    public void setSubBalance(int subBalance) {
        this.subBalance = subBalance;
    }

    public Promotion getPotentialPromotion() {
        return potentialPromotion;
    }

    public Byte getPotentialPromotionId() {
        return potentialPromotionId;
    }

    public void setPotentialPromotion(Promotion potentialPromotion) {
        this.potentialPromotion = potentialPromotion;
        if(potentialPromotion!=null)
            potentialPromotionId = potentialPromotion.getI();
    }

    public void setDeviceUID(String deviceUID) {
        this.deviceUID = deviceUID;
    }

    public String getDeviceUID() {
        return deviceUID;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Promotion getPotentialPromoCodePromotion() {
        return potentialPromoCodePromotion;
    }

    public void setPotentialPromoCodePromotion(Promotion potentialPromoCodePromotion) {
        this.potentialPromoCodePromotion = potentialPromoCodePromotion;
        if (potentialPromoCodePromotion != null)
            potentialPromoCodePromotionId = potentialPromoCodePromotion.getI();
    }

    public AccountCheckDTO toAccountCheckDTO(String rememberMeToken, List<String> appStoreProductIds, int currentGraceDurationSeconds) {
        Chart chart = userGroup.getChart();
        News news = userGroup.getNews();
        DrmPolicy drmPolicy = userGroup.getDrmPolicy();

        PaymentDetails currentPaymentDetails = getCurrentPaymentDetails();
        boolean paymentEnabled = ((null != currentPaymentDetails && currentPaymentDetails.isActivated())||(lastSubscribedPaymentSystem != null && lastSubscribedPaymentSystem.equals(PaymentDetails.ITUNES_SUBSCRIPTION) && status != null
                && status.getName().equals(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED.name())));
        String oldPaymentType = getOldPaymentType(currentPaymentDetails);
        String oldPaymentStatus = getOldPaymentStatus(currentPaymentDetails);

        AccountCheckDTO accountCheckDTO = new AccountCheckDTO();
        accountCheckDTO.setChartTimestamp(chart.getTimestamp());
        accountCheckDTO.setChartItems(chart.getNumTracks());
        setNewsItemsAndTimestamp(news, accountCheckDTO);

        int graceDurationSeconds;
        if (UserStatus.LIMITED.equals(status.getName())) {
            graceDurationSeconds = getDeactivatedO2PSMSGraceCreditSeconds();
        } else {
            graceDurationSeconds = currentGraceDurationSeconds;
        }

        accountCheckDTO.setTimeOfMovingToLimitedStatusSeconds(Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance, graceDurationSeconds));
        if (null != getCurrentPaymentDetails())
            accountCheckDTO.setLastPaymentStatus(getCurrentPaymentDetails().getLastPaymentStatus());

        accountCheckDTO.setDrmType(drmPolicy.getDrmType().getName());
        accountCheckDTO.setDrmValue(drmPolicy.getDrmValue());
        accountCheckDTO.setStatus(status.getName());
        accountCheckDTO.setDisplayName(displayName);
        accountCheckDTO.setSubBalance((byte) subBalance);
        accountCheckDTO.setDeviceType(deviceType.getName());
        accountCheckDTO.setDeviceUID(deviceString);
        accountCheckDTO.setPaymentType(oldPaymentType);
        accountCheckDTO.setPaymentEnabled(paymentEnabled);
        accountCheckDTO.setPhoneNumber(mobile);
        accountCheckDTO.setOperator(operator);
        accountCheckDTO.setPaymentStatus(oldPaymentStatus);
        accountCheckDTO.setUserName(userName);
        accountCheckDTO.setUserToken(token);
        accountCheckDTO.setRememberMeToken(rememberMeToken);
        accountCheckDTO.setFreeTrial(isOnFreeTrial());
        accountCheckDTO.setProvider(provider);
        accountCheckDTO.setContract(toStringIfNull(contract));
        accountCheckDTO.setSegment(toStringIfNull(segment));
        accountCheckDTO.setLastSubscribedPaymentSystem(lastSubscribedPaymentSystem);

        accountCheckDTO.setFullyRegistred(EmailValidator.validate(userName));

        accountCheckDTO.setoAuthProvider((StringUtils.hasText(facebookId)) ? OAuthProvider.FACEBOOK : OAuthProvider.NONE);
        accountCheckDTO.setNextSubPaymentSeconds(getNextSubPayment());

        if (potentialPromotion != null)
            accountCheckDTO.setPromotionLabel(potentialPromotion.getLabel());
        accountCheckDTO.setHasPotentialPromoCodePromotion(potentialPromoCodePromotion != null);

        accountCheckDTO.setActivation(getActivationStatus());

        if(appStoreProductIds!=null){
            StringBuilder temp = new StringBuilder();
            for (String appStoreProductId : appStoreProductIds) {
                if (appStoreProductId != null) {
                    temp.append("," + appStoreProductId);
                }
            }
            if (temp.length() != 0)
                accountCheckDTO.setAppStoreProductId(temp.substring(1));
        }

        LOGGER.debug("Output parameter accountCheckDTO=[{}]", accountCheckDTO);
        return accountCheckDTO;
    }

    private void setNewsItemsAndTimestamp(News news, AccountCheckDTO accountCheckDTO) {
        if(news == null) return;
        accountCheckDTO.setNewsTimestamp(news.getTimestamp());
        accountCheckDTO.setNewsItems(news.getNumEntries());
    }

    // TODO Review this code after client refactoring
    protected String getOldPaymentType(PaymentDetails paymentDetails) {
        if (lastSubscribedPaymentSystem != null && lastSubscribedPaymentSystem.equals(PaymentDetails.ITUNES_SUBSCRIPTION) && status != null
                && status.getName().equals(mobi.nowtechnologies.server.shared.enums.UserStatus.SUBSCRIBED.name())) {
            return "ITUNES_SUBSCRIPTION";
        }else if (null == paymentDetails)
            return "UNKNOWN";
        if (PaymentDetails.SAGEPAY_CREDITCARD_TYPE.equals(paymentDetails.getPaymentType())) {
            return "creditCard";
        } else if (PaymentDetails.PAYPAL_TYPE.equals(paymentDetails.getPaymentType())) {
            return "PAY_PAL";
        } else if (PaymentDetails.MIG_SMS_TYPE.equals(paymentDetails.getPaymentType())) {
            return "PSMS";
        }else if (PaymentDetails.O2_PSMS_TYPE.equals(paymentDetails.getPaymentType())){
        	return "O2_PSMS";
        }
        return "UNKNOWN";
    }

    protected String getOldPaymentStatus(PaymentDetails paymentDetails) {
        if (null == paymentDetails)
            return PaymentStatus.NULL;
        if (PaymentDetails.SAGEPAY_CREDITCARD_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PAYMENT;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.OK;
                case NONE:
                    return PaymentStatus.NULL;
            }
        } else if (PaymentDetails.PAYPAL_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PAY_PAL;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.PAY_PAL_ERROR;
                case NONE:
                    return PaymentStatus.NULL;
            }
        } else if (PaymentDetails.MIG_SMS_TYPE.equals(paymentDetails.getPaymentType())) {
            switch (paymentDetails.getLastPaymentStatus()) {
                case AWAITING:
                    return PaymentStatus.AWAITING_PSMS;
                case SUCCESSFUL:
                    return PaymentStatus.OK;
                case ERROR:
                case EXTERNAL_ERROR:
                    return PaymentStatus.PSMS_ERROR;
            }
            if (paymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) && !paymentDetails.isActivated()) {
                return PaymentStatus.PIN_PENDING;
            } else if (paymentDetails.getLastPaymentStatus().equals(PaymentDetailsStatus.NONE) && paymentDetails.isActivated()) {
                return PaymentStatus.NULL;
            }
        }
        return null;
    }

    public AccountDto toAccountDto(int currentGraceDurationSeconds) {
        AccountDto accountDto = new AccountDto();
        accountDto.setEmail(userName);
        accountDto.setPhoneNumber(mobile);
        accountDto.setSubBalance(subBalance);

        PaymentDetails currentPaymentDetails = getCurrentPaymentDetails();

        AccountDto.Subscription subscription;
        if (status.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails == null)
            subscription = AccountDto.Subscription.freeTrialSubscription;
        else if (status.equals(UserStatusDao.getSubscribedUserStatus()) && currentPaymentDetails != null && currentPaymentDetails.isActivated())
            subscription = AccountDto.Subscription.subscribedSubscription;
        else if ((currentPaymentDetails != null && !currentPaymentDetails.isActivated()) || status.equals(UserStatusDao.getLimitedUserStatus()))
            subscription = AccountDto.Subscription.unsubscribedSubscription;
        else
            throw new PersistenceException("Couldn't recognize the user subscription");

        accountDto.setSubscription(subscription);
        int graceDurationSeconds;
        if (UserStatus.LIMITED.equals(status.getName())) {
            graceDurationSeconds = getDeactivatedO2PSMSGraceCreditSeconds();
        } else {
            graceDurationSeconds = currentGraceDurationSeconds;
        }

        accountDto.setTimeOfMovingToLimitedStatus(new Date(Utils.getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance, graceDurationSeconds) * 1000L));
        if (potentialPromotion != null)
            accountDto.setPotentialPromotion(String.valueOf(potentialPromotion.getI()));
        LOGGER.debug("Output parameter accountDto=[{}]", accountDto);
        return accountDto;
    }

    public ContactUsDto toContactUsDto() {
        ContactUsDto contactUsDto = new ContactUsDto();
        contactUsDto.setEmail(userName);
        contactUsDto.setName(displayName);

        LOGGER.debug("Output parameter contactUsDto=[{}]", contactUsDto);
        return contactUsDto;
    }

    public Long getFirstDeviceLoginMillis() {
        return firstDeviceLoginMillis;
    }

    public void setFirstDeviceLoginMillis(Long firstDeviceLoginMillis) {
        this.firstDeviceLoginMillis = firstDeviceLoginMillis;
    }

    public Long getFirstUserLoginMillis() {
        return firstUserLoginMillis;
    }

    public void setFirstUserLoginMillis(Long firstUserLoginMillis) {
        this.firstUserLoginMillis = firstUserLoginMillis;
    }

    public long getLastSuccesfullPaymentSmsSendingTimestampMillis() {
        return lastSuccesfullPaymentSmsSendingTimestampMillis;
    }

    public void setLastSuccesfullPaymentSmsSendingTimestampMillis(long lastSuccesfullPaymentSmsSendingTimestampMillis) {
        this.lastSuccesfullPaymentSmsSendingTimestampMillis = lastSuccesfullPaymentSmsSendingTimestampMillis;
    }

    public BigDecimal getAmountOfMoneyToUserNotification() {
        return amountOfMoneyToUserNotification;
    }

    public void setAmountOfMoneyToUserNotification(BigDecimal amountOfMoneyToUserNotification) {
        this.amountOfMoneyToUserNotification = amountOfMoneyToUserNotification;
    }

    public Long getFreeTrialExpiredMillis() {
        return freeTrialExpiredMillis;
    }

    public void setFreeTrialExpiredMillis(Long freeTrialExpiredMillis) {
        this.freeTrialExpiredMillis = freeTrialExpiredMillis;
    }

    public Long getFreeTrialStartedTimestampMillis() {
        return freeTrialStartedTimestampMillis;
    }

    public void setFreeTrialStartedTimestampMillis(Long freeTrialStartedTimestampMillis) {
        this.freeTrialStartedTimestampMillis = freeTrialStartedTimestampMillis;
    }

    public ActivationStatus getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(ActivationStatus activationStatus) {
        this.activationStatus = activationStatus;
    }

    public String getBase64EncodedAppStoreReceipt() {
        return base64EncodedAppStoreReceipt;
    }

    public void setBase64EncodedAppStoreReceipt(String base64EncodedAppStoreReceipt) {
        this.base64EncodedAppStoreReceipt = base64EncodedAppStoreReceipt;
    }

    public String getAppStoreOriginalTransactionId() {
        return appStoreOriginalTransactionId;
    }

    public void setAppStoreOriginalTransactionId(String appStoreOriginalTransactionId) {
        this.appStoreOriginalTransactionId = appStoreOriginalTransactionId;
    }

    public String getLastSubscribedPaymentSystem() {
        return lastSubscribedPaymentSystem;
    }

    public void setLastSubscribedPaymentSystem(String lastSubscribedPaymentSystem) {
        this.lastSubscribedPaymentSystem = lastSubscribedPaymentSystem;
    }

    public long getLastPaymentTryMillis() {
        return lastPaymentTryMillis;
    }

    public void setLastPaymentTryMillis(long lastPaymentTryMillis) {
        this.lastPaymentTryMillis = lastPaymentTryMillis;
    }

    public long getDeactivatedO2PSMSGraceCreditMillis() {
        return deactivatedO2PSMSGraceCreditMillis;
    }

    public void setDeactivatedO2PSMSGraceCreditMillis(long deactivatedO2PSMSGraceCreditMillis) {
        this.deactivatedO2PSMSGraceCreditMillis = deactivatedO2PSMSGraceCreditMillis;
    }
    
    public int getDeactivatedO2PSMSGraceCreditSeconds() {
        return (int)(deactivatedO2PSMSGraceCreditMillis/1000L);
    }

    public void setDeactivatedO2PSMSGraceCreditSeconds(int deactivatedO2PSMSGraceCreditSeconds) {
        this.deactivatedO2PSMSGraceCreditMillis = deactivatedO2PSMSGraceCreditSeconds*1000L;
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("userName", userName)
                .add("facebookId", facebookId)
                .add("deviceUID", deviceUID)
                .add("subBalance", subBalance)
                .add("userGroupId", userGroupId)
                .add("userStatusId", userStatusId)
                .add("nextSubPayment", nextSubPayment)
                .add("isFreeTrial", isOnFreeTrial())
                .add("currentPaymentDetailsId", currentPaymentDetailsId)
                .add("lastPaymentTx", lastPaymentTx)
                .add("token", token)
                .add("paymentStatus", paymentStatus)
                .add("paymentType", paymentType)
                .add("base64EncodedAppStoreReceipt", base64EncodedAppStoreReceipt)
                .add("appStoreOriginalTransactionId", appStoreOriginalTransactionId)
                .add("numPsmsRetries", numPsmsRetries)
                .add("lastSuccessfulPaymentTimeMillis", lastSuccessfulPaymentTimeMillis)
                .add("amountOfMoneyToUserNotification ", amountOfMoneyToUserNotification)
                .add("lastSubscribedPaymentSystem", lastSubscribedPaymentSystem)
                .add("lastSuccesfullPaymentSmsSendingTimestampMillis", lastSuccesfullPaymentSmsSendingTimestampMillis)
                .add("potentialPromoCodePromotionId", potentialPromoCodePromotionId)
                .add("potentialPromotionId", potentialPromotionId)
                .add("deactivatedO2PSMSGraceCreditMillis", deactivatedO2PSMSGraceCreditMillis)
                .add("pin", pin)
                .add("code", code)
                .add("operator", operator)
                .add("mobile", mobile)
                .add("conformStoredToken", conformStoredToken)
                .add("lastDeviceLogin", lastDeviceLogin)
                .add("lastWebLogin", lastWebLogin)
                .add("lastWebLogin", lastWebLogin)
                .add("firstUserLoginMillis", firstUserLoginMillis)
                .add("firstDeviceLoginMillis", firstDeviceLoginMillis)
                .add("device", device)
                .add("deviceModel", deviceModel)
                .add("deviceTypeId", deviceTypeId)
                .add("newStoredToken", newStoredToken)
                .add("tempToken", tempToken)
                .add("postcode", postcode)
                .add("address1", address1)
                .add("address2", country)
                .add("city", city)
                .add("title", title)
                .add("displayName ", displayName)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("ipAddress", ipAddress)
                .add("canContact", canContact)
                .add("deviceString", deviceString)
                .add("freeTrialStartedTimestampMillis", freeTrialStartedTimestampMillis)
                .add("activationStatus", activationStatus)
                .add("provider", provider)
                .add("contract", contract).toString();
    }

    /**
     * Returns true only if lastSuccessfulPaymentMillis == 0 and nextSubpaymentMillis > System.currentMillis
     * @return
     */
    public boolean isOnFreeTrial() {
        return (null!=this.freeTrialExpiredMillis && this.freeTrialExpiredMillis>System.currentTimeMillis());
    }

    public boolean isLimited() {
        return this.status != null && UserStatus.LIMITED.equals(this.status.getName());
    }

    public boolean isSubscribed(){
        return this.status != null && UserStatus.SUBSCRIBED.equals(this.status.getName());
    }

    public boolean isLimitedAfterOverdue() {
        return false;//TODO
    }

    public boolean isUnsubscribedWithFullAccess() {
        return false;//TODO
    }

    public boolean isOverdue() {
        return false;//TODO
    }

    public boolean isSubscribedViaInApp() {
        return false;//TODO
    }

    public boolean isTrialExpired() {
        return false;//TODO
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
    public void setSegment(SegmentType segment) {
        this.segment = segment;
    }

    public SegmentType getSegment() {
        return segment;
    }
}
