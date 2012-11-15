package mobi.nowtechnologies.server.shared.dto.admin;

import mobi.nowtechnologies.server.shared.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class PendingPaymentDto {
	public static final String PENDING_PAYMENT_DTO ="PENDING_PAYMENT_DTO";
	public static final String PENDING_PAYMENT_DTO_LIST ="PENDING_PAYMENT_DTO_LIST";
	
	private Long id;
	private String internalTxId;
	private Date date;
	private String gateway;
	private BigDecimal amount;
	private CurrencyCode currencyCode;
	private int numPsmsRetries;
	
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
	public int getNumPsmsRetries() {
		return numPsmsRetries;
	}
	public void setNumPsmsRetries(int numPsmsRetries) {
		this.numPsmsRetries = numPsmsRetries;
	}
	@Override
	public String toString() {
		return "PendingPaymentDto [id=" + id + ", internalTxId=" + internalTxId + ", amount=" + amount + ", currencyCode=" + currencyCode + ", date=" + date
				+ ", gateway=" + gateway + ", numPsmsRetries=" + numPsmsRetries + "]";
	}

}
