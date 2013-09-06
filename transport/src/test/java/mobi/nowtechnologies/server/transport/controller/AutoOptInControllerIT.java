package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.shared.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * User: Titov Mykhaylo (titov)
 * 05.09.13 15:44
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
public class AutoOptInControllerIT {

    @Autowired
    private DispatcherServlet dispatcherServlet;

    @Test
    public void shouldAutoOptIn() throws ServletException, IOException {
        String userName = "+447111111114";
        String appVersion = "4.1";
        String apiVersion = "4.1";
        String communityUrl = "o2";
        String timestamp = "2011_12_26_07_04_23";
        String storedToken = "f701af8d07e5c95d3f5cf3bd9a62344d";
        String deviceUid = "";
        String userToken = Utils.createTimestampToken(storedToken, timestamp);

        String url = "/h/" + communityUrl + "/" + apiVersion + "/AUTO_OPT_IN";

        MockHttpServletRequest httpServletRequestMock = new MockHttpServletRequest("POST", url);
        httpServletRequestMock.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpServletRequestMock.addHeader("Accept", "application/json");
        httpServletRequestMock.setPathInfo(url);

        httpServletRequestMock.addParameter("APP_VERSION", appVersion);
        httpServletRequestMock.addParameter("USER_NAME", userName);
        httpServletRequestMock.addParameter("USER_TOKEN", userToken);
        httpServletRequestMock.addParameter("TIMESTAMP", timestamp);
        httpServletRequestMock.addParameter("DEVICE_UID", deviceUid);

        MockHttpServletResponse httpServletResponseMock = new MockHttpServletResponse();

        dispatcherServlet.service(httpServletRequestMock, httpServletResponseMock);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), httpServletResponseMock.getStatus());
        assertEquals("", httpServletResponseMock.getContentAsString());

    }
}
