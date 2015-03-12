package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.NZSubscriberInfo;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.NZSubscriberInfoRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessor;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev
 * Date: 2/26/2015
 */
public class MTVNZPaymentSMSMessageProcessor implements SMSMessageProcessor<MTVNZResponse> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Set<String> paymentShortCodes = new HashSet<>();

    @Resource
    MTVNZPaymentSystemService mtvnzPaymentSystemService;
    @Resource
    MTVNZPaymentResponseParser mtvnzPaymentResponseParser;
    @Resource
    NZSubscriberInfoRepository nzSubscriberInfoRepository;
    @Resource
    PendingPaymentService pendingPaymentService;

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return deliverSm.isSmscDeliveryReceipt() && paymentShortCodes.contains(deliverSm.getDestAddress());
    }

    @Override
    public void parserAndProcess(Object data) {
        logger.info("Processing receipt: {}", data);
        DeliverSm deliverSm = (DeliverSm) data;
        final MTVNZResponse mtvnzResponse = mtvnzPaymentResponseParser.parse(deliverSm);
        logger.debug("Parse result: {}", mtvnzResponse);
        process(mtvnzResponse);
    }

    @Override
    public void process(MTVNZResponse mtvnzResponse) {
        final NZSubscriberInfo subscriberInfo = nzSubscriberInfoRepository.findSubscriberInfoByMsisdn(mtvnzResponse.getPhoneNumber());
        logger.debug("Subscriber Info {} for {} msisdn", subscriberInfo, mtvnzResponse.getPhoneNumber());
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
}
