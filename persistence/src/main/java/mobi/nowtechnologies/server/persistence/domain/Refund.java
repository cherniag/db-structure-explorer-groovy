package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.shared.enums.ActionReason;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 12:57
 */
@Entity
@Table(name = "refund")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    public User user;

    @Column(name = "log_time_millis")
    public long logTimeMillis;

    @Column(name = "next_sub_payment_millis")
    public long nextSubPaymentMillis;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "payment_details_id")
    public PaymentDetails paymentDetails;

    @Column(name = "reason", nullable = false)
    @Enumerated(EnumType.STRING)
    public ActionReason actionReason;

    public Integer getUserId() {
        Integer userId = null;
        if (user != null) {
            userId = user.getId();
        }
        return userId;
    }

    public Long getPaymentDetailsId() {
        Long paymentDetailsId = null;
        if (paymentDetails != null) {
            paymentDetailsId = paymentDetails.getI();
        }
        return paymentDetailsId;
    }

    public static class NullObjectRefund extends Refund {}

    public static Refund nullObject(){
        return new NullObjectRefund();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("getUserId()", getUserId())
                .append("getPaymentDetailsId()", getPaymentDetailsId())
                .append("logTimeMillis", logTimeMillis)
                .append("nextSubPaymentMillis", nextSubPaymentMillis)
                .append("actionReason", actionReason)
                .toString();
    }
}
