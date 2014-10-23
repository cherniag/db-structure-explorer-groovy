package mobi.nowtechnologies.server.shared.dto.web;

import mobi.nowtechnologies.server.shared.enums.DurationUnit;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Titov Mykhaylo (titov)
 */
public class PaymentHistoryItemDto {
	public static final String PAYMENT_HISTORY_ITEM_DTO = "PaymentHistoryItemDto";

	private String transactionId;

	private Date date;

	private String description;

	private BigDecimal amount;

	private String paymentMethod;

	private long duration;

	private DurationUnit durationUnit;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public DurationUnit getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(DurationUnit durationUnit) {
		this.durationUnit = durationUnit;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("amount", amount)
				.append("date", date)
				.append("duration", duration)
				.append("periodUnit", durationUnit)
				.append("paymentMethod", paymentMethod)
				.append("transactionId", transactionId)
				.append("description", description)
				.toString();
	}
}
