package mobi.nowtechnologies.server.job.executor;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.service.payment.PaymentSystemService;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import static mobi.nowtechnologies.server.service.payment.response.InternalErrorResponse.createErrorResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.task.TaskExecutor;

public class PendingPaymentExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PendingPaymentExecutor.class);

    private Map<String, PaymentSystemService> paymentSystems = new HashMap<String, PaymentSystemService>();

    private TaskExecutor executor;

    /**
     * Adding a pending payment to task executor, where PendingPayment according to PaymentSystem will be carried out
     *
     * @param pendingPayment - the payment that should be carried out
     */
    public void execute(PendingPayment pendingPayment) {
        final PendingPaymentTask task = new PendingPaymentTask(pendingPayment);

        try {
            executor.execute(task);
        } catch (Exception e) {
            LOGGER.error("Error while adding pending payments to executor. Task: " + task, e);
            PaymentSystemService paymentSystemService = paymentSystems.get(pendingPayment.getPaymentSystem());
            paymentSystemService.commitPayment(pendingPayment, createErrorResponse("Unknown exception"));
        }
    }

    public void setExecutor(TaskExecutor executor) {
        this.executor = executor;
    }

    public void setPaymentSystems(Map<String, PaymentSystemService> paymentSystems) {
        this.paymentSystems.putAll(paymentSystems);
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
                LOGGER.error("Error during pending payment transaction with tx:{}, payment system {}, caused by {}", pendingPayment.getInternalTxId(), pendingPayment.getPaymentSystem(),
                             e.getMessage());
            } finally {
                LOGGER.info("Payment transaction finished");
                LogUtils.removePaymentMDC();
                LogUtils.removeAll3rdParyRequestProfileMDC();
            }
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("i", pendingPayment.getI()).append("userId", pendingPayment.getUserId()).append("paymentSystem", pendingPayment.getPaymentSystem())
                                            .append("internalTxId", pendingPayment.getInternalTxId()).append("timestamp", new Date(pendingPayment.getTimestamp()))
                                            .append("expireMillis", new Date(pendingPayment.getExpireTimeMillis())).toString();
        }
    }
}