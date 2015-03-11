package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.sms.SMPPMessage;
import mobi.nowtechnologies.server.service.sms.SMPPServiceImpl;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessorContainer;
import mobi.nowtechnologies.server.service.sms.SMSResponse;

import com.sentaca.spring.smpp.mt.MTMessage;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Alexsandr_Kolpakov Date: 10/7/13 Time: 10:03 AM
 */
public class VFNZSMSGatewayServiceImpl implements SMSGatewayService<SMSResponse> {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private SMPPServiceImpl smppService;

    @Override
    public SMSResponse send(String numbers, String message, String originator) {
        return send(numbers, message, originator, SMSCDeliveryReceipt.SUCCESS_FAILURE, -1L);
    }

    public SMSResponse send(String numbers, String message, String originator, SMSCDeliveryReceipt smscDeliveryReceipt, long expireTimeMillis) {
        return send(new SMPPMessage(originator, numbers, message, smscDeliveryReceipt, expireTimeMillis));
    }

    protected SMSResponse send(MTMessage messageObject) {
        LOGGER.debug("start sending sms [{}], [{}], [{}]", new Object[] {messageObject.getOriginatingAddress(), messageObject.getDestinationAddress(), messageObject.getContent()});
        boolean result = false;
        try {
            result = smppService.sendMessage(messageObject);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }

        SMSResponse response = generateResponse(result);
        LOGGER.info("Sms was sent successfully ({}) from [{}] to [{}] with message [{}]", response.isSuccessful(), messageObject.getOriginatingAddress(), messageObject.getDestinationAddress(), messageObject.getContent());
        return response;
    }

    private SMSResponse generateResponse(final boolean result){
        return new SMSResponse() {
            @Override
            public boolean isSuccessful() {
                return result;
            }
        };
    }

    public void setSmppService(SMPPServiceImpl smppService) {
        this.smppService = smppService;
    }

}
