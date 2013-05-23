package mobi.nowtechnologies.server.job.executor;

import java.util.Map;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.shared.log.LogUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

public class PendingPaymentExecutor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PendingPaymentExecutor.class);
	
	private Map<String, PaymentSystemService> paymentSystems;
	
	/**
	 * Adding a pending payment to task executor,
	 * where PendingPayment according to PaymentSystem will be carried out
	 * @param pendingPayment - the payment that should be carried out
	 */
	public void execute(PendingPayment pendingPayment) {
		this.executor.execute(new PendingPaymentTask(pendingPayment));
	}
	
	private class PendingPaymentTask implements Runnable {
		private PendingPayment pendingPayment;
		
		public PendingPaymentTask(PendingPayment pendingPayment) {
			this.pendingPayment = pendingPayment;
		}

		@Override
		public void run() {
			try {
				User user = pendingPayment.getUser();
				LogUtils.putPaymentMDC(String.valueOf(user.getId()), String.valueOf(user.getUserName()), String.valueOf(user.getUserGroup().getCommunity().getName()), this.getClass());
				LogUtils.put3rdParyRequestProfileSpecificMDC(user.getUserName(), user.getMobile(), user.getId());
				
			LOGGER.info("Starting payment transaction with txId:{} ...", pendingPayment.getInternalTxId());
			LOGGER.info("for user {} with balance {}", user.getId(), user.getSubBalance());
			PaymentSystemService paymentSystemService = paymentSystems.get(pendingPayment.getPaymentSystem());
			paymentSystemService.startPayment(pendingPayment);
			LOGGER.info("Transaction for pending payment with tx:{} has been sent to external payment system {}", pendingPayment.getInternalTxId(), pendingPayment.getPaymentSystem());
			} catch (Exception e) {
				LOGGER.error("Error dring pending payment transaction with tx:{}, payment system {}", pendingPayment.getInternalTxId(), pendingPayment.getPaymentSystem());
				LOGGER.error("Pending payment error caused by: {}", e);
			}finally{
				LOGGER.info("Payment transaction finished");
				LogUtils.removeSpecificMDC();
				LogUtils.removePaymentMDC();
				LogUtils.remove3rdParyRequestProfileMDC();
			}
		}
	}
	
	private TaskExecutor executor;

	public void setExecutor(TaskExecutor executor) {
		this.executor = executor;
	}

	public void setPaymentSystems(Map<String, PaymentSystemService> paymentSystems) {
		this.paymentSystems = paymentSystems;
	}	
}