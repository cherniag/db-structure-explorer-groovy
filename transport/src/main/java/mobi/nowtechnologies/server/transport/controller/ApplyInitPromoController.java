package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MergeResult;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ENTERED_NUMBER;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class ApplyInitPromoController extends CommonController {

    @Resource
    private UpdateO2UserTask updateO2UserTask;

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.11}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.10}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.9}/APPLY_INIT_PROMO",
                        "**/{community}/{apiVersion:6\\.8}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.7}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.6}/APPLY_INIT_PROMO",
                        "**/{community}/{apiVersion:6\\.5}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.4}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.3}/APPLY_INIT_PROMO",
                        "**/{community}/{apiVersion:6\\.2}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.1}/APPLY_INIT_PROMO", "**/{community}/{apiVersion:6\\.0}/APPLY_INIT_PROMO"})
    public ModelAndView applyPromotionWithReactivation(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                       @RequestParam("OTAC_TOKEN") String token, @RequestParam(value = "DEVICE_UID", required = false) String deviceUID) {

        return applyInitPromoImpl(userName, userToken, timestamp, token, deviceUID, true);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:3\\.[6-9]|[4-5]{1}\\.[0-9]{1,3}}/APPLY_INIT_PROMO"})
    public ModelAndView applyPromotion(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                       @RequestParam("OTAC_TOKEN") String token, @RequestParam(value = "DEVICE_UID", required = false) String deviceUID) {

        return applyInitPromoImpl(userName, userToken, timestamp, token, deviceUID, false);
    }

    private ModelAndView applyInitPromoImpl(String userName, String userToken, String timestamp, String token, String deviceUID, boolean checkReactivation) {
        String community = getCurrentCommunityUri();
        String apiVersion = getCurrentApiVersion();
        try {
            LOGGER.info("APPLY_INIT_PROMO Started for user[{}] in community[{}] otac_token[{}]", userName, community, token);

            boolean isMajorApiVersionNumberLessThan4 = isMajorApiVersionNumberLessThan(VERSION_4, apiVersion);

            User user = checkUser(userName, userToken, timestamp, deviceUID, false, ENTERED_NUMBER);

            MergeResult mergeResult = userService.applyInitPromo(user, token, isMajorApiVersionNumberLessThan4, false, checkReactivation);

            AccountCheckDTO accountCheckDTO = getAccountCheckDTOAfterApplyPromo(mergeResult);

            if (isMajorApiVersionNumberLessThan4) {
                updateO2UserTask.handleUserUpdate(mergeResult.getResultOfOperation());
            }

            return buildModelAndView(accountCheckDTO);
        } catch (UserCredentialsException ce) {
            LOGGER.error("APPLY_INIT_PROMO can not find user[{}] in community[{}] otac_token[{}]", userName, community, token);
            throw ce;
        } catch (RuntimeException re) {
            LOGGER.error("APPLY_INIT_PROMO error [{}] for user[{}] in community[{}] otac_token[{}]", re.getMessage(), userName, community, token);
            throw re;
        }
    }

    private AccountCheckDTO getAccountCheckDTOAfterApplyPromo(MergeResult mergeResult) {
        User user = mergeResult.getResultOfOperation();
        AccountCheckDTO accountCheckDTO = accCheckService.processAccCheck(mergeResult, false, false);
        accountCheckDTO.withFullyRegistered(true).withHasPotentialPromoCodePromotion(user.isHasPromo());
        return accountCheckDTO;
    }


}
