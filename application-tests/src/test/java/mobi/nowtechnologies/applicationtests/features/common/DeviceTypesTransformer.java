package mobi.nowtechnologies.applicationtests.features.common;


import cucumber.api.Transformer;
import mobi.nowtechnologies.applicationtests.services.helper.transformer.DataLoader;

import java.util.List;

public class DeviceTypesTransformer extends Transformer<List<String>> {
    private final String location;

    public DeviceTypesTransformer() {
        location = "features/common/devices.txt";
    }

    @Override
    public List<String> transform(String value) {
        return DataLoader.loadDeviceTypes(location);
    }
}
