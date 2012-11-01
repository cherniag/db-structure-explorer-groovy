package mobi.nowtechnologies.server.web.interceptor;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mobi.nowtechnologies.server.dto.CommunityDto;
import mobi.nowtechnologies.server.service.AdminUserService;
import mobi.nowtechnologies.server.shared.web.filter.CommunityResolverFilter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

/**
 * ImplicitObjectsInterceptor stands for adding business objects to all pages and templates
 * @author Alexander Kolpakov (akolpakov)
 * @author dmytro
 *
 */
public class ImplicitObjectsInterceptor extends HandlerInterceptorAdapter {
	
	private AdminUserService adminUserService;
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
				List<CommunityDto> communitiesbyUser = adminUserService.getCommunitiesbyUser(authentication.getName());
				Cookie cookie = WebUtils.getCookie(request, CommunityResolverFilter.DEFAULT_COMMUNITY_COOKIE_NAME);
				if(cookie != null){
					for (CommunityDto communityDto : communitiesbyUser) {
						if (cookie.getValue().equals(communityDto.getUrl())) {
							communityDto.setActive(true);
							break;
						}
					}
				}
				
				modelAndView.getModel().put("communities", communitiesbyUser);
			}
	}

	public void setAdminUserService(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}
}