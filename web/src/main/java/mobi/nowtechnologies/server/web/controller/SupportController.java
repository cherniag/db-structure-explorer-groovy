package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.ContactUsDto;
import mobi.nowtechnologies.server.web.validator.ContactUsDtoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
@Controller
public class SupportController extends CommonController{
	private static final Logger LOGGER = LoggerFactory.getLogger(SupportController.class);
	
	private UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@InitBinder(ContactUsDto.NAME)
	public void initBinder(HttpServletRequest request, WebDataBinder binder) {
		binder.setValidator(new ContactUsDtoValidator());
	}
	
	@RequestMapping(value = "contact_us.html", method = RequestMethod.GET)
	public ModelAndView getContactUsPage(HttpServletRequest request) {
		LOGGER.debug("input parameters request [{}]", request);

		int userId = getUserId();
		User user = userService.findById(userId);

        ContactUsDto contactUsDto = new ContactUsDto();
        contactUsDto.setEmail(user.getUserName());
        contactUsDto.setName(user.getDisplayName());

		ModelAndView modelAndView = new ModelAndView("contact_us");
		modelAndView.getModelMap().put(ContactUsDto.NAME, contactUsDto);
		
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
	
	@RequestMapping(value = "contact_us.html", method = RequestMethod.POST)
	public ModelAndView sendEmailToSupport(HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute(ContactUsDto.NAME) ContactUsDto dto, BindingResult result) {
		LOGGER.debug("input parameters request, response, dto, result: [{}], [{}], [{}], [{}]", new Object[]{request, response, dto, result});
		boolean sentStatus = false;
		if (!result.hasErrors()) {
			userService.contactWithUser(dto.getEmail(), dto.getName(), dto.getSubject());
			sentStatus=true;
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		ModelAndView modelAndView = new ModelAndView("contact_us");
			modelAndView.addObject("sentStatus", sentStatus);
			modelAndView.addObject(ContactUsDto.NAME, dto);
		LOGGER.debug("Output parameter modelAndView=[{}]", modelAndView);
		return modelAndView;
	}
}