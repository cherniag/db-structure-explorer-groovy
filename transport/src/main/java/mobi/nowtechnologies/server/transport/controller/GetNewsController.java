package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.MessageService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import mobi.nowtechnologies.server.shared.dto.NewsDto;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.REGISTERED;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Titov Mykhaylo (titov)
 */
@Controller
public class GetNewsController extends CommonController {

    @Resource
    private MessageService messageService;

    @RequestMapping(method = RequestMethod.GET, value = {"**/{community}/{apiVersion:6\\.12}/GET_NEWS", "**/{community}/{apiVersion:6\\.11}/GET_NEWS", "**/{community}/{apiVersion:6\\.10}/GET_NEWS",
                                                         "**/{community}/{apiVersion:6\\.9}/GET_NEWS", "**/{community}/{apiVersion:6\\.8}/GET_NEWS"})
    public ModelAndView getNewsWithBannersWithOneTimeSubscription(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                                  @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
                                                                  @RequestParam(required = false, value = "DEVICE_UID") String deviceUID, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, true, true, ACTIVATED);

        setMandatoryLastModifiedHeader(response);

        return modelAndView;
    }


    @RequestMapping(method = RequestMethod.GET,
                    value = {"**/{community}/{apiVersion:6\\.7}/GET_NEWS", "**/{community}/{apiVersion:6\\.6}/GET_NEWS", "**/{community}/{apiVersion:6\\.5}/GET_NEWS",
                        "**/{community}/{apiVersion:6\\.4}/GET_NEWS", "**/{community}/{apiVersion:6\\.3}/GET_NEWS"})
    public ModelAndView getNewsWithBannersWithCaching(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                                      @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
                                                      @RequestParam(required = false, value = "DEVICE_UID") String deviceUID, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, true, false, ACTIVATED);

        setMandatoryLastModifiedHeader(response);

        return modelAndView;
    }


    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/{apiVersion:6\\.2}/GET_NEWS"})
    public ModelAndView getNewsWithBanners(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                           @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis,
                                           @RequestParam(required = false, value = "DEVICE_UID") String deviceUID) throws Exception {
        return getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, true, false, ACTIVATED);
    }


    // Support community o2, apiVersion 3.6 and higher
    @RequestMapping(method = RequestMethod.POST,
                    value = {"**/{community}/{apiVersion:6\\.1}/GET_NEWS", "**/{community}/{apiVersion:6\\.0}/GET_NEWS", "**/{community}/{apiVersion:5\\.[0-4]{1,3}}/GET_NEWS",
                        "**/{community}/{apiVersion:3\\.[6-9]|4\\.[0-9]{1,3}}/GET_NEWS"})
    public ModelAndView getNews_O2(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                   @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis, @RequestParam(required = false, value = "DEVICE_UID") String deviceUID)
        throws Exception {
        return getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, false, false, ACTIVATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = {"**/{community}/5.5/GET_NEWS", "**/{community}/5.5.0/GET_NEWS"})
    public ModelAndView getNews_v5(@RequestParam("USER_NAME") String userName, @RequestParam("USER_TOKEN") String userToken, @RequestParam("TIMESTAMP") String timestamp,
                                   @RequestParam(value = "LAST_UPDATE_NEWS", required = false) Long lastUpdateNewsTimeMillis, @RequestParam(required = false, value = "DEVICE_UID") String deviceUID)
        throws Exception {
        return getNews(userName, userToken, timestamp, lastUpdateNewsTimeMillis, deviceUID, false, false, REGISTERED, ACTIVATED);
    }

    private ModelAndView getNews(String userName, String userToken, String timestamp, Long lastUpdateNewsTimeMillis, String deviceUID, boolean withBanners, boolean withOneTimePayment,
                                 ActivationStatus... activationStatuses) throws Exception {
        User user = checkUser(userName, userToken, timestamp, deviceUID, false, activationStatuses);

        NewsDto newsDto = messageService.processGetNewsCommand(user, getCurrentCommunityUri(), lastUpdateNewsTimeMillis, withBanners);

        AccountCheckDTO accountCheck = accCheckService.processAccCheck(user, false, false, withOneTimePayment);

        return buildModelAndView(accountCheck, newsDto);
    }
}
