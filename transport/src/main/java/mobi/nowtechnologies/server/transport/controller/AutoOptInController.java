package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: Titov Mykhaylo (titov)
 * 03.09.13 16:04
 */
@Controller
public class AutoOptInController extends CommonController {
    private AccCheckController accCheckController;

    public void setAccCheckController(AccCheckController accCheckController) {
        this.accCheckController = accCheckController;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{communityUri}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/AUTO_OPT_IN"
    })
    public ModelAndView autoOptIn(@RequestParam("USER_NAME") String userName,
                                  @RequestParam("USER_TOKEN") String userToken,
                                  @RequestParam("TIMESTAMP") String timestamp,
                                  @RequestParam("DEVICE_UID") String deviceUID,
                                  @RequestParam(value = "OTAC_TOKEN", required = false) String otac) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");

            user = userService.checkCredentials(userName, userToken, timestamp, getCurrentCommunityUri(), deviceUID);

            user = userService.autoOptIn(user, otac);

            AccountCheckDTO accountCheckDTO = accCheckController.processAccCheck(user);

            accountCheckDTO.withHasPotentialPromoCodePromotion(true);

            return buildModelAndView(accountCheckDTO);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, getCurrentCommunityUri(), null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }
}
