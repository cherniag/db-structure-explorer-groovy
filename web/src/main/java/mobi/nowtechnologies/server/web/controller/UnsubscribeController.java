package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.web.payment.UnsubscribeDto;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;
import mobi.nowtechnologies.server.web.validator.UnsubscribeValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class UnsubscribeController extends CommonController {
	
	public static final String VIEW_UNSUBSCRIBE = "/unsubscribe";
	public static final String PAGE_UNSUBSCRIBE = PaymentsController.SCOPE_PREFIX+"/unsubscribe.html";
	
	private UserService userService;
	
	@InitBinder(UnsubscribeDto.NAME)
	public void initBinder(HttpServletRequest request, WebDataBinder binder) {
		binder.setValidator(new UnsubscribeValidator());
	}
	
	@RequestMapping(value=PAGE_UNSUBSCRIBE, method=RequestMethod.GET)
	public ModelAndView getUnsubscribePage(@PathVariable("scopePrefix") String scopePrefix) {
		ModelAndView modelAndView = new ModelAndView(scopePrefix+VIEW_UNSUBSCRIBE);
			modelAndView.addObject(UnsubscribeDto.NAME, new UnsubscribeDto());
		return modelAndView;
	}
	
	@RequestMapping(value=PAGE_UNSUBSCRIBE, method=RequestMethod.POST)
	public ModelAndView unsubscribe(HttpServletRequest request, @PathVariable("scopePrefix") String scopePrefix,
			@Valid @ModelAttribute(UnsubscribeDto.NAME) UnsubscribeDto dto,
			BindingResult result,
			@CookieValue(value = CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME) String communityURL) {

        ModelAndView modelAndView = new ModelAndView(scopePrefix + VIEW_UNSUBSCRIBE);
			
		if (result.hasErrors()) {
			modelAndView.addObject("result", "fail");
		} else {
			userService.unsubscribeUser(getSecurityContextDetails().getUserId(), dto);
			modelAndView.addObject("result", "successful");
		}
		return modelAndView;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}