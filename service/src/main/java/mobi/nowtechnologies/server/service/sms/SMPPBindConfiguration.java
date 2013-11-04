package mobi.nowtechnologies.server.service.sms;

import com.sentaca.spring.smpp.BindConfiguration;

/**
 * User: Alexsandr_Kolpakov
 * Date: 10/31/13
 * Time: 11:33 AM
 */
public class SMPPBindConfiguration extends BindConfiguration {
    private final Integer DEFAULT_POOL_SIZE = 1;

    private Integer connectionPoolSize = DEFAULT_POOL_SIZE;

    public Integer getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(Integer connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }
}
