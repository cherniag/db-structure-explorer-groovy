package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.common.dto.UserRegInfo;
import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.enums.Tariff;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.DispatcherServlet;

import javax.annotation.Resource;

import static mobi.nowtechnologies.server.shared.enums.Tariff.*;
import static org.junit.Assert.assertEquals;

/**
 * User: Titov Mykhaylo (titov)
 * 24.07.13 9:08
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:transport-servlet-test.xml",
        "classpath:META-INF/service-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/dao-test.xml",
        "classpath:META-INF/soap.xml",
        "classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "transport.EntityController", webapp = "classpath:.")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
@PrepareForTest(Utils.class)
public class ActivateVideoControllerIT {

    @Autowired
    DispatcherServlet dispatcherServlet;

    @Resource(name = "service.UserService")
    UserService userService;

    @Test
    public void shouldActivateVideo() throws Exception{
        String userName = "zzz@z.com";
        String timestamp = "2011_12_26_07_04_23";
        String apiVersion = "4.0";
        String communityName = "o2";
        String appVersion = "CNBETA";
        String communityUrl="o2";

        String deviceType = UserRegInfo.DeviceType.ANDROID;

        String ipAddress = "2.24.0.1";

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", "/" + apiVersion + "/SIGN_UP_DEVICE");
        httpServletRequest.addHeader("Content-Type", "text/xml");
        httpServletRequest.setRemoteAddr(ipAddress);
        httpServletRequest.setPathInfo("/" + apiVersion + "/SIGN_UP_DEVICE");

        httpServletRequest.addParameter("COMMUNITY_NAME", communityName);
        httpServletRequest.addParameter("DEVICE_UID", userName);
        httpServletRequest.addParameter("API_VERSION", apiVersion);
        httpServletRequest.addParameter("APP_VERSION", appVersion);
        httpServletRequest.addParameter("DEVICE_TYPE", deviceType);

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

        assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

        String contentAsString = mockHttpServletResponse.getContentAsString();
        String storedToken = contentAsString.substring(contentAsString.indexOf("<userToken>") + "<userToken>".length(), contentAsString.indexOf("</userToken>"));
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        User user = userService.getUser(userName, communityName);
        user.setTariff(_4G);
        userService.updateUser(user);

        String requestURI = "/" + communityUrl + "/" + apiVersion + "/ACTIVATE_VIDEO";
        httpServletRequest = new MockHttpServletRequest("POST", requestURI);
        httpServletRequest.addHeader("Content-Type", "text/xml");
        httpServletRequest.setRemoteAddr(ipAddress);
        httpServletRequest.setPathInfo(requestURI);

        httpServletRequest.addParameter("USER_NAME", userName);
        httpServletRequest.addParameter("DEVICE_UID", userName);
        httpServletRequest.addParameter("USER_TOKEN", userToken);
        httpServletRequest.addParameter("APP_VERSION", appVersion);
        httpServletRequest.addParameter("TIMESTAMP", timestamp);

        mockHttpServletResponse = new MockHttpServletResponse();
        dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

        assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

    }

}
