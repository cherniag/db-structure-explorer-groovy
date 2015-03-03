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

    public static final String STOP_MSG = "stop";
    public static final String OPERATOR_NAME = "vf";

    private UserService userService;
    private String stopText = STOP_MSG;
    private String operatorName = OPERATOR_NAME;
    private Set<String> supportedNumbers = Collections.<String>emptySet();

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process(MOMessage message) {
        String text = message.getText();
        String phoneNumber = message.getOriginator();
        if (text.toLowerCase().contains(stopText)) {
            LOGGER.debug("Start proccess stop sms [{}]", message);

            userService.unsubscribeUser(phoneNumber, operatorName);

            LOGGER.debug("Finish proccess stop sms [{}]", message);
        }
    }

    @Override
    public boolean supports(DeliverSm deliverSm) {
        return !deliverSm.isSmscDeliveryReceipt() && supportedNumbers.contains(deliverSm.getDestAddress());
    }

    public void setStopText(String stopText) {
        this.stopText = stopText;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public void setSupportedNumbers(Set<String> supportedNumbers) {
        this.supportedNumbers = supportedNumbers;
    }
}
