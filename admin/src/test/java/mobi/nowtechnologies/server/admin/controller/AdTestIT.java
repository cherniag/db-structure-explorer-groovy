package mobi.nowtechnologies.server.admin.controller;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.Cookie;

import mobi.nowtechnologies.server.mock.MockWebApplication;
import mobi.nowtechnologies.server.mock.MockWebApplicationContextLoader;
import mobi.nowtechnologies.server.security.NowTechTokenBasedRememberMeServices;
import mobi.nowtechnologies.server.shared.enums.AdActionType;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
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
public class AdTestIT {

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
	public void testGetAdsPage_Success() throws Exception{
		
		String requestURI = "/ads";
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		
		assertEquals("{\"allAdFilterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"J2ME\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"AD_ITEM_DTO_LIST\":[{\"id\":81,\"actionType\":\"URL\",\"action\":\"http://www.ukr.net\",\"message\":\"Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!\",\"activated\":true,\"filterDtos\":[{\"name\":\"LAST_TRIAL_DAY\"}],\"file\":null,\"imageFileName\":null,\"position\":1},{\"id\":82,\"actionType\":\"URL\",\"action\":\"http://google.com.ua\",\"message\":\"The Wanted would love to match the drama and musicality of the Take That shows on their upcoming tour. The boys start a 10 date US Tour on Jan 17th and then head back to the UK for their 1st show on February 15th at the Capital FM Arena in Nottingham.\",\"activated\":true,\"filterDtos\":[{\"name\":\"ANDROID\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"}],\"file\":null,\"imageFileName\":null,\"position\":2},{\"id\":83,\"actionType\":\"ISRC\",\"action\":\"1XLS70CD\",\"message\":\"The NOW! Top 40 Chart delivers all the hits, all the time, straight to your mobile! Subscribed already? Congrats! If not, please get your groove on to keep the hits coming! Click in app or via your email! No email? Check your junk folder! Go for it!\",\"activated\":true,\"filterDtos\":[{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"}],\"file\":null,\"imageFileName\":null,\"position\":3},{\"id\":84,\"actionType\":\"ISRC\",\"action\":\"2XLS70CD\",\"message\":\"Flipping back to music after producing new movie W.E., Madonna revealed that her 12th album will be called MDNA and will more than likely be released in March. The first single Gimme All Your Luvin will feature Nicki Minaj and MIA.\",\"activated\":true,\"filterDtos\":[{\"name\":\"PAYMENT_ERROR\"}],\"file\":null,\"imageFileName\":null,\"position\":4},{\"id\":85,\"actionType\":\"ISRC\",\"action\":\"3XLS70CD\",\"message\":\"Grammy producers are keen for Adele to open the legendary awards ceremony in February but will have to wait for an answer as the singer is still recovering from throat surgery. The Someone Like You singer has been nominated for six awards.\",\"activated\":true,\"filterDtos\":[{\"name\":\"IOS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"file\":null,\"imageFileName\":null,\"position\":5},{\"id\":86,\"actionType\":\"URL\",\"action\":\"http://www.i.ua/\",\"message\":\"MasterCard and the Brits have launched an amazing competition in which fans can duet with either JLS, Emeli Sande or Labrinth. The winner will get VIP tickets to the awards show itself AND appear in an advert with their favourite popstar!\",\"activated\":false,\"filterDtos\":[],\"file\":null,\"imageFileName\":null,\"position\":6},{\"id\":87,\"actionType\":\"ISRC\",\"action\":\"4XLS70CD\",\"message\":\"Cher Lloyd is engaged! According to reports, the Swagger Jagger hitmaker and her boyfriend, hairdresser Craig Monk, actually got engaged last month but have been trying to keep it a secret. Best of luck to the happy couple!\",\"activated\":true,\"filterDtos\":[{\"name\":\"LIMITED\"}],\"file\":null,\"imageFileName\":null,\"position\":7},{\"id\":88,\"actionType\":\"ISRC\",\"action\":\"5XLS70CD\",\"message\":\"Lady Gaga has apparently taken up darts! The Born This Way singer recently accompanied her Vampire Diaries actor boyfriend, Taylor Kinney, to a pub game and apparently she is hooked! Taylor partnered Gaga as her love interest in her video You and I.\",\"activated\":false,\"filterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"}],\"file\":null,\"imageFileName\":null,\"position\":8},{\"id\":89,\"actionType\":\"URL\",\"action\":\"https://musicqubed.com/\",\"message\":\"With Little Mix winning X Factor, an original Sugababes reunion and a rumoured new 10th anniversary Girls Aloud album, 2012 looks like the year of the girl band! Wonder what the Spice Girls are up to?\",\"activated\":true,\"filterDtos\":[{\"name\":\"J2ME\"}],\"file\":null,\"imageFileName\":null,\"position\":9},{\"id\":90,\"actionType\":\"ISRC\",\"action\":\"6XLS70CD\",\"message\":\"The BRIT Awards 2012 will be held on Tuesday 21 February at The O2 Arena and broadcast live on ITV1. James Corden will host again this year and nominees have been announced. www.brits.co.uk\",\"activated\":true,\"filterDtos\":[{\"name\":\"ANDROID\"}],\"file\":null,\"imageFileName\":null,\"position\":10}],\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}", mockHttpServletResponse.getContentAsString());
	}
	
	@Test
	public void testGetAddAdPage_Success() throws Exception{
		String requestURI = "/ads/";
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		
		assertEquals("{\"allAdFilterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"J2ME\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"AD_ITEM_DTO\":{\"id\":null,\"actionType\":null,\"action\":null,\"message\":null,\"activated\":false,\"filterDtos\":null,\"file\":null,\"imageFileName\":null,\"position\":null},\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}", mockHttpServletResponse.getContentAsString());
	}
	
	@Test
	public void testSaveAd_FileIsNull_Failure()throws Exception{
		String requestURI = "/ads/";
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(RequestMethod.POST.name(), requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");
		
		httpServletRequest.addParameter("actionType", AdActionType.URL.name());
		httpServletRequest.addParameter("action", "https://i.ua");
		httpServletRequest.addParameter("message", "some message");
		httpServletRequest.addParameter("activated", "true");
		httpServletRequest.addParameter("filterDtos", "ONE_MONTH_PROMO");
		httpServletRequest.addParameter("filterDtos", "LIMITED");
		httpServletRequest.addParameter("imageFileName", "imageFileName");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), mockHttpServletResponse.getStatus());
		
		assertEquals("{\"allAdFilterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"J2ME\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"AD_ITEM_DTO\":{\"id\":null,\"actionType\":\"URL\",\"action\":\"https://i.ua\",\"message\":\"some message\",\"activated\":true,\"filterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"LIMITED\"}],\"file\":null,\"imageFileName\":\"imageFileName\",\"position\":null},\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}", mockHttpServletResponse.getContentAsString());
	}
	
	@Test
	public void testSaveAd_Success()throws Exception{
		String requestURI = "/ads/";
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(RequestMethod.POST.name(), requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");
		
		httpServletRequest.addParameter("actionType", AdActionType.URL.name());
		httpServletRequest.addParameter("action", "https://i.ua");
		httpServletRequest.addParameter("message", "some message");
		httpServletRequest.addParameter("activated", "true");
		httpServletRequest.addParameter("filterDtos", "ONE_MONTH_PROMO");
		httpServletRequest.addParameter("filterDtos", "LIMITED");
		httpServletRequest.addParameter("file", "1");
		httpServletRequest.addParameter("imageFileName", "");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		
		assertEquals("{\"allAdFilterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"J2ME\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"AD_ITEM_DTO\":{\"id\":null,\"actionType\":\"URL\",\"action\":\"https://i.ua\",\"message\":\"some message\",\"activated\":true,\"filterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"LIMITED\"}],\"file\":null,\"imageFileName\":\"\",\"position\":null},\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}", mockHttpServletResponse.getContentAsString());
	}
	
	@Test
	public void testUpdateAd_Success()throws Exception{
		String requestURI = "/ads/";
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(RequestMethod.POST.name(), requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");
		
		httpServletRequest.addParameter("id", "81");
		httpServletRequest.addParameter("actionType", AdActionType.URL.name());
		httpServletRequest.addParameter("action", "");
		httpServletRequest.addParameter("message", "");
		httpServletRequest.addParameter("activated", "false");
		httpServletRequest.addParameter("filterDtos", "ONE_MONTH_PROMO");
		httpServletRequest.addParameter("filterDtos", "LIMITED");
		httpServletRequest.addParameter("file", "1");
		httpServletRequest.addParameter("imageFileName", "someImageFileName");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		
		assertEquals("{\"allAdFilterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"J2ME\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"AD_ITEM_DTO\":{\"id\":81,\"actionType\":\"URL\",\"action\":\"\",\"message\":\"\",\"activated\":false,\"filterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"LIMITED\"}],\"file\":null,\"imageFileName\":\"someImageFileName\",\"position\":null},\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}", mockHttpServletResponse.getContentAsString());
	}
	
	@Test
	public void testGetUpdateAdPage_Success()throws Exception{
		String requestURI = "/ads/81";
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
		
		assertEquals("{\"allAdFilterDtos\":[{\"name\":\"ONE_MONTH_PROMO\"},{\"name\":\"J2ME\"},{\"name\":\"PAYMENT_ERROR\"},{\"name\":\"ANDROID\"},{\"name\":\"IOS\"},{\"name\":\"LAST_TRIAL_DAY\"},{\"name\":\"LIMITED\"},{\"name\":\"BLACKBERRY\"},{\"name\":\"FREE_TRIAL\"},{\"name\":\"NOT_ACTIVE_PAYMENT_DETAILS_OR_NO_PAYMENT_DETAILS\"},{\"name\":\"LIMITED_AFTER_TRIAL\"}],\"AD_ITEM_DTO\":{\"id\":81,\"actionType\":\"URL\",\"action\":\"http://www.ukr.net\",\"message\":\"Blue Ivy Carter, new daughter of Beyonce and JayZ, is already making chart history! Daddy Z features cute cries from little Princess B on his new track, Glory, making her the youngest person ever to appear in the Billboard chart!\",\"activated\":true,\"filterDtos\":[{\"name\":\"LAST_TRIAL_DAY\"}],\"file\":null,\"imageFileName\":null,\"position\":1},\"filesURL\":\"http://c1129449.r49.cf3.rackcdn.com/\"}", mockHttpServletResponse.getContentAsString());
	}
	
	@Test
	public void testDelete_Success()throws Exception{
		String requestURI = "/ads/81";
		String communityUrl = "nowtop40";

		MockHttpServletRequest httpServletRequest = new MockHttpServletRequest(RequestMethod.DELETE.name(), requestURI);
		httpServletRequest.setPathInfo(requestURI);

		Cookie cookie = getCommunityCoockie(httpServletRequest, communityUrl);

		httpServletRequest.setCookies(cookie);

		String rememberMeToken = nowTechTokenBasedRememberMeServices.getRememberMeToken("admin", "admin");

		httpServletRequest.addHeader(nowTechTokenBasedRememberMeServices.getKey(), rememberMeToken);
		httpServletRequest.addHeader("Accept", "application/json");

		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

		dispatcherServlet.service(httpServletRequest, mockHttpServletResponse);

		assertEquals(HttpStatus.OK.value(), mockHttpServletResponse.getStatus());
	}
	
}
