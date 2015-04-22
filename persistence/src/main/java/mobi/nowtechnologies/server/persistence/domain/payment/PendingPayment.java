package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "tb_pendingPayments")
public class PendingPayment extends AbstractPayment {

    private long expireTimeMillis;

    public long getExpireTimeMillis() {
        return expireTimeMillis;
    }

    public void setExpireTimeMillis(long expireTimeMillis) {
        this.expireTimeMillis = expireTimeMillis;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .appendSuper(super.toString())
            .append("expireTimeMillis", expireTimeMillis)
            .toString();
    }
}