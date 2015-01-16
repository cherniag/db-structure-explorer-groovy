package mobi.nowtechnologies.applicationtests.services.runner;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import groovy.lang.Closure;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class Runner {
    private List<UserDeviceData> datas;
    private ExecutorService service;
    private int threads;

    Runner(ExecutorService executorService, int threadsCount, List<UserDeviceData> userDeviceDatas) {
        datas = userDeviceDatas;
        threads = threadsCount;
        service = executorService;
    }

    public void parallel(Closure<UserDeviceData> closure)  {
        UserDeviceData firstTest = datas.get(0);
        closure.call(firstTest);

        // run others when first is OK
        List<List<UserDeviceData>> partitions = Lists.partition(datas, threads);
        for(List<UserDeviceData> p : partitions) {
            try {
                service.invokeAll(createTasks(p, closure));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void parallel(Invoker<UserDeviceData> toInvoke)  {
        List<UserDeviceData> copy = new ArrayList<UserDeviceData>(datas);

        UserDeviceData firstTest = copy.get(0);
        toInvoke.invoke(firstTest);
        copy.remove(0);

        // run others when first is OK
        List<List<UserDeviceData>> partitions = Lists.partition(copy, threads);
        for(List<UserDeviceData> p : partitions) {
            try {
                service.invokeAll(createTasks(p, toInvoke));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    Collection<Callable<Void>> createTasks(List<UserDeviceData> userDeviceDatas, final Closure<UserDeviceData> closure) {
        return Lists.transform(userDeviceDatas, new Function<UserDeviceData, Callable<Void>>() {
            @Override
            public Callable<Void> apply(final UserDeviceData input) {
                return new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        closure.call(input);
                        return null;
                    }
                };
            }
        });
    }

    Collection<Callable<Void>> createTasks(List<UserDeviceData> userDeviceDatas, final Invoker<UserDeviceData> toInvoke) {
        return Lists.transform(userDeviceDatas, new Function<UserDeviceData, Callable<Void>>() {
            @Override
            public Callable<Void> apply(final UserDeviceData input) {
                return new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        toInvoke.invoke(input);
                        return null;
                    }
                };
            }
        });
    }
}
