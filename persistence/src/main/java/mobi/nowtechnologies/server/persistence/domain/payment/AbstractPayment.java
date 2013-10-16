package mobi.nowtechnologies.server.persistence.domain.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.web.PaymentHistoryItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractPayment {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPayment.class);
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long i;
	
	private String internalTxId;
	
	private String externalTxId;
	
	private BigDecimal amount;

	private long timestamp;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="userId")
	private User user;
	
	@Column(insertable=false, updatable=false)
	private int userId;
	
	private int subweeks;
	
	private Integer offerId;
	
	private String currencyISO;
	
	private String paymentSystem;
	
	@Enumerated(EnumType.STRING)
	private PaymentDetailsType type;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="paymentDetailsId")
	private PaymentDetails paymentDetails;
	
	@Column(insertable=false, updatable=false)
	private Long paymentDetailsId; 
	
	public AbstractPayment() {
		internalTxId = UUID.randomUUID().toString().replaceAll("-",	"");
	}

	public Long getI() {
		return i;
	}

	public void setI(Long i) {
		this.i = i;
	}

	public String getInternalTxId() {
		return internalTxId;
	}

	public void setInternalTxId(String internalTxId) {
		this.internalTxId = internalTxId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		if(user!=null) userId=user.getId();
	}

	public int getSubweeks() {
		return subweeks;
	}

	public void setSubweeks(int subweeks) {
		this.subweeks = subweeks;
	}

	public String getCurrencyISO() {
		return currencyISO;
	}

	public void setCurrencyISO(String currencyISO) {
		this.currencyISO = currencyISO;
	}

	public String getPaymentSystem() {
		return paymentSystem;
	}

	public void setPaymentSystem(String paymentSystem) {
		this.paymentSystem = paymentSystem;
	}

	public PaymentDetailsType getType() {
		return type;
	}

	public void setType(PaymentDetailsType type) {
		this.type = type;
	}

	public String getExternalTxId() {
		return externalTxId;
	}

	public void setExternalTxId(String externalTxId) {
		this.externalTxId = externalTxId;
	}

	public int getUserId() {
		return userId;
	}

	public Integer getOfferId() {
		return offerId;
	}

	public void setOfferId(Integer offerId) {
		this.offerId = offerId;
	}

	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
		if (paymentDetails != null)
			paymentDetailsId = paymentDetails.getI();
	}

	@Override
	public String toString() {
		return "amount=" + amount + ", currencyISO=" + currencyISO + ", externalTxId=" + externalTxId + ", i=" + i + ", internalTxId="
				+ internalTxId + ", paymentSystem=" + paymentSystem + ", subweeks=" + subweeks + ", timestamp=" + timestamp + ", type=" + type + ", userId="
				+ userId +", paymentDetailsId="+paymentDetailsId +", offerId="+offerId;
	}

	public PaymentHistoryItemDto toPaymentHistoryItemDto() {
		PaymentHistoryItemDto paymentHistoryItemDto = new PaymentHistoryItemDto();
		
		paymentHistoryItemDto.setTransactionId(internalTxId);
		paymentHistoryItemDto.setDate(new Date(timestamp));
		if (type.equals(PaymentDetailsType.FIRST))
			paymentHistoryItemDto.setDescription("1");
		else 
			paymentHistoryItemDto.setDescription("2");
		paymentHistoryItemDto.setWeeks(subweeks);
		paymentHistoryItemDto.setPaymentMethod(paymentSystem);
		paymentHistoryItemDto.setAmount(amount);
		
		LOGGER.debug("Output parameter paymentHistoryItemDto=[{}]", paymentHistoryItemDto);
		return paymentHistoryItemDto;
	}

	public static List<PaymentHistoryItemDto> toPaymentHistoryItemDto(List<AbstractPayment> abstractPayments) {
		LOGGER.debug("input parameters abstractPayments: [{}]", abstractPayments);
		
		List<PaymentHistoryItemDto> paymentHistoryItemDtos = new LinkedList<PaymentHistoryItemDto>();
		for (AbstractPayment abstractPayment : abstractPayments) {
			paymentHistoryItemDtos.add(abstractPayment.toPaymentHistoryItemDto());
		}
		
		LOGGER.debug("Output parameter paymentHistoryItemDtos=[{}]", paymentHistoryItemDtos);
		return paymentHistoryItemDtos;
	}
}