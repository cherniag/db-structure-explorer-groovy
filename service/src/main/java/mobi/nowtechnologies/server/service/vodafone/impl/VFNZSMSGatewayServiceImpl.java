package mobi.nowtechnologies.server.service.vodafone.impl;

import com.sentaca.spring.smpp.mt.MTMessage;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.sms.*;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/7/13
 * Time: 10:03 AM
 */
public class VFNZSMSGatewayServiceImpl implements SMSGatewayService<SMSResponse> {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private SMPPServiceImpl smppService;
    private SMSMessageProcessorContainer smppMessageProcessorContainer;

    @Override
    public SMSResponse send(String numbers, String message, String originator) {
        return send(numbers, message, originator, SMSCDeliveryReceipt.SUCCESS_FAILURE, -1L);
    }

    public SMSResponse send(String numbers, String message, String originator, SMSCDeliveryReceipt smscDeliveryReceipt, long expireTimeMillis) {
        return send(new SMPPMessage(originator, numbers, message, smscDeliveryReceipt, expireTimeMillis));
    }

    protected SMSResponse send(MTMessage messageObject){
        LOGGER.debug("start sending sms [{}], [{}], [{}]", new Object[]{messageObject.getOriginatingAddress(), messageObject.getDestinationAddress(), messageObject.getContent()});
        boolean result = false;
        try {
            result = smppService.sendMessage(messageObject);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }

        SMSResponse response = generateResponse(result, messageObject);
        LOGGER.info(response.getMessage());
        return response;
    }

    private SMSResponse generateResponse(final boolean result, final MTMessage message){
        final String resultPrefix = result ? "" : "un";
        return new SMSResponse() {
            private MTMessage mtMessage;
            @Override
            public String getMessage() {
                return String.format("Sms was sent %ssuccessfully from %s to %s with message %s", resultPrefix, message.getOriginatingAddress(), message.getDestinationAddress(), message.getContent());
            }

            @Override
            public boolean isSuccessful() {
                return result;
            }
        };
    }

    public void setSmppService(SMPPServiceImpl smppService) {
        this.smppService = smppService;
    }

    public void setSmppMessageProcessorContainer(SMSMessageProcessorContainer smppMessageProcessorContainer) {
        this.smppMessageProcessorContainer = smppMessageProcessorContainer;
    }
}
