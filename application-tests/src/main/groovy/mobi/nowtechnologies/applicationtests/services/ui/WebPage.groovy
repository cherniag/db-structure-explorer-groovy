package mobi.nowtechnologies.applicationtests.services.ui;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

class WebPage {
    public WebDriver driver;
    public UserDeviceData deviceData;
    public PhoneState phoneState;

    WebPage(WebDriver driver, UserDeviceData deviceData, PhoneState phoneState) {
        this.driver = driver;
        this.deviceData = deviceData;
        this.phoneState = phoneState;
    }

    void go(String uri) {
        driver.get(uri);
    }

    String text(String selector) {
        By by = By.cssSelector(selector);

        return driver.findElement(by).getText();
    }

    void click(String selector) {
        By by = By.cssSelector(selector);

        driver.findElement(by).click();
    }

    void type(String selector, String value) {
        By by = By.cssSelector(selector);

        driver.findElement(by).sendKeys(value);
    }

    boolean waitForVisible(String selector) {
        By by = By.cssSelector(selector);

        try {
            new WebDriverWait(driver, 10).until(new ExpectedCondition<WebElement>() {
                @Override
                public WebElement apply(WebDriver d) {
                    return d.findElement(by);
                }
            });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
