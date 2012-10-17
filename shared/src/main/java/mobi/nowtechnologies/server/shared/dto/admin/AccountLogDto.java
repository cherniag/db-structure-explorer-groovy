package mobi.nowtechnologies.server.shared.dto.admin;

import java.util.Date;

import mobi.nowtechnologies.server.shared.enums.TransactionType;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class AccountLogDto {

	public static final String ACCOUNT_LOG_DTO = "ACCOUNT_LOG_DTO";
	public static final String ACCOUNT_LOG_DTO_LIST = "ACCOUNT_LOG_DTO_LIST";

	private long id;

	private int balanceAfter;

	private Date logTimestamp;

	private Integer relatedMediaId;

	private String relatedMediaIsrc;

	private Long relatedPaymentUID;

	private TransactionType transactionType;

	private int userId;

	private String promoCode;

	private String gateway;

	private Integer amount;

	private String amountCurrency;

	private String internalTxId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(int balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public Date getLogTimestamp() {
		return logTimestamp;
	}

	public void setLogTimestamp(Date logTimestamp) {
		this.logTimestamp = logTimestamp;
	}

	public Integer getRelatedMediaId() {
		return relatedMediaId;
	}

	public void setRelatedMediaId(Integer relatedMediaId) {
		this.relatedMediaId = relatedMediaId;
	}

	public String getRelatedMediaIsrc() {
		return relatedMediaIsrc;
	}

	public void setRelatedMediaIsrc(String relatedMediaIsrc) {
		this.relatedMediaIsrc = relatedMediaIsrc;
	}

	public Long getRelatedPaymentUID() {
		return relatedPaymentUID;
	}

	public void setRelatedPaymentUID(Long relatedPaymentUID) {
		this.relatedPaymentUID = relatedPaymentUID;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getAmountCurrency() {
		return amountCurrency;
	}

	public void setAmountCurrency(String amountCurrency) {
		this.amountCurrency = amountCurrency;
	}

	public String getInternalTxId() {
		return internalTxId;
	}

	public void setInternalTxId(String internalTxId) {
		this.internalTxId = internalTxId;
	}

	@Override
	public String toString() {
		return "AccountLogDto [id=" + id + ", userId=" + userId + ", amount=" + amount + ", amountCurrency=" + amountCurrency + ", balanceAfter="
				+ balanceAfter + ", transactionType=" + transactionType + ", gateway=" + gateway + ", logTimestamp=" + logTimestamp + ", promoCode="
				+ promoCode + ", relatedMediaId=" + relatedMediaId + ", relatedMediaIsrc=" + relatedMediaIsrc + ", relatedPaymentUID=" + relatedPaymentUID
				+ ", internalTxId=" + internalTxId + "]";
	}

}
