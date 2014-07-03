package mobi.nowtechnologies.applicationtests.services.device.domain;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.modelmapper.internal.util.Assert;

import java.util.List;

public class ApiVersion implements HasVersion {
    private Integer version;
    private Integer major;
    private Integer minor;

    private ApiVersion() {
    }

    public static ApiVersion from(String versionString) {
        List<String> versions = Lists.newArrayList(Splitter.on(".").omitEmptyStrings().split(versionString));
        Assert.isTrue(versions.size() < 4 && versions.size() > 0, "Versions does not match (v.mj.mn) format: " + versionString);

        ApiVersion restored = version(digit(versions.get(0)));
        if(versions.size() > 1) {
            return restored.major(digit(versions.get(1)));
        }
        if(versions.size() > 2) {
            return restored.minor(digit(versions.get(2)));
        }

        return restored;
    }

    public static ApiVersion version(int version) {
        Assert.isTrue(version >= 0);
        ApiVersion api = new ApiVersion();
        api.version = version;
        return api;
    }

    public ApiVersion major(int major) {
        this.major = major;
        return this;
    }

    public ApiVersion minor(int minor) {
        this.minor = minor;
        return this;
    }

    @Override
    public String getApiVersion() {
        StringBuilder v = new StringBuilder();
        v.append(version);
        if(major != null) {
            v.append('.');
            v.append(major);
        }
        if(minor != null) {
            v.append('.');
            v.append(minor);
        }
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

        if (major != null ? !major.equals(that.major) : that.major != null) return false;
        if (minor != null ? !minor.equals(that.minor) : that.minor != null) return false;
        if (!version.equals(that.version)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + (major != null ? major.hashCode() : 0);
        result = 31 * result + (minor != null ? minor.hashCode() : 0);
        return result;
    }

    private static int digit(String v) {
        return Integer.parseInt(v);
    }
}
