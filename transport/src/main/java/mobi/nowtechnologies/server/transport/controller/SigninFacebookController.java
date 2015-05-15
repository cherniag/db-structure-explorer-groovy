package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.AccountCheckDto;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MergeResult;
import mobi.nowtechnologies.server.service.UserPromoService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.dto.facebook.FacebookUserDetailsDto;
import mobi.nowtechnologies.server.social.service.facebook.FacebookService;
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
public class SigninFacebookController extends CommonController {

    @Resource
    private FacebookService facebookService;

    @Resource
    private UserPromoService userPromoService;

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.12}/SIGN_IN_FACEBOOK", "**/{community}/{apiVersion:6\\.11}/SIGN_IN_FACEBOOK",
                                                          "**/{community}/{apiVersion:6\\.10}/SIGN_IN_FACEBOOK"})
    public ModelAndView applyPromotionByFacebookWithProfileImageUrl(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                                    @RequestParam("ACCESS_TOKEN") String facebookAccessToken, @RequestParam("FACEBOOK_USER_ID") String facebookUserId,
                                                                    @RequestParam("USER_NAME") String userName, @RequestParam("DEVICE_UID") String deviceUID) {
        return signInFacebookImpl(userToken, timestamp, facebookAccessToken, facebookUserId, userName, deviceUID, true, true, true);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.9}/SIGN_IN_FACEBOOK", "**/{community}/{apiVersion:6\\.8}/SIGN_IN_FACEBOOK"})
    public ModelAndView applyPromotionByFacebookWithOneTimePayment(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                                   @RequestParam("ACCESS_TOKEN") String facebookAccessToken, @RequestParam("FACEBOOK_USER_ID") String facebookUserId,
                                                                   @RequestParam("USER_NAME") String userName, @RequestParam("DEVICE_UID") String deviceUID) {
        return signInFacebookImpl(userToken, timestamp, facebookAccessToken, facebookUserId, userName, deviceUID, true, true, false);
    }

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.7}/SIGN_IN_FACEBOOK", "**/{community}/{apiVersion:6\\.6}/SIGN_IN_FACEBOOK", "**/{community}/{apiVersion:6\\.5}/SIGN_IN_FACEBOOK",
                        "**/{community}/{apiVersion:6\\.4}/SIGN_IN_FACEBOOK", "**/{community}/{apiVersion:6\\.3}/SIGN_IN_FACEBOOK", "**/{community}/{apiVersion:6\\.2}/SIGN_IN_FACEBOOK",
                        "**/{community}/{apiVersion:6\\.1}/SIGN_IN_FACEBOOK", "**/{community}/{apiVersion:6\\.0}/SIGN_IN_FACEBOOK"})
    public ModelAndView applyPromotionByFacebookWithCheckReactivation(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                                      @RequestParam("ACCESS_TOKEN") String facebookAccessToken, @RequestParam("FACEBOOK_USER_ID") String facebookUserId,
                                                                      @RequestParam("USER_NAME") String userName, @RequestParam("DEVICE_UID") String deviceUID) {
        return signInFacebookImpl(userToken, timestamp, facebookAccessToken, facebookUserId, userName, deviceUID, true, false, false);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:5\\.2}/SIGN_IN_FACEBOOK"})
    public ModelAndView applyPromotionByFacebook(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp, @RequestParam("ACCESS_TOKEN") String facebookAccessToken,
                                                 @RequestParam("FACEBOOK_USER_ID") String facebookUserId, @RequestParam("USER_NAME") String userName, @RequestParam("DEVICE_UID") String deviceUID) {
        return signInFacebookImpl(userToken, timestamp, facebookAccessToken, facebookUserId, userName, deviceUID, false, false, false);
    }

    private ModelAndView signInFacebookImpl(String userToken, String timestamp, String facebookAccessToken, String facebookUserId, String userName, String deviceUID, boolean disableReactivation,
                                            boolean withOneTimeSubscriptionFlag, boolean withFacebookProfileImageUrl) {
        String community = null;
        try {
            community = getCurrentCommunityUri();
            LOGGER.info("APPLY_INIT_PROMO_FACEBOOK Started for accessToken[{}] in community[{}] ", facebookAccessToken, community);
            User user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.REGISTERED);
            SocialNetworkInfo userInfo = facebookService.getFacebookUserInfo(facebookAccessToken, facebookUserId);
            MergeResult mergeResult = userPromoService.applyInitPromoByFacebook(user, userInfo, disableReactivation);
            AccountCheckDto accountCheckDto = accCheckService.processAccCheck(mergeResult, true, withOneTimeSubscriptionFlag);

            if (withFacebookProfileImageUrl) {
                ((FacebookUserDetailsDto) accountCheckDto.getUserDetails()).setFacebookProfileImageUrl(userInfo.getProfileImageUrl());
                ((FacebookUserDetailsDto) accountCheckDto.getUserDetails()).setFacebookProfileImageSilhouette(userInfo.isProfileImageSilhouette());
            }

            return buildModelAndView(accountCheckDto);
        } catch (UserCredentialsException ce) {
            LOGGER.error("APPLY_INIT_PROMO_FACEBOOK can not find deviceUID[{}] in community[{}]", deviceUID, community);
            throw ce;
        } catch (RuntimeException re) {
            LOGGER.error("APPLY_INIT_PROMO_FACEBOOK error [{}] for facebookAccessToken[{}] in community[{}]", re.getMessage(), facebookAccessToken, community);
            throw re;
        }
    }


}
