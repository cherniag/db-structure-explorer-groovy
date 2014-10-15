package mobi.nowtechnologies.applicationtests.services.ui
import mobi.nowtechnologies.applicationtests.services.WebApplicationUriService
import mobi.nowtechnologies.applicationtests.services.device.PhoneState
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData
import org.junit.Assert
import org.springframework.stereotype.Service

import javax.annotation.Resource

@Service
class WebPortalService {
    @Resource
    WebApplicationUriService webApplicationUriService;
    @Resource
    PageFactory pageFactory;

    void open(String url, PhoneState phoneState, UserDeviceData deviceData) {
        pageFactory.acquire(deviceData, phoneState).go(url);
    }

    public void submitMnoPhoneNumberForm(String phoneNumber, UserDeviceData deviceData, PhoneState phoneState) {
        String url = webApplicationUriService.web(deviceData, phoneState, "entering_phone_number")

        WebPage page = pageFactory.acquire(deviceData, phoneState);
        page.go(url);
        page.type("#phone", phoneNumber)
        page.click(".button-mno")
        Assert.assertTrue(page.waitForVisible("#enterPinForm"))

    }

    public void submitPinForm(String pin, String phoneNumber, UserDeviceData deviceData, PhoneState phoneState) {
        def parameters = Collections.singletonMap("phoneNumber", phoneNumber.replace("+", "%2B"))

        String url = webApplicationUriService.web(deviceData, phoneState, "entering_pin", parameters)

        WebPage page = pageFactory.acquire(deviceData, phoneState);
        page.go(url);
        page.type("#pin", pin)
        page.click(".button-mno");
        Assert.assertTrue(page.waitForVisible(".body-message-mno-congratulations"));
    }

    public void submitUnsubscribeRequest(UserDeviceData deviceData, PhoneState phoneState) {
        String url = webApplicationUriService.web(deviceData, phoneState, "payments")

        WebPage page = pageFactory.acquire(deviceData, phoneState);
        page.go(url);
        page.click("a[href\$=\"unsubscribe.html\"]")
        Assert.assertTrue(page.waitForVisible("form#unsubscribeDto input[type=submit]"))
        page.click("form#unsubscribeDto input[type=submit]")
        Assert.assertTrue(page.waitForVisible("input[type=button][title\$=\"account.html\"]"))
    }
}
