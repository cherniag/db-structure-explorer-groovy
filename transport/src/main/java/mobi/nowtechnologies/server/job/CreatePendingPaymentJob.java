package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.job.executor.PendingPaymentExecutor;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static mobi.nowtechnologies.server.service.payment.response.InternalErrorResponse.createErrorResponse;

public class CreatePendingPaymentJob {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreatePendingPaymentJob.class);

	private PendingPaymentService pendingPaymentService;

	private PendingPaymentExecutor executor;

    private Map<String, PaymentSystemService> paymentSystems;

	public void execute() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Pending Payment job...");
			List<PendingPayment> createPendingPayments = pendingPaymentService.createPendingPayments();
			for (PendingPayment pendingPayment : createPendingPayments) {
				LOGGER.info("Adding pending payment with txId {} to pool", pendingPayment.getInternalTxId());
                try {
                    executor.execute(pendingPayment);
                } catch (Exception e) {
                    LOGGER.error("Error while adding pending payments to executor. {}", e);
                    PaymentSystemService paymentSystemService = paymentSystems.get(pendingPayment.getPaymentSystem());
                    paymentSystemService.commitPayment(pendingPayment, createErrorResponse(e.getClass().getSimpleName()));
                }
            }
			LOGGER.info("[DONE] Pending Payment job finished with {} pending payments added to queue", createPendingPayments.size());
		} catch (Exception e) {
			LOGGER.error("Error while running Pending Payment job", e);
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	public void setPendingPaymentService(
			PendingPaymentService pendingPaymentService) {
		this.pendingPaymentService = pendingPaymentService;
	}

	public void setExecutor(PendingPaymentExecutor executor) {
		this.executor = executor;
	}

    public void setPaymentSystems(Map<String, PaymentSystemService> paymentSystems) {
        this.paymentSystems = paymentSystems;
    }
}