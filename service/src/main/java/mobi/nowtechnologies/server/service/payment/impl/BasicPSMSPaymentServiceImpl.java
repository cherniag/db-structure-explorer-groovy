package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.*;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.PSMSPaymentService;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static mobi.nowtechnologies.server.shared.Utils.preFormatCurrency;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/15/13
 * Time: 2:54 PM
 */
public abstract class BasicPSMSPaymentServiceImpl<T extends PSMSPaymentDetails> extends AbstractPaymentSystemService implements PSMSPaymentService<T>{
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private Class<T> paymentDetailsClass;
    protected CommunityResourceBundleMessageSource messageSource;

    protected BasicPSMSPaymentServiceImpl(Class<T> clazz){
        this.paymentDetailsClass = clazz;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @Transactional(propagation = REQUIRED)
    public void startPayment(final PendingPayment pendingPayment) throws Exception {
        final User user = pendingPayment.getUser();
        final PSMSPaymentDetails paymentDetails = (PSMSPaymentDetails)pendingPayment.getPaymentDetails();
        final PaymentPolicy paymentPolicy = paymentDetails.getPaymentPolicy();
        Community community = user.getUserGroup().getCommunity();

        LOGGER.debug("Start psms payment pendingPayment: [{}]", pendingPayment);


        Boolean smsNotify = Boolean.valueOf(messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms."+paymentPolicy.getPaymentType()+".send",
                null, null));

        String message = smsNotify ? messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms."+paymentPolicy.getPaymentType(),
                new Object[]{community.getDisplayName(), preFormatCurrency(pendingPayment.getAmount()), pendingPayment.getSubweeks(), paymentPolicy.getShortCode()}, null) : null;

        PaymentSystemResponse response = makePayment(pendingPayment, message);

        if(!response.isFuture()){
            commitPayment(pendingPayment, response);
        }
    }

    @Override
    @Transactional(propagation = REQUIRED)
    public SubmittedPayment commitPayment(PendingPayment pendingPayment, PaymentSystemResponse response) {
        final User user = pendingPayment.getUser();
        final T paymentDetails = (T)pendingPayment.getPaymentDetails();

        LOGGER.info("Sent request to [{}] external system with pending payment [{}] and received response [{}]", new Object[]{paymentDetails.getPaymentType(), pendingPayment.getI(), response});
        if (!response.isSuccessful()) {
            LOGGER.error("External exception while making payment [{}] psms transaction for user with id: [{}] and paymentDetails ", new Object[]{paymentDetails.getPaymentType(), user.getId()});
        }

        return super.commitPayment(pendingPayment, response);
    }

    @Transactional(propagation = REQUIRED)
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

    @Transactional(propagation = REQUIRED)
    public T createPaymentDetails(User user, PaymentPolicy paymentPolicy) throws ServiceException {
        LOGGER.info("Start creation psms payment details for user [{}] and paymentPolicyId [{}]...", new Object[]{user.getUserName(), paymentPolicy.getId()});

        T details = newPSMSPaymentDetails();
        details.setPaymentPolicy(paymentPolicy);
        details.setPhoneNumber(user.getMobile());
        details.setCreationTimestampMillis(Utils.getEpochMillis());
        details.setOwner(user);

        paymentDetailsService.update(details);

        LOGGER.info("Done creation of psms payment details [{}] for user [{}]", new Object[]{details, user.getUserName()});

        return details;
    }
    
    protected abstract PaymentSystemResponse makePayment(PendingPayment pendingPayment, String message);

    protected T newPSMSPaymentDetails(){
        try {
            return paymentDetailsClass.newInstance();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }
}
