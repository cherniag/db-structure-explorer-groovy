package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.payment.impl.BasicPSMSPaymentServiceImpl;
import mobi.nowtechnologies.server.service.payment.response.PaymentSystemResponse;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.service.vodafone.impl.VFNZSMSGatewayServiceImpl;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.shared.Utils.preFormatCurrency;

import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

/**
 * Author: Gennadii Cherniaiev
 * Date: 2/26/2015
 */
public class MTVNZPaymentHelper extends BasicPSMSPaymentServiceImpl {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private VFNZSMSGatewayServiceImpl smsGatewayService;
    private CommunityResourceBundleMessageSource messageSource;
    private PendingPaymentRepository pendingPaymentRepository;

    @Override
    @Transactional
    public void startPayment(PendingPayment pendingPayment) throws Exception {
        logger.info("Start payment: {}", pendingPayment);
        final PSMSPaymentDetails paymentDetails = (PSMSPaymentDetails) pendingPayment.getPaymentDetails();
        final String phoneNumber = paymentDetails.getPhoneNumber();

        String shortCode = paymentDetails.getPaymentPolicy().getShortCode();
        String message = getPaymentNotificationText(pendingPayment, shortCode);

        SMSResponse smsResponse = smsGatewayService.send(phoneNumber, message, shortCode, SMSCDeliveryReceipt.SUCCESS_FAILURE, getExpireMillis());
        if(smsResponse.isSuccessful()){
            logger.info("Payment request {} has been sent successfully", pendingPayment);
        } else {
            logger.warn("Could not send SMS payment request for {} : {}, skip current attempt", phoneNumber, smsResponse.getDescriptionError());
            skipAttemptWithoutRetryIncrement(pendingPayment, smsResponse.getDescriptionError());
        }
    }


    @Transactional
    public void finishPaymentForNotVFUser(PendingPayment pendingPayment, String reason) {
        pendingPayment.getPaymentDetails().completedWithError(reason);

        paymentDetailsRepository.save(pendingPayment.getPaymentDetails());

        userService.unsubscribeUser(pendingPayment.getUser(), reason);

        pendingPaymentRepository.delete(pendingPayment);

        getPaymentEventNotifier().onUnsubscribe(pendingPayment.getUser());
    }

    @Transactional
    public void skipAttemptWithoutRetryIncrement(PendingPayment pendingPayment, String reason){
        PSMSPaymentDetails paymentDetails = (PSMSPaymentDetails) pendingPayment.getPaymentDetails();
        MTVNZResponse unavailableResponse = MTVNZResponse.serviceUnavailableResponse(paymentDetails.getPhoneNumber(), reason);
        super.commitPayment(pendingPayment, unavailableResponse);
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return MTVNZResponse.errorResponse(null, null, "MTVNZ payment was expired");
    }

    @Override
    protected PaymentSystemResponse makePayment(PendingPayment pendingPayment, String message) {
        throw new UnsupportedOperationException("");
    }

    private String getPaymentNotificationText(PendingPayment pendingPayment, String shortCode) {
        Period period = pendingPayment.getPeriod();
        String communityRewriteUrl = pendingPayment.getUser().getCommunityRewriteUrl();
        String key = "sms.mtvnzPsms.payment.text." + shortCode + "." + period.getDurationUnit();

        Object[] args = {preFormatCurrency(pendingPayment.getAmount()), period.getDuration()};

        return messageSource.getMessage(communityRewriteUrl, key, args, null);
    }

    public void setSmsGatewayService(VFNZSMSGatewayServiceImpl smsGatewayService) {
        this.smsGatewayService = smsGatewayService;
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setPendingPaymentRepository(PendingPaymentRepository pendingPaymentRepository) {
        this.pendingPaymentRepository = pendingPaymentRepository;
    }

}
