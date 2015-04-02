package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.common.util.PhoneData;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PendingPayment;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessor;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.jsmpp.bean.DeliverSm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Gennadii Cherniaiev
 * Date: 2/26/2015
 */
public class MTVNZPaymentSMSProcessor implements SMSMessageProcessor<MTVNZResponse> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String supportedCommunity = Community.MTV_NZ_COMMUNITY_REWRITE_URL;

    private Set<String> paymentShortCodes = new HashSet<>();

    @Resource
    MTVNZPaymentSystemService mtvnzPaymentSystemService;
    @Resource
    MTVNZPaymentResponseParser mtvnzPaymentResponseParser;
    @Resource
    PendingPaymentService pendingPaymentService;
    @Resource
    UserRepository userRepository;

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return deliverSm.isSmscDeliveryReceipt() && paymentShortCodes.contains(deliverSm.getDestAddress());
    }

    @Override
    public void parserAndProcess(Object data) {
        DeliverSm deliverSm = (DeliverSm) data;
        logger.info("Parse DeliverSm from [{}], data [{}]", deliverSm.getSourceAddr(), new String(ArrayUtils.nullToEmpty(deliverSm.getShortMessage())));
        MTVNZResponse response = mtvnzPaymentResponseParser.parse(deliverSm);

        process(response);
    }

    @Override
    public void process(MTVNZResponse response) {
        logger.info("Process response: {}", response);
        PhoneData phoneData = new PhoneData(response.getPhoneNumber());

        List<User> users = userRepository.findByMobileAndCommunity(phoneData.getMobile(), supportedCommunity);

        for (User user : users) {
            processResponse(user, response);
        }

        logger.info("Process finished for {}",response.getPhoneNumber());
    }

    private void processResponse(User user, MTVNZResponse response) {
        logger.info("Process payment system response for user {}", user.getUserName());

        List<PendingPayment> pendingPayments = pendingPaymentService.getPendingPayments(user.getId());

        for (PendingPayment pendingPayment : pendingPayments) {
            logger.info("Check and commit {}", pendingPayment);
            if(supportedPaymentSystem(pendingPayment)){
                mtvnzPaymentSystemService.commitPayment(pendingPayment, response);
            }
        }
    }

    private boolean supportedPaymentSystem(PendingPayment pendingPayment) {
        return PaymentDetails.MTVNZ_PSMS_TYPE.equals(pendingPayment.getPaymentSystem());
    }

    public void setPaymentShortCodes(Set<String> paymentShortCodes) {
        this.paymentShortCodes.addAll(paymentShortCodes);
    }
}
