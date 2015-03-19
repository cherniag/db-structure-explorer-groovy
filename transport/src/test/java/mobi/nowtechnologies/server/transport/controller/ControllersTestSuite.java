package mobi.nowtechnologies.server.transport.controller;

import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(Suite.class)
@Suite.SuiteClasses(
    {AccCheckControllerTestIT.class, ActivateVideoAudioFreeTrialControllerTestIT.class, ApplyInitPromoControllerTestIT.class, AutoOptInControllerTestIT.class, CommonControllerTestIT.class,
        GetChartControllerTestIT.class, GetFileControllerTestIT.class, PhoneNumberControllerTestIT.class, GetNewsControllerTestIT.class, SignUpDeviceControllerTestIT.class})
public class ControllersTestSuite {}