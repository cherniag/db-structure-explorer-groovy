package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.PhoneActivationDto;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * SingUpDeviceController
 * 
 * @author Alexander Kollpakov (akolpakov)
 * 
 */
@Controller
public class PhoneNumberController extends CommonController {
		
	private UserService userService;

    private UserService vfUserService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setVfUserService(UserService vfUserService) {
        this.vfUserService = vfUserService;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "/{community:o2}/{apiVersion:3\\.[0-9]{1,3}}/PHONE_NUMBER",
            "*/{community:o2}/{apiVersion:3\\.[0-9]{1,3}}/PHONE_NUMBER"
    })
	public ModelAndView activatePhoneNumber(
			@RequestParam(value = "PHONE", required = false) String phone,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@PathVariable("community") String community,
			@PathVariable("apiVersion") String apiVersion) throws Exception {
		LOGGER.info("PHONE_NUMBER Started for user[{}] community[{}]", userName, community);
		
		Exception ex = null;
		User user = null; 
		try {
			user = userService.checkCredentials(userName, userToken, timestamp, community);
			
			boolean populateO2SubscriberData = !isMajorApiVersionNumberLessThan(VERSION_4, apiVersion);
			user = userService.activatePhoneNumber(user, phone, populateO2SubscriberData);
			
			String redeemServerO2Url = userService.getRedeemServerO2Url(user);
			
			return new ModelAndView(view, Response.class.toString(), new Response(new Object[]{new PhoneActivationDto(user.getActivationStatus(), user.getMobile(), redeemServerO2Url)}));
		}catch(Exception e){
			ex = e;
			throw e;
		} finally {
			logProfileData(null, community, null, phone, user, ex);
            LOGGER.info("PHONE_NUMBER Finished for user[{}] community[{}]", userName, community);
		}
	}

    @RequestMapping(method = RequestMethod.POST, value = {
            "/{community:vf_nz}/{apiVersion:5\\.[0-9]{1,3}}/PHONE_NUMBER",
            "*/{community:vf_nz}/{apiVersion:5\\.[0-9]{1,3}}/PHONE_NUMBER"
    })
    public ModelAndView activatePhoneNumber_VF_NZ(
            @RequestParam(value = "PHONE", required = false) String phone,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion) throws Exception {
        LOGGER.info("PHONE_NUMBER Started for user[{}] community[{}]", userName, community);

        Exception ex = null;
        User user = null;
        try {
            user = vfUserService.checkCredentials(userName, userToken, timestamp, community);

            user = vfUserService.activatePhoneNumber(user, phone, true);

            return new ModelAndView(view, Response.class.toString(), new Response(new Object[]{new PhoneActivationDto(user.getActivationStatus(), user.getMobile(), null)}));
        }catch(Exception e){
            ex = e;
            throw e;
        } finally {
            logProfileData(null, community, null, phone, user, ex);
            LOGGER.info("PHONE_NUMBER Finished for user[{}] community[{}]", userName, community);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:o2}/{apiVersion:4\\.0}/PHONE_NUMBER",
            "*/{community:o2}/{apiVersion:4\\.0}/PHONE_NUMBER.json"
    })
    public ModelAndView activatePhoneNumberJson(
            @RequestParam(value = "PHONE", required = false) String phone,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @PathVariable("community") String community,
            @PathVariable("apiVersion") String apiVersion) throws Exception {

        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = activatePhoneNumber(phone, userName, userToken, timestamp, community, apiVersion);
        modelAndView.setViewName(defaultViewName);

        return modelAndView;
    }
}
