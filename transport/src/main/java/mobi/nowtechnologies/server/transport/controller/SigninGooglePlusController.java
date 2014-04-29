package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.social.GooglePlusUserInfo;
import mobi.nowtechnologies.server.service.UserPromoService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.social.googleplus.GooglePlusService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by oar on 2/13/14.
 */

@Controller
public class SigninGooglePlusController extends CommonController {

    @Resource
    private GooglePlusService googlePlusService;

    @Resource
    private UserPromoService userPromoService;

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/5.2/SIGN_IN_GOOGLE_PLUS"})
    public ModelAndView applyPromotionBySignInGooglePlus(
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("ACCESS_TOKEN") String accessToken,
            @RequestParam("GOOGLE_PLUS_USER_ID") String googlePlusUserId,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("DEVICE_UID") String deviceUID) {
        Exception ex = null;
        User user = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("APPLY_INIT_PROMO_GOOGLE_PLUS Started for accessToken[{}] in community[{}] ", accessToken, community);
            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.REGISTERED);
            GooglePlusUserInfo googlePlusUserInfo = googlePlusService.getAndValidateProfile(accessToken, googlePlusUserId);
            user = userPromoService.applyInitPromoByGooglePlus(user, googlePlusUserInfo);
            return buildModelAndView(accCheckService.processAccCheck(user, true));
        } catch (UserCredentialsException ce) {
            ex = ce;
            LOGGER.error("APPLY_INIT_PROMO_GOOGLE_PLUS can not find deviceUID[{}] in community[{}]", deviceUID, community);
            throw ce;
        } catch (RuntimeException re) {
            ex = re;
            LOGGER.error("APPLY_INIT_PROMO_GOOGLE_PLUS error [{}] for accessToken[{}] in community[{}]", re.getMessage(), accessToken, community);
            throw re;
        } finally {
            logProfileData(null, community, null, null, user, ex);
            LOGGER.info("APPLY_INIT_PROMO_GOOGLE_PLUS Finished for accessToken[{}] in community[{}]", accessToken, community);
        }
    }


}
