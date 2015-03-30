package mobi.nowtechnologies.server.service.vodafone.impl;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.sms.BasicSMSMessageProcessor;

import java.util.Collections;
import java.util.Set;

import com.sentaca.spring.smpp.mo.MOMessage;
import org.jsmpp.bean.DeliverSm;

/**
 * User: Alexsandr_Kolpakov Date: 10/10/13 Time: 10:26 AM
 */
public class VFNZUnsubscribeProccessor extends BasicSMSMessageProcessor<MOMessage> {

    private UserService userService;
    private String stopText = "stop";
    private String operatorName = "vf";
    private Set<String> supportedNumbers = Collections.emptySet();

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process(MOMessage message) {
        String text = message.getText();
        String phoneNumber = message.getOriginator();
        if (text.toLowerCase().trim().contains(stopText)) {
            LOGGER.debug("Start precessing stop sms [{}]", message);

            userService.unsubscribeUser(phoneNumber, operatorName);

            LOGGER.debug("Finish processing stop sms [{}]", message);
        }
    }

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return !deliverSm.isSmscDeliveryReceipt() && supportedNumbers.contains(deliverSm.getDestAddress());
    }

    public void setStopText(String stopText) {
        this.stopText = stopText.trim().toLowerCase();
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public void setSupportedNumbers(Set<String> supportedNumbers) {
        this.supportedNumbers = supportedNumbers;
    }
}
