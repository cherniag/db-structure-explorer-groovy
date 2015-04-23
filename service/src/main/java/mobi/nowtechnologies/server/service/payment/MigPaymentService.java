package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.exception.ServiceException;

public interface MigPaymentService extends PaymentSystemService {

    public static final String MIG_DELIVERED = "0";
    public static final String MIG_NOT_DELIVERED = "2";

    public SubmittedPayment commitPayment(String messageId, String status, String descriptionError) throws ServiceException;

    public boolean sendPin(String numbers, String message) throws ServiceException;

}