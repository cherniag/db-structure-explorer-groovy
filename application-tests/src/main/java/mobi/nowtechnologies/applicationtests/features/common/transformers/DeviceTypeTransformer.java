package mobi.nowtechnologies.applicationtests.features.common.transformers;

import cucumber.api.Transformer;
import mobi.nowtechnologies.server.persistence.dao.DeviceTypeDao;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import org.springframework.util.Assert;

import java.util.Map;

public class DeviceTypeTransformer extends Transformer<DeviceType> {
    @Override
    public DeviceType transform(String name) {
        String normilized = name.toUpperCase();

        Map<String, DeviceType> deviceTypeValues = DeviceTypeDao.getDeviceTypeMapNameAsKeyAndDeviceTypeValue();

        Assert.isTrue(deviceTypeValues.containsKey(normilized), "Unknown device type: " + normilized + ", values: " + deviceTypeValues);

        return deviceTypeValues.get(name);
    }
}
