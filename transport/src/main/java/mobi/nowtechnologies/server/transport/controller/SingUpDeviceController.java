package mobi.nowtechnologies.server.transport.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
	public ModelAndView signUpDevice(HttpServletRequest request, @Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME)UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result) throws Exception {
        LOGGER.info("SIGN_UP_DEVICE Started for [{}]",userDeviceDetailsDto);
        User user = null;
        Exception ex = null;
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
			user = userService.findByNameAndCommunity(accountCheckDTO.getUserName(), userDeviceDetailsDto.getCommunityName());
						
			accountCheckDTO = userService.applyInitialPromotion(user);
			final Object[] objects = new Object[]{accountCheckDTO};
			precessRememberMeToken(objects);
			
			return new ModelAndView(view, Response.class.toString(), new Response(objects));
		}catch(Exception e){
			ex = e;
			throw e;
		} finally {
			logProfileData(null, null, userDeviceDetailsDto, null, user, ex);
            LOGGER.info("SIGN_UP_DEVICE Finished for [{}]",userDeviceDetailsDto);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = {
            "/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/SIGN_UP_DEVICE",
            "/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/SIGN_UP_DEVICE",
			"*/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/SIGN_UP_DEVICE",
            "*/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/SIGN_UP_DEVICE"
    })
	public ModelAndView signUpDevice_V3GT(HttpServletRequest request,
			@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result) throws Exception {
        LOGGER.info("SIGN_UP_DEVICE Started for [{}]",userDeviceDetailsDto);
        
        User user = null;
        Exception ex = null;
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
			user = userService.findByNameAndCommunity(accountCheckDTO.getUserName(), userDeviceDetailsDto.getCommunityName());

			final Object[] objects = new Object[] { accountCheckDTO };
			precessRememberMeToken(objects);
			
			return new ModelAndView(view, Response.class.toString(), new Response(objects));
		}catch(Exception e){
			ex = e;
			throw e;
		} finally {
			logProfileData(null, null, userDeviceDetailsDto, null, user, ex);
            LOGGER.info("SIGN_UP_DEVICE Finished for [{}]",userDeviceDetailsDto);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = 
		{
			"/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/SIGN_UP_DEVICE", 
			"/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/SIGN_UP_DEVICE",
			"*/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/SIGN_UP_DEVICE", 
			"*/{community:o2}/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/SIGN_UP_DEVICE",
            "*/{community:o2}/{apiVersion:4\\.0}/SIGN_UP_DEVICE"
		})
	public ModelAndView signUpDevice_O2(HttpServletRequest request,
			@Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result,
			@PathVariable("community") String community) {
		LOGGER.info("SIGN_UP_DEVICE Started for [{}] community[{}]",userDeviceDetailsDto, community);
		
		User user = null;
	    Exception ex = null;
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
		        user = userService.findByNameAndCommunity(accountCheckDTO.getUserName(), userDeviceDetailsDto.getCommunityName());

		        final Object[] objects = new Object[] { accountCheckDTO };
		        precessRememberMeToken(objects);

		        return new ModelAndView(view, Response.class.toString(), new Response(objects));
        }catch (ValidationException ve){
        	ex = ve;
            LOGGER.error("SIGN_UP_DEVICE Validation error [{}] for [{}] community[{}]",ve.getMessage(), userDeviceDetailsDto, community);
            throw ve;
        }catch (RuntimeException re){
        	ex = re;
            LOGGER.error("SIGN_UP_DEVICE error [{}] for [{}] community[{}]",re.getMessage(), userDeviceDetailsDto, community);
            throw re;
		} finally {
			logProfileData(null, community, userDeviceDetailsDto, null, user, ex);
            LOGGER.info("SIGN_UP_DEVICE Finished for [{}] community[{}]",userDeviceDetailsDto, community);
		}
	}

    @RequestMapping(method = RequestMethod.POST, value =
            {
                    "*/{community:o2}/{apiVersion:4\\.0}/SIGN_UP_DEVICE.json"
            }, produces = "application/json")
    public @ResponseBody Response signUpDevice_O2Json(HttpServletRequest request,
                                 @Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result,
                                 @PathVariable("community") String community) {
        return (Response)signUpDevice_O2(request, userDeviceDetailsDto, result, community).getModelMap().get(MODEL_NAME);
    }

    @RequestMapping(method = RequestMethod.POST, value ={
            "*/{community:.*}/{apiVersion:5\\.0}/SIGN_UP_DEVICE",
            "*/{community:.*}/{apiVersion:5\\.0}/SIGN_UP_DEVICE.json"
    })
    public ModelAndView signUpDeviceWithAcceptHeaderSupporting(HttpServletRequest request,
                                                      @Valid @ModelAttribute(UserDeviceRegDetailsDto.NAME) UserDeviceRegDetailsDto userDeviceDetailsDto, BindingResult result,
                                                      @PathVariable("community") String community,
                                                      @PathVariable("apiVersion") String apiVersion) {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = signUpDevice_O2(request, userDeviceDetailsDto, result, community);
        modelAndView.setViewName(defaultViewName);
        return modelAndView;
    }

    public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
