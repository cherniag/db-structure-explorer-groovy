package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: Titov Mykhaylo (titov) 22.07.13 17:24
 */
@Controller
public class ActivateVideoAudioFreeTrialController extends CommonController {

    @Resource
    private PromotionService promotionService;


    @RequestMapping(method = RequestMethod.POST, value = {"**/{communityUri}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL"})
    public ModelAndView activateVideo(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                      @RequestParam("DEVICE_UID") String deviceUID) throws Exception {
        User user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

        user = promotionService.activateVideoAudioFreeTrial(user);

        AccountCheckDTO accountCheckDTO = accCheckService.processAccCheck(user, false, false, false);

        return buildModelAndView(accountCheckDTO);
    }
}
