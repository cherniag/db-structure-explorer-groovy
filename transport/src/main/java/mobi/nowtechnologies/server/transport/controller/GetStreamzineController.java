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
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.web.spring.IfModifiedSinceHeader;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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
            })
    public Response getUpdateWithCache(@RequestParam("APP_VERSION") String appVersion,
                              @PathVariable("community") String community,
                              @RequestParam("USER_NAME") String userName,
                              @RequestParam("USER_TOKEN") String userToken,
                              @RequestParam("TIMESTAMP") String timestamp,
                              @RequestParam("WIDTHXHEIGHT") Resolution resolution,
                              @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
                              @IfModifiedSinceHeader Long modifiedSince,
                              ServletWebRequest webRequest) throws Exception {
        return getResponse(community, userName, userToken, timestamp, resolution, deviceUID, webRequest, modifiedSince);
    }



    @RequestMapping(method = POST,
            value = {
                    "**/{community}/{apiVersion:6.1}/GET_STREAMZINE",
                    "**/{community}/{apiVersion:6.2}/GET_STREAMZINE",
                    "**/{community}/{apiVersion:6.3}/GET_STREAMZINE"
            })
    public Response getUpdate(@RequestParam("APP_VERSION") String appVersion,
                              @PathVariable("community") String community,
                              @RequestParam("USER_NAME") String userName,
                              @RequestParam("USER_TOKEN") String userToken,
                              @RequestParam("TIMESTAMP") String timestamp,
                              @RequestParam("WIDTHXHEIGHT") Resolution resolution,
                              @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        return getResponse(community, userName, userToken, timestamp, resolution, deviceUID, null, null);
    }

    private Response getResponse(String community, String userName, String userToken, String timestamp, Resolution resolution, String deviceUID, ServletWebRequest webRequest, Long lastUpdateFromClient) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            LOGGER.info("GET_STREAMZINE started: userName [{}], community [{}], resolution [{}], deviceUID [{}]", userName, community, deviceUID);

            streamzineUpdateService.checkAvailability(community);

            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            Date date = getDateForStreamzine(lastUpdateFromClient);

            Update update = streamzineUpdateService.getUpdate(date, user, community);

            boolean checkCaching = ((webRequest != null) && (lastUpdateFromClient != null));
            if (checkCaching){
                Long lastUpdateTime = update.getDate().getTime();
                if (webRequest.checkNotModified(lastUpdateTime)){
                    return null;
                }
            }

            LOGGER.debug("found update {} for {}", update, date);

            StreamzineUpdateDto dto = streamzineUpdateAsm.convertOne(update, community, resolution.withDeviceType(user.getDeviceType().getName()));

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
        return (lastUpdateFromClient == null || lastUpdateFromClient == 0)? new Date() : new Date(lastUpdateFromClient);
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
