package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;

import org.springframework.beans.BeanUtils;

@Entity
@Table(name="tb_submittedPayments")
@NamedQuery(name=SubmittedPayment.NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC, query = "select submittedPayment from SubmittedPayment submittedPayment where submittedPayment.userId=? and submittedPayment.status='SUCCESSFUL' order by submittedPayment.timestamp desc")
public class SubmittedPayment extends AbstractPayment {
	
	public static final String NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC = "NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC";

	private String descriptionError;
	
	@Enumerated(EnumType.STRING)
	private PaymentDetailsStatus status;

	public String getDescriptionError() {
		return descriptionError;
	}

	public void setDescriptionError(String descriptionError) {
		this.descriptionError = descriptionError;
	}

	public PaymentDetailsStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentDetailsStatus status) {
		this.status = status;
	}
	
	public static SubmittedPayment valueOf(PendingPayment pendingPayment) {
		SubmittedPayment payment = new SubmittedPayment();
			BeanUtils.copyProperties(pendingPayment, payment);
			payment.setI(null);
		return payment;
	}
}