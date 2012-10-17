package mobi.nowtechnologies.server.shared.dto.admin;

import java.math.BigDecimal;
import java.util.Date;
import mobi.nowtechnologies.server.shared.enums.CurrencyCode;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class SubmittedPaymentDto {
	
	public static final String SUBMITTED_PAYMENT_DTO_LIST = "SUBMITTED_PAYMENT_DTO_LIST";

	private Long id;
	
	private String internalTxId;
	
	private Date date;

	private String gateway;
	
	private BigDecimal amount;
	
	private CurrencyCode currencyCode;
	
	private String descriptionError;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInternalTxId() {
		return internalTxId;
	}

	public void setInternalTxId(String internalTxId) {
		this.internalTxId = internalTxId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public CurrencyCode getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(CurrencyCode currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getDescriptionError() {
		return descriptionError;
	}

	public void setDescriptionError(String descriptionError) {
		this.descriptionError = descriptionError;
	}

	@Override
	public String toString() {
		return "SubmittedPaymentDto [id=" + id + ", internalTxId=" + internalTxId + ", amount=" + amount + ", currencyCode=" + currencyCode + ", date=" + date
				+ ", descriptionError=" + descriptionError + ", gateway=" + gateway + "]";
	}
}
