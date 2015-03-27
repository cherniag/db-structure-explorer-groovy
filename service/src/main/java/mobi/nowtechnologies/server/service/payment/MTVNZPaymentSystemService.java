package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.ProviderNotAvailableException;
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
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * Author: Gennadii Cherniaiev
 * Date: 2/26/2015
 */
public class MTVNZPaymentSystemService extends BasicPSMSPaymentServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(MTVNZPaymentSystemService.class);

    private NZSubscriberInfoService nzSubscriberInfoService;
    private VFNZSMSGatewayServiceImpl smsGatewayService;
    private CommunityResourceBundleMessageSource messageSource;
    private PendingPaymentRepository pendingPaymentRepository;

    @Override
    @Transactional(propagation = REQUIRED)
    public void startPayment(PendingPayment pendingPayment) throws Exception {
        LOGGER.info("Start payment: {}", pendingPayment);
        final PSMSPaymentDetails paymentDetails = (PSMSPaymentDetails) pendingPayment.getPaymentDetails();
        final String phoneNumber = paymentDetails.getPhoneNumber();
        try {
            boolean belongs = nzSubscriberInfoService.belongs(phoneNumber);
            if(!belongs){
                LOGGER.info("User {} is not VF subscriber", phoneNumber);
                processPaymentFromNotSubscriber(pendingPayment);
                return;
            }
            String shortCode = paymentDetails.getPaymentPolicy().getShortCode();
            String message = getPaymentNotificationText(pendingPayment, shortCode);

            SMSResponse smsResponse = smsGatewayService.send(phoneNumber, message, shortCode, SMSCDeliveryReceipt.SUCCESS_FAILURE, getExpireMillis());
            if(smsResponse.isSuccessful()){
                LOGGER.info("Payment request {} has been sent successfully", pendingPayment);
            } else {
                LOGGER.warn("Could not send SMS payment request : {}, skip current attempt", smsResponse.getDescriptionError());
                skipCurrentPaymentAttempt(pendingPayment, smsResponse.getDescriptionError());
            }
        } catch (ProviderNotAvailableException e) {
            LOGGER.warn("NZ subscriber service is not available: {}", e.getMessage());
            skipCurrentPaymentAttempt(pendingPayment, e.getMessage());
        }

    }

    @Override
    protected PaymentSystemResponse makePayment(PendingPayment pendingPayment, String message) {
        throw new UnsupportedOperationException("");
    }

    private void skipCurrentPaymentAttempt(PendingPayment pendingPayment, String reason) {
        PSMSPaymentDetails paymentDetails = (PSMSPaymentDetails) pendingPayment.getPaymentDetails();
        MTVNZResponse serviceUnavailableResponse = MTVNZResponse.serviceUnavailableResponse(paymentDetails.getPhoneNumber(), reason);
        super.commitPayment(pendingPayment, serviceUnavailableResponse);
    }

    private void processPaymentFromNotSubscriber(PendingPayment pendingPayment) {
        String reason = "User does not belong to VF";

        paymentDetailsService.setErrorStatus(pendingPayment.getPaymentDetails(), reason, null);

        userService.unsubscribeUser(pendingPayment.getUser(), reason);

        pendingPaymentRepository.delete(pendingPayment);

        getPaymentEventNotifier().onUnsubscribe(pendingPayment.getUser());
    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return MTVNZResponse.errorResponse(null, null, "MTVNZ payment was expired");
    }

    private String getPaymentNotificationText(PendingPayment pendingPayment, String shortCode) {
        Period period = pendingPayment.getPeriod();
        String communityRewriteUrl = pendingPayment.getUser().getCommunityRewriteUrl();
        String key = "sms.mtvnzPsms.payment.text." + shortCode + "." + period.getDurationUnit();

        Object[] args = {preFormatCurrency(pendingPayment.getAmount()), period.getDuration()};

        return messageSource.getMessage(communityRewriteUrl, key, args, null);
    }

    public void setNzSubscriberInfoService(NZSubscriberInfoService nzSubscriberInfoService) {
        this.nzSubscriberInfoService = nzSubscriberInfoService;
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
