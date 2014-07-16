package mobi.nowtechnologies.applicationtests.services.helper.transformer;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.common.dto.UserRegInfo;
import org.modelmapper.internal.util.Assert;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    public static List<String> loadCommunites(String location) {
        return loadStrings(location);
    }

    public static List<HasVersion> loadVersions(String location) {
        List<HasVersion> versions = new ArrayList<HasVersion>();
        for (String d : loadStrings(location)) {
            versions.add(ApiVersion.from(d));
        }
        return versions;
    }

    public static List<String> loadDeviceTypes(String location) {
        List<String> deviceTypeConstants = Lists.newArrayList(UserRegInfo.DeviceType.getValues());
        List<String> deviceTypesInConfig = loadStrings(location);
        // provide assertions
        for (String deviceTypeInConfig : deviceTypesInConfig) {
            Assert.isTrue(deviceTypeConstants.contains(deviceTypeInConfig), "DeviceType found in config: " + deviceTypeInConfig + " does not match: " + deviceTypeConstants);
        }
        return deviceTypesInConfig;
    }

    //
    // Internals
    //
    private static List<String> loadStrings(String location) {
        return parseContent(readContent(location));
    }

    private static List<String> parseContent(String content) {
        Iterable<String> splitted = Splitter.on(",").omitEmptyStrings().trimResults().split(content);
        return Lists.newArrayList(splitted);
    }

    private static String readContent(String location) {
        try {
            Resource resource = new ClassPathResource(location);
            return Files.toString(resource.getFile(), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
