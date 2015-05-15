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
import mobi.nowtechnologies.server.transport.controller.core.CommonController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
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
                    value = {"**/{community}/{apiVersion:6.12}/GET_STREAMZINE", "**/{community}/{apiVersion:6.11}/GET_STREAMZINE", "**/{community}/{apiVersion:6.10}/GET_STREAMZINE",
                             "**/{community}/{apiVersion:6.9}/GET_STREAMZINE",
                        "**/{community}/{apiVersion:6.8}/GET_STREAMZINE", "**/{community}/{apiVersion:6.7}/GET_STREAMZINE", "**/{community}/{apiVersion:6.6}/GET_STREAMZINE",
                        "**/{community}/{apiVersion:6.5}/GET_STREAMZINE", "**/{community}/{apiVersion:6.4}/GET_STREAMZINE", "**/{community}/{apiVersion:6.3}/GET_STREAMZINE"})
    public Response getUpdateWithCache(@PathVariable("community") String community, @RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken,
                                       @RequestParam("TIMESTAMP") String timestamp, @RequestParam("WIDTHXHEIGHT") Resolution resolution,
                                       @RequestParam(required = false, value = "DEVICE_UID") String deviceUID, HttpServletResponse response) throws Exception {
        Response update = getResponse(community, userName, userToken, timestamp, resolution, deviceUID, true);

        setMandatoryLastModifiedHeader(response);

        return update;
    }


    @RequestMapping(method = POST,
                    value = {"**/{community}/{apiVersion:6.2}/GET_STREAMZINE", "**/{community}/{apiVersion:6.1}/GET_STREAMZINE"})
    public Response getUpdate(@PathVariable("community") String community, @RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken,
                              @RequestParam("TIMESTAMP") String timestamp, @RequestParam("WIDTHXHEIGHT") Resolution resolution, @RequestParam(required = false, value = "DEVICE_UID") String deviceUID)
        throws Exception {
        return getResponse(community, userName, userToken, timestamp, resolution, deviceUID, false);
    }

    private Response getResponse(String community, String userName, String userToken, String timestamp, Resolution resolution, String deviceUID, boolean includePlayer) throws Exception {
        streamzineUpdateService.checkAvailability(community);

        User user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

        Date date = new Date();

        Update update = streamzineUpdateService.getUpdate(date, user, community);

        LOGGER.debug("found update {} for {}", update, date);

        StreamzineUpdateDto dto = streamzineUpdateAsm.convertOne(update, community, resolution.withDeviceType(user.getDeviceType().getName()), includePlayer);

        LOGGER.debug("StreamzineUpdateDto: [{}]", dto);

        return new Response(new Object[] {dto});
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
