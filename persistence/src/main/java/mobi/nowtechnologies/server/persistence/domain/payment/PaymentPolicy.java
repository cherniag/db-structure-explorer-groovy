package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto.PaymentPolicyDto;
import mobi.nowtechnologies.server.shared.enums.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static mobi.nowtechnologies.server.shared.enums.MediaType.AUDIO;
import static mobi.nowtechnologies.server.shared.enums.MediaType.VIDEO_AND_AUDIO;

@Entity
@Table(name = "tb_paymentPolicy")
@NamedQueries(value = { @NamedQuery(name = PaymentPolicy.GET_OPERATORS_LIST, query = "select paymentPolicy.operator from PaymentPolicy paymentPolicy where paymentPolicy.communityId=?1 and paymentPolicy.paymentType=?2"),
        @NamedQuery(name = PaymentPolicy.GET_BY_COMMUNITY_AND_AVAILABLE_IN_STORE, query = "select paymentPolicy from PaymentPolicy paymentPolicy where paymentPolicy.community=?1 and paymentPolicy.availableInStore=?2")})
@Access(AccessType.FIELD)
public class PaymentPolicy {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentPolicy.class);

    public static final String GET_OPERATORS_LIST = "GET_OPERATORS_LIST";

    public static final String GET_BY_COMMUNITY_AND_AVAILABLE_IN_STORE = "GET_BY_COMMUNITY_AND_AVAILABLE_IN_STORE";



    public static enum Fields {
        communityId
    }

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

    @Column(name = "subWeeks", columnDefinition = "tinyint(3)", length = 3, nullable = false)
    private byte subweeks;

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

    @Column(name="app_store_product_id")
    private String appStoreProductId;

    @Enumerated(EnumType.STRING)
    @Column(name="provider", columnDefinition = "varchar(255)")
    private ProviderType provider;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private SegmentType segment;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "char(255)")
    private Contract contract;

    @Column(name="content_category")
    private String contentCategory;

    @Column(name="content_type")
    private String contentType;

    @Column(name="sub_merchant_id")
    private String subMerchantId;

    @Column(name="content_description")
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

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
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

    public void setSubweeks(byte subweeks) {
        this.subweeks = subweeks;
    }

    public byte getSubweeks() {
        return subweeks;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorName() {
        return operatorName;
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

    public OfferPaymentPolicyDto toOfferPaymentPolicyDto() {
        OfferPaymentPolicyDto offerPaymentPolicyDto = new OfferPaymentPolicyDto();

        offerPaymentPolicyDto.setPaymentType(paymentType);

        LOGGER.debug("Output parameter [{}]", offerPaymentPolicyDto);
        return offerPaymentPolicyDto;
    }

    public static List<OfferPaymentPolicyDto> toOfferPaymentPolicyDtos(List<PaymentPolicy> paymentPolicies) {
        LOGGER.debug("input parameters paymentPolicies: [{}]", paymentPolicies);

        List<OfferPaymentPolicyDto> offerPaymentPolicyDtos = new ArrayList<OfferPaymentPolicyDto>();
        for (PaymentPolicy paymentPolicy : paymentPolicies) {
            offerPaymentPolicyDtos.add(paymentPolicy.toOfferPaymentPolicyDto());
        }

        LOGGER.debug("Output parameter [{}]", offerPaymentPolicyDtos);
        return offerPaymentPolicyDtos;
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

    public PaymentPolicyDto toPaymentPolicyDto(PaymentDetailsByPaymentDto paymentDetailsByPaymentDto) {
        LOGGER.debug("input parameters paymentDetailsByPaymentDto: [{}]", paymentDetailsByPaymentDto);

        PaymentPolicyDto paymentPolicyDto = paymentDetailsByPaymentDto.new PaymentPolicyDto();

        paymentPolicyDto.setCurrencyISO(currencyISO);
        if (operator!=null) {
            paymentPolicyDto.setOperator(operator.getId());
            paymentPolicyDto.setOperatorName(operatorName);
            paymentPolicyDto.setShortCode(shortCode);
        }

        paymentPolicyDto.setPaymentType(paymentType);
        paymentPolicyDto.setSubcost(subcost);
        paymentPolicyDto.setSubweeks(new Integer(subweeks));

        LOGGER.debug("Output parameter [{}]", paymentPolicyDto);
        return paymentPolicyDto;
    }

    public boolean is4GVideoAudioSubscription(){
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

    public PaymentPolicy withId(Integer id){
        setId(id);
        return this;
    }

    public PaymentPolicy withTariff(Tariff tariff){
        setTariff(tariff);
        return this;
    }

    public PaymentPolicy withCommunity(Community community){
        setCommunity(community);
        return this;
    }

    public PaymentPolicy withMediaType(MediaType mediaType){
        setMediaType(mediaType);
        return this;
    }

    public PaymentPolicy withDefault(boolean aDefault){
        this.setDefault(aDefault);
        return this;
    }

    public PaymentPolicy withPaymentType(String paymentType){
        setPaymentType(paymentType);
        return this;
    }

    public PaymentPolicy withSegment(SegmentType segment){
        setSegment(segment);
        return this;
    }

    public PaymentPolicy withContract(Contract contract){
        setContract(contract);
        return this;
    }

    public PaymentPolicy withProvider(ProviderType provider){
        setProvider(provider);
        return this;
    }

    public PaymentPolicy withSubCost(BigDecimal subCost) {
        setSubcost(subCost);
        return this;
    }

    public PaymentPolicy withSubWeeks(byte subWeeks) {
        setSubweeks(subWeeks);
        return this;
    }

    public PaymentPolicy withAvailableInStore(boolean availableInStore) {
        setAvailableInStore(availableInStore);
        return this;
    }

    public PaymentPolicy withAfterNextSubPaymentSeconds(int afterNextSubPaymentSeconds){
        this.afterNextSubPaymentSeconds = afterNextSubPaymentSeconds;
        return this;
    }

    public PaymentPolicy withaAvancedPaymentSeconds(int advancedPaymentSeconds){
        this.advancedPaymentSeconds = advancedPaymentSeconds;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("communityId", communityId)
                .append("subcost", subcost)
                .append("subweeks", subweeks)
                .append("operatorId", operatorId)
                .append("paymentType", paymentType)
                .append("operatorName", operatorName)
                .append("shortCode", shortCode)
                .append("currencyISO", currencyISO)
                .append("availableInStore", availableInStore)
                .append("appStoreProductId", appStoreProductId)
                .append("provider", provider)
                .append("segment", segment)
                .append("contract", contract)
                .append("contentCategory", contentCategory)
                .append("contentType", contentType)
                .append("subMerchantId", subMerchantId)
                .append("contentDescription", contentDescription)
                .append("tariff", tariff)
                .append("mediaType", mediaType)
                .append("advancedPaymentSeconds", advancedPaymentSeconds)
                .append("afterNextSubPaymentSeconds", afterNextSubPaymentSeconds)
                .append("isDefault", isDefault)
                .toString();
    }
}