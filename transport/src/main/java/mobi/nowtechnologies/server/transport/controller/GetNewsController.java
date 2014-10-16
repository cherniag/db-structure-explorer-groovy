package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MessageService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.ContentDtoResult;
import mobi.nowtechnologies.server.shared.dto.NewsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedSinceHeader;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.REGISTERED;
import static mobi.nowtechnologies.server.shared.web.spring.modifiedsince.IfModifiedDefaultValue.ZERO;

/**
 * @author Titov Mykhaylo (titov)
 */
@Controller
public class GetNewsController extends CommonController {

    @Resource
    private MessageService messageService;


    @RequestMapping(method = RequestMethod.GET, value = {
            "**/{community}/{apiVersion:6\\.3}/GET_NEWS"
    })
    public ModelAndView getNewsWithBannersWithCaching(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID,
            @IfModifiedSinceHeader(defaultValue = ZERO) Long modifiedSince,
            ServletWebRequest webRequest
    ) throws Exception {
        return getNews(userName, userToken, timestamp, modifiedSince, deviceUID, true, webRequest, ACTIVATED);
    }


    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:6\\.2}/GET_NEWS",
    })
    public ModelAndView getNewsWithBanners(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        return getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, true, null, ACTIVATED);
    }


    // Support community o2, apiVersion 3.6 and higher
    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}}/GET_NEWS",
            "**/{community}/{apiVersion:5\\.[0-4]{1,3}}/GET_NEWS",
            "**/{community}/{apiVersion:6\\.0}/GET_NEWS",
            "**/{community}/{apiVersion:6\\.1}/GET_NEWS"
    })
    public ModelAndView getNews_O2(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        return getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, false, null, ACTIVATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = {
            "**/{community}/5.5/GET_NEWS",
            "**/{community}/5.5.0/GET_NEWS"
    })
    public ModelAndView getNews_v5(
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
            @RequestParam(required = false, value = "DEVICE_UID") String deviceUID
    ) throws Exception {
        return getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, false, null, REGISTERED, ACTIVATED);
    }

    private ModelAndView getNews(String userName,
                                 String userToken,
                                 String timestamp,
                                 Long lastUpdateNewsTimeMillis,
                                 String deviceUID,
                                 boolean withBanners,
                                 ServletWebRequest servletWebRequest, ActivationStatus... activationStatuses) throws Exception {
        User user = null;
        Exception ex = null;
        String community = getCurrentCommunityUri();
        try {
            LOGGER.info("command processing started");

            user = checkUser(userName, userToken, timestamp, deviceUID, false, activationStatuses);

            boolean checkCaching = ((servletWebRequest != null) & (lastUpdateNewsTimeMillis != null));

            ContentDtoResult<NewsDto> newsDtoResult = messageService.processGetNewsCommand(user, community, lastUpdateNewsTimeMillis, withBanners, checkCaching);

            if (checkCaching) {
                Long lastUpdateTime = newsDtoResult.getLastUpdatedTime();
                if (servletWebRequest.checkNotModified(lastUpdateTime)) {
                    return null;
                }
            }

            AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false);

            return buildModelAndView(accountCheck, newsDtoResult.getContent());
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            logProfileData(deviceUID, community, null, null, user, ex);
            LOGGER.info("command processing finished");
        }
    }
}
