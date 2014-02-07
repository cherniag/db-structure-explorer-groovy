package mobi.nowtechnologies.server.persistence.domain;

import com.google.common.base.Objects;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.dao.UserStatusDao;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.shared.dto.web.AccountDto;
import mobi.nowtechnologies.server.shared.dto.web.ContactUsDto;
import mobi.nowtechnologies.server.shared.enums.*;
import mobi.nowtechnologies.server.shared.enums.PaymentType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static mobi.nowtechnologies.server.persistence.domain.Community.O2_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.Community.VF_NZ_COMMUNITY_REWRITE_URL;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.ITUNES_SUBSCRIPTION;
import static mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails.O2_PSMS_TYPE;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;
import static mobi.nowtechnologies.server.shared.Utils.*;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYG;
import static mobi.nowtechnologies.server.shared.enums.Contract.PAYM;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.O2;
import static mobi.nowtechnologies.server.shared.enums.ProviderType.VF;
import static mobi.nowtechnologies.server.shared.enums.SegmentType.CONSUMER;
import static mobi.nowtechnologies.server.shared.enums.SubscriptionDirection.DOWNGRADE;
import static mobi.nowtechnologies.server.shared.enums.SubscriptionDirection.UPGRADE;
import static mobi.nowtechnologies.server.shared.enums.Tariff._3G;
import static mobi.nowtechnologies.server.shared.enums.Tariff._4G;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Entity
@Table(name = "tb_users", uniqueConstraints = {@UniqueConstraint(columnNames = {"deviceUID", "userGroup"}), @UniqueConstraint(columnNames = {"userName", "userGroup"})})
@NamedQueries({
        @NamedQuery(name = User.NQ_GET_USER_COUNT_BY_DEVICE_UID_GROUP_STOREDTOKEN, query = "select count(user) from User user where user.deviceUID=? and user.userGroupId=? and token=?"),
        @NamedQuery(name = User.NQ_GET_USER_BY_EMAIL_COMMUNITY_URL, query = "select u from User u where u.userName = ?1 and u.userGroupId=(select userGroup.id from UserGroup userGroup where userGroup.communityId=(select community.id from Community community where community.rewriteUrlParameter=?2))"),
        @NamedQuery(name = User.NQ_FIND_USER_BY_ID, query = "select u from User u where u.id = ?1")
})
public class User implements Serializable {
    private static final long serialVersionUID = 4414398062970887453L;

    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    public static final String NQ_GET_USERS_FOR_RETRY_PAYMENT = "getUsersForRetryPayment";
    public static final String NQ_GET_USER_BY_EMAIL_COMMUNITY_URL = "getUserByEmailAndCommunityURL";
    public static final String NQ_GET_USER_COUNT_BY_DEVICE_UID_GROUP_STOREDTOKEN = "getUserCountByDeviceUID_UserGroup_StoredToken";
    public static final String NQ_FIND_USER_BY_ID = "findUserById";

    public static final String NONE = "NONE";

    public static enum Fields {
        userName, mobile, operator, id, paymentStatus, paymentType, facebookId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "last_promo", columnDefinition = "INT default NULL")
    private PromoCode lastPromo;

    @Column(name = "contract_channel")
    @Enumerated(EnumType.STRING)
    private ContractChannel contractChannel;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff", columnDefinition = "char(255)", nullable = false)
    private Tariff tariff;

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
    private Integer userGroupId;

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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)")
    private ProviderType provider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private Contract contract;

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

    @Column(name = "potentialPromotion_i", insertable = false, updatable = false)
    private Integer potentialPromotionId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "potentialPromoCodePromotion_i", nullable = true)
    private Promotion potentialPromoCodePromotion;

    @Column(name = "potentialPromoCodePromotion_i", insertable = false, updatable = false)
    private Integer potentialPromoCodePromotionId;

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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private FBDetails fbDetails;

    private String facebookId;

    @Column(nullable = true)
    private String deviceUID;

    private Long firstDeviceLoginMillis;

    private Long firstUserLoginMillis;

    private long lastSuccesfullPaymentSmsSendingTimestampMillis;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amountOfMoneyToUserNotification;

    @Column(nullable = true)
    private Long freeTrialStartedTimestampMillis;

    @Lob
    @Column(name = "base64_encoded_app_store_receipt")
    private String base64EncodedAppStoreReceipt;

    @Column(name = "app_store_original_transaction_id")
    private String appStoreOriginalTransactionId;

    @Column(name = "last_subscribed_payment_system")
    private String lastSubscribedPaymentSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "segment", columnDefinition = "char(255)")
    private SegmentType segment;

    @Column(name = "last_before48_sms_millis", columnDefinition = "BIGINT default 0")
    private long lastBefore48SmsMillis;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_charts",
            joinColumns =
            @JoinColumn(name = "user_id", referencedColumnName = "i"),
            inverseJoinColumns =
            @JoinColumn(name = "chart_id", referencedColumnName = "i")
    )
    private List<Chart> selectedCharts = new ArrayList<Chart>();

    @Column(name = "video_free_trial_has_been_activated", columnDefinition = "boolean default false")
    private boolean videoFreeTrialHasBeenActivated;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "last_successful_payment_details_id", nullable = true)
    private PaymentDetails lastSuccessfulPaymentDetails;

    @Column(name = "idfa", nullable = true)
    private String idfa;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<UserIPhoneDetails> userIPhoneDetailsList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userId", cascade = CascadeType.REMOVE)
    private List<UserAndroidDetails> userAndroidDetailsList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserLog> userLogs;

    @Transient
    private User oldUser;

    @Transient
    private boolean isAutoOptInEnabled = true;

    @Transient
    private boolean hasPromo = false;

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
        setTariff(_3G);
    }

    public boolean isHasPromo() {
        return hasPromo;
    }

    public void setHasPromo(boolean hasPromo) {
        this.hasPromo = hasPromo;
    }

    public boolean isO24GConsumer() {
        return isO2Consumer() && is4G();
    }

    public boolean isO23GConsumer() {
        return isO2Consumer() && is3G();
    }

    public ContractChannel getContractChannel() {
        return contractChannel;
    }

    public void setContractChannel(ContractChannel contractChannel) {
        this.contractChannel = contractChannel;
    }

    public PromoCode getLastPromo() {
        return lastPromo;
    }

    public void setLastPromo(PromoCode lastPromo) {
        this.lastPromo = lastPromo;
    }

    private boolean isLastPromoForVideoAndAudio() {
        return isNotNull(lastPromo) && lastPromo.forVideoAndAudio();
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public boolean isIOsNonO2ITunesSubscribedUser() {
        return isIOSDevice() && isNonO2User() && isSubscribedByITunes() && isSubscribedStatus();
    }

    public boolean isSubscribedByITunes() {
        return ITUNES_SUBSCRIPTION.equals(lastSubscribedPaymentSystem);
    }

    public boolean isIOSDevice() {
        return DeviceTypeDao.getIOSDeviceType().equals(deviceType);
    }

    public boolean isInvalidPaymentPolicy() {
        return isNull(currentPaymentDetails) || isPaymentPolicyInvalidByProvider()
                || isPaymentPolicyInvalidBySegment();
    }

    private boolean isPaymentPolicyInvalidBySegment() {
        return O2.equals(getProvider()) && segment != currentPaymentDetails.getPaymentPolicy().getSegment();
    }

    private boolean isPaymentPolicyInvalidByProvider() {
        ProviderType paymentPolicyProvider = currentPaymentDetails.getPaymentPolicy().getProvider();
        ProviderType userProvider = getProvider();

        return isNotNull(paymentPolicyProvider) && !paymentPolicyProvider.equals(userProvider);
    }

    public boolean isNonO2User() {
        return !O2.equals(getProvider());
    }

    public boolean isNonVFUser() {
        return !ProviderType.VF.equals(this.provider);
    }

    public boolean isO2CommunityUser() {
        Community community = userGroup.getCommunity();
        String rewriteUrlParameter = community.getRewriteUrlParameter();
        return O2_COMMUNITY_REWRITE_URL.equalsIgnoreCase(rewriteUrlParameter);
    }

    public boolean isSMSActivatedUser() {
        return getActivationStatus() == ActivationStatus.ACTIVATED || StringUtils.equals(getMobile(), getUserName()) || isVFNZCommunityUser() || isO2CommunityUser();
    }

    public boolean isMonthlyPaidUser() {
        return (isO2CommunityUser() && isNonO2User()) || (isVFNZCommunityUser() && isNonVFUser());
    }

    public boolean isO2PAYGConsumer() {
        return isO2Consumer() && PAYG.equals(contract);
    }

    public boolean isO2User() {
        Community community = this.getUserGroup().getCommunity();
        return O2.equals(this.provider) && O2_COMMUNITY_REWRITE_URL.equals(community.getRewriteUrlParameter());
    }

    public boolean isVFNZUser() {
        return VF.equals(this.provider) && isVFNZCommunityUser();
    }

    public boolean isVFNZCommunityUser() {
        Community community = this.getUserGroup().getCommunity();
        return VF_NZ_COMMUNITY_REWRITE_URL.equals(community.getRewriteUrlParameter());
    }

    public boolean isO2Consumer() {
        return isO2User() && CONSUMER.equals(segment);
    }

    public boolean isO2Business() {
        return isO2User() && !isO2Consumer();
    }

    public boolean isTempUserName(){
        return getUserName().equals(getDeviceUID());
    }

    public boolean isActivatedUserName(){
        return getUserName().equals(getMobile());
    }
    public String getCommunityRewriteUrl(){
        Community community = getUserGroup() != null ? getUserGroup().getCommunity() : null;
        String communityRewriteUrl = community != null ? community.getRewriteUrlParameter() : null;
        return communityRewriteUrl;
    }

	public List<Chart> getSelectedCharts() {
		return selectedCharts;
	}

    public boolean hasPhoneNumber(){
        return !StringUtils.isEmpty(getMobile());
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

    public void setSelectedCharts(List<Chart> selectedCharts) {
        this.selectedCharts = selectedCharts;
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

    public Date getNextSubPaymentAsDate() {
        return new Date((long) this.nextSubPayment * 1000L);
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
            userGroupId = userGroup.getId();
    }

    public Integer getUserGroupId() {
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
        if (currentPaymentDetails != null)
            currentPaymentDetailsId = currentPaymentDetails.getI();
    }

    public Long getCurrentPaymentDetailsId() {
        return currentPaymentDetailsId;
    }

    public String getDeviceTypeIdString() {
        return deviceType.getName();
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
        return paymentType != null ? paymentType.toString() : null;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = PaymentType.valueOfByType(paymentType);
    }

    public User withFreeTrialExpiredMillis(Long freeTrialExpiredMillis) {
        this.freeTrialExpiredMillis = freeTrialExpiredMillis;
        return this;
    }

    public User withNextSubPayment(int time) {
        this.nextSubPayment = time;
        return this;
    }

    public User withTariff(Tariff tariff) {
        setTariff(tariff);
        return this;
    }

    public User withLastPromo(PromoCode lastPromo) {
        setLastPromo(lastPromo);
        return this;
    }

    public User withLastSuccessfulPaymentDetails(PaymentDetails lastSuccessfulPaymentDetails) {
        setLastSuccessfulPaymentDetails(lastSuccessfulPaymentDetails);
        return this;
    }

    public User withCurrentPaymentDetails(PaymentDetails currentPaymentDetails) {
        setCurrentPaymentDetails(currentPaymentDetails);
        return this;
    }

    public User withContract(Contract contract) {
        setContract(contract);
        return this;
    }

    public User withUserGroup(UserGroup userGroup) {
        setUserGroup(userGroup);
        return this;
    }

    public User withProvider(ProviderType provider) {
        setProvider(provider);
        return this;
    }

    public User withSegment(SegmentType segment) {
        setSegment(segment);
        return this;
    }

    public User withUserName(String userName) {
        setUserName(userName);
        return this;
    }

    public User withContractChannel(ContractChannel contractChannel) {
        setContractChannel(contractChannel);
        return this;
    }

    public User withDeviceUID(String deviceUID) {
        setDeviceUID(deviceUID);
        return this;
    }

    public User withMobile(String mobile) {
        setMobile(mobile);
        return this;
    }

    public User withPin(String pin) {
        setPin(pin);
        return this;
    }

    public User withActivationStatus(ActivationStatus activationStatus) {
        setActivationStatus(activationStatus);
        return this;
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

    public Integer getPotentialPromotionId() {
        return potentialPromotionId;
    }

    public void setPotentialPromotion(Promotion potentialPromotion) {
        this.potentialPromotion = potentialPromotion;
        if (potentialPromotion != null)
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

    public AccountDto toAccountDto() {
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

        accountDto.setTimeOfMovingToLimitedStatus(getTimeOfMovingToLimitedStatus(nextSubPayment, subBalance) * 1000L);
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

    public void setFreeTrialExpired(Date freeTrialExpiredMillis) {
        this.freeTrialExpiredMillis = freeTrialExpiredMillis.getTime();
    }

    public User withFreeTrialExpiredMillis(Date freeTrialExpiredMillis) {
        this.freeTrialExpiredMillis = freeTrialExpiredMillis.getTime();
        return this;
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

    public PaymentDetailsStatus getLastPaymentStatus() {
        return currentPaymentDetails != null ? currentPaymentDetails.getLastPaymentStatus() : null;
    }

    public void setLastSubscribedPaymentSystem(String lastSubscribedPaymentSystem) {
        this.lastSubscribedPaymentSystem = lastSubscribedPaymentSystem;
    }

    public long getLastBefore48SmsMillis() {
        return lastBefore48SmsMillis;
    }

    public void setLastBefore48SmsMillis(long lastBefore48SmsMillis) {
        this.lastBefore48SmsMillis = lastBefore48SmsMillis;
    }

    public List<UserLog> getUserLogs() {
        return userLogs;
    }

    public void setUserLogs(List<UserLog> userLogs) {
        this.userLogs = userLogs;
    }

    private Integer getLastPromoId() {
        if (isNotNull(lastPromo)) return lastPromo.getId();
        return null;
    }

    public boolean isOnFreeTrial() {
        return freeTrialExpiredMillis != null && freeTrialExpiredMillis > getEpochMillis();
    }

    public boolean wasSubscribed() {
        return !isEmpty(getLastSubscribedPaymentSystem())
                || getCurrentPaymentDetails() != null;
    }

    public boolean isLimited() {
        return this.status != null && UserStatus.LIMITED.equals(this.status.getName())
                || (new DateTime(getNextSubPaymentAsDate()).isBeforeNow()
                && getLastSubscribedPaymentSystem() != null
                && !hasActivePaymentDetails());
    }

    public boolean isSubscribedStatus() {
        return this.status != null && UserStatus.SUBSCRIBED.equals(this.status.getName());
    }

    public boolean isNonO2Community() {
        Community community = this.userGroup.getCommunity();
        String communityUrl = checkNotNull(community.getRewriteUrlParameter());

        if (!O2_COMMUNITY_REWRITE_URL.equalsIgnoreCase(communityUrl))
            return true;

        return false;
    }

    public boolean isNotActivePaymentDetails() {
        PaymentDetails currentPaymentDetails = getCurrentPaymentDetails();
        return currentPaymentDetails != null && !currentPaymentDetails.isActivated();
    }

    public boolean hasActivePaymentDetails() {
        PaymentDetails currentPaymentDetails = getCurrentPaymentDetails();
        return currentPaymentDetails != null && currentPaymentDetails.isActivated();
    }

    public boolean hasPendingPayment() {
        PaymentDetails currentPaymentDetails = getCurrentPaymentDetails();
        return currentPaymentDetails != null && PaymentDetailsStatus.AWAITING == currentPaymentDetails.getLastPaymentStatus();
    }

    public boolean isBeforeExpiration(long timestamp, int hours) {
        return nextSubPayment <= timestamp / 1000 + hours * 60 * 60;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType providerType) {
        provider = providerType;
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

    public User withNextSubPayment(Date time) {
        this.nextSubPayment = truncatedToSeconds(time);
        return this;
    }

    public User withVideoFreeTrialHasBeenActivated(boolean videoFreeTrialHasBeenActivated) {
        setVideoFreeTrialHasBeenActivated(videoFreeTrialHasBeenActivated);
        return this;
    }

    public User withContractChanel(ContractChannel contractChanel) {
        setContractChannel(contractChanel);
        return this;
    }

    public Boolean isSelectedChart(ChartDetail chartDetail) {
        Chart sameTypeChart = null;
        if (getSelectedCharts() != null && getSelectedCharts().size() > 0) {
            for (Chart chart : getSelectedCharts()) {
                if (chart.getI().equals(chartDetail.getChart().getI()))
                    return true;
                else if (chart.getType() == chartDetail.getChart().getType())
                    sameTypeChart = chart;

            }
        }

        return sameTypeChart == null && chartDetail.getDefaultChart() != null ? chartDetail.getDefaultChart() : false;
    }

    public boolean isExpiring() {
        return isSubscribedStatus() && new DateTime(getNextSubPaymentAsDate()).isAfterNow()
                && !hasActivePaymentDetails() && getLastPaymentStatus() != PaymentDetailsStatus.ERROR && wasSubscribed();
    }

    public boolean has4GVideoAudioSubscription() {
        return is4GVideoAudioPaymentDetails(currentPaymentDetails);
    }

    public boolean isOn4GVideoAudioBoughtPeriod() {
        return isOnBoughtPeriod() && is4GVideoAudioPaymentDetails(lastSuccessfulPaymentDetails);
    }

    public boolean isOnAudioBoughtPeriod() {
        return isOnBoughtPeriod() && isAudioPaymentDetails(lastSuccessfulPaymentDetails);
    }

    public boolean isOnBoughtPeriod() {
        return isNextSubPaymentInTheFuture() && (isNotNull(lastSuccessfulPaymentDetails) || isSubscribedByITunes());
    }

    public boolean isNextSubPaymentInTheFuture() {
        return nextSubPayment > getEpochSeconds();
    }

    private boolean is4GVideoAudioPaymentDetails(PaymentDetails paymentDetails) {
        if (paymentDetails != null) {
            PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();
            return paymentPolicy.is4GVideoAudioSubscription();
        }
        return false;
    }

    private boolean isAudioPaymentDetails(PaymentDetails paymentDetails) {
        if (paymentDetails != null) {
            PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();
            return paymentPolicy.isAudioSubscription();
        }
        return false;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariffType) {
        this.tariff = tariffType;
    }

    public boolean is4G() {
        return _4G.equals(tariff);
    }

    public boolean is3G() {
        return _3G.equals(tariff);
    }

    public boolean isVideoFreeTrialHasBeenActivated() {
        return videoFreeTrialHasBeenActivated;
    }

    public boolean isO2PAYMConsumer() {
        return isO2Consumer() && PAYM.equals(contract);
    }

    public boolean hasAllDetails() {
        if (ProviderType.O2.equals(provider) || ProviderType.NON_O2.equals(provider))
            return this.contract != null && this.contractChannel != null && this.segment != null && this.tariff != null;
        else
            return this.provider != null;
    }

    public void setVideoFreeTrialHasBeenActivated(boolean videoFreeTrialHasBeenActivated) {
        this.videoFreeTrialHasBeenActivated = videoFreeTrialHasBeenActivated;
    }

    public PaymentDetails getLastSuccessfulPaymentDetails() {
        return lastSuccessfulPaymentDetails;
    }

    public void setLastSuccessfulPaymentDetails(PaymentDetails lastSuccessfulPaymentDetails) {
        this.lastSuccessfulPaymentDetails = lastSuccessfulPaymentDetails;
    }

    public boolean isSubjectToAutoOptIn() {
        return isAutoOptInEnabled && isNull(oldUser) && ((isO24GConsumer() && !isLastPromoForVideoAndAudio()) || (isO23GConsumer() && !isLastPromoForAudio()));
    }

    private boolean isLastPromoForAudio() {
        return isNotNull(lastPromo) && lastPromo.forAudio();
    }

    public SubscriptionDirection getSubscriptionDirection() {

        PaymentDetails currentDetails = getCurrentPaymentDetails();
        PaymentDetails successfulDetails = getLastSuccessfulPaymentDetails();
        if (isNull(currentDetails) || isNull(successfulDetails)) return null;

        String currentPaymentDetailsPaymentType = currentPaymentDetails.getPaymentType();

        String successfulDetailsPaymentType = successfulDetails.getPaymentType();
        if (!(currentPaymentDetailsPaymentType.equals(O2_PSMS_TYPE) && successfulDetailsPaymentType.equals(O2_PSMS_TYPE)))
            return null;

        PaymentPolicy currentPolicy = currentDetails.getPaymentPolicy();
        PaymentPolicy successPolicy = successfulDetails.getPaymentPolicy();
        if (isNull(currentPolicy) || isNull(successPolicy)) return null;

        boolean activeCPD = currentDetails.isActivated();

        MediaType lastSuccessMediaType = successPolicy.getMediaType();
        MediaType currentMediaType = currentPolicy.getMediaType();
        if (activeCPD
                && currentMediaType == VIDEO_AND_AUDIO
                && lastSuccessMediaType == AUDIO
                && !isOnVideoAudioFreeTrial()
                && is4G()) return UPGRADE;
        if (activeCPD
                && currentMediaType == AUDIO
                && lastSuccessMediaType == VIDEO_AND_AUDIO
                && !isOnVideoAudioFreeTrial()
                && is4G()) return DOWNGRADE;
        return null;
    }

    public boolean isOnVideoAudioFreeTrial() {
        return isLastPromoForVideoAndAudio() && isOnFreeTrial();
    }

    public boolean canPlayVideo() {
        return isOnVideoAudioFreeTrial() || isOn4GVideoAudioBoughtPeriod();
    }

    public boolean isEligibleForVideo() {
        return isO24GConsumer() || isOnWhiteListedVideoAudioFreeTrial();
    }

    public boolean isOnWhiteListedVideoAudioFreeTrial() {
        return isOnVideoAudioFreeTrial() && isWhiteListedLastPromo();
    }

    private boolean isWhiteListedLastPromo() {
        return isNotNull(lastPromo) && lastPromo.isWhiteListed();
    }

    public PaymentDetails getPaymentDetails(Class<?> clazz) {
        if (paymentDetailsList != null && clazz != null) {
            for (PaymentDetails paymentDetails : paymentDetailsList) {
                if (paymentDetails.getPaymentType().getClass() == clazz) {
                    return paymentDetails;
                }
            }
        }

        return null;
    }

    public User withOldUser(User oldUser) {
        this.oldUser = oldUser;
        return this;
    }

    public User getOldUser() {
        return oldUser;
    }

    private Integer getOldUserId() {
        if (isNull(oldUser)) return null;
        return oldUser.getId();
    }

    public User withAutoOptInEnabled(boolean isAutoOptInEnabled) {
        this.isAutoOptInEnabled = isAutoOptInEnabled;
        return this;
    }

    public User withDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public User withDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
        return this;
    }

    public User withIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }


    public FBDetails getFbDetails() {
        return fbDetails;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("old_user_id", getOldUserId())
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
                .add("lastBefore48SmsMillis", lastBefore48SmsMillis)
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
                .add("freeTrialExpiredMillis", freeTrialExpiredMillis)
                .add("activationStatus", activationStatus)
                .add("segment", segment)
                .add("provider", provider)
                .add("tariff", tariff)
                .add("contractChannel", contractChannel)
                .add("lastPromoId", getLastPromoId())
                .add("contract", contract)
                .add("hasPromo", hasPromo)
                .add("isAutoOptInEnabled", isAutoOptInEnabled).toString();
    }
}