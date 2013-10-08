package mobi.nowtechnologies.server.service.vodafone.impl;

import com.sentaca.spring.smpp.SMPPService;
import com.sentaca.spring.smpp.mt.MTMessage;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.service.sms.SMSMessageProcessorContainer;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.shared.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/7/13
 * Time: 10:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class VFNZSMSGatewayServiceImpl implements SMSGatewayService<SMSResponse> {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private SMPPService smppService;
    private SMSMessageProcessorContainer smppMessageProcessorContainer;

    @Override
    public SMSResponse send(String numbers, String message, String originator) {
        return send(new MTMessage(originator, numbers, message));
    }

    protected SMSResponse send(MTMessage messageObject){
        LOGGER.debug("start sending sms [{}], [{}], [{}]", new Object[]{messageObject.getOriginatingAddress(), messageObject.getDestinationAddress(), messageObject.getContent()});
        try {

            smppService.send(messageObject);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }

        LOGGER.debug("Sms was sent successfully from [{}] to [{}] with message [{}]", new Object[]{messageObject.getOriginatingAddress(), messageObject.getDestinationAddress(), messageObject.getContent()});
        return generateSuccessfulResponse(messageObject);
    }

    @Override
    public SMSResponse send(String numbers, String message, String originator, Processor processor) {
        MTMessage messageObject = new MTMessage(originator, numbers, message);
        smppMessageProcessorContainer.registerMessageProcessor(messageObject, processor);

        return send(messageObject);
    }

    private SMSResponse generateSuccessfulResponse(final MTMessage message){
        return new SMSResponse() {
            private MTMessage mtMessage;
            @Override
            public String getMessage() {
                return String.format("Sms was sent successfully from %s to %s with message %s", message.getOriginatingAddress(), message.getDestinationAddress(), message.getContent());
            }

            @Override
            public boolean isSuccessful() {
                return true;
            }
        };
    }

    public void setSmppService(SMPPService smppService) {
        this.smppService = smppService;
    }

    public void setSmppMessageProcessorContainer(SMSMessageProcessorContainer smppMessageProcessorContainer) {
        this.smppMessageProcessorContainer = smppMessageProcessorContainer;
    }
}
