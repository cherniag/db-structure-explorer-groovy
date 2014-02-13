package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.PromotionService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.springframework.stereotype.Controller;
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

    public void setPromotionService(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{communityUri}/{apiVersion:[4-9]{1}\\.[0-9]{1,3}}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL"})
    public ModelAndView activateVideo(
                              @RequestParam("USER_NAME") String userName,
                              @RequestParam("USER_TOKEN") String userToken,
                              @RequestParam("TIMESTAMP") String timestamp,
                              @RequestParam("DEVICE_UID") String deviceUID) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");

            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.ACTIVATED);

            user = promotionService.activateVideoAudioFreeTrial(user);

            AccountCheckDTO accountCheckDTO = getAccountCheckDTO(user);

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
