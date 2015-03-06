package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessor;
import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Gennadii Cherniaiev
 * Date: 2/26/2015
 */
public class MTVNZPaymentSMSMessageProcessor implements SMSMessageProcessor<MTVNZResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MTVNZPaymentSMSMessageProcessor.class);

    private Set<String> paymentShortCodes = new HashSet<>();
    private MTVNZPaymentSystemService mtvnzPaymentSystemService;
    private MTVNZPaymentResponseParser mtvnzPaymentResponseParser;
    private NZSubscriberInfoRepository nzSubscriberInfoRepository;
    private PendingPaymentService pendingPaymentService;

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return deliverSm.isSmscDeliveryReceipt() && paymentShortCodes.contains(deliverSm.getDestAddress());
    }

    @Override
    public void parserAndProcess(Object data) {
        LOGGER.info("Processing receipt: {}", data);
        DeliverSm deliverSm = (DeliverSm) data;
        final MTVNZResponse mtvnzResponse = mtvnzPaymentResponseParser.parse(deliverSm);
        LOGGER.debug("Parse result: {}", mtvnzResponse);
        process(mtvnzResponse);
    }

    @Override
    public void process(MTVNZResponse mtvnzResponse) {
        final NZSubscriberInfo subscriberInfo = nzSubscriberInfoRepository.findSubscriberInfoByMsisdn(mtvnzResponse.getPhoneNumber());
        LOGGER.debug("Subscriber Info {} for {} msisdn", subscriberInfo, mtvnzResponse.getPhoneNumber());
        if(subscriberInfo == null) {
            throw new ServiceException("No NZSubscriberInfo found for " + mtvnzResponse.getPhoneNumber());
        }
        List<PendingPayment> pendingPayments = pendingPaymentService.getPendingPayments(subscriberInfo.getUserId());
        for (PendingPayment pendingPayment : pendingPayments) {
            if(supportedPaymentSystem(pendingPayment)){
                mtvnzPaymentSystemService.commitPayment(pendingPayment, mtvnzResponse);
            }
        }
    }

    private boolean supportedPaymentSystem(PendingPayment pendingPayment) {
        return PaymentDetails.MTVNZ_PSMS_TYPE.equals(pendingPayment.getPaymentSystem());
    }

    public void setPaymentShortCodes(Set<String> paymentShortCodes) {
        this.paymentShortCodes = paymentShortCodes;
    }

    public void setMtvnzPaymentSystemService(MTVNZPaymentSystemService mtvnzPaymentSystemService) {
        this.mtvnzPaymentSystemService = mtvnzPaymentSystemService;
    }

    public void setMtvnzPaymentResponseParser(MTVNZPaymentResponseParser mtvnzPaymentResponseParser) {
        this.mtvnzPaymentResponseParser = mtvnzPaymentResponseParser;
    }

    public void setNzSubscriberInfoRepository(NZSubscriberInfoRepository nzSubscriberInfoRepository) {
        this.nzSubscriberInfoRepository = nzSubscriberInfoRepository;
    }

    public void setPendingPaymentService(PendingPaymentService pendingPaymentService) {
        this.pendingPaymentService = pendingPaymentService;
    }
}
