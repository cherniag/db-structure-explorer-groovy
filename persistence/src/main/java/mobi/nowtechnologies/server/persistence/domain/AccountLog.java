package mobi.nowtechnologies.server.persistence.domain;

import static mobi.nowtechnologies.server.shared.Utils.getEpochSeconds;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.shared.dto.web.PaymentHistoryItemDto;
import mobi.nowtechnologies.server.shared.enums.TransactionType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name="tb_accountLog")
@NamedQueries({
	@NamedQuery(name=AccountLog.NQ_FIND_BY_USER_AND_LOG_TYPE, query="select accountLog from AccountLog accountLog where accountLog.userId=? and accountLog.transactionType=? order by accountLog.id desc")	
})
public class AccountLog implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NQ_FIND_BY_USER_AND_LOG_TYPE = "findByUserAndLogType";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountLog.class);
	
	public static enum Fields {
		userId,
		balanceAfter,
		transactionType
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="i")
	private long id;

	private int balanceAfter;

	private int logTimestamp;

	@Column(insertable=false, updatable=false)
	private Integer relatedMediaUID;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "relatedMediaUID", nullable = true)
	private Media media;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "relatedPaymentUID", nullable = true)
	private SubmittedPayment submittedPayment;
	
	@Column(insertable=false, updatable=false)
	private Long relatedPaymentUID;

	@Enumerated(EnumType.ORDINAL)
	private TransactionType transactionType;

	@Column(name="userUID")
	private int userId;
	
	private String promoCode;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "offerId", nullable = true)
	private Offer offer;
	
	@Column(insertable=false, updatable=false)
	private Integer offerId;

    @Column(length = 10000)
    private String description;

    public AccountLog() {
    }
    
	public AccountLog(int userId, SubmittedPayment submittedPayment, 
			int balanceAfter, TransactionType transactionType) {
		this.logTimestamp = getEpochSeconds();
		this.balanceAfter = balanceAfter;
		this.transactionType = transactionType;
		this.userId = userId;
		
		setSubmittedPayment(submittedPayment);
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBalanceAfter() {
		return this.balanceAfter;
	}	

	public void setBalanceAfter(int balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public int getLogTimestamp() {
		return this.logTimestamp;
	}

	public void setLogTimestamp(int logTimestamp) {
		this.logTimestamp = logTimestamp;
	}

	public Integer getRelatedMediaUID() {
		return this.relatedMediaUID;
	}

	public void setRelatedMediaUID(Integer relatedMediaUID) {
		this.relatedMediaUID = relatedMediaUID;
	}

	public Long getRelatedPaymentUID() {
		return this.relatedPaymentUID;
	}

	public TransactionType getTransactionType() {
		return this.transactionType;
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

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
		if(media!=null){
			relatedMediaUID = media.getI();
		}else{
			relatedMediaUID = null;
		}
	}

	public SubmittedPayment getSubmittedPayment() {
		return submittedPayment;
	}

	public void setSubmittedPayment(SubmittedPayment submittedPayment) {
		this.submittedPayment = submittedPayment;
		if (submittedPayment!=null){
			this.relatedPaymentUID = submittedPayment.getI();
		}else{
			relatedPaymentUID = null;
		}
	}

	@Transient
	@XmlTransient
	public Calendar getDateTime() {
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeInMillis(TimeUnit.SECONDS.toMillis(logTimestamp));
		return calendar;
	}
	
	public static List<PaymentHistoryItemDto> toPaymentHistoryItemDto(List<AccountLog> accountLogs){
		LOGGER.debug("input parameters accountLogs: [{}]", accountLogs);
		
		List<PaymentHistoryItemDto> paymentHistoryItemDtos = new LinkedList<PaymentHistoryItemDto>();
		for (AccountLog accountLog : accountLogs) {
			paymentHistoryItemDtos.add(accountLog.toPaymentHistoryItemDto());
		}
		
		LOGGER.debug("Output parameter paymentHistoryItemDtos=[{}]", paymentHistoryItemDtos);
		return paymentHistoryItemDtos;
	}

	public PaymentHistoryItemDto toPaymentHistoryItemDto() {
		PaymentHistoryItemDto paymentHistoryItemDto = new PaymentHistoryItemDto();
		
		//paymentHistoryItemDto.setAmount(balanceAfter);
		paymentHistoryItemDto.setDate(new Date(logTimestamp*1000L)); // TODO AccountLog field logTimestamp is type of int (UNIXTIME in seconds)
		paymentHistoryItemDto.setDescription(String.valueOf(transactionType));
		
		LOGGER.debug("Output parameter paymentHistoryItemDto=[{}]", paymentHistoryItemDto);
		return paymentHistoryItemDto;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
		if(offer!=null){
			offerId = offer.getId();
		}else {
			offerId=null;
		}
	}

	public Integer getOfferId() {
		return offerId;
	}

    public AccountLog withDescription(String description){
        this.description=description;
        return this;
    }

    public AccountLog withUser(User user){
        this.userId=user.getId();
        return this;
    }

    public AccountLog withBalanceAfter(int balanceAfter){
        this.balanceAfter = balanceAfter;
        return this;
    }

    public AccountLog withLogTimestamp(int logTimestamp){
        this.logTimestamp = logTimestamp;
        return this;
    }

    public AccountLog withTransactionType(TransactionType transactionType){
        this.transactionType = transactionType;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("balanceAfter", balanceAfter)
                .append("logTimestamp", logTimestamp)
                .append("relatedMediaUID", relatedMediaUID)
                .append("relatedPaymentUID", relatedPaymentUID)
                .append("transactionType", transactionType)
                .append("userId", userId)
                .append("promoCode", promoCode)
                .append("offerId", offerId)
                .append("description", description)
                .toString();
    }
}