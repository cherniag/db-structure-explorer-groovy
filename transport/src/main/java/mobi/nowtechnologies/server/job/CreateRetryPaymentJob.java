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

public class CreateRetryPaymentJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateRetryPaymentJob.class);
	
	private PendingPaymentService pendingPaymentService;
	
	private PendingPaymentExecutor executor;

    private Map<String, PaymentSystemService> paymentSystems;
	
	public void execute() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Retry Payment job...");
			
			List<PendingPayment> createRetryPayments = pendingPaymentService.createRetryPayments();
			
			for(PendingPayment pendingPayment : createRetryPayments) {
                try {
                    executor.execute(pendingPayment);
                } catch (Exception e) {
                    LOGGER.error("Error while adding retries payments. {}", e);
                    PaymentSystemService paymentSystemService = paymentSystems.get(pendingPayment.getPaymentSystem());
                    paymentSystemService.commitPayment(pendingPayment, createErrorResponse(e.getClass().getSimpleName()));
                }
            }
			LOGGER.info("[DONE] Retry Payment job has been finished with {} pending payment(s) added to queue", createRetryPayments.size());
		} catch (Exception e) {
			LOGGER.error("Error while running Retry Payment job. {}", e);
		} finally {
			LogUtils.removeClassNameMDC();
		}
	}

	public void setPendingPaymentService(PendingPaymentService pendingPaymentService) {
		this.pendingPaymentService = pendingPaymentService;
	}

	public void setExecutor(PendingPaymentExecutor executor) {
		this.executor = executor;
	}

    public void setPaymentSystems(Map<String, PaymentSystemService> paymentSystems) {
        this.paymentSystems = paymentSystems;
    }
}