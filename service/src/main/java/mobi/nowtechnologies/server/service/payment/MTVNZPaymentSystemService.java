package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.NZProviderType;
import mobi.nowtechnologies.server.persistence.domain.payment.PSMSPaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.Period;
import mobi.nowtechnologies.server.persistence.repository.PendingPaymentRepository;
import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoProvider;
import mobi.nowtechnologies.server.service.nz.NZSubscriberResult;
import mobi.nowtechnologies.server.service.nz.ProviderConnectionException;
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
public class MTVNZPaymentSystemService extends BasicPSMSPaymentServiceImpl {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private NZSubscriberInfoProvider nzSubscriberInfoProvider;
    private VFNZSMSGatewayServiceImpl smsGatewayService;
    private CommunityResourceBundleMessageSource messageSource;
    private PendingPaymentRepository pendingPaymentRepository;

    @Override
    @Transactional
    public void startPayment(PendingPayment pendingPayment) throws Exception {
        logger.info("Start payment: {}", pendingPayment);
        final PSMSPaymentDetails paymentDetails = (PSMSPaymentDetails) pendingPayment.getPaymentDetails();
        final String phoneNumber = paymentDetails.getPhoneNumber();
        final String normalizePhoneNumber = normalizePhoneNumber(phoneNumber);

        try {
            NZSubscriberResult subscriberResult = nzSubscriberInfoProvider.getSubscriberResult(normalizePhoneNumber);
            NZProviderType nzProviderType = NZProviderType.of(subscriberResult.getProviderName());
            if(nzProviderType != NZProviderType.VODAFONE){
                logger.info("User {} is not VF subscriber", normalizePhoneNumber);
                finishPaymentForNotSubscriber(pendingPayment);
                return;
            }
            String shortCode = paymentDetails.getPaymentPolicy().getShortCode();
            String message = getPaymentNotificationText(pendingPayment, shortCode);

            SMSResponse smsResponse = smsGatewayService.send(phoneNumber, message, shortCode, SMSCDeliveryReceipt.SUCCESS_FAILURE, getExpireMillis());
            if(smsResponse.isSuccessful()){
                logger.info("Payment request {} has been sent successfully", pendingPayment);
            } else {
                logger.warn("Could not send SMS payment request for {} : {}, skip current attempt", phoneNumber, smsResponse.getDescriptionError());
                skipCurrentPaymentAttempt(pendingPayment, smsResponse.getDescriptionError());
            }
        } catch (ProviderConnectionException e) {
            logger.warn("Connection problem to NZ subscriber service: {}", e.getMessage());
            skipCurrentPaymentAttempt(pendingPayment, e.getMessage());
        } catch (MsisdnNotFoundException e) {
            logger.warn("User {} with phone number {} was not found", pendingPayment.getUserId(), normalizePhoneNumber);
            finishPaymentForUnknownSubscriber(pendingPayment);
        }

    }

    @Override
    public PaymentSystemResponse getExpiredResponse() {
        return MTVNZResponse.errorResponse(null, null, "MTVNZ payment was expired");
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

    private void finishPaymentForNotSubscriber(PendingPayment pendingPayment) {
        processPaymentForWrongSubscriber(pendingPayment, "User does not belong to VF");
    }

    private void finishPaymentForUnknownSubscriber(PendingPayment pendingPayment) {
        processPaymentForWrongSubscriber(pendingPayment, "MSISDN not found");
    }

    private void processPaymentForWrongSubscriber(PendingPayment pendingPayment, String reason) {
        pendingPayment.getPaymentDetails().completedWithError(reason);

        paymentDetailsRepository.save(pendingPayment.getPaymentDetails());

        userService.unsubscribeUser(pendingPayment.getUser(), reason);

        pendingPaymentRepository.delete(pendingPayment);

        getPaymentEventNotifier().onUnsubscribe(pendingPayment.getUser());
    }

    private String getPaymentNotificationText(PendingPayment pendingPayment, String shortCode) {
        Period period = pendingPayment.getPeriod();
        String communityRewriteUrl = pendingPayment.getUser().getCommunityRewriteUrl();
        String key = "sms.mtvnzPsms.payment.text." + shortCode + "." + period.getDurationUnit();

        Object[] args = {preFormatCurrency(pendingPayment.getAmount()), period.getDuration()};

        return messageSource.getMessage(communityRewriteUrl, key, args, null);
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if(phoneNumber.startsWith("+")) {
            return phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    public void setNzSubscriberInfoProvider(NZSubscriberInfoProvider nzSubscriberInfoProvider) {
        this.nzSubscriberInfoProvider = nzSubscriberInfoProvider;
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
