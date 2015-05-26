package mobi.nowtechnologies.server.transport.serviceconfig;

import mobi.nowtechnologies.server.transport.controller.core.ErrorMessage;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.support.UserAgent;
import mobi.nowtechnologies.server.support.editor.UserAgentPropertyEditor;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import mobi.nowtechnologies.server.transport.serviceconfig.dto.ServiceConfigDto;
import mobi.nowtechnologies.server.versioncheck.domain.VersionCheckStatus;
import mobi.nowtechnologies.server.versioncheck.service.VersionCheckResponse;
import mobi.nowtechnologies.server.versioncheck.service.VersionCheckService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Set;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ServiceConfigController extends CommonController {

    @Resource
    VersionCheckService versionCheckService;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(UserAgent.class, new UserAgentPropertyEditor(communityRepository));
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = {"**/{community}/{apiVersion:6\\.12}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.11}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.10}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.9}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.8}/SERVICE_CONFIG"})
    public Response getServiceConfigWithNewHeader(@RequestHeader("X-User-Agent") UserAgent userAgent, @PathVariable("community") String community) throws Exception {
        return getServiceConfigWithMigratedAndImage(userAgent, community);
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = {"**/{community}/{apiVersion:6\\.7}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.6}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.5}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.4}/SERVICE_CONFIG",
                             "**/{community}/{apiVersion:6\\.3}/SERVICE_CONFIG"})
    public Response getServiceConfigWithMigratedAndImage(@RequestHeader("User-Agent") UserAgent userAgent, @PathVariable("community") String community) throws Exception {
        ServiceConfigDto dto = getServiceConfigInternal(userAgent, community, VersionCheckStatus.getAllStatuses());
        return new Response(new Object[] {dto});
    }

    @RequestMapping(method = RequestMethod.GET,
                    value = {"**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}|5\\.[0-2]{1,3}|6\\.0|6\\.1|6\\.2}/SERVICE_CONFIG"})
    public Response getServiceConfig(@RequestHeader("User-Agent") UserAgent userAgent, @PathVariable("community") String community) throws Exception {
        ServiceConfigDto dto = getServiceConfigInternal(userAgent, community, VersionCheckStatus.getAllStatusesWithoutMigrated());
        dto.nullifyImage();
        return new Response(new Object[] {dto});
    }


    private ServiceConfigDto getServiceConfigInternal(UserAgent userAgent, String community, Set<VersionCheckStatus> includedStatuses) throws Exception {
        VersionCheckResponse check = versionCheckService.check(userAgent.getCommunity().getId(), userAgent.getPlatform(), userAgent.getApplicationName(), userAgent.getVersion(), includedStatuses);

        ServiceConfigDto dto = convert(check, userAgent.getCommunity().getRewriteUrlParameter());

        LOGGER.info("SERVICE_CONFIG response [{}]", dto);
        return dto;

    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    public ModelAndView badParameters(HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage("A required HTTP header was not specified.");
        return buildModelAndView(errorMessage);
    }

    private ServiceConfigDto convert(VersionCheckResponse response, String communityName) {
        String message = getMessage(response.getMessageKey(), communityName);
        String link = response.getUri();
        String imageFileName = response.getImageFileName();

        return new ServiceConfigDto(response.getStatus(), message, link, imageFileName);
    }

    private String getMessage(String messageKey, String communityName) {
        if (messageKey != null) {
            String message = communityResourceBundleMessageSource.getMessage(communityName, messageKey, null, null);
            if (messageKey.equals(message)) {
                LOGGER.error("Not found message or is the same as key for [{}] and community [{]]", messageKey, communityName);
                return null;
            }

            return message;
        } else {
            return null;
        }
    }
}
