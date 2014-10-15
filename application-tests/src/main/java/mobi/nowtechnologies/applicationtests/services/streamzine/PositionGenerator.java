package mobi.nowtechnologies.applicationtests.services.streamzine;

import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PositionGenerator {
    private ConcurrentHashMap<UserDeviceData, AtomicInteger> values = new ConcurrentHashMap<UserDeviceData, AtomicInteger>();

    public void init(Collection<UserDeviceData> datas) {
        values.clear();

        for (UserDeviceData data : datas) {
            values.put(data, new AtomicInteger(0));
        }
    }

    public int nextPosition(UserDeviceData data) {
        return values.get(data).addAndGet(1);
    }
}
