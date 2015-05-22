package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.service.MergeResult;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: Titov Mykhaylo (titov) 03.09.13 16:04
 */
@Controller
public class AutoOptInController extends CommonController {

    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{communityUri}/{apiVersion:6\\.11}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.10}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.9}/AUTO_OPT_IN",
                        "**/{communityUri}/{apiVersion:6\\.8}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.7}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.6}/AUTO_OPT_IN",
                        "**/{communityUri}/{apiVersion:6\\.5}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.4}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.3}/AUTO_OPT_IN",
                        "**/{communityUri}/{apiVersion:6\\.2}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.1}/AUTO_OPT_IN", "**/{communityUri}/{apiVersion:6\\.0}/AUTO_OPT_IN"})
    public ModelAndView autoOptInWithCheckReactivation(@PathVariable("communityUri") String communityUri, @RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken,
                                                       @RequestParam("TIMESTAMP") String timestamp, @RequestParam("DEVICE_UID") String deviceUID,
                                                       @RequestParam(value = "OTAC_TOKEN", required = false) String otac) throws Exception {
        return autoOptInCheckImpl(communityUri, userName, userToken, timestamp, deviceUID, otac, true);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{communityUri}/{apiVersion:[4-5]{1}\\.[0-9]{1,3}}/AUTO_OPT_IN"})
    public ModelAndView autoOptIn(@PathVariable("communityUri") String communityUri, @RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken,
                                  @RequestParam("TIMESTAMP") String timestamp, @RequestParam("DEVICE_UID") String deviceUID, @RequestParam(value = "OTAC_TOKEN", required = false) String otac)
        throws Exception {
        return autoOptInCheckImpl(communityUri, userName, userToken, timestamp, deviceUID, otac, false);
    }

    private ModelAndView autoOptInCheckImpl(String communityUri, String userName, String userToken, String timestamp, String deviceUID, String otac, boolean checkReactivation) throws Exception {
        MergeResult mergeResult = userService.autoOptIn(communityUri, userName, userToken, timestamp, deviceUID, otac, checkReactivation);
        AccountCheckDTO accountCheckDTO = accCheckService.processAccCheck(mergeResult, false, false).withHasPotentialPromoCodePromotion(true);

        return buildModelAndView(accountCheckDTO);
    }
}
