package mobi.nowtechnologies.server.service.payment.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.MigPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.AbstractPaymentSystemService;
import mobi.nowtechnologies.server.service.payment.MigPaymentService;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class MigPaymentServiceImpl extends AbstractPaymentSystemService implements MigPaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigPaymentServiceImpl.class);

    private MigHttpService httpService;

    private CommunityResourceBundleMessageSource messageSource;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void startPayment(PendingPayment pendingPayment) throws ServiceException {
        MigPaymentDetails currentPaymentDetails = (MigPaymentDetails) pendingPayment.getUser().getCurrentPaymentDetails();
        PaymentPolicy paymentPolicy = currentPaymentDetails.getPaymentPolicy();
        Community community = pendingPayment.getUser().getCommunity();

        Period period = paymentPolicy.getPeriod();

        String message = messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms.psms",
                                                  new Object[] {community.getDisplayName(), paymentPolicy.getSubcost(), period.getDuration(), period.getDurationUnit(), paymentPolicy.getShortCode()},
                                                  null);

        String internalTxId = Utils.getBigRandomInt().toString();
        MigResponse response = httpService.makePremiumSMSRequest(internalTxId, paymentPolicy.getShortCode(), currentPaymentDetails.getMigPhoneNumber(), message);

        pendingPayment.setInternalTxId(internalTxId);
        pendingPayment.setExternalTxId(response.getExternalTxId());
        getPendingPaymentRepository().save(pendingPayment);
        LOGGER.info("Sent request to MIG with pending payment {}. {}", pendingPayment.getI(), response);
        if (!response.isSuccessful()) {
            LOGGER.error("External exception while making payment transaction with MIG for user {} ", pendingPayment.getUser().getId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public SubmittedPayment commitPayment(String messageId, String status, String descriptionError) throws ServiceException {
        PaymentSystemResponse response = null;
        try {
            PendingPayment pendingPayment = getPendingPaymentRepository().findByExternalTransactionId(messageId);
            if (null != pendingPayment) {
                User user = pendingPayment.getUser();
                LogUtils.putPaymentMDC(String.valueOf(user.getId()), String.valueOf(user.getUserName()), String.valueOf(user.getUserGroup().getCommunity().getName()), this.getClass());

                LOGGER.info("Committing payment transaction with MIG for PendingPayment with internalTxId  {} ", messageId);

                if (MIG_DELIVERED.equals(status)) {
                    response = MigResponse.successfulMigResponse();
                } else {
                    response = MigResponse.failMigResponse(
                        "Pending payment with internalTxId ".concat(messageId).concat(" was not delivered to user cause: ").concat(descriptionError).concat(" Status code ").concat(status));
                }
                LOGGER.info("MIG responded {} for pending payment {}", response, messageId);
                if (null != pendingPayment && null != response) {
                    return super.commitPayment(pendingPayment, response);
                }
            } else {
                LOGGER.info("MIG response for free sms {} ", messageId);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while committing MIG payment", e);
            return null;
        } finally {
            LogUtils.removePaymentMDC();
        }
        LOGGER.warn("Couldn't find PendingPayment with internalTxId  {} in MIG callback.", messageId);
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public MigPaymentDetails createPaymentDetails(String migPhoneNumber, User user, Community community, PaymentPolicy paymentPolicy) throws ServiceException {
        LOGGER.info("Starting creating MIG payment details...");
        MigPaymentDetails paymentDetails = new MigPaymentDetails();
        paymentDetails.setCreationTimestampMillis(System.currentTimeMillis());
        paymentDetails.setLastPaymentStatus(PaymentDetailsStatus.PENDING);
        paymentDetails.resetMadeAttempts();
        paymentDetails.setRetriesOnError(getRetriesOnError());
        paymentDetails.setMigPhoneNumber(migPhoneNumber);
        paymentDetails.setPaymentPolicy(paymentPolicy);
        paymentDetails.setActivated(false);
        paymentDetails.setOwner(user);
        // Storing pin to user
        String pin = Utils.generateRandom4DigitsPIN();
        user.setPin(pin);

        paymentDetails = getPaymentDetailsRepository().save(paymentDetails);
        sendPin(migPhoneNumber, messageSource.getMessage(community.getRewriteUrlParameter().toLowerCase(), "sms.freeMsg", new Object[] {pin}, null));
        LOGGER.info("Free sms with pin code was sent");
        return paymentDetails;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public MigPaymentDetails commitPaymnetDetails(User user, String verificationPin) throws ServiceException {

        LOGGER.info("Verifying pin from mig...");
        if (StringUtils.hasText(verificationPin) && user.getPin().equals(verificationPin)) {

            MigPaymentDetails paymentDetails = (MigPaymentDetails) paymentDetailsService.getPendingPaymentDetails(user.getId());
            user.setPin("");

            paymentDetails = (MigPaymentDetails) super.commitPaymentDetails(user, paymentDetails);

            LOGGER.info("Verification passed. Mig payment details has been created.");
            return paymentDetails;
        }

        LOGGER.info("Incorrect pin for user [{}]", user.getId());
        throw new ServiceException("Incorrect pin");
    }

    @Override
    public boolean sendPin(String numbers, String message) throws ServiceException {
        MigResponse response = httpService.makeFreeSMSRequest(numbers, message);
        if (response.isSuccessful()) {
            return true;
        }
        LOGGER.error("Problem while sending free sms. Error: {}", response.getDescriptionError());
        return false;
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return MigResponse.failMigResponse("Mig pending payment has been expired");
    }

    public void setHttpService(MigHttpService httpService) {
        this.httpService = httpService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}