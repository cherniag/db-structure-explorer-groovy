package mobi.nowtechnologies.applicationtests.services.runner;

import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RunnerService {

    @Value("${execution.threads}")
    private int threadsCount;

    private ExecutorService service;

    @PostConstruct
    void construct() {
        service = Executors.newFixedThreadPool(threadsCount);
    }

    public Runner create(Collection<UserDeviceData> datas) {
        List<UserDeviceData> userDeviceDatas = new ArrayList<UserDeviceData>(datas);
        Collections.sort(userDeviceDatas);
        return new Runner(service, threadsCount, userDeviceDatas);
    }
}
