package mobi.nowtechnologies.server.transport.phonenumber;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.o2.impl.O2ProviderService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import mobi.nowtechnologies.server.transport.phonenumber.dto.PhoneActivationDto;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * SingUpDeviceController
 *
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class PhoneNumberController extends CommonController {
    @Resource
    private O2ProviderService o2ClientService;

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community:o2}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/PHONE_NUMBER"})
    public ModelAndView activatePhoneNumber_O2(@RequestParam(value = "PHONE", required = false) String phone, @RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken,
                                               @RequestParam("TIMESTAMP") String timestamp, @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        User user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.REGISTERED, ActivationStatus.ENTERED_NUMBER);

        boolean populateO2SubscriberData = !isMajorApiVersionNumberLessThan(VERSION_4, getCurrentApiVersion());
        user = userService.activatePhoneNumber(user, phone);

        if (phone != null && populateO2SubscriberData) {
            userService.populateSubscriberData(user);
        }

        String redeemServerO2Url = o2ClientService.getRedeemServerO2Url(user.getMobile());

        return buildModelAndView(new PhoneActivationDto(user.getActivationStatus(), user.getMobile(), redeemServerO2Url));
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community:^(?!o2$).*}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/PHONE_NUMBER"})
    public ModelAndView activatePhoneNumber(@RequestParam(value = "PHONE", required = false) String phone, @RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken,
                                            @RequestParam("TIMESTAMP") String timestamp, @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        UserService userService = getUserService(getCurrentCommunityUri());

        User user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.REGISTERED, ActivationStatus.ENTERED_NUMBER);

        user = userService.activatePhoneNumber(user, phone);

        if (phone != null) {
            userService.populateSubscriberData(user);
        }

        return buildModelAndView(new PhoneActivationDto(user.getActivationStatus(), user.getMobile(), null));
    }
}
