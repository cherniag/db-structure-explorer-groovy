package mobi.nowtechnologies.server.transport.controller;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MergeResult;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.service.UserPromoService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SignInEmailController extends CommonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignInEmailController.class);

    @Resource
    private UserPromoService userPromoService;

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.11}/SIGN_IN_EMAIL", "**/{community}/{apiVersion:6\\.10}/SIGN_IN_EMAIL", "**/{community}/{apiVersion:6\\.9}/SIGN_IN_EMAIL",
                        "**/{community}/{apiVersion:6\\.8}/SIGN_IN_EMAIL"})
    public ModelAndView applyPromotionByEmailWithOneTimePayment(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                                @RequestParam("EMAIL_ID") Long activationEmailId, @RequestParam("EMAIL") String email, @RequestParam("TOKEN") String token,
                                                                @RequestParam("DEVICE_UID") String deviceUID, @PathVariable String community) {
        return signInEmail(userToken, timestamp, activationEmailId, email, token, deviceUID, community, true);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.[0-7]}/SIGN_IN_EMAIL", "**/{community}/{apiVersion:3\\.[6-9]|[4-5]{1}\\.[0-9]{1,3}}/SIGN_IN_EMAIL"})
    public ModelAndView applyPromotionByEmail(@RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp, @RequestParam("EMAIL_ID") Long activationEmailId,
                                              @RequestParam("EMAIL") String email, @RequestParam("TOKEN") String token, @RequestParam("DEVICE_UID") String deviceUID, @PathVariable String community) {
        return signInEmail(userToken, timestamp, activationEmailId, email, token, deviceUID, community, false);
    }

    private ModelAndView signInEmail(String userToken, String timestamp, Long activationEmailId, String email, String token, String deviceUID, String community, boolean withOneTimePayment) {
        try {
            LOGGER.info("SIGN_IN_EMAIL Started for activationEmailId: [{}], email: [{}], deviceUID: [{}]", activationEmailId, email, deviceUID);
            User user = checkUser(deviceUID, userToken, timestamp, deviceUID, false, ActivationStatus.PENDING_ACTIVATION);

            MergeResult mergeResult = userPromoService.applyInitPromoByEmail(user, activationEmailId, email, token);

            return buildModelAndView(accCheckService.processAccCheck(mergeResult, false, withOneTimePayment));
        } catch (UserCredentialsException ce) {
            LOGGER.error("SIGN_IN_EMAIL can not find deviceUID: [{}] in community: [{}]", deviceUID, community);
            throw ce;
        } catch (RuntimeException re) {
            LOGGER.error("SIGN_IN_EMAIL error: [{}] for user :[{}], community: [{}], activationEmailId: [{}]", re.getMessage(), deviceUID, community, activationEmailId);
            throw re;
        }
    }
}
