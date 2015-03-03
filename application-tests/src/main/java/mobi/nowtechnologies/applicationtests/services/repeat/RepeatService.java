package mobi.nowtechnologies.applicationtests.services.repeat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class RepeatService {

    @Value("${repeats.retry.delay.millis}")
    private int retryDelayInMillis;

    @Value("${repeats.retry.count}")
    private int retryCount;

    public <T> T repeat(Repeatable<T> repeatable) {
        T result = null;

        for (int i = 0; i < retryCount; i++) {
            result = repeatable.result();

            delay();

            if (!repeatable.again()) {
                return result;
            }
        }
        return result;
    }

    private void delay() {
        try {
            Thread.sleep(retryDelayInMillis);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    void check() {
        Assert.isTrue(retryDelayInMillis > 0 && retryCount > 0, "Config parameters must be positive, retryCount:" + retryCount + ", retryDelayInMillis: " + retryDelayInMillis);
    }
}
