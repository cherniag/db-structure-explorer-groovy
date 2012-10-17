package mobi.nowtechnologies.server.admin.controller;

import java.util.List;

import mobi.nowtechnologies.server.dto.CommunityDto;
import mobi.nowtechnologies.server.service.AdminUserService;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
@Controller
public class HomeController extends AbstractCommonController {
	
	private AdminUserService adminUserService;
	
	@RequestMapping(value={"/"}, method=RequestMethod.GET)
	public ModelAndView getHomePage() {
		final String communityUrl = RequestUtils.getCommunityURL();
		String viewName = "home";
		if(communityUrl == null)
		{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			List<CommunityDto> communitiesbyUser = adminUserService.getCommunitiesbyUser(authentication.getName());
			viewName = "redirect:/?community="+communitiesbyUser.get(0).getUrl();
		}
		
		ModelAndView modelAndView = new ModelAndView(viewName);
		return modelAndView;
	}

	public void setAdminUserService(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}
}