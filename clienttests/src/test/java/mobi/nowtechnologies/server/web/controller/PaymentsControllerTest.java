package mobi.nowtechnologies.server.web.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import util.RememberMegenerator;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class PaymentsControllerTest {

	private WebClient webClient;
	
	private String o23GUser = "testUser-o2-3g";
	private String o23GToken = "token-o2-3g";
	
//	private String o24GUserNotOpted = "testUser-o2-4g-ontopted";
//	private String o24GTokenNotOpted = "token-o2-4g-ontopted";
//	
//	private String o24GUserOpted = "testUser-o2-4g-opted";
//	private String o24GTokenOpted = "token-o2-4g-opted";
//	
//	private String o2BusinessUser = "testUser-o2-business";
//	private String o2BusinessUserToken = "token-o2-business";
//	
//	private String nono2OnIOSUser = "testUser-nono2OnIOS";
//	private String nono2OnIOSToken = "token-nono2OnIOS";
//	
//	private String nono2OnAndroidUser = "testUser-nono2OnAndroid";
//	private String nono2OnAndroidToken = "token-nono2OnAndroid";
	
	private String pageUrl = "http://localhost:8080/web/payments_inapp.html";
	private String IOSUserAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";
	
	@Before
	public void setUp() {
		webClient = new WebClient();
		
		CookieManager cookieManager = webClient.getCookieManager();
		
		String rememberMeToken = RememberMegenerator.getRememberMeCookie(o23GUser, o23GToken);
		
		cookieManager.addCookie( new Cookie("localhost", "_REMEMBER_ME", rememberMeToken) );
		cookieManager.addCookie( new Cookie("localhost", "_chartsnow_community", "o2") );
	}
	
	
	@Test
	public void tst() throws Exception {
		WebRequest request = new WebRequest(new URL(pageUrl), HttpMethod.GET);
        request.setRequestParameters(new ArrayList<NameValuePair>());
        request.getRequestParameters().add(new NameValuePair("COMMUNITY_NAME", "o2"));
        request.getRequestParameters().add(new NameValuePair("TIMESTAMP", new Date().toString()));
        request.getRequestParameters().add(new NameValuePair("APP_VERSION", "ANDROID"));
        request.getRequestParameters().add(new NameValuePair("API_VERSION", "4.0"));
        
        request.setAdditionalHeader("User-Agent", IOSUserAgent);
        
        Page page = webClient.getPage(request);
        String pageContent = page.getWebResponse().getContentAsString(); 
        System.out.println( pageContent );
	}
}
