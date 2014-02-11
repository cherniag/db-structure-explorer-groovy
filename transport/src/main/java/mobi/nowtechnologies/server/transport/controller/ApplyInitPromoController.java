package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserPromoService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.facebook.FacebookService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class ApplyInitPromoController extends CommonController {

    private UpdateO2UserTask updateO2UserTask;
    private AccCheckController accCheckController;

    @Resource
    private FacebookService facebookService;

    private UserPromoService userPromoService;

    public void setAccCheckController(AccCheckController accCheckController) {
        this.accCheckController = accCheckController;
    }

    public void setUpdateO2UserTask(UpdateO2UserTask updateO2UserTask) {
        this.updateO2UserTask = updateO2UserTask;
    }

    public void setUserPromoService(UserPromoService userPromoService) {
        this.userPromoService = userPromoService;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/APPLY_INIT_PROMO"
    })
    public ModelAndView applyPromotion(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("OTAC_TOKEN") String token,
            @RequestParam(value = "DEVICE_UID", required = false) String deviceUID) {

        Exception ex = null;
        User user = null;
        String community = getCurrentCommunityUri();
        String apiVersion = getCurrentApiVersion();
        try {
            LOGGER.info("APPLY_INIT_PROMO Started for user[{}] in community[{}] otac_token[{}]", userName, community, token);

            boolean isMajorApiVersionNumberLessThan4 = isMajorApiVersionNumberLessThan(VERSION_4, apiVersion);

            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.ENTERED_NUMBER);

            user = userService.applyInitPromo(user, token, isMajorApiVersionNumberLessThan4, false);

            AccountCheckDTO accountCheckDTO = accCheckController.processAccCheck(user);

            accountCheckDTO.withFullyRegistered(true).withHasPotentialPromoCodePromotion(user.isHasPromo());

            if (isMajorApiVersionNumberLessThan4) {
                updateO2UserTask.handleUserUpdate(user);
            }

            return buildModelAndView(accountCheckDTO);
        } catch (UserCredentialsException ce) {
            ex = ce;
            LOGGER.error("APPLY_INIT_PROMO can not find user[{}] in community[{}] otac_token[{}]", userName, community, token);
            throw ce;
        } catch (RuntimeException re) {
            ex = re;
            LOGGER.error("APPLY_INIT_PROMO error [{}] for user[{}] in community[{}] otac_token[{}]", re.getMessage(), userName, community, token);
            throw re;
        } finally {
            logProfileData(null, community, null, null, user, ex);
            LOGGER.info("APPLY_INIT_PROMO Finished for user[{}] in community[{}] otac_token[{}]", userName, community, token);
        }
    }


    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/APPLY_INIT_PROMO_FACEBOOK"})
    public ModelAndView applyPromotionByFacebook(
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("ACCESS_TOKEN") String facebookAccessToken,
            @RequestParam("FACEBOOK_USER_ID") String facebookUserId,
            @RequestParam("DEVICE_UID") String deviceUID) {
        Exception ex = null;
        User user = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("APPLY_INIT_PROMO_FACEBOOK Started for accessToken[{}] in community[{}] ", facebookAccessToken, community);
            user = checkUser(deviceUID, userToken, timestamp, deviceUID, ActivationStatus.REGISTERED);
            FacebookProfile facebookProfile = facebookService.getAndValidateFacebookProfile(facebookAccessToken, facebookUserId);
            //MOBILE_IS_SET BECAUSE MERGE IS POSSIBLE
            user.setMobile(facebookProfile.getEmail());
            user = userService.applyInitPromo(user, null, false, true);
            facebookService.saveFacebookInfoForUser(user, facebookProfile);
            AccountCheckDTO accountCheckDTO = accCheckController.processAccCheck(user);
            accountCheckDTO.withFullyRegistered(true).withHasPotentialPromoCodePromotion(user.isHasPromo());
            return buildModelAndView(accountCheckDTO);
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

    @RequestMapping(method = RequestMethod.POST, value = {
            "**//*{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/EMAIL_CONFIRM_APPLY_INIT_PROMO"})
    public ModelAndView applyPromotionByEmail(
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("EMAIL_ID") Long activationEmailId,
            @RequestParam("EMAIL") String email,
            @RequestParam("TOKEN") String token,
            @RequestParam("DEVICE_UID") String deviceUID) {
        Exception ex = null;
        User user = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("EMAIL_CONFIRM_APPLY_INIT_PROMO Started for activationEmailId: [{}], email: [{}], deviceUID: [{}]",
                    activationEmailId, email, deviceUID);
            user = checkUser(deviceUID, userToken, timestamp, deviceUID, ActivationStatus.REGISTERED);

            user = userPromoService.applyInitPromoByEmail(user, activationEmailId, email, token);

            AccountCheckDTO accountCheckDTO = accCheckController.processAccCheck(user);

            accountCheckDTO.withFullyRegistered(true).withHasPotentialPromoCodePromotion(user.isHasPromo());

            return buildModelAndView(accountCheckDTO);
        } catch (UserCredentialsException ce) {
            ex = ce;
            LOGGER.error("EMAIL_CONFIRM_APPLY_INIT_PROMO can not find deviceUID: [{}] in community: [{}]", deviceUID, community);
            throw ce;
        } catch (RuntimeException re) {
            ex = re;
            LOGGER.error("EMAIL_CONFIRM_APPLY_INIT_PROMO error: [{}] for user :[{}], community: [{}], activationEmailId: [{}]",
                    re.getMessage(), deviceUID, community, activationEmailId);
            throw re;
        } finally {
            logProfileData(null, community, null, null, user, ex);
            LOGGER.info("EMAIL_CONFIRM_APPLY_INIT_PROMO error: [{}] for user :[{}], community: [{}], activationEmailId: [{}]",
                    deviceUID, community, activationEmailId);
        }
    }

}
