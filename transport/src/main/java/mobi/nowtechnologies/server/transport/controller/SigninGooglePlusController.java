package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MergeResult;
import mobi.nowtechnologies.server.service.UserPromoService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.service.googleplus.GooglePlusService;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by oar on 2/13/14.
 */
@Controller
public class SigninGooglePlusController extends CommonController {

    @Resource
    private GooglePlusService googlePlusService;

    @Resource
    private UserPromoService userPromoService;

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.12}/SIGN_IN_GOOGLE_PLUS", "**/{community}/{apiVersion:6\\.11}/SIGN_IN_GOOGLE_PLUS",
                             "**/{community}/{apiVersion:6\\.10}/SIGN_IN_GOOGLE_PLUS",
                        "**/{community}/{apiVersion:6\\.9}/SIGN_IN_GOOGLE_PLUS", "**/{community}/{apiVersion:6\\.8}/SIGN_IN_GOOGLE_PLUS"})
    public ModelAndView applyPromotionBySignInGooglePlusWithOneTimeSubscription(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                                                @RequestParam("ACCESS_TOKEN") String accessToken, @RequestParam("GOOGLE_PLUS_USER_ID") String googlePlusUserId,
                                                                                @RequestParam("USER_NAME") String userName, @RequestParam("DEVICE_UID") String deviceUID) {
        return signInGooglePlus(userToken, timestamp, accessToken, googlePlusUserId, userName, deviceUID, true, true);
    }


    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.7}/SIGN_IN_GOOGLE_PLUS", "**/{community}/{apiVersion:6\\.6}/SIGN_IN_GOOGLE_PLUS", "**/{community}/{apiVersion:6\\.5}/SIGN_IN_GOOGLE_PLUS",
                        "**/{community}/{apiVersion:6\\.4}/SIGN_IN_GOOGLE_PLUS", "**/{community}/{apiVersion:6\\.3}/SIGN_IN_GOOGLE_PLUS", "**/{community}/{apiVersion:6\\.2}/SIGN_IN_GOOGLE_PLUS",
                        "**/{community}/{apiVersion:6\\.1}/SIGN_IN_GOOGLE_PLUS", "**/{community}/{apiVersion:6\\.0}/SIGN_IN_GOOGLE_PLUS"})
    public ModelAndView applyPromotionBySignInGooglePlusWithCheckReactivation(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                                              @RequestParam("ACCESS_TOKEN") String accessToken, @RequestParam("GOOGLE_PLUS_USER_ID") String googlePlusUserId,
                                                                              @RequestParam("USER_NAME") String userName, @RequestParam("DEVICE_UID") String deviceUID) {
        return signInGooglePlus(userToken, timestamp, accessToken, googlePlusUserId, userName, deviceUID, true, false);
    }

    private ModelAndView signInGooglePlus(String userToken, String timestamp, String accessToken, String googlePlusUserId, String userName, String deviceUID, boolean disableReactivation,
                                          boolean withOneTimePayment) {
        String community = null;
        try {
            community = getCurrentCommunityUri();
            LOGGER.info("APPLY_INIT_PROMO_GOOGLE_PLUS Started for accessToken[{}] in community[{}] ", accessToken, community);
            User user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.REGISTERED);
            SocialNetworkInfo googlePlusUserInfo = googlePlusService.getGooglePlusUserInfo(accessToken, googlePlusUserId);
            MergeResult mergeResult = userPromoService.applyInitPromoByGooglePlus(user, googlePlusUserInfo, disableReactivation);
            return buildModelAndView(accCheckService.processAccCheck(mergeResult, true, withOneTimePayment));
        } catch (UserCredentialsException ce) {
            LOGGER.error("APPLY_INIT_PROMO_GOOGLE_PLUS can not find deviceUID[{}] in community[{}]", deviceUID, community);
            throw ce;
        } catch (RuntimeException re) {
            LOGGER.error("APPLY_INIT_PROMO_GOOGLE_PLUS error [{}] for accessToken[{}] in community[{}]", re.getMessage(), accessToken, community);
            throw re;
        }
    }


}
