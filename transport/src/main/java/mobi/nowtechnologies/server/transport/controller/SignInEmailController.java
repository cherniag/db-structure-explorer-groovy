package mobi.nowtechnologies.server.transport.controller;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserPromoService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
public class SignInEmailController extends CommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignInEmailController.class);

    @Resource
    private UserPromoService userPromoService;

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/SIGN_IN_EMAIL"})
    public ModelAndView applyPromotionByEmail(
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("EMAIL_ID") Long activationEmailId,
            @RequestParam("EMAIL") String email,
            @RequestParam("TOKEN") String token,
            @RequestParam("DEVICE_UID") String deviceUID,
            @PathVariable String community) {
        Exception ex = null;
        User user = null;
        try {
            LOGGER.info("SIGN_IN_EMAIL Started for activationEmailId: [{}], email: [{}], deviceUID: [{}]",
                    activationEmailId, email, deviceUID);
            user = checkUser(deviceUID, userToken, timestamp, deviceUID, ActivationStatus.REGISTERED);

            user = userPromoService.applyInitPromoByEmail(user, activationEmailId, email, token);

            return buildModelAndView(accCheckService.processAccCheck(user, false));
        } catch (UserCredentialsException ce) {
            ex = ce;
            LOGGER.error("SIGN_IN_EMAIL can not find deviceUID: [{}] in community: [{}]", deviceUID, community);
            throw ce;
        } catch (RuntimeException re) {
            ex = re;
            LOGGER.error("SIGN_IN_EMAIL error: [{}] for user :[{}], community: [{}], activationEmailId: [{}]",
                    re.getMessage(), deviceUID, community, activationEmailId);
            throw re;
        } finally {
            logProfileData(null, community, null, null, user, ex);
            LOGGER.info("SIGN_IN_EMAIL error for user: [{}], community: [{}], activationEmailId: [{}]",
                    deviceUID, community, activationEmailId);
        }
    }
}
