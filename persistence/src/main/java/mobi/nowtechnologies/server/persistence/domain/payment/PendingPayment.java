package mobi.nowtechnologies.server.persistence.domain.payment;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="tb_pendingPayments")
public class PendingPayment extends AbstractPayment {

	private long expireTimeMillis;

	public long getExpireTimeMillis() {
		return expireTimeMillis;
	}

	public void setExpireTimeMillis(long expireTimeMillis) {
		this.expireTimeMillis = expireTimeMillis;
	}
}