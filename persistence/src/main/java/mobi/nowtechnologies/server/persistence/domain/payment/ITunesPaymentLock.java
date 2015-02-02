package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.*;

/**
 * Author: Gennadii Cherniaiev
 * Date: 12/10/2014
 */
@Entity
@Table(name = "itunes_payment_lock",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "next_sub_payment"})
)
public class ITunesPaymentLock {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "next_sub_payment")
    private int nextSubPayment;

    public ITunesPaymentLock(int userId, int nextSubPayment) {
        this.userId = userId;
        this.nextSubPayment = nextSubPayment;
    }

    protected ITunesPaymentLock() {
    }

    public int getUserId() {
        return userId;
    }

    public int getNextSubPayment() {
        return nextSubPayment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ITunesPaymentLock)) return false;

        ITunesPaymentLock that = (ITunesPaymentLock) o;

        return nextSubPayment == that.nextSubPayment && userId == that.userId;

    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + nextSubPayment;
        return result;
    }
}

