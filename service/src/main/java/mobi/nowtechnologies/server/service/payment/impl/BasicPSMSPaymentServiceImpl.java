package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PSMSPaymentService;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/15/13
 * Time: 2:54 PM
 */
public abstract class BasicPSMSPaymentServiceImpl<T extends PSMSPaymentDetails> extends AbstractPaymentSystemService implements PSMSPaymentService<T>{
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Class<T> paymentDetailsClass;

    protected BasicPSMSPaymentServiceImpl(Class<T> clazz){
        this.paymentDetailsClass = clazz;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void startPayment(PendingPayment pendingPayment) throws Exception {
        LOGGER.debug("input parameters pendingPayment: [{}]", pendingPayment);
        final User user = pendingPayment.getUser();
        final T paymentDetails = (T)pendingPayment.getPaymentDetails();

        PaymentSystemResponse response = makePayment(pendingPayment);

        LOGGER.info("Sent request to external system with pending payment [{}] and received response [{}]", pendingPayment.getI(), response);
        if (!response.isSuccessful()) {
            LOGGER.error("External exception while making payment psms transaction for user with id: [{}] and paymentDetails [{}] ", new Object[]{user.getId(), paymentDetails});
        }

        commitPayment(pendingPayment, response);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T commitPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException {
        LOGGER.info("Committing o2Psms payment details for user {} ...", user.getUserName());

        T details = (T) user.getPaymentDetails(paymentDetailsClass);

        if(details == null)
            details = createPaymentDetails(user, paymentPolicy);

        details = (T) super.commitPaymentDetails(user, details);

        paymentDetailsService.update(details);
        userService.updateUser(user);

        LOGGER.info("Done commitment of psms payment details [{}] for user [{}]", new Object[]{details, user.getUserName()});

        return details;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public T createPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException {
        LOGGER.info("Start creation psms payment details for user [{}] and paymentPolicyId [{}]...", new Object[]{user.getUserName(), paymentPolicy.getId()});

        T details = newPSMSPaymentDetails();
        details.setPaymentPolicy(paymentPolicy);
        details.setPhoneNumber(user.getMobile());
        details.setCreationTimestampMillis(Utils.getEpochMillis());

        paymentDetailsService.update(details);
        userService.updateUser(user);

        LOGGER.info("Done creation of psms payment details [{}] for user [{}]", new Object[]{details, user.getUserName()});

        return details;
    }
    
    protected abstract PaymentSystemResponse makePayment(PendingPayment pendingPayment);

    protected T newPSMSPaymentDetails(){
        try {
            return paymentDetailsClass.newInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }
}
