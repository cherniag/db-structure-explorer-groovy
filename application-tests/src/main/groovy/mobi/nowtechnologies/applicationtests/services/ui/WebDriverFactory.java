package mobi.nowtechnologies.applicationtests.services.ui;

import cucumber.custom.CustomCucumberRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class WebDriverFactory implements DisposableBean {

    WebDriver driver = instanciateAndRunDriver();

    public WebDriver get() {
        return driver;
    }

    @Override
    public void destroy() throws Exception {
        driver.close();
        driver.quit();
    }

    private PhantomJSDriver instanciateAndRunDriver() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, System.getProperty(CustomCucumberRunner.PHANTOM_JS_LOCATION));
        desiredCapabilities.setCapability("phantomjs.page.settings.userAgent",
                                          "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19");
        return new PhantomJSDriver(desiredCapabilities);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroy();
    }
}
