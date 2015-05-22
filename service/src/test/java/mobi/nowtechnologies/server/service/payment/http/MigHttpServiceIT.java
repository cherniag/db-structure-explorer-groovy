package mobi.nowtechnologies.server.service.payment.http;

import mobi.nowtechnologies.server.service.payment.request.MigRequest;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.support.http.PostService;

import org.junit.*;

@Ignore
public class MigHttpServiceIT {

    private MigHttpService httpService;

    @Before
    public void startup() {
        httpService = new MigHttpService();
        httpService.setPostService(new PostService());
        httpService.setRequest(new MigRequest());
        httpService.setFreeSMSURL("http://91.216.137.155:8105/mig/mig-chartsnow/test.asp");
        httpService.setPremiumSMSURL("http://91.216.137.155:8105/mig/mig-chartsnow/test.asp");
    }

    @Test
    public void makeFreeSMSRequest_Successful() {
        MigResponse response = httpService.makeFreeSMSRequest("MIG01XU.00447857211954", "This is a free sms via MIG");

        Assert.assertNotNull(response);
    }

    @Test
    public void makePremiumSMSRequest_Successful() {
        httpService.setTimeToLiveMin(10);
        MigResponse response = httpService.makePremiumSMSRequest("991122334", "80988", "MIG01XU.00447465465456", "This is a premium sms via MIG");
        Assert.assertNotNull(response);
    }
}