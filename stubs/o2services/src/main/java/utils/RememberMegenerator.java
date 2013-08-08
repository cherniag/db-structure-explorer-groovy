package utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.DigestUtils;

public class RememberMegenerator {
	static long TWO_WEEKS = 14L * 24L * 60L * 60L * 1000L;
	static String help = "Help:\n" + "Possible arguments:\n" + "\t\t[tb_users.userName] [tb_users.token] \n"
			+ "\t\tor\n" + "\t\t[tb_users.userName] [tb_users.token] [expiredMillies]\n\n"
			+ "\t\t by default expiredMillies = NOW() + TWO_WEEKS in milliseconds\n\n";
	private static String iphoneAgent = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";

	public static void main(String[] args) {

		String username = "+447408831838";//CharMatcher.isNot('\'').retainFrom(args[0]);
		String userToken = "c7213cd89f51597428f6bce9cf9d6cac";//CharMatcher.isNot('\'').retainFrom(args[1]);

		try {

			String expiredMillis = getExpiredMillis(args);
			String signature = makeTokenSignature(username, Long.parseLong(expiredMillis), userToken);

			String rememberMeToken = encodeCookie(new String[] { username, expiredMillis, signature });
			System.out.println("_REMEMBER_ME=" + rememberMeToken);

			WebDriver d = createFirefoxDriver();//createChromeDriver();
			//String url="http://rage.musicqubed.com/web/payments_inapp.html";
			String url = "http://localhost:8080/web/payments_inapp.html";

			d.get(url);

			d.manage().addCookie(new Cookie("_REMEMBER_ME", rememberMeToken));

			d.manage().addCookie(new Cookie("_chartsnow_community", "o2"));
			d.get(url);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("args: " + arrayToString(args));
			System.out.println(help);
		}
	}

	//    private static WebDriver createChromeDriver() {
	//        System.setProperty("webdriver.chrome.driver", "c:\\tools\\chromedriver.exe");
	//        DesiredCapabilities capabilities = new DesiredCapabilities();
	//        //capabilities.["chrome.switches"] = ["--user-agent="+USER_AGENT_STRING] 
	//        Map<String, String> chromePrefs = new HashMap<String, String>();
	//        chromePrefs.put("chrome.switches","--user-agent="+iphoneAgent);
	////        if (!config.isShowDownloadDialog()) {
	////            chromePrefs.put("download.default_directory", config.getDownloadDirectory());
	////        }
	//        
	////        chromePrefs.put("download.prompt_for_download", String.valueOf(config.isShowDownloadDialog()));
	//       capabilities.setCapability("chrome.prefs", chromePrefs);
	//
	//        //unfortunately, there's no other way to set default download directory, but to use deprecated constructor
	//        ChromeDriver driver = new ChromeDriver(capabilities);
	//        return driver;
	//    }

	private static WebDriver createFirefoxDriver() throws Exception {
		//    	File fireFoxFile=new File("C:\\Program Files (x86)\\Mozilla Firefox\\Firefox.exe");
		//    	if(!fireFoxFile.exists()){
		//    		throw new RuntimeException("Ffox missing "+fireFoxFile.getAbsolutePath());
		//    	}
		//    	System.setProperty("webdriver.firefox.driver",fireFoxFile.getAbsolutePath());
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("general.useragent.override", iphoneAgent);
		WebDriver driver = new FirefoxDriver(profile);
		return driver;

		/*
		DesiredCapabilities c = DesiredCapabilities.firefox();
		
		FirefoxProfile profile = createFirefoxProfile();
		File userAgentSwicher=new File("c:\\tools\\user_agent_switcher-0.7.3-fx+sm.xpi");
        if(!userAgentSwicher.exists()){
        	throw new RuntimeException("Switcher not found "+userAgentSwicher.getAbsolutePath());
        }
		profile.addExtension(userAgentSwicher);
        FirefoxDriver driver = new FirefoxDriver(c);//profile);
        c.setCapability(FirefoxDriver.PROFILE, profile);
    
        c.setCapability("binary", fireFoxFile.getAbsolutePath());
        //setDefaultTimeout(driver);
        //driver.setLogLevel(Level.FINE);
        return driver;*/
	}

	//    
	//    private static FirefoxProfile createFirefoxProfile() {
	//        File dir = createTempDir("ffoxTempSelenium");
	//        FirefoxProfile profile = new FirefoxProfile(dir);
	//        profile.setPreference("general.useragent.override",iphoneAgent);
	//        //profile.addAdditionalPreference("general.useragent.override", iphoneAgent);
	//        
	////        if (!config.isShowDownloadDialog()) {
	////            profile.setPreference("browser.download.folderList", 2);
	////            profile.setPreference("browser.download.manager.showWhenStarting", false);
	////            profile.setPreference("browser.download.dir", config.getDownloadDirectory());
	////            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv,text/xml");
	////            //TODO add list of media types to browser config that should be automatically saved
	////        }
	//        return profile;
	//    }

	public static File createTempDir(String prefix) {
		try {
			File tempFile = File.createTempFile(prefix, "temp");
			File dir = new File(tempFile.getParent(), tempFile.getName() + "dir");
			dir.mkdir();
			return dir;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private static String arrayToString(String[] args) {
		return Joiner.on(", ").join(args).toString();
	}

	private static String getExpiredMillis(String[] args) {
		String expiredMillis = Long.toString(System.currentTimeMillis() + TWO_WEEKS);
		if (args.length == 3)
			expiredMillis = args[2];
		return expiredMillis;
	}

	protected static String encodeCookie(String[] cookieTokens) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cookieTokens.length; i++) {
			sb.append(cookieTokens[i]);

			if (i < cookieTokens.length - 1) {
				sb.append(":");
			}
		}

		String value = sb.toString();

		sb = new StringBuilder(new String(Base64.encode(value.getBytes())));

		while (sb.charAt(sb.length() - 1) == '=') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String makeTokenSignature(String username, long tokenExpiryTime, String password) {
		return DigestUtils.md5DigestAsHex((username + ":" + tokenExpiryTime + ":" + password + ":" + "web").getBytes());
	}
}
