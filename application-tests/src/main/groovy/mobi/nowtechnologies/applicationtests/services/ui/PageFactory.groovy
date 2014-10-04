package mobi.nowtechnologies.applicationtests.services.ui

import mobi.nowtechnologies.applicationtests.services.device.PhoneState
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import org.openqa.selenium.WebDriver
import org.springframework.stereotype.Component

import javax.annotation.Resource

@Component
class PageFactory {
    @Resource
    WebDriverFactory factory;

    private ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    public WebPage acquire(UserDeviceData deviceData, PhoneState phoneState) {

        def driver = driverHolder.get()
        if(driver != null) {
            driver.manage().deleteAllCookies();
        } else {
            driver = factory.get();
            driverHolder.set(driver);
        }

        return new WebPage(driver, deviceData, phoneState);
    }
}
