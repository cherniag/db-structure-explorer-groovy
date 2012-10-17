package mobi.nowtechnologies.server.shared.dto.web;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class PaymentHistoryItemDto {
	public static final String PAYMENT_HISTORY_ITEM_DTO = "PaymentHistoryItemDto";

	private String transactionId;

	private Date date;

	private String description;

	private BigDecimal amount;

	private String paymentMethod;
	
	private int weeks;

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

	public int getWeeks() {
		return weeks;
	}

	public void setWeeks(int weeks) {
		this.weeks = weeks;
	}

	@Override
	public String toString() {
		return "PaymentHistoryItemDto [amount=" + amount + ", date=" + date + ", description=" + description + ", paymentMethod=" + paymentMethod
				+ ", transactionId=" + transactionId + ", weeks=" + weeks + "]";
	}

}
