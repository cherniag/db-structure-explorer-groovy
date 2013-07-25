package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.DrmService;
import mobi.nowtechnologies.server.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class GetPurchasedContentInfoController extends CommonController {
	
	private UserService userService;
	private DrmService drmService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void setDrmService(DrmService drmService) {
		this.drmService = drmService;
	}

	@RequestMapping(method = RequestMethod.POST, value = {"/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}\\.[0-9]{1,3}}/GET_PURCHASED_CONTENT_INFO", "/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}}/GET_PURCHASED_CONTENT_INFO",
			"*/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}\\.[0-9]{1,3}}/GET_PURCHASED_CONTENT_INFO", "*/{apiVersion:[3-9]{1,2}\\.[4-9][0-9]{0,2}}/GET_PURCHASED_CONTENT_INFO"})
	public ModelAndView getPurchasedContentInfo(
				HttpServletRequest request,
				@RequestParam("APP_VERSION") String appVersion,
				@RequestParam("COMMUNITY_NAME") String communityName,
				@RequestParam("API_VERSION") String apiVersion,
				@RequestParam("USER_NAME") String userName,
				@RequestParam("USER_TOKEN") String userToken,
				@RequestParam("TIMESTAMP") String timestamp) throws Exception {
		User user = null;
		Exception ex = null;
		try {
			LOGGER.info("command proccessing started");
			if (userName == null)
				throw new NullPointerException("The parameter userName is null");
			if (communityName == null)
				throw new NullPointerException("The parameter communityName is null");
			if (null == appVersion)
				throw new NullPointerException("The argument aAppVersion is null");
			if (null == apiVersion)
				throw new NullPointerException("The argument aApiVersion is null");
			if (null == userToken)
				throw new NullPointerException("The argument aUserToken is null");
			if (null == timestamp)
				throw new NullPointerException("The argument aTimestamp is null");

			user = userService.checkCredentials(userName, userToken,
						timestamp, communityName);

			Object[] objects = drmService.getPurchasedContentInfo(user, communityName);
			precessRememberMeToken(objects);

			return new ModelAndView(view, Response.class.toString(), new Response(
						objects));
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			logProfileData(null, communityName, null, null, user, ex);
			LOGGER.info("command processing finished");
		}
	}

}
