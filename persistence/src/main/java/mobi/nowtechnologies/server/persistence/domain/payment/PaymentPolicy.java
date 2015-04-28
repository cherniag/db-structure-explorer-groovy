package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.enums.PaymentPolicyType;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.Contract;
import mobi.nowtechnologies.server.shared.enums.MediaType;
import mobi.nowtechnologies.server.shared.enums.ProviderType;
import mobi.nowtechnologies.server.shared.enums.SegmentType;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "tb_paymentPolicy")
@Access(AccessType.FIELD)
public class PaymentPolicy {

    public static final List<String> PAYMENT_TYPES = Arrays.asList("creditCard", "iTunesSubscription", "o2Psms", "PAY_PAL", "PSMS", "vfPsms");

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    @Column(name = "i")
    private Integer id;
    @Column(name = "communityID", length = 10, nullable = false, insertable = false, updatable = false)
    private Integer communityId;
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "communityId")
    private Community community;
    @Column(name = "subCost", columnDefinition = "char(5)", length = 5, nullable = false)
    private BigDecimal subcost;
    @Embedded
    private Period period;
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "operator")
    private Operator operator;
    @Column(name = "operator", insertable = false, updatable = false)
    private Integer operatorId;
    private String paymentType;
    @Transient
    private String operatorName;
    private String shortCode;
    private String currencyISO;
    private boolean availableInStore;
    @Column(name = "app_store_product_id")
    private String appStoreProductId;
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", columnDefinition = "varchar(255)")
    private ProviderType provider;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private SegmentType segment;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private Contract contract;
    @Column(name = "content_category")
    private String contentCategory;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "sub_merchant_id")
    private String subMerchantId;
    @Column(name = "content_description")
    private String contentDescription;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)", nullable = false)
    private Tariff tariff;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)", name = "media_type", nullable = false)
    private MediaType mediaType;
    @Column(name = "advanced_payment_seconds", nullable = false)
    private int advancedPaymentSeconds;
    @Column(name = "after_next_sub_payment_seconds", nullable = false)
    private int afterNextSubPaymentSeconds;
    @Column(name = "is_default")
    private boolean isDefault;
    private boolean online;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_policy_type")
    private PaymentPolicyType paymentPolicyType;
    @Column(name = "payment_order", nullable = false, columnDefinition = "int default 0")
    private int order;

    @Column(name = "start_date_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateTime;

    @Column(name = "end_date_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateTime;

    @Column(name = "message_key")
    private String messageKey;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public BigDecimal getSubcost() {
        return subcost;
    }

    public void setSubcost(BigDecimal subcost) {
        this.subcost = subcost;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Integer getCommunityId() {
        return communityId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(String currencyISO) {
        this.currencyISO = currencyISO;
    }

    public boolean isAvailableInStore() {
        return availableInStore;
    }

    public void setAvailableInStore(boolean availableInStore) {
        this.availableInStore = availableInStore;
    }

    public String getAppStoreProductId() {
        return appStoreProductId;
    }

    public void setAppStoreProductId(String appStoreProductId) {
        this.appStoreProductId = appStoreProductId;
    }

    public PaymentPolicyType getPaymentPolicyType() {
        return paymentPolicyType;
    }

    public void setPaymentPolicyType(PaymentPolicyType paymentPolicyType) {
        this.paymentPolicyType = paymentPolicyType;
    }

    public int getOrder() {
        return order;
    }

    public SegmentType getSegment() {
        return segment;
    }

    public void setSegment(SegmentType segment) {
        this.segment = segment;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getContentCategory() {
        return contentCategory;
    }

    public void setContentCategory(String contentCategory) {
        this.contentCategory = contentCategory;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getSubMerchantId() {
        return subMerchantId;
    }

    public void setSubMerchantId(String subMerchantId) {
        this.subMerchantId = subMerchantId;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType providerType) {
        provider = providerType;
    }

    public int getAfterNextSubPaymentSeconds() {
        return afterNextSubPaymentSeconds;
    }

    public int getAdvancedPaymentSeconds() {
        return advancedPaymentSeconds;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public PaymentPolicyDto toPaymentPolicyDto(PaymentDetailsByPaymentDto paymentDetailsByPaymentDto) {
        PaymentPolicyDto paymentPolicyDto = paymentDetailsByPaymentDto.new PaymentPolicyDto();

        paymentPolicyDto.setCurrencyISO(currencyISO);
        if (operator != null) {
            paymentPolicyDto.setOperator(operator.getId());
            paymentPolicyDto.setOperatorName(operatorName);
            paymentPolicyDto.setShortCode(shortCode);
        }

        paymentPolicyDto.setPaymentType(paymentType);
        paymentPolicyDto.setSubcost(subcost);
        paymentPolicyDto.setDuration(period.getDuration());
        paymentPolicyDto.setDurationUnit(period.getDurationUnit());
        return paymentPolicyDto;
    }

    public boolean is4GVideoAudioSubscription() {
        return Tariff._4G.equals(tariff) && VIDEO_AND_AUDIO.equals(mediaType);
    }

    public boolean isVideoAndAudio4GSubscription() {// jstl can not call methods starting with numbers
        return is4GVideoAudioSubscription();
    }

    public boolean isAudioSubscription() {
        return AUDIO.equals(mediaType);
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public PaymentPolicy withId(Integer id) {
        setId(id);
        return this;
    }

    public PaymentPolicy withTariff(Tariff tariff) {
        setTariff(tariff);
        return this;
    }

    public PaymentPolicy withCommunity(Community community) {
        setCommunity(community);
        return this;
    }

    public PaymentPolicy withMediaType(MediaType mediaType) {
        setMediaType(mediaType);
        return this;
    }

    public PaymentPolicy withDefault(boolean aDefault) {
        this.setDefault(aDefault);
        return this;
    }

    public PaymentPolicy withPaymentType(String paymentType) {
        setPaymentType(paymentType);
        return this;
    }

    public PaymentPolicy withSegment(SegmentType segment) {
        setSegment(segment);
        return this;
    }

    public PaymentPolicy withContract(Contract contract) {
        setContract(contract);
        return this;
    }

    public PaymentPolicy withProvider(ProviderType provider) {
        setProvider(provider);
        return this;
    }

    public PaymentPolicy withSubCost(BigDecimal subCost) {
        setSubcost(subCost);
        return this;
    }

    public PaymentPolicy withAvailableInStore(boolean availableInStore) {
        setAvailableInStore(availableInStore);
        return this;
    }

    public PaymentPolicy withAfterNextSubPaymentSeconds(int afterNextSubPaymentSeconds) {
        this.afterNextSubPaymentSeconds = afterNextSubPaymentSeconds;
        return this;
    }

    public PaymentPolicy withAdvancedPaymentSeconds(int advancedPaymentSeconds) {
        this.advancedPaymentSeconds = advancedPaymentSeconds;
        return this;
    }

    public PaymentPolicy withShortCode(String shortCode) {
        this.shortCode = shortCode;
        return this;
    }

    public PaymentPolicy withOperator(Operator operator) {
        this.operator = operator;
        return this;
    }

    public PaymentPolicy withCurrencyISO(String currencyISO) {
        this.currencyISO = currencyISO;
        return this;
    }

    public PaymentPolicy withAppStoreProductId(String appStoreProductId) {
        this.appStoreProductId = appStoreProductId;
        return this;
    }

    public PaymentPolicy withContentCategory(String contentCategory) {
        this.contentCategory = contentCategory;
        return this;
    }

    public PaymentPolicy withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public PaymentPolicy withContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
        return this;
    }

    public PaymentPolicy withSubMerchantId(String subMerchantId) {
        this.subMerchantId = subMerchantId;
        return this;
    }

    public PaymentPolicy withOnline(boolean online) {
        this.online = online;
        return this;
    }

    public PaymentPolicy withPeriod(Period period) {
        this.period = period;
        return this;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("communityId", communityId).append("subcost", subcost).append("period", period).append("operatorId", operatorId)
                                        .append("paymentType", paymentType).append("paymentPolicyType", paymentPolicyType).append("operatorName", operatorName).append("shortCode", shortCode)
                                        .append("currencyISO", currencyISO).append("availableInStore", availableInStore).append("appStoreProductId", appStoreProductId).append("provider", provider)
                                        .append("segment", segment).append("contract", contract).append("contentCategory", contentCategory).append("contentType", contentType)
                                        .append("subMerchantId", subMerchantId).append("contentDescription", contentDescription).append("tariff", tariff).append("mediaType", mediaType)
                                        .append("advancedPaymentSeconds", advancedPaymentSeconds).append("afterNextSubPaymentSeconds", afterNextSubPaymentSeconds).append("isDefault", isDefault)
                                        .append("online", online).append("order", order).append("startDateTime", startDateTime).append("endDateTime", endDateTime).append("messageKey", messageKey)
                                        .toString();
    }
}