package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.exception.ValidationException;
import mobi.nowtechnologies.server.service.validator.UserDeviceRegDetailsDtoValidator;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.web.UserDeviceRegDetailsDto;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * SingUpDeviceController
 * 
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 * 
 */
@Controller
public class SingUpDeviceController extends CommonController {
		
	private UserService userService;
	
	@InitBinder(UserDeviceRegDetailsDto.NAME)
	public void initUserDeviceRegDetailsDtoBinder(HttpServletRequest request, WebDataBinder binder) {
		binder.setValidator(new UserDeviceRegDetailsDtoValidator(request, userService, communityService));
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/SIGN_UP_DEVICE")
	public ModelAndView signUpDevice(HttpServletRequest request, @Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME)UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result) {
		LOGGER.info("command processing started");
		try {
			if (result.hasErrors()){
				List<ObjectError>  objectErrors = result.getAllErrors();
				
				for (ObjectError objectError : objectErrors){
					throw ValidationException.getInstance(objectError.getDefaultMessage());
				}
			}
			
			String remoteAddr = Utils.getIpFromRequest(request);
			userDeviceDetailsDto.setIpAddress(remoteAddr);
		
			AccountCheckDTO accountCheckDTO = userService.registerUser(userDeviceDetailsDto, true);
			User user = userService.findByNameAndCommunity(accountCheckDTO.getUserName(), userDeviceDetailsDto.getCommunityName());
						
			accountCheckDTO = userService.applyInitialPromotion(user);
			final Object[] objects = new Object[]{accountCheckDTO};
			proccessRememberMeToken(objects);
			
			return new ModelAndView(view, Response.class.toString(), new Response(objects));
			
		} finally {
			LOGGER.info("command processing finished");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = {"/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/SIGN_UP_DEVICE", "/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/SIGN_UP_DEVICE",
			"*/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/SIGN_UP_DEVICE", "*/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/SIGN_UP_DEVICE"})
	public ModelAndView signUpDevice_V3GT(HttpServletRequest request,
			@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result) {
		LOGGER.info("command processing started");
		try {
			if (result.hasErrors()) {
				List<ObjectError> objectErrors = result.getAllErrors();

				for (ObjectError objectError : objectErrors) {
					throw ValidationException.getInstance(objectError.getDefaultMessage());
				}
			}

			String remoteAddr = Utils.getIpFromRequest(request);
			userDeviceDetailsDto.setIpAddress(remoteAddr);

			AccountCheckDTO accountCheckDTO = userService.registerUser(userDeviceDetailsDto, true);

			final Object[] objects = new Object[] { accountCheckDTO };
			proccessRememberMeToken(objects);
			
			return new ModelAndView(view, Response.class.toString(), new Response(objects));

		} finally {
			LOGGER.info("command processing finished");
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/SIGN_UP_DEVICE", "/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/SIGN_UP_DEVICE"})
	public ModelAndView signUpDevice_O2(HttpServletRequest request,
			@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result,
			@PathVariable("community") String community) {
		LOGGER.info("command processing started");
		try {
			if (result.hasErrors()) {
				List<ObjectError> objectErrors = result.getAllErrors();

				for (ObjectError objectError : objectErrors) {
					throw ValidationException.getInstance(objectError.getDefaultMessage());
				}
			}

			String remoteAddr = Utils.getIpFromRequest(request);
			userDeviceDetailsDto.setIpAddress(remoteAddr);
			userDeviceDetailsDto.setCOMMUNITY_NAME(community);

			AccountCheckDTO accountCheckDTO = userService.registerUser(userDeviceDetailsDto, false);

			final Object[] objects = new Object[] { accountCheckDTO };
			proccessRememberMeToken(objects);
			
			return new ModelAndView(view, Response.class.toString(), new Response(objects));

		} finally {
			LOGGER.info("command processing finished");
		}
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}