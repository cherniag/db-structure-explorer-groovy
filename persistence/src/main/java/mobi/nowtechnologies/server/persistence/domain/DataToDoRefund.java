package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.*;

/**
 * User: Titov Mykhaylo (titov)
 * 15.07.13 12:57
 */
@Entity
@Table(name = "data_to_do_refund")
public class DataToDoRefund {

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

    public static class NullObjectDataToDoRefund extends DataToDoRefund{}

    public static DataToDoRefund nullObject(){
        return new NullObjectDataToDoRefund();
    }

    @Override
    public String toString() {
        return "DataToDoRefund[" +
                "id=" + id +
                ", getUserId()=" + getUserId() +
                ", getPaymentDetailsId()=" + getPaymentDetailsId() +
                ", logTimeMillis=" + logTimeMillis +
                ", nextSubPaymentMillis=" + nextSubPaymentMillis +
                ']';
    }
}
