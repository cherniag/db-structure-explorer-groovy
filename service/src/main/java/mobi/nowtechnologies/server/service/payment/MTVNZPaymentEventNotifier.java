package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.TaskService;

/**
 * Author: Gennadii Cherniaiev
 * Date: 3/3/2015
 */
public class MTVNZPaymentEventNotifier implements PaymentEventNotifier {

    private TaskService taskService;

    @Override
    public void onError(PaymentDetails paymentDetails) {
        if(paymentDetails.isCurrentAttemptFailed()){
            taskService.createSendPaymentErrorNotificationTask(paymentDetails.getOwner());
        }
    }

    @Override
    public void onUnsubscribe(User user) {
         taskService.createSendUnsubscribeNotificationTask(user);
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
