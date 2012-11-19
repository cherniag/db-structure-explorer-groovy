package mobi.nowtechnologies.server.web.service.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.CommunityService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.web.security.service.impl.UserDetailsServiceImpl;
import mobi.nowtechnologies.server.shared.web.security.userdetails.UserDetailsImpl;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;
import mobi.nowtechnologies.server.web.security.facebook.FacebookAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.facebook.api.FacebookProfile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class FacebookUserDetailsServiceImpl extends UserDetailsServiceImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookUserDetailsServiceImpl.class);
	
	private CommunityService communityService;
	
	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		throw new UnsupportedOperationException();
	}
	
	private boolean isUserRegistrationAllowed() {
		HttpServletRequest request = RequestUtils.getHttpServletRequest();
		Boolean registrationValue=Boolean.valueOf(request.getParameter(FacebookAuthenticationFilter.REGISTRATION));
		LOGGER.debug("Output parameter registrationValue=[{}]", registrationValue);
		return registrationValue;
	}

	public UserDetails loadUserByFacebookProfile(FacebookProfile facebookProfile) throws UsernameNotFoundException {
		LOGGER.debug("input parameters facebookProfile: [{}]", facebookProfile);
		String communityURL = RequestUtils.getCommunityURL();
		
		String communityName = communityService.getCommunityByUrl(communityURL).getName();
		boolean isNewUser=false;

		User user = userService.findByFacebookId(facebookProfile.getId(), communityName);
		
		UserDetails userDetails = null;
		if (user == null&&isUserRegistrationAllowed()) {
			HttpServletRequest httpServletRequest = RequestUtils.getHttpServletRequest();
			String ipAddress = Utils.getIpFromRequest(httpServletRequest);
			user = userService.registerUser(facebookProfile, communityName, ipAddress);
			isNewUser=true;
		}
		
		if (user != null) userDetails = new UserDetailsImpl(user, isNewUser);

		LOGGER.debug("Output parameter userDetails=[{}]", userDetails);
		return userDetails;
	}

}
