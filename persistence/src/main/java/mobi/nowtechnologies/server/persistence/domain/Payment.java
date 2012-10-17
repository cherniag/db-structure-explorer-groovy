package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the tb_payments database table.
 * 
 */
@Entity
@Table(name="tb_payments")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="paymentType",discriminatorType=DiscriminatorType.STRING)
public abstract class Payment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Fields {
    	internalTxCode, status, userUID, txType, timestamp, description
    }

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long i;

	private float amount;

	@Column(name="description",columnDefinition="char(100)")
	private String description;

	@Column(name="externalAuthCode",columnDefinition="char(20)")
	private String externalAuthCode;

	@Column(name="externalSecurityKey",columnDefinition="char(20)")
	private String externalSecurityKey;

	@Column(name="externalTxCode",columnDefinition="char(38)")
	private String externalTxCode;

	@Column(name="internalTxCode",columnDefinition="char(40)")
	private String internalTxCode;

	private long relatedPayment;

	@Column(name="status",columnDefinition="char(15)")
	private String status;

	@Column(name="statusDetail",columnDefinition="char(255)")
	private String statusDetail;

	private int timestamp;

	private int txType;

	private int userUID;
	
	@Column(name = "subWeeks", columnDefinition = "tinyint(3)", length = 3, nullable = false)
	private byte subweeks;
	
	private Integer numPaymentRetries;
	
	private String currencyCode;
	
	//private String payerId;

    public Payment() {
    }

	public long getI() {
		return this.i;
	}

	public void setI(long i) {
		this.i = i;
	}

	public float getAmount() {
		return this.amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExternalAuthCode() {
		return this.externalAuthCode;
	}

	public void setExternalAuthCode(String externalAuthCode) {
		this.externalAuthCode = externalAuthCode;
	}

	public String getExternalSecurityKey() {
		return this.externalSecurityKey;
	}

	public void setExternalSecurityKey(String externalSecurityKey) {
		this.externalSecurityKey = externalSecurityKey;
	}

	public String getExternalTxCode() {
		return this.externalTxCode;
	}

	public void setExternalTxCode(String externalTxCode) {
		this.externalTxCode = externalTxCode;
	}

	public String getInternalTxCode() {
		return this.internalTxCode;
	}

	public void setInternalTxCode(String internalTxCode) {
		this.internalTxCode = internalTxCode;
	}

	public long getRelatedPayment() {
		return this.relatedPayment;
	}

	public void setRelatedPayment(long relatedPayment) {
		this.relatedPayment = relatedPayment;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusDetail() {
		return this.statusDetail;
	}

	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}
	
	@Transient
	@XmlTransient
	public Calendar getDateTime() {
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(timestamp));
		return calendar;
	}

	public int getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getTxType() {
		return this.txType;
	}

	public void setTxType(int txType) {
		this.txType = txType;
	}

	public int getUserUID() {
		return this.userUID;
	}

	public void setUserUID(int userUID) {
		this.userUID = userUID;
	}

	public byte getSubweeks() {
		return subweeks;
	}

	public void setSubweeks(byte subweeks) {
		this.subweeks = subweeks;
	}

	public Integer getNumPaymentRetries() {
		return numPaymentRetries;
	}

	public void setNumPaymentRetries(Integer numPaymentRetries) {
		this.numPaymentRetries = numPaymentRetries;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	@Override
	public String toString() {
		return "Payment [amount=" + amount + ", currencyCode=" + currencyCode
				+ ", description=" + description + ", externalAuthCode="
				+ externalAuthCode + ", externalSecurityKey="
				+ externalSecurityKey + ", externalTxCode=" + externalTxCode
				+ ", i=" + i + ", internalTxCode=" + internalTxCode
				+ ", numPaymentRetries=" + numPaymentRetries
				+ ", relatedPayment=" + relatedPayment + ", status=" + status
				+ ", statusDetail=" + statusDetail + ", subweeks=" + subweeks
				+ ", timestamp=" + timestamp + ", txType=" + txType
				+ ", userUID=" + userUID + "]";
	}

//	public String getPayerId() {
//		return payerId;
//	}
//
//	public void setPayerId(String payerId) {
//		this.payerId = payerId;
//	}
//
//	@Override
//	public String toString() {
//		return "Payment [amount=" + amount + ", description=" + description
//				+ ", externalAuthCode=" + externalAuthCode
//				+ ", externalSecurityKey=" + externalSecurityKey
//				+ ", externalTxCode=" + externalTxCode + ", i=" + i
//				+ ", internalTxCode=" + internalTxCode + ", numPaymentRetries="
//				+ numPaymentRetries + ", payerId=" + payerId
//				+ ", relatedPayment=" + relatedPayment + ", status=" + status
//				+ ", statusDetail=" + statusDetail + ", subweeks=" + subweeks
//				+ ", timestamp=" + timestamp + ", txType=" + txType
//				+ ", userUID=" + userUID + "]";
//	}
}