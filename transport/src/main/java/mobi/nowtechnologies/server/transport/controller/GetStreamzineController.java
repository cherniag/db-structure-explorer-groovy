package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.assembler.StreamzineUpdateAsm;
import mobi.nowtechnologies.server.dto.streamzine.StreamzineUpdateDto;
import mobi.nowtechnologies.server.editor.ResolutionParameterEditor;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import mobi.nowtechnologies.server.service.streamzine.StreamzineNotAvailable;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import mobi.nowtechnologies.server.shared.dto.ContentDtoResult;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedSinceHeader;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedDefaultValue.ZERO;
import static mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedUtils.checkNotModified;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class GetStreamzineController extends CommonController {
    @Resource(name = "streamzineUpdateService")
    private StreamzineUpdateService streamzineUpdateService;

    @Resource(name = "streamzineUpdateAsm")
    private StreamzineUpdateAsm streamzineUpdateAsm;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Resolution.class, new ResolutionParameterEditor());
    }

    @RequestMapping(method = GET,
            value = {
                    "**/{community}/{apiVersion:6.3}/GET_STREAMZINE",
                    "**/{community}/{apiVersion:6.4}/GET_STREAMZINE",
                    "**/{community}/{apiVersion:6.5}/GET_STREAMZINE"
            })
    public Response getUpdateWithCache(@RequestParam("APP_VERSION") String appVersion,
                                       @PathVariable("community") String community,
                                       @RequestParam("USER_NAME") String userName,
                                       @RequestParam("USER_TOKEN") String userToken,
                                       @RequestParam("TIMESTAMP") String timestamp,
                                       @RequestParam("WIDTHXHEIGHT") Resolution resolution,
                                       @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                                       @IfModifiedSinceHeader(defaultValue = ZERO) Long modifiedSince,
                                       HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getResponse(community, userName, userToken, timestamp, resolution, deviceUID, request, response, modifiedSince);
    }


    @RequestMapping(method = POST,
            value = {
                    "**/{community}/{apiVersion:6.1}/GET_STREAMZINE",
                    "**/{community}/{apiVersion:6.2}/GET_STREAMZINE",
            })
    public Response getUpdate(@RequestParam("APP_VERSION") String appVersion,
                              @PathVariable("community") String community,
                              @RequestParam("USER_NAME") String userName,
                              @RequestParam("USER_TOKEN") String userToken,
                              @RequestParam("TIMESTAMP") String timestamp,
                              @RequestParam("WIDTHXHEIGHT") Resolution resolution,
                              @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                              HttpServletResponse response) throws Exception {
        return getResponse(community, userName, userToken, timestamp, resolution, deviceUID, null, response, null);
    }

    private Response getResponse(String community, String userName, String userToken, String timestamp, Resolution resolution, String deviceUID, HttpServletRequest request, HttpServletResponse response, Long lastUpdateFromClient) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("GET_STREAMZINE started: userName [{}], community [{}], resolution [{}], deviceUID [{}]", userName, community, deviceUID);

            streamzineUpdateService.checkAvailability(community);

            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            Date date = getDateForStreamzine(lastUpdateFromClient);

            boolean checkCaching = ((request != null) && (lastUpdateFromClient != null));


            ContentDtoResult<Update> updateDtoResult = streamzineUpdateService.getUpdate(date, user, community, checkCaching);

            if (checkCaching) {
                Long lastUpdateTime = updateDtoResult.getLastUpdatedTime();
                if (checkNotModified(lastUpdateTime, request, response)) {
                    return null;
                }
            }

            LOGGER.debug("found update {} for {}", updateDtoResult.getContent(), date);

            StreamzineUpdateDto dto = streamzineUpdateAsm.convertOne(updateDtoResult.getContent(), community, resolution.withDeviceType(user.getDeviceType().getName()), getCurrentApiVersion());

            LOGGER.debug("StreamzineUpdateDto: [{}]", dto);

            return new Response(new Object[]{dto});
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("GET_STREAMZINE  finished");
        }
    }

    private Date getDateForStreamzine(Long lastUpdateFromClient) {
        return (lastUpdateFromClient == null) ? new Date() : new Date(lastUpdateFromClient);
    }


    @ExceptionHandler(StreamzineNotAvailable.class)
    public ModelAndView handleNotAllowed(StreamzineNotAvailable exception, HttpServletResponse response) {
        return sendResponse(exception, response, NOT_FOUND, false);
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    public ModelAndView badParameters(ConversionNotSupportedException exception, HttpServletResponse response) {
        return sendResponse(exception, response, BAD_REQUEST, false);
    }


}
