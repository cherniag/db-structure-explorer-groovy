package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.ServiceConfigDto;
import mobi.nowtechnologies.server.editor.UserAgentRequestEditor;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;
import mobi.nowtechnologies.server.service.versioncheck.UserAgentRequest;
import mobi.nowtechnologies.server.service.versioncheck.VersionCheckResponse;
import mobi.nowtechnologies.server.service.versioncheck.VersionCheckService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class ServiceConfigController extends CommonController {
    @Resource
    private VersionCheckService versionCheckService;

    @Resource
    private CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(UserAgentRequest.class, new UserAgentRequestEditor());
    }

    @RequestMapping(method = GET,
            value = {
                    "**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}|5\\.[0-2]{1,3}|6\\.1|6\\.2}/SERVICE_CONFIG"
            })
    public Response getServiceConfig(
            @RequestHeader("User-Agent") UserAgentRequest userAgent,
            @RequestParam("APP_VERSION") String appVersion,
            @PathVariable("community") String community,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("SERVICE_CONFIG started: userName [{}], community [{}], resolution [{}], deviceUID [{}]", userName, community, deviceUID);

            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            ServiceConfigDto dto = convert(versionCheckService.check(userAgent), user);

            return new Response(new Object[]{dto});
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("SERVICE_CONFIG  finished");
        }
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    public ModelAndView badParameters(ConversionNotSupportedException exception, HttpServletResponse response) {
        return sendResponse(exception, response, BAD_REQUEST, false);
    }

    private ServiceConfigDto convert(VersionCheckResponse response, User user) {
        VersionCheckStatus status = response.getStatus();
        String message = communityResourceBundleMessageSource.getMessage(user.getCommunityRewriteUrl(), response.getMessageKey(), null, null);
        String link = response.getUri().toString();

        return new ServiceConfigDto(status, message, link);
    }


}
