package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    private UpdateO2UserTask updateO2UserTask;
    private AccCheckController accCheckController;

    public void setAccCheckController(AccCheckController accCheckController) {
        this.accCheckController = accCheckController;
    }

    public void setUpdateO2UserTask(UpdateO2UserTask updateO2UserTask) {
        this.updateO2UserTask = updateO2UserTask;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/APPLY_INIT_PROMO"
    })
    public ModelAndView applyPromotion(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("OTAC_TOKEN") String token,
            @RequestParam(value = "DEVICE_UID", required = false) String deviceUID,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion) {
    	
    	Exception ex = null;
 		User user = null; 
        try {
            LOGGER.info("APPLY_INIT_PROMO Started for user[{}] in community[{}] otac_token[{}]", userName, community, token);

            boolean isMajorApiVersionNumberLessThan4 = isMajorApiVersionNumberLessThan(VERSION_4, apiVersion);

            if (isValidDeviceUID(deviceUID)) {
                user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);
            }
            else {
                user = userService.checkCredentials(userName, userToken, timestamp, community);
            }

            user = userService.applyInitPromo(user, token, isMajorApiVersionNumberLessThan4);

            AccountCheckDTO accountCheckDTO = accCheckController.processAccCheck(user);

            accountCheckDTO.withFullyRegistered(true).withHasPotentialPromoCodePromotion(user.isHasPromo());

            user = (User) accountCheckDTO.user;

            if (isMajorApiVersionNumberLessThan4) {
                updateO2UserTask.handleUserUpdate(user);
            }

            return buildModelAndView(accountCheckDTO);
        }catch (UserCredentialsException ce){
        	ex = ce;
            LOGGER.error("APPLY_INIT_PROMO can not find user[{}] in community[{}] otac_token[{}]", userName, community, token);
            throw ce;
        }catch (RuntimeException re){
        	ex = re;
            LOGGER.error("APPLY_INIT_PROMO error [{}] for user[{}] in community[{}] otac_token[{}]",re.getMessage(), userName, community, token);
            throw re;
        }finally {
        	logProfileData(null, community, null, null, user, ex);
           LOGGER.info("APPLY_INIT_PROMO Finished for user[{}] in community[{}] otac_token[{}]", userName, community, token);
        }
    }
}
