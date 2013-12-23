package mobi.nowtechnologies.server.transport.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
{
        AccCheckControllerTestIT.class,
        ActivateVideoAudioFreeTrialControllerTestIT.class,
        ApplyInitPromoControllerTestIT.class,
        AutoOptInControllerTestIT.class,
        CommonControllerTestIT.class,
        GetChartControllerTestIT.class,
        GetFileControllerTestIT.class,
        PhoneNumberControllerTestIT.class,
        GetNewsControllerTestIT.class,
        SignUpDeviceControllerTestIT.class,
        VersionControllerTestIT.class
})
public class TestSuitIT {
}