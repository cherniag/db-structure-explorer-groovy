package mobi.nowtechnologies.server.service.vodafone.impl;

import com.sentaca.spring.smpp.mo.MOMessage;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Processor;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/10/13
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class VFNZUnsubscribeProccessor extends Processor<MOMessage> {
    public static final String STOP_MSG = "stop";

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void process(MOMessage message) {
        String text = message.getText();
        String phoneNumber = message.getOriginator();
        if (text.toLowerCase().contains(STOP_MSG)) {
             userService.unsubscribeUser(phoneNumber, "vf_nz");
        }
    }
}
