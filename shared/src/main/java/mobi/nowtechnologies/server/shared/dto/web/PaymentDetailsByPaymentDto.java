package mobi.nowtechnologies.server.shared.dto.web;

import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

/**
 * @author Titov Mykhaylo (titov)
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
		private int duration;
		private DurationUnit durationUnit;
		private Integer operator;
		private String operatorName;
		private String paymentType;
		private String shortCode;
		private BigDecimal oldSubcost;
		private Integer oldDuration;
		private DurationUnit oldDurationUnit;
		private String currencyISO;

		public BigDecimal getSubcost() {
			return subcost;
		}

		public void setSubcost(BigDecimal subcost) {
			this.subcost = subcost;
		}

		public BigDecimal getOldSubcost() {
			return oldSubcost;
		}

		public void setOldSubcost(BigDecimal oldSubcost) {
			this.oldSubcost = oldSubcost;
		}

		public Integer getOldDuration() {
			return oldDuration;
		}

		public void setOldDuration(Integer oldDuration) {
			this.oldDuration = oldDuration;
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

		public long getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}

		public DurationUnit getDurationUnit() {
			return durationUnit;
		}

		public void setDurationUnit(DurationUnit durationUnit) {
			this.durationUnit = durationUnit;
		}

		public DurationUnit getOldDurationUnit() {
			return oldDurationUnit;
		}

		public void setOldDurationUnit(DurationUnit oldDurationUnit) {
			this.oldDurationUnit = oldDurationUnit;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("subcost", subcost)
					.append("duration", duration)
					.append("periodUnit", durationUnit)
					.append("operator", operator)
					.append("operatorName", operatorName)
					.append("paymentType", paymentType)
					.append("shortCode", shortCode)
					.append("oldSubcost", oldSubcost)
					.append("oldDuration", oldDuration)
					.append("oldDurationUnit", oldDurationUnit)
					.append("currencyISO", currencyISO)
					.toString();
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
		return new ToStringBuilder(this)
				.append("paymentDetailsId", paymentDetailsId)
				.append("paymentType", paymentType)
				.append("paymentPolicyDto", paymentPolicyDto)
				.append("activated", activated)
				.toString();
	}
}
