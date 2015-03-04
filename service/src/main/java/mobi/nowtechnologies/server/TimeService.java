package mobi.nowtechnologies.server;

import java.util.Date;

public class TimeService {

    public Date now() {
        return new Date();
    }

    public int nowSeconds() {
        return (int) (new Date().getTime() / 1000);
    }
}
