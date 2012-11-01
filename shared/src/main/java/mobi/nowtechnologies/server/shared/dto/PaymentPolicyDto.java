package mobi.nowtechnologies.server.shared.dto;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="PaymentPolicy")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PaymentPolicyDto {
	
	public static final String PAYMENT_POLICY_DTO = "paymentPolicy"; 
	
	private short id;
	private BigDecimal subcost;
	private Integer subweeks;
	private Integer operator;
	private String operatorName;
	private String paymentType;
	private String shortCode;
	private BigDecimal oldSubcost;
	private Integer oldSubweeks;
	private String currencyISO;
	
	public PaymentPolicyDto() {
	}
	
	public short getId() {
		return id;
	}
	public void setId(short id) {
		this.id = id;
	}
	public BigDecimal getSubcost() {
		return subcost;
	}
	public void setSubcost(BigDecimal subcost) {
		this.subcost = subcost;
	}
	
	public Integer getSubweeks() {
		return subweeks;
	}

	public void setSubweeks(Integer subweeks) {
		this.subweeks = subweeks;
	}

	public BigDecimal getOldSubcost() {
		return oldSubcost;
	}

	public void setOldSubcost(BigDecimal oldSubcost) {
		this.oldSubcost = oldSubcost;
	}

	public Integer getOldSubweeks() {
		return oldSubweeks;
	}

	public void setOldSubweeks(Integer oldSubweeks) {
		this.oldSubweeks = oldSubweeks;
	}

	public Integer getOperator() {
		return operator;
	}
	public void setOperator(Integer operator) {
		this.operator = operator;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getCurrencyISO() {
		return currencyISO;
	}

	public void setCurrencyISO(String currencyISO) {
		this.currencyISO = currencyISO;
	}
}