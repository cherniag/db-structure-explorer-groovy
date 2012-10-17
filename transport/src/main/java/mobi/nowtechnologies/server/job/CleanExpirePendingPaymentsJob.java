package mobi.nowtechnologies.server.job;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.SubmittedPayment;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanExpirePendingPaymentsJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CleanExpirePendingPaymentsJob.class);
	
	private PendingPaymentService pendingPaymentService;
	
	private Map<String, PaymentSystemService> paymentSystems;
	
	public void execute() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Clean Expire Pending Payments Job ...");
			List<PendingPayment> expiredPendingPayments = pendingPaymentService.getExpiredPendingPayments();
			List<SubmittedPayment> cleanedPayments = new LinkedList<SubmittedPayment>();
			for (PendingPayment pendingPayment : expiredPendingPayments) {
				PaymentSystemService paymentSystemService = paymentSystems.get(pendingPayment.getPaymentSystem());
				try {
					cleanedPayments.add(paymentSystemService.commitPayment(pendingPayment, paymentSystemService.getExpiredResponse()));
				} catch (Exception e) {
					LOGGER.warn("Can't remove expired pending payment with id:{} and txId:{}", pendingPayment.getI(), pendingPayment.getInternalTxId());
				}
			}
			LOGGER.info("[DONE] Clean Expire Pending Payments Job has been finished with {} pending payments removed", cleanedPayments.size());
		} catch (Exception e) {
			LOGGER.error("Error while cleaning expired pending payments. {}", e);
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	public void setPendingPaymentService(PendingPaymentService pendingPaymentService) {
		this.pendingPaymentService = pendingPaymentService;
	}

	public void setPaymentSystems(Map<String, PaymentSystemService> paymentSystems) {
		this.paymentSystems = paymentSystems;
	}
	
}