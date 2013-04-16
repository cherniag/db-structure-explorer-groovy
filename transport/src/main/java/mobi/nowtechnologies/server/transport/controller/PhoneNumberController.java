package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.PhoneActivationDto;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
	
	@RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/PHONE_NUMBER"})
	public ModelAndView activatePhoneNumber(
			@RequestParam(value = "PHONE", required = false) String phone,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@PathVariable("community") String community) {
		LOGGER.info("PHONE_NUMBER Started for user[{}] community[{}]", userName, community);
		try {
			User user = userService.checkCredentials(userName, userToken, timestamp, community);

			user = userService.activatePhoneNumber(user, phone);
			
			String redeemServerO2Url = userService.getRedeemServerO2Url(user);
			
			return new ModelAndView(view, Response.class.toString(), new Response(new Object[]{new PhoneActivationDto(user.getActivationStatus(), user.getMobile(), redeemServerO2Url)}));

		} finally {
            LOGGER.info("PHONE_NUMBER Finished for user[{}] community[{}]", userName, community);
		}
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}