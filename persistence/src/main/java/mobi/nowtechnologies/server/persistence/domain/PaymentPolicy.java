package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.shared.dto.web.OfferPaymentPolicyDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto;
import mobi.nowtechnologies.server.shared.dto.web.PaymentDetailsByPaymentDto.PaymentPolicyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_paymentPolicy")
@NamedQueries(value = { @NamedQuery(name = PaymentPolicy.GET_OPERATORS_LIST, query = "select paymentPolicy.operator from PaymentPolicy paymentPolicy where paymentPolicy.communityId=?1 and paymentPolicy.paymentType=?2"),
		@NamedQuery(name = PaymentPolicy.GET_BY_COMMUNITY_AND_AVAILABLE_IN_STORE, query = "select paymentPolicy from PaymentPolicy paymentPolicy where paymentPolicy.community=?1 and paymentPolicy.availableInStore=?2")})
public class PaymentPolicy {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentPolicy.class);

	public static final String GET_OPERATORS_LIST = "GET_OPERATORS_LIST";

	public static final String GET_BY_COMMUNITY_AND_AVAILABLE_IN_STORE = "GET_BY_COMMUNITY_AND_AVAILABLE_IN_STORE";

	public static enum Fields {
		communityId, operator, paymentType
	}

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	@Column(name = "i", length = 5, nullable = false)
	private short id;

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

	public void setId(short id) {
		this.id = id;
	}

	public short getId() {
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
	

	@Override
	public String toString() {
		return "PaymentPolicy [communityId=" + communityId + ", id=" + id
				+ ", paymentType=" + paymentType + ", shortCode=" + shortCode
				+ ", subcost=" + subcost + ", subweeks=" + subweeks + ", appStoreProductId=" + appStoreProductId + ", availableInStore=" + availableInStore + "]";
	}

	public PaymentPolicyDto toPaymentPolicyDto(PaymentDetailsByPaymentDto paymentDetailsByPaymentDto) {
		LOGGER.debug("input parameters paymentDetailsByPaymentDto: [{}]", paymentDetailsByPaymentDto);
		
		PaymentPolicyDto paymentPolicyDto = paymentDetailsByPaymentDto.new PaymentPolicyDto();
		
		paymentPolicyDto.setCurrencyISO(currencyISO);
		//paymentPolicyDto.setOldSubcost(oldSubcost);
		//paymentPolicyDto.setOldSubweeks(oldSubweeks);
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


}