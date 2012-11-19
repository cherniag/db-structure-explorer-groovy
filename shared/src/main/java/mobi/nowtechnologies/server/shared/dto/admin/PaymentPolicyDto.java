package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.enums.CurrencyCode;
import org.springframework.expression.spel.ast.Operator;

import java.math.BigDecimal;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PaymentPolicyDto {
	
	private short id;

	private Integer communityId;

	private BigDecimal subcost;

	private byte subweeks;

	private Operator operator;

	private Integer operatorId;

	private String paymentType;

	private String operatorName;

	private String shortCode;

	private CurrencyCode currencyCode;

	private boolean availableInStore;

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public Integer getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Integer communityId) {
		this.communityId = communityId;
	}

	public BigDecimal getSubcost() {
		return subcost;
	}

	public void setSubcost(BigDecimal subcost) {
		this.subcost = subcost;
	}

	public byte getSubweeks() {
		return subweeks;
	}

	public void setSubweeks(byte subweeks) {
		this.subweeks = subweeks;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
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

	public CurrencyCode getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(CurrencyCode currencyCode) {
		this.currencyCode = currencyCode;
	}

	public boolean isAvailableInStore() {
		return availableInStore;
	}

	public void setAvailableInStore(boolean availableInStore) {
		this.availableInStore = availableInStore;
	}

	@Override
	public String toString() {
		return "PaymentPolicyDto [availableInStore=" + availableInStore + ", communityId=" + communityId + ", currencyCode=" + currencyCode + ", id=" + id
				+ ", operator=" + operator + ", operatorId=" + operatorId + ", operatorName=" + operatorName + ", paymentType=" + paymentType + ", shortCode="
				+ shortCode + ", subcost=" + subcost + ", subweeks=" + subweeks + "]";
	}

}
