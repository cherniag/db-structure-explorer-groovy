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
            "{other:.*}/{communityUri:o2}/{apiVersion:4\\.0}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL"})
    public ModelAndView activateVideo(@RequestParam("APP_VERSION") String appVersion,
                              @RequestParam("USER_NAME") String userName,
                              @RequestParam("USER_TOKEN") String userToken,
                              @RequestParam("TIMESTAMP") String timestamp,
                              @RequestParam("DEVICE_UID") String deviceUID,
                              @PathVariable("communityUri") String communityUri) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");

            user = promotionService.activateVideoAudioFreeTrial(userName, userToken, timestamp, communityUri, deviceUID);

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

    @RequestMapping(method = RequestMethod.POST, value = {
            "{other:.*}/{communityUri:o2}/{apiVersion:4\\.1}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:4\\.2}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL",
            "{other:.*}/{communityUri}/{apiVersion:5\\.0}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:4\\.1}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL.json",
            "{other:.*}/{communityUri:o2}/{apiVersion:4\\.2}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL.json",
            "{other:.*}/{communityUri}/{apiVersion:5\\.0}/ACTIVAzTE_VIDEO_AUDIO_FREE_TRIAL.json",
            "/{communityUri:o2}/{apiVersion:4\\.2}/ACTIVATE_VIDEO_AUDIO_FREE_TRIAL.json",
            "/{communityUri}/{apiVersion:5\\.0}/ACTIVAzTE_VIDEO_AUDIO_FREE_TRIAL.json"
    })
    public ModelAndView activateVideoAcceptHeaderSupport(@RequestParam("APP_VERSION") String appVersion,
                                      @RequestParam("USER_NAME") String userName,
                                      @RequestParam("USER_TOKEN") String userToken,
                                      @RequestParam("TIMESTAMP") String timestamp,
                                      @RequestParam("DEVICE_UID") String deviceUID,
                                      @PathVariable("communityUri") String communityUri,
                                      @PathVariable("apiVersion") String apiVersion) throws Exception {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = activateVideo(appVersion, userName, userToken, timestamp, deviceUID, communityUri);
        modelAndView.setViewName(defaultViewName);
        return modelAndView;
    }
}
