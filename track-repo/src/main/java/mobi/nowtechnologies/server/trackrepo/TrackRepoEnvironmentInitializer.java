package mobi.nowtechnologies.server.trackrepo;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * Created by Oleg Artomov on 7/2/2014.
 */
public class TrackRepoEnvironmentInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    @Override
    public void initialize(ConfigurableWebApplicationContext applicationContext) {
        String filesExtension = SystemUtils.IS_OS_WINDOWS ? "bat" : "sh";
        System.setProperty("scriptsExtension", filesExtension);
    }
}
