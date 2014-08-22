package mobi.nowtechnologies.applicationtests.services.device.domain;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.modelmapper.internal.util.Assert;

import java.util.List;

public class ApiVersion implements HasVersion, Comparable<ApiVersion> {
    private int major;
    private int minor;

    private ApiVersion() {
    }

    public static ApiVersion from(String versionString) {
        List<String> versions = Lists.newArrayList(Splitter.on(".").omitEmptyStrings().split(versionString));
        Assert.isTrue(versions.size() == 2, "Version does not match (major.minor) format: " + versionString);

        return version(digit(versions.get(0)), digit(versions.get(1)));
    }

    public static ApiVersion version(int major, int minor) {
        Assert.isTrue(major > 0);
        Assert.isTrue(minor >= 0);

        ApiVersion api = new ApiVersion();
        api.major = major;
        api.minor = minor;

        return api;
    }

    @Override
    public String getApiVersion() {
        StringBuilder v = new StringBuilder();
        v.append(major);
        v.append('.');
        v.append(minor);
        return v.toString();
    }

    @Override
    public String toString() {
        return getApiVersion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiVersion that = (ApiVersion) o;

        if (major != that.major) return false;
        if (minor != that.minor) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        return result;
    }

    private static int digit(String v) {
        return Integer.parseInt(v);
    }

    @Override
    public int compareTo(ApiVersion other) {
        return getApiVersion().compareTo(other.getApiVersion());
    }
}
