package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
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

import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/security-test.xml",
		"classpath:META-INF/admin-test.xml",
		"classpath:META-INF/service-test.xml",
		"classpath:META-INF/dao-test.xml",
		"classpath:META-INF/shared.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "admin.ChartItemController")
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class ChartIT {

	@Autowired
	private DispatcherServlet dispatcherServlet;

	@Autowired
	private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;

	private Cookie getCommunityCoockie(MockHttpServletRequest httpServletRequest, String communityUrl) {
		Cookie cookie = new Cookie("_chartsnow_community", communityUrl);
		cookie.setPath(httpServletRequest.getContextPath());
		cookie.setMaxAge(365 * 24 * 60 * 60);
		return cookie;
	}

	@Test
	public void testUpdateChartItems_Success() throws Exception {

		String newPublishTime = "2012-11-09_07:50:00";
		String requestURI = "/chartsNEW/5/1970-01-01_02:00:00/" + newPublishTime;

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", requestURI);
		httpServletRequest.setPathInfo(requestURI);

		String communityUrl = "nowtop40";
		
		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

		assertEquals("/charts/5/" + newPublishTime, mockHttpServletResponse.getRedirectedUrl());
	}


	@Test
	public void testUpdateChartItems_OldAndNewPublishTmesAreEquals_Failure() throws Exception {

		String oldPublishTime = "1970-01-01_02:00:00";
		String newPublishTime = oldPublishTime;
		String requestURI = "/chartsNEW/5/" + oldPublishTime + "/" + newPublishTime;
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), mockHttpServletResponse.getStatus());
		
		String responseContent = mockHttpServletResponse.getContentAsString();
		
		assertNotNull(responseContent);
		assertEquals("{\"errorCode\":\"chartItems.changingPublishTimeOnAlreadyScheduledTime.error\"}", responseContent);
		
	}
	
	@Test
	public void testUpdateChartItems_OldPublishTmeNotScheduled_Failure() throws Exception {

		String oldPublishTime = "1970-01-01_00:00:00";
		String newPublishTime = "2012-11-09_07:50:00";
		String requestURI = "/chartsNEW/5/" + oldPublishTime + "/" + newPublishTime;
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("POST", requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), mockHttpServletResponse.getStatus());
		
		String responseContent = mockHttpServletResponse.getContentAsString();
		
		assertNotNull(responseContent);
		assertEquals("{\"errorCode\":\"chartItems.notExisted.changingPublishTime.error\"}", responseContent);
		
	}

}
