package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.PhoneActivationDto;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;

import org.springframework.stereotype.Controller;
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
	
	/**
	 * Initiate activation of users phone number. As the result of this command user should get an SMS with pin to verify his phone number.
	 * 
	 * @param userName
	 * @param userToken
	 * @param timestamp
	 * 
	 * @return activation phone number info.
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(method = RequestMethod.POST, value = {"/O2/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/PHONE_NUMBER", "/O2/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/PHONE_NUMBER"})
	public ModelAndView activatePhoneNumber(
			@RequestParam("PHONE") String phone,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp) {
		LOGGER.info("command processing started");
		try {
			User user = userService.checkCredentials(userName, userToken, timestamp, "O2");

			user = userService.activatePhoneNumber(user, phone);
			
			return new ModelAndView(view, Response.class.toString(), new Response(new Object[]{new PhoneActivationDto(user.getActivationStatus(), user.getMobile())}));

		} finally {
			LOGGER.info("command processing finished");
		}
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}