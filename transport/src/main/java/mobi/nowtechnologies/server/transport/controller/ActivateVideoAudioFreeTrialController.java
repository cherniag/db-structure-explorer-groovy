package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: Titov Mykhaylo (titov)
 * 22.07.13 17:24
 */
@Controller
public class ActivateVideoAudioFreeTrialController extends CommonController {

    private PromotionService promotionService;

    private AccCheckController accCheckController;

    public void setAccCheckController(AccCheckController accCheckController) {
        this.accCheckController = accCheckController;
    }

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{communityUri}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL"})
    public ModelAndView activateVideo(
                              @RequestParam("USER_NAME") String userName,
                              @RequestParam("USER_TOKEN") String userToken,
                              @RequestParam("TIMESTAMP") String timestamp,
                              @RequestParam("DEVICE_UID") String deviceUID,
                              @PathVariable("communityUri") String communityUri) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");

            if (isValidDeviceUID(deviceUID)) {
                user = userService.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID);
            }
            else {
                user = userService.checkCredentials(userName, userToken, timestamp, communityUri);
            }

            user = promotionService.activateVideoAudioFreeTrial(user);

            AccountCheckDTO accountCheckDTO = accCheckController.processAccCheck(user);

            return buildModelAndView(accountCheckDTO);
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, communityUri, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }
}
