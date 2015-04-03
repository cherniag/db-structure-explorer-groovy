package mobi.nowtechnologies.server.transport.serviceconfig;

import mobi.nowtechnologies.server.transport.serviceconfig.dto.ServiceConfigDto;
import mobi.nowtechnologies.server.transport.serviceconfig.editor.UserAgentRequestEditor;
import mobi.nowtechnologies.server.persistence.domain.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.versioncheck.VersionCheckStatus;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.service.versioncheck.UserAgentRequest;
import mobi.nowtechnologies.server.versioncheck.VersionCheckResponse;
import mobi.nowtechnologies.server.versioncheck.VersionCheckService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Set;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
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
                    value = {"**/{community}/{apiVersion:6\\.10}/SERVICE_CONFIG", "**/{community}/{apiVersion:6\\.9}/SERVICE_CONFIG", "**/{community}/{apiVersion:6\\.8}/SERVICE_CONFIG"})
    public Response getServiceConfigWithNewHeader(@RequestHeader("X-User-Agent") UserAgentRequest userAgent, @PathVariable("community") String community) throws Exception {
        return getServiceConfigWithMigratedAndImage(userAgent, community);
    }

    @RequestMapping(method = GET,
                    value = {"**/{community}/{apiVersion:6\\.7}/SERVICE_CONFIG", "**/{community}/{apiVersion:6\\.6}/SERVICE_CONFIG", "**/{community}/{apiVersion:6\\.5}/SERVICE_CONFIG",
                        "**/{community}/{apiVersion:6\\.4}/SERVICE_CONFIG", "**/{community}/{apiVersion:6\\.3}/SERVICE_CONFIG"})
    public Response getServiceConfigWithMigratedAndImage(@RequestHeader("User-Agent") UserAgentRequest userAgent, @PathVariable("community") String community) throws Exception {
        ServiceConfigDto dto = getServiceConfigInternal(userAgent, community, VersionCheckStatus.getAllStatuses());
        return new Response(new Object[] {dto});
    }

    @RequestMapping(method = GET,
                    value = {"**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}|5\\.[0-2]{1,3}|6\\.0|6\\.1|6\\.2}/SERVICE_CONFIG"})
    public Response getServiceConfig(@RequestHeader("User-Agent") UserAgentRequest userAgent, @PathVariable("community") String community) throws Exception {
        ServiceConfigDto dto = getServiceConfigInternal(userAgent, community, VersionCheckStatus.getAllStatusesWithoutMigrated());
        dto.nullifyImage();
        return new Response(new Object[] {dto});
    }


    private ServiceConfigDto getServiceConfigInternal(UserAgentRequest userAgent, String community, Set<VersionCheckStatus> includedStatuses) throws Exception {
        Exception ex = null;
        try {
            LOGGER.info("SERVICE_CONFIG started: community [{}], userAgent [{}]", community, userAgent);

            VersionCheckResponse check = versionCheckService.check(userAgent, includedStatuses);

            ServiceConfigDto dto = convert(check, userAgent);

            LOGGER.info("SERVICE_CONFIG response [{}]", dto);
            return dto;
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
        if (messageKey != null) {
            String rewriteUrlParameter = userAgent.getCommunity().getRewriteUrlParameter();

            String message = communityResourceBundleMessageSource.getMessage(rewriteUrlParameter, messageKey, null, null);

            if (messageKey.equals(message)) {
                LOGGER.error("Not found message or is the same as key for [{}] and community [{]]", messageKey, rewriteUrlParameter);
                return null;
            }

            return message;
        } else {
            return null;
        }
    }
}
