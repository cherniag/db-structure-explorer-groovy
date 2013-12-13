package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MessageService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.NewsDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	// Support community o2, apiVersion 3.6 and higher
	@RequestMapping(method = RequestMethod.POST, value = {
			"**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/GET_NEWS"
    })
	public ModelAndView getNews_O2(
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
			@RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
			@PathVariable("community") String community,
			@PathVariable("apiVersion") String apiVersion
            ) throws Exception {

		User user = null;
		Exception ex = null;
		try {
			LOGGER.info("command processing started");
            if (isValidDeviceUID(deviceUID)) {
                user = userService.checkCredentials(userName, userToken, timestamp, community, deviceUID);
            }
            else {
                user = userService.checkCredentials(userName, userToken, timestamp, community);
            }

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
}
