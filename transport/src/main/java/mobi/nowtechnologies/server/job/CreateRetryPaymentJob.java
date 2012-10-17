package mobi.nowtechnologies.server.job;

import java.util.List;

import mobi.nowtechnologies.server.job.executor.PendingPaymentExecutor;
import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.service.payment.PendingPaymentService;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateRetryPaymentJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateRetryPaymentJob.class);
	
	private PendingPaymentService pendingPaymentService;
	
	private PendingPaymentExecutor executor;
	
	public void execute() {
		try {
			LogUtils.putClassNameMDC(this.getClass());
			LOGGER.info("[START] Retry Payment job...");
			
			List<PendingPayment> createRetryPayments = pendingPaymentService.createRetryPayments();
			
			for(PendingPayment pendingPayment : createRetryPayments) {
				executor.execute(pendingPayment);
			}
			LOGGER.info("[DONE] Retry Payment job has been finished with {} pending payment(s) added to queue", createRetryPayments.size());
		} catch (Exception e) {
			LOGGER.error("Error while adding retries payments. {}", e);
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

}