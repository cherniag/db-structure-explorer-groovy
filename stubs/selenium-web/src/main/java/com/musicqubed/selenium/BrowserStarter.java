package com.musicqubed.selenium;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class BrowserStarter {
	private static String iphoneAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";

	public static void main(String[] args) {

		//String url="http://rage.musicqubed.com/web/payments_inapp.html";
		String url = "http://localhost:8080/web/payments_inapp.html";
		String username = "+447408831838";
		String userToken = "c7213cd89f51597428f6bce9cf9d6cac";

		String rememberMeToken = RememberMeUtils.generateRememberMeToken(username, userToken);
		System.out.println("_REMEMBER_ME=" + rememberMeToken);

		startBrowser(url, rememberMeToken);
	}

	public static void startBrowser(String url, String rememberMeToken) {
		try {


			WebDriver webDriver = createFirefoxDriver();
			webDriver.get(url);
			webDriver.manage().addCookie(new Cookie("_REMEMBER_ME", rememberMeToken));
			webDriver.manage().addCookie(new Cookie("_chartsnow_community", "o2"));
			webDriver.get(url);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static WebDriver createFirefoxDriver() throws Exception {
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("general.useragent.override", iphoneAgent);
		WebDriver driver = new FirefoxDriver(profile);
		return driver;
	}

}
