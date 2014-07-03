package mobi.nowtechnologies.applicationtests.services.device;

import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDeviceDataService {
    public List<UserDeviceData> table(List<HasVersion> version, List<String> communities, List<String> deviceTypes) {
        Assert.notEmpty(version);
        Assert.notEmpty(communities);
        Assert.notEmpty(deviceTypes);

        List<UserDeviceData> userDeviceData = new ArrayList<UserDeviceData>(version.size() * communities.size());

        for (HasVersion hasVersion : version) {
            for (String community : communities) {
                for (String deviceType : deviceTypes) {
                    userDeviceData.add(new UserDeviceData(hasVersion, community, deviceType));
                }
            }
        }

        return userDeviceData;
    }
}
