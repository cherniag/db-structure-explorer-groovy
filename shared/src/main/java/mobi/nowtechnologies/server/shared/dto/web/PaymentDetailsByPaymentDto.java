package mobi.nowtechnologies.server.shared.dto.web;

import java.math.BigDecimal;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PaymentDetailsByPaymentDto {

	public static final String NAME = "paymentDetailsByPaymentDto";

	private Long paymentDetailsId;
	private String paymentType;
	private PaymentPolicyDto paymentPolicyDto;
	private boolean activated;

	public class PaymentPolicyDto {

		public static final String NAME = "paymentPolicyDto";

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

		@Override
		public String toString() {
			return "PaymentPolicyDto [currencyISO=" + currencyISO + ", oldSubcost=" + oldSubcost + ", oldSubweeks=" + oldSubweeks + ", operator=" + operator + ", operatorName=" + operatorName
					+ ", paymentType=" + paymentType + ", shortCode=" + shortCode + ", subcost=" + subcost + ", subweeks=" + subweeks + "]";
		}

	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public Long getPaymentDetailsId() {
		return paymentDetailsId;
	}

	public void setPaymentDetailsId(Long paymentDetailsId) {
		this.paymentDetailsId = paymentDetailsId;
	}

	public PaymentPolicyDto getPaymentPolicyDto() {
		return paymentPolicyDto;
	}

	public void setPaymentPolicyDto(PaymentPolicyDto paymentPolicyDto) {
		this.paymentPolicyDto = paymentPolicyDto;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean isActivated() {
		return activated;
	}

	@Override
	public String toString() {
		return "PaymentDetailsByPaymentDto [paymentDetailsId=" + paymentDetailsId + ", paymentPolicyDto=" + paymentPolicyDto + ", paymentType=" + paymentType + ", activated=" + activated + "]";
	}

}
