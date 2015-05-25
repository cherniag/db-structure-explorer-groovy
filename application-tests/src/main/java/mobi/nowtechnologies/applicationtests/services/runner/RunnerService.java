package mobi.nowtechnologies.applicationtests.services.runner;

import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RunnerService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${execution.threads}")
    private int threadsCount;

    private ExecutorService service;

    @PostConstruct
    void construct() {
        service = Executors.newFixedThreadPool(threadsCount);
        logger.info("THREADS COUNT for execution:" + threadsCount);
    }

    public Runner create(Collection<UserDeviceData> datas) {
        List<UserDeviceData> userDeviceDatas = new ArrayList<UserDeviceData>(datas);
        Collections.sort(userDeviceDatas);
        logger.info("User Device datas count: " + userDeviceDatas.size());
        return new Runner(service, threadsCount, userDeviceDatas);
    }
}
