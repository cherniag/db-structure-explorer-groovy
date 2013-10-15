package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.SMPPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/14/13
 * Time: 7:20 PM
 */
public class SMPPServiceImpl extends SMPPService {
    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
