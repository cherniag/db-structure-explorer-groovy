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
 * 03.09.13 16:04
 */
@Controller
public class AutoOptInController extends CommonController {

    @RequestMapping(method = RequestMethod.POST, value = {
            "{other:.*}/{communityUri:o2}/{apiVersion:4\\.2}/AUTO_OPT_IN",
            "{other:.*}/{communityUri:o2}/{apiVersion:4\\.2}/AUTO_OPT_IN.json"
    })
    public ModelAndView autoOptIn(@RequestParam("APP_VERSION") String appVersion,
                                  @RequestParam("USER_NAME") String userName,
                                  @RequestParam("USER_TOKEN") String userToken,
                                  @RequestParam("TIMESTAMP") String timestamp,
                                  @RequestParam("DEVICE_UID") String deviceUID,
                                  @RequestParam("OTAC_TOKEN") String otac,
                                  @PathVariable("communityUri") String communityUri,
                                  @PathVariable("apiVersion") String apiVersion) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            apiVersionThreadLocal.set(apiVersion);

            LOGGER.info("command processing started");

            user = userService.autoOptIn(userName, userToken, timestamp, communityUri, deviceUID, otac);

            final AccountCheckDTO accountCheckDTO = userService.proceessAccountCheckCommandForAuthorizedUser(user.getId(),
                    null, null, null);

            final Object[] objects = new Object[] { accountCheckDTO };
            precessRememberMeToken(objects);

            return new ModelAndView(defaultViewName, MODEL_NAME, new Response(objects));
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, communityUri, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }
}
