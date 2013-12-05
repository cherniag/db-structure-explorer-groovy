package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MessageService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.NewsDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class GetNewsController extends CommonController {

	private UserService userService;
	private MessageService messageService;
    private AccCheckController accCheckController;

    public void setAccCheckController(AccCheckController accCheckController) {
        this.accCheckController = accCheckController;
    }

    public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	@RequestMapping(method = RequestMethod.POST, value = { "/GET_NEWS", "*/GET_NEWS" })
	public ModelAndView getNews(
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis) throws Exception {

		User user = null;
		Exception ex = null;
		try {
			LOGGER.info("command processing started");
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

			NewsDto newsDto = messageService.processGetNewsCommand(user, communityName, lastUpdateNewsTimeMillis, false);

            AccountCheckDTO accountCheck = accCheckController.processAccCheckBeforeO2Releases(user);

			return buildModelAndView(accountCheck, newsDto);
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			logProfileData(null, communityName, null, null, user, ex);
			LOGGER.info("command processing finished");
		}
	}

	// Support community o2, apiVersion 3.6 and higher
	// @RequestMapping(method = RequestMethod.POST, value =
	// {"/{community:o2}/{apiVersion:(?:[3-9]|[1-9][0-9])\\.(?:[6-9]|[1-9][0-9]{1,2})}/GET_NEWS",
	// "/{community:o2}/{apiVersion:(?:[3-9]|[1-9][0-9])\\.(?:[6-9]|[1-9][0-9]{1,2})\\.[1-9][0-9]{0,2}}/GET_NEWS"})
	@RequestMapping(method = RequestMethod.POST, value = {
			"/{community:o2}/{apiVersion:[3-9]\\.[6-9]}/GET_NEWS",
			"/{community:o2}/{apiVersion:[3-9]\\.[1-9][0-9]}/GET_NEWS",
			"/{community:o2}/{apiVersion:[1-9][0-9]\\.[6-9]}/GET_NEWS",
			"/{community:o2}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]}/GET_NEWS",
			"/{community:o2}/{apiVersion:[3-9]\\.[6-9]\\.[1-9][0-9]{0,2}}/GET_NEWS",
			"/{community:o2}/{apiVersion:[3-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/GET_NEWS",
			"/{community:o2}/{apiVersion:[1-9][0-9]\\.[6-9]\\.[1-9][0-9]{0,2}}/GET_NEWS",
			"/{community:o2}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/GET_NEWS",
			"*/{community:o2}/{apiVersion:[3-9]\\.[6-9]}/GET_NEWS",
			"*/{community:o2}/{apiVersion:[3-9]\\.[1-9][0-9]}/GET_NEWS", 
			"*/{community:o2}/{apiVersion:[1-9][0-9]\\.[6-9]}/GET_NEWS",  
			"*/{community:o2}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]}/GET_NEWS",  
			"*/{community:o2}/{apiVersion:[3-9]\\.[6-9]\\.[1-9][0-9]{0,2}}/GET_NEWS", 
			"*/{community:o2}/{apiVersion:[3-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/GET_NEWS",
			"*/{community:o2}/{apiVersion:[1-9][0-9]\\.[6-9]\\.[1-9][0-9]{0,2}}/GET_NEWS",
			"*/{community:o2}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/GET_NEWS",
            "*/{community:o2}/{apiVersion:4.0}/GET_NEWS"})
	public ModelAndView getNews_O2(
			@RequestParam("APP_VERSION") String appVersion,
			@RequestParam("COMMUNITY_NAME") String communityName,
			@RequestParam("API_VERSION") String apiVersion,
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
			@RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
			@PathVariable("community") String community) throws Exception {

		User user = null;
		Exception ex = null;
		try {
			LOGGER.info("command processing started");
			user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);

			NewsDto newsDto= messageService.processGetNewsCommand(user, community, lastUpdateNewsTimeMillis, true);

            AccountCheckDTO accountCheck = accCheckController.processAccCheck(user);

			return buildModelAndView(accountCheck, newsDto);
		} catch (Exception e) {
			ex = e;
			throw e;
		} finally {
			logProfileData(deviceUID, community, null, null, user, ex);
			LOGGER.info("command processing finished");
		}
	}

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:o2}/{apiVersion:4\\.0}/GET_NEWS.json"
    }, produces = "application/json")
    public @ResponseBody Response getNews_O2Json(
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @PathVariable("community") String community) throws Exception {
        return (Response)getNews_O2(appVersion, communityName, apiVersion, userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, community).getModelMap().get(MODEL_NAME);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:.*}/{apiVersion:5\\.0}/GET_NEWS",
            "*/{community:.*}/{apiVersion:5\\.0}/GET_NEWS.json"
    })
    public ModelAndView getNewsAcceptHeaderSupporting(
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @PathVariable("community") String community) throws Exception {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = getNews_O2(appVersion, community, apiVersion, userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, community);
        modelAndView.setViewName(defaultViewName);
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "*/{community:o2}/{apiVersion:4\\.1}/GET_NEWS",
            "*/{community:o2}/{apiVersion:4\\.1}/GET_NEWS.json",
            "*/{community:o2}/{apiVersion:4\\.2}/GET_NEWS",
            "*/{community:o2}/{apiVersion:4\\.2}/GET_NEWS.json"
    })
    public ModelAndView getNews_O2AcceptHeaderSupport(
            @RequestParam("APP_VERSION") String appVersion,
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("API_VERSION") String apiVersion,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @PathVariable("community") String community) throws Exception {
        apiVersionThreadLocal.set(apiVersion);

        ModelAndView modelAndView = getNews_O2(appVersion, communityName, apiVersion, userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, community);
        modelAndView.setViewName(defaultViewName);

        return modelAndView;

    }
}
