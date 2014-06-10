package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.assembler.StreamzineUpdateAsm;
import mobi.nowtechnologies.server.dto.streamzine.StreamzineUpdateDto;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.service.streamzine.StreamzineUpdateService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class GetStreamzineController extends CommonController {
    @Resource(name = "streamzineUpdateService")
    private StreamzineUpdateService streamzineUpdateService;

    @Resource(name = "streamzineUpdateAsm")
    private StreamzineUpdateAsm streamzineUpdateAsm;

    @RequestMapping(method = RequestMethod.POST,
            value = {"**/{community:hl_uk}/{apiVersion:6.0}/GET_STREAMZINE"})
    public Response getUpdate(HttpServletResponse response,
                          @RequestParam("APP_VERSION") String appVersion,
                          @PathVariable("community") String community,
                          @PathVariable("apiVersion") String apiVersion,
                          @RequestParam("USER_NAME") String userName,
                          @RequestParam("USER_TOKEN") String userToken,
                          @RequestParam("TIMESTAMP") String timestamp,
                          @RequestParam("widthxheight") String resolution,
                          @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        User user = null;
        Exception ex = null;
        try {
            user = checkUser(userName, userToken, timestamp, deviceUID, false, ActivationStatus.ACTIVATED);

            Date date = new Date();

            Update update = streamzineUpdateService.getUpdate(date, user);

            LOGGER.debug("found update {} for {}", update, date);

            StreamzineUpdateDto dto = streamzineUpdateAsm.convertOne(update);

            return new Response(new Object[]{dto});
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }
}
