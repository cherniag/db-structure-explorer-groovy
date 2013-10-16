package mobi.nowtechnologies.server.persistence.domain;

import mobi.nowtechnologies.server.persistence.domain.payment.AbstractPayment;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="tb_pendingPayments")
@NamedQueries({
	@NamedQuery(name=PendingPayment.NQ_GET_EXPIRED_PENDING_PAYMENTS, query="select pp from PendingPayment pp where pp.expireTimeMillis < ?")
})
public class PendingPayment extends AbstractPayment {
	public static final String NQ_GET_EXPIRED_PENDING_PAYMENTS = "NQ_GET_EXPIRED_PENDING_PAYMENTS";
	
	private long expireTimeMillis;

	public long getExpireTimeMillis() {
		return expireTimeMillis;
	}

	public void setExpireTimeMillis(long expireTimeMillis) {
		this.expireTimeMillis = expireTimeMillis;
	}
}