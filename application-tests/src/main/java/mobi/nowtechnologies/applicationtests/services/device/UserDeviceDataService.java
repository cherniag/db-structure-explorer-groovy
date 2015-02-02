package mobi.nowtechnologies.applicationtests.services.device;

import com.google.common.collect.Sets;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

@Component
public class UserDeviceDataService {
    public List<UserDeviceData> table(List<String> version, String community, List<String> deviceTypes) {
        Assert.hasText(community);
        return table(version, Sets.newHashSet(community), Sets.newHashSet(deviceTypes), Sets.newHashSet(RequestFormat.JSON));
    }

    public List<UserDeviceData> table(List<String> version, Set<String> communities, Set<String> deviceTypes) {
        return table(version, communities, deviceTypes, Sets.newHashSet(RequestFormat.values()));
    }

    public List<UserDeviceData> table(List<String> version, String community, List<String> deviceTypes, Set<RequestFormat> formats) {
        Assert.hasText(community);
        return table(version, Sets.newHashSet(community), Sets.newHashSet(deviceTypes), formats);
    }

    public List<UserDeviceData> table(List<String> versions, Set<String> communities, Set<String> deviceTypes, Set<RequestFormat> formats) {
        return table(versions, communities, deviceTypes, formats, "");
    }

    public List<UserDeviceData> table(List<String> versions,
                                      Set<String> communities,
                                      Set<String> deviceTypes,
                                      Set<RequestFormat> formats,
                                      String qualifier) {
        Assert.notEmpty(versions);
        Assert.notEmpty(communities);
        Assert.notEmpty(deviceTypes);
        Assert.notEmpty(formats);

        String q = (qualifier == null) ? "" : qualifier;

        List<UserDeviceData> userDeviceData = new ArrayList<>();

        for (String version : versions) {
            for (String community : communities) {
                for (String deviceType : deviceTypes) {
                    for (RequestFormat format : formats) {
                        userDeviceData.add(new UserDeviceData(version, community, deviceType, format, q));
                    }
                }
            }
        }

        return userDeviceData;
    }
}
