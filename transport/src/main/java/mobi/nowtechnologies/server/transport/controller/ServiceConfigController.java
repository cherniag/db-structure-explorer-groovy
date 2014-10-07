package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.dto.transport.ServiceConfigDto;
import mobi.nowtechnologies.server.editor.UserAgentRequestEditor;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheckStatus;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.versioncheck.UserAgentRequest;
import mobi.nowtechnologies.server.service.versioncheck.VersionCheckResponse;
import mobi.nowtechnologies.server.service.versioncheck.VersionCheckService;
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
    @Resource
    private CommunityRepository communityRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(UserAgentRequest.class, new UserAgentRequestEditor(communityRepository));
    }

    @RequestMapping(method = GET,
            value = {
                    "**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}|5\\.[0-2]{1,3}|6\\.0|6\\.1|6\\.2}/SERVICE_CONFIG"
            })
    public Response getServiceConfig(
            @RequestHeader("User-Agent") UserAgentRequest userAgent,
            @PathVariable("community") String community) throws Exception {
        Exception ex = null;
        try {
            LOGGER.info("SERVICE_CONFIG started: community [{}], userAgent [{}]", community, userAgent);

            ServiceConfigDto dto = convert(versionCheckService.check(userAgent), userAgent);

            LOGGER.info("SERVICE_CONFIG response [{}]", dto);
            return new Response(new Object[]{dto});
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(null, community, null, null, null, ex);
            LOGGER.info("SERVICE_CONFIG finished");
        }
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    public ModelAndView badParameters(HttpServletResponse response) {
        response.setStatus(BAD_REQUEST.value());
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage("A required HTTP header was not specified.");
        return buildModelAndView(errorMessage);
    }

    private ServiceConfigDto convert(VersionCheckResponse response, UserAgentRequest userAgent) {
        VersionCheckStatus status = response.getStatus();
        String message = getMessage(response, userAgent);
        String link = response.getUri();
        String imageFileName = response.getImageFileName();

        return new ServiceConfigDto(status, message, link, imageFileName);
    }

    private String getMessage(VersionCheckResponse response, UserAgentRequest userAgent) {
        String messageKey = response.getMessageKey();
        if(messageKey != null) {
            String rewriteUrlParameter = userAgent.getCommunity().getRewriteUrlParameter();

            String message = communityResourceBundleMessageSource.getMessage(rewriteUrlParameter, messageKey, null, null);

            if(messageKey.equals(message)) {
                LOGGER.error("Not found message or is the same as key for [{}] and community [{]]", messageKey, rewriteUrlParameter);
                return null;
            }

            return message;
        } else {
            return null;
        }
    }
}
