package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: Titov Mykhaylo (titov)
 * 22.07.13 17:24
 */
@Controller
public class ActivateTrialController extends CommonController {

    @RequestMapping(method = RequestMethod.POST, value = {
            "{other:.*}/{communityUri:o2}/{apiVersion:[4-9]\\.[0-9]}/ACTIVATE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:[4-9]\\.[1-9][0-9]}/ACTIVATE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:[1-9][0-9]\\.[0-9]}/ACTIVATE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]}/ACTIVATE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:[4-9]\\.[1-9]\\.[1-9][0-9]{0,2}}/ACTIVATE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:[4-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/ACTIVATE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:[1-9][0-9]\\.[1-9]\\.[1-9][0-9]{0,2}}/ACTIVATE_TRIAL",
            "{other:.*}/{communityUri:o2}/{apiVersion:[1-9][0-9]\\.[1-9][0-9]\\.[1-9][0-9]{0,2}}/ACTIVATE_TRIAL"})
    public ModelAndView activateTrial(@RequestParam("APP_VERSION") String appVersion,
                                      @RequestParam("USER_NAME") String userName,
                                      @RequestParam("USER_TOKEN") String userToken,
                                      @RequestParam("TIMESTAMP") String timestamp,
                                      @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                      @PathVariable("communityUri") String communityUri) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("command processing started");

            user = userService.checkCredentials(userName, userToken, timestamp, communityUri, deviceUID);

            AccountCheckDTO accountCheckDTO = userService.processActivateTrialCommand(user);

            Object[] objects = new Object[]{accountCheckDTO};
            precessRememberMeToken(objects);
            return new ModelAndView(view, Response.class.toString(), new Response(objects));
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, communityUri, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }

}
