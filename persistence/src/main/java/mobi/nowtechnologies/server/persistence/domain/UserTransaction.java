package mobi.nowtechnologies.server.persistence.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.Date;
/**
 * Author: Gennadii Cherniaiev Date: 3/17/2015
 */
@Entity
@Table(name = "user_transactions")
public class UserTransaction {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "create_timestamp")
    private long createTimestamp = new Date().getTime();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "start_timestamp")
    private long startTimestamp;

    @Column(name = "end_timestamp")
    private long endTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private UserTransactionType transactionType;

    @Column(name = "promo_code")
    private String promoCode;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public UserTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(UserTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

}
