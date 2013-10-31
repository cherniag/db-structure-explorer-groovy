package mobi.nowtechnologies.server.admin.controller;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import org.h2.jdbc.JdbcConnection;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.DispatcherServlet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;

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
@PrepareForTest(JdbcConnection.class)
@Ignore
public class MediaSearchIT {
	
	TestContextManager testContextManager;
	
	@Rule
	public PowerMockRule powerMockRule  = new PowerMockRule();

	@Autowired
	private DispatcherServlet dispatcherServlet;

	@Autowired
	private NowTechTokenBasedRememberMeServices nowTechTokenBasedRememberMeServices;
	
	@PersistenceContext
	private EntityManager em;

	private Cookie getCommunityCoockie(MockHttpServletRequest httpServletRequest, String communityUrl) {
		Cookie cookie = new Cookie("_chartsnow_community", communityUrl);
		cookie.setPath(httpServletRequest.getContextPath());
		cookie.setMaxAge(365 * 24 * 60 * 60);
		return cookie;
	}

	@Test()
	@Timed(millis=15000)
	public void testGetMediaList_Success() throws Exception {

		String requestURI = "/chartsNEW/5/1970-01-01_02:00:00/media/list";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest("GET", requestURI);
		httpServletRequest.setPathInfo(requestURI);
		httpServletRequest.addParameter("q", "21XLS70CD");

		String communityUrl = "nowtop40";

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());

		String expected = "{\"CHART_ITEM_DTO_LIST\":[{\"id\":null,\"chartId\":5,\"mediaDto\":{\"id\":21,\"artistDto\":{\"id\":1,\"info\":\"Games third single and second UK hit was co-written with his fellow G-Unit member and sometime rival 50 Cent.\",\"name\":\"CLASSIC CHOICE\",\"realName\":\"CLASSIC CHOICE\"},\"audioFileDto\":null,\"headerFileDto\":null,\"imageFIleLargeDto\":null,\"imageFileSmallDto\":{\"id\":205,\"filename\":\"21XLS70CDS.jpg\",\"fileType\":null,\"size\":2357},\"isrc\":\" 21XLS70CD\",\"label\":null,\"priceCurrency\":\"WEEK\",\"imgFileResolutionDto\":null,\"purchasedFileDto\":null,\"audioPreviewFileDto\":null,\"headerPreviewFileDto\":null,\"info\":null,\"title\":\"Party Rock Anthem\",\"price\":1.00,\"type\":\"MEDIA\",\"publishDate\":1417366688000,\"itunesUrl\":\"http://clkuk.tradedoubler.com/click?p=23708%26a=1997010%26url=http://itunes.apple.com/gb/album/firestarter/id20913017?i=20913006%26uo=4%26partnerId=2003\"},\"info\":\"\",\"position\":0,\"prevPosition\":0,\"chgPosition\":\"UNCHANGED\",\"channel\":\"\",\"publishTime\":0}]}";

		assertEquals(expected, mockHttpServletResponse.getContentAsString());

	}
}
