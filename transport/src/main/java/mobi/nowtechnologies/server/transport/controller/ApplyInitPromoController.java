package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.job.UpdateO2UserTask;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.UserCredentialsException;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * ApplyInitPromoConroller
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class ApplyInitPromoController extends CommonController {

    private UserService userService;
    private O2ClientService o2ClientService;
    private UpdateO2UserTask updateO2UserTask;

    public void setO2ClientService(O2ClientService o2ClientService) {
        this.o2ClientService = o2ClientService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUpdateO2UserTask(UpdateO2UserTask updateO2UserTask) {
        this.updateO2UserTask = updateO2UserTask;
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/APPLY_INIT_PROMO", "/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/APPLY_INIT_PROMO"})
    public ModelAndView applyInitialPromotion(
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp) throws Exception {

        LOGGER.info("command processing started");
        Exception ex = null;
		User user = null; 
		try {
            user = userService.findByNameAndCommunity(userName, communityName);

            AccountCheckDTO accountCheckDTO = userService.applyInitialPromotion(user);
            final Object[] objects = new Object[]{accountCheckDTO};
            precessRememberMeToken(objects);

            return new ModelAndView(view, Response.class.toString(), new Response(objects));
		}catch(Exception e){
			ex = e;
			throw e;
		} finally {
			logProfileData(null, communityName, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/APPLY_INIT_PROMO", "*/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/APPLY_INIT_PROMO"})
    public ModelAndView applyO2Promotion(
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("OTAC_TOKEN") String token,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion) {
    	
    	Exception ex = null;
 		User user = null; 
        try {
            LOGGER.info("APPLY_INIT_PROMO Started for user[{}] in community[{}] otac_token[{}]", userName, community, token);
            user = userService.findByNameAndCommunity(userName, communityName);
            User mobileUser = null;
            if (null != user) {
            	mobileUser = userService.findByNameAndCommunity(user.getMobile(), communityName);
            	
    			boolean updateContractAndProvider = isMajorApiVersionNumberLessThan(VERSION_4, apiVersion);

            	AccountCheckDTO accountCheckDTO = userService.applyInitPromoO2(user, mobileUser, token, updateContractAndProvider);

    	        final Object[] objects = new Object[]{accountCheckDTO};
    	        precessRememberMeToken(objects);

    	        user = user.getActivationStatus() != ActivationStatus.ACTIVATED ? mobileUser : user;

                if (isMajorApiVersionNumberLessThan(VERSION_4, apiVersion) ){
                    updateO2UserTask.handleUserUpdate(user);
                }

    	    	return new ModelAndView(view, Response.class.toString(), new Response(objects));
            }
            throw new UserCredentialsException("Bad user credentials");
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

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:o2}/{apiVersion:4\\.0}/APPLY_INIT_PROMO.json"
    }, produces = "application/json")
    public @ResponseBody Response applyO2PromotionJson(
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("OTAC_TOKEN") String token,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion) {
        return (Response)applyO2Promotion(communityName, userName, userToken, timestamp, token, community, apiVersion).getModelMap().get(MODEL_NAME);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:o2}/{apiVersion:4\\.1}/APPLY_INIT_PROMO",
            "*/{community:o2}/{apiVersion:4\\.1}/APPLY_INIT_PROMO.json",
            "*/{community:o2}/{apiVersion:4\\.2}/APPLY_INIT_PROMO",
            "*/{community:o2}/{apiVersion:4\\.2}/APPLY_INIT_PROMO.json"
    })
    public ModelAndView applyO2PromotionAcceptHeaderSupport(
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("OTAC_TOKEN") String token,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion) {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = applyO2Promotion(communityName, userName, userToken, timestamp, token, community, apiVersion);
        modelAndView.setViewName(defaultViewName);

        return modelAndView;
    }
}
