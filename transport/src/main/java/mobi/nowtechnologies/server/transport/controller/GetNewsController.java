package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MessageService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.NewsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
@Controller
public class GetNewsController extends CommonController {

    @Resource
	private MessageService messageService;

	// Support community o2, apiVersion 3.6 and higher
	@RequestMapping(method = RequestMethod.POST, value = {
			"**/{community}/{apiVersion:3\\.[6-9]|[4-9]{1}\\.[0-9]{1,3}}/GET_NEWS"
    })
	public ModelAndView getNews_O2(
			@RequestParam("USER_NAME") String userName,
			@RequestParam("USER_TOKEN") String userToken,
			@RequestParam("TIMESTAMP") String timestamp,
			@RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
			@RequestParam(required = false, value = "DEVICE_UID") String deviceUID
            ) throws Exception {

		User user = null;
		Exception ex = null;
        String community = getCurrentCommunityUri();
		try {
			LOGGER.info("command processing started");

            user = checkUser(userName, userToken, timestamp, deviceUID, ActivationStatus.ACTIVATED);

			NewsDto newsDto= messageService.processGetNewsCommand(user, community, lastUpdateNewsTimeMillis, true);

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

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
