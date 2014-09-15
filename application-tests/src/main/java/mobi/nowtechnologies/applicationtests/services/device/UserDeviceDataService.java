package mobi.nowtechnologies.applicationtests.services.device;

import com.google.common.collect.Sets;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

@Component
public class UserDeviceDataService {
    public List<UserDeviceData> table(List<String> version,
                                      Set<String> communities,
                                      Set<String> deviceTypes) {
        return table(version, communities, deviceTypes, Sets.newHashSet(RequestFormat.values()));
    }

    public List<UserDeviceData> table(List<String> versions,
                                      Set<String> communities,
                                      Set<String> deviceTypes,
                                      Set<RequestFormat> formats) {
        Assert.notEmpty(versions);
        Assert.notEmpty(communities);
        Assert.notEmpty(deviceTypes);
        Assert.notEmpty(formats);

        List<UserDeviceData> userDeviceData = new ArrayList<UserDeviceData>(versions.size() * communities.size());

        for (String version : versions) {
            for (String community : communities) {
                for (String deviceType : deviceTypes) {
                    for (RequestFormat format : Arrays.asList(RequestFormat.JSON)) {
                        userDeviceData.add(new UserDeviceData(version, community, deviceType, format));
                    }
                }
            }
        }

        return userDeviceData;
    }

    public List<UserDeviceData> table(List<String> version, List<String> communities, List<String> deviceTypes, RequestFormat format) {
        return table(version, new HashSet<String>(communities), new HashSet<String>(deviceTypes), Sets.newHashSet(format));
    }

    public List<UserDeviceData> table(List<String> version, List<String> communities, List<String> deviceTypes) {
        return table(version, new HashSet<String>(communities), new HashSet<String>(deviceTypes), Sets.newHashSet(RequestFormat.JSON));
    }

    public List<UserDeviceData> table(List<String> version, String community, List<String> deviceTypes) {
        Assert.hasText(community);
        return table(version, Sets.newHashSet(community), Sets.newHashSet(deviceTypes), Sets.newHashSet(RequestFormat.JSON));
    }

    public List<UserDeviceData> table(List<String> version, String community, List<String> deviceTypes, RequestFormat format) {
        Assert.hasText(community);
        return table(version, Sets.newHashSet(community), Sets.newHashSet(deviceTypes), Sets.newHashSet(format));
    }

    public List<UserDeviceData> table(String version, List<String> communities, List<String> deviceTypes) {
        Assert.notNull(version);
        return table(Arrays.asList(version), Sets.newHashSet(communities), Sets.newHashSet(deviceTypes), Sets.newHashSet(RequestFormat.JSON));
    }

    public List<UserDeviceData> table(String version, List<String> communities, List<String> deviceTypes, RequestFormat format) {
        Assert.notNull(version);
        return table(Arrays.asList(version), Sets.newHashSet(communities), Sets.newHashSet(deviceTypes), Sets.newHashSet(format));
    }
}
