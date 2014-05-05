package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserPromoService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
import mobi.nowtechnologies.server.service.facebook.exception.FacebookForbiddenException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by oar on 2/13/14.
 */

@Controller
public class SigninFacebookController extends CommonController {

    @Resource
    private FacebookService facebookService;

    @Resource
    private UserPromoService userPromoService;

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:5\\.2}/SIGN_IN_FACEBOOK"})
    public ModelAndView applyPromotionByFacebook(
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("ACCESS_TOKEN") String facebookAccessToken,
            @RequestParam("FACEBOOK_USER_ID") String facebookUserId,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("DEVICE_UID") String deviceUID) {
        return signInFacebookImpl(userToken, timestamp, facebookAccessToken, facebookUserId, userName, deviceUID, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:6\\.0}/SIGN_IN_FACEBOOK"})
    public ModelAndView applyPromotionByFacebookWithCheckReactivation(
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("ACCESS_TOKEN") String facebookAccessToken,
            @RequestParam("FACEBOOK_USER_ID") String facebookUserId,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("DEVICE_UID") String deviceUID) {
        return signInFacebookImpl(userToken, timestamp, facebookAccessToken, facebookUserId, userName, deviceUID, true);
    }



    private ModelAndView signInFacebookImpl(String userToken, String timestamp, String facebookAccessToken, String facebookUserId, String userName, String deviceUID, boolean checkReactivation) {
        Exception ex = null;
        User user = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("APPLY_INIT_PROMO_FACEBOOK Started for accessToken[{}] in community[{}] ", facebookAccessToken, community);
            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.REGISTERED);
            FacebookProfile facebookProfile = facebookService.getAndValidateFacebookProfile(facebookAccessToken, facebookUserId);
            user = userPromoService.applyInitPromoByFacebook(user, facebookProfile, checkReactivation);
            return buildModelAndView(accCheckService.processAccCheck(user, true));
        } catch (UserCredentialsException ce) {
            ex = ce;
            LOGGER.error("APPLY_INIT_PROMO_FACEBOOK can not find deviceUID[{}] in community[{}]", deviceUID, community);
            throw ce;
        } catch (RuntimeException re) {
            ex = re;
            LOGGER.error("APPLY_INIT_PROMO_FACEBOOK error [{}] for facebookAccessToken[{}] in community[{}]", re.getMessage(), facebookAccessToken, community);
            throw re;
        } finally {
            logProfileData(null, community, null, null, user, ex);
            LOGGER.info("APPLY_INIT_PROMO_FACEBOOK Finished for facebookAccessToken[{}] in community[{}]", facebookAccessToken, community);
        }
    }


    @ExceptionHandler(FacebookForbiddenException.class)
    public ModelAndView handleInvalidToken(Exception exception, HttpServletResponse response) {
        return sendResponse(exception, response, HttpStatus.FORBIDDEN);
    }

}
